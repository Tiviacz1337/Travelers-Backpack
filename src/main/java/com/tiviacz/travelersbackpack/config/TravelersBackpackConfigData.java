package com.tiviacz.travelersbackpack.config;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;

@Config(name = TravelersBackpack.MODID)
public class TravelersBackpackConfigData implements ConfigData
{
    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.CollapsibleObject
    public BackpackSettings backpackSettings = new BackpackSettings();

    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.CollapsibleObject
    public World world = new World();

    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.CollapsibleObject
    public BackpackAbilities backpackAbilities = new BackpackAbilities();

    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.CollapsibleObject
    public SlownessDebuff slownessDebuff = new SlownessDebuff();

    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.CollapsibleObject
    public Client client = new Client();

    public static class BackpackSettings
    {
        @ConfigEntry.Gui.CollapsibleObject
        public LeatherTierConfig leather = new LeatherTierConfig();

        @ConfigEntry.Gui.CollapsibleObject
        public IronTierConfig iron = new IronTierConfig();

        @ConfigEntry.Gui.CollapsibleObject
        public GoldTierConfig gold = new GoldTierConfig();

        @ConfigEntry.Gui.CollapsibleObject
        public DiamondTierConfig diamond = new DiamondTierConfig();

        @ConfigEntry.Gui.CollapsibleObject
        public NetheriteTierConfig netherite = new NetheriteTierConfig();

        @ConfigEntry.Gui.RequiresRestart
        public boolean enableTierUpgrades = true;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Unused - keep for compatibility only, will be removed in future versions")
        public boolean disableCrafting = false;

        @ConfigEntry.Gui.RequiresRestart
        public boolean enableCraftingUpgrade = true;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("New backpacks will have crafting grid by default")
        public boolean craftingUpgradeByDefault = false;

        @ConfigEntry.Gui.RequiresRestart
        public boolean craftingSavesItems = true;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Enables wearing backpack directly from ground")
        public boolean enableBackpackBlockQuickEquip = true;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Enables unequipping the backpack when player clicks with empty hand on the ground")
        public boolean enableBackpackRightClickUnequip = false;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Backpack immune to any damage source (lava, fire), can't be destroyed, never disappears as floating item")
        public boolean invulnerableBackpack = true;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("List of items that can be put in tool slots (Use registry names, for example: minecraft:apple, minecraft:flint)")
        public String[] toolSlotsAcceptableItems = {};

        @ConfigEntry.Gui.RequiresRestart
        @Comment("List of items that can't be put in backpack inventory (Use registry names, for example: minecraft:apple, minecraft:flint)")
        public String[] blacklistedItems = {};

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Tool slots accept any item")
        public boolean toolSlotsAcceptEverything = false;

        @ConfigEntry.Gui.RequiresRestart
        public boolean allowShulkerBoxes = false;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Unused - keep for compatibility only, will be removed in future versions")
        public long[] tanksCapacity = new long[]
                {FluidConstants.BUCKET * 2, FluidConstants.BUCKET * 3, FluidConstants.BUCKET * 4, FluidConstants.BUCKET * 5, FluidConstants.BUCKET * 6};

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Prevents backpack disappearing in void")
        public boolean voidProtection = true;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Places backpack at place where player died")
        public boolean backpackDeathPlace = true;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Places backpack at place where player died, replacing all blocks that are breakable and do not have inventory (backpackDeathPlace must be true in order to work)")
        public boolean backpackForceDeathPlace = false;

        @ConfigEntry.Gui.RequiresRestart
        public boolean enableSleepingBagSpawnPoint = false;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("If true, backpack can only be worn by placing it in curios 'Back' slot\nWARNING - Remember to TAKE OFF BACKPACK BEFORE enabling or disabling this integration!! - if not you'll lose your backpack")
        public boolean trinketsIntegration = false;
    }

