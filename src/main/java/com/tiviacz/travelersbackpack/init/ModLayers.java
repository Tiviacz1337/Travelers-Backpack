package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.HudOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModLayers {
    private static final Field LAYERS = ObfuscationReflectionHelper.findField(Gui.class, "layers");

    @SubscribeEvent
    public static void registerLayers(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            final var minecraft = Minecraft.getInstance();
            final var gui = minecraft.gui;

            try {
                final var layers = (LayeredDraw)LAYERS.get(gui);
                registerLayers(layers);
            } catch(final IllegalAccessException e) {
                throw new RuntimeException("Failed to get Gui layers", e);
            }
        });
    }

    private static void registerLayers(LayeredDraw layers) {
        layers.add(new HudOverlay());
    }
}
