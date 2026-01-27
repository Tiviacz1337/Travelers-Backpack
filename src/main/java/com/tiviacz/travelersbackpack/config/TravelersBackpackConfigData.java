package com.tiviacz.travelersbackpack.config;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;

@Config(name = TravelersBackpack.MODID)
public class TravelersBackpackConfigData implements ConfigData {
    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.CollapsibleObject
    public BackpackSettings backpackSettings = new BackpackSettings();

    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.CollapsibleObject
    public BackpackUpgrades backpackUpgrades = new BackpackUpgrades();

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

    public static class BackpackUpgrades {
        @ConfigEntry.Gui.RequiresRestart
        public boolean enableTanksUpgrade = true;

        @ConfigEntry.Gui.RequiresRestart
        public boolean enableCraftingUpgrade = true;

        @ConfigEntry.Gui.RequiresRestart
        public boolean enableFurnaceUpgrade = true;

        @ConfigEntry.Gui.RequiresRestart
        public boolean enableSmokerUpgrade = true;

        @ConfigEntry.Gui.RequiresRestart
        public boolean enableBlastFurnaceUpgrade = true;

        @ConfigEntry.Gui.RequiresRestart
        public boolean enableJukeboxUpgrade = true;

        @ConfigEntry.Gui.CollapsibleObject
        public PickupUpgradeSettings pickupUpgradeSettings = new PickupUpgradeSettings();

        @ConfigEntry.Gui.CollapsibleObject
        public MagnetUpgradeSettings magnetUpgradeSettings = new MagnetUpgradeSettings();

        @ConfigEntry.Gui.CollapsibleObject
        public FeedingUpgradeSettings feedingUpgradeSettings = new FeedingUpgradeSettings();

        @ConfigEntry.Gui.CollapsibleObject
        public RefillUpgradeSettings refillUpgradeSettings = new RefillUpgradeSettings();

        @ConfigEntry.Gui.CollapsibleObject
        public VoidUpgradeSettings voidUpgradeSettings = new VoidUpgradeSettings();

        public static class PickupUpgradeSettings {
            @ConfigEntry.Gui.RequiresRestart
            public boolean enableUpgrade = true;

            @ConfigEntry.Gui.RequiresRestart
            @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
            public int filterSlotCount = 9;

            @ConfigEntry.Gui.RequiresRestart
            @ConfigEntry.BoundedDiscrete(min = 1, max = 5)
            public int slotsInRow = 3;
        }

        public static class MagnetUpgradeSettings {
            @ConfigEntry.Gui.RequiresRestart
            public boolean enableUpgrade = true;

            @ConfigEntry.Gui.RequiresRestart
            @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
            public int filterSlotCount = 9;

            @ConfigEntry.Gui.RequiresRestart
            @ConfigEntry.BoundedDiscrete(min = 1, max = 5)
            public int slotsInRow = 3;

            @ConfigEntry.Gui.RequiresRestart
            public int pullRange = 5;

            @ConfigEntry.Gui.RequiresRestart
            public int tickRate = 10;
        }

        public static class FeedingUpgradeSettings {
            @ConfigEntry.Gui.RequiresRestart
            public boolean enableUpgrade = true;

            @ConfigEntry.Gui.RequiresRestart
            @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
            public int filterSlotCount = 9;

            @ConfigEntry.Gui.RequiresRestart
            @ConfigEntry.BoundedDiscrete(min = 1, max = 5)
            public int slotsInRow = 3;

            @ConfigEntry.Gui.RequiresRestart
            public int tickRate = 100;
        }

        public static class RefillUpgradeSettings {
            @ConfigEntry.Gui.RequiresRestart
            public boolean enableUpgrade = true;

            @ConfigEntry.Gui.RequiresRestart
            @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
            public int filterSlotCount = 9;

            @ConfigEntry.Gui.RequiresRestart
            @ConfigEntry.BoundedDiscrete(min = 1, max = 5)
            public int slotsInRow = 3;

            @ConfigEntry.Gui.RequiresRestart
            public int tickRate = 5;
        }

        public static class VoidUpgradeSettings {
            @ConfigEntry.Gui.RequiresRestart
            public boolean enableUpgrade = true;

            @ConfigEntry.Gui.RequiresRestart
            @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
            public int filterSlotCount = 9;

            @ConfigEntry.Gui.RequiresRestart
            @ConfigEntry.BoundedDiscrete(min = 1, max = 5)
            public int slotsInRow = 3;
        }
    }

