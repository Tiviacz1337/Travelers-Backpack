package com.tiviacz.travelersbackpack.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class ContainerContentsHelper {
    public static NonNullList<ItemStack> getItems(ItemContainerContents contents, int defaultSize) {
        int size = Math.max((int)contents.allItemsCopyStream().count(), defaultSize);
        NonNullList<ItemStack> itemsList = NonNullList.withSize(size, ItemStack.EMPTY);
        contents.copyInto(itemsList);
        return itemsList;
    }

    public static ItemContainerContents updateStack(ItemContainerContents contents, int defaultSize, ItemStack stack, int index) {
        int size = Math.max((int)contents.allItemsCopyStream().count(), defaultSize);
        NonNullList<ItemStack> itemsList = NonNullList.withSize(size, ItemStack.EMPTY);
        contents.copyInto(itemsList);
        itemsList.set(index, stack);
        return ItemContainerContents.fromItems(itemsList);
    }
}