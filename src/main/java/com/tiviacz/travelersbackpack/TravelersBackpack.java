package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.capability.TravelersBackpackCapability;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("travelersbackpack")
public class TravelersBackpack
{
    public static final String MODID = "travelersbackpack";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final SimpleChannel NETWORK = ModNetwork.getNetworkChannel();

    public TravelersBackpack()
    {
        TravelersBackpackConfig.register(ModLoadingContext.get());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFinish);

        MinecraftForge.EVENT_BUS.register(this);

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        ModContainerTypes.CONTAINER_TYPES.register(modEventBus);

        //Fluid Effects
        EffectFluidRegistry.initEffects();
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        TravelersBackpackCapability.register();
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
        ModClientEventHandler.registerScreenFactory();
        ModClientEventHandler.bindTileEntityRenderer();
        ModClientEventHandler.registerKeybinding();
        ModClientEventHandler.addLayer();
    }

    private void onFinish(final FMLLoadCompleteEvent event)
    {
        ModItems.addBackpacksToList();
        ResourceUtils.createModelTextureLocations();
        ResourceUtils.createWearableTextureLocations();
    }
}