package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface ITravelersBackpackContainer extends ITanks
{
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

    Level level();

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
                Optional<IFluidHandlerItem> container = FluidUtil.getFluidHandler(stack);

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