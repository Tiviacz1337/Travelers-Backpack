package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;

public interface ITravelersBackpackContainer extends ITanks
{
    void saveItems(CompoundTag compound);

    void loadItems(CompoundTag compound);

    void saveColor(CompoundTag compound);

    void loadColor(CompoundTag compound);

    void saveAbility(CompoundTag compound);

    void loadAbility(CompoundTag compound);

    void saveTime(CompoundTag compound);

    void loadTime(CompoundTag compound);

    void saveAllData(CompoundTag compound);

    void loadAllData(CompoundTag compound);

    boolean hasColor();

    int getColor();

    boolean getAbilityValue();

    void setAbility(boolean value);

    int getLastTime();

    void setLastTime(int time);

    CompoundTag getTagCompound(ItemStack stack);

    boolean hasBlockEntity();

    boolean isSleepingBagDeployed();

    ItemStackHandler getHandler();

    ItemStackHandler getCraftingGridHandler();

    SlotManager getSlotManager();

    ItemStack removeItem(int index, int count);

    Level getLevel();

    BlockPos getPosition();

    byte getScreenID();

    ItemStack getItemStack();

    void setUsingPlayer(Player player);

    byte INVENTORY_DATA = 0;
    byte CRAFTING_INVENTORY_DATA = 1;
    byte COMBINED_INVENTORY_DATA = 2;
    byte TANKS_DATA = 3;
    byte COLOR_DATA = 4;
    byte ABILITY_DATA = 5;
    byte LAST_TIME_DATA = 6;
    byte SLOT_DATA = 7;
    byte ALL_DATA = 8;

    void setDataChanged(byte... dataId);

    void setChanged();
}