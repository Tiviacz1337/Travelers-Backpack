package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.compat.curios.TravelersBackpackCurios;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("travelersbackpack")
public class TravelersBackpack
{
    public static final String MODID = "travelersbackpack";
    public static final Logger LOGGER = LogManager.getLogger();

    private static boolean curiosLoaded;
    private static boolean craftingTweaksLoaded;

    public static boolean corpseLoaded;
    public static boolean gravestoneLoaded;

    public static boolean comfortsLoaded;
    public static boolean endermanOverhaulLoaded;

    public TravelersBackpack(IEventBus eventBus)
    {
        NeoForgeMod.enableMilkFluid();

        TravelersBackpackConfig.register(ModLoadingContext.get());

        eventBus.addListener(this::setup);
        eventBus.addListener(this::doClientStuff);
        eventBus.addListener(this::onFinish);

        ModItems.ITEMS.register(eventBus);
        ModBlocks.BLOCKS.register(eventBus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(eventBus);
        ModMenuTypes.MENU_TYPES.register(eventBus);
        ModRecipeSerializers.SERIALIZERS.register(eventBus);
        ModFluids.FLUID_TYPES.register(eventBus);
        ModFluids.FLUIDS.register(eventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(eventBus);
        ModLootModifiers.LOOT_MODIFIER_SERIALIZERS.register(eventBus);
        ModLootConditions.LOOT_CONDITIONS.register(eventBus);
        ModAttachmentTypes.ATTACHMENT_TYPES.register(eventBus);

        curiosLoaded = ModList.get().isLoaded("curios");
        if(curiosLoaded) loadCuriosCompat(eventBus);

        craftingTweaksLoaded = ModList.get().isLoaded("craftingtweaks");

        corpseLoaded = ModList.get().isLoaded("corpse");
        gravestoneLoaded = ModList.get().isLoaded("gravestone");

        comfortsLoaded = ModList.get().isLoaded("comforts");
        endermanOverhaulLoaded = ModList.get().isLoaded("endermanoverhaul");
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            EffectFluidRegistry.initEffects();
            enableCraftingTweaks();
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
        ModClientEventHandler.bindTileEntityRenderer();
        ModClientEventHandler.registerItemModelProperty();
    }

    private void onFinish(final FMLLoadCompleteEvent event)
    {
        ModItems.addBackpacksToList();
        ResourceUtils.createTextureLocations();
        ResourceUtils.createSleepingBagTextureLocations();
    }

    private static void loadCuriosCompat(IEventBus bus)
    {
        bus.addListener(TravelersBackpackCurios::registerCurio);
    }

    public static boolean enableCurios()
    {
        return curiosLoaded && TravelersBackpackConfig.SERVER.backpackSettings.curiosIntegration.get();
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