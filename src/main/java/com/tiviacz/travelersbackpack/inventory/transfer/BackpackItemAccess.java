package com.tiviacz.travelersbackpack.inventory.transfer;

import com.google.common.base.Preconditions;
import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.StorageAccessWrapper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.transfer.ItemAccessResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class BackpackItemAccess extends ItemAccessResourceHandler<ItemResource> {
    protected final Item validItem;
    protected final DataComponentType<BackpackContainerContents> component;
    protected final BackpackWrapper wrapper;

    public BackpackItemAccess(ItemAccess itemAccess, BackpackWrapper wrapper, DataComponentType<BackpackContainerContents> component) {
        super(itemAccess, wrapper.getStorageSize());
        // Store the current item, such that if the item changes later we don't return any stored content from it.
        this.wrapper = wrapper;
        this.validItem = itemAccess.getResource().getItem();
        this.component = component;
        Preconditions.checkArgument(wrapper.getStorageSize() <= /* ItemContainerContents.MAX_SIZE */ 256,
                "The max size of ItemContainerContents is 256 slots.");
    }

    /**
     * Retrieves the {@link ItemContainerContents} from the current resource of the item access.
     */
    protected BackpackContainerContents getContents(ItemResource accessResource) {
        return accessResource.getOrDefault(component, BackpackContainerContents.EMPTY);
    }

    /**
     * Retrieves a copy of a single stack from the underlying data component,
     * returning {@link ItemStack#EMPTY} if the component does not have a slot present.
     *
     * @param contents the existing contents
     * @param slot     the target slot
     * @return a copy of the stack in the target slot
     */
    protected ItemStack getStackFromContents(BackpackContainerContents contents, int slot) {
        return slot < contents.getItems().size() ? contents.getStackInSlot(slot) : ItemStack.EMPTY;
    }

    @Override
    protected ItemResource getResourceFrom(ItemResource accessResource, int index) {
        if (accessResource.is(validItem)) {
            return ItemResource.of(getStackFromContents(getContents(accessResource), index));
        } else {
            return ItemResource.EMPTY;
        }
    }

    @Override
    protected int getAmountFrom(ItemResource accessResource, int index) {
        if (accessResource.is(validItem)) {
            return getStackFromContents(getContents(accessResource), index).getCount();
        } else {
            return 0;
        }
    }

    @Override
    protected ItemResource update(ItemResource accessResource, int index, ItemResource newResource, int newAmount) {
        var contents = getContents(accessResource);
        // Ensure we don't truncate any data by taking the max of the number of slots we need to fit, and our desired size
        NonNullList<ItemStack> list = NonNullList.withSize(Math.max(contents.getItems().size(), size), ItemStack.EMPTY);
        contents.copyInto(list);
        list.set(index, newResource.toStack(newAmount));
        return accessResource.with(this.component, BackpackContainerContents.fromItems(list.size(), list));
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        // Any resource is valid, but we have to check that the item of the item access has not changed.
        return itemAccess.getResource().is(validItem);
    }

    @Override
    protected int getCapacity(int index, ItemResource resource) {
        return resource.isEmpty() ? Item.ABSOLUTE_MAX_STACK_SIZE : Math.min(resource.getMaxStackSize(), Item.ABSOLUTE_MAX_STACK_SIZE);
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