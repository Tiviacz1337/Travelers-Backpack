package com.tiviacz.travelersbackpack.inventory.sorter;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.inventory.transfer.BackpackResourceHandler;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class ContainerSorter {
    public static final int SORT_BACKPACK = 0;
    public static final int QUICK_STACK = 1;
    public static final int TRANSFER_TO_BACKPACK = 2;
    public static final int TRANSFER_TO_PLAYER = 3;

    public static void selectSort(BackpackWrapper backpackWrapper, Player player, int button, boolean shiftPressed) {
        if(button == SORT_BACKPACK) {
            sortBackpack(backpackWrapper, player, backpackWrapper.getSortType(), shiftPressed);
        } else if(button == QUICK_STACK) {
            quickStackToBackpackNoSort(backpackWrapper, player, shiftPressed);
        } else if(button == TRANSFER_TO_BACKPACK) {
            transferToBackpackNoSort(backpackWrapper, player, shiftPressed);
        } else if(button == TRANSFER_TO_PLAYER) {
            transferToPlayer(backpackWrapper, player);
        }
    }

    public static void sortBackpack(BackpackWrapper backpackWrapper, Player player, SortSelector.SortType type, boolean shiftPressed) {
        if(shiftPressed) {
            backpackWrapper.setNextSortType();
        } else {
            List<ItemStack> stacks = new ArrayList<>();
            CustomResourceHandler storage = new CustomResourceHandler(backpackWrapper, backpackWrapper.getStorage());
            for(int i = 0; i < storage.getSlots(); i++) {
                addStackWithMerge(stacks, backpackWrapper.getUnsortableSlots().contains(i) ? ItemStack.EMPTY : storage.getStackInSlot(i));
            }
            if(!stacks.isEmpty()) {
                stacks.sort(SortSelector.getSortTypeComparator(stacks, type));
            }
            if(stacks.isEmpty()) return;
            int j = 0;
            for(int i = 0; i < storage.getSlots(); i++) {
                if(backpackWrapper.getUnsortableSlots().contains(i)) continue;
                storage.setStackInSlot(i, j < stacks.size() ? stacks.get(j) : ItemStack.EMPTY);
                j++;
            }
        }
    }

    public static void quickStackToBackpackNoSort(BackpackWrapper backpackWrapper, Player player, boolean shiftPressed) {
        ResourceHandler<ItemResource> playerStacks = VanillaContainerWrapper.of(player.getInventory());
        for(int i = shiftPressed ? 0 : 9; i < 36; ++i) {
            ItemStack playerStack = playerStacks.getResource(i).toStack(playerStacks.getAmountAsInt(i));
            if(playerStack.isEmpty() || (backpackWrapper.getScreenID() == Reference.ITEM_SCREEN_ID && i == (backpackWrapper.getBackpackSlotIndex() == -1 ? player.getInventory().getSelectedSlot() : backpackWrapper.getBackpackSlotIndex())))
                continue;
            CustomResourceHandler storage = new CustomResourceHandler(backpackWrapper, backpackWrapper.getStorage());
            boolean hasExistingStack = IntStream.range(0, storage.getSlots()).mapToObj(storage::getStackInSlot).filter(existing -> !existing.isEmpty()).anyMatch(existing -> existing.getItem() == playerStack.getItem());
            if(!hasExistingStack) continue;
            ItemStack ext = InventoryHelper.extractItem(playerStacks, i, Integer.MAX_VALUE, false); //playerStacks.extractItem(i, Integer.MAX_VALUE, false);
            for(int j = 0; j < storage.getSlots(); ++j) {
                ext = ItemUtil.insertItemReturnRemaining(storage, j, ext, false, null); //storage.insertItem(j, ext, false);
                if(ext.isEmpty()) break;
            }
            if(!ext.isEmpty()) {
                ItemUtil.insertItemReturnRemaining(playerStacks, i, ext, false, null); //playerStacks.insertItem(i, ext, false);
            }
        }
    }

    public static void transferToBackpackNoSort(BackpackWrapper backpackWrapper, Player player, boolean shiftPressed) {
        ResourceHandler<ItemResource> playerStacks = VanillaContainerWrapper.of(player.getInventory());
        //Run for Memory Slots
        if(!backpackWrapper.getMemorySlots().isEmpty()) {
            for(Pair<Integer, Pair<ItemStack, Boolean>> pair : backpackWrapper.getMemorySlots()) {
                for(int i = shiftPressed ? 0 : 9; i < 36; ++i) {
                    ItemStack playerStack = playerStacks.getResource(i).toStack(playerStacks.getAmountAsInt(i));//playerStacks.getStackInSlot(i);
                    if(playerStack.isEmpty() || (backpackWrapper.getScreenID() == Reference.ITEM_SCREEN_ID && i == (backpackWrapper.getBackpackSlotIndex() == -1 ? player.getInventory().getSelectedSlot() : backpackWrapper.getBackpackSlotIndex())))
                        continue;
                    CustomResourceHandler wrapper = new CustomResourceHandler(backpackWrapper, backpackWrapper.getStorage());
                    ItemStack extSimulate = InventoryHelper.extractItem(playerStacks, i, Integer.MAX_VALUE, true); //playerStacks.extractItem(i, Integer.MAX_VALUE, true);
                    ItemStack ext = ItemStack.EMPTY; //playerStacks.extractItem(i, Integer.MAX_VALUE, false);
                    if(pair.getSecond().getSecond() ? ItemStackUtils.isSameItemSameTags(pair.getSecond().getFirst(), extSimulate) : ItemStack.isSameItem(pair.getSecond().getFirst(), extSimulate)) {
                        ext = InventoryHelper.extractItem(playerStacks, i, Integer.MAX_VALUE, false); //playerStacks.extractItem(i, Integer.MAX_VALUE, false);
                        ext = ItemUtil.insertItemReturnRemaining(wrapper, pair.getFirst(), ext, false, null); //wrapper.insertItem(pair.getFirst(), ext, false);
                        if(ext.isEmpty()) continue;
                    }
                    if(!ext.isEmpty()) {
                        ItemUtil.insertItemReturnRemaining(playerStacks, i, ext, false, null);
                        //playerStacks.insertItem(i, ext, false);
                    }
                }
            }
        }

        //Run for Normal Slots
        for(int i = shiftPressed ? 0 : 9; i < 36; ++i) {
            ItemStack playerStack = playerStacks.getResource(i).toStack(playerStacks.getAmountAsInt(i));//playerStacks.getStackInSlot(i);
            if(playerStack.isEmpty() || (backpackWrapper.getScreenID() == Reference.ITEM_SCREEN_ID && i == (backpackWrapper.getBackpackSlotIndex() == -1 ? player.getInventory().getSelectedSlot() : backpackWrapper.getBackpackSlotIndex())))
                continue;
            CustomResourceHandler wrapper = new CustomResourceHandler(backpackWrapper, backpackWrapper.getStorage());
            ItemStack ext = InventoryHelper.extractItem(playerStacks, i, Integer.MAX_VALUE, false); //playerStacks.extractItem(i, Integer.MAX_VALUE, false);
            for(int j = 0; j < wrapper.getSlots(); ++j) {
                ext = ItemUtil.insertItemReturnRemaining(wrapper, j, ext, false, null); //wrapper.insertItem(j, ext, false);
                if(ext.isEmpty()) break;
            }
            if(!ext.isEmpty()) {
                ItemUtil.insertItemReturnRemaining(playerStacks, i, ext, false, null); //playerStacks.insertItem(i, ext, false);
            }
        }
    }

    public static void transferToPlayer(BackpackWrapper backpackWrapper, Player player) {
        ResourceHandler<ItemResource> playerStacks = VanillaContainerWrapper.of(player.getInventory());
        CustomResourceHandler wrapper = new CustomResourceHandler(backpackWrapper, backpackWrapper.getStorage());
        for(int i = 0; i < wrapper.getSlots(); ++i) {
            ItemStack stack = wrapper.getStackInSlot(i);
            if(stack.isEmpty()) continue;
            ItemStack ext = InventoryHelper.extractItem(wrapper, i, Integer.MAX_VALUE, false); //wrapper.extractItem(i, Integer.MAX_VALUE, false);
            for(int j = 9; j < 36; ++j) {
                ext = ItemUtil.insertItemReturnRemaining(playerStacks, j, ext, false, null); //playerStacks.insertItem(j, ext, false);
                if(ext.isEmpty()) break;
            }
            if(!ext.isEmpty()) {
                wrapper.isTransferToPlayer = true;
                ItemUtil.insertItemReturnRemaining(wrapper, i, ext, false, null); //wrapper.insertItem(i, ext, false);
                wrapper.isTransferToPlayer = false;
            }
        }
    }

    private static void addStackWithMerge(List<ItemStack> stacks, ItemStack newStack) {
        if(newStack.isEmpty()) return;
        if(newStack.isStackable() && newStack.getCount() != newStack.getMaxStackSize()) {
            for(int j = stacks.size() - 1; j >= 0; j--) {
                ItemStack oldStack = stacks.get(j);
                if(canMergeItems(newStack, oldStack)) {
                    combineStacks(newStack, oldStack);
                    if(oldStack.isEmpty() || oldStack.getCount() == 0) {
                        stacks.remove(j);
                    }
                }
            }
        }
        stacks.add(newStack);
    }

    private static void combineStacks(ItemStack stack, ItemStack stack2) {
        if(stack.getMaxStackSize() >= stack.getCount() + stack2.getCount()) {
            stack.grow(stack2.getCount());
            stack2.setCount(0);
        }
        int maxInsertAmount = Math.min(stack.getMaxStackSize() - stack.getCount(), stack2.getCount());
        stack.grow(maxInsertAmount);
        stack2.shrink(maxInsertAmount);
    }

    private static boolean canMergeItems(ItemStack stack1, ItemStack stack2) {
        if(!stack1.isStackable() || !stack2.isStackable()) {
            return false;
        }
        if(stack1.getCount() == stack2.getMaxStackSize() || stack2.getCount() == stack2.getMaxStackSize()) {
            return false;
        }
        if(stack1.getItem() != stack2.getItem()) {
            return false;
        }
        if(stack1.getDamageValue() != stack2.getDamageValue()) {
            return false;
        }
        return ItemStack.isSameItemSameComponents(stack1, stack2);
    }

    public static class CustomResourceHandler implements ResourceHandler<ItemResource> {
        public final BackpackWrapper wrapper;
        public final BackpackResourceHandler parent;
        public boolean isTransferToPlayer;

        public CustomResourceHandler(BackpackWrapper wrapper, BackpackResourceHandler parent) {
            this(wrapper, parent, false);
        }

        public CustomResourceHandler(BackpackWrapper wrapper, BackpackResourceHandler parent, boolean isTransferToPlayer) {
            this.wrapper = wrapper;
            this.parent = parent;
            this.isTransferToPlayer = isTransferToPlayer;
        }

        public ItemStack getStackInSlot(int slot) {
            return getResource(slot).toStack(getAmountAsInt(slot));
        }

        public int getSlots() {
            return size();
        }

        public void setStackInSlot(int slot, ItemStack stack) {
            parent.set(slot, ItemResource.of(stack), stack.getCount());
        }

        @Override
        public int size() {
            return parent.size();
        }

        @Override
        public ItemResource getResource(int index) {
            return parent.getResource(index);
        }

        @Override
        public long getAmountAsLong(int index) {
            return parent.getAmountAsLong(index);
        }

        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            return parent.getCapacityAsLong(index, resource);
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return parent.isValid(index, resource);
        }

        /*@Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if(wrapper.getMemorizedSlot(slot).isPresent()) {
                return wrapper.getMemorySlots().stream().noneMatch(pair -> {
                    if(pair.getSecond().getSecond()) {
                        return pair.getFirst() == slot && ItemStackUtils.isSameItemSameTags(pair.getSecond().getFirst(), stack);
                    } else {
                        return pair.getFirst() == slot && ItemStack.isSameItem(pair.getSecond().getFirst(), stack);
                    }
                }) ? stack : ItemUtil.insertItemReturnRemaining(parent, slot, stack, simulate, null);
            }
            return wrapper.getUnsortableSlots().contains(slot) ? stack : ItemUtil.insertItemReturnRemaining(parent, slot, stack, simulate, null);
        }*/

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
            if(wrapper.getMemorizedSlot(index).isPresent()) {
                return wrapper.getMemorySlots().stream().noneMatch(pair -> {
                    if(pair.getSecond().getSecond()) {
                        return pair.getFirst() == index && ItemStackUtils.isSameItemSameTags(pair.getSecond().getFirst(), resource.toStack());
                    } else {
                        return pair.getFirst() == index && ItemStack.isSameItem(pair.getSecond().getFirst(), resource.toStack());
                    }
                }) ? 0 : parent.insert(index, resource, amount, transaction);
            }
            return wrapper.getUnsortableSlots().contains(index) ? 0 : parent.insert(index, resource, amount, transaction);
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
            return wrapper.getUnsortableSlots().contains(index) ? 0 : parent.extract(index, resource, amount, transaction);
        }
    }

    /*public static class CustomWrapper implements IItemHandlerModifiable {
        public final BackpackWrapper wrapper;
        public final BackpackResourceHandler parent;
        public boolean isTransferToPlayer;

        public CustomWrapper(BackpackWrapper wrapper, BackpackResourceHandler parent) {
            this(wrapper, parent, false);
        }

        public CustomWrapper(BackpackWrapper wrapper, BackpackResourceHandler parent, boolean isTransferToPlayer) {
            this.wrapper = wrapper;
            this.parent = parent;
            this.isTransferToPlayer = isTransferToPlayer;
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            parent.setStackInSlot(slot, stack);
        }

        @Override
        public int getSlots() {
            return parent.getSlots();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return parent.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if(wrapper.getMemorizedSlot(slot).isPresent()) {
                return wrapper.getMemorySlots().stream().noneMatch(pair -> {
                    if(pair.getSecond().getSecond()) {
                        return pair.getFirst() == slot && ItemStackUtils.isSameItemSameTags(pair.getSecond().getFirst(), stack);
                    } else {
                        return pair.getFirst() == slot && ItemStack.isSameItem(pair.getSecond().getFirst(), stack);
                    }
                }) ? stack : ItemUtil.insertItemReturnRemaining(parent, slot, stack, simulate, null);
            }
            return wrapper.getUnsortableSlots().contains(slot) ? stack : ItemUtil.insertItemReturnRemaining(parent, slot, stack, simulate, null);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return wrapper.getUnsortableSlots().contains(slot) ? ItemStack.EMPTY : InventoryHelper.extractItem(parent, slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return parent.getCapacityAsInt(slot, ItemResource.EMPTY);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return parent.isValid(slot, ItemResource.of(stack));
        }
    } */
}