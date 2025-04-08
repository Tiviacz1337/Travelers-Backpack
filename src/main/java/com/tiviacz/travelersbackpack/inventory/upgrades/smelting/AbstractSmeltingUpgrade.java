package com.tiviacz.travelersbackpack.inventory.upgrades.smelting;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.menu.slot.UpgradeSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.*;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AbstractSmeltingUpgrade<T> extends UpgradeBase<T> implements IEnable, ITickableUpgrade, IMoveSelector {
    protected static final int SLOT_INPUT = 0;
    protected static final int SLOT_FUEL = 1;
    protected static final int SLOT_RESULT = 2;
    private final Level level;
    protected ItemStackHandler items;
    private RecipeHolder<? extends AbstractCookingRecipe> cachedRecipe = null;
    private boolean recipeFetched = false;
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;
    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck;
    private final String upgradeName;

    public AbstractSmeltingUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> furnaceContents, RecipeType<? extends AbstractCookingRecipe> recipeType, String upgradeName) {
        super(manager, dataHolderSlot, new Point(66, 82));
        this.level = manager.getWrapper().getLevel();
        this.items = createHandler(furnaceContents);
        this.recipeType = recipeType;
        this.quickCheck = RecipeManager.createCheck(this.recipeType);
        this.upgradeName = upgradeName;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public WidgetBase<BackpackScreen> createWidget(BackpackScreen screen, int x, int y) {
        return new AbstractSmeltingWidget<>(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y), "screen.travelersbackpack." + upgradeName);
    }

    @Override
    public void onUpgradeRemoved(ItemStack removedStack) {
        removedStack.remove(ModDataComponents.COOKING_TOTAL_TIME);
        removedStack.remove(ModDataComponents.COOKING_FINISH_TIME);
        removedStack.remove(ModDataComponents.BURN_TOTAL_TIME);
        removedStack.remove(ModDataComponents.BURN_FINISH_TIME);
    }

    @Override
    public List<Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<Slot> slots = new ArrayList<>();
        slots.add(new UpgradeSlotItemHandler<AbstractSmeltingUpgrade<?>>(this, this.items, SLOT_INPUT, x + 7, y + 23));
        slots.add(new UpgradeSlotItemHandler<AbstractSmeltingUpgrade<?>>(this, this.items, SLOT_FUEL, x + 7, y + 23 + 36));
        slots.add(new UpgradeSlotItemHandler<AbstractSmeltingUpgrade<?>>(this, this.items, SLOT_RESULT, x + 7 + 18 + 18, y + 23 + 18));
        return slots;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if(!enabled) {
            stopCooking();
            stopBurning();
            /*if(isCooking()) {
                long remainingTime = getCookingFinishTime() - this.level.getGameTime();
                setCookingTotalTime((int)remainingTime);
                setCookingFinishTime(0);
            }
            if(isBurning()) {
                long remainingTime = getBurnFinishTime() - this.level.getGameTime();
                setBurnTotalTime((int)remainingTime);
                setBurnFinishTime(0);
            }*/
        } else {
            /*if(getCookingTotalTime() > 0) {
                setCookingFinishTime(this.level.getGameTime() + getCookingTotalTime());
            }
            if(getBurnTotalTime() > 0) {
                setBurnFinishTime(this.level.getGameTime() + getBurnTotalTime());
            }*/
            checkCooking(this.level, false);
        }
    }

    @Override
    public void tick(@Nullable Player player, Level level, BlockPos pos, int currentTick) {
        if(level.isClientSide || !isEnabled(this)) {
            return;
        }

        long currentTime = level.getGameTime();
        if(isBurning()) {
            if(isCooking()) {
                if(currentTime >= getCookingFinishTime()) {
                    finishCooking();
                }
            }

            if(currentTime >= getBurnFinishTime()) {
                finishBurning();
            }
        }

        if(player != null && player.containerMenu instanceof BackpackBaseMenu) {
            tickSmelting(level);
        }

        if(!hasCooldown() || getCooldown() != getTickRate()) {
            setCooldown(getTickRate());
        }
    }

    @Override
    public int getTickRate() {
        return 5;
    }

    public boolean isFuel(ItemStack pStack) {
        return pStack.getBurnTime(null) > 0;
    }

    public boolean isBurning() {
        return getBurnFinishTime() > (long)0;
    }

    public boolean isCooking() {
        return getCookingFinishTime() > (long)0;
    }

    public boolean hasFuel() {
        return (!getStack(SLOT_FUEL).isEmpty() && isFuel(getStack(SLOT_FUEL))) || isBurning();
    }

    public void tickSmelting(Level level) {
        if(!this.recipeFetched && this.cachedRecipe == null) {
            this.cachedRecipe = this.quickCheck.getRecipeFor(new SingleRecipeInput(getStack(SLOT_INPUT)), level).orElse(null);
            this.recipeFetched = true;
        }

        boolean shouldStop = false;

        if(isCooking()) {
            if(isBurning()) {
                if(this.cachedRecipe != null) {
                    RecipeHolder<? extends AbstractCookingRecipe> currentRecipe = this.quickCheck.getRecipeFor(new SingleRecipeInput(getStack(SLOT_INPUT)), level).orElse(null);
                    if(this.cachedRecipe != currentRecipe) {
                        this.cachedRecipe = currentRecipe;
                        shouldStop = true;
                    }
                } else {
                    shouldStop = true;
                }
            }
        }

        if(shouldStop) {
            stopCooking();
            checkCooking(level, false);
        }
    }

    public void checkCooking(Level level, boolean force) {
        if(level.isClientSide || !isEnabled(this)) {
            return;
        }

        if(this.cachedRecipe == null) {
            this.cachedRecipe = this.quickCheck.getRecipeFor(new SingleRecipeInput(getStack(SLOT_INPUT)), level).orElse(null);
        }

        if((force || !isCooking()) && canBurn(this.cachedRecipe)) {
            if(!isBurning()) {
                startBurning();
            }
            startCooking(this.cachedRecipe.value());
        }
    }

    public void startCooking(AbstractCookingRecipe recipe) {
        int cookingDuration = recipe.getCookingTime();
        setCookingFinishTime(this.level.getGameTime() + cookingDuration);
        setCookingTotalTime(cookingDuration);
    }

    public void startBurning() {
        int litDuration = getBurnDuration(getStack(SLOT_FUEL));
        setBurnFinishTime(this.level.getGameTime() + litDuration);
        setBurnTotalTime(litDuration);
        shrinkFuelSlot();
    }

    private boolean canBurn(@Nullable RecipeHolder<? extends AbstractCookingRecipe> recipe) {
        if(!getStack(SLOT_INPUT).isEmpty() && recipe != null && hasFuel()) {
            ItemStack cookingResult = recipe.value().assemble(new SingleRecipeInput(getStack(SLOT_INPUT)), this.level.registryAccess());
            if(cookingResult.isEmpty()) {
                return false;
            } else {
                ItemStack resultSlotStack = getStack(SLOT_RESULT);
                if(resultSlotStack.isEmpty()) {
                    return true;
                } else if(!ItemStack.isSameItem(resultSlotStack, cookingResult)) {
                    return false;
                } else if(resultSlotStack.getCount() + cookingResult.getCount() <= this.items.getSlotLimit(SLOT_RESULT) && resultSlotStack.getCount() + cookingResult.getCount() <= resultSlotStack.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
                    return true;
                } else {
                    return resultSlotStack.getCount() + cookingResult.getCount() <= cookingResult.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
                }
            }
        } else {
            return false;
        }
    }

    public void finishCooking() {
        if(this.cachedRecipe == null) {
            this.cachedRecipe = this.quickCheck.getRecipeFor(new SingleRecipeInput(getStack(SLOT_INPUT)), level).orElse(null);
        }
        if(this.cachedRecipe != null) {
            ItemStack result = this.cachedRecipe.value().assemble(new SingleRecipeInput(getStack(SLOT_INPUT)), level.registryAccess());

            //Reduce input slot count
            ItemStack input = getStack(SLOT_INPUT).copy();
            ItemStack resultSlot = getStack(SLOT_RESULT).copy();

            if(!resultSlot.isEmpty()) {
                resultSlot.grow(1);
            } else {
                resultSlot = result;
            }

            if(input.is(Blocks.WET_SPONGE.asItem()) && !getStack(SLOT_FUEL).isEmpty() && getStack(SLOT_FUEL).is(Items.BUCKET)) {
                setStack(SLOT_FUEL, new ItemStack(Items.WATER_BUCKET));
            }

            input.shrink(1);
            setStack(SLOT_INPUT, input);
            setStack(SLOT_RESULT, resultSlot);
        }

        if(canBurn(this.cachedRecipe)) {
            checkCooking(this.level, true);
        } else {
            stopCooking();
        }
    }

    public void shrinkFuelSlot() {
        ItemStack fuel = getStack(SLOT_FUEL).copy();
        if(fuel.hasCraftingRemainingItem()) {
            setStack(SLOT_FUEL, fuel.getCraftingRemainingItem());
        } else {
            fuel.shrink(1);
            setStack(SLOT_FUEL, fuel);
        }
    }

    public void finishBurning() {
        ItemStack fuel = getStack(SLOT_FUEL).copy();

        if(isFuel(fuel) && isCooking()) {
            startBurning();
        } else {
            stopCooking();
            stopBurning();
            this.cachedRecipe = null;
        }
    }

    public void setStack(int slot, ItemStack stack) {
        this.items.setStackInSlot(slot, stack);
    }

    public ItemStack getStack(int slot) {
        return this.items.getStackInSlot(slot);
    }

    protected int getBurnDuration(ItemStack pFuel) {
        if(pFuel.isEmpty()) {
            return 0;
        } else {
            return pFuel.getBurnTime(this.recipeType);
        }
    }

    public int getBurnTotalTime() {
        return getDataHolderStack().getOrDefault(ModDataComponents.BURN_TOTAL_TIME, 0);
    }

    public long getBurnFinishTime() {
        return getDataHolderStack().getOrDefault(ModDataComponents.BURN_FINISH_TIME, (long)0);
    }

    public int getCookingTotalTime() {
        return getDataHolderStack().getOrDefault(ModDataComponents.COOKING_TOTAL_TIME, 0);
    }

    public long getCookingFinishTime() {
        return getDataHolderStack().getOrDefault(ModDataComponents.COOKING_FINISH_TIME, (long)0);
    }

    public void setBurnTotalTime(int time) {
        setStackData(ModDataComponents.BURN_TOTAL_TIME.get(), time);
    }

    public void setBurnFinishTime(long time) {
        setStackData(ModDataComponents.BURN_FINISH_TIME.get(), time);
    }

    public void setCookingTotalTime(int time) {
        setStackData(ModDataComponents.COOKING_TOTAL_TIME.get(), time);
    }

    public void setCookingFinishTime(long time) {
        setStackData(ModDataComponents.COOKING_FINISH_TIME.get(), time);
    }

    public void stopCooking() {
        setCookingFinishTime(0);
        setCookingTotalTime(0);
    }

    public void stopBurning() {
        setBurnFinishTime(0);
        setBurnTotalTime(0);
    }

    public void setSlotChanged(ItemStack dataHolderStack, int index, ItemStack stack) {
        dataHolderStack.update(ModDataComponents.BACKPACK_CONTAINER, new BackpackContainerContents(3), new BackpackContainerContents.Slot(index, stack), BackpackContainerContents::updateSlot);
    }

    public <D> void setStackData(DataComponentType<D> data, D value) {
        ItemStack stack = getDataHolderStack().copy();
        if(value == null) {
            stack.remove(data);
        } else {
            stack.set(data, value);
        }
        getUpgradeManager().getUpgradesHandler().setStackInSlot(getDataHolderSlot(), stack);
    }

    private ItemStackHandler createHandler(NonNullList<ItemStack> stacks) {
        return new ItemStackHandler(stacks) {
            @Override
            protected void onContentsChanged(int slot) {
                updateDataHolderUnchecked(dataHolderStack -> setSlotChanged(dataHolderStack, slot, getStackInSlot(slot)));

                if(getUpgradeManager().getWrapper().getScreenID() == Reference.WEARABLE_SCREEN_ID) {
                    checkCooking(AbstractSmeltingUpgrade.this.level, false);
                }
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if(slot == SLOT_INPUT) {
                    return BackpackSlotItemHandler.isItemValid(stack);
                }
                if(slot == SLOT_FUEL) {
                    ItemStack fuel = getStack(SLOT_FUEL);
                    return stack.getBurnTime(AbstractSmeltingUpgrade.this.recipeType) > 0 || stack.is(Items.BUCKET) && !fuel.is(Items.BUCKET);
                }
                return false;
            }

            @Override
            protected int getStackLimit(int slot, @NotNull ItemStack stack) {
                if(slot == SLOT_FUEL && stack.is(Items.BUCKET)) {
                    return 1;
                }
                return super.getStackLimit(slot, stack);
            }
        };
    }
}