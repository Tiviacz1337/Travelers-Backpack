package com.tiviacz.travelersbackpack.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.tiviacz.travelersbackpack.init.ModCrafting;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Iterator;
import java.util.Random;

public class ShapelessBackpackRecipe extends ShapelessRecipe
{
    public ShapelessBackpackRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn)
    {
        super(idIn, groupIn, recipeOutputIn, recipeItemsIn);
    }

    @Override
    public ItemStack getCraftingResult(final CraftingInventory inv)
    {
        final ItemStack output = super.getCraftingResult(inv);

        if(!output.isEmpty())
        {
            for(int i = 0; i < inv.getSizeInventory(); i++)
            {
                final ItemStack ingredient = inv.getStackInSlot(i);

                if(!ingredient.isEmpty() && ingredient.getItem() instanceof TravelersBackpackItem)
                {
                    final CompoundNBT compound = ingredient.getTag();
                    output.setTag(compound);
                    break;
                }
            }
        }
        return output;
    }

    private ItemStack damageShears(final ItemStack stack)
    {
        final PlayerEntity craftingPlayer = ForgeHooks.getCraftingPlayer();

        if(stack.attemptDamageItem(1, craftingPlayer == null ? new Random() : craftingPlayer.world.rand, craftingPlayer instanceof ServerPlayerEntity ? (ServerPlayerEntity) craftingPlayer : null))
        {
            ForgeEventFactory.onPlayerDestroyItem(craftingPlayer, stack, null);
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(final CraftingInventory inventoryCrafting)
    {
        final NonNullList<ItemStack> remainingItems = NonNullList.withSize(inventoryCrafting.getSizeInventory(), ItemStack.EMPTY);

        for(int i = 0; i < remainingItems.size(); ++i)
        {
            final ItemStack itemstack = inventoryCrafting.getStackInSlot(i);

            if(!itemstack.isEmpty() && itemstack.getItem() instanceof ShearsItem)
            {
                remainingItems.set(i, damageShears(itemstack.copy()));
            }
            else
            {
                remainingItems.set(i, ForgeHooks.getContainerItem(itemstack));
            }
        }
        return remainingItems;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return ModCrafting.BACKPACK_SHAPELESS;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapelessBackpackRecipe>
    {
        public Serializer() { }

        public ShapelessBackpackRecipe read(ResourceLocation recipeId, JsonObject json) {
            String s = JSONUtils.getString(json, "group", "");
            NonNullList<Ingredient> nonnulllist = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
            if (nonnulllist.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (nonnulllist.size() > 3 * 3) {
                throw new JsonParseException("Too many ingredients for shapeless recipe the max is " + 3 * 3);
            } else {
                ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
                return new ShapelessBackpackRecipe(recipeId, s, itemstack, nonnulllist);
            }
        }

        private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for(int i = 0; i < ingredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.deserialize(ingredientArray.get(i));
                if (!ingredient.hasNoMatchingItems()) {
                    nonnulllist.add(ingredient);
                }
            }

            return nonnulllist;
        }

        public ShapelessBackpackRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            String s = buffer.readString(32767);
            int i = buffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

            for(int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.read(buffer));
            }

            ItemStack itemstack = buffer.readItemStack();
            return new ShapelessBackpackRecipe(recipeId, s, itemstack, nonnulllist);
        }

        public void write(PacketBuffer buffer, ShapelessBackpackRecipe recipe) {
            buffer.writeString(recipe.getGroup());
            buffer.writeVarInt(recipe.getIngredients().size());
            Iterator var3 = recipe.getIngredients().iterator();

            while(var3.hasNext()) {
                Ingredient ingredient = (Ingredient)var3.next();
                ingredient.write(buffer);
            }

            buffer.writeItemStack(recipe.getRecipeOutput());
        }
    }
}
