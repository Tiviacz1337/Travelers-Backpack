package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public interface ITravelersBackpackContainer extends ITanks
{
    void saveItems(CompoundTag compound);

    void loadItems(CompoundTag compound);

    void saveColor(CompoundTag compound);

    void loadColor(CompoundTag compound);

    void saveSleepingBagColor(CompoundTag compound);

    void loadSleepingBagColor(CompoundTag compound);

    void saveAbility(CompoundTag compound);

    void loadAbility(CompoundTag compound);

    void saveTime(CompoundTag compound);

    void loadTime(CompoundTag compound);

    void saveAllData(CompoundTag compound);

    void loadAllData(CompoundTag compound);

    boolean hasColor();

    int getColor();

    boolean hasSleepingBagColor();

    int getSleepingBagColor();

    boolean getAbilityValue();

    void setAbility(boolean value);

    int getLastTime();

    void setLastTime(int time);

    boolean hasBlockEntity();

    boolean isSleepingBagDeployed();

    ItemStackHandler getHandler();

    ItemStackHandler getCraftingGridHandler();

    ItemStackHandler getFluidSlotsHandler();

    IItemHandlerModifiable getCombinedHandler();

    SlotManager getSlotManager();

    SettingsManager getSettingsManager();

    Tiers.Tier getTier();

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
    byte SLEEPING_BAG_COLOR_DATA = 5;
    byte ABILITY_DATA = 6;
    byte LAST_TIME_DATA = 7;
    byte SLOT_DATA = 8;
    byte SETTINGS_DATA = 9;
    byte ALL_DATA = 10;

    void setDataChanged(byte... dataId);

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