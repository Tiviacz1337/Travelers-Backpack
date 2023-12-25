package com.tiviacz.travelersbackpack.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.neoforged.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class RecipeUtils
{
    private static final Method DESERIALIZE_KEY = ObfuscationReflectionHelper.findMethod(ShapedRecipe.class, "m_44210_" /* keyFromJson */, JsonObject.class);
    private static final Method SHRINK = ObfuscationReflectionHelper.findMethod(ShapedRecipe.class, "m_44186_" /* shrink */, String[].class);
    private static final Method PATTERN_FROM_JSON = ObfuscationReflectionHelper.findMethod(ShapedRecipe.class, "m_44196_" /* patternFromJson*/, JsonArray.class);
    private static final Method DESERIALIZE_INGREDIENTS = ObfuscationReflectionHelper.findMethod(ShapedRecipe.class, "m_44202_" /* deserializeIngredients */, String[].class, Map.class, int.class, int.class);

    public static ShapedPrimer parseShaped(final JsonObject json)
    {
        try {
            @SuppressWarnings("unchecked")
            final Map<String, Ingredient> key = (Map<String, Ingredient>) DESERIALIZE_KEY.invoke(null, GsonHelper.getAsJsonObject(json, "key"));

            final String[] pattern = (String[]) SHRINK.invoke(null, PATTERN_FROM_JSON.invoke(null, GsonHelper.getAsJsonArray(json, "pattern")));

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