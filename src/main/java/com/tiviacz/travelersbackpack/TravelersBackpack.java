package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.compat.trinkets.TravelersBackpackTrinketIntegration;
import com.tiviacz.travelersbackpack.compat.universalgraves.UniversalGravesCompat;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.handlers.*;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TravelersBackpack implements ModInitializer {
    public static final String MODID = "travelersbackpack";
    public static final Logger LOGGER = LogManager.getLogger();

    public static boolean accessoriesLoaded;
    public static boolean trinketsLoaded;
    public static boolean craftingTweaksLoaded;

    public static boolean toughasnailsLoaded;
    public static boolean comfortsLoaded;
    public static boolean universalGravesLoaded;

    public static boolean polymorphLoaded;

    @Override
    public void onInitialize() {
        TravelersBackpackConfig.register();
        ModCreativeTabs.registerItemGroup();
        ModBlocks.init();
        ModItems.init();
        ModBlockEntityTypes.init();
        ModBlockEntityTypes.initSidedStorage();
        ModMenuTypes.init();
        ModRecipeSerializers.init();
        ModNetwork.initServer();
        ModCommands.registerCommands();
        EntityItemHandler.registerListeners();
        LootHandler.registerListeners();
        TradeOffersHandler.init();
        RightClickHandler.registerListeners();
        SleepHandler.registerListener();
        registerDeathHandler();
        TravelersBackpackBlock.registerDispenserBehaviour();

        ModCreativeTabs.addItemGroup();

        TravelersBackpackItem.registerCauldronInteraction();

        accessoriesLoaded = FabricLoader.getInstance().isModLoaded("accessories");
        trinketsLoaded = FabricLoader.getInstance().isModLoaded("trinkets");
        craftingTweaksLoaded = FabricLoader.getInstance().isModLoaded("craftingtweaks");

        if(trinketsLoaded) TravelersBackpackTrinketIntegration.init();

        toughasnailsLoaded = FabricLoader.getInstance().isModLoaded("toughasnails");
        comfortsLoaded = FabricLoader.getInstance().isModLoaded("comforts");

        universalGravesLoaded = FabricLoader.getInstance().isModLoaded("universal-graves");
        if(universalGravesLoaded) UniversalGravesCompat.register();

        polymorphLoaded = FabricLoader.getInstance().isModLoaded("polymorph");

        EffectFluidRegistry.initEffects();
    }

    public void registerDeathHandler() {
        DeathHandler.registerListeners();
    }

    public static boolean enableIntegration() {
        return enableTrinkets();
    }

    public static boolean enableTrinkets() {
        return trinketsLoaded && TravelersBackpackConfig.getConfig().backpackSettings.trinketsIntegration;
    }

    public static boolean isAnyGraveModInstalled() {
        return TravelersBackpack.universalGravesLoaded;
    }
}