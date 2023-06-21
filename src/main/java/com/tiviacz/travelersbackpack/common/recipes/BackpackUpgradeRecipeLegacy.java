package com.tiviacz.travelersbackpack.common.recipes;

import com.google.gson.JsonObject;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModRecipeSerializers;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.LegacyUpgradeRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class BackpackUpgradeRecipeLegacy extends LegacyUpgradeRecipe
{
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;

    public BackpackUpgradeRecipeLegacy(ResourceLocation id, Ingredient base, Ingredient addition, ItemStack result)
    {
        super(id, base, addition, result);

        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess)
    {
        ItemStack itemstack = this.result.copy();
        CompoundTag compoundtag = container.getItem(0).getTag();
        if(compoundtag != null)
        {
            compoundtag = compoundtag.copy();

            if(compoundtag.contains(Tiers.TIER))
            {
                Tiers.Tier tier = Tiers.of(compoundtag.getInt(Tiers.TIER));

                if(this.addition.test(Tiers.of(compoundtag.getInt(Tiers.TIER)).getTierUpgradeIngredient().getDefaultInstance()))
                {
                    compoundtag.putInt(Tiers.TIER, tier.getNextTier().getOrdinal());
                    itemstack.setTag(compoundtag.copy());
                    return itemstack;
                }
            }
            else
            {
                if(this.addition.test(Tiers.LEATHER.getTierUpgradeIngredient().getDefaultInstance()))
                {
                    compoundtag.putInt(Tiers.TIER, Tiers.LEATHER.getNextTier().getOrdinal());
                    itemstack.setTag(compoundtag.copy());
                    return itemstack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(Container p_267029_, Level p_267244_)
    {
        return TravelersBackpackConfig.enableTierUpgrades && super.matches(p_267029_, p_267244_);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModRecipeSerializers.BACKPACK_UPGRADE_LEGACY.get();
    }

    public static class Serializer implements RecipeSerializer<BackpackUpgradeRecipeLegacy>
    {
        public BackpackUpgradeRecipeLegacy fromJson(ResourceLocation p_267011_, JsonObject p_267297_) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(p_267297_, "base"));
            Ingredient ingredient1 = Ingredient.fromJson(GsonHelper.getAsJsonObject(p_267297_, "addition"));
            ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(p_267297_, "result"));
            return new BackpackUpgradeRecipeLegacy(p_267011_, ingredient, ingredient1, itemstack);
        }

        public BackpackUpgradeRecipeLegacy fromNetwork(ResourceLocation p_266671_, FriendlyByteBuf p_266826_) {
            Ingredient ingredient = Ingredient.fromNetwork(p_266826_);
            Ingredient ingredient1 = Ingredient.fromNetwork(p_266826_);
            ItemStack itemstack = p_266826_.readItem();
            return new BackpackUpgradeRecipeLegacy(p_266671_, ingredient, ingredient1, itemstack);
        }

        public void toNetwork(FriendlyByteBuf p_266918_, BackpackUpgradeRecipeLegacy p_266728_) {
            p_266728_.base.toNetwork(p_266918_);
            p_266728_.addition.toNetwork(p_266918_);
            p_266918_.writeItem(p_266728_.result);
        }
    }
}
