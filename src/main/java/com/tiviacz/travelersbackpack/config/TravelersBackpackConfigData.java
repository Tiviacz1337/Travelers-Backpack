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
    @ConfigEntry.Gui.NoTooltip
    public boolean disableCrafting = false;

    @ConfigEntry.Gui.Tooltip
    @Comment("Enables wearing backpack directly from ground")
    public boolean enableBackpackBlockQuickEquip = true;

    @ConfigEntry.Gui.Tooltip
    @Comment("Enables backpacks spawning in loot chests")
    public boolean enableLoot = true;

    @ConfigEntry.Gui.Tooltip
    @Comment("Backpack immune to any damage source (lava, fire), can't be destroyed, never disappears as floating item")
    public boolean invulnerableBackpack = true;

    @ConfigEntry.Gui.NoTooltip
    public boolean enableBackpackAbilities = true;

    @ConfigEntry.Gui.NoTooltip
    public long tanksCapacity = Reference.BASIC_TANK_CAPACITY;

    @ConfigEntry.Gui.Tooltip(count = 2)
    @Comment("If true, backpack can only be worn by placing it in curios 'Back' slot\nWARNING - Remember to TAKE OFF BACKPACK BEFORE enabling or disabling this integration!! - if not you'll lose your backpack")
    public boolean trinketsIntegration = false;

    @ConfigEntry.Gui.Tooltip
    @Comment("Places backpack at place where player died")
    public boolean backpackDeathPlace = true;

    @ConfigEntry.Gui.Tooltip
    @Comment("Places backpack at place where player died, replacing all blocks that are breakable and do not have inventory (backpackDeathPlace must be true in order to work)")
    public boolean backpackForceDeathPlace = false;

    @ConfigEntry.Gui.NoTooltip
    public boolean enableSleepingBagSpawnPoint = false;

    @ConfigEntry.Gui.Tooltip
    @Comment("Enables auto message with backpack coords after player dies")
    public boolean enableBackpackCoordsMessage = true;

    @ConfigEntry.Gui.Tooltip
    @Comment("Enables tool cycling via keybind (Default Z) + scroll combination, while backpack is worn")
    public boolean enableToolCycling = true;

    //@ConfigEntry.Gui.Tooltip
    //@Comment("Allows tool cycling using keybinding only (Default Z) (Do not change to false - it won't work)")
    //public boolean disableScrollWheel = true;

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