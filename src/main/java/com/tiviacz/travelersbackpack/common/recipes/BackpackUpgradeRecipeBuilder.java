package com.tiviacz.travelersbackpack.common.recipes;

import com.google.gson.JsonObject;
import com.tiviacz.travelersbackpack.init.ModRecipeSerializers;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

import java.util.LinkedHashMap;
import java.util.Map;

public class BackpackUpgradeRecipeBuilder
{
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

    public BackpackUpgradeRecipeBuilder unlocks(String pKey, Criterion<?> p_297342_) {
        this.criteria.put(pKey, p_297342_);
        return this;
    }

    public void save(RecipeOutput pRecipeOutput, String pLocation) {
        this.save(pRecipeOutput, new ResourceLocation(pLocation));
    }

    public void save(RecipeOutput pRecipeOutput, ResourceLocation pRecipeId)
    {
        this.ensureValid(pRecipeId);
        Advancement.Builder advancement$builder = pRecipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId)).rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement$builder::addCriterion);
        BackpackUpgradeRecipe backpackUpgradeRecipe = new BackpackUpgradeRecipe(this.template, this.base, this.addition, new ItemStack(this.result));
        pRecipeOutput.accept(pRecipeId, backpackUpgradeRecipe, advancement$builder.build(pRecipeId.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation pLocation) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + pLocation);
        }
    }
}