    public static class BackpackSettings {
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
        @Comment("Enables equipping the backpack on right-click from the ground (Disabled if Trinkets/Accessories Integration is enabled)")
        public boolean rightClickEquip = true;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Enables unequipping the backpack on right-click on the ground with empty hand (Disabled if Trinkets/Accessories Integration is enabled)")
        public boolean rightClickUnequip = false;

        @Comment("Allows to use only equipped backpack")
        public boolean allowOnlyEquippedBackpack = false;

        @Comment("Allows opening the backpack by pressing a keybind while hovering over the slot with backpack in the player's inventory")
        public boolean allowOpeningFromSlot = false;

        @Comment("Prevents more than one player from accessing the backpack at the same time when it's placed on the ground")
        public boolean preventMultiplePlayersAccess = false;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Backpack immune to any damage source (lava, fire), can't be destroyed, never disappears as floating item")
        public boolean invulnerableBackpack = true;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Allows swapping tools between tool slots and the player’s inventory via a quick-swap menu")
        public boolean allowToolSwapping = true;

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
        @Comment("Allows putting shulker boxes and other items with inventory in backpack")
        public boolean allowShulkerBoxes = false;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Prevents backpack disappearing in void, spawns floating backpack above minimum Y when player dies in void")
        public boolean voidProtection = true;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Places backpack at place where player died")
        public boolean backpackDeathPlace = true;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Places backpack at place where player died, replacing all blocks that are breakable and do not have inventory (backpackDeathPlace must be true in order to work)")
        public boolean backpackForceDeathPlace = false;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Allows sleeping in a sleeping bag without the need to unequip and place the backpack on the ground")
        public boolean quickSleepingBag = true;

