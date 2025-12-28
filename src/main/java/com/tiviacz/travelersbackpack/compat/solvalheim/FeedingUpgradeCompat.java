package com.tiviacz.travelersbackpack.compat.solvalheim;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.feeding.FeedingFilterSettings;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FeedingUpgradeCompat {
    public static boolean tryFeedingFoodFromStorage(BackpackWrapper wrapper, Level level, int hungerLevel, Player player, FeedingFilterSettings filterSettings) {
        if(level.isClientSide()) return false;
        ItemStackHandler storage = wrapper.getStorageForInputOutput();
        return InventoryHelper.iterate(storage, (slot, stack) -> tryFeedingStack(level, hungerLevel, player, slot, stack, storage, filterSettings));
    }

    public static boolean tryFeedingStack(Level level, int hungerLevel, Player player, Integer slot, ItemStack stack, ItemStackHandler backpackStorage, FeedingFilterSettings filterSettings) {
        if(isEdible(stack, player) && matchesFilter(player, stack, filterSettings)) {
            ItemStack mainHandItem = player.getMainHandItem();
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);

            ItemStack singleItemCopy = stack.copy();
            singleItemCopy.setCount(1);

            if(singleItemCopy.use(level, player, InteractionHand.MAIN_HAND).getResult() == InteractionResult.CONSUME) {
                stack.shrink(1);
                backpackStorage.setStackInSlot(slot, stack);

                InteractionResultHolder<ItemStack> result = UseItemCallback.EVENT.invoker().interact(player, level, InteractionHand.MAIN_HAND);
                ItemStack resultItem = result.getObject();
                if(result.getResult() == InteractionResult.PASS) {
                    resultItem = singleItemCopy.getItem().finishUsingItem(singleItemCopy, level, player);
                }

                if(!resultItem.isEmpty()) {
                    ItemStack insertResult = InventoryHelper.addItemStackToHandler(backpackStorage, resultItem, false);
                    if(!insertResult.isEmpty()) {
                        player.drop(insertResult, true);
                    }
                }
                player.setItemInHand(InteractionHand.MAIN_HAND, mainHandItem);
                return true;
            }
            player.setItemInHand(InteractionHand.MAIN_HAND, mainHandItem);
        }
        return false;
    }

    private static boolean matchesFilter(Player player, ItemStack stack, FeedingFilterSettings filter) {
        if(filter.getSettings().get(FeedingFilterSettings.ALLOW_MODE) == FeedingFilterSettings.ALLOW) {
            return filter.getFilterItems().stream().anyMatch(food -> ItemStack.isSameItemSameTags(food.copyWithCount(1), stack)) && filter.checkHarmfulEffects(stack);
        }
        if(filter.getSettings().get(FeedingFilterSettings.ALLOW_MODE) == FeedingFilterSettings.BLOCK) {
            return filter.getFilterItems().stream().noneMatch(food -> ItemStack.isSameItemSameTags(food.copyWithCount(1), stack)) && filter.checkHarmfulEffects(stack);
        }
        return false;
    }

    private static boolean isEdible(ItemStack stack, LivingEntity player) {
        if(!stack.isEdible()) {
            return false;
        }
        FoodProperties foodProperties = stack.getItem().getFoodProperties();
        return foodProperties != null && foodProperties.getNutrition() >= 1;
    }
}