package com.tiviacz.travelersbackpack.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class RecipeUtils
{
    private static final Method DESERIALIZE_KEY = ObfuscationReflectionHelper.findMethod(ShapedRecipe.class, "func_192408_a" /* deserializeKey */, JsonObject.class);
    private static final Method SHRINK = ObfuscationReflectionHelper.findMethod(ShapedRecipe.class, "func_194134_a" /* shrink */, String[].class);
    private static final Method PATTERN_FROM_JSON = ObfuscationReflectionHelper.findMethod(ShapedRecipe.class, "func_192407_a" /* patternFromJson*/, JsonArray.class);
    private static final Method DESERIALIZE_INGREDIENTS = ObfuscationReflectionHelper.findMethod(ShapedRecipe.class, "func_192402_a" /* deserializeIngredients */, String[].class, Map.class, int.class, int.class);

    public static ShapedPrimer parseShaped(final JsonObject json)
    {
        try {
            @SuppressWarnings("unchecked")
            final Map<String, Ingredient> key = (Map<String, Ingredient>) DESERIALIZE_KEY.invoke(null, JSONUtils.getAsJsonObject(json, "key"));

            final String[] pattern = (String[]) SHRINK.invoke(null, PATTERN_FROM_JSON.invoke(null, JSONUtils.getAsJsonArray(json, "pattern")));

            final int recipeWidth = pattern[0].length();
            final int recipeHeight = pattern.length;

            @SuppressWarnings("unchecked")
            final NonNullList<Ingredient> ingredients = (NonNullList<Ingredient>) DESERIALIZE_INGREDIENTS.invoke(null, pattern, key, recipeWidth, recipeHeight);

            return new ShapedPrimer(ingredients, recipeWidth, recipeHeight);
        } catch (final IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to parse shaped recipe", e);
        }
    }

    public static class ShapedPrimer
    {
        private final NonNullList<Ingredient> ingredients;
        private final int recipeWidth;
        private final int recipeHeight;

        public ShapedPrimer(final NonNullList<Ingredient> ingredients, final int recipeWidth, final int recipeHeight) {
            this.ingredients = ingredients;
            this.recipeWidth = recipeWidth;
            this.recipeHeight = recipeHeight;
        }

        public NonNullList<Ingredient> getIngredients() {
            return ingredients;
        }

        public int getRecipeWidth() {
            return recipeWidth;
        }

        public int getRecipeHeight() {
            return recipeHeight;
        }
    }
}
