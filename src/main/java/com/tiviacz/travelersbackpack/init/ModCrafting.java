package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.common.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.common.ShapelessBackpackRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModCrafting
{
    public static RecipeSerializer<ShapelessBackpackRecipe> BACKPACK_SHAPELESS;
    public static SpecialRecipeSerializer<BackpackDyeRecipe> BACKPACK_DYE;
    public static RecipeSerializer<ShapedBackpackRecipe> BACKPACK_SHAPED;

    public static void init()
    {
        BACKPACK_SHAPELESS = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(TravelersBackpack.MODID, "backpack_shapeless"), new ShapelessBackpackRecipe.Serializer());
        BACKPACK_DYE = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(TravelersBackpack.MODID, "backpack_dye"), new SpecialRecipeSerializer<>(BackpackDyeRecipe::new));
        BACKPACK_SHAPED = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(TravelersBackpack.MODID, "backpack_shaped"), new ShapedBackpackRecipe.Serializer());
    }
}
