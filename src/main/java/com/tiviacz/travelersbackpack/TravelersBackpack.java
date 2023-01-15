package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

@Mod("travelersbackpack")
public class TravelersBackpack
{
    public static final String MODID = "travelersbackpack";
    public static final Logger LOGGER = LogManager.getLogger();
    public static SimpleChannel NETWORK;

    private static boolean curiosLoaded;

    public TravelersBackpack()
    {
        TravelersBackpackConfig.register(ModLoadingContext.get());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFinish);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onEnqueueIMC);

        MinecraftForge.EVENT_BUS.register(this);

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModRecipeSerializers.SERIALIZERS.register(modEventBus);
        ModFluids.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);

        curiosLoaded = ModList.get().isLoaded("curios");
    }

    private void onEnqueueIMC(InterModEnqueueEvent event)
    {
        if(!enableCurios()) return;
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BACK.getMessageBuilder().build());
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            ModNetwork.registerNetworkChannel();
            EffectFluidRegistry.initEffects();
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
        ModClientEventHandler.registerScreenFactory();
        ModClientEventHandler.bindTileEntityRenderer();
        ModClientEventHandler.registerItemModelProperty();
    }

    private void onFinish(final FMLLoadCompleteEvent event)
    {
        ModItems.addBackpacksToList();
        ResourceUtils.createTextureLocations();
        ResourceUtils.createSleepingBagTextureLocations();
    }

    public static boolean enableCurios()
    {
        return curiosLoaded && TravelersBackpackConfig.curiosIntegration;
    }
}