package com.tiviacz.travelersbackpack.inventory.transfer;

import com.google.common.base.Preconditions;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.StorageAccessWrapper;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemAccessItemHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class BackpackItemAccess extends ItemAccessItemHandler {
    protected final BackpackWrapper wrapper;

    public BackpackItemAccess(ItemAccess itemAccess, BackpackWrapper wrapper, DataComponentType<ItemContainerContents> component) {
        super(itemAccess, component, wrapper.getStorageSize());
        this.wrapper = wrapper;
        Preconditions.checkArgument(wrapper.getStorageSize() <= 256, "The max size of ItemContainerContents is 256 slots.");
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        return StorageAccessWrapper.insert(wrapper, super::insert, index, resource, amount, transaction);
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        return StorageAccessWrapper.extract(wrapper, super::extract, index, resource, amount, transaction);
    }
}