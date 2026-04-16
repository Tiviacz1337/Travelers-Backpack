package com.tiviacz.travelersbackpack.common.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SmithingRecipeDisplay;

import java.util.List;
import java.util.Optional;

public class BackpackUpgradeRecipe extends SimpleSmithingRecipe {
    public static final MapCodec<BackpackUpgradeRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
                            Ingredient.CODEC.optionalFieldOf("template").forGetter(o -> o.template),
                            Ingredient.CODEC.fieldOf("base").forGetter(o -> o.base),
                            Ingredient.CODEC.optionalFieldOf("addition").forGetter(o -> o.addition),
                            ItemStackTemplate.CODEC.fieldOf("result").forGetter(o -> o.result)
                    )
                    .apply(i, BackpackUpgradeRecipe::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, BackpackUpgradeRecipe> STREAM_CODEC = StreamCodec.composite(
            CommonInfo.STREAM_CODEC,
            o -> o.commonInfo,
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
            o -> o.template,
            Ingredient.CONTENTS_STREAM_CODEC,
            o -> o.base,
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
            o -> o.addition,
            ItemStackTemplate.STREAM_CODEC,
            o -> o.result,
            BackpackUpgradeRecipe::new
    );
    public static final RecipeSerializer<BackpackUpgradeRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);
    private final Optional<Ingredient> template;
    private final Ingredient base;
    private final Optional<Ingredient> addition;
    private final ItemStackTemplate result;

    public BackpackUpgradeRecipe(CommonInfo commonInfo, Optional<Ingredient> template, Ingredient base, Optional<Ingredient> addition, ItemStackTemplate result) {
        super(commonInfo);
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput pInput) {
        ItemStack result = TransmuteRecipe.createWithOriginalComponents(this.result, pInput.base());
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
    public RecipeSerializer<BackpackUpgradeRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    protected PlacementInfo createPlacementInfo() {
        return PlacementInfo.createFromOptionals(List.of(this.template, Optional.of(this.base), this.addition));
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(
                new SmithingRecipeDisplay(
                        Ingredient.optionalIngredientToDisplay(this.template),
                        this.base.display(),
                        Ingredient.optionalIngredientToDisplay(this.addition),
                        new SlotDisplay.ItemStackSlotDisplay(this.result),
                        new SlotDisplay.ItemSlotDisplay(Items.SMITHING_TABLE)
                )
        );
    }
}