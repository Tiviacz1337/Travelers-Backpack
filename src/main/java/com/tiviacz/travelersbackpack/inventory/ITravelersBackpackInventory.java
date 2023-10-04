package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public interface ITravelersBackpackInventory extends ITanks
{
    void saveItems(CompoundNBT compound);

    void loadItems(CompoundNBT compound);

    void saveColor(CompoundNBT compound);

    void loadColor(CompoundNBT compound);

    void saveSleepingBagColor(CompoundNBT compound);

    void loadSleepingBagColor(CompoundNBT compound);

    void saveAbility(CompoundNBT compound);

    void loadAbility(CompoundNBT compound);

    void saveTime(CompoundNBT compound);

    void loadTime(CompoundNBT compound);

    void saveAllData(CompoundNBT compound);

    void loadAllData(CompoundNBT compound);

    boolean hasColor();

    int getColor();

    boolean hasSleepingBagColor();

    int getSleepingBagColor();

    boolean getAbilityValue();

    void setAbility(boolean value);

    int getLastTime();

    void setLastTime(int time);

    boolean hasTileEntity();

    boolean isSleepingBagDeployed();

    ItemStackHandler getInventory();

    ItemStackHandler getCraftingGridInventory();

    ItemStackHandler getFluidSlotsInventory();

    IItemHandlerModifiable getCombinedInventory();

    SlotManager getSlotManager();

    SettingsManager getSettingsManager();

    Tiers.Tier getTier();

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
    byte SLEEPING_BAG_COLOR_DATA = 5;
    byte ABILITY_DATA = 6;
    byte LAST_TIME_DATA = 7;
    byte SLOT_DATA = 8;
    byte SETTINGS_DATA = 9;
    byte ALL_DATA = 10;

    void setDataChanged(byte... dataIds);

    void setDataChanged();

    default ItemStackHandler createTemporaryHandler()
    {
        return new ItemStackHandler(4)
        {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack)
            {
                LazyOptional<IFluidHandlerItem> container = FluidUtil.getFluidHandler(stack);

                if(slot == 1 || slot == 3)
                {
                    return false;
                }

                if(stack.getItem() == Items.POTION || stack.getItem() == Items.GLASS_BOTTLE)
                {
                    return true;
                }
                return container.isPresent();
            }
        };
    }
}