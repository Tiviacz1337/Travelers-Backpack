package com.tiviacz.travelersbackpack.inventory.upgrades.feeding;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.upgrades.FilterSettingsBase;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FeedingFilterSettings extends FilterSettingsBase {
    //Button Types
    public static final int ALLOW_MODE = 0;
    public static final int HUNGER_MODE = 1;
    public static final int IGNORE_EFFECT_MODE = 2;

    //Options
    public static final int ALLOW = 0;
    public static final int BLOCK = 1;

    public static final int ALWAYS_EAT = 0;
    public static final int HALF_NUTRITION = 1;
    public static final int FULL_NUTRITION = 2;

    public static final int BLOCK_BAD_EFFECTS = 0;
    public static final int ALLOW_BAD_EFFECTS = 1;

    public FeedingFilterSettings(ItemStackHandler storage, List<ItemStack> items, List<Integer> filterSettings) {
        super(storage, items, filterSettings, TravelersBackpackConfig.SERVER.backpackUpgrades.feedingUpgradeSettings.filterSlotCount.get());
    }

    @Override
    public boolean matchesFilter(@Nullable Player player, ItemStack stack) {
        FoodData foodData = player.getFoodData();
        if(filterSettings.get(ALLOW_MODE) == ALLOW) {
            return this.filterItems.stream().anyMatch(food -> ItemStack.isSameItemSameComponents(food.copyWithCount(1), stack)) && compareHungerLevel(foodData, stack) && checkHarmfulEffects(stack);
        }
        if(filterSettings.get(ALLOW_MODE) == BLOCK) {
            return this.filterItems.stream().noneMatch(food -> ItemStack.isSameItemSameComponents(food.copyWithCount(1), stack)) && compareHungerLevel(foodData, stack) && checkHarmfulEffects(stack);
        }
        return false;
    }

    public boolean compareHungerLevel(FoodData foodData, ItemStack stack) {
        if(filterSettings.get(HUNGER_MODE) == ALWAYS_EAT) {
            return foodData.needsFood();
        }
        if(filterSettings.get(HUNGER_MODE) == HALF_NUTRITION) {
            return getNutritionDifference(foodData, stack) <= getHalfOfStackHunger(stack);
        }
        if(filterSettings.get(HUNGER_MODE) == FULL_NUTRITION) {
            return getNutritionDifference(foodData, stack) <= 0;
        }
        return false;
    }

    public boolean checkHarmfulEffects(ItemStack stack) {
        if(filterSettings.get(IGNORE_EFFECT_MODE) == BLOCK_BAD_EFFECTS) {
            return checkHarmfulEffect(stack.get(DataComponents.FOOD));
        } else {
            return true;
        }
    }

    public boolean checkHarmfulEffect(FoodProperties props) {
        for(FoodProperties.PossibleEffect effect : props.effects()) {
            if(effect.effect().getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                return false;
            }
        }
        return true;
    }

    public int getNutritionDifference(FoodData foodData, ItemStack stack) {
        if(foodData.needsFood()) {
            FoodProperties foodProps = stack.get(DataComponents.FOOD);
            int foodNutrition = foodProps.nutrition();
            int playerNutrition = foodData.getFoodLevel();
            return playerNutrition + foodNutrition - 20;
        }
        return 0;
    }

    public int getHalfOfStackHunger(ItemStack stack) {
        return stack.get(DataComponents.FOOD).nutrition() / 2;
    }
}