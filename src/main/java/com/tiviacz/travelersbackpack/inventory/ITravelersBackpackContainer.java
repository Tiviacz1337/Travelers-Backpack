package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.util.ContainerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

    void markLastTimeDirty();

    CompoundTag getTagCompound(ItemStack stack);

    boolean hasBlockEntity();

    boolean isSleepingBagDeployed();

    ItemStackHandler getHandler();

    ItemStackHandler getCraftingGridHandler();

    default ItemStack removeItem(int index, int count)
    {
        ItemStack stack = ContainerUtils.removeItem(getHandler(), index, count);
        if(!stack.isEmpty())
        {
            this.setChanged();
        }
        return stack;
    }

    Level getLevel();

    BlockPos getPosition();

    byte getScreenID();

    ItemStack getItemStack();

    void setChanged();
}