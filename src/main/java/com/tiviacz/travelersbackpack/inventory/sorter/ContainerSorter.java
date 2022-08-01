package com.tiviacz.travelersbackpack.inventory.sorter;

import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class ContainerSorter
{
    public static final byte SORT_BACKPACK = 0;
    public static final byte QUICK_STACK = 1;
    public static final byte TRANSFER_TO_BACKPACK = 2;
    public static final byte TRANSFER_TO_PLAYER = 3;

    public static void selectSort(ITravelersBackpackContainer container, Player player, byte button, boolean shiftPressed)
    {
        if(button == SORT_BACKPACK)
        {
            sortBackpack(container, player, SortType.Type.CATEGORY, shiftPressed);
        }
        else if(button == QUICK_STACK)
        {
            quickStackToBackpackNoSort(container, player, shiftPressed);
        }
        else if(button == TRANSFER_TO_BACKPACK)
        {
            transferToBackpackNoSort(container, player, shiftPressed);
        }
        else if(button == TRANSFER_TO_PLAYER)
        {
            transferToPlayer(container, player);
        }
    }

    public static void sortBackpack(ITravelersBackpackContainer container, Player player, SortType.Type type, boolean shiftPressed)
    {
        List<ItemStack> stacks = new ArrayList<>();
        RangedWrapper rangedWrapper = new RangedWrapper(container.getHandler(), 0, 39);

        for(int i = 0; i < rangedWrapper.getSlots(); i++)
        {
            addStackWithMerge(stacks, rangedWrapper.getStackInSlot(i));
        }

        if(!stacks.isEmpty())
        {
            stacks.sort(Comparator.comparing(stack -> SortType.getStringForSort(stack, type)));
        }

        if(stacks.size() == 0) return;

        for(int i = 0; i < rangedWrapper.getSlots(); i++)
        {
            rangedWrapper.setStackInSlot(i, i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY);
        }
        container.setDataChanged(ITravelersBackpackContainer.INVENTORY_DATA);

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

    public static void quickStackToBackpackNoSort(ITravelersBackpackContainer container, Player player, boolean shiftPressed)
    {
        IItemHandler playerStacks = new InvWrapper(player.getInventory());

        for(int i = shiftPressed ? 0 : 9; i < 36; ++i)
        {
            ItemStack playerStack = playerStacks.getStackInSlot(i);
            if(playerStack.isEmpty() || (container.getScreenID() == Reference.ITEM_SCREEN_ID && i == player.getInventory().selected)) continue;
            RangedWrapper rangedWrapper = new RangedWrapper(container.getHandler(), 0, 39);

            boolean hasExistingStack = IntStream.range(0, container.getHandler().getSlots()).mapToObj(rangedWrapper::getStackInSlot).filter(existing -> !existing.isEmpty()).anyMatch(existing -> existing.getItem() == playerStack.getItem());
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

    public static void transferToBackpackNoSort(ITravelersBackpackContainer container, Player player, boolean shiftPressed)
    {
        IItemHandler playerStacks = new InvWrapper(player.getInventory());

        for(int i = shiftPressed ? 0 : 9; i < 36; ++i)
        {
            ItemStack playerStack = playerStacks.getStackInSlot(i);

            if(playerStack.isEmpty() || (container.getScreenID() == Reference.ITEM_SCREEN_ID && i == player.getInventory().selected)) continue;
            RangedWrapper rangedWrapper = new RangedWrapper(container.getHandler(), 0, 39);

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

    public static void transferToPlayer(ITravelersBackpackContainer container, Player player)
    {
        IItemHandler playerStacks = new InvWrapper(player.getInventory());
        RangedWrapper rangedWrapper = new RangedWrapper(container.getHandler(), 0, 39);

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
                rangedWrapper.insertItem(i, ext, false);
            }
        }
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
        return ItemStack.isSame(stack1, stack2);
    }
}