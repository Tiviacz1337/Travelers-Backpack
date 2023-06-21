package com.tiviacz.travelersbackpack.common.recipes;

import com.google.gson.JsonObject;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
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
import net.minecraft.world.World;

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
        ItemStack itemstack = this.result.copy();
        NbtCompound nbtCompound = inventory.getStack(0).getNbt();
        if(nbtCompound != null)
        {
            nbtCompound = nbtCompound.copy();

            if(nbtCompound.contains(Tiers.TIER))
            {
                Tiers.Tier tier = Tiers.of(nbtCompound.getInt(Tiers.TIER));

                if(this.addition.test(Tiers.of(nbtCompound.getInt(Tiers.TIER)).getTierUpgradeIngredient().getDefaultStack()))
                {
                    nbtCompound.putInt(Tiers.TIER, tier.getNextTier().getOrdinal());
                    itemstack.setNbt(nbtCompound.copy());
                    return itemstack;
                }
            }
            else
            {
                if(this.addition.test(Tiers.LEATHER.getTierUpgradeIngredient().getDefaultStack()))
                {
                    nbtCompound.putInt(Tiers.TIER, Tiers.LEATHER.getNextTier().getOrdinal());
                    itemstack.setNbt(nbtCompound.copy());
                    return itemstack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(Inventory inventory, World world)
    {
        return TravelersBackpackConfig.enableTierUpgrades && super.matches(inventory, world);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModCrafting.BACKPACK_UPGRADE;
    }

    public static class Serializer implements RecipeSerializer<BackpackUpgradeRecipe>
    {
        public BackpackUpgradeRecipe read(Identifier identifier, JsonObject jsonObject) {
            Ingredient ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "base"));
            Ingredient ingredient2 = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "addition"));
            ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
            return new BackpackUpgradeRecipe(identifier, ingredient, ingredient2, itemStack);
        }

        public BackpackUpgradeRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
            Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
            ItemStack itemStack = packetByteBuf.readItemStack();
            return new BackpackUpgradeRecipe(identifier, ingredient, ingredient2, itemStack);
        }

        public void write(PacketByteBuf packetByteBuf, BackpackUpgradeRecipe upgradeRecipe) {
            upgradeRecipe.base.write(packetByteBuf);
            upgradeRecipe.addition.write(packetByteBuf);
            packetByteBuf.writeItemStack(upgradeRecipe.result);
        }
    }
}