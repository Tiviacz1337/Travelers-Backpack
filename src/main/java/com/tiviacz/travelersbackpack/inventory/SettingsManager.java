package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.nbt.NbtCompound;

public class SettingsManager
{
    private final ITravelersBackpackInventory inv;
    private byte[] craftingSettings = new byte[]{1, 1};

    public static final byte CRAFTING = 0;

    public static final int LOCK_CRAFTING_GRID = 0;
    public static final int RENDER_OVERLAY = 1;

    private final String CRAFTING_SETTINGS = "CraftingSettings";

    public SettingsManager(ITravelersBackpackInventory inv)
    {
        this.inv = inv;
    }

    public boolean isCraftingGridLocked()
    {
        return getByte(CRAFTING, LOCK_CRAFTING_GRID) == (byte)1;
    }

    public boolean renderOverlay()
    {
        return getByte(CRAFTING, RENDER_OVERLAY) == (byte)1;
    }

    public byte getByte(byte dataArray, int place)
    {
        if(dataArray == CRAFTING)
        {
            return this.craftingSettings[place];
        }
        return 0;
    }

    public void set(byte selectedDataArray, int place, byte value)
    {
        byte[] dataArray = new byte[0];

        if(selectedDataArray == CRAFTING) dataArray = this.craftingSettings;

        dataArray[place] = value;
        markDirty();
    }

    public void writeSettings(NbtCompound compound)
    {
        compound.putByteArray(CRAFTING_SETTINGS, craftingSettings);
    }

    public void readSettings(NbtCompound compound)
    {
        this.craftingSettings = compound.contains(CRAFTING_SETTINGS) ? compound.getByteArray(CRAFTING_SETTINGS) : new byte[]{1, 1};
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
}