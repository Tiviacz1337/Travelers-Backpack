package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.nbt.CompoundNBT;

public class SettingsManager
{
    private final ITravelersBackpackInventory inventory;
    private byte[] craftingSettings = new byte[]{1, 1};

    public static final byte CRAFTING = 0;

    public static final int LOCK_CRAFTING_GRID = 0;
    public static final int RENDER_OVERLAY = 1;

    private final String CRAFTING_SETTINGS = "CraftingSettings";

    public SettingsManager(ITravelersBackpackInventory inventory)
    {
        this.inventory = inventory;
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
        setChanged();
    }

    public void saveSettings(CompoundNBT compound)
    {
        compound.putByteArray(CRAFTING_SETTINGS, craftingSettings);
    }

    public void loadSettings(CompoundNBT compound)
    {
        this.craftingSettings = compound.contains(CRAFTING_SETTINGS) ? compound.getByteArray(CRAFTING_SETTINGS) : new byte[]{1, 1};
    }

    public void setChanged()
    {
        if(inventory.getScreenID() != Reference.TILE_SCREEN_ID)
        {
            inventory.setDataChanged(ITravelersBackpackInventory.SETTINGS_DATA);
        }
        else
        {
            inventory.setDataChanged();
        }
    }
}