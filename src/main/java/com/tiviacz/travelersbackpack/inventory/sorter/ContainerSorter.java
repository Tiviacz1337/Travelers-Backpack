package com.tiviacz.travelersbackpack.inventory.sorter;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

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
    public static final byte SORT = 4;
    public static final byte MEMORY = 5;

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
        else if(button == SORT)
        {
            setUnsortable(container, player, shiftPressed);
        }
        else if(button == MEMORY)
        {
            setMemory(container, player, shiftPressed);
        }
    }

    public static void sortBackpack(ITravelersBackpackContainer container, Player player, SortType.Type type, boolean shiftPressed)
    {
        if(!container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE))
        {
            List<ItemStack> stacks = new ArrayList<>();

            CustomWrapper wrapper = new CustomWrapper(container, container.getHandler());

            for(int i = 0; i < wrapper.getSlots(); i++)
            {
                addStackWithMerge(stacks, container.getSlotManager().isSlot(SlotManager.UNSORTABLE, i) ? ItemStack.EMPTY : wrapper.getStackInSlot(i));
            }

            if(!stacks.isEmpty())
            {
                stacks.sort(Comparator.comparing(stack -> SortType.getStringForSort(stack, type)));
            }

            if(stacks.isEmpty()) return;

            int j = 0;

            for(int i = 0; i < wrapper.getSlots(); i++)
            {
                if(container.getSlotManager().isSlot(SlotManager.UNSORTABLE, i)) continue;

                wrapper.setStackInSlot(i, j < stacks.size() ? stacks.get(j) : ItemStack.EMPTY);
                j++;
            }

            container.setDataChanged(ITravelersBackpackContainer.INVENTORY_DATA);
        }
    }

    public static void quickStackToBackpackNoSort(ITravelersBackpackContainer container, Player player, boolean shiftPressed)
    {
        IItemHandler playerStacks = new InvWrapper(player.getInventory());

        for(int i = shiftPressed ? 0 : 9; i < 36; ++i)
        {
            ItemStack playerStack = playerStacks.getStackInSlot(i);
            if(playerStack.isEmpty() || (container.getScreenID() == Reference.ITEM_SCREEN_ID && i == player.getInventory().selected)) continue;

            CustomWrapper wrapper = new CustomWrapper(container, container.getHandler());

            boolean hasExistingStack = IntStream.range(0, wrapper.getSlots()).mapToObj(wrapper::getStackInSlot).filter(existing -> !existing.isEmpty()).anyMatch(existing -> existing.getItem() == playerStack.getItem());
            if(!hasExistingStack) continue;

            ItemStack ext = playerStacks.extractItem(i, Integer.MAX_VALUE, false);

            for(int j = 0; j < wrapper.getSlots(); ++j)
            {
                ext = wrapper.insertItem(j, ext, false);
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

        //Run for Memory Slots
        if(!container.getSlotManager().getMemorySlots().isEmpty())
        {
            for(Pair<Integer, ItemStack> pair : container.getSlotManager().getMemorySlots())
            {
                for(int i = shiftPressed ? 0 : 9; i < 36; ++i)
                {
                    ItemStack playerStack = playerStacks.getStackInSlot(i);

                    if(playerStack.isEmpty() || (container.getScreenID() == Reference.ITEM_SCREEN_ID && i == player.getInventory().selected)) continue;
                    CustomWrapper wrapper = new CustomWrapper(container, container.getHandler());

                    ItemStack extSimulate = playerStacks.extractItem(i, Integer.MAX_VALUE, true);
                    ItemStack ext = ItemStack.EMPTY; //playerStacks.extractItem(i, Integer.MAX_VALUE, false);

                    if(ItemStackUtils.isSameItemSameTags(pair.getSecond(), extSimulate))
                    {
                        ext = playerStacks.extractItem(i, Integer.MAX_VALUE, false);

                        ext = wrapper.insertItem(pair.getFirst(), ext, false);
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

            if(playerStack.isEmpty() || (container.getScreenID() == Reference.ITEM_SCREEN_ID && i == player.getInventory().selected)) continue;
            CustomWrapper wrapper = new CustomWrapper(container, container.getHandler());

            ItemStack ext = playerStacks.extractItem(i, Integer.MAX_VALUE, false);

            for(int j = 0; j < wrapper.getSlots(); ++j)
            {
                ext = wrapper.insertItem(j, ext, false);
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
        CustomWrapper wrapper = new CustomWrapper(container, container.getHandler());

        for(int i = 0; i < wrapper.getSlots(); ++i)
        {
            ItemStack stack = wrapper.getStackInSlot(i);

            if(stack.isEmpty()) continue;

            ItemStack ext = wrapper.extractItem(i, Integer.MAX_VALUE, false);

            for(int j = 9; j < 36; ++j)
            {
                ext = playerStacks.insertItem(j, ext, false);
                if(ext.isEmpty()) break;
            }

            if(!ext.isEmpty())
            {
                wrapper.isTransferToPlayer = true;
                wrapper.insertItem(i, ext, false);
                wrapper.isTransferToPlayer = false;
            }
        }
    }

    public static void setUnsortable(ITravelersBackpackContainer container, Player player, boolean shiftPressed)
    {
        container.getSlotManager().setSelectorActive(SlotManager.UNSORTABLE, !container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE));
    }

    public static void setMemory(ITravelersBackpackContainer container, Player player, boolean shiftPressed)
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
        return ItemStack.isSameItemSameTags(stack1, stack2);
    }

    public static class CustomWrapper implements IItemHandlerModifiable
    {
        public ITravelersBackpackContainer container;
        public ItemStackHandler parent;
        public boolean isTransferToPlayer;

        public CustomWrapper(ITravelersBackpackContainer container, ItemStackHandler parent)
        {
            this(container, parent, false);
        }

        public CustomWrapper(ITravelersBackpackContainer container, ItemStackHandler parent, boolean isTransferToPlayer)
        {
            this.container = container;
            this.parent = parent;
            this.isTransferToPlayer = isTransferToPlayer;
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack)
        {
            parent.setStackInSlot(slot, stack);
        }

        @Override
        public int getSlots()
        {
            return parent.getSlots();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot)
        {
            return parent.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
        {
            if(container.getSlotManager().isSlot(SlotManager.MEMORY, slot))
            {
                return container.getSlotManager().getMemorySlots().stream().noneMatch(pair -> pair.getFirst() == slot && ItemStackUtils.isSameItemSameTags(pair.getSecond(), stack)) && !isTransferToPlayer ? stack : parent.insertItem(slot, stack, simulate);
            }
            return container.getSlotManager().isSlot(SlotManager.UNSORTABLE, slot) ? stack : parent.insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return container.getSlotManager().isSlot(SlotManager.UNSORTABLE, slot) ? ItemStack.EMPTY : parent.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot)
        {
            return parent.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack)
        {
            return parent.isItemValid(slot, stack);
        }
    }
}