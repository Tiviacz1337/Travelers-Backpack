package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.attachment.AttachmentUtils;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.compat.accessories.TravelersBackpackAccessory;
import com.tiviacz.travelersbackpack.compat.craftingtweaks.CraftingTweaksCompat;
import com.tiviacz.travelersbackpack.compat.pneumonogravestones.PneumonoGravestonesCompat;
import com.tiviacz.travelersbackpack.compat.trinkets.TravelersBackpackTrinket;
import com.tiviacz.travelersbackpack.compat.universalgraves.UniversalGravesCompat;
import com.tiviacz.travelersbackpack.compat.vinurl.VinURLNetwork;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.handlers.*;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Supporters;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.config.ModConfig;
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
    public static boolean vinurlLoaded;

    public static boolean polymorphLoaded;
    public static boolean trashSlotLoaded;
    public static boolean lambDynamicLightsLoaded;
    public static boolean mouseTweaksLoaded;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> currentServer = server);
        ConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.SERVER, TravelersBackpackConfig.serverSpec);
        ConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.COMMON, TravelersBackpackConfig.commonSpec);
        ModConfigEvents.loading(TravelersBackpack.MODID).register(config -> {
            if(config.getSpec() == TravelersBackpackConfig.serverSpec) {
                TravelersBackpackConfig.SERVER.reload();
            }
        });
        ModConfigEvents.reloading(TravelersBackpack.MODID).register(config -> {
            if(config.getSpec() == TravelersBackpackConfig.serverSpec) {
                TravelersBackpackConfig.SERVER.reload();
            }
        });
        ModItemGroups.registerItemGroup();
        ModBlocks.init();
        ModItems.init();
        ModItems.registerItemFluidStorage();
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

        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipeSerializers.BACKPACK_SHAPED);
        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipeSerializers.BACKPACK_UPGRADE);

        ModItemGroups.addItemGroup();

        TravelersBackpackItem.registerCauldronInteraction();

        FabricLoader fabricLoader = FabricLoader.getInstance();

        accessoriesLoaded = fabricLoader.isModLoaded("accessories");
        trinketsLoaded = fabricLoader.isModLoaded("trinkets");
        craftingTweaksLoaded = fabricLoader.isModLoaded("craftingtweaks");
        if(craftingTweaksLoaded) CraftingTweaksCompat.registerCraftingTweaksAddition();

        if(accessoriesLoaded) TravelersBackpackAccessory.init();
        if(trinketsLoaded && !accessoriesLoaded) TravelersBackpackTrinket.init();

        toughasnailsLoaded = fabricLoader.isModLoaded("toughasnails");
        comfortsLoaded = fabricLoader.isModLoaded("comforts");

        universalGravesLoaded = fabricLoader.isModLoaded("universal-graves");
        if(universalGravesLoaded) UniversalGravesCompat.register();

        pneumonoGravestonesLoaded = fabricLoader.isModLoaded("gravestones");
        if(pneumonoGravestonesLoaded) PneumonoGravestonesCompat.register();

        polymorphLoaded = fabricLoader.isModLoaded("polymorph");
        trashSlotLoaded = fabricLoader.isModLoaded("trashslot");
        vinurlLoaded = fabricLoader.isModLoaded("vinurl");
        if(vinurlLoaded) VinURLNetwork.register();

        lambDynamicLightsLoaded = fabricLoader.isModLoaded("lambdynlights");
        mouseTweaksLoaded = fabricLoader.isModLoaded("mousetweaks");


        //Fetch supporters
        Supporters.fetchSupporters();

        EffectFluidRegistry.initEffects();

        //Data Transfer
        AttachmentUtils.registerJoinEquip();
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
        return accessoriesLoaded && TravelersBackpackConfig.serverSpec.isLoaded() && TravelersBackpackConfig.SERVER.backpackSettings.backSlotIntegration.get();
    }

    public static boolean enableTrinkets() {
        return trinketsLoaded && !enableAccessories() && TravelersBackpackConfig.serverSpec.isLoaded() && TravelersBackpackConfig.SERVER.backpackSettings.backSlotIntegration.get();
    }

    public static boolean isAnyGraveModInstalled() {
        return TravelersBackpack.universalGravesLoaded || TravelersBackpack.pneumonoGravestonesLoaded;
    }
}