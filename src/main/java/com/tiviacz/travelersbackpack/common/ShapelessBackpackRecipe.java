package com.tiviacz.travelersbackpack.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.tiviacz.travelersbackpack.init.ModCrafting;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

import java.util.Iterator;
import java.util.Random;

public class ShapelessBackpackRecipe extends ShapelessRecipe
{
    private final String group;

    public ShapelessBackpackRecipe(Identifier id, String group, ItemStack output, DefaultedList<Ingredient> input)
    {
        super(id, group, output, input);

        this.group = group;
    }

    @Override
    public ItemStack craft(final CraftingInventory inv)
    {
        final ItemStack output = super.craft(inv);

        if(!output.isEmpty())
        {
            for(int i = 0; i < inv.size(); i++)
            {
                final ItemStack ingredient = inv.getStack(i);

                if(!ingredient.isEmpty() && ingredient.getItem() instanceof TravelersBackpackItem)
                {
                    final NbtCompound compound = ingredient.getNbt();
                    output.setNbt(compound);
                    break;
                }
            }
        }
        return output;
    }

    private ItemStack damageShears(final ItemStack stack)
    {
        final PlayerEntity craftingPlayer = null;//ForgeHooks.getCraftingPlayer();

        if(stack.damage(1, craftingPlayer == null ? new Random() : craftingPlayer.world.random, craftingPlayer instanceof ServerPlayerEntity ? (ServerPlayerEntity) craftingPlayer : null))
        {
            //ForgeEventFactory.onPlayerDestroyItem(craftingPlayer, stack, null);
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(CraftingInventory inventoryCrafting)
    {
        final DefaultedList<ItemStack> remainingItems = DefaultedList.ofSize(inventoryCrafting.size(), ItemStack.EMPTY);

        for(int i = 0; i < remainingItems.size(); ++i)
        {
            final ItemStack itemstack = inventoryCrafting.getStack(i);

            if(!itemstack.isEmpty() && itemstack.getItem() instanceof ShearsItem)
            {
                remainingItems.set(i, damageShears(itemstack.copy()));
            }
            else
            {
                remainingItems.set(i, itemstack);
            }
        }
        return remainingItems;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModCrafting.BACKPACK_SHAPELESS;
    }

    public static class Serializer implements RecipeSerializer<ShapelessBackpackRecipe>
    {
        public Serializer() { }

        public ShapelessBackpackRecipe read(Identifier recipeId, JsonObject json) {
            String s = JsonHelper.getString(json, "group", "");
            DefaultedList<Ingredient> nonnulllist = readIngredients(JsonHelper.getArray(json, "ingredients"));
            if (nonnulllist.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (nonnulllist.size() > 3 * 3) {
                throw new JsonParseException("Too many ingredients for shapeless recipe the max is " + 3 * 3);
            } else {
                ItemStack itemstack = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
                return new ShapelessBackpackRecipe(recipeId, s, itemstack, nonnulllist);
            }
        }

        private static DefaultedList<Ingredient> readIngredients(JsonArray ingredientArray) {
            DefaultedList<Ingredient> nonnulllist = DefaultedList.of();

            for(int i = 0; i < ingredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
                if (!ingredient.isEmpty()) {
                    nonnulllist.add(ingredient);
                }
            }

            return nonnulllist;
        }

        public ShapelessBackpackRecipe read(Identifier recipeId, PacketByteBuf buffer) {
            String s = buffer.readString(32767);
            int i = buffer.readVarInt();
            DefaultedList<Ingredient> nonnulllist = DefaultedList.ofSize(i, Ingredient.EMPTY);

            for(int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.fromPacket(buffer));
            }

            ItemStack itemstack = buffer.readItemStack();
            return new ShapelessBackpackRecipe(recipeId, s, itemstack, nonnulllist);
        }

        public void write(PacketByteBuf buffer, ShapelessBackpackRecipe recipe) {
            buffer.writeString(recipe.group);
            buffer.writeVarInt(recipe.getIngredients().size());
            Iterator var3 = recipe.getIngredients().iterator();

            while(var3.hasNext()) {
                Ingredient ingredient = (Ingredient)var3.next();
                ingredient.write(buffer);
            }

            buffer.writeItemStack(recipe.getOutput());
        }
    }
}
