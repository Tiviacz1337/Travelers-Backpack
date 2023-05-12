package com.tiviacz.travelersbackpack.common.recipes;

import com.google.gson.JsonObject;
import com.tiviacz.travelersbackpack.init.ModCrafting;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class BackpackUpgradeRecipe extends SmithingRecipe
{
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;

    public BackpackUpgradeRecipe(Identifier id, Ingredient base, Ingredient addition, ItemStack result)
    {
        super(id, base, addition, result);

        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public ItemStack craft(Inventory inventory)
    {
        ItemStack itemstack = this.getOutput().copy();
        NbtCompound nbtCompound = inventory.getStack(0).getTag();
        if(nbtCompound != null)
        {
            nbtCompound = nbtCompound.copy();

            if(nbtCompound.contains(Tiers.TIER))
            {
                Tiers.Tier tier = Tiers.of(nbtCompound.getString(Tiers.TIER));

                if(this.addition.test(Tiers.of(nbtCompound.getString(Tiers.TIER)).getTierUpgradeIngredient().getDefaultStack()))
                {
                    nbtCompound.putString(Tiers.TIER, tier.getNextTier().getName());
                    itemstack.setTag(nbtCompound.copy());
                    return itemstack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModCrafting.BACKPACK_UPGRADE;
    }

    public static class Serializer implements RecipeSerializer<BackpackUpgradeRecipe>
    {
        @Override
        public BackpackUpgradeRecipe read(Identifier identifier, JsonObject jsonObject) {
            Ingredient ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "base"));
            Ingredient ingredient2 = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "addition"));
            ItemStack itemStack = ShapedRecipe.getItemStack(JsonHelper.getObject(jsonObject, "result"));
            return new BackpackUpgradeRecipe(identifier, ingredient, ingredient2, itemStack);
        }

        @Override
        public BackpackUpgradeRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
            Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
            ItemStack itemStack = packetByteBuf.readItemStack();
            return new BackpackUpgradeRecipe(identifier, ingredient, ingredient2, itemStack);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, BackpackUpgradeRecipe upgradeRecipe) {
            upgradeRecipe.base.write(packetByteBuf);
            upgradeRecipe.addition.write(packetByteBuf);
            packetByteBuf.writeItemStack(upgradeRecipe.result);
        }
    }
}