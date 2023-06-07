package com.tiviacz.travelersbackpack.common.recipes;

import com.google.gson.JsonObject;
import com.tiviacz.travelersbackpack.init.ModRecipeSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BackpackUpgradeRecipeBuilder
{
    private final Ingredient f_266030_;
    private final Ingredient f_265893_;
    private final Ingredient f_265959_;
    private final RecipeCategory f_266018_;
    private final Item f_266005_;
    private final Advancement.Builder f_266090_ = Advancement.Builder.advancement();
    private final RecipeSerializer<?> f_266006_;

    public BackpackUpgradeRecipeBuilder(RecipeSerializer<?> p_266683_, Ingredient p_266973_, Ingredient p_267047_, Ingredient p_267009_, RecipeCategory p_266694_, Item p_267183_) {
        this.f_266018_ = p_266694_;
        this.f_266006_ = p_266683_;
        this.f_266030_ = p_266973_;
        this.f_265893_ = p_267047_;
        this.f_265959_ = p_267009_;
        this.f_266005_ = p_267183_;
    }

    public static BackpackUpgradeRecipeBuilder m_266555_(Ingredient p_267071_, Ingredient p_266959_, Ingredient p_266803_, RecipeCategory p_266757_, Item p_267256_) {
        return new BackpackUpgradeRecipeBuilder(ModRecipeSerializers.BACKPACK_UPGRADE.get(), p_267071_, p_266959_, p_266803_, p_266757_, p_267256_);
    }

    public BackpackUpgradeRecipeBuilder m_266439_(String p_266919_, CriterionTriggerInstance p_267277_) {
        this.f_266090_.addCriterion(p_266919_, p_267277_);
        return this;
    }

    public void m_266260_(Consumer<FinishedRecipe> p_267068_, String p_267035_) {
        this.m_266371_(p_267068_, new ResourceLocation(p_267035_));
    }

    public void m_266371_(Consumer<FinishedRecipe> p_267089_, ResourceLocation p_267287_) {
        this.m_266305_(p_267287_);
        this.f_266090_.parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(p_267287_)).rewards(AdvancementRewards.Builder.recipe(p_267287_)).requirements(RequirementsStrategy.OR);
        p_267089_.accept(new BackpackUpgradeRecipeBuilder.Result(p_267287_, this.f_266006_, this.f_266030_, this.f_265893_, this.f_265959_, this.f_266005_, this.f_266090_, p_267287_.withPrefix("recipes/" + this.f_266018_.getFolderName() + "/")));
    }

    private void m_266305_(ResourceLocation p_267259_) {
        if (this.f_266090_.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + p_267259_);
        }
    }

    public static record Result(ResourceLocation f_266011_, RecipeSerializer<?> f_265962_, Ingredient f_266002_, Ingredient f_266112_, Ingredient f_265903_, Item f_265972_, Advancement.Builder f_265855_, ResourceLocation f_266094_) implements FinishedRecipe {
        public void serializeRecipeData(JsonObject p_266713_) {
            p_266713_.add("template", this.f_266002_.toJson());
            p_266713_.add("base", this.f_266112_.toJson());
            p_266713_.add("addition", this.f_265903_.toJson());
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.f_265972_).toString());
            p_266713_.add("result", jsonobject);
        }

        /**
         * Gets the ID for the recipe.
         */
        public ResourceLocation getId() {
            return this.f_266011_;
        }

        public RecipeSerializer<?> getType() {
            return this.f_265962_;
        }

        /**
         * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
         */
        @Nullable
        public JsonObject serializeAdvancement() {
            return this.f_265855_.serializeToJson();
        }

        /**
         * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #getAdvancementJson}
         * is non-null.
         */
        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.f_266094_;
        }
    }
}