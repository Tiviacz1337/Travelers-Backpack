package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

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

    int getRows();

    int getYOffset();

    boolean hasBlockEntity();

    boolean isSleepingBagDeployed();

    ItemStackHandler getHandler();

    ItemStackHandler getToolSlotsHandler();

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
    byte TOOLS_DATA = 1;
    byte CRAFTING_INVENTORY_DATA = 2;
    byte COMBINED_INVENTORY_DATA = 3;
    byte TANKS_DATA = 4;
    byte COLOR_DATA = 5;
    byte SLEEPING_BAG_COLOR_DATA = 6;
    byte ABILITY_DATA = 7;
    byte LAST_TIME_DATA = 8;
    byte SLOT_DATA = 9;
    byte SETTINGS_DATA = 10;
    byte ALL_DATA = 11;

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

    String TIER = "Tier";
    String INVENTORY = "Inventory";
    String TOOLS_INVENTORY = "ToolsInventory";
    String CRAFTING_INVENTORY = "CraftingInventory";
    String LEFT_TANK = "LeftTank";
    String RIGHT_TANK = "RightTank";
    String SLEEPING_BAG = "SleepingBag";
    String COLOR = "Color";
    String SLEEPING_BAG_COLOR = "SleepingBagColor";
    String ABILITY = "Ability";
    String LAST_TIME = "LastTime";
    String CUSTOM_NAME = "CustomName";
}