    public static class LeatherTierConfig
    {
        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of inventory slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 1, max = 63)
        public int inventorySlotCount = 27;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of tool slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 0, max = 6)
        public int toolSlotCount = 2;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Tank capacity for the tier, 81000 equals 1 Bucket, (Leather default: 3 buckets)")
        public long tankCapacity = FluidConstants.BUCKET * 3;
    }

    public static class IronTierConfig
    {
        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of inventory slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 1, max = 63)
        public int inventorySlotCount = 36;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of tool slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 0, max = 6)
        public int toolSlotCount = 3;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Tank capacity for the tier, 81000 equals 1 Bucket, (Iron default: 4 buckets)")
        public long tankCapacity = FluidConstants.BUCKET * 4;
    }

    public static class GoldTierConfig
    {
        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of inventory slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 1, max = 63)
        public int inventorySlotCount = 45;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of tool slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 0, max = 6)
        public int toolSlotCount = 4;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Tank capacity for the tier, 81000 equals 1 Bucket, (Gold default: 5 buckets)")
        public long tankCapacity = FluidConstants.BUCKET * 5;
    }

    public static class DiamondTierConfig
    {
        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of inventory slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 1, max = 63)
        public int inventorySlotCount = 54;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of tool slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 0, max = 6)
        public int toolSlotCount = 5;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Tank capacity for the tier, 81000 equals 1 Bucket, (Diamond default: 6 buckets)")
        public long tankCapacity = FluidConstants.BUCKET * 6;
    }

    public static class NetheriteTierConfig
    {
        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of inventory slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 1, max = 63)
        public int inventorySlotCount = 63;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of tool slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 0, max = 6)
        public int toolSlotCount = 6;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Tank capacity for the tier, 81000 equals 1 Bucket, (Netherite default: 7 buckets)")
        public long tankCapacity = FluidConstants.BUCKET * 7;
    }

    public static class World
    {
        @ConfigEntry.Gui.RequiresRestart
        @Comment("Enables backpacks spawning in loot chests")
        public boolean enableLoot = true;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Enables chance to spawn Zombie, Skeleton, Wither Skeleton, Piglin or Enderman with random backpack equipped")
        public boolean spawnEntitiesWithBackpack = true;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("List of overworld entity types that can spawn with equipped backpack. DO NOT ADD anything to this list, because the game will crash, remove entries if mob should not spawn with backpack")
        public String[] possibleOverworldEntityTypes = {"minecraft:zombie", "minecraft:skeleton", "minecraft:enderman"};

        @ConfigEntry.Gui.RequiresRestart
        @Comment("List of nether entity types that can spawn with equipped backpack. DO NOT ADD anything to this list, because the game will crash, remove entries if mob should not spawn with backpack")
        public String[] possibleNetherEntityTypes = {
                "minecraft:wither_skeleton",
                "minecraft:piglin"
            };

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Defines spawn chance of entity with backpack (1 in [selected value])")
        public int spawnChance = 500;

        @ConfigEntry.Gui.RequiresRestart
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
        @Comment("Enables trade for Villager Backpack in Librarian villager trades")
        public boolean enableVillagerTrade = true;
    }

    public static class BackpackAbilities
    {
        @ConfigEntry.Gui.RequiresRestart
        public boolean enableBackpackAbilities = true;

        @ConfigEntry.Gui.RequiresRestart
        public boolean forceAbilityEnabled = false;

        @ConfigEntry.Gui.RequiresRestart
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
        @Comment("Player gets slowness effect, if carries too many backpacks in inventory")
        public boolean tooManyBackpacksSlowness = false;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.BoundedDiscrete(min = 1, max = 37)
        @Comment("Maximum number of backpacks, which can be carried in inventory, without slowness effect")
        public int maxNumberOfBackpacks = 3;

        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.BoundedDiscrete(min = 1, max = 5)
        public int slownessPerExcessedBackpack = 1;
    }

    public static class Client
    {
        @ConfigEntry.Gui.CollapsibleObject
        public Overlay overlay = new Overlay();

        @Comment("Enables legacy GUI (Blue slots for storage, brown for crafting and green for tools)")
        public boolean enableLegacyGui = false;

        @Comment("Enables tool cycling via keybind (Default Z) + scroll combination, while backpack is worn")
        public boolean enableToolCycling = true;

        @Comment("Allows tool cycling using keybinding only (Default Z)")
        public boolean disableScrollWheel = false;

        @Comment("Enables tip, how to obtain a backpack, if there's no crafting recipe for it")
        public boolean obtainTips = true;

        @Comment("Render tools in tool slots on the backpack, while worn")
        public boolean renderTools = true;

        @Comment("Render backpack if elytra is present")
        public boolean renderBackpackWithElytra = true;

        @Comment("Disable backpack rendering")
        public boolean disableBackpackRender = false;

        public static class Overlay
        {
            @Comment("Enables tanks and tool slots overlay, while backpack is worn")
            public boolean enableOverlay = true;

            @Comment("Offsets to left side")
            public int offsetX = 20;

            @Comment("Offsets to up")
            public int offsetY = 30;
        }
    }
}