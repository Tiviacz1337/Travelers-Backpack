package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.common.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.common.ShapelessBackpackRecipe;
import com.tiviacz.travelersbackpack.util.InjectionUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(TravelersBackpack.MODID)
@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCrafting
{
    public static final RecipeSerializer<ShapedBackpackRecipe> BACKPACK_SHAPED = InjectionUtils.Null();
    public static final RecipeSerializer<ShapelessBackpackRecipe> BACKPACK_SHAPELESS = InjectionUtils.Null();
    public static final SimpleRecipeSerializer<BackpackDyeRecipe> BACKPACK_DYE = InjectionUtils.Null();

    @SubscribeEvent
    public static void register(final RegistryEvent.Register<RecipeSerializer<?>> event)
    {
        event.getRegistry().registerAll(new ShapedBackpackRecipe.Serializer().setRegistryName(new ResourceLocation(TravelersBackpack.MODID, "backpack_shaped")));
        event.getRegistry().registerAll(new ShapelessBackpackRecipe.Serializer().setRegistryName(new ResourceLocation(TravelersBackpack.MODID, "backpack_shapeless")));
        event.getRegistry().registerAll(new SimpleRecipeSerializer<>(BackpackDyeRecipe::new).setRegistryName(new ResourceLocation(TravelersBackpack.MODID, "backpack_dye")));
    }
}