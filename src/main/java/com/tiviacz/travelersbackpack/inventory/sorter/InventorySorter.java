package com.tiviacz.travelersbackpack.inventory.sorter;

import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.InventoryImproved;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

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
    }

    public static void sortBackpack(ITravelersBackpackInventory inventory, PlayerEntity player, SortType.Type type, boolean shiftPressed)
    {
        if(shiftPressed)
        {
            inventory.getSlotManager().setActive(!inventory.getSlotManager().isActive());
        }
        else if(!inventory.getSlotManager().isActive())
        {
            List<ItemStack> stacks = new ArrayList<>();

            for(int i = 0; i < 39; i++)
            {
                addStackWithMerge(stacks, inventory.getSlotManager().hasSlot(i) ? ItemStack.EMPTY : inventory.getInventory().getStack(i));
            }

            if(!stacks.isEmpty())
            {
                stacks.sort(Comparator.comparing(stack -> SortType.getStringForSort(stack, type)));
            }

            if(stacks.size() == 0) return;

            int j = 0;

            for(int i = 0; i < 39; i++)
            {
                if(inventory.getSlotManager().hasSlot(i)) continue;

                inventory.getInventory().setStack(i, j < stacks.size() ? stacks.get(j) : ItemStack.EMPTY);
                j++;
            }
            inventory.markDataDirty(ITravelersBackpackInventory.INVENTORY_DATA);
        }
    }

    public static void quickStackToBackpackNoSort(ITravelersBackpackInventory inventory, PlayerEntity player, boolean shiftPressed)
    {
        for(int i = shiftPressed ? 0 : 9; i < 36; ++i)
        {
            ItemStack playerStack = player.inventory.getStack(i);
            if(playerStack.isEmpty() || !inventory.getInventory().isValid(0, playerStack) || (inventory.getScreenID() == Reference.ITEM_SCREEN_ID && i == player.inventory.selectedSlot)) continue;

            boolean hasExistingStack = IntStream.range(0, 39).mapToObj(inventory.getInventory()::getStack).filter(existing -> !existing.isEmpty()).anyMatch(existing -> existing.getItem() == playerStack.getItem());
            if(!hasExistingStack) continue;

            ItemStack ext = extractItem(inventory, player.inventory, i, Integer.MAX_VALUE);

            for(int j = 0; j < 39; ++j)
            {
                ext = insertItem(inventory, inventory.getInventory(), j, ext);
                if(ext.isEmpty()) break;
            }

            if(!ext.isEmpty())
            {
                insertItem(inventory, player.inventory, i, ext);
            }
        }
    }

    public static void transferToBackpackNoSort(ITravelersBackpackInventory inventory, PlayerEntity player, boolean shiftPressed)
    {
        for(int i = shiftPressed ? 0 : 9; i < 36; ++i)
        {
            ItemStack playerStack = player.inventory.getStack(i);
            if(playerStack.isEmpty() || !inventory.getInventory().isValid(0, playerStack) || (inventory.getScreenID() == Reference.ITEM_SCREEN_ID && i == player.inventory.selectedSlot)) continue;

            ItemStack ext = extractItem(inventory, player.inventory, i, Integer.MAX_VALUE);

            for(int j = 0; j < 39; ++j)
            {
                ext = insertItem(inventory, inventory.getInventory(), j, ext);
                if(ext.isEmpty()) break;
            }

            if(!ext.isEmpty())
            {
                insertItem(inventory, player.inventory, i, ext);
            }
        }
    }

    public static void transferToPlayer(ITravelersBackpackInventory inventory, PlayerEntity player)
    {
        for(int i = 0; i < 39; ++i)
        {
            ItemStack stack = inventory.getInventory().getStack(i);

            if(stack.isEmpty()) continue;

            ItemStack ext = extractItem(inventory, inventory.getInventory(), i, Integer.MAX_VALUE);

            for(int j = 9; j < 36; ++j)
            {
                ext = insertItem(inventory, player.inventory, j, ext);
                if(ext.isEmpty()) break;
            }

            if(!ext.isEmpty())
            {
                insertItem(inventory, inventory.getInventory(), i, ext);
            }
        }
    }

    private static void addStackWithMerge(List<ItemStack> stacks, ItemStack newStack)
    {
        if(newStack.isEmpty()) return;

        if(newStack.isStackable() && newStack.getCount() != newStack.getMaxCount())
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
        if(stack.getMaxCount() >= stack.getCount() + stack2.getCount())
        {
            stack.increment(stack2.getCount());
            stack2.setCount(0);
        }

        int maxInsertAmount = Math.min(stack.getMaxCount() - stack.getCount(), stack2.getCount());
        stack.increment(maxInsertAmount);
        stack2.decrement(maxInsertAmount);
    }

    private static boolean canMergeItems(ItemStack stack1, ItemStack stack2)
    {
        if(!stack1.isStackable() || !stack2.isStackable())
        {
            return false;
        }
        if(stack1.getCount() == stack2.getMaxCount() || stack2.getCount() == stack2.getMaxCount())
        {
            return false;
        }
        if(stack1.getItem() != stack2.getItem())
        {
            return false;
        }
        if(stack1.getDamage() != stack2.getDamage())
        {
            return false;
        }
        return ItemStack.areTagsEqual(stack1, stack2);
    }

    public static ItemStack insertItem(ITravelersBackpackInventory inventory, Inventory target, int slot, ItemStack stack)
    {
        if(stack.isEmpty())
            return ItemStack.EMPTY;

        if(!target.isValid(slot, stack))
            return stack;

        if(target instanceof InventoryImproved && inventory.getSlotManager().hasSlot(slot)) return stack;

        //validateSlotIndex(slot);

        ItemStack existing = target.getStack(slot);

        int limit = stack.getMaxCount(); //getStackLimit(slot, stack);

        if(!existing.isEmpty())
        {
            if(!canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if(limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if(existing.isEmpty())
        {
            target.setStack(slot, reachedLimit ? copyStackWithSize(stack, limit) : stack);
        }
        else
        {
            existing.increment(reachedLimit ? limit : stack.getCount());
        }
        target.markDirty();

        return reachedLimit ? copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    public static ItemStack extractItem(ITravelersBackpackInventory inventory, Inventory target, int slot, int amount)
    {
        if(amount == 0)
            return ItemStack.EMPTY;

        if(target instanceof InventoryImproved && inventory.getSlotManager().hasSlot(slot)) return ItemStack.EMPTY;
        //validateSlotIndex(slot);

        ItemStack existing = target.getStack(slot);

        if(existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxCount());

        if(existing.getCount() <= toExtract)
        {
            target.setStack(slot, ItemStack.EMPTY);
            target.markDirty();
            return existing;
        }
        else
        {
            target.setStack(slot, copyStackWithSize(existing, existing.getCount() - toExtract));
            target.markDirty();

            return copyStackWithSize(existing, toExtract);
        }
    }

    public static boolean canItemStacksStack(ItemStack a, ItemStack b)
    {
        if(a.isEmpty() || !a.isItemEqual(b) || a.hasTag() != b.hasTag())
            return false;

        return !a.hasTag() || a.getTag().equals(b.getTag());
    }

    public static ItemStack copyStackWithSize(ItemStack itemStack, int size)
    {
        if(size == 0)
            return ItemStack.EMPTY;
        ItemStack copy = itemStack.copy();
        copy.setCount(size);
        return copy;
    }
}