package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.datagen.ModLootTableProvider;
import com.tiviacz.travelersbackpack.datagen.ModRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventHandler {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        boolean includeServer = event.includeServer();
        generator.addProvider(includeServer, new ModRecipeProvider(output));
        generator.addProvider(includeServer, ModLootTableProvider.create(output));
    }

    @SubscribeEvent
    public static void onModConfigLoad(final ModConfigEvent.Loading configEvent) {
        if(configEvent.getConfig().getSpec() == TravelersBackpackConfig.serverSpec) {
            TravelersBackpackConfig.SERVER.initializeLists();
        }
    }

    @SubscribeEvent
    public static void onModConfigReload(final ModConfigEvent.Reloading configEvent) {
        if(configEvent.getConfig().getSpec() == TravelersBackpackConfig.serverSpec) {
            TravelersBackpackConfig.SERVER.initializeLists();
        }
    }
}