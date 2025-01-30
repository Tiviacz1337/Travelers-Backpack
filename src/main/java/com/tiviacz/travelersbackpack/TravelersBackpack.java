package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.compat.polymorph.PolymorphCompat;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Supporters;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("travelersbackpack")
public class TravelersBackpack {
    public static final String MODID = "travelersbackpack";
    public static final Logger LOGGER = LogManager.getLogger();
    public static SimpleChannel NETWORK;

    public static boolean curiosLoaded;
    public static boolean accessoriesLoaded;
    public static boolean craftingTweaksLoaded;

    public static boolean corpseLoaded;
    public static boolean gravestoneLoaded;

    public static boolean toughasnailsLoaded;
    public static boolean comfortsLoaded;
    public static boolean endermanOverhaulLoaded;

    public static boolean jeiLoaded;
    public static boolean polymorphLoaded;

    public TravelersBackpack(FMLJavaModLoadingContext context) {
        ForgeMod.enableMilkFluid();
        context.registerConfig(ModConfig.Type.SERVER, TravelersBackpackConfig.serverSpec);
        context.registerConfig(ModConfig.Type.COMMON, TravelersBackpackConfig.commonSpec);
        context.registerConfig(ModConfig.Type.CLIENT, TravelersBackpackConfig.clientSpec);

        MinecraftForge.EVENT_BUS.register(this);
        final IEventBus eventBus = context.getModEventBus();

        eventBus.addListener(this::setup);
        eventBus.addListener(this::doClientStuff);

        ModItems.ITEMS.register(eventBus);
        ModItems.ENTITY_TYPES.register(eventBus);
        ModBlocks.BLOCKS.register(eventBus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(eventBus);
        ModMenuTypes.MENU_TYPES.register(eventBus);
        ModRecipeSerializers.SERIALIZERS.register(eventBus);
        //ModFluids.FLUID_TYPES.register(eventBus);
        //ModFluids.FLUIDS.register(eventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(eventBus);
        ModLootModifiers.LOOT_MODIFIER_SERIALIZERS.register(eventBus);
        ModDataComponents.DATA_COMPONENT_TYPES.register(eventBus);

        curiosLoaded = ModList.get().isLoaded("curios");
        accessoriesLoaded = ModList.get().isLoaded("accessories");
        craftingTweaksLoaded = ModList.get().isLoaded("craftingtweaks");

        if(curiosLoaded && !accessoriesLoaded) loadCuriosCompat(eventBus);

        corpseLoaded = ModList.get().isLoaded("corpse");
        gravestoneLoaded = ModList.get().isLoaded("gravestone");

        toughasnailsLoaded = ModList.get().isLoaded("toughasnails");
        comfortsLoaded = ModList.get().isLoaded("comforts");
        endermanOverhaulLoaded = ModList.get().isLoaded("endermanoverhaul");

        jeiLoaded = ModList.get().isLoaded("jei");
        polymorphLoaded = ModList.get().isLoaded("polymorph");

        //Fetch supporters
        Supporters.fetchSupporters();
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModNetwork.registerNetworkChannel();
            TravelersBackpackBlock.registerDispenserBehaviour();
            EffectFluidRegistry.initEffects();
            enableCraftingTweaks();
            TravelersBackpackItem.registerCauldronInteraction();
            //if(accessoriesLoaded) TravelersBackpackAccessory.init();
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ModClientEventHandler.registerScreenFactories();
            ModClientEventHandler.registerBlockEntityRenderers();
            ModClientEventHandler.registerItemModelProperties();
        });
        if(polymorphLoaded) PolymorphCompat.registerWidget();

        // if(accessoriesLoaded) TravelersBackpackAccessory.initClient();
        // if(curiosLoaded && !accessoriesLoaded) TravelersBackpackCurio.registerCurioRenderer();
    }

    private static void loadCuriosCompat(IEventBus bus) {
        //bus.addListener(TravelersBackpackCurio::registerCurio);
    }

    public static boolean enableIntegration() {
        return enableCurios() || enableAccessories();
    }

    public static boolean enableCurios() {
        return curiosLoaded && !accessoriesLoaded && TravelersBackpackConfig.SERVER.backpackSettings.backSlotIntegration.get();
    }

    public static boolean enableAccessories() {
        return accessoriesLoaded && TravelersBackpackConfig.SERVER.backpackSettings.backSlotIntegration.get();
    }

    public static void enableCraftingTweaks() {
        if(craftingTweaksLoaded) {
            try {
                Class.forName("com.tiviacz.travelersbackpack.compat.craftingtweaks.TravelersBackpackCraftingGridProvider").getConstructor().newInstance();
            } catch(Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isAnyGraveModInstalled() {
        return TravelersBackpack.corpseLoaded || TravelersBackpack.gravestoneLoaded;
    }
}