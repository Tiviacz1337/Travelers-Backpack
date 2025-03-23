package com.tiviacz.travelersbackpack;

import com.mojang.authlib.minecraft.client.ObjectMapper;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.compat.trinkets.TravelersBackpackTrinketIntegration;
import com.tiviacz.travelersbackpack.compat.universalgraves.UniversalGravesCompat;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.handlers.*;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Supporters;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

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
        //Patch
        readOldCommonConfig();

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
        TickHandler.register();

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

        //Fetch supporters
        Supporters.fetchSupporters();

        //Patch
        replaceNewConfigValue();
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

    /**
     * Patch to adapt with old setting to prevent automatic Trinkets integration if someone didn't use it.
     */

    private static boolean needReplacement = false;

    public static void readOldCommonConfig() {
        try {
            // Read the JSON5 file as a string
            Path path = FabricLoader.getInstance().getConfigDir().resolve("travelersbackpack.json5");
            String content = new String(Files.readAllBytes(path));

            // Remove comments (line comments and block comments)
            content = content.replaceAll("//.*?$", "");  // Remove single-line comments
            content = content.replaceAll("/\\*.*?\\*/", "");  // Remove block comments

            // Create an ObjectMapper
            ObjectMapper mapper = ObjectMapper.create();

            // Parse the cleaned content into a Map
            Map<String, Object> jsonMap = mapper.readValue(content, Map.class);

            //Invalid config - no need to patch
            if(jsonMap == null) {
                return;
            }

            //New config - no need to patch anymore
            if(jsonMap.containsKey("backpackUpgrades")) {
                return;
            }

            // Navigate to the trinketsIntegration setting
            Map<String, Object> backpackSettings = (Map<String, Object>)jsonMap.get("backpackSettings");
            Boolean trinketsIntegration = (Boolean)backpackSettings.get("trinketsIntegration");

            // Print the value
            if(!trinketsIntegration) {
                needReplacement = true;
            }
        } catch(IOException e) {}
    }

    public static void replaceNewConfigValue() {
        if(!needReplacement) return;
        try {
            // Get the file path using FabricLoader
            Path path = FabricLoader.getInstance().getConfigDir().resolve("travelersbackpack.json5");

            // Read the JSON5 file content as a string
            String content = new String(Files.readAllBytes(path));

            // Define the key-value pair to replace
            String searchKey = "\"trinketsIntegration\":";
            String newValue = "\"trinketsIntegration\": " + false;

            // Replace the value of trinketsIntegration while preserving other content
            String updatedContent = content.replaceFirst(searchKey + ".*?(,|\n)", newValue + "$1");

            // Write the modified content back to the file
            Files.write(path, updatedContent.getBytes());

            // Print the updated value
        } catch(IOException e) {}
    }
}