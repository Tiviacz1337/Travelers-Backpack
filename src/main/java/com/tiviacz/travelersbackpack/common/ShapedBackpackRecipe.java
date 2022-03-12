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

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return ModCrafting.BACKPACK_SHAPED;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapedBackpackRecipe>
    {
        @Override
        public ShapedBackpackRecipe fromJson(final ResourceLocation recipeID, final JsonObject json)
        {
            final String group = JSONUtils.getAsString(json, "group", "");
            final RecipeUtils.ShapedPrimer primer = RecipeUtils.parseShaped(json);
            final ItemStack result = CraftingHelper.getItemStack(JSONUtils.getAsJsonObject(json, "result"), true);

            return new ShapedBackpackRecipe(recipeID, group, primer.getRecipeWidth(), primer.getRecipeHeight(), primer.getIngredients(), result);
        }

        @Override
        public ShapedBackpackRecipe fromNetwork(final ResourceLocation recipeID, final PacketBuffer buffer) {
            final int width = buffer.readVarInt();
            final int height = buffer.readVarInt();
            final String group = buffer.readUtf(Short.MAX_VALUE);
            final NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);

            for (int i = 0; i < ingredients.size(); ++i) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }

            final ItemStack result = buffer.readItem();

            return new ShapedBackpackRecipe(recipeID, group, width, height, ingredients, result);
        }

        @Override
        public void toNetwork(final PacketBuffer buffer, final ShapedBackpackRecipe recipe) {
            buffer.writeVarInt(recipe.getRecipeWidth());
            buffer.writeVarInt(recipe.getRecipeHeight());
            buffer.writeUtf(recipe.getGroup());

            for (final Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.getResultItem());
        }
    }
}
