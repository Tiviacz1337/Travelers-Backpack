package com.tiviacz.travelersbackpack.inventory;

import net.minecraft.entity.player.PlayerEntity;
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

    NbtCompound getTagCompound(ItemStack stack);

    boolean hasTileEntity();

    boolean isSleepingBagDeployed();

    InventoryImproved getInventory();

    InventoryImproved getCraftingGridInventory();

    ItemStack decrStackSize(int index, int count);

    World getWorld();

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
    byte ALL_DATA = 7;

    void markDataDirty(byte... dataIds);

    void markDirty();
}