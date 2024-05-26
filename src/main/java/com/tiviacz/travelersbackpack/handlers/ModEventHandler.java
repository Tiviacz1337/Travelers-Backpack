package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.datagen.ModLootTableProvider;
import com.tiviacz.travelersbackpack.datagen.ModRecipeProvider;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventHandler
{
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        boolean includeServer = event.includeServer();

        generator.addProvider(includeServer, new ModRecipeProvider(output));
        generator.addProvider(includeServer, ModLootTableProvider.create(output));
    }

    @SubscribeEvent
    public static void registerPayloadHandler(RegisterPayloadHandlerEvent event)
    {
        ModNetwork.register(event.registrar(TravelersBackpack.MODID));
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        //Register block ItemHandler capability
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntityTypes.TRAVELERS_BACKPACK.get(), (blockEntity, side) -> blockEntity.getHandler());

        //Register block FluidHandler capability
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntityTypes.TRAVELERS_BACKPACK.get(), (blockEntity, side) ->
        {
            Direction direction = blockEntity.getBlockDirection(blockEntity);

            if(side == null) return blockEntity.getLeftTank();

            if(direction == Direction.NORTH)
            {
                switch(side)
                {
                    case WEST: return blockEntity.getRightTank();
                    case EAST: return blockEntity.getLeftTank();
                }
            }
            if(direction == Direction.SOUTH)
            {
                switch(side)
                {
                    case EAST: return blockEntity.getRightTank();
                    case WEST: return blockEntity.getLeftTank();
                }
            }

            if(direction == Direction.EAST)
            {
                switch(side)
                {
                    case NORTH: return blockEntity.getRightTank();
                    case SOUTH: return blockEntity.getLeftTank();
                }
            }

            if(direction == Direction.WEST)
            {
                switch(side)
                {
                    case SOUTH: return blockEntity.getRightTank();
                    case NORTH: return blockEntity.getLeftTank();
                }
            }
            return blockEntity.getLeftTank();
        });
    }

    @SubscribeEvent
    public static void onModConfigLoad(final ModConfigEvent.Loading configEvent)
    {
        if(configEvent.getConfig().getSpec() == TravelersBackpackConfig.serverSpec)
        {
            TravelersBackpackConfig.SERVER.initializeLists();
        }
    }

    @SubscribeEvent
    public static void onModConfigReload(final ModConfigEvent.Reloading configEvent)
    {
        if(configEvent.getConfig().getSpec() == TravelersBackpackConfig.serverSpec)
        {
            TravelersBackpackConfig.SERVER.initializeLists();
        }
    }
}