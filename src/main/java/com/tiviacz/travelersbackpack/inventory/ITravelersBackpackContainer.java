package com.tiviacz.travelersbackpack.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;

public interface ITravelersBackpackContainer extends ITanks
{
    void saveItems(CompoundTag compound);

    void loadItems(CompoundTag compound);

    void saveTime(CompoundTag compound);

    void loadTime(CompoundTag compound);

    void saveColor(CompoundTag compound);

    void loadColor(CompoundTag compound);

    void saveAllData(CompoundTag compound);

    void loadAllData(CompoundTag compound);

    CompoundTag getTagCompound(ItemStack stack);

    int getColor();

    boolean hasColor();

    boolean hasBlockEntity();

    boolean isSleepingBagDeployed();

    ItemStackHandler getHandler();

    ItemStackHandler getCraftingGridHandler();

    BlockPos getPosition();

    ItemStack removeItem(int index, int count);

    int getLastTime();

    void setLastTime(int time);

    Level getLevel();

    byte getScreenID();

    ItemStack getItemStack();

    void setChanged();
}