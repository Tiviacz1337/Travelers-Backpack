package com.tiviacz.travelersbackpack.common;

import com.google.gson.JsonObject;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.RecipeUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;

import javax.annotation.Nullable;

public class ShapedBackpackRecipe extends ShapedRecipe
{
    public ShapedBackpackRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn)
    {
        super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
    }

    @Override
    public ItemStack assemble(CraftingContainer inv)
    {
        final ItemStack output = super.assemble(inv);

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

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    public static class Serializer implements RecipeSerializer<ShapedBackpackRecipe>
    {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public ShapedBackpackRecipe fromJson(ResourceLocation recipeID, JsonObject json)
        {
            final String group = GsonHelper.getAsString(json, "group", "");
            final RecipeUtils.ShapedPrimer primer = RecipeUtils.parseShaped(json);
            final ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);

            return new ShapedBackpackRecipe(recipeID, group, primer.getRecipeWidth(), primer.getRecipeHeight(), primer.getIngredients(), result);
        }

        @Nullable
        @Override
        public ShapedBackpackRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buffer)
        {
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
        public void toNetwork(FriendlyByteBuf buffer, ShapedBackpackRecipe recipe)
        {
            buffer.writeVarInt(recipe.getRecipeWidth());
            buffer.writeVarInt(recipe.getRecipeHeight());
            buffer.writeUtf(recipe.getGroup());

            for (final Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItemStack(recipe.getResultItem(), false);
        }
    }
}
