package com.tiviacz.travelersbackpack.inventory.sorter;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class InventorySorter
{
    public static final byte SORT_BACKPACK = 0;
    public static final byte QUICK_STACK = 1;
    public static final byte TRANSFER_TO_BACKPACK = 2;
    public static final byte TRANSFER_TO_PLAYER = 3;
    public static final byte SORT = 4;
    public static final byte MEMORY = 5;

    public static void selectSort(ITravelersBackpackInventory inventory, PlayerEntity player, byte button, boolean shiftPressed)
    {
        if(button == SORT_BACKPACK)
        {
            sortBackpack(inventory, player, SortType.Type.CATEGORY, shiftPressed);
        }
        else if(button == QUICK_STACK)
        {
            quickStackToBackpackNoSort(inventory, player, shiftPressed);
        }
        else if(button == TRANSFER_TO_BACKPACK)
        {
            transferToBackpackNoSort(inventory, player, shiftPressed);
        }
        else if(button == TRANSFER_TO_PLAYER)
        {
            transferToPlayer(inventory, player);
        }
        else if(button == SORT)
        {
            setUnsortable(inventory, player, shiftPressed);
        }
        else if(button == MEMORY)
        {
            setMemory(inventory, player, shiftPressed);
        }
    }

    public static void sortBackpack(ITravelersBackpackInventory inventory, PlayerEntity player, SortType.Type type, boolean shiftPressed)
    {
        if(!inventory.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE))
        {
            List<ItemStack> stacks = new ArrayList<>();
            CustomRangedWrapper rangedWrapper = new CustomRangedWrapper(inventory, inventory.getInventory(), 0, 39);

            for(int i = 0; i < rangedWrapper.getSlots(); i++)
            {
                addStackWithMerge(stacks, inventory.getSlotManager().isSlot(SlotManager.UNSORTABLE, i) ? ItemStack.EMPTY : rangedWrapper.getStackInSlot(i));
            }

            if(!stacks.isEmpty())
            {
                stacks.sort(Comparator.comparing(stack -> SortType.getStringForSort(stack, type)));
            }

            if(stacks.size() == 0) return;

            int j = 0;

            for(int i = 0; i < rangedWrapper.getSlots(); i++)
            {
                if(inventory.getSlotManager().isSlot(SlotManager.UNSORTABLE, i)) continue;

                rangedWrapper.setStackInSlot(i, j < stacks.size() ? stacks.get(j) : ItemStack.EMPTY);
                j++;
            }
            inventory.setDataChanged(ITravelersBackpackInventory.INVENTORY_DATA);
        }
    }

    public static void quickStackToBackpackNoSort(ITravelersBackpackInventory inventory, PlayerEntity player, boolean shiftPressed)
    {
        IItemHandler playerStacks = new InvWrapper(player.inventory);

        for(int i = shiftPressed ? 0 : 9; i < 36; ++i)
        {
            ItemStack playerStack = playerStacks.getStackInSlot(i);
            if(playerStack.isEmpty() || (inventory.getScreenID() == Reference.ITEM_SCREEN_ID && i == player.inventory.selected)) continue;
            CustomRangedWrapper rangedWrapper = new CustomRangedWrapper(inventory, inventory.getInventory(), 0, 39);

            boolean hasExistingStack = IntStream.range(0, inventory.getInventory().getSlots()).mapToObj(rangedWrapper::getStackInSlot).filter(existing -> !existing.isEmpty()).anyMatch(existing -> existing.getItem() == playerStack.getItem());
            if(!hasExistingStack) continue;

            ItemStack ext = playerStacks.extractItem(i, Integer.MAX_VALUE, false);

            for(int j = 0; j < rangedWrapper.getSlots(); ++j)
            {
                ext = rangedWrapper.insertItem(j, ext, false);
                if(ext.isEmpty()) break;
            }

            if(!ext.isEmpty())
            {
                playerStacks.insertItem(i, ext, false);
            }
        }
    }

    public static void transferToBackpackNoSort(ITravelersBackpackInventory inventory, PlayerEntity player, boolean shiftPressed)
    {
        IItemHandler playerStacks = new InvWrapper(player.inventory);

        //Run for Memory Slots
        if(!inventory.getSlotManager().getMemorySlots().isEmpty())
        {
            for(Pair<Integer, ItemStack> pair : inventory.getSlotManager().getMemorySlots())
            {
                for(int i = shiftPressed ? 0 : 9; i < 36; ++i)
                {
                    ItemStack playerStack = playerStacks.getStackInSlot(i);

                    if(playerStack.isEmpty() || (inventory.getScreenID() == Reference.ITEM_SCREEN_ID && i == player.inventory.selected)) continue;
                    CustomRangedWrapper rangedWrapper = new CustomRangedWrapper(inventory, inventory.getInventory(), 0, 39);

                    ItemStack extSimulate = playerStacks.extractItem(i, Integer.MAX_VALUE, true);
                    ItemStack ext = ItemStack.EMPTY; //playerStacks.extractItem(i, Integer.MAX_VALUE, false);

                    if(ItemStackUtils.isSameItemSameTags(pair.getSecond(), extSimulate))
                    {
                        ext = playerStacks.extractItem(i, Integer.MAX_VALUE, false);

                        ext = rangedWrapper.insertItem(pair.getFirst(), ext, false);
                        if(ext.isEmpty()) continue;
                    }

                    if(!ext.isEmpty())
                    {
                        playerStacks.insertItem(i, ext, false);
                    }
                }
            }
        }

        //Run for Normal Slots
        for(int i = shiftPressed ? 0 : 9; i < 36; ++i)
        {
            ItemStack playerStack = playerStacks.getStackInSlot(i);
            if(playerStack.isEmpty() || (inventory.getScreenID() == Reference.ITEM_SCREEN_ID && i == player.inventory.selected)) continue;
            CustomRangedWrapper rangedWrapper = new CustomRangedWrapper(inventory, inventory.getInventory(), 0, 39);

            ItemStack ext = playerStacks.extractItem(i, Integer.MAX_VALUE, false);

            for(int j = 0; j < rangedWrapper.getSlots(); ++j)
            {
                ext = rangedWrapper.insertItem(j, ext, false);
                if(ext.isEmpty()) break;
            }

            if(!ext.isEmpty())
            {
                playerStacks.insertItem(i, ext, false);
            }
        }
    }

    public static void transferToPlayer(ITravelersBackpackInventory inventory, PlayerEntity player)
    {
        IItemHandler playerStacks = new InvWrapper(player.inventory);
        CustomRangedWrapper rangedWrapper = new CustomRangedWrapper(inventory, inventory.getInventory(), 0, 39);

        for(int i = 0; i < rangedWrapper.getSlots(); ++i)
        {
            ItemStack stack = rangedWrapper.getStackInSlot(i);

            if(stack.isEmpty()) continue;

            ItemStack ext = rangedWrapper.extractItem(i, Integer.MAX_VALUE, false);

            for(int j = 9; j < 36; ++j)
            {
                ext = playerStacks.insertItem(j, ext, false);
                if(ext.isEmpty()) break;
            }

            if(!ext.isEmpty())
            {
                rangedWrapper.isTransferToPlayer = true;
                rangedWrapper.insertItem(i, ext, false);
                rangedWrapper.isTransferToPlayer = false;
            }
        }
    }

    public static void setUnsortable(ITravelersBackpackInventory container, PlayerEntity player, boolean shiftPressed)
    {
        container.getSlotManager().setSelectorActive(SlotManager.UNSORTABLE, !container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE));
    }

    public static void setMemory(ITravelersBackpackInventory container, PlayerEntity player, boolean shiftPressed)
    {
        container.getSlotManager().setSelectorActive(SlotManager.MEMORY, !container.getSlotManager().isSelectorActive(SlotManager.MEMORY));
    }

    private static void addStackWithMerge(List<ItemStack> stacks, ItemStack newStack)
    {
        if(newStack.isEmpty()) return;

        if(newStack.isStackable() && newStack.getCount() != newStack.getMaxStackSize())
        {
            for(int j = stacks.size() - 1; j >= 0; j--)
            {
                ItemStack oldStack = stacks.get(j);

                if(canMergeItems(newStack, oldStack))
                {
                    combineStacks(newStack, oldStack);

                    if(oldStack.isEmpty() || oldStack.getCount() == 0)
                    {
                        stacks.remove(j);
                    }
                }
            }
        }
        stacks.add(newStack);
    }

    private static void combineStacks(ItemStack stack, ItemStack stack2)
    {
        if(stack.getMaxStackSize() >= stack.getCount() + stack2.getCount())
        {
            stack.grow(stack2.getCount());
            stack2.setCount(0);
        }

        int maxInsertAmount = Math.min(stack.getMaxStackSize() - stack.getCount(), stack2.getCount());
        stack.grow(maxInsertAmount);
        stack2.shrink(maxInsertAmount);
    }

    private static boolean canMergeItems(ItemStack stack1, ItemStack stack2)
    {
        if(!stack1.isStackable() || !stack2.isStackable())
        {
            return false;
        }
        if(stack1.getCount() == stack2.getMaxStackSize() || stack2.getCount() == stack2.getMaxStackSize())
        {
            return false;
        }
        if(stack1.getItem() != stack2.getItem())
        {
            return false;
        }
        if(stack1.getDamageValue() != stack2.getDamageValue())
        {
            return false;
        }
        return ItemStack.tagMatches(stack1, stack2);
    }

    public static class CustomRangedWrapper extends RangedWrapper
    {
        private final ITravelersBackpackInventory inventory;
        public boolean isTransferToPlayer;

        public CustomRangedWrapper(ITravelersBackpackInventory inventory, IItemHandlerModifiable compose, int minSlot, int maxSlotExclusive)
        {
            this(inventory, compose, minSlot, maxSlotExclusive, false);
        }

        public CustomRangedWrapper(ITravelersBackpackInventory inventory, IItemHandlerModifiable compose, int minSlot, int maxSlotExclusive, boolean isTransferToPlayer)
        {
            super(compose, minSlot, maxSlotExclusive);
            this.inventory = inventory;
            this.isTransferToPlayer = isTransferToPlayer;
        }

        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            if(inventory.getSlotManager().isSlot(SlotManager.MEMORY, slot))
            {
                return inventory.getSlotManager().getMemorySlots().stream().noneMatch(pair -> pair.getFirst() == slot && ItemStackUtils.isSameItemSameTags(pair.getSecond(), stack)) && !isTransferToPlayer ? stack : super.insertItem(slot, stack, simulate);
            }
            return inventory.getSlotManager().isSlot(SlotManager.UNSORTABLE, slot) ? stack : super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return inventory.getSlotManager().isSlot(SlotManager.UNSORTABLE, slot) ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
        }
    }
}