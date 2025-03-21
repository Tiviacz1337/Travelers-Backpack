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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

public class BackpackUpgradeRecipe extends SmithingTransformRecipe {
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;

    public BackpackUpgradeRecipe(Ingredient pTemplate, Ingredient pBase, Ingredient pAddition, ItemStack pResult) {
        super(pTemplate, pBase, pAddition, pResult);
        this.template = pTemplate;
        this.base = pBase;
        this.addition = pAddition;
        this.result = pResult;
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput pInput, HolderLookup.Provider pRegistries) {
        ItemStack result = pInput.getItem(1).transmuteCopy(this.result.getItem(), this.result.getCount());
        result.applyComponents(this.result.getComponentsPatch());

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
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BACKPACK_UPGRADE.get();
    }

    public static class Serializer implements RecipeSerializer<BackpackUpgradeRecipe> {
        private static final MapCodec<BackpackUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(
                p_340782_ -> p_340782_.group(
                                Ingredient.CODEC.fieldOf("template").forGetter(p_301310_ -> p_301310_.template),
                                Ingredient.CODEC.fieldOf("base").forGetter(p_300938_ -> p_300938_.base),
                                Ingredient.CODEC.fieldOf("addition").forGetter(p_301153_ -> p_301153_.addition),
                                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(p_300935_ -> p_300935_.result)
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
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(p_320375_);
            Ingredient ingredient1 = Ingredient.CONTENTS_STREAM_CODEC.decode(p_320375_);
            Ingredient ingredient2 = Ingredient.CONTENTS_STREAM_CODEC.decode(p_320375_);
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(p_320375_);
            return new BackpackUpgradeRecipe(ingredient, ingredient1, ingredient2, itemstack);
        }

        private static void toNetwork(RegistryFriendlyByteBuf p_320743_, BackpackUpgradeRecipe p_319840_) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(p_320743_, p_319840_.template);
            Ingredient.CONTENTS_STREAM_CODEC.encode(p_320743_, p_319840_.base);
            Ingredient.CONTENTS_STREAM_CODEC.encode(p_320743_, p_319840_.addition);
            ItemStack.STREAM_CODEC.encode(p_320743_, p_319840_.result);
        }
    }
}