package com.tiviacz.travelersbackpack.common.recipes;

import com.google.gson.JsonObject;
import com.tiviacz.travelersbackpack.init.ModRecipeSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BackpackUpgradeRecipeBuilder
{
    private final Ingredient base;
    private final Ingredient addition;
    private final Item result;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private final RecipeSerializer<?> type;

    public BackpackUpgradeRecipeBuilder(RecipeSerializer<?> type, Ingredient base, Ingredient addition, Item result) {
        this.type = type;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public static BackpackUpgradeRecipeBuilder upgrade(Ingredient pBase, Ingredient pAddition, Item pResult) {
        return new BackpackUpgradeRecipeBuilder(ModRecipeSerializers.BACKPACK_UPGRADE.get(), pBase, pAddition, pResult);
    }

    public BackpackUpgradeRecipeBuilder unlocks(String pName, CriterionTriggerInstance pCriterion) {
        this.advancement.addCriterion(pName, pCriterion);
        return this;
    }

    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, String pId) {
        this.save(pFinishedRecipeConsumer, new ResourceLocation(pId));
    }

    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pId) {
        this.ensureValid(pId);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pId)).rewards(AdvancementRewards.Builder.recipe(pId)).requirements(RequirementsStrategy.OR);
        pFinishedRecipeConsumer.accept(new BackpackUpgradeRecipeBuilder.Result(pId, this.type, this.base, this.addition, this.result, this.advancement, new ResourceLocation(pId.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + pId.getPath())));
    }

    private void ensureValid(ResourceLocation pId) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + pId);
        }
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Ingredient base;
        private final Ingredient addition;
        private final Item result;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final RecipeSerializer<?> type;

        public Result(ResourceLocation pId, RecipeSerializer<?> pType, Ingredient pBase, Ingredient pAddition, Item pResult, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId) {
            this.id = pId;
            this.type = pType;
            this.base = pBase;
            this.addition = pAddition;
            this.result = pResult;
            this.advancement = pAdvancement;
            this.advancementId = pAdvancementId;
        }

        public void serializeRecipeData(JsonObject pJson) {
            pJson.add("base", this.base.toJson());
            pJson.add("addition", this.addition.toJson());
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", Registry.ITEM.getKey(this.result).toString());
            pJson.add("result", jsonobject);
        }

        public ResourceLocation getId() {
            return this.id;
        }

        public RecipeSerializer<?> getType() {
            return this.type;
        }

        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
