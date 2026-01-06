package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.compat.common.RecipeViewersNetwork;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.datagen.ModItemTagsProvider;
import com.tiviacz.travelersbackpack.datagen.ModLootTableProvider;
import com.tiviacz.travelersbackpack.datagen.ModRecipeProvider;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.transfer.FluidTankWrapper;
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
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

@EventBusSubscriber(modid = TravelersBackpack.MODID)
public class ModEventHandler {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        generator.addProvider(true, new ModItemTagsProvider(output, event.getLookupProvider()));
        generator.addProvider(true, new ModRecipeProvider.Runner(output, event.getLookupProvider()));
        generator.addProvider(true, ModLootTableProvider.create(output, event.getLookupProvider()));
    }

    @SubscribeEvent
    public static void registerPayloadHandler(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar payloadRegistrar = event.registrar(TravelersBackpack.MODID);
        ModNetwork.register(payloadRegistrar);
        if(TravelersBackpack.jeiLoaded || TravelersBackpack.reiLoaded || TravelersBackpack.emiLoaded) {
            RecipeViewersNetwork.registerPackets(payloadRegistrar);
        }
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        //Register block ItemHandler capability
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntityTypes.BACKPACK.get(), (blockEntity, side) -> {
            if(blockEntity.getWrapper() != BackpackWrapper.DUMMY) {
                return blockEntity.getWrapper().getStorageForInputOutput();
            }
            return new ItemStacksResourceHandler(0);
        });

        //Register block FluidHandler capability
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntityTypes.BACKPACK.get(), (blockEntity, side) -> {
            Direction direction = blockEntity.getBlockDirection();
            if(blockEntity.getWrapper() != BackpackWrapper.DUMMY && blockEntity.getWrapper().getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                TanksUpgrade tanksUpgrade = blockEntity.getWrapper().getUpgradeManager().getUpgrade(TanksUpgrade.class).get();
                if(side == null) return new FluidTankWrapper(tanksUpgrade.getLeftTank());

                if(direction == Direction.NORTH) {
                    switch(side) {
                        case WEST:
                            return new FluidTankWrapper(tanksUpgrade.getRightTank());
                        case EAST:
                            return new FluidTankWrapper(tanksUpgrade.getLeftTank());
                    }
                }
                if(direction == Direction.SOUTH) {
                    switch(side) {
                        case EAST:
                            return new FluidTankWrapper(tanksUpgrade.getRightTank());
                        case WEST:
                            return new FluidTankWrapper(tanksUpgrade.getLeftTank());
                    }
                }

                if(direction == Direction.EAST) {
                    switch(side) {
                        case NORTH:
                            return new FluidTankWrapper(tanksUpgrade.getRightTank());
                        case SOUTH:
                            return new FluidTankWrapper(tanksUpgrade.getLeftTank());
                    }
                }

                if(direction == Direction.WEST) {
                    switch(side) {
                        case SOUTH:
                            return new FluidTankWrapper(tanksUpgrade.getRightTank());
                        case NORTH:
                            return new FluidTankWrapper(tanksUpgrade.getLeftTank());
                    }
                }
                return new FluidTankWrapper(tanksUpgrade.getLeftTank());
            }
            return new FluidStacksResourceHandler(0, 0);
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