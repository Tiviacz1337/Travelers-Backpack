package com.tiviacz.travelersbackpack.common.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.tiviacz.travelersbackpack.init.ModRecipeSerializers;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.random.Random;

import java.util.Iterator;

public class ShapelessBackpackRecipe extends ShapelessRecipe
{
    private final String group;

    public ShapelessBackpackRecipe(Identifier id, String group, CraftingRecipeCategory category, ItemStack output, DefaultedList<Ingredient> input)
    {
        super(id, group, category, output, input);

        this.group = group;
    }

    @Override
    public ItemStack craft(final RecipeInputInventory inv, DynamicRegistryManager manager)
    {
        final ItemStack output = super.craft(inv, manager);

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

        if(stack.damage(1, craftingPlayer == null ? Random.create() : craftingPlayer.getWorld().random, craftingPlayer instanceof ServerPlayerEntity ? (ServerPlayerEntity) craftingPlayer : null))
        {
            //ForgeEventFactory.onPlayerDestroyItem(craftingPlayer, stack, null);
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventoryCrafting)
    {
        final DefaultedList<ItemStack> remainingItems = DefaultedList.ofSize(inventoryCrafting.size(), ItemStack.EMPTY);

        for(int i = 0; i < remainingItems.size(); ++i)
        {
            final ItemStack itemstack = inventoryCrafting.getStack(i);

            if(!itemstack.isEmpty() && itemstack.getItem() instanceof ShearsItem)
            {
                remainingItems.set(i, damageShears(itemstack.copy()));
            }
            if(itemstack.getItem().hasRecipeRemainder())
            {
                remainingItems.set(i, new ItemStack(itemstack.getItem().getRecipeRemainder()));
            }
        }
        return remainingItems;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModRecipeSerializers.BACKPACK_SHAPELESS;
    }

    public static class Serializer implements RecipeSerializer<ShapelessBackpackRecipe>
    {
        public Serializer() { }

        public ShapelessBackpackRecipe read(Identifier recipeId, JsonObject json) {
            String s = JsonHelper.getString(json, "group", "");
            CraftingRecipeCategory craftingRecipeCategory = (CraftingRecipeCategory)CraftingRecipeCategory.CODEC.byId(JsonHelper.getString(json, "category", (String)null), CraftingRecipeCategory.MISC);
            DefaultedList<Ingredient> nonnulllist = readIngredients(JsonHelper.getArray(json, "ingredients"));
            if (nonnulllist.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (nonnulllist.size() > 3 * 3) {
                throw new JsonParseException("Too many ingredients for shapeless recipe the max is " + 3 * 3);
            } else {
                ItemStack itemstack = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
                return new ShapelessBackpackRecipe(recipeId, s, craftingRecipeCategory, itemstack, nonnulllist);
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
            CraftingRecipeCategory craftingRecipeCategory = (CraftingRecipeCategory)buffer.readEnumConstant(CraftingRecipeCategory.class);
            int i = buffer.readVarInt();
            DefaultedList<Ingredient> nonnulllist = DefaultedList.ofSize(i, Ingredient.EMPTY);

            for(int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.fromPacket(buffer));
            }

            ItemStack itemstack = buffer.readItemStack();
            return new ShapelessBackpackRecipe(recipeId, s, craftingRecipeCategory, itemstack, nonnulllist);
        }

        public void write(PacketByteBuf buffer, ShapelessBackpackRecipe recipe) {
            buffer.writeString(recipe.group);
            buffer.writeEnumConstant(recipe.getCategory());
            buffer.writeVarInt(recipe.getIngredients().size());
            Iterator var3 = recipe.getIngredients().iterator();

            while(var3.hasNext()) {
                Ingredient ingredient = (Ingredient)var3.next();
                ingredient.write(buffer);
            }

            buffer.writeItemStack(recipe.output);
        }
    }
}