package com.tiviacz.travelersbackpack.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITravelersBackpackInventory extends ITanks
{
    void writeItems(NbtCompound compound);

    void readItems(NbtCompound compound);

    void writeColor(NbtCompound compound);

    void readColor(NbtCompound compound);

    void writeTime(NbtCompound compound);

    void readTime(NbtCompound compound);

    void writeAllData(NbtCompound compound);

    void readAllData(NbtCompound compound);

    boolean hasColor();

    int getColor();

    boolean hasTileEntity();

    boolean isSleepingBagDeployed();

    InventoryImproved getInventory();

    InventoryImproved getCraftingGridInventory();

    ItemStack decrStackSize(int index, int count);

    int getLastTime();

    void setLastTime(int time);

    byte getScreenID();

    World getWorld();

    BlockPos getPosition();

    ItemStack getItemStack();

    NbtCompound getTagCompound(ItemStack stack);

    void markDirty();
}
