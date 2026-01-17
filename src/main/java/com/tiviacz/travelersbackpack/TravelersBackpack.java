package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.compat.accessories.TravelersBackpackAccessory;
import com.tiviacz.travelersbackpack.compat.common.RecipeViewersNetwork;
import com.tiviacz.travelersbackpack.compat.craftingtweaks.CraftingTweaksCompat;
import com.tiviacz.travelersbackpack.compat.pneumonogravestones.PneumonoGravestonesCompat;
import com.tiviacz.travelersbackpack.compat.trinkets.TravelersBackpackTrinket;
import com.tiviacz.travelersbackpack.compat.universalgraves.UniversalGravesCompat;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.handlers.*;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Supporters;
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
    public static boolean pneumonoGravestonesLoaded;

    public static boolean polymorphLoaded;
    public static boolean trashSlotLoaded;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> currentServer = server);
        TravelersBackpackConfig.register();
        ModItemGroups.registerItemGroup();
        ModBlocks.init();
        ModItems.init();
        ModAttachmentTypes.init();
        ModBlockEntityTypes.init();
        ModBlockEntityTypes.initSidedStorage();
        ModDataComponents.init();
        ModScreenHandlerTypes.init();
        ModRecipeSerializers.init();
        ModNetwork.initServer();
        ModCommands.registerCommands();
        ModAdvancements.init();
        EntityItemHandler.registerListeners();
        LootHandler.registerListeners();
        TradeOffersHandler.init();
        RightClickHandler.registerListeners();
        SleepHandler.registerListener();
        registerDeathHandler();
        TravelersBackpackBlock.registerDispenserBehaviour();
        TickHandler.register();

        ModItemGroups.addItemGroup();

        TravelersBackpackItem.registerCauldronInteraction();

        FabricLoader fabricLoader = FabricLoader.getInstance();

        if(fabricLoader.isModLoaded("roughlyenoughitems") || fabricLoader.isModLoaded("jei") || fabricLoader.isModLoaded("emi")) {
            RecipeViewersNetwork.registerPackets();
        }

        accessoriesLoaded = fabricLoader.isModLoaded("accessories");
        trinketsLoaded = fabricLoader.isModLoaded("trinkets");
        craftingTweaksLoaded = fabricLoader.isModLoaded("craftingtweaks");
        if(craftingTweaksLoaded) CraftingTweaksCompat.registerCraftingTweaksAddition();

        if(accessoriesLoaded) TravelersBackpackAccessory.init();
        if(trinketsLoaded) TravelersBackpackTrinket.init();

        toughasnailsLoaded = fabricLoader.isModLoaded("toughasnails");
        comfortsLoaded = fabricLoader.isModLoaded("comforts");

        universalGravesLoaded = fabricLoader.isModLoaded("universal-graves");
        if(universalGravesLoaded) UniversalGravesCompat.register();

        pneumonoGravestonesLoaded = fabricLoader.isModLoaded("gravestones");
        if(pneumonoGravestonesLoaded) PneumonoGravestonesCompat.register();

        polymorphLoaded = fabricLoader.isModLoaded("polymorph");
        trashSlotLoaded = fabricLoader.isModLoaded("trashslot");

        //Fetch supporters
        Supporters.fetchSupporters();

        EffectFluidRegistry.initEffects();
    }

    public void registerDeathHandler() {
        DeathHandler.registerListeners();
    }

    @Nullable
    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }

    public static boolean enableIntegration() {
        return enableTrinkets() || enableAccessories();
    }

    public static boolean enableAccessories() {
        return accessoriesLoaded && TravelersBackpackConfig.getConfig().backpackSettings.backSlotIntegration;
    }

    public static boolean enableTrinkets() {
        return trinketsLoaded && TravelersBackpackConfig.getConfig().backpackSettings.backSlotIntegration;
    }

    public static boolean isAnyGraveModInstalled() {
        return TravelersBackpack.universalGravesLoaded || TravelersBackpack.pneumonoGravestonesLoaded;
    }
}