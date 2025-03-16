package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.datagen.ModLootTableProvider;
import com.tiviacz.travelersbackpack.datagen.ModRecipeProvider;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.StorageAccessWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = TravelersBackpack.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandler {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        boolean includeServer = event.includeServer();
        generator.addProvider(includeServer, new ModRecipeProvider(output, event.getLookupProvider()));
        generator.addProvider(includeServer, ModLootTableProvider.create(output, event.getLookupProvider()));
    }

    @SubscribeEvent
    public static void registerPayloadHandler(RegisterPayloadHandlersEvent event) {
        ModNetwork.register(event.registrar(TravelersBackpack.MODID));
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        //Register block ItemHandler capability
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntityTypes.BACKPACK.get(), (blockEntity, side) -> {
            if(blockEntity.getWrapper() != BackpackWrapper.DUMMY) {
                return new StorageAccessWrapper(blockEntity.getWrapper(), blockEntity.getWrapper().getStorage());
            }
            return new ItemStackHandler(0);
        });

        //Register block FluidHandler capability
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntityTypes.BACKPACK.get(), (blockEntity, side) -> {
            Direction direction = blockEntity.getBlockDirection();
            if(blockEntity.getWrapper() != BackpackWrapper.DUMMY && blockEntity.getWrapper().getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                TanksUpgrade tanksUpgrade = blockEntity.getWrapper().getUpgradeManager().getUpgrade(TanksUpgrade.class).get();
                if(side == null) return tanksUpgrade.getLeftTank();

                if(direction == Direction.NORTH) {
                    switch(side) {
                        case WEST:
                            return tanksUpgrade.getRightTank();
                        case EAST:
                            return tanksUpgrade.getLeftTank();
                    }
                }
                if(direction == Direction.SOUTH) {
                    switch(side) {
                        case EAST:
                            return tanksUpgrade.getRightTank();
                        case WEST:
                            return tanksUpgrade.getLeftTank();
                    }
                }

                if(direction == Direction.EAST) {
                    switch(side) {
                        case NORTH:
                            return tanksUpgrade.getRightTank();
                        case SOUTH:
                            return tanksUpgrade.getLeftTank();
                    }
                }

                if(direction == Direction.WEST) {
                    switch(side) {
                        case SOUTH:
                            return tanksUpgrade.getRightTank();
                        case NORTH:
                            return tanksUpgrade.getLeftTank();
                    }
                }
                return tanksUpgrade.getLeftTank();
            }
            return new FluidTank(0);
        });
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