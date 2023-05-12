package com.tiviacz.travelersbackpack.common.recipes;

import com.google.gson.JsonObject;
import com.tiviacz.travelersbackpack.init.ModCrafting;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BackpackUpgradeRecipeJsonBuilder
{
    private final Ingredient base;
    private final Ingredient addition;
    private final Item result;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.create();
    private final RecipeSerializer<?> serializer;

    public BackpackUpgradeRecipeJsonBuilder(RecipeSerializer<?> serializer, Ingredient base, Ingredient addition, Item result) {
        this.serializer = serializer;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public static BackpackUpgradeRecipeJsonBuilder create(Ingredient base, Ingredient addition, Item result) {
        return new BackpackUpgradeRecipeJsonBuilder(ModCrafting.BACKPACK_UPGRADE, base, addition, result);
    }

    public BackpackUpgradeRecipeJsonBuilder criterion(String criterionName, CriterionConditions conditions) {
        this.advancementBuilder.criterion(criterionName, conditions);
        return this;
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, String recipeId) {
        this.offerTo(exporter, new Identifier(recipeId));
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        this.validate(recipeId);
        this.advancementBuilder.parent(new Identifier("recipes/root")).criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(net.minecraft.advancement.AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriterionMerger.OR);
        RecipeSerializer var10004 = this.serializer;
        Ingredient var10005 = this.base;
        Ingredient var10006 = this.addition;
        Item var10007 = this.result;
        Advancement.Builder var10008 = this.advancementBuilder;
        String var10011 = recipeId.getNamespace();
        String var10012 = this.result.getGroup().getName();
        exporter.accept(new BackpackUpgradeRecipeJsonBuilder.BackpackUpgradeRecipeJsonProvider(recipeId, var10004, var10005, var10006, var10007, var10008, new Identifier(var10011, "recipes/" + var10012 + "/" + recipeId.getPath())));
    }

    private void validate(Identifier recipeId) {
        if (this.advancementBuilder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }

    public static class BackpackUpgradeRecipeJsonProvider implements RecipeJsonProvider {
        private final Identifier recipeId;
        private final Ingredient base;
        private final Ingredient addition;
        private final Item result;
        private final Advancement.Builder advancementBuilder;
        private final Identifier advancementId;
        private final RecipeSerializer<?> serializer;

        public BackpackUpgradeRecipeJsonProvider(Identifier recipeId, RecipeSerializer<?> serializer, Ingredient base, Ingredient addition, Item result, Advancement.Builder advancementBuilder, Identifier advancementId) {
            this.recipeId = recipeId;
            this.serializer = serializer;
            this.base = base;
            this.addition = addition;
            this.result = result;
            this.advancementBuilder = advancementBuilder;
            this.advancementId = advancementId;
        }

        public void serialize(JsonObject json) {
            json.add("base", this.base.toJson());
            json.add("addition", this.addition.toJson());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("item", Registry.ITEM.getId(this.result).toString());
            json.add("result", jsonObject);
        }

        public Identifier getRecipeId() {
            return this.recipeId;
        }

        public RecipeSerializer<?> getSerializer() {
            return this.serializer;
        }

        @Nullable
        public JsonObject toAdvancementJson() {
            return this.advancementBuilder.toJson();
        }

        @Nullable
        public Identifier getAdvancementId() {
            return this.advancementId;
        }
    }
}