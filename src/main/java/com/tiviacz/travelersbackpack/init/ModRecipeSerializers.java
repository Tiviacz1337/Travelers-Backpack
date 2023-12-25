package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.recipes.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.common.recipes.BackpackUpgradeRecipe;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.common.recipes.ShapelessBackpackRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers
{
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, TravelersBackpack.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, ShapedBackpackRecipe.Serializer> BACKPACK_SHAPED = SERIALIZERS.register("backpack_shaped", () -> ShapedBackpackRecipe.Serializer.INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>, ShapelessBackpackRecipe.Serializer> BACKPACK_SHAPELESS = SERIALIZERS.register("backpack_shapeless", () -> ShapelessBackpackRecipe.Serializer.INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<BackpackDyeRecipe>> BACKPACK_DYE = SERIALIZERS.register("backpack_dye", () -> new SimpleCraftingRecipeSerializer<>(BackpackDyeRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, BackpackUpgradeRecipe.Serializer> BACKPACK_UPGRADE = SERIALIZERS.register("backpack_upgrade", BackpackUpgradeRecipe.Serializer::new);
}