package com.tiviacz.travelersbackpack.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public final class BackpackContainerContents {
    public static final BackpackContainerContents EMPTY = new BackpackContainerContents(NonNullList.withSize(27, ItemStack.EMPTY));
    public static final Codec<BackpackContainerContents> CODEC = BackpackContainerContents.Slot.CODEC
            .sizeLimitedListOf(256)
            .xmap(BackpackContainerContents::fromSlots, BackpackContainerContents::asSlots);
    public static final StreamCodec<RegistryFriendlyByteBuf, BackpackContainerContents> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(256)).map
            (BackpackContainerContents::new, containerContents -> containerContents.items);

    private final NonNullList<ItemStack> items;
    private final int hashCode;

    private BackpackContainerContents(NonNullList<ItemStack> pItems) {
        if(pItems.size() > 256) {
            throw new IllegalArgumentException("Got " + pItems.size() + " items, but maximum is 256");
        } else {
            this.items = pItems;
            this.hashCode = ItemStack.hashStackList(pItems);
        }
    }

    public BackpackContainerContents(int pSize) {
        this(NonNullList.withSize(pSize, ItemStack.EMPTY));
    }

    //Only for Codec
    private BackpackContainerContents(List<ItemStack> stacks) {
        this(stacks.size());
        for(int i = 0; i < stacks.size(); i++) {
            this.items.set(i, stacks.get(i));
        }
    }

    public NonNullList<ItemStack> getItems() {
        return this.items; //#TODO change to copy?
    }

    private static BackpackContainerContents fromSlots(List<BackpackContainerContents.Slot> slots) {
        OptionalInt optionalint = slots.stream().mapToInt(BackpackContainerContents.Slot::index).max();
        if(optionalint.isEmpty()) {
            return EMPTY;
        } else {
            BackpackContainerContents contents = new BackpackContainerContents(optionalint.getAsInt() + 1);
            for(BackpackContainerContents.Slot slot : slots) {
                contents.items.set(slot.index(), slot.item());
            }
            return contents;
        }
    }

    public static BackpackContainerContents fromItems(int size, List<ItemStack> pItems) {
        BackpackContainerContents contents = new BackpackContainerContents(size);
        for(int j = 0; j < size; j++) {
            contents.items.set(j, pItems.get(j).copy());
        }
        return contents;
    }

    private List<BackpackContainerContents.Slot> asSlots() {
        List<BackpackContainerContents.Slot> list = new ArrayList<>();
        for(int i = 0; i < this.items.size(); i++) {
            ItemStack itemstack = this.items.get(i);
            list.add(new BackpackContainerContents.Slot(i, itemstack));
        }
        return list;
    }

    public static BackpackContainerContents updateSlot(BackpackContainerContents oldContents, BackpackContainerContents.Slot slot) {
        NonNullList<ItemStack> itemsCopy = NonNullList.withSize(oldContents.items.size(), ItemStack.EMPTY);
        oldContents.copyInto(itemsCopy);
        if(slot.index >= 0 && slot.index < itemsCopy.size()) {
            itemsCopy.set(slot.index, slot.item.copy());
        }
        return BackpackContainerContents.fromItems(itemsCopy.size(), itemsCopy);
    }

    public void copyInto(NonNullList<ItemStack> list) {
        for(int i = 0; i < list.size(); i++) {
            ItemStack itemstack = i < this.items.size() ? this.items.get(i) : ItemStack.EMPTY;
            list.set(i, itemstack.copy());
        }
    }

    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return this.items.get(slot).copy();
    }

    /**
     * Neo: Throws {@link UnsupportedOperationException} if the provided slot index is invalid.
     */
    private void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= getItems().size()) {
            throw new UnsupportedOperationException("Slot " + slot + " not in valid range - [0," + getItems().size() + ")");
        }
    }

    @Override
    public boolean equals(Object pOther) {
        if(this == pOther) {
            return true;
        } else {
            return pOther instanceof BackpackContainerContents contents && ItemStack.listMatches(this.items, contents.items);
        }
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    public record Slot(int index, ItemStack item) {
        public static final Codec<BackpackContainerContents.Slot> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                        Codec.intRange(0, 255).fieldOf("slot").forGetter(BackpackContainerContents.Slot::index),
                        ItemStack.OPTIONAL_CODEC.fieldOf("item").forGetter(BackpackContainerContents.Slot::item))
                .apply(inst, BackpackContainerContents.Slot::new)
        );
    }
}
