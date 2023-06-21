package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.nbt.CompoundTag;

public class SettingsManager
{
    private final ITravelersBackpackContainer container;
    private byte[] craftingSettings = new byte[]{1, 1};

    public static final byte CRAFTING = 0;

    public static final int LOCK_CRAFTING_GRID = 0;
    public static final int RENDER_OVERLAY = 1;

    private final String CRAFTING_SETTINGS = "CraftingSettings";

    public SettingsManager(ITravelersBackpackContainer container)
    {
        this.container = container;
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

    public void saveSettings(CompoundTag compound)
    {
        compound.putByteArray(CRAFTING_SETTINGS, craftingSettings);
    }

    public void loadSettings(CompoundTag compound)
    {
        this.craftingSettings = compound.contains(CRAFTING_SETTINGS) ? compound.getByteArray(CRAFTING_SETTINGS) : new byte[]{1, 1};
    }

    public void setChanged()
    {
        if(container.getScreenID() != Reference.BLOCK_ENTITY_SCREEN_ID)
        {
            container.setDataChanged(ITravelersBackpackContainer.SETTINGS_DATA);
        }
        else
        {
            container.setDataChanged();
        }
    }
}