package com.tiviacz.travelersbackpack.common.recipes;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class BackpackUpgradeRecipeJsonBuilder
{
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;
    private final RecipeCategory category;
    private final Item result;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap();

    public BackpackUpgradeRecipeJsonBuilder(Ingredient template, Ingredient base, Ingredient addition, RecipeCategory category, Item result) {
        this.category = category;
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public static BackpackUpgradeRecipeJsonBuilder create(Ingredient template, Ingredient base, Ingredient addition, RecipeCategory category, Item result) {
        return new BackpackUpgradeRecipeJsonBuilder(template, base, addition, category, result);
    }

    public BackpackUpgradeRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    public void offerTo(RecipeExporter exporter, String recipeId) {
        this.offerTo(exporter, new Identifier(recipeId));
    }

    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        this.validate(recipeId);
        Advancement.Builder builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        Objects.requireNonNull(builder);
        this.criteria.forEach(builder::criterion);
        BackpackUpgradeRecipe backpackUpgradeRecipe = new BackpackUpgradeRecipe(this.template, this.base, this.addition, new ItemStack(this.result));
        exporter.accept(recipeId, backpackUpgradeRecipe, builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
    }

    private void validate(Identifier recipeId) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }
}