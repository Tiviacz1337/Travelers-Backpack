package com.tiviacz.travelersbackpack.config;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.util.Reference;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = TravelersBackpack.MODID)
public class TravelersBackpackConfigData implements ConfigData
{
    @ConfigEntry.Category("BackpackSettings")
    BackpackSettings backpackSettings = new BackpackSettings();

    @ConfigEntry.Category("World")
    World world = new World();

    @ConfigEntry.Category("BackpackAbilities")
    BackpackAbilities backpackAbilities = new BackpackAbilities();

    @ConfigEntry.Category("SlownessDebuff")
    SlownessDebuff slownessDebuff = new SlownessDebuff();

    @ConfigEntry.Category("Client")
    Client client = new Client();

    public static class BackpackSettings
    {
        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.NoTooltip
        public boolean enableTierUpgrades = true;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.NoTooltip
        public boolean disableCrafting = false;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("Enables wearing backpack directly from ground")
        public boolean enableBackpackBlockQuickEquip = true;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("Backpack immune to any damage source (lava, fire), can't be destroyed, never disappears as floating item")
        public boolean invulnerableBackpack = true;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("List of items that can be put in tool slots (Use registry names, for example: minecraft:apple)")
        public String[] toolSlotsAcceptableItems = {};

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("List of items that can't be put in backpack inventory (Use registry names, for example: minecraft:apple)")
        public String[] blacklistedItems = {};

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        public boolean allowShulkerBoxes = false;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.NoTooltip
        public long[] tanksCapacity = new long[] {Reference.BUCKET * 2, Reference.BUCKET * 3, Reference.BUCKET * 4, Reference.BUCKET * 5, Reference.BUCKET * 6}; //Reference.BASIC_TANK_CAPACITY;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("Prevents backpack disappearing in void")
        public boolean voidProtection = true;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("Places backpack at place where player died")
        public boolean backpackDeathPlace = true;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("Places backpack at place where player died, replacing all blocks that are breakable and do not have inventory (backpackDeathPlace must be true in order to work)")
        public boolean backpackForceDeathPlace = false;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.NoTooltip
        public boolean enableSleepingBagSpawnPoint = false;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip(count = 2)
        @Comment("If true, backpack can only be worn by placing it in curios 'Back' slot\nWARNING - Remember to TAKE OFF BACKPACK BEFORE enabling or disabling this integration!! - if not you'll lose your backpack")
        public boolean trinketsIntegration = false;
    }

    public static class World
    {
        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("Enables backpacks spawning in loot chests")
        public boolean enableLoot = true;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("Enables chance to spawn Zombie, Skeleton, Wither Skeleton, Piglin or Enderman with random backpack equipped")
        public boolean spawnEntitiesWithBackpack = true;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("List of overworld entity types that can spawn with equipped backpack. DO NOT ADD anything to this list, because the game will crash, remove entries if mob should not spawn with backpack")
        public String[] possibleOverworldEntityTypes = {"minecraft:zombie", "minecraft:skeleton", "minecraft:enderman"};

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("List of nether entity types that can spawn with equipped backpack. DO NOT ADD anything to this list, because the game will crash, remove entries if mob should not spawn with backpack")
        public String[] possibleNetherEntityTypes = {
                "minecraft:wither_skeleton",
                "minecraft:piglin"
            };

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("Defines spawn chance of entity with backpack (1 in [selected value])")
        public int spawnChance = 500;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("List of backpacks that can spawn on overworld mobs")
        public String[] overworldBackpacks = {
                "travelersbackpack:standard",
                "travelersbackpack:diamond",
                "travelersbackpack:gold",
                "travelersbackpack:emerald",
                "travelersbackpack:iron",
                "travelersbackpack:lapis",
                "travelersbackpack:redstone",
                "travelersbackpack:coal",
                "travelersbackpack:bookshelf",
                "travelersbackpack:sandstone",
                "travelersbackpack:snow",
                "travelersbackpack:sponge",
                "travelersbackpack:cake",
                "travelersbackpack:cactus",
                "travelersbackpack:hay",
                "travelersbackpack:melon",
                "travelersbackpack:pumpkin",
                "travelersbackpack:creeper",
                "travelersbackpack:enderman",
                "travelersbackpack:skeleton",
                "travelersbackpack:spider",
                "travelersbackpack:bee",
                "travelersbackpack:wolf",
                "travelersbackpack:fox",
                "travelersbackpack:ocelot",
                "travelersbackpack:horse",
                "travelersbackpack:cow",
                "travelersbackpack:pig",
                "travelersbackpack:sheep",
                "travelersbackpack:chicken",
                "travelersbackpack:squid"
        };

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("List of backpacks that can spawn on nether mobs")
        public String[] netherBackpacks = {
                "travelersbackpack:quartz",
                "travelersbackpack:nether",
                "travelersbackpack:blaze",
                "travelersbackpack:ghast",
                "travelersbackpack:magma_cube",
                "travelersbackpack:wither"
        };

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("Enables trade for Villager Backpack in Librarian villager trades")
        public boolean enableVillagerTrade = true;
    }

