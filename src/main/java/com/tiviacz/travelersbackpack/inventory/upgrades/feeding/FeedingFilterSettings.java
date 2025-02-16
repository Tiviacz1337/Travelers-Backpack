package com.tiviacz.travelersbackpack.inventory.upgrades.feeding;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

import java.util.List;

public class FeedingFilterSettings {
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

    private List<ItemStack> filterItems;
    private List<Integer> filterSettings;
    private ItemStackHandler storage;

    public FeedingFilterSettings(ItemStackHandler storage, List<ItemStack> items, List<Integer> filterSettings) {
        this.filterItems = items;
        this.filterSettings = filterSettings;
        this.storage = storage;
    }

    public List<Integer> getSettings() {
        return this.filterSettings;
    }

    public boolean canEat(FoodData foodData, ItemStack stack) {
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
            return checkHarmfulEffect(stack.get(DataComponents.CONSUMABLE));
        } else {
            return true;
        }
    }

    public boolean checkHarmfulEffect(Consumable consumable) {
        if(consumable != null) {
            for(ConsumeEffect effect : consumable.onConsumeEffects()) {
                if(effect.getType() == ConsumeEffect.Type.APPLY_EFFECTS) {
                    ApplyStatusEffectsConsumeEffect applyStatusEffect = (ApplyStatusEffectsConsumeEffect)effect;
                    for(MobEffectInstance mobEffect : applyStatusEffect.effects()) {
                        if(mobEffect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                            return false;
                        }
                    }
                }
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

    public void updateFilter(List<ItemStack> items) {
        this.filterItems = items.stream().limit(TravelersBackpackConfig.getConfig().backpackUpgrades.feedingUpgradeSettings.filterSlotCount).filter(stack -> !stack.isEmpty()).toList();
    }

    public void updateSettings(List<Integer> settings) {
        this.filterSettings = settings;
    }
}

