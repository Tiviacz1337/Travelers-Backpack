package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public interface ITravelersBackpackInventory extends ITanks
{
    void saveItems(CompoundNBT compound);

    void loadItems(CompoundNBT compound);

    void saveColor(CompoundNBT compound);

    void loadColor(CompoundNBT compound);

    void saveAbility(CompoundNBT compound);

    void loadAbility(CompoundNBT compound);

    void saveTime(CompoundNBT compound);

    void loadTime(CompoundNBT compound);

    void saveAllData(CompoundNBT compound);

    void loadAllData(CompoundNBT compound);

    boolean hasColor();

    int getColor();

    boolean getAbilityValue();

    void setAbility(boolean value);

    int getLastTime();

    void setLastTime(int time);

    CompoundNBT getTagCompound(ItemStack stack);

    boolean hasTileEntity();

    boolean isSleepingBagDeployed();

    ItemStackHandler getInventory();

    ItemStackHandler getCraftingGridInventory();

    SlotManager getSlotManager();

    ItemStack decrStackSize(int index, int count);

    World getLevel();

    BlockPos getPosition();

    byte getScreenID();

    ItemStack getItemStack();

    void setUsingPlayer(PlayerEntity player);

    byte INVENTORY_DATA = 0;
    byte CRAFTING_INVENTORY_DATA = 1;
    byte COMBINED_INVENTORY_DATA = 2;
    byte TANKS_DATA = 3;
    byte COLOR_DATA = 4;
    byte ABILITY_DATA = 5;
    byte LAST_TIME_DATA = 6;
    byte SLOT_DATA = 7;
    byte ALL_DATA = 8;

    void setDataChanged(byte... dataIds);

    void setChanged();
}