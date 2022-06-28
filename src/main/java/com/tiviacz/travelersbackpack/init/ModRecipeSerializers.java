package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.common.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.common.ShapelessBackpackRecipe;
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
}