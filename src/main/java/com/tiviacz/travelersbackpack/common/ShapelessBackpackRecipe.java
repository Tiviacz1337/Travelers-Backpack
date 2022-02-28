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
    public ItemStack assemble(final CraftingInventory inv)
    {
        final ItemStack output = super.assemble(inv);

        if(!output.isEmpty())
        {
            for(int i = 0; i < inv.getContainerSize(); i++)
            {
                final ItemStack ingredient = inv.getItem(i);

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

        if(stack.hurt(1, craftingPlayer == null ? new Random() : craftingPlayer.level.getRandom(), craftingPlayer instanceof ServerPlayerEntity ? (ServerPlayerEntity) craftingPlayer : null))
        {
            ForgeEventFactory.onPlayerDestroyItem(craftingPlayer, stack, null);
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(final CraftingInventory inventoryCrafting)
    {
        final NonNullList<ItemStack> remainingItems = NonNullList.withSize(inventoryCrafting.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < remainingItems.size(); ++i)
        {
            final ItemStack itemstack = inventoryCrafting.getItem(i);

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

        private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for(int i = 0; i < ingredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
                if (!ingredient.isEmpty()) {
                    nonnulllist.add(ingredient);
                }
            }

            return nonnulllist;
        }
        @Override
        public ShapelessBackpackRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            String s = JSONUtils.getAsString(json, "group", "");
            NonNullList<Ingredient> nonnulllist = readIngredients(JSONUtils.getAsJsonArray(json, "ingredients"));
            if (nonnulllist.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (nonnulllist.size() > 3 * 3) {
                throw new JsonParseException("Too many ingredients for shapeless recipe the max is " + 3 * 3);
            } else {
                ItemStack itemstack = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
                return new ShapelessBackpackRecipe(recipeId, s, itemstack, nonnulllist);
            }
        }

        @Override
        public ShapelessBackpackRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            String s = buffer.readUtf(32767);
            int i = buffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

            for(int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.fromNetwork(buffer));
            }

            ItemStack itemstack = buffer.readItem();
            return new ShapelessBackpackRecipe(recipeId, s, itemstack, nonnulllist);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, ShapelessBackpackRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeVarInt(recipe.getIngredients().size());
            Iterator var3 = recipe.getIngredients().iterator();

            while(var3.hasNext()) {
                Ingredient ingredient = (Ingredient)var3.next();
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.getResultItem());
        }
    }
}
