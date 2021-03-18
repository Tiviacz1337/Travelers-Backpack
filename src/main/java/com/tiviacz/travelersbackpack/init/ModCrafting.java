package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.common.ShapelessBackpackRecipe;
import com.tiviacz.travelersbackpack.util.InjectionUtils;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(TravelersBackpack.MODID)
@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCrafting
{
    public static final IRecipeSerializer<ShapedBackpackRecipe> BACKPACK_SHAPED = InjectionUtils.Null();
    public static final IRecipeSerializer<ShapelessBackpackRecipe> BACKPACK_SHAPELESS = InjectionUtils.Null();

    @SubscribeEvent
    public static void register(final RegistryEvent.Register<IRecipeSerializer<?>> event)
    {
        event.getRegistry().registerAll(new ShapedBackpackRecipe.Serializer().setRegistryName(new ResourceLocation(TravelersBackpack.MODID, "backpack_shaped")));
        event.getRegistry().registerAll(new ShapelessBackpackRecipe.Serializer().setRegistryName(new ResourceLocation(TravelersBackpack.MODID, "backpack_shapeless")));
    }
}