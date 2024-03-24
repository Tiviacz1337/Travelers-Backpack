package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.nbt.NbtCompound;

public class SettingsManager
{
    private final ITravelersBackpackInventory inv;
    private byte[] craftingSettings = new byte[]{(byte)(TravelersBackpackConfig.getConfig().backpackSettings.craftingUpgradeByDefault ? 1 : 0), 0, 1};
    private byte[] toolSlotsSettings = new byte[] {0};

    public static final byte CRAFTING = 0;
    public static final byte TOOL_SLOTS = 1;

    public static final int HAS_CRAFTING_GRID = 0;
    public static final int SHOW_CRAFTING_GRID = 1;
    public static final int SHIFT_CLICK_TO_BACKPACK = 2;

    public static final int SHOW_TOOL_SLOTS = 0;

    public static final String CRAFTING_SETTINGS = "CraftingSettings";
    public static final String TOOL_SLOTS_SETTINGS = "ToolSlotsSettings";

    public SettingsManager(ITravelersBackpackInventory inv)
    {
        this.inv = inv;
    }

    public boolean hasCraftingGrid()
    {
        return getByte(CRAFTING, HAS_CRAFTING_GRID) == (byte)1;
    }

    public boolean shiftClickToBackpack()
    {
        return getByte(CRAFTING, SHIFT_CLICK_TO_BACKPACK) == (byte)1;
    }

    public boolean showCraftingGrid()
    {
        return getByte(CRAFTING, SHOW_CRAFTING_GRID) == (byte)1;
    }

    public boolean showToolSlots()
    {
        return getByte(TOOL_SLOTS, SHOW_TOOL_SLOTS) == (byte)1;
    }

    public byte getByte(byte dataArray, int place)
    {
        if(dataArray == CRAFTING)
        {
            return this.craftingSettings[place];
        }
        if(dataArray == TOOL_SLOTS)
        {
            return this.toolSlotsSettings[place];
        }
        return 0;
    }

    public void set(byte selectedDataArray, int place, byte value)
    {
        byte[] dataArray = new byte[0];

        if(selectedDataArray == CRAFTING) dataArray = this.craftingSettings;
        if(selectedDataArray == TOOL_SLOTS) dataArray = this.toolSlotsSettings;

        dataArray[place] = value;
        markDirty();
    }

    public void writeSettings(NbtCompound compound)
    {
        compound.putByteArray(CRAFTING_SETTINGS, craftingSettings);
        compound.putByteArray(TOOL_SLOTS_SETTINGS, toolSlotsSettings);
    }

    public void readSettings(NbtCompound compound)
    {   //#TODO clean up in 1.21
        this.craftingSettings = compound.contains(CRAFTING_SETTINGS) ? (compound.getByteArray(CRAFTING_SETTINGS).length == 3 ? compound.getByteArray(CRAFTING_SETTINGS) : new byte[]{(byte)1, 0, 1}) : new byte[]{(byte)(TravelersBackpackConfig.getConfig().backpackSettings.craftingUpgradeByDefault ? 1 : 0), 0, 1};
        this.toolSlotsSettings = compound.contains(TOOL_SLOTS_SETTINGS) ? compound.getByteArray(TOOL_SLOTS_SETTINGS) : new byte[] {0};
    }

    public void markDirty()
    {
        if(inv.getScreenID() != Reference.BLOCK_ENTITY_SCREEN_ID)
        {
            inv.markDataDirty(ITravelersBackpackInventory.SETTINGS_DATA);
        }
        else
        {
            inv.markDirty();
        }
    }

    public void loadDefaults()
    {
        this.craftingSettings = new byte[]{(byte)(TravelersBackpackConfig.getConfig().backpackSettings.craftingUpgradeByDefault ? 1 : 0), 0, 1};
        this.toolSlotsSettings = new byte[] {0};
        markDirty();
    }
}