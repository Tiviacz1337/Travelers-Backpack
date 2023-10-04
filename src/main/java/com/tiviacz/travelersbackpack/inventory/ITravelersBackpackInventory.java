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
import net.minecraft.util.collection.DefaultedList;
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

    boolean hasTileEntity();

    boolean isSleepingBagDeployed();

    InventoryImproved getInventory();

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

    void markDataDirty(byte... dataIds);

    void markDirty();

    default InventoryImproved createTemporaryInventory()
    {
        return new InventoryImproved(DefaultedList.ofSize(4, ItemStack.EMPTY))
        {
            @Override
            public void markDirty()
            {
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
}