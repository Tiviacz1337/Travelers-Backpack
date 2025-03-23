package com.tiviacz.travelersbackpack.inventory.upgrades.feeding;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.filter.IFilter;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.StorageAccessWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.FilterSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.IEnable;
import com.tiviacz.travelersbackpack.inventory.upgrades.ITickableUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FeedingUpgrade extends UpgradeBase<FeedingUpgrade> implements IFilter, IEnable, ITickableUpgrade {
    private static final int COOLDOWN = 100;
    private static final int STILL_HUNGRY_COOLDOWN = 10;

    public ItemStackHandler filter;
    private final FeedingFilterSettings filterSettings;

    public FeedingUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> filter) {
        super(manager, dataHolderSlot, new Point(66, 103));
        this.filter = createFilter(filter);
        int activeSlotCount = TravelersBackpackConfig.getConfig().backpackUpgrades.feedingUpgradeSettings.filterSlotCount;
        this.filterSettings = new FeedingFilterSettings(manager.getWrapper().getStorage(), filter.stream().limit(activeSlotCount).filter(stack -> !stack.isEmpty()).toList(), getFilter());
    }

    @Override
    public List<Integer> getFilter() {
        List<Integer> filter = NbtHelper.getOrDefault(getUpgradeManager().getUpgradesHandler().getStackInSlot(this.dataHolderSlot), ModDataHelper.FILTER_SETTINGS, List.of(1, 1, 0));
        //Conversion error fix - #TODO to remove
        if(filter.size() != 3) {
            NbtHelper.remove(getUpgradeManager().getUpgradesHandler().getStackInSlot(this.dataHolderSlot), ModDataHelper.FILTER_SETTINGS);
            filter = List.of(1, 1, 0);
        }
        return filter;
        //return NbtHelper.getOrDefault(getUpgradeManager().getUpgradesHandler().getStackInSlot(this.dataHolderSlot), ModDataHelper.FILTER_SETTINGS, List.of(1, 1, 0));
        //return getUpgradeManager().getUpgradesHandler().getStackInSlot(this.dataHolderSlot).getOrDefault(ModDataComponents.FILTER_SETTINGS.get(), List.of(1, 1, 0));
    }

    public FeedingFilterSettings getFilterSettings() {
        return this.filterSettings;
    }

    public boolean canEat(Player player, ItemStack stack) {
        return getFilterSettings().canEat(player.getFoodData(), stack) && isEnabled() && !player.getCooldowns().isOnCooldown(stack.getItem()); //Cooldown patch for everlasting foods from Artifacts
    }

    @Override
    public boolean isEnabled() {
        return NbtHelper.getOrDefault(getUpgradeManager().getUpgradesHandler().getStackInSlot(this.dataHolderSlot), ModDataHelper.UPGRADE_ENABLED, true);
        // return getUpgradeManager().getUpgradesHandler().getStackInSlot(this.dataHolderSlot).getOrDefault(ModDataComponents.UPGRADE_ENABLED.get(), true);
    }

    @Override
    public void updateSettings() {
        this.filterSettings.updateSettings(getFilter());
    }

    @Override
    public int getFilterSlotCount() {
        return TravelersBackpackConfig.getConfig().backpackUpgrades.feedingUpgradeSettings.filterSlotCount;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public WidgetBase createWidget(BackpackScreen screen, int x, int y) {
        return new FeedingWidget(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y));
    }

    @Override
    public List<Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<Slot> slots = new ArrayList<>();
        int activeSlotCount = TravelersBackpackConfig.getConfig().backpackUpgrades.feedingUpgradeSettings.filterSlotCount;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                slots.add(new FilterSlotItemHandler(this, this.filter, j + i * 3, x + 7 + j * 18, y + 44 + i * 18, activeSlotCount) {
                    @Override
                    public boolean mayPlace(ItemStack pStack) {
                        return menu.getWrapper().isOwner(menu.player) && super.mayPlace(pStack);
                    }
                });
            }
        }
        return slots;
    }

    private ItemStackHandler createFilter(NonNullList<ItemStack> stacks) {
        return new ItemStackHandler(stacks) {
            @Override
            protected void onContentsChanged(int slot) {
                ItemStack stack = getUpgradeManager().getUpgradesHandler().getStackInSlot(getDataHolderSlot());

                //Crash prevent for TS (???)
                if(stack.isEmpty()) return;

                NbtHelper.set(stack, ModDataHelper.BACKPACK_CONTAINER, filter);
                //  stack.set(ModDataComponents.BACKPACK_CONTAINER.get(), InventoryHelper.itemsToList(9, filter));
                getUpgradeManager().getUpgradesHandler().setStackInSlot(getDataHolderSlot(), stack);

                getFilterSettings().updateFilter(NbtHelper.get(stack, ModDataHelper.BACKPACK_CONTAINER));
                // getFilterSettings().updateFilter(stack.get(ModDataComponents.BACKPACK_CONTAINER.get()).getItems());
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack.isEdible();
                //return true; //stack.has(DataComponents.FOOD);
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }

    @Override
    public void tick(@Nullable Player player, Level level, BlockPos pos, int currentTick) {
        if(currentTick % getCooldown() != 0) {
            return;
        }

        if(feedPlayerAndGetHungry(player, level)) {
            setCooldown(STILL_HUNGRY_COOLDOWN);
            return;
        }

        setCooldown(COOLDOWN);
    }

    private boolean feedPlayerAndGetHungry(Player player, Level level) {
        int hungerLevel = 20 - player.getFoodData().getFoodLevel();
        if(hungerLevel == 0 || level.isClientSide) {
            return false;
        }
        return tryFeedingFoodFromStorage(level, hungerLevel, player) && player.getFoodData().getFoodLevel() < 20;
    }

    private boolean tryFeedingFoodFromStorage(Level level, int hungerLevel, Player player) {
        ItemStackHandler storage = getUpgradeManager().getWrapper().getStorage();
        return InventoryHelper.iterateHandler(storage, (slot, stack) -> tryFeedingStack(level, hungerLevel, player, slot, stack, storage));
    }

    private boolean tryFeedingStack(Level level, int hungerLevel, Player player, Integer slot, ItemStack stack, ItemStackHandler backpackStorage) {
        if(isEdible(stack, player) && canEat(player, stack)) {
            ItemStack mainHandItem = player.getMainHandItem();
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);//player.getInventory().items.set(player.getInventory().selected, stack);

            ItemStack singleItemCopy = stack.copy();
            singleItemCopy.setCount(1);

            if(singleItemCopy.use(level, player, InteractionHand.MAIN_HAND).getResult() == InteractionResult.CONSUME) {
                //player.getInventory().items.set(player.getInventory().selected, mainHandItem);

                stack.shrink(1);
                backpackStorage.setStackInSlot(slot, stack);

                InteractionResultHolder<ItemStack> result = UseItemCallback.EVENT.invoker().interact(player, level, InteractionHand.MAIN_HAND);
                ItemStack resultItem = result.getObject();
                if (result.getResult() == InteractionResult.PASS) {
                    resultItem = singleItemCopy.getItem().finishUsingItem(singleItemCopy, level, player);
                }

                if(!resultItem.isEmpty()) {
                    ItemStack insertResult = InventoryHelper.addItemStackToHandler(new StorageAccessWrapper(getUpgradeManager().getWrapper(), backpackStorage), resultItem, false);
                    if(!insertResult.isEmpty()) {
                        player.drop(insertResult, true);
                    }
                }
                player.setItemInHand(InteractionHand.MAIN_HAND, mainHandItem);
                return true;
            }
            player.setItemInHand(InteractionHand.MAIN_HAND, mainHandItem);//player.getInventory().items.set(player.getInventory().selected, mainHandItem);
        }
        return false;
    }

    private static boolean isEdible(ItemStack stack, LivingEntity player) {
        if(!stack.isEdible()) {
            return false;
        }
        FoodProperties foodProperties = stack.getItem().getFoodProperties();
        return foodProperties != null && foodProperties.getNutrition() >= 1;

      /*  if(!stack.has(DataComponents.FOOD)) {
            return false;
        }
        //FoodProperties foodProperties = stack.getItem().getFoodProperties(stack, player);
        FoodProperties foodProperties = stack.get(DataComponents.FOOD);
        return foodProperties != null && foodProperties.nutrition() >= 1; */
        //return true;
    }
}