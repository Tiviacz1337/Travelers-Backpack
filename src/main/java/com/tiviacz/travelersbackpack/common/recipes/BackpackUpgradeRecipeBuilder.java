package com.tiviacz.travelersbackpack.common.recipes;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.TransmuteResult;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class BackpackUpgradeRecipeBuilder {
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;
    private final RecipeCategory category;
    private final Item result;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public BackpackUpgradeRecipeBuilder(Ingredient pTemplate, Ingredient pBase, Ingredient pAddition, RecipeCategory pCategory, Item pResult) {
        this.category = pCategory;
        this.template = pTemplate;
        this.base = pBase;
        this.addition = pAddition;
        this.result = pResult;
    }

    public static BackpackUpgradeRecipeBuilder backpackUpgrade(Ingredient pTemplate, Ingredient pBase, Ingredient pAddition, RecipeCategory pCategory, Item pResult) {
        return new BackpackUpgradeRecipeBuilder(pTemplate, pBase, pAddition, pCategory, pResult);
    }

    public BackpackUpgradeRecipeBuilder unlocks(String pKey, Criterion<?> criterion) {
        this.criteria.put(pKey, criterion);
        return this;
    }

    public void save(RecipeOutput recipeOutput, String recipeId) {
        this.save(recipeOutput, ResourceKey.create(Registries.RECIPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, recipeId)));
    }

    public void save(RecipeOutput output, ResourceKey<Recipe<?>> resourceKey) {
        this.ensureValid(resourceKey);
        Advancement.Builder advancement$builder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
                .rewards(AdvancementRewards.Builder.recipe(resourceKey))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement$builder::addCriterion);
        BackpackUpgradeRecipe backpackUpgradeRecipe = new BackpackUpgradeRecipe(
                Optional.of(this.template), this.base, Optional.of(this.addition), new TransmuteResult(this.result)
        );
        output.accept(
                resourceKey, backpackUpgradeRecipe, advancement$builder.build(resourceKey.location().withPrefix("recipes/" + this.category.getFolderName() + "/"))
        );
    }

    private void ensureValid(ResourceKey<Recipe<?>> recipe) {
        if(this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipe.location());
        }
    }
}