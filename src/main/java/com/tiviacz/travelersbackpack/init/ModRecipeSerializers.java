package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.recipes.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.common.recipes.BackpackUpgradeRecipe;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.common.recipes.ShapelessBackpackRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipeSerializers
{
    public static final DeferredRegister<IRecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TravelersBackpack.MODID);

    public static final RegistryObject<IRecipeSerializer<?>> BACKPACK_SHAPED = SERIALIZERS.register("backpack_shaped", () -> ShapedBackpackRecipe.Serializer.INSTANCE);
    public static final RegistryObject<IRecipeSerializer<?>> BACKPACK_SHAPELESS = SERIALIZERS.register("backpack_shapeless", () -> ShapelessBackpackRecipe.Serializer.INSTANCE);
    public static final RegistryObject<SpecialRecipeSerializer<?>> BACKPACK_DYE = SERIALIZERS.register("backpack_dye", () -> new SpecialRecipeSerializer<>(BackpackDyeRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> BACKPACK_UPGRADE = SERIALIZERS.register("backpack_upgrade", BackpackUpgradeRecipe.Serializer::new);
}