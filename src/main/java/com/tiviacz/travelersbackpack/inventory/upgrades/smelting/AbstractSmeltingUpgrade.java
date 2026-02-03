package com.tiviacz.travelersbackpack.inventory.upgrades.smelting;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.menu.slot.UpgradeSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.*;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class AbstractSmeltingUpgrade<T> extends UpgradeBase<T> implements IEnable, ITickableUpgrade, IMoveSelector {
    protected static final int SLOT_INPUT = 0;
    protected static final int SLOT_FUEL = 1;
    protected static final int SLOT_RESULT = 2;
    private final Supplier<Level> level;
    protected ItemStackHandler items;
    private AbstractCookingRecipe cachedRecipe = null;
    private boolean recipeFetched = false;
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;
    private final RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> quickCheck;
    private final String upgradeName;

    public AbstractSmeltingUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> furnaceContents, RecipeType<? extends AbstractCookingRecipe> recipeType, String upgradeName) {
        super(manager, dataHolderSlot, new Point(66, 82));
        this.level = () -> manager.getWrapper().getLevel();
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
        NbtHelper.remove(removedStack, ModDataHelper.COOKING_TOTAL_TIME);
        NbtHelper.remove(removedStack, ModDataHelper.COOKING_FINISH_TIME);
        NbtHelper.remove(removedStack, ModDataHelper.BURN_TOTAL_TIME);
        NbtHelper.remove(removedStack, ModDataHelper.BURN_FINISH_TIME);
    }

    @Override
    public List<Pair<Integer, Integer>> getUpgradeSlotsPosition(int x, int y) {
        List<Pair<Integer, Integer>> positions = new ArrayList<>();
        positions.add(Pair.of(x + 7, y + 23));
        positions.add(Pair.of(x + 7, y + 23 + 36));
        positions.add(Pair.of(x + 7 + 18 + 18, y + 23 + 18));
        return positions;
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
        } else {
            checkCooking(this.level.get(), false);
        }
    }

    public void syncClient(ItemStack backpack) {
        int slot = getDataHolderSlot();
        NonNullList<ItemStack> contents = NbtHelper.get(backpack, ModDataHelper.UPGRADES);
        if(contents == null) return;
        if(slot >= contents.size()) return;
        ItemStack stack = contents.get(slot);

        long finishTime = NbtHelper.getOrDefault(stack, ModDataHelper.BURN_FINISH_TIME, (long)0);
        int total = NbtHelper.getOrDefault(stack, ModDataHelper.BURN_TOTAL_TIME, 0);
        setBurnTotalTime(total);
        setBurnFinishTime(finishTime);
        long cookingTime = NbtHelper.getOrDefault(stack, ModDataHelper.COOKING_FINISH_TIME, (long)0);
        int cookingTotal = NbtHelper.getOrDefault(stack, ModDataHelper.COOKING_TOTAL_TIME, 0);
        setCookingFinishTime(cookingTime);
        setCookingTotalTime(cookingTotal);
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

        if((player != null && player.containerMenu instanceof BackpackBaseMenu) || getUpgradeManager().getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
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
        return ForgeHooks.getBurnTime(pStack, null) > 0;
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
            this.cachedRecipe = this.quickCheck.getRecipeFor(new RecipeWrapper(this.items), level).orElse(null);
            this.recipeFetched = true;
        }

        boolean shouldStop = false;

        if(isCooking()) {
            if(isBurning()) {
                if(this.cachedRecipe != null) {
                    AbstractCookingRecipe currentRecipe = this.quickCheck.getRecipeFor(new RecipeWrapper(this.items), level).orElse(null);
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
            this.cachedRecipe = this.quickCheck.getRecipeFor(new RecipeWrapper(this.items), level).orElse(null);
        }

        if((force || !isCooking()) && canBurn(this.cachedRecipe)) {
            if(!isBurning()) {
                startBurning();
            }
            startCooking(this.cachedRecipe);
        }
    }

    public void startCooking(AbstractCookingRecipe recipe) {
        int cookingDuration = recipe.getCookingTime();
        setCookingFinishTime(this.level.get().getGameTime() + cookingDuration);
        setCookingTotalTime(cookingDuration);
    }

    public void startBurning() {
        int litDuration = getBurnDuration(getStack(SLOT_FUEL));
        setBurnFinishTime(this.level.get().getGameTime() + litDuration);
        setBurnTotalTime(litDuration);
        shrinkFuelSlot();
    }

    private boolean canBurn(@Nullable AbstractCookingRecipe recipe) {
        if(!getStack(SLOT_INPUT).isEmpty() && recipe != null && hasFuel()) {
            ItemStack cookingResult = recipe.assemble(new RecipeWrapper(this.items), this.level.get().registryAccess());
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
            this.cachedRecipe = this.quickCheck.getRecipeFor(new RecipeWrapper(this.items), level.get()).orElse(null);
        }
        if(this.cachedRecipe != null) {
            ItemStack result = this.cachedRecipe.assemble(new RecipeWrapper(this.items), level.get().registryAccess());

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
            checkCooking(this.level.get(), true);
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
            return ForgeHooks.getBurnTime(pFuel, this.recipeType);
        }
    }

    public int getBurnTotalTime() {
        return NbtHelper.getOrDefault(getDataHolderStack(), ModDataHelper.BURN_TOTAL_TIME, 0);
    }

    public long getBurnFinishTime() {
        return NbtHelper.getOrDefault(getDataHolderStack(), ModDataHelper.BURN_FINISH_TIME, (long)0);
    }

    public int getCookingTotalTime() {
        return NbtHelper.getOrDefault(getDataHolderStack(), ModDataHelper.COOKING_TOTAL_TIME, 0);
    }

    public long getCookingFinishTime() {
        return NbtHelper.getOrDefault(getDataHolderStack(), ModDataHelper.COOKING_FINISH_TIME, (long)0);
    }

    public void setBurnTotalTime(int time) {
        setStackData(ModDataHelper.BURN_TOTAL_TIME, time);
    }

    public void setBurnFinishTime(long time) {
        setStackData(ModDataHelper.BURN_FINISH_TIME, time);
    }

    public void setCookingTotalTime(int time) {
        setStackData(ModDataHelper.COOKING_TOTAL_TIME, time);
    }

    public void setCookingFinishTime(long time) {
        setStackData(ModDataHelper.COOKING_FINISH_TIME, time);
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
        NbtHelper.update(dataHolderStack, ModDataHelper.BACKPACK_CONTAINER, 3, index, stack);
    }

    public void setStackData(String data, Object value) {
        ItemStack stack = getDataHolderStack().copy();
        if(value == null) {
            NbtHelper.remove(stack, data);
        } else {
            NbtHelper.set(stack, data, value);
        }
        getUpgradeManager().getUpgradesHandler().setStackInSlot(getDataHolderSlot(), stack);
    }

    private ItemStackHandler createHandler(NonNullList<ItemStack> stacks) {
        return new ItemStackHandler(stacks) {
            @Override
            protected void onContentsChanged(int slot) {
                updateDataHolderUnchecked(dataHolderStack -> setSlotChanged(dataHolderStack, slot, getStackInSlot(slot)));

                if(getUpgradeManager().getWrapper().getScreenID() != Reference.ITEM_SCREEN_ID) {
                    checkCooking(AbstractSmeltingUpgrade.this.level.get(), false);
                }
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if(slot == SLOT_INPUT) {
                    return BackpackSlotItemHandler.isItemValid(stack);
                }
                if(slot == SLOT_FUEL) {
                    ItemStack fuel = getStack(SLOT_FUEL);
                    return ForgeHooks.getBurnTime(stack, AbstractSmeltingUpgrade.this.recipeType) > 0 || stack.is(Items.BUCKET) && !fuel.is(Items.BUCKET);
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