package com.tiviacz.travelersbackpack.common.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Iterator;

public class ShapelessBackpackRecipe extends ShapelessRecipe
{
    public ShapelessBackpackRecipe(ResourceLocation idIn, String groupIn, CraftingBookCategory category, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn)
    {
        super(idIn, groupIn, category, recipeOutputIn, recipeItemsIn);
    }

    @Override
    public ItemStack assemble(final CraftingContainer inv, RegistryAccess registryAccess)
    {
        final ItemStack output = super.assemble(inv, registryAccess);

        if(!output.isEmpty())
        {
            for(int i = 0; i < inv.getContainerSize(); i++)
            {
                final ItemStack ingredient = inv.getItem(i);

                if(!ingredient.isEmpty() && ingredient.getItem() instanceof TravelersBackpackItem)
                {
                    final CompoundTag compound = ingredient.getTag();
                    output.setTag(compound);
                    break;
                }
            }
        }
        return output;
    }

    private ItemStack damageShears(final ItemStack stack)
    {
        final Player craftingPlayer = ForgeHooks.getCraftingPlayer();

        if(stack.hurt(1, craftingPlayer == null ? RandomSource.create() : craftingPlayer.level().random, craftingPlayer instanceof ServerPlayer ? (ServerPlayer) craftingPlayer : null))
        {
            ForgeEventFactory.onPlayerDestroyItem(craftingPlayer, stack, null);
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(final CraftingContainer inventoryCrafting)
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
                remainingItems.set(i, ForgeHooks.getCraftingRemainingItem(itemstack));
            }
        }
        return remainingItems;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    public static class Serializer implements RecipeSerializer<ShapelessBackpackRecipe>
    {
        public static final Serializer INSTANCE = new Serializer();

        private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray)
        {
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
        public ShapelessBackpackRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            String s = GsonHelper.getAsString(json, "group", "");
            CraftingBookCategory craftingbookcategory = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(json, "category", (String)null), CraftingBookCategory.MISC);
            NonNullList<Ingredient> nonnulllist = readIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (nonnulllist.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (nonnulllist.size() > 3 * 3) {
                throw new JsonParseException("Too many ingredients for shapeless recipe the max is " + 3 * 3);
            } else {
                ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
                return new ShapelessBackpackRecipe(recipeId, s, craftingbookcategory, itemstack, nonnulllist);
            }
        }

        @Nullable
        @Override
        public ShapelessBackpackRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            String s = buffer.readUtf(32767);
            CraftingBookCategory craftingbookcategory = buffer.readEnum(CraftingBookCategory.class);
            int i = buffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

            for(int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.fromNetwork(buffer));
            }

            ItemStack itemstack = buffer.readItem();
            return new ShapelessBackpackRecipe(recipeId, s, craftingbookcategory, itemstack, nonnulllist);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapelessBackpackRecipe recipe)
        {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeEnum(recipe.category());
            buffer.writeVarInt(recipe.getIngredients().size());
            Iterator var3 = recipe.getIngredients().iterator();

            while(var3.hasNext()) {
                Ingredient ingredient = (Ingredient)var3.next();
                ingredient.toNetwork(buffer);
            }

            buffer.writeItemStack(recipe.result, false);
        }
    }
}