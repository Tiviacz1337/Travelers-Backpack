package com.tiviacz.travelersbackpack.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModRecipeSerializers;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BackpackUpgradeRecipe extends SmithingTransformRecipe
{
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;

    public BackpackUpgradeRecipe(Ingredient pTemplate, Ingredient pBase, Ingredient pAddition, ItemStack pResult)
    {
        super(pTemplate, pBase, pAddition, pResult);

        this.template = pTemplate;
        this.base = pBase;
        this.addition = pAddition;
        this.result = pResult;
    }

    @Override
    public ItemStack assemble(Container p_267036_, RegistryAccess p_266699_)
    {
        ItemStack itemstack = this.result.copy();
        CompoundTag compoundtag = p_267036_.getItem(1).getTag();

        if(compoundtag != null)
        {
            compoundtag = compoundtag.copy();

            if(compoundtag.contains(Tiers.TIER))
            {
                Tiers.Tier tier = Tiers.of(compoundtag.getInt(Tiers.TIER));

                if(this.addition.test(Tiers.of(compoundtag.getInt(Tiers.TIER)).getTierUpgradeIngredient().getDefaultInstance()))
                {
                    compoundtag.putInt(Tiers.TIER, tier.getNextTier().getOrdinal());

                    if(compoundtag.contains("Inventory"))
                    {
                        if(compoundtag.getCompound("Inventory").contains("Size", Tag.TAG_INT))
                        {
                            compoundtag.getCompound("Inventory").putInt("Size", tier.getNextTier().getAllSlots());
                        }
                    }

                    itemstack.setTag(compoundtag.copy());
                    return itemstack;
                }
            }
            else
            {
                if(this.addition.test(Tiers.LEATHER.getTierUpgradeIngredient().getDefaultInstance()))
                {
                    compoundtag.putInt(Tiers.TIER, Tiers.LEATHER.getNextTier().getOrdinal());

                    if(compoundtag.contains("Inventory"))
                    {
                        if(compoundtag.getCompound("Inventory").contains("Size", Tag.TAG_INT))
                        {
                            compoundtag.getCompound("Inventory").putInt("Size", Tiers.LEATHER.getAllSlots());
                        }
                    }

                    itemstack.setTag(compoundtag.copy());
                    return itemstack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(Container p_266855_, Level p_266781_)
    {
        return TravelersBackpackConfig.enableTierUpgrades && super.matches(p_266855_, p_266781_);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModRecipeSerializers.BACKPACK_UPGRADE.get();
    }

    public static class Serializer implements RecipeSerializer<BackpackUpgradeRecipe> {
        private static final Codec<BackpackUpgradeRecipe> backpackUpgradeRecipeCodec = RecordCodecBuilder.create((p_301330_) -> {
            return p_301330_.group(Ingredient.CODEC.fieldOf("template").forGetter((p_297231_) -> {
                return p_297231_.template;
            }), Ingredient.CODEC.fieldOf("base").forGetter((p_298250_) -> {
                return p_298250_.base;
            }), Ingredient.CODEC.fieldOf("addition").forGetter((p_299654_) -> {
                return p_299654_.addition;
            }), ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter((p_297480_) -> {
                return p_297480_.result;
            })).apply(p_301330_, BackpackUpgradeRecipe::new);
        });

        @Override
        public Codec<BackpackUpgradeRecipe> codec() {
            return backpackUpgradeRecipeCodec;
        }

        @Override
        public @Nullable BackpackUpgradeRecipe fromNetwork(FriendlyByteBuf pBuffer) {
            Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            Ingredient ingredient1 = Ingredient.fromNetwork(pBuffer);
            Ingredient ingredient2 = Ingredient.fromNetwork(pBuffer);
            ItemStack itemstack = pBuffer.readItem();
            return new BackpackUpgradeRecipe(ingredient, ingredient1, ingredient2, itemstack);
        }

        public void toNetwork(FriendlyByteBuf p_266746_, BackpackUpgradeRecipe p_266927_) {
            p_266927_.template.toNetwork(p_266746_);
            p_266927_.base.toNetwork(p_266746_);
            p_266927_.addition.toNetwork(p_266746_);
            p_266746_.writeItem(p_266927_.result);
        }
    }
}