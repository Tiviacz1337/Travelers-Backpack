package com.tiviacz.travelersbackpack.common.recipes;

import com.google.gson.JsonObject;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.SleepingBagItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.RecipeUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.common.crafting.CraftingHelper;

import javax.annotation.Nullable;

public class ShapedBackpackRecipe extends ShapedRecipe
{
    public ShapedBackpackRecipe(ResourceLocation idIn, String groupIn, CraftingBookCategory category, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn, boolean showNotification)
    {
        super(idIn, groupIn, category, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn, showNotification);
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess access)
    {
        final ItemStack output = super.assemble(inv, access);

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

                if(!ingredient.isEmpty() && ingredient.getItem() instanceof SleepingBagItem)
                {
                    output.getOrCreateTag().putInt(ITravelersBackpackContainer.SLEEPING_BAG_COLOR, getProperColor((SleepingBagItem)ingredient.getItem()));
                }
            }
        }
        return output;
    }

    public static int getProperColor(SleepingBagItem item)
    {
        if(item.getBlock() instanceof SleepingBagBlock sleepingBag)
        {
            return sleepingBag.getColor().getId();
        }
        return DyeColor.RED.getId();
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
            CraftingBookCategory craftingbookcategory = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(json, "category", (String)null), CraftingBookCategory.MISC);
            final RecipeUtils.ShapedPrimer primer = RecipeUtils.parseShaped(json);
            final ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            boolean flag = GsonHelper.getAsBoolean(json, "show_notification", true);

            return new ShapedBackpackRecipe(recipeID, group, craftingbookcategory, primer.getRecipeWidth(), primer.getRecipeHeight(), primer.getIngredients(), result, flag);
        }

        @Nullable
        @Override
        public ShapedBackpackRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buffer)
        {
            final int width = buffer.readVarInt();
            final int height = buffer.readVarInt();
            final String group = buffer.readUtf(Short.MAX_VALUE);
            CraftingBookCategory craftingbookcategory = buffer.readEnum(CraftingBookCategory.class);
            final NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);

            for (int i = 0; i < ingredients.size(); ++i) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }

            final ItemStack result = buffer.readItem();
            boolean flag = buffer.readBoolean();

            return new ShapedBackpackRecipe(recipeID, group, craftingbookcategory, width, height, ingredients, result, flag);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapedBackpackRecipe recipe)
        {
            buffer.writeVarInt(recipe.getRecipeWidth());
            buffer.writeVarInt(recipe.getRecipeHeight());
            buffer.writeUtf(recipe.getGroup());
            buffer.writeEnum(recipe.category());

            for (final Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItemStack(recipe.result, false);
            buffer.writeBoolean(recipe.showNotification());
        }
    }
}