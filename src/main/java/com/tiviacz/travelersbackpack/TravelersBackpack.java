package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("travelersbackpack")
public class TravelersBackpack
{
    public static final String MODID = "travelersbackpack";
    public static final Logger LOGGER = LogManager.getLogger();
    public static SimpleChannel NETWORK;

    //private static boolean curiosLoaded;
    private static boolean craftingTweaksLoaded;

    public static boolean corpseLoaded;
    public static boolean gravestoneLoaded;

    public static boolean toughasnailsLoaded;
    public static boolean comfortsLoaded;
    public static boolean endermanOverhaulLoaded;

    public TravelersBackpack()
    {
        ForgeMod.enableMilkFluid();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, TravelersBackpackConfig.serverSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TravelersBackpackConfig.commonSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TravelersBackpackConfig.clientSpec);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModRecipeSerializers.SERIALIZERS.register(modEventBus);
        //ModFluids.FLUID_TYPES.register(modEventBus);
        //ModFluids.FLUIDS.register(modEventBus); //#TODO not possible / too much work
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModLootModifiers.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        ModDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);

        //curiosLoaded = ModList.get().isLoaded("curios");
        craftingTweaksLoaded = ModList.get().isLoaded("craftingtweaks");

        corpseLoaded = ModList.get().isLoaded("corpse");
        gravestoneLoaded = ModList.get().isLoaded("gravestone");

        toughasnailsLoaded = ModList.get().isLoaded("toughasnails");
        comfortsLoaded = ModList.get().isLoaded("comforts");
        endermanOverhaulLoaded = ModList.get().isLoaded("endermanoverhaul");
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            ModNetwork.registerNetworkChannel();
            EffectFluidRegistry.initEffects();
            enableCraftingTweaks();
            TravelersBackpackItem.registerCauldronInteraction();
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
        ModClientEventHandler.registerScreenFactories();
        ModClientEventHandler.registerBlockEntityRenderers();
        ModClientEventHandler.registerItemModelProperties();
    }

    public static boolean enableIntegration() {
        return false;
    }

    public static void enableCraftingTweaks()
    {
        if(craftingTweaksLoaded)
        {
            try {
                Class.forName("com.tiviacz.travelersbackpack.compat.craftingtweaks.TravelersBackpackCraftingGridProvider").getConstructor().newInstance();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isAnyGraveModInstalled()
    {
        return TravelersBackpack.corpseLoaded || TravelersBackpack.gravestoneLoaded;
    }
}