        @ConfigEntry.Gui.RequiresRestart
        public boolean enableSleepingBagSpawnPoint = false;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Backpacks can only be equipped in the Trinkets/Accessories 'Back' slot, provided those mods are installed. If set to false, backpacks can only be equipped by clicking the button in the Backpack GUI. " +
                "This setting can be changed without unequipping the backpack. An already equipped backpack will not disappear and can be retrieved from the player's inventory.")
        public boolean backSlotIntegration = true;
    }

    public static class LeatherTierConfig {
        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of inventory slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 1, max = 154)
        public int inventorySlotCount = 27;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of upgrade slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
        public int upgradeSlotCount = 2;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of tool slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 0, max = 8)
        public int toolSlotCount = 2;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Tank capacity per row of backpack storage, 81000 equals 1 Bucket (Leather backpack 3 rows of 9 slots = 3 * 81000")
        public long tankCapacityPerRow = FluidConstants.BUCKET;
    }

    public static class IronTierConfig {
        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of inventory slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 1, max = 154)
        public int inventorySlotCount = 45;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of upgrade slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
        public int upgradeSlotCount = 3;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of tool slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 0, max = 8)
        public int toolSlotCount = 3;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Tank capacity per row of backpack storage, 81000 equals 1 Bucket (Leather backpack 3 rows of 9 slots = 3 * 81000")
        public long tankCapacityPerRow = FluidConstants.BUCKET;
    }

    public static class GoldTierConfig {
        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of inventory slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 1, max = 154)
        public int inventorySlotCount = 63;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of upgrade slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
        public int upgradeSlotCount = 4;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of tool slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 0, max = 8)
        public int toolSlotCount = 4;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Tank capacity per row of backpack storage, 81000 equals 1 Bucket (Leather backpack 3 rows of 9 slots = 3 * 81000")
        public long tankCapacityPerRow = FluidConstants.BUCKET;
    }

    public static class DiamondTierConfig {
        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of inventory slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 1, max = 154)
        public int inventorySlotCount = 81;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of upgrade slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
        public int upgradeSlotCount = 5;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of tool slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 0, max = 8)
        public int toolSlotCount = 5;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Tank capacity per row of backpack storage, 81000 equals 1 Bucket (Leather backpack 3 rows of 9 slots = 3 * 81000")
        public long tankCapacityPerRow = FluidConstants.BUCKET;
    }

    public static class NetheriteTierConfig {
        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of inventory slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 1, max = 154)
        public int inventorySlotCount = 99;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of upgrade slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
        public int upgradeSlotCount = 6;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Number of tool slots for the tier")
        @ConfigEntry.BoundedDiscrete(min = 0, max = 8)
        public int toolSlotCount = 6;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Tank capacity per row of backpack storage, 81000 equals 1 Bucket (Leather backpack 3 rows of 9 slots = 3 * 81000")
        public long tankCapacityPerRow = FluidConstants.BUCKET;
    }

    public static class World {
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
        @Comment("Defines spawn chance of entity with a backpack")
        @ConfigEntry.BoundedDiscrete(min = 0, max = 1)
        public float chance = 0.005F;

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

    public static class BackpackAbilities {
        @ConfigEntry.Gui.RequiresRestart
        public boolean enableBackpackAbilities = true;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Newly crafted backpacks will have ability enabled by default")
        public boolean forceAbilityEnabled = true;

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
                "travelersbackpack:warden",
                "travelersbackpack:bat",
                "travelersbackpack:bee",
                "travelersbackpack:ocelot",
                "travelersbackpack:cow",
                "travelersbackpack:chicken",
                "travelersbackpack:squid",
                "travelersbackpack:hay",
                "travelersbackpack:fox"
        };

        @ConfigEntry.Gui.RequiresRestart
        @Comment("List of effect abilities associated with backpacks, you can modify this list as you wish. Different effects can be added to different backpacks. \n Formatting: \"<backpack_registry_name>; <status_effect_registry_name>; <min_duration_ticks>; <max_duration_ticks>; <amplifier>\"")
        public String[] backpackEffects = {
                "travelersbackpack:bat; minecraft:night_vision; 260; 300; 0",
                "travelersbackpack:magma_cube; minecraft:fire_resistance; 260; 300; 0",
                "travelersbackpack:squid; minecraft:water_breathing; 260; 300; 0",
                "travelersbackpack:squid; minecraft:night_vision; 260; 300; 0",
                "travelersbackpack:dragon; minecraft:regeneration; 260; 300; 0",
                "travelersbackpack:dragon; minecraft:strength; 250; 290; 0",
                "travelersbackpack:quartz; minecraft:haste; 260; 300; 0",
                "travelersbackpack:fox; minecraft:jump_boost; 260; 300; 0"
        };

        @ConfigEntry.Gui.RequiresRestart
        @Comment("List of cooldowns that are being applied after ability usage, the backpacks on the list are all that currently have cooldowns, adding additional backpack will not give it cooldown. \n Formatting: \"<backpack_registry_name>; <min_possible_cooldown_seconds>; <max_possible_cooldown_seconds>\"")
        public String[] cooldowns = {
                "travelersbackpack:creeper; 1200; 1800",
                "travelersbackpack:cow; 480; 540",
                "travelersbackpack:chicken; 360; 600",
                "travelersbackpack:cake; 360; 480",
                "travelersbackpack:melon; 120; 480"
        };
    }

    public static class SlownessDebuff {
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

    public static class Client {
        @ConfigEntry.Gui.CollapsibleObject
        public ToolsOverlay toolsOverlay = new ToolsOverlay();

        @ConfigEntry.Gui.CollapsibleObject
        public Overlay overlay = new Overlay();

        @Comment("Whether the backpack icon should be visible in player's inventory")
        public boolean showBackpackIconInInventory = true;

        @Comment("Sends a message to the player on death with backpack coordinates")
        public boolean sendBackpackCoordinatesMessage = true;

        @Comment("Depreciated - see allowToolSwapping instead")
        public boolean enableToolCycling = true;

        @Comment("Depreciated")
        public boolean disableScrollWheel = false;

        @Comment("Enables tip, how to obtain a backpack, if there's no crafting recipe for it")
        public boolean obtainTips = true;

        @Comment("Render tools in tool slots on the backpack, while worn")
        public boolean renderTools = true;

        @Comment("Only for supporters, option to show/hide the Supporter Star Badge. If you want to receive the Supporter Star Badge, visit my Ko-fi page :)! - https://ko-fi.com/tiviacz1337")
        public boolean showSupporterBadge = true;

        public static class ToolsOverlay {
            @Comment("X offset")
            public int offsetX = 0;

            @Comment("Y offset")
            public int offsetY = 0;
        }

        public static class Overlay {
            @Comment("Enables tanks and tool slots overlay, while backpack is worn")
            public boolean enableOverlay = true;

            @Comment("Offsets to left side")
            public int offsetX = 20;

            @Comment("Offsets to up")
            public int offsetY = 30;
        }
    }
}