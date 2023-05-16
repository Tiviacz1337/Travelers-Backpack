package com.tiviacz.travelersbackpack.config;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.datagen.ModRecipeProvider;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TravelersBackpackConfig
{
    //Backpack Settings
    public static boolean enableTierUpgrades;
    public static boolean disableCrafting;
    public static boolean enableBackpackBlockWearable;
    public static boolean invulnerableBackpack;
    public static boolean toolSlotsAcceptSwords;
    public static List<? extends String> toolSlotsAcceptableItems;
    public static List<? extends String> blacklistedItems;
    public static boolean allowShulkerBoxes;
    public static List<? extends Integer> tanksCapacity;
    public static boolean voidProtection;
    public static boolean backpackDeathPlace;
    public static boolean backpackForceDeathPlace;
    public static boolean enableSleepingBagSpawnPoint;
    public static boolean curiosIntegration;

    //World
    public static boolean enableLoot;
    public static boolean spawnEntitiesWithBackpack;
    public static List<? extends String> possibleOverworldEntityTypes;
    public static List<? extends String> possibleNetherEntityTypes;
    public static int spawnChance;
    public static List<? extends String> overworldBackpacks;
    public static List<? extends String> netherBackpacks;
    public static boolean enableVillagerTrade;

    //Abilities
    public static boolean enableBackpackAbilities;
    public static boolean forceAbilityEnabled;

    //Slowness Debuff
    public static boolean tooManyBackpacksSlowness;
    public static int maxNumberOfBackpacks;
    public static double slownessPerExcessedBackpack;

    //Client Settings
    public static boolean enableToolCycling;
    public static boolean disableScrollWheel;
    public static boolean obtainTips;
    public static boolean renderTools;
    public static boolean renderBackpackWithElytra;
    public static boolean disableBackpackRender;

    //Overlay
    public static boolean enableOverlay;
    public static int offsetX;
    public static int offsetY;

    public static class Common
    {
        private static final String REGISTRY_NAME_MATCHER = "([a-z0-9_.-]+:[a-z0-9_/.-]+)";

        BackpackSettings backpackSettings;
        World world;
        BackpackAbilities backpackAbilities;
        SlownessDebuff slownessDebuff;

        Common(final ForgeConfigSpec.Builder builder)
        {
            builder.comment("Common config settings")
                    .push("common");

            //Backpack Settings
            backpackSettings = new BackpackSettings(builder, "backpackSettings");

            //World
            world = new World(builder, "world");

            //Abilities
            backpackAbilities = new BackpackAbilities(builder, "backpackAbilities");

            //Slowness Debuff
            slownessDebuff = new SlownessDebuff(builder, "slownessDebuff");

            builder.pop();
        }

        public static class BackpackSettings
        {
            public final ForgeConfigSpec.BooleanValue enableTierUpgrades;
            public final ForgeConfigSpec.BooleanValue disableCrafting;
            public final ForgeConfigSpec.BooleanValue enableBackpackBlockWearable;
            public final ForgeConfigSpec.BooleanValue invulnerableBackpack;
            public final ForgeConfigSpec.BooleanValue toolSlotsAcceptSwords;
            public final ForgeConfigSpec.ConfigValue<List<? extends String>> toolSlotsAcceptableItems;
            public final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklistedItems;
            public final ForgeConfigSpec.BooleanValue allowShulkerBoxes;
            public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> tanksCapacity;
            public final ForgeConfigSpec.BooleanValue voidProtection;
            public final ForgeConfigSpec.BooleanValue backpackDeathPlace;
            public final ForgeConfigSpec.BooleanValue backpackForceDeathPlace;
            public final ForgeConfigSpec.BooleanValue enableSleepingBagSpawnPoint;
            public final ForgeConfigSpec.BooleanValue curiosIntegration;

            BackpackSettings(final ForgeConfigSpec.Builder builder, final String path)
            {
                builder.push(path);

                //Backpack Settings

                enableTierUpgrades = builder
                        .define("enableTierUpgrades", true);

                disableCrafting = builder
                        .define("disableCrafting", false);

                enableBackpackBlockWearable = builder
                        .comment("Enables wearing backpack directly from ground")
                        .define("enableBackpackBlockWearable", true);

                invulnerableBackpack = builder
                        .comment("Backpack immune to any damage source (lava, fire), can't be destroyed, never disappears as floating item")
                        .define("invulnerableBackpack", true);

                toolSlotsAcceptSwords = builder
                        .define("toolSlotsAcceptSwords", true);

                toolSlotsAcceptableItems = builder
                        .comment("List of items that can be put in tool slots (Use registry names, for example: minecraft:apple)")
                        .defineList("toolSlotsAcceptableItems", Collections.emptyList(), mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));

                blacklistedItems = builder
                        .comment("List of items that can't be put in backpack inventory (Use registry names, for example: minecraft:apple)")
                        .defineList("blacklistedItems", Collections.emptyList(), mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));

                allowShulkerBoxes = builder
                        .define("allowShulkerBoxes", false);

                tanksCapacity = builder
                        .comment("Represents tanks capacity for each tier, from left: Leather, Iron, Gold, Diamond, Netherite, 1000 equals 1 Bucket")
                        .defineList("tanksCapacity", getTanksCapacity(), mapping -> String.valueOf(mapping).matches("\\d+"));

                voidProtection = builder
                        .comment("Prevents backpack disappearing in void")
                        .define("voidProtection", true);

                backpackDeathPlace = builder
                        .comment("Places backpack at place where player died")
                        .define("backpackDeathPlace", true);

                backpackForceDeathPlace = builder
                        .comment("Places backpack at place where player died, replacing all blocks that are breakable and do not have inventory (backpackDeathPlace must be true in order to work)")
                        .define("backpackForceDeathPlace", false);

                enableSleepingBagSpawnPoint = builder
                        .define("enableSleepingBagSpawnPoint", false);

                curiosIntegration = builder
                        .comment("If true, backpack can only be worn by placing it in curios 'Back' slot", "WARNING - Remember to TAKE OFF BACKPACK BEFORE enabling or disabling this integration!! - if not you'll lose your backpack")
                        .define("curiosIntegration", false);

                builder.pop();
            }

            private List<Integer> getTanksCapacity()
            {
                List<Integer> ret = new ArrayList<>();
                ret.add(Reference.BUCKET * 2);
                ret.add(Reference.BUCKET * 3);
                ret.add(Reference.BUCKET * 4);
                ret.add(Reference.BUCKET * 5);
                ret.add(Reference.BUCKET * 6);
                return ret;
            }
        }

        public static class World
        {
            public final ForgeConfigSpec.BooleanValue enableLoot;
            public final ForgeConfigSpec.BooleanValue spawnEntitiesWithBackpack;
            public final ForgeConfigSpec.ConfigValue<List<? extends String>> possibleOverworldEntityTypes;
            public final ForgeConfigSpec.ConfigValue<List<? extends String>> possibleNetherEntityTypes;
            public final ForgeConfigSpec.IntValue spawnChance;
            public final ForgeConfigSpec.ConfigValue<List<? extends String>> overworldBackpacks;
            public final ForgeConfigSpec.ConfigValue<List<? extends String>> netherBackpacks;
            public final ForgeConfigSpec.BooleanValue enableVillagerTrade;

            World(final ForgeConfigSpec.Builder builder, final String path)
            {
                builder.push(path);

                enableLoot = builder
                        .comment("Enables backpacks spawning in loot chests")
                        .define("enableLoot", true);

                spawnEntitiesWithBackpack = builder
                        .comment("Enables chance to spawn Zombie, Skeleton, Wither Skeleton, Piglin or Enderman with random backpack equipped")
                        .define("spawnEntitiesWithBackpack", true);

                possibleOverworldEntityTypes = builder
                        .comment("List of overworld entity types that can spawn with equipped backpack. DO NOT ADD anything to this list, because the game will crash, remove entries if mob should not spawn with backpack")
                        .defineList("possibleOverworldEntityTypes", this::getPossibleOverworldEntityTypes, mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));

                possibleNetherEntityTypes = builder
                        .comment("List of nether entity types that can spawn with equipped backpack. DO NOT ADD anything to this list, because the game will crash, remove entries if mob should not spawn with backpack")
                        .defineList("possibleNetherEntityTypes", this::getPossibleNetherEntityTypes, mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));


                spawnChance = builder
                        .comment("Defines spawn chance of entity with backpack (1 in [selected value])")
                        .defineInRange("spawnChance", 500, 0, Integer.MAX_VALUE);

                overworldBackpacks = builder
                        .comment("List of backpacks that can spawn on overworld mobs")
                        .defineList("overworldBackpacks", this::getOverworldBackpacksList, mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));

                netherBackpacks = builder
                        .comment("List of backpacks that can spawn on nether mobs")
                        .defineList("netherBackpacks", this::getNetherBackpacksList, mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));

                enableVillagerTrade = builder
                        .comment("Enables trade for Villager Backpack in Librarian villager trades")
                        .define("enableVillagerTrade", true);

                builder.pop();
            }

            private List<String> getPossibleOverworldEntityTypes()
            {
                List<String> ret = new ArrayList<>();
                ret.add("minecraft:zombie");
                ret.add("minecraft:skeleton");
                ret.add("minecraft:enderman");
                return ret;
            }

            private List<String> getPossibleNetherEntityTypes()
            {
                List<String> ret = new ArrayList<>();
                ret.add("minecraft:wither_skeleton");
                ret.add("minecraft:piglin");
                return ret;
            }


            private List<String> getOverworldBackpacksList()
            {
                List<String> ret = new ArrayList<>();
                ret.add("travelersbackpack:standard");
                ret.add("travelersbackpack:diamond");
                ret.add("travelersbackpack:gold");
                ret.add("travelersbackpack:emerald");
                ret.add("travelersbackpack:iron");
                ret.add("travelersbackpack:lapis");
                ret.add("travelersbackpack:redstone");
                ret.add("travelersbackpack:coal");
                ret.add("travelersbackpack:bookshelf");
                ret.add("travelersbackpack:sandstone");
                ret.add("travelersbackpack:snow");
                ret.add("travelersbackpack:sponge");
                ret.add("travelersbackpack:cake");
                ret.add("travelersbackpack:cactus");
                ret.add("travelersbackpack:hay");
                ret.add("travelersbackpack:melon");
                ret.add("travelersbackpack:pumpkin");
                ret.add("travelersbackpack:creeper");
                ret.add("travelersbackpack:enderman");
                ret.add("travelersbackpack:skeleton");
                ret.add("travelersbackpack:spider");
                ret.add("travelersbackpack:bee");
                ret.add("travelersbackpack:wolf");
                ret.add("travelersbackpack:fox");
                ret.add("travelersbackpack:ocelot");
                ret.add("travelersbackpack:horse");
                ret.add("travelersbackpack:cow");
                ret.add("travelersbackpack:pig");
                ret.add("travelersbackpack:sheep");
                ret.add("travelersbackpack:chicken");
                ret.add("travelersbackpack:squid");
                return ret;
            }

            private List<String> getNetherBackpacksList()
            {
                List<String> ret = new ArrayList<>();
                ret.add("travelersbackpack:quartz");
                ret.add("travelersbackpack:nether");
                ret.add("travelersbackpack:blaze");
                ret.add("travelersbackpack:ghast");
                ret.add("travelersbackpack:magma_cube");
                ret.add("travelersbackpack:wither");
                return ret;
            }
        }

        public static class BackpackAbilities
        {
            public final ForgeConfigSpec.BooleanValue enableBackpackAbilities;
            public final ForgeConfigSpec.BooleanValue forceAbilityEnabled;

            BackpackAbilities(final ForgeConfigSpec.Builder builder, final String path)
            {
                builder.push(path);

                enableBackpackAbilities = builder
                        .define("enableBackpackAbilities", true);

                forceAbilityEnabled = builder
                        .define("forceAbilityEnabled", false);

                builder.pop();
            }
        }

        public static class SlownessDebuff
        {
            public final ForgeConfigSpec.BooleanValue tooManyBackpacksSlowness;
            public final ForgeConfigSpec.IntValue maxNumberOfBackpacks;
            public final ForgeConfigSpec.DoubleValue slownessPerExcessedBackpack;

            SlownessDebuff(final ForgeConfigSpec.Builder builder, final String path)
            {
                builder.push(path);

                tooManyBackpacksSlowness = builder
                        .comment("Player gets slowness effect, if carries too many backpacks in inventory")
                        .define("tooManyBackpacksSlowness", false);

                maxNumberOfBackpacks = builder
                        .comment("Maximum number of backpacks, which can be carried in inventory, without slowness effect")
                        .defineInRange("maxNumberOfBackpacks", 3, 1, 37);

                slownessPerExcessedBackpack = builder
                        .defineInRange("slownessPerExcessedBackpack", 1, 0.1, 5);

                builder.pop();
            }
        }

        public void loadItemsFromConfig(List<? extends String> configList, List<Item> targetList)
        {
            for(String registryName : configList)
            {
                ResourceLocation res = new ResourceLocation(registryName);

                if(ForgeRegistries.ITEMS.containsKey(res))
                {
                    targetList.add(ForgeRegistries.ITEMS.getValue(res));
                }
            }
        }

        public void loadEntityTypesFromConfig(List<? extends String> configList, List<EntityType> targetList)
        {
            for(String registryName : configList)
            {
                ResourceLocation res = new ResourceLocation(registryName);

                if(ForgeRegistries.ENTITIES.containsKey(res))
                {
                    targetList.add(ForgeRegistries.ENTITIES.getValue(res));
                }
            }
        }
    }

    public static class Client
    {
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
        //Backpack Settings
        enableTierUpgrades = COMMON.backpackSettings.enableTierUpgrades.get();
        disableCrafting = COMMON.backpackSettings.disableCrafting.get();
        enableBackpackBlockWearable = COMMON.backpackSettings.enableBackpackBlockWearable.get();
        invulnerableBackpack = COMMON.backpackSettings.invulnerableBackpack.get();
        toolSlotsAcceptSwords = COMMON.backpackSettings.toolSlotsAcceptSwords.get();
        toolSlotsAcceptableItems = COMMON.backpackSettings.toolSlotsAcceptableItems.get();
        blacklistedItems = COMMON.backpackSettings.blacklistedItems.get();
        allowShulkerBoxes = COMMON.backpackSettings.allowShulkerBoxes.get();
        tanksCapacity = COMMON.backpackSettings.tanksCapacity.get();
        voidProtection = COMMON.backpackSettings.voidProtection.get();
        backpackDeathPlace = COMMON.backpackSettings.backpackDeathPlace.get();
        backpackForceDeathPlace = COMMON.backpackSettings.backpackForceDeathPlace.get();
        enableSleepingBagSpawnPoint = COMMON.backpackSettings.enableSleepingBagSpawnPoint.get();
        curiosIntegration = COMMON.backpackSettings.curiosIntegration.get();

        //World
        enableLoot = COMMON.world.enableLoot.get();
        spawnEntitiesWithBackpack = COMMON.world.spawnEntitiesWithBackpack.get();
        possibleOverworldEntityTypes = COMMON.world.possibleOverworldEntityTypes.get();
        possibleNetherEntityTypes = COMMON.world.possibleNetherEntityTypes.get();
        spawnChance = COMMON.world.spawnChance.get();
        overworldBackpacks = COMMON.world.overworldBackpacks.get();
        netherBackpacks = COMMON.world.netherBackpacks.get();
        enableVillagerTrade = COMMON.world.enableVillagerTrade.get();

        //Abilities
        enableBackpackAbilities = COMMON.backpackAbilities.enableBackpackAbilities.get();
        forceAbilityEnabled = COMMON.backpackAbilities.forceAbilityEnabled.get();

        //Slowness Debuff
        tooManyBackpacksSlowness = COMMON.slownessDebuff.tooManyBackpacksSlowness.get();
        maxNumberOfBackpacks = COMMON.slownessDebuff.maxNumberOfBackpacks.get();
        slownessPerExcessedBackpack = COMMON.slownessDebuff.slownessPerExcessedBackpack.get();
    }

    public static void bakeClientConfig()
    {
        enableToolCycling = CLIENT.enableToolCycling.get();
        disableScrollWheel = CLIENT.disableScrollWheel.get();
        obtainTips = CLIENT.obtainTips.get();
        renderTools = CLIENT.renderTools.get();
        renderBackpackWithElytra = CLIENT.renderBackpackWithElytra.get();
        disableBackpackRender = CLIENT.disableBackpackRender.get();

        //Overlay
        enableOverlay = CLIENT.overlay.enableOverlay.get();
        offsetX = CLIENT.overlay.offsetX.get();
        offsetY = CLIENT.overlay.offsetY.get();
    }

    //GATHER DATA
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();

        if(event.includeServer())
        {
            generator.addProvider(new ModRecipeProvider(generator));
        }
    }
}