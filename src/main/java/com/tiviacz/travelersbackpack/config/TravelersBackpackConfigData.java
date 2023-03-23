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

    @ConfigEntry.Category("Abilities")
    Abilities abilities = new Abilities();

    @ConfigEntry.Category("SlownessDebuff")
    SlownessDebuff slownessDebuff = new SlownessDebuff();

    @ConfigEntry.Category("Client")
    Client client = new Client();

    public static class BackpackSettings
    {
        @ConfigEntry.Gui.NoTooltip
        public boolean disableCrafting = false;

        @ConfigEntry.Gui.Tooltip
        @Comment("Enables wearing backpack directly from ground")
        public boolean enableBackpackBlockQuickEquip = true;

        @ConfigEntry.Gui.Tooltip
        @Comment("Backpack immune to any damage source (lava, fire), can't be destroyed, never disappears as floating item")
        public boolean invulnerableBackpack = true;

        @ConfigEntry.Gui.NoTooltip
        public long tanksCapacity = Reference.BASIC_TANK_CAPACITY;

        @ConfigEntry.Gui.Tooltip
        @Comment("Prevents backpack disappearing in void")
        public boolean voidProtection = true;

        @ConfigEntry.Gui.Tooltip
        @Comment("Places backpack at place where player died")
        public boolean backpackDeathPlace = true;

        @ConfigEntry.Gui.Tooltip
        @Comment("Places backpack at place where player died, replacing all blocks that are breakable and do not have inventory (backpackDeathPlace must be true in order to work)")
        public boolean backpackForceDeathPlace = false;

        @ConfigEntry.Gui.NoTooltip
        public boolean enableSleepingBagSpawnPoint = false;

        @ConfigEntry.Gui.Tooltip(count = 2)
        @Comment("If true, backpack can only be worn by placing it in curios 'Back' slot\nWARNING - Remember to TAKE OFF BACKPACK BEFORE enabling or disabling this integration!! - if not you'll lose your backpack")
        public boolean trinketsIntegration = false;
    }

    public static class World
    {
        @ConfigEntry.Gui.Tooltip
        @Comment("Enables backpacks spawning in loot chests")
        public boolean enableLoot = true;

        @ConfigEntry.Gui.Tooltip
        @Comment("Enables chance to spawn Zombie, Skeleton, Wither Skeleton, Piglin or Enderman with random backpack equipped")
        public boolean spawnEntitiesWithBackpack = true;

        @ConfigEntry.Gui.Tooltip
        @Comment("Defines spawn chance of entity with backpack (1 in [selected value])")
        public int spawnChance = 100;
    }

    public static class Abilities
    {
        @ConfigEntry.Gui.NoTooltip
        public boolean enableBackpackAbilities = true;

        @ConfigEntry.Gui.NoTooltip
        public boolean forceAbilityEnabled = false;
    }

    public static class SlownessDebuff
    {
        @ConfigEntry.Gui.Tooltip
        @Comment("Player gets slowness effect, if carries too many backpacks in inventory")
        public boolean tooManyBackpacksSlowness = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 37)
        @Comment("Maximum number of backpacks, which can be carried in inventory, without slowness effect")
        public int maxNumberOfBackpacks = 3;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 5)
        public int slownessPerExcessedBackpack = 1;
    }

    public static class Client
    {
        @ConfigEntry.Category("Overlay")
        Client.Overlay overlay = new Client.Overlay();

        @ConfigEntry.Gui.Tooltip
        @Comment("Enables tool cycling via keybind (Default Z) + scroll combination, while backpack is worn")
        public boolean enableToolCycling = true;

        @ConfigEntry.Gui.Tooltip
        @Comment("Enables tip, how to obtain a backpack, if there's no crafting recipe for it")
        public boolean obtainTips = true;

        @ConfigEntry.Gui.Tooltip
        @Comment("Render tools in tool slots on the backpack, while worn")
        public boolean renderTools = true;

        @ConfigEntry.Gui.Tooltip
        @Comment("Render backpack if elytra is present")
        public boolean renderBackpackWithElytra = true;

        @ConfigEntry.Gui.Tooltip
        @Comment("Disable backpack rendering")
        public boolean disableBackpackRender = false;

        public static class Overlay
        {
            @ConfigEntry.Gui.Tooltip
            @Comment("Enables tanks and tool slots overlay, while backpack is worn")
            public boolean enableOverlay = true;

            @ConfigEntry.Gui.Tooltip
            @Comment("Offsets to left side")
            public int offsetX = 20;

            @ConfigEntry.Gui.Tooltip
            @Comment("Offsets to up")
            public int offsetY = 30;
        }
    }
}