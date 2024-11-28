package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.recipes.BackpackUpgradeRecipe;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, TravelersBackpack.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> BACKPACK_SHAPED = SERIALIZERS.register("backpack_shaped", () -> ShapedBackpackRecipe.Serializer.INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> BACKPACK_UPGRADE = SERIALIZERS.register("backpack_upgrade", BackpackUpgradeRecipe.Serializer::new);
}