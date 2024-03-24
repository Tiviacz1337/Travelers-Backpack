package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITravelersBackpackInventory extends ITanks
{
    void writeItems(NbtCompound compound);

    void readItems(NbtCompound compound);

    void writeColor(NbtCompound compound);

    void readColor(NbtCompound compound);

    void writeSleepingBagColor(NbtCompound compound);

    void readSleepingBagColor(NbtCompound compound);

    void writeAbility(NbtCompound compound);

    void readAbility(NbtCompound compound);

    void writeTime(NbtCompound compound);

    void readTime(NbtCompound compound);

    void writeAllData(NbtCompound compound);

    void readAllData(NbtCompound compound);

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

    boolean hasTileEntity();

    boolean isSleepingBagDeployed();

    InventoryImproved getInventory();

    InventoryImproved getToolSlotsInventory();

    InventoryImproved getCraftingGridInventory();

    InventoryImproved getFluidSlotsInventory();

    Inventory getCombinedInventory();

    SlotManager getSlotManager();

    SettingsManager getSettingsManager();

    Tiers.Tier getTier();

    World getWorld();

    BlockPos getPosition();

    byte getScreenID();

    ItemStack getItemStack();

    void setUsingPlayer(PlayerEntity player);

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

    void markDataDirty(byte... dataIds);

    void markDirty();

    default InventoryImproved createTemporaryInventory()
    {
        return new InventoryImproved(4)
        {
            @Override
            public void markDirty()
            { //#todo check it
                markDataDirty(COMBINED_INVENTORY_DATA);
            }

            @Override
            public boolean isValid(int slot, ItemStack stack)
            {
                Storage<FluidVariant> storage = ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);

                if(slot == 1 || slot == 3)
                {
                    return false;
                }

                if(stack.getItem() == Items.POTION || stack.getItem() == Items.GLASS_BOTTLE || stack.getItem() == Items.MILK_BUCKET)
                {
                    return true;
                }

                return storage != null;
            }
        };
    }

    String TIER = "Tier";
    String INVENTORY = "Inventory";
    String TOOLS_INVENTORY = "ToolsInventory";
    String CRAFTING_INVENTORY = "CraftingInventory";
    String LEFT_TANK = "LeftTank";
    String LEFT_TANK_AMOUNT = "LeftTankAmount";
    String RIGHT_TANK = "RightTank";
    String RIGHT_TANK_AMOUNT = "RightTankAmount";
    String SLEEPING_BAG = "SleepingBag";
    String COLOR = "Color";
    String SLEEPING_BAG_COLOR = "SleepingBagColor";
    String ABILITY = "Ability";
    String LAST_TIME = "LastTime";
    String CUSTOM_NAME = "CustomName";
}