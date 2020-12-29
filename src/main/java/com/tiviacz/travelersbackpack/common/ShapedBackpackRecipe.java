package com.tiviacz.travelersbackpack.common;

import com.google.gson.JsonObject;
import com.tiviacz.travelersbackpack.init.ModCrafting;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.RecipeUtils;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ShapedBackpackRecipe extends ShapedRecipe
{
    public ShapedBackpackRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn)
    {
        super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
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

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return ModCrafting.BACKPACK_SHAPED;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapedBackpackRecipe>
    {
        @Override
        public ShapedBackpackRecipe read(final ResourceLocation recipeID, final JsonObject json)
        {
            final String group = JSONUtils.getString(json, "group", "");
            final RecipeUtils.ShapedPrimer primer = RecipeUtils.parseShaped(json);
            final ItemStack result = CraftingHelper.getItemStack(JSONUtils.getJsonObject(json, "result"), true);

            return new ShapedBackpackRecipe(recipeID, group, primer.getRecipeWidth(), primer.getRecipeHeight(), primer.getIngredients(), result);
        }

        @Override
        public ShapedBackpackRecipe read(final ResourceLocation recipeID, final PacketBuffer buffer) {
            final int width = buffer.readVarInt();
            final int height = buffer.readVarInt();
            final String group = buffer.readString(Short.MAX_VALUE);
            final NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);

            for (int i = 0; i < ingredients.size(); ++i) {
                ingredients.set(i, Ingredient.read(buffer));
            }

            final ItemStack result = buffer.readItemStack();

            return new ShapedBackpackRecipe(recipeID, group, width, height, ingredients, result);
        }

        @Override
        public void write(final PacketBuffer buffer, final ShapedBackpackRecipe recipe) {
            buffer.writeVarInt(recipe.getRecipeWidth());
            buffer.writeVarInt(recipe.getRecipeHeight());
            buffer.writeString(recipe.getGroup());

            for (final Ingredient ingredient : recipe.getIngredients()) {
                ingredient.write(buffer);
            }

            buffer.writeItemStack(recipe.getRecipeOutput());
        }
    }
}
