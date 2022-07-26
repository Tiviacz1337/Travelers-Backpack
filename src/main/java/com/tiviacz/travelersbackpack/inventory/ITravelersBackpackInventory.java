package com.tiviacz.travelersbackpack.inventory;

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

    void markLastTimeDirty();

    CompoundNBT getTagCompound(ItemStack stack);

    boolean hasTileEntity();

    boolean isSleepingBagDeployed();

    ItemStackHandler getInventory();

    ItemStackHandler getCraftingGridInventory();

    ItemStack decrStackSize(int index, int count);

    World getLevel();

    BlockPos getPosition();

    byte getScreenID();

    ItemStack getItemStack();

    void setChanged();
}