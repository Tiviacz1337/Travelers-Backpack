package com.tiviacz.travelersbackpack.common.recipes;

import com.google.gson.JsonObject;
import com.tiviacz.travelersbackpack.init.ModRecipeSerializers;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.LinkedHashMap;
import java.util.Map;

public class BackpackUpgradeRecipeBuilder
{
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;
    private final RecipeCategory category;
    private final Item result;
    private final Map<String, Criterion<?>> f_291373_ = new LinkedHashMap<>();
    private final RecipeSerializer<?> type;

    public BackpackUpgradeRecipeBuilder(RecipeSerializer<?> pType, Ingredient pTemplate, Ingredient pBase, Ingredient pAddition, RecipeCategory pCategory, Item pResult) {
        this.category = pCategory;
        this.type = pType;
        this.template = pTemplate;
        this.base = pBase;
        this.addition = pAddition;
        this.result = pResult;
    }

    public static BackpackUpgradeRecipeBuilder backpackUpgrade(Ingredient pTemplate, Ingredient pBase, Ingredient pAddition, RecipeCategory pCategory, Item pResult) {
        return new BackpackUpgradeRecipeBuilder(ModRecipeSerializers.BACKPACK_UPGRADE.get(), pTemplate, pBase, pAddition, pCategory, pResult);
    }

    public BackpackUpgradeRecipeBuilder unlocks(String pKey, Criterion<?> p_297342_) {
        this.f_291373_.put(pKey, p_297342_);
        return this;
    }

    public void save(RecipeOutput p_300964_, String pLocation) {
        this.save(p_300964_, new ResourceLocation(pLocation));
    }

    public void save(RecipeOutput p_301024_, ResourceLocation pLocation) {
        this.ensureValid(pLocation);
        Advancement.Builder advancement$builder = p_301024_.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pLocation)).rewards(AdvancementRewards.Builder.recipe(pLocation)).requirements(AdvancementRequirements.Strategy.OR);
        this.f_291373_.forEach(advancement$builder::addCriterion);
        p_301024_.accept(new BackpackUpgradeRecipeBuilder.Result(pLocation, this.type, this.template, this.base, this.addition, this.result, advancement$builder.build(pLocation.withPrefix("recipes/" + this.category.getFolderName() + "/"))));
    }

    private void ensureValid(ResourceLocation pLocation) {
        if (this.f_291373_.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + pLocation);
        }
    }

    public static record Result(ResourceLocation id, RecipeSerializer<?> type, Ingredient template, Ingredient base, Ingredient addition, Item result, AdvancementHolder advancement) implements FinishedRecipe {
        public void serializeRecipeData(JsonObject p_266713_) {
            p_266713_.add("template", this.template.toJson(true));
            p_266713_.add("base", this.base.toJson(true));
            p_266713_.add("addition", this.addition.toJson(true));
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
            p_266713_.add("result", jsonobject);
        }

        public ResourceLocation m_126168_() {
            return this.id;
        }

        public RecipeSerializer<?> m_126169_() {
            return this.type;
        }

        public AdvancementHolder m_126373_() {
            return this.advancement;
        }
    }
}