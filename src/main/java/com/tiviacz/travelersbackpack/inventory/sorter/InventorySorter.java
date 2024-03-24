package com.tiviacz.travelersbackpack.inventory.sorter;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.InventoryImproved;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

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
            //RangedWrapper rangedWrapper = new RangedWrapper(inventory, inventory.getSettingsManager().isCraftingGridLocked() ? inventory.getInventory() : inventory.getCombinedInventory(), 0, inventory.getSettingsManager().isCraftingGridLocked() ? inventory.getTier().getStorageSlots() : inventory.getTier().getStorageSlotsWithCrafting());
            //RangedWrapper rangedWrapper = new RangedWrapper(inventory, inventory.getCombinedInventory(), 0, inventory.getTier().getStorageSlotsWithCrafting());

            for(int i = 0; i < inventory.getInventory().size(); i++)
            {
                //if(inventory.getSettingsManager().isCraftingGridLocked() && inventory.getSlotManager().isSlot(SlotManager.CRAFTING, i)) continue;

                addStackWithMerge(stacks, inventory.getSlotManager().isSlot(SlotManager.UNSORTABLE, i) ? ItemStack.EMPTY : inventory.getInventory().getStack(i));
            }

            if(!stacks.isEmpty())
            {
                stacks.sort(Comparator.comparing(stack -> SortType.getStringForSort(stack, type)));
            }

            if(stacks.isEmpty()) return;

            int j = 0;

            for(int i = 0; i < inventory.getInventory().size(); i++)
            {
                if(inventory.getSlotManager().isSlot(SlotManager.UNSORTABLE, i)) continue;

                inventory.getInventory().setStack(i, j < stacks.size() ? stacks.get(j) : ItemStack.EMPTY);
                j++;

                //if(inventory.getSettingsManager().isCraftingGridLocked() && inventory.getSlotManager().isSlot(SlotManager.CRAFTING, i)) continue;

               // if(inventory.getSlotManager().isSlot(SlotManager.UNSORTABLE, i)) continue;

              //  rangedWrapper.setStack(i, j < stacks.size() ? stacks.get(j) : ItemStack.EMPTY);
               // j++;
            }
            //if(player.currentScreenHandler instanceof TravelersBackpackBaseScreenHandler screen)
            //{
            //    screen.onContentChanged(screen.craftMatrix);
           // }
            inventory.markDataDirty(ITravelersBackpackInventory.INVENTORY_DATA);
        }
    }

    public static void quickStackToBackpackNoSort(ITravelersBackpackInventory inventory, PlayerEntity player, boolean shiftPressed)
    {
        for(int i = shiftPressed ? 0 : 9; i < 36; ++i)
        {
            ItemStack playerStack = player.getInventory().getStack(i);
            if(playerStack.isEmpty() || !inventory.getInventory().isValid(0, playerStack) || (inventory.getScreenID() == Reference.ITEM_SCREEN_ID && i == player.getInventory().selectedSlot)) continue;
            //RangedWrapper rangedWrapper = new RangedWrapper(inventory, inventory.getSettingsManager().isCraftingGridLocked() ? inventory.getInventory() : inventory.getCombinedInventory(), 0, inventory.getSettingsManager().isCraftingGridLocked() ? inventory.getTier().getStorageSlots() : inventory.getTier().getStorageSlotsWithCrafting());

            boolean hasExistingStack = IntStream.range(0, inventory.getInventory().size()).mapToObj(inventory.getInventory()::getStack).filter(existing -> !existing.isEmpty()).anyMatch(existing -> existing.getItem() == playerStack.getItem());
            if(!hasExistingStack) continue;

            ItemStack ext = extractItem(inventory, player.getInventory(), i, Integer.MAX_VALUE, false);

            for(int j = 0; j < inventory.getInventory().size(); ++j)
            {
                ext = insertItem(inventory, inventory.getInventory(), j, ext, false);
                if(ext.isEmpty()) break;
            }

            if(!ext.isEmpty())
            {
                insertItem(inventory, player.getInventory(), i, ext, false);
            }
        }
    }

    public static void transferToBackpackNoSort(ITravelersBackpackInventory inventory, PlayerEntity player, boolean shiftPressed)
    {
        //Run for Memory Slots
        if(!inventory.getSlotManager().getMemorySlots().isEmpty())
        {
            for(Pair<Integer, ItemStack> pair : inventory.getSlotManager().getMemorySlots())
            {
                for(int i = shiftPressed ? 0 : 9; i < 36; ++i)
                {
                    ItemStack playerStack = player.getInventory().getStack(i);

                    if(playerStack.isEmpty() || (inventory.getScreenID() == Reference.ITEM_SCREEN_ID && i == player.getInventory().selectedSlot)) continue;
                    //RangedWrapper rangedWrapper = new RangedWrapper(inventory, inventory.getCombinedInventory(), 0, inventory.getTier().getStorageSlotsWithCrafting());

                    ItemStack extSimulate = extractItem(inventory, player.getInventory(), i, Integer.MAX_VALUE, true);

                    ItemStack ext = ItemStack.EMPTY; //playerStacks.extractItem(i, Integer.MAX_VALUE, false);

                    if(ItemStackUtils.canCombine(pair.getSecond(), extSimulate))
                    {
                        ext = extractItem(inventory, player.getInventory(), i, Integer.MAX_VALUE, false);

                        ext = insertItem(inventory, inventory.getInventory(), pair.getFirst(), ext, false);
                        if(ext.isEmpty()) continue;
                    }

                    if(!ext.isEmpty())
                    {
                        insertItem(inventory, player.getInventory(), i, ext, false);
                    }
                }
            }
        }

        //Run for Normal Slots
        for(int i = shiftPressed ? 0 : 9; i < 36; ++i)
        {
            ItemStack playerStack = player.getInventory().getStack(i);
            if(playerStack.isEmpty() || !inventory.getInventory().isValid(0, playerStack) || (inventory.getScreenID() == Reference.ITEM_SCREEN_ID && i == player.getInventory().selectedSlot)) continue;
            //RangedWrapper rangedWrapper = new RangedWrapper(inventory, inventory.getSettingsManager().isCraftingGridLocked() ? inventory.getInventory() : inventory.getCombinedInventory(), 0, inventory.getSettingsManager().isCraftingGridLocked() ? inventory.getTier().getStorageSlots() : inventory.getTier().getStorageSlotsWithCrafting());

            ItemStack ext = extractItem(inventory, player.getInventory(), i, Integer.MAX_VALUE, false);

            for(int j = 0; j < inventory.getInventory().size(); ++j)
            {
                ext = insertItem(inventory, inventory.getInventory(), j, ext, false);
                if(ext.isEmpty()) break;
            }

            if(!ext.isEmpty())
            {
                insertItem(inventory, player.getInventory(), i, ext, false);
            }
        }
    }

    public static void transferToPlayer(ITravelersBackpackInventory inventory, PlayerEntity player)
    {
        //RangedWrapper rangedWrapper = new RangedWrapper(inventory, inventory.getSettingsManager().isCraftingGridLocked() ? inventory.getInventory() : inventory.getCombinedInventory(), 0, inventory.getSettingsManager().isCraftingGridLocked() ? inventory.getTier().getStorageSlots() : inventory.getTier().getStorageSlotsWithCrafting());

        for(int i = 0; i < inventory.getInventory().size(); ++i)
        {
            ItemStack stack = inventory.getInventory().getStack(i);

            if(stack.isEmpty()) continue;

            ItemStack ext = extractItem(inventory, inventory.getInventory(), i, Integer.MAX_VALUE, false);

            for(int j = 9; j < 36; ++j)
            {
                ext = insertItem(inventory, player.getInventory(), j, ext, false);
                if(ext.isEmpty()) break;
            }

            if(!ext.isEmpty())
            {
                insertItem(inventory, inventory.getInventory(), i, ext, true);
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
        return ItemStack.canCombine(stack1, stack2);
        //#TODO check
    }

    public static ItemStack insertItem(ITravelersBackpackInventory inventory, Inventory target, int slot, @NotNull ItemStack stack, boolean isTransferToPlayer)
    {
        if(stack.isEmpty())
            return ItemStack.EMPTY;

        if(!target.isValid(slot, stack))
            return stack;

        if(target instanceof InventoryImproved && inventory.getSlotManager().isSlot(SlotManager.UNSORTABLE, slot)) return stack;

        if(target instanceof InventoryImproved && inventory.getSlotManager().isSlot(SlotManager.MEMORY, slot) && inventory.getSlotManager().getMemorySlots().stream().noneMatch(pair -> pair.getFirst() == slot && ItemStackUtils.canCombine(pair.getSecond(), stack)) && !isTransferToPlayer)
        {
            return stack;
        }
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

    public static ItemStack extractItem(ITravelersBackpackInventory inventory, Inventory target, int slot, int amount, boolean simulate)
    {
        if(amount == 0)
            return ItemStack.EMPTY;

        if(target instanceof InventoryImproved && inventory.getSlotManager().isSlot(SlotManager.UNSORTABLE, slot)) return ItemStack.EMPTY;
        //validateSlotIndex(slot);

        ItemStack existing = target.getStack(slot);

        if(existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxCount());

        if(existing.getCount() <= toExtract)
        {
            if(!simulate)
            {
                target.setStack(slot, ItemStack.EMPTY);
                target.markDirty();
                return existing;
            }
            else
            {
                return existing.copy();
            }
        }
        else
        {
            if(!simulate)
            {
                target.setStack(slot, copyStackWithSize(existing, existing.getCount() - toExtract));
                target.markDirty();
            }

            return copyStackWithSize(existing, toExtract);
        }
    }

    public static boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b)
    {
        if(a.isEmpty() || !ItemStack.areItemsEqual(a, b) || a.hasNbt() != b.hasNbt())
            return false;

        return !a.hasNbt() || a.getNbt().equals(b.getNbt());
    }

    public static ItemStack copyStackWithSize(@NotNull ItemStack itemStack, int size)
    {
        if(size == 0)
            return ItemStack.EMPTY;
        ItemStack copy = itemStack.copy();
        copy.setCount(size);
        return copy;
    }
}