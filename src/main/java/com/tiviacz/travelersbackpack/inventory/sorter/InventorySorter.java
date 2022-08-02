package com.tiviacz.travelersbackpack.inventory.sorter;

import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
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
        List<ItemStack> stacks = new ArrayList<>();
        //RangedWrapper rangedWrapper = new RangedWrapper(inventory.getInventory(), 0, 39);

        for(int i = 0; i < 39; i++)
        {
            addStackWithMerge(stacks, inventory.getInventory().getStack(i));
        }

        if(!stacks.isEmpty())
        {
            stacks.sort(Comparator.comparing(stack -> SortType.getStringForSort(stack, type)));
        }

        if(stacks.size() == 0) return;

        for(int i = 0; i < 39; i++)
        {
            inventory.getInventory().setStack(i, i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY);
        }
        inventory.markDataDirty(ITravelersBackpackInventory.INVENTORY_DATA);

       /* if(shiftPressed)
        {
            IItemHandler playerInvWrapper = new InvWrapper(player.inventory);

            List<ItemStack> playerStacks = new ArrayList<>();

            for(int i = 9; i < 36; i++)
            {
                addStackWithMerge(playerStacks, playerInvWrapper.getStackInSlot(i));
            }

            if(!stacks.isEmpty())
            {
                stacks.sort(Comparator.comparing(stack -> SortType.getStringForSort(stack, type)));
            }

            if(stacks.size() == 0) return;

            for(int i = 9; i < 36; i++)
            {
                playerInvWrapper.insertItem(i, i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY, false);
            }
        } */
    }

    public static void quickStackToBackpackNoSort(ITravelersBackpackInventory inventory, PlayerEntity player, boolean shiftPressed)
    {
        PlayerInventoryStorage playerStacks = PlayerInventoryStorage.of(player);
        InventoryStorage inventoryStacks = InventoryStorage.of(inventory.getInventory(), null);
        CombinedStorage rangedStacks = getRangedSlots(inventoryStacks.getSlots(), 0, 39);

        for(int i = shiftPressed ? 0 : 9; i < 36; ++i)
        {
            ItemStack playerStack = playerStacks.getSlot(i).getResource().toStack();
            if(playerStack.isEmpty() || !inventory.getInventory().isValid(0, playerStack) || (inventory.getScreenID() == Reference.ITEM_SCREEN_ID && i == player.getInventory().selectedSlot)) continue;

            boolean hasExistingStack = IntStream.range(0, 39).mapToObj(inventory.getInventory()::getStack).filter(existing -> !existing.isEmpty()).anyMatch(existing -> existing.getItem() == playerStack.getItem());
            if(!hasExistingStack) continue;

            try(Transaction transaction = Transaction.openOuter())
            {
                long amount = StorageUtil.move(playerStacks, rangedStacks, f -> true, Long.MAX_VALUE, transaction);

                if(amount > 0) transaction.commit();
            }
        }
    }

    public static void transferToBackpackNoSort(ITravelersBackpackInventory inventory, PlayerEntity player, boolean shiftPressed)
    {
        PlayerInventoryStorage playerStacks = PlayerInventoryStorage.of(player);
        InventoryStorage inventoryStacks = InventoryStorage.of(inventory.getInventory(), null);
        CombinedStorage rangedStacks = getRangedSlots(inventoryStacks.getSlots(), 0, 39);

        for(int i = shiftPressed ? 0 : 9; i < 36; ++i)
        {
            ItemStack playerStack = playerStacks.getSlot(i).getResource().toStack();
            if(playerStack.isEmpty() || !inventory.getInventory().isValid(0, playerStack) || (inventory.getScreenID() == Reference.ITEM_SCREEN_ID && i == player.getInventory().selectedSlot)) continue;

            try(Transaction transaction = Transaction.openOuter())
            {
                long amount = StorageUtil.move(playerStacks, rangedStacks, f -> true, Long.MAX_VALUE, transaction);

                if(amount > 0) transaction.commit();
            }
        }
    }

    public static void transferToPlayer(ITravelersBackpackInventory inventory, PlayerEntity player)
    {
        PlayerInventoryStorage playerStacks = PlayerInventoryStorage.of(player);
        CombinedStorage rangedPlayerStacks = getRangedSlots(playerStacks.getSlots(), 0, 36);
        InventoryStorage inventoryStacks = InventoryStorage.of(inventory.getInventory(), null);
        CombinedStorage rangedStacks = getRangedSlots(inventoryStacks.getSlots(), 0, 39);

        for(int i = 0; i < rangedStacks.parts.size(); ++i)
        {
            ItemStack stack = inventoryStacks.getSlot(i).getResource().toStack();

            if(stack.isEmpty()) continue;

            try(Transaction transaction = Transaction.openOuter())
            {
                long amount = StorageUtil.move(rangedStacks, rangedPlayerStacks, f -> true, Long.MAX_VALUE, transaction);

                if(amount > 0) transaction.commit();
            }
        }
    }

    public static CombinedStorage getRangedSlots(List<SingleSlotStorage<ItemVariant>> slots, int startSlot, int endSlot)
    {
        return new CombinedStorage(slots.subList(startSlot, endSlot));
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
        return ItemStack.areNbtEqual(stack1, stack2);
    }
}