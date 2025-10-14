package com.tiviacz.travelersbackpack.common.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModRecipeSerializers;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SmithingRecipeDisplay;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class BackpackUpgradeRecipe implements SmithingRecipe {
    final Optional<Ingredient> template;
    final Ingredient base;
    final Optional<Ingredient> addition;
    final TransmuteResult result;
    @Nullable
    private PlacementInfo placementInfo;

    public BackpackUpgradeRecipe(Optional<Ingredient> pTemplate, Ingredient pBase, Optional<Ingredient> pAddition, TransmuteResult pResult) {
        this.template = pTemplate;
        this.base = pBase;
        this.addition = pAddition;
        this.result = pResult;
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput pInput, HolderLookup.Provider pRegistries) {
        ItemStack result = this.result.apply(pInput.base());
        result.applyComponents(this.result.components());

        ItemStack base = pInput.getItem(1);
        ItemStack addition = pInput.getItem(2);
        int tier = base.getOrDefault(ModDataComponents.TIER, 0);

        if(addition.is(Tiers.of(tier).getTierUpgradeIngredient())) {
            upgradeInventory(result, Tiers.of(tier).getNextTier());
            return result;
        }
        return ItemStack.EMPTY;
    }

    public void upgradeInventory(ItemStack stack, Tiers.Tier nextTier) {
        stack.set(ModDataComponents.TIER, nextTier.getOrdinal());
        stack.set(ModDataComponents.STORAGE_SLOTS, nextTier.getStorageSlots());
        stack.set(ModDataComponents.UPGRADE_SLOTS, nextTier.getUpgradeSlots());
        stack.set(ModDataComponents.TOOL_SLOTS, nextTier.getToolSlots());
        if(stack.has(ModDataComponents.RENDER_INFO)) {
            stack.set(ModDataComponents.RENDER_INFO, getUpgradedTanksCapacity(stack, nextTier.getStorageSlots()));
        }
    }

    public RenderInfo getUpgradedTanksCapacity(ItemStack stack, int storageSlots) {
        boolean extended = storageSlots > 81;
        int rows = (int)Math.ceil((double)storageSlots / (extended ? 11 : 9)) + (extended ? 2 : 0);
        CompoundTag infoTag = stack.get(ModDataComponents.RENDER_INFO).compoundTag().copy();
        RenderInfo newInfo = new RenderInfo(infoTag);
        newInfo.updateCapacity(Tiers.of(stack.getOrDefault(ModDataComponents.TIER, 0)).getTankCapacityPerRow() * rows);
        return newInfo;
    }

    @Override
    public RecipeSerializer<BackpackUpgradeRecipe> getSerializer() {
        return ModRecipeSerializers.BACKPACK_UPGRADE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        if(this.placementInfo == null) {
            this.placementInfo = PlacementInfo.createFromOptionals(List.of(this.template, Optional.of(this.base), this.addition));
        }

        return this.placementInfo;
    }

    @Override
    public Optional<Ingredient> templateIngredient() {
        return this.template;
    }

    @Override
    public Ingredient baseIngredient() {
        return this.base;
    }

    @Override
    public Optional<Ingredient> additionIngredient() {
        return this.addition;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(
                new SmithingRecipeDisplay(
                        Ingredient.optionalIngredientToDisplay(this.template),
                        this.base.display(),
                        Ingredient.optionalIngredientToDisplay(this.addition),
                        this.result.display(),
                        new SlotDisplay.ItemSlotDisplay(Items.SMITHING_TABLE)
                )
        );
    }

    public static class Serializer implements RecipeSerializer<BackpackUpgradeRecipe> {
        private static final MapCodec<BackpackUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(
                p_340782_ -> p_340782_.group(
                                Ingredient.CODEC.optionalFieldOf("template").forGetter(p_301310_ -> p_301310_.template),
                                Ingredient.CODEC.fieldOf("base").forGetter(p_300938_ -> p_300938_.base),
                                Ingredient.CODEC.optionalFieldOf("addition").forGetter(p_301153_ -> p_301153_.addition),
                                TransmuteResult.CODEC.fieldOf("result").forGetter(p_300935_ -> p_300935_.result)
                        )
                        .apply(p_340782_, BackpackUpgradeRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, BackpackUpgradeRecipe> STREAM_CODEC = StreamCodec.of(
                BackpackUpgradeRecipe.Serializer::toNetwork, BackpackUpgradeRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<BackpackUpgradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BackpackUpgradeRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static BackpackUpgradeRecipe fromNetwork(RegistryFriendlyByteBuf p_320375_) {
            Optional<Ingredient> ingredient = Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.decode(p_320375_);
            Ingredient ingredient1 = Ingredient.CONTENTS_STREAM_CODEC.decode(p_320375_);
            Optional<Ingredient> ingredient2 = Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.decode(p_320375_);
            TransmuteResult itemstack = TransmuteResult.STREAM_CODEC.decode(p_320375_);
            return new BackpackUpgradeRecipe(ingredient, ingredient1, ingredient2, itemstack);
        }

        private static void toNetwork(RegistryFriendlyByteBuf p_320743_, BackpackUpgradeRecipe p_319840_) {
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.encode(p_320743_, p_319840_.template);
            Ingredient.CONTENTS_STREAM_CODEC.encode(p_320743_, p_319840_.base);
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.encode(p_320743_, p_319840_.addition);
            TransmuteResult.STREAM_CODEC.encode(p_320743_, p_319840_.result);
        }
    }
}