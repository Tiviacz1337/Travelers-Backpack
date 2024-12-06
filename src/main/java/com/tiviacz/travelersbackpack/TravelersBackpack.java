package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.compat.trinkets.TravelersBackpackTrinket;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.init.ModItemGroups;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpackneo.init.ModRecipeSerializers;
import com.tiviacz.travelersbackpackneo.init.ModScreenHandlerTypes;
import com.tiviacz.travelersbackpackold.compat.accessories.TravelersBackpackAccessory;
import com.tiviacz.travelersbackpackold.compat.craftingtweaks.TravelersBackpackCraftingGridProvider;
import com.tiviacz.travelersbackpackold.compat.universalgraves.UniversalGravesCompat;
import com.tiviacz.travelersbackpackold.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpackold.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpackold.handlers.*;
import com.tiviacz.travelersbackpackold.items.TravelersBackpackItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class TravelersBackpack implements ModInitializer {
    public static final String MODID = "travelersbackpack";
    public static final Logger LOGGER = LogManager.getLogger();
    private static MinecraftServer currentServer = null;

    public static boolean accessoriesLoaded;
    public static boolean trinketsLoaded;
    public static boolean craftingTweaksLoaded;

    public static boolean toughasnailsLoaded;
    public static boolean comfortsLoaded;
    public static boolean universalGravesLoaded;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> currentServer = server);
        TravelersBackpackConfig.register();
        ModItemGroups.registerItemGroup();
        ModBlocks.init();
        ModItems.init();
        ModBlockEntityTypes.init();
        ModBlockEntityTypes.initSidedStorage();
        ModDataComponents.init();
        ModScreenHandlerTypes.init();
        ModRecipeSerializers.init();
        ModNetwork.initServer();
        ModCommands.registerCommands();
        EntityItemHandler.registerListeners();
        LootHandler.registerListeners();
        TradeOffersHandler.init();
        RightClickHandler.registerListeners();
        SleepHandler.registerListener();

        ModItemGroups.addItemGroup();

        TravelersBackpackItem.registerCauldronBehavior();

        accessoriesLoaded = FabricLoader.getInstance().isModLoaded("accessories");
        trinketsLoaded = FabricLoader.getInstance().isModLoaded("trinkets");
        craftingTweaksLoaded = FabricLoader.getInstance().isModLoaded("craftingtweaks");

        if (craftingTweaksLoaded) new TravelersBackpackCraftingGridProvider();

        if (accessoriesLoaded) TravelersBackpackAccessory.init();
        if (trinketsLoaded && !accessoriesLoaded) TravelersBackpackTrinket.init();

        toughasnailsLoaded = FabricLoader.getInstance().isModLoaded("toughasnails");
        comfortsLoaded = FabricLoader.getInstance().isModLoaded("comforts");

        universalGravesLoaded = FabricLoader.getInstance().isModLoaded("universal-graves");
        if (universalGravesLoaded) UniversalGravesCompat.register();

        EffectFluidRegistry.initEffects();
    }

    @Nullable
    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }

    public static boolean enableIntegration() {
        return enableTrinkets() || enableAccessories();
    }

    public static boolean enableAccessories() {
        return accessoriesLoaded && TravelersBackpackConfig.getConfig().backpackSettings.accessoriesIntegration;
    }

    public static boolean enableTrinkets() {
        return trinketsLoaded && !enableAccessories() && TravelersBackpackConfig.getConfig().backpackSettings.trinketsIntegration;
    }

    public static boolean isAnyGraveModInstalled() {
        return TravelersBackpack.universalGravesLoaded;
    }
}