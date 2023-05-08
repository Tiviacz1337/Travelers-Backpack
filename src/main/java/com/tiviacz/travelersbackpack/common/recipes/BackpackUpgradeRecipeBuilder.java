package com.tiviacz.travelersbackpack.common.recipes;

import com.google.gson.JsonObject;
import com.tiviacz.travelersbackpack.TravelersBackpack;
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

public class BackpackUpgradeRecipeBuilder {
    private final Ingredient f_266058_;
    private final Ingredient f_265936_;
    private final RecipeCategory f_266038_;
    private final Item f_265902_;
    private final Advancement.Builder f_266025_ = Advancement.Builder.advancement();
    private final RecipeSerializer<?> f_265951_;

    public BackpackUpgradeRecipeBuilder(RecipeSerializer<?> p_266753_, Ingredient p_267080_, Ingredient p_267246_, RecipeCategory p_267024_, Item p_266767_) {
        this.f_266038_ = p_267024_;
        this.f_265951_ = p_266753_;
        this.f_266058_ = p_267080_;
        this.f_265936_ = p_267246_;
        this.f_265902_ = p_266767_;
    }

    public static BackpackUpgradeRecipeBuilder m_266485_(Ingredient p_266949_, Ingredient p_267302_, RecipeCategory p_266837_, Item p_266863_) {
        return new BackpackUpgradeRecipeBuilder(ModRecipeSerializers.BACKPACK_UPGRADE.get(), p_266949_, p_267302_, p_266837_, p_266863_);
    }

    public BackpackUpgradeRecipeBuilder m_266457_(String p_267310_, CriterionTriggerInstance p_266808_) {
        this.f_266025_.addCriterion(p_267310_, p_266808_);
        return this;
    }

    public void m_266193_(Consumer<FinishedRecipe> p_266900_, String p_266899_) {
        this.m_266417_(p_266900_, new ResourceLocation(TravelersBackpack.MODID, p_266899_));
    }

    public void m_266417_(Consumer<FinishedRecipe> p_266852_, ResourceLocation p_267253_) {
        this.m_266347_(p_267253_);
        this.f_266025_.parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(p_267253_)).rewards(AdvancementRewards.Builder.recipe(p_267253_)).requirements(RequirementsStrategy.OR);
        p_266852_.accept(new BackpackUpgradeRecipeBuilder.Result(p_267253_, this.f_265951_, this.f_266058_, this.f_265936_, this.f_265902_, this.f_266025_, p_267253_.withPrefix("recipes/" + this.f_266038_.getFolderName() + "/")));
    }

    private void m_266347_(ResourceLocation p_266958_) {
        if (this.f_266025_.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + p_266958_);
        }
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation f_265979_;
        private final Ingredient f_265984_;
        private final Ingredient f_265983_;
        private final Item f_266047_;
        private final Advancement.Builder f_265909_;
        private final ResourceLocation f_265900_;
        private final RecipeSerializer<?> f_265953_;

        public Result(ResourceLocation p_267216_, RecipeSerializer<?> p_266997_, Ingredient p_266970_, Ingredient p_266975_, Item p_267271_, Advancement.Builder p_266866_, ResourceLocation p_266867_) {
            this.f_265979_ = p_267216_;
            this.f_265953_ = p_266997_;
            this.f_265984_ = p_266970_;
            this.f_265983_ = p_266975_;
            this.f_266047_ = p_267271_;
            this.f_265909_ = p_266866_;
            this.f_265900_ = p_266867_;
        }

        public void serializeRecipeData(JsonObject p_267275_) {
            p_267275_.add("base", this.f_265984_.toJson());
            p_267275_.add("addition", this.f_265983_.toJson());
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.f_266047_).toString());
            p_267275_.add("result", jsonobject);
        }

        public ResourceLocation getId() {
            return this.f_265979_;
        }

        public RecipeSerializer<?> getType() {
            return this.f_265953_;
        }

        @Nullable
        public JsonObject serializeAdvancement() {
            return this.f_265909_.serializeToJson();
        }

        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.f_265900_;
        }
    }
}
