package com.tiviacz.travelersbackpack.inventory;

import net.minecraft.inventory.Inventories;
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

    void writeAbility(NbtCompound compound);

    void readAbility(NbtCompound compound);

    void writeTime(NbtCompound compound);

    void readTime(NbtCompound compound);

    void writeAllData(NbtCompound compound);

    void readAllData(NbtCompound compound);

    boolean hasColor();

    int getColor();

    boolean getAbilityValue();

    void setAbility(boolean value);

    int getLastTime();

    void setLastTime(int time);

    void markLastTimeDirty();

    NbtCompound getTagCompound(ItemStack stack);

    boolean hasTileEntity();

    boolean isSleepingBagDeployed();

    InventoryImproved getInventory();

    InventoryImproved getCraftingGridInventory();

    default ItemStack decrStackSize(int index, int count)
    {
        ItemStack itemstack = Inventories.splitStack(getInventory().getStacks(), index, count);

        if(!itemstack.isEmpty())
        {
            this.markDirty();
        }
        return itemstack;
    }

    World getWorld();

    BlockPos getPosition();

    byte getScreenID();

    ItemStack getItemStack();

    void markDirty();
}