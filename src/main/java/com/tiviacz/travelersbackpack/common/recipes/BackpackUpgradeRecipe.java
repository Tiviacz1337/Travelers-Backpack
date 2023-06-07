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
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.Level;

public class BackpackUpgradeRecipe extends SmithingTransformRecipe
{
    final Ingredient f_265949_;
    final Ingredient f_265888_;
    final Ingredient f_265907_;
    final ItemStack f_266098_;

    public BackpackUpgradeRecipe(ResourceLocation p_267143_, Ingredient p_266750_, Ingredient p_266787_, Ingredient p_267292_, ItemStack p_267031_)
    {
        super(p_267143_, p_266750_, p_266787_, p_267292_, p_267031_);

        this.f_265949_ = p_266750_;
        this.f_265888_ = p_266787_;
        this.f_265907_ = p_267292_;
        this.f_266098_ = p_267031_;
    }

    @Override
    public ItemStack assemble(Container p_267036_, RegistryAccess p_266699_)
    {
        ItemStack itemstack = this.f_266098_.copy();
        CompoundTag compoundtag = p_267036_.getItem(1).getTag();

        if(compoundtag != null)
        {
            compoundtag = compoundtag.copy();

            if(compoundtag.contains(Tiers.TIER))
            {
                Tiers.Tier tier = Tiers.of(compoundtag.getString(Tiers.TIER));

                if(this.f_265907_.test(Tiers.of(compoundtag.getString(Tiers.TIER)).getTierUpgradeIngredient().getDefaultInstance()))
                {
                    compoundtag.putString(Tiers.TIER, tier.getNextTier().getName());
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
        public BackpackUpgradeRecipe fromJson(ResourceLocation p_266953_, JsonObject p_266720_) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(p_266720_, "template"));
            Ingredient ingredient1 = Ingredient.fromJson(GsonHelper.getAsJsonObject(p_266720_, "base"));
            Ingredient ingredient2 = Ingredient.fromJson(GsonHelper.getAsJsonObject(p_266720_, "addition"));
            ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(p_266720_, "result"));
            return new BackpackUpgradeRecipe(p_266953_, ingredient, ingredient1, ingredient2, itemstack);
        }

        public BackpackUpgradeRecipe fromNetwork(ResourceLocation p_267117_, FriendlyByteBuf p_267316_) {
            Ingredient ingredient = Ingredient.fromNetwork(p_267316_);
            Ingredient ingredient1 = Ingredient.fromNetwork(p_267316_);
            Ingredient ingredient2 = Ingredient.fromNetwork(p_267316_);
            ItemStack itemstack = p_267316_.readItem();
            return new BackpackUpgradeRecipe(p_267117_, ingredient, ingredient1, ingredient2, itemstack);
        }

        public void toNetwork(FriendlyByteBuf p_266746_, BackpackUpgradeRecipe p_266927_) {
            p_266927_.f_265949_.toNetwork(p_266746_);
            p_266927_.f_265888_.toNetwork(p_266746_);
            p_266927_.f_265907_.toNetwork(p_266746_);
            p_266746_.writeItem(p_266927_.f_266098_);
        }
    }
}