package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.recipes.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.common.recipes.BackpackUpgradeRecipe;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.common.recipes.ShapelessBackpackRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipeSerializers
{
    public static RecipeSerializer<ShapelessBackpackRecipe> BACKPACK_SHAPELESS;
    public static SpecialRecipeSerializer<BackpackDyeRecipe> BACKPACK_DYE;
    public static RecipeSerializer<ShapedBackpackRecipe> BACKPACK_SHAPED;
    public static RecipeSerializer<BackpackUpgradeRecipe> BACKPACK_UPGRADE;

    public static void init()
    {
        BACKPACK_SHAPELESS = Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(TravelersBackpack.MODID, "backpack_shapeless"), new ShapelessBackpackRecipe.Serializer());
        BACKPACK_DYE = Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(TravelersBackpack.MODID, "backpack_dye"), new SpecialRecipeSerializer<>(BackpackDyeRecipe::new));
        BACKPACK_SHAPED = Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(TravelersBackpack.MODID, "backpack_shaped"), new ShapedBackpackRecipe.Serializer());
        BACKPACK_UPGRADE = Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(TravelersBackpack.MODID, "backpack_upgrade"), new BackpackUpgradeRecipe.Serializer());
    }
}