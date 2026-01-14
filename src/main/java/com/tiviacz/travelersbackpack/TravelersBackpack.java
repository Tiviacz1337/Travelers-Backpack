package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.advancements.ActionTypeTrigger;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.compat.craftingtweaks.CraftingTweaksCompat;
import com.tiviacz.travelersbackpack.compat.curios.TravelersBackpackCurio;
import com.tiviacz.travelersbackpack.compat.polymorph.PolymorphCompat;
import com.tiviacz.travelersbackpack.compat.trashslot.TrashSlotCompat;
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
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    public static boolean tetraLoaded;
    public static boolean toughasnailsLoaded;
    public static boolean comfortsLoaded;
    public static boolean endermanOverhaulLoaded;

    public static boolean jeiLoaded;
    public static boolean reiLoaded;
    public static boolean emiLoaded;
    public static boolean polymorphLoaded;
    public static boolean trashSlotLoaded;

    public TravelersBackpack() {
        ForgeMod.enableMilkFluid();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, TravelersBackpackConfig.serverSpec);
        readOldCommonConfig();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TravelersBackpackConfig.commonSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TravelersBackpackConfig.clientSpec);

        MinecraftForge.EVENT_BUS.register(this);
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::setup);
        eventBus.addListener(this::doClientStuff);

        ModItems.ITEMS.register(eventBus);
        ModItems.ENTITY_TYPES.register(eventBus);
        ModBlocks.BLOCKS.register(eventBus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(eventBus);
        ModMenuTypes.MENU_TYPES.register(eventBus);
        ModRecipeSerializers.SERIALIZERS.register(eventBus);
        ModFluids.FLUID_TYPES.register(eventBus);
        ModFluids.FLUIDS.register(eventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(eventBus);
        ModLootModifiers.LOOT_MODIFIER_SERIALIZERS.register(eventBus);

        curiosLoaded = ModList.get().isLoaded("curios");
        accessoriesLoaded = ModList.get().isLoaded("accessories");
        craftingTweaksLoaded = ModList.get().isLoaded("craftingtweaks");

        corpseLoaded = ModList.get().isLoaded("corpse");
        gravestoneLoaded = ModList.get().isLoaded("gravestone");

        tetraLoaded = ModList.get().isLoaded("tetra");
        toughasnailsLoaded = ModList.get().isLoaded("toughasnails");
        comfortsLoaded = ModList.get().isLoaded("comforts");
        endermanOverhaulLoaded = ModList.get().isLoaded("endermanoverhaul");

        jeiLoaded = ModList.get().isLoaded("jei");
        reiLoaded = ModList.get().isLoaded("roughlyenoughitems");
        emiLoaded = ModList.get().isLoaded("emi");
        polymorphLoaded = ModList.get().isLoaded("polymorph");
        trashSlotLoaded = ModList.get().isLoaded("trashslot");

        //Fetch supporters
        Supporters.fetchSupporters();
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModNetwork.registerNetworkChannel();
            TravelersBackpackBlock.registerDispenserBehaviour();
            EffectFluidRegistry.initEffects();
            TravelersBackpackItem.registerCauldronInteraction();
            ActionTypeTrigger.register();
            if(craftingTweaksLoaded) CraftingTweaksCompat.registerCraftingTweaksAddition();
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ModClientEventHandler.registerScreenFactories();
            ModClientEventHandler.registerItemModelProperties();
            if(craftingTweaksLoaded) CraftingTweaksCompat.registerCraftingTweaksAdditionClient();
            if(trashSlotLoaded) TrashSlotCompat.register();
        });
        if(curiosLoaded) TravelersBackpackCurio.registerCurioRenderer();
        if(polymorphLoaded) PolymorphCompat.registerWidget();
    }

    public static boolean enableIntegration() {
        return enableCurios();
    }

    public static boolean enableCurios() {
        return curiosLoaded && TravelersBackpackConfig.SERVER.backpackSettings.backSlotIntegration.get();
    }

    public static boolean isAnyGraveModInstalled() {
        return TravelersBackpack.corpseLoaded || TravelersBackpack.gravestoneLoaded;
    }

    /**
     * Patch to adapt with old setting to prevent automatic Curios integration if someone didn't use it.
     */

    public static void readOldCommonConfig() {
        Path path = FMLPaths.CONFIGDIR.get().resolve("travelersbackpack-common.toml");
        boolean isCommonBackpackSettingsSection = false;
        Boolean curiosIntegration = null;

        try(BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while((line = reader.readLine()) != null) {
                // Trim whitespace and ignore comments or empty lines
                line = line.trim();
                if(line.startsWith("#") || line.isEmpty()) {
                    continue;
                }

                // Check for the [common.backpackSettings] section
                if(line.startsWith("[") && line.endsWith("]")) {
                    isCommonBackpackSettingsSection = line.equalsIgnoreCase("[common.backpackSettings]");
                    continue;
                }

                // If inside the [common.backpackSettings] section, look for curiosIntegration
                if(isCommonBackpackSettingsSection && line.startsWith("curiosIntegration")) {
                    // Split the line into key and value
                    String[] parts = line.split("=", 2);
                    if(parts.length == 2) {
                        curiosIntegration = Boolean.parseBoolean(parts[1].trim());
                        break; // Stop reading after finding the value
                    }
                }
            }

            if(curiosIntegration != null) {
                System.out.println("curiosIntegration: " + curiosIntegration);
                generateDefaultConfig(curiosIntegration);
            } else {
                System.out.println("curiosIntegration not found in the [common.backpackSettings] section.");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateDefaultConfig(boolean curiosIntegration) {
        // Path to the TOML file to generate
        Path tomlFilePath = FMLPaths.GAMEDIR.get().resolve("defaultconfigs").resolve("travelersbackpack-server.toml");

        // Content of the TOML file
        String tomlContent = """
                #Server config settings
                [server]
                
                    [server.backpackSettings]
                        #If true, backpack can only be worn by placing it in Curios or Accessories 'Back' slot
                        #WARNING - Remember to TAKE OFF BACKPACK BEFORE enabling or disabling this integration!! - if not you'll lose your backpack
                        backSlotIntegration = false
                """;

        if(curiosIntegration) {
            tomlContent = """
                    #Server config settings
                    [server]
                    
                        [server.backpackSettings]
                            #If true, backpack can only be worn by placing it in Curios or Accessories 'Back' slot
                            #WARNING - Remember to TAKE OFF BACKPACK BEFORE enabling or disabling this integration!! - if not you'll lose your backpack
                            backSlotIntegration = true
                    """;
        }

        // Write the TOML content to the file
        try(BufferedWriter writer = Files.newBufferedWriter(tomlFilePath)) {
            writer.write(tomlContent);
            System.out.println("TOML file generated successfully: " + tomlFilePath.toAbsolutePath());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}