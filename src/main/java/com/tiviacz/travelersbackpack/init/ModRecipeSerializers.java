package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.recipes.BackpackUpgradeRecipe;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipeSerializers {
    public static RecipeSerializer<ShapedBackpackRecipe> BACKPACK_SHAPED;
    public static RecipeSerializer<BackpackUpgradeRecipe> BACKPACK_UPGRADE;

    public static void init() {
        BACKPACK_SHAPED = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_shaped"), new ShapedBackpackRecipe.Serializer());
        BACKPACK_UPGRADE = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_upgrade"), new BackpackUpgradeRecipe.Serializer());
    }
}