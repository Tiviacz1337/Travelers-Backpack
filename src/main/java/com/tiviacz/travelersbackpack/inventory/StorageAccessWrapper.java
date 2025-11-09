package com.tiviacz.travelersbackpack.inventory;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.inventory.transfer.QuadFunction;
import com.tiviacz.travelersbackpack.inventory.upgrades.voiding.VoidUpgrade;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Custom ItemStackHandler for Traveler's Backpack block entity interactions with hoppers, pipes etc. that respects unsortable and memory slots :)
 */
public class StorageAccessWrapper implements ResourceHandler<ItemResource> {
    private final BackpackWrapper wrapper;
    private final ResourceHandler<ItemResource> parent;

    public StorageAccessWrapper(BackpackWrapper wrapper, ResourceHandler<ItemResource> parent) {
        this.wrapper = wrapper;
        this.parent = parent;
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

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        return insert(wrapper, parent::insert, index, resource, amount, transaction);
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        return extract(wrapper, parent::extract, index, resource, amount, transaction);
    }

    public static int matchesStack(ItemStack inserted, Pair<Integer, Pair<ItemStack, Boolean>> memorizedStack) {
        if(memorizedStack.getSecond().getSecond()) {
            return ItemStackUtils.isSameItemSameTags(inserted, memorizedStack.getSecond().getFirst()) ? memorizedStack.getFirst() : -1;
        } else {
            return ItemStack.isSameItem(inserted, memorizedStack.getSecond().getFirst()) ? memorizedStack.getFirst() : -1;
        }
    }

    public static boolean tryVoiding(BackpackWrapper wrapper, ItemStack stack) {
        return wrapper.getUpgradeManager().getUpgrade(VoidUpgrade.class).map(voidUpgrade -> voidUpgrade.canVoid(stack)).orElse(false);
    }

    public static int insert(BackpackWrapper wrapper, QuadFunction<Integer, ItemResource, Integer, TransactionContext, Integer> inserter, int index, ItemResource resource, int amount, TransactionContext transaction) {
        //Voiding
        if(tryVoiding(wrapper, resource.toStack())) {
            return amount;
        }
        //Try inserting to memory slots first
        if(!wrapper.getMemorySlots().isEmpty()) {
            ItemStack stack = resource.toStack();
            for(Pair<Integer, Pair<ItemStack, Boolean>> memorizedStack : wrapper.getMemorySlots()) {
                if(memorizedStack.getSecond().getFirst().getItem() != stack.getItem()) {
                    continue;
                }
                int result = matchesStack(stack, memorizedStack);
                if(result != -1) {
                    return inserter.apply(result, resource, amount, transaction);
                }
            }
        }
        return wrapper.getUnsortableSlots().contains(index) ? 0 : inserter.apply(index, resource, amount, transaction);
    }

    public static int extract(BackpackWrapper wrapper, QuadFunction<Integer, ItemResource, Integer, TransactionContext, Integer> extractor, int index, ItemResource resource, int amount, TransactionContext transaction) {
        return wrapper.getUnsortableSlots().contains(index) ? 0 : extractor.apply(index, resource, amount, transaction);
    }
}