    public static class BackpackAbilities
    {
        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.NoTooltip
        public boolean enableBackpackAbilities = true;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.NoTooltip
        public boolean forceAbilityEnabled = false;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("List of backpacks that are allowed to have an ability. DO NOT ADD anything to this list, because the game will crash, remove entries if backpack should not have ability")
        public String[] allowedAbilities = {
                "travelersbackpack:netherite",
                "travelersbackpack:diamond",
                "travelersbackpack:gold",
                "travelersbackpack:emerald",
                "travelersbackpack:iron",
                "travelersbackpack:lapis",
                "travelersbackpack:redstone",
                "travelersbackpack:bookshelf",
                "travelersbackpack:sponge",
                "travelersbackpack:cake",
                "travelersbackpack:cactus",
                "travelersbackpack:melon",
                "travelersbackpack:pumpkin",
                "travelersbackpack:creeper",
                "travelersbackpack:dragon",
                "travelersbackpack:enderman",
                "travelersbackpack:blaze",
                "travelersbackpack:ghast",
                "travelersbackpack:magma_cube",
                "travelersbackpack:spider",
                "travelersbackpack:wither",
                "travelersbackpack:bat",
                "travelersbackpack:bee",
                "travelersbackpack:ocelot",
                "travelersbackpack:cow",
                "travelersbackpack:chicken",
                "travelersbackpack:squid"
        };
    }

    public static class SlownessDebuff
    {
        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("Player gets slowness effect, if carries too many backpacks in inventory")
        public boolean tooManyBackpacksSlowness = true;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 37)
        @Comment("Maximum number of backpacks, which can be carried in inventory, without slowness effect")
        public int maxNumberOfBackpacks = 3;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 5)
        public int slownessPerExcessedBackpack = 1;
    }

    public static class Client
    {
        @ConfigEntry.Category("Overlay")
        Overlay overlay = new Overlay();

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("Enables tool cycling via keybind (Default Z) + scroll combination, while backpack is worn")
        public boolean enableToolCycling = true;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("Enables tip, how to obtain a backpack, if there's no crafting recipe for it")
        public boolean obtainTips = true;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("Render tools in tool slots on the backpack, while worn")
        public boolean renderTools = true;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("Render backpack if elytra is present")
        public boolean renderBackpackWithElytra = true;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        @Comment("Disable backpack rendering")
        public boolean disableBackpackRender = false;

        public static class Overlay
        {
            @ConfigEntry.Gui.RequiresRestart
            @ConfigEntry.Gui.Tooltip
            @Comment("Enables tanks and tool slots overlay, while backpack is worn")
            public boolean enableOverlay = true;

            @ConfigEntry.Gui.RequiresRestart
            @ConfigEntry.Gui.Tooltip
            @Comment("Offsets to left side")
            public int offsetX = 20;

            @ConfigEntry.Gui.RequiresRestart
            @ConfigEntry.Gui.Tooltip
            @Comment("Offsets to up")
            public int offsetY = 30;
        }
    }
}