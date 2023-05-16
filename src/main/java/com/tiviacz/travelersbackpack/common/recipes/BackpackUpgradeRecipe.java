package com.tiviacz.travelersbackpack.common.recipes;

import com.google.gson.JsonObject;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModRecipeSerializers;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.SmithingRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BackpackUpgradeRecipe extends SmithingRecipe
{
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;

    public BackpackUpgradeRecipe(ResourceLocation id, Ingredient base, Ingredient addition, ItemStack result)
    {
        super(id, base, addition, result);

        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public ItemStack assemble(IInventory inventory)
    {
        ItemStack itemstack = this.getResultItem().copy();
        CompoundNBT compoundtag = inventory.getItem(0).getTag();
        if(compoundtag != null)
        {
            compoundtag = compoundtag.copy();

            if(compoundtag.contains(Tiers.TIER))
            {
                Tiers.Tier tier = Tiers.of(compoundtag.getString(Tiers.TIER));

                if(this.addition.test(Tiers.of(compoundtag.getString(Tiers.TIER)).getTierUpgradeIngredient().getDefaultInstance()))
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
    public boolean matches(IInventory pInv, World pLevel)
    {
        return TravelersBackpackConfig.enableTierUpgrades && super.matches(pInv, pLevel);
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return ModRecipeSerializers.BACKPACK_UPGRADE.get();
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BackpackUpgradeRecipe>
    {
        public BackpackUpgradeRecipe fromJson(ResourceLocation id, JsonObject jsonObject)
        {
            Ingredient ingredient = Ingredient.fromJson(JSONUtils.getAsJsonObject(jsonObject, "base"));
            Ingredient ingredient1 = Ingredient.fromJson(JSONUtils.getAsJsonObject(jsonObject, "addition"));
            ItemStack itemstack = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(jsonObject, "result"));
            return new BackpackUpgradeRecipe(id, ingredient, ingredient1, itemstack);
        }

        public BackpackUpgradeRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            Ingredient ingredient1 = Ingredient.fromNetwork(buffer);
            ItemStack itemstack = buffer.readItem();
            return new BackpackUpgradeRecipe(id, ingredient, ingredient1, itemstack);
        }

        public void toNetwork(PacketBuffer buffer, BackpackUpgradeRecipe recipe) {
            recipe.base.toNetwork(buffer);
            recipe.addition.toNetwork(buffer);
            buffer.writeItem(recipe.result);
        }
    }
}