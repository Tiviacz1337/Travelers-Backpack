package com.tiviacz.travelersbackpack.common.recipes;

import com.google.gson.JsonObject;
import com.tiviacz.travelersbackpack.init.ModRecipeSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BackpackUpgradeRecipeBuilder
{
    private final Ingredient base;
    private final Ingredient addition;
    private final Item result;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private final IRecipeSerializer<?> type;

    public BackpackUpgradeRecipeBuilder(IRecipeSerializer<?> p_i232549_1_, Ingredient p_i232549_2_, Ingredient p_i232549_3_, Item p_i232549_4_) {
        this.type = p_i232549_1_;
        this.base = p_i232549_2_;
        this.addition = p_i232549_3_;
        this.result = p_i232549_4_;
    }

    public static BackpackUpgradeRecipeBuilder smithing(Ingredient p_240502_0_, Ingredient p_240502_1_, Item p_240502_2_) {
        return new BackpackUpgradeRecipeBuilder(ModRecipeSerializers.BACKPACK_UPGRADE.get(), p_240502_0_, p_240502_1_, p_240502_2_);
    }

    public BackpackUpgradeRecipeBuilder unlocks(String p_240503_1_, ICriterionInstance p_240503_2_) {
        this.advancement.addCriterion(p_240503_1_, p_240503_2_);
        return this;
    }

    public void save(Consumer<IFinishedRecipe> p_240504_1_, String p_240504_2_) {
        this.save(p_240504_1_, new ResourceLocation(p_240504_2_));
    }

    public void save(Consumer<IFinishedRecipe> p_240505_1_, ResourceLocation p_240505_2_) {
        this.ensureValid(p_240505_2_);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(p_240505_2_)).rewards(AdvancementRewards.Builder.recipe(p_240505_2_)).requirements(IRequirementsStrategy.OR);
        p_240505_1_.accept(new BackpackUpgradeRecipeBuilder.Result(p_240505_2_, this.type, this.base, this.addition, this.result, this.advancement, new ResourceLocation(p_240505_2_.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + p_240505_2_.getPath())));
    }

    private void ensureValid(ResourceLocation p_240506_1_) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + p_240506_1_);
        }
    }

    public static class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final Ingredient base;
        private final Ingredient addition;
        private final Item result;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final IRecipeSerializer<?> type;

        public Result(ResourceLocation p_i232550_1_, IRecipeSerializer<?> p_i232550_2_, Ingredient p_i232550_3_, Ingredient p_i232550_4_, Item p_i232550_5_, Advancement.Builder p_i232550_6_, ResourceLocation p_i232550_7_) {
            this.id = p_i232550_1_;
            this.type = p_i232550_2_;
            this.base = p_i232550_3_;
            this.addition = p_i232550_4_;
            this.result = p_i232550_5_;
            this.advancement = p_i232550_6_;
            this.advancementId = p_i232550_7_;
        }

        public void serializeRecipeData(JsonObject p_218610_1_) {
            p_218610_1_.add("base", this.base.toJson());
            p_218610_1_.add("addition", this.addition.toJson());
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", Registry.ITEM.getKey(this.result).toString());
            p_218610_1_.add("result", jsonobject);
        }

        public ResourceLocation getId() {
            return this.id;
        }

        public IRecipeSerializer<?> getType() {
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