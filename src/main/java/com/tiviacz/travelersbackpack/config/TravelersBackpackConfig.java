package com.tiviacz.travelersbackpack.config;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TravelersBackpackConfig
{
    //COMMON
    public static boolean curiosIntegration;
    public static boolean backpackDeathPlace;
    public static boolean backpackForceDeathPlace;
    public static boolean enableSleepingBagSpawnPoint;
    public static boolean enableLoot;
    public static boolean toolSlotsAcceptSwords;
    public static boolean disableCrafting;
    public static boolean enableBackpackBlockWearable;
    public static boolean invulnerableBackpack;
    public static boolean enableBackpackAbilities;
    public static int tanksCapacity;

    //CLIENT
    public static boolean displayWarning;
    public static boolean enableToolCycling;
    public static boolean disableScrollWheel;
    public static boolean obtainTips;
    public static boolean renderTools;
    public static boolean renderBackpackWithElytra;
    public static boolean disableBackpackRender;

    //OVERLAY
    public static boolean enableOverlay;
    public static int offsetX;
    public static int offsetY;

    public static class Common
    {
        public final ForgeConfigSpec.BooleanValue curiosIntegration;
        public final ForgeConfigSpec.BooleanValue backpackDeathPlace;
        public final ForgeConfigSpec.BooleanValue backpackForceDeathPlace;
        public final ForgeConfigSpec.BooleanValue enableSleepingBagSpawnPoint;
        public final ForgeConfigSpec.BooleanValue enableLoot;
        public final ForgeConfigSpec.BooleanValue toolSlotsAcceptSwords;
        public final ForgeConfigSpec.BooleanValue disableCrafting;
        public final ForgeConfigSpec.BooleanValue enableBackpackBlockWearable;
        public final ForgeConfigSpec.BooleanValue invulnerableBackpack;
        public final ForgeConfigSpec.BooleanValue enableBackpackAbilities;
        public final ForgeConfigSpec.IntValue tanksCapacity;

        Common(final ForgeConfigSpec.Builder builder)
        {
            builder.comment("Common config settings")
                    .push("common");

            curiosIntegration = builder
                                            .comment("If true, backpack can only be worn by placing it in curios 'Back' slot", "WARNING - Remember to TAKE OFF BACKPACK BEFORE enabling or disabling this integration!! - if not you'll lose your backpack")
                                            .define("curiosIntegration", false);

            backpackDeathPlace = builder
                                            .comment("Places backpack at place where player died")
                                            .define("backpackDeathPlace", true);

            backpackForceDeathPlace = builder
                                            .comment("Places backpack at place where player died, replacing all blocks that are breakable and do not have inventory (backpackDeathPlace must be true in order to work)")
                                            .define("backpackForceDeathPlace", false);

            enableSleepingBagSpawnPoint = builder
                                            .define("enableSleepingBagSpawnPoint", false);

            enableLoot = builder
                                            .comment("Enables backpacks spawning in loot chests")
                                            .define("enableLoot", true);

            toolSlotsAcceptSwords = builder
                                            .define("toolSlotsAcceptSwords", true);

            disableCrafting = builder
                                            .define("disableCrafting", false);

            enableBackpackBlockWearable = builder
                                            .comment("Enables wearing backpack directly from ground")
                                            .define("enableBackpackBlockWearable", true);

            invulnerableBackpack = builder
                                            .comment("Backpack immune to any damage source (lava, fire), can't be destroyed, never disappears as floating item")
                                            .define("invulnerableBackpack", true);

            enableBackpackAbilities = builder
                                            .define("enableBackpackAbilities", true);

            tanksCapacity = builder
                                            .defineInRange("tanksCapacity", Reference.BASIC_TANK_CAPACITY, Reference.POTION, Integer.MAX_VALUE);

            builder.pop();
        }
    }

    public static class Client
    {
        public final ForgeConfigSpec.BooleanValue displayWarning;
        public final ForgeConfigSpec.BooleanValue enableToolCycling;
        public final ForgeConfigSpec.BooleanValue disableScrollWheel;
        public final ForgeConfigSpec.BooleanValue obtainTips;
        public final ForgeConfigSpec.BooleanValue renderTools;
        public final ForgeConfigSpec.BooleanValue renderBackpackWithElytra;
        public final ForgeConfigSpec.BooleanValue disableBackpackRender;
        public final Overlay overlay;

        Client(final ForgeConfigSpec.Builder builder)
        {
            builder.comment("Client-only settings")
                    .push("client");

            displayWarning              = builder
                                        .comment("Displays warning about item deletion after updating from older version")
                                        .define("displayWarning", true);

            enableToolCycling = builder
                                        .comment("Enables tool cycling via keybind (Default Z) + scroll combination, while backpack is worn")
                                        .define("enableToolCycling", true);

            disableScrollWheel = builder
                                        .comment("Allows tool cycling using keybinding only (Default Z)")
                                        .define("disableScrollWheel", false);

            obtainTips = builder
                                        .comment("Enables tip, how to obtain a backpack, if there's no crafting recipe for it")
                                        .define("obtainTips", true);

            renderTools = builder
                                        .comment("Render tools in tool slots on the backpack, while worn")
                                        .define("renderTools", true);

            renderBackpackWithElytra = builder
                                        .comment("Render backpack if elytra is present")
                                        .define("renderBackpackWithElytra", true);

            disableBackpackRender = builder
                                        .comment("Disable backpack rendering")
                                        .define("disableBackpackRender", false);

            overlay = new Overlay(
                                        builder,
                                        "The position of the Overlay on the screen",
                                        "overlay",
                                        true, 20, 30
            );

            builder.pop();
        }

        public static class Overlay
        {
            public final ForgeConfigSpec.BooleanValue enableOverlay;
            public final ForgeConfigSpec.IntValue offsetX;
            public final ForgeConfigSpec.IntValue offsetY;

            Overlay(final ForgeConfigSpec.Builder builder, final String comment, final String path, final boolean defaultOverlay, final int defaultX, final int defaultY)
            {
                builder.comment(comment)
                                .push(path);

                enableOverlay = builder
                                .comment("Enables tanks and tool slots overlay, while backpack is worn")
                                .define("enableOverlay", defaultOverlay);

                offsetX = builder
                                .comment("Offsets to left side")
                                .defineInRange("offsetX", defaultX, Integer.MIN_VALUE, Integer.MAX_VALUE);

                offsetY = builder
                                .comment("Offsets to up")
                                .defineInRange("offsetY", defaultY, Integer.MIN_VALUE, Integer.MAX_VALUE);

                builder.pop();
            }
        }
    }

    //COMMON
    private static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    //CLIENT
    private static final ForgeConfigSpec clientSpec;
    public static final Client CLIENT;

    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    //REGISTRY
    public static void register(final ModLoadingContext context)
    {
        context.registerConfig(ModConfig.Type.COMMON, commonSpec);
        context.registerConfig(ModConfig.Type.CLIENT, clientSpec);
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent.Loading configEvent)
    {
        if(configEvent.getConfig().getSpec() == TravelersBackpackConfig.commonSpec)
        {
            bakeCommonConfig();
        }
        if(configEvent.getConfig().getSpec() == TravelersBackpackConfig.clientSpec)
        {
            bakeClientConfig();
        }
    }

    public static void bakeCommonConfig()
    {
        curiosIntegration = COMMON.curiosIntegration.get();
        backpackDeathPlace = COMMON.backpackDeathPlace.get();
        backpackForceDeathPlace = COMMON.backpackForceDeathPlace.get();
        enableSleepingBagSpawnPoint = COMMON.enableSleepingBagSpawnPoint.get();
        enableLoot = COMMON.enableLoot.get();
        toolSlotsAcceptSwords = COMMON.toolSlotsAcceptSwords.get();
        enableBackpackBlockWearable = COMMON.enableBackpackBlockWearable.get();
        disableCrafting = COMMON.disableCrafting.get();
        invulnerableBackpack = COMMON.invulnerableBackpack.get();
        enableBackpackAbilities = COMMON.enableBackpackAbilities.get();
        tanksCapacity = COMMON.tanksCapacity.get();
    }

    public static void bakeClientConfig()
    {
        displayWarning = CLIENT.displayWarning.get();
        enableToolCycling = CLIENT.enableToolCycling.get();
        disableScrollWheel = CLIENT.disableScrollWheel.get();
        obtainTips = CLIENT.obtainTips.get();
        renderTools = CLIENT.renderTools.get();
        renderBackpackWithElytra = CLIENT.renderBackpackWithElytra.get();
        disableBackpackRender = CLIENT.disableBackpackRender.get();

        //OVERLAY
        enableOverlay = CLIENT.overlay.enableOverlay.get();
        offsetX = CLIENT.overlay.offsetX.get();
        offsetY = CLIENT.overlay.offsetY.get();
    }
}