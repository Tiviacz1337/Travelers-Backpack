package com.tiviacz.travelersbackpack.inventory.upgrades;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public abstract class FilterSettingsBase {
    protected List<ItemStack> filterItems;
    protected List<String> filterTags = new ArrayList<>();
    protected List<TagKey<Item>> tags = new ArrayList<>();
    protected List<Integer> filterSettings;
    protected ItemStackHandler storage;
    private final int slotLimit;

    public FilterSettingsBase(ItemStackHandler storage, List<ItemStack> items, List<Integer> filterSettings, int slotLimit) {
        this(storage, items, filterSettings, List.of(), slotLimit);
    }

    public FilterSettingsBase(ItemStackHandler storage, List<ItemStack> items, List<Integer> filterSettings, List<String> filterTags, int slotLimit) {
        this.filterItems = items;
        this.filterTags = filterTags;
        this.filterSettings = filterSettings;
        this.storage = storage;
        this.slotLimit = slotLimit;

        reloadItemTags();
    }

    public abstract boolean matchesFilter(@Nullable Player player, ItemStack stack);

    public List<Integer> getSettings() {
        return this.filterSettings;
    }

    public boolean isTagFilter() {
        return false;
    }

    public boolean compareModId(ItemStack stack, ItemStack other) {
        return stack.getItem().getCreatorModId(stack).equals(other.getItem().getCreatorModId(other));
    }

    public Stream<ItemStack> streamStorageContents() {
        List<ItemStack> arrayList = new ArrayList<>();
        for(int i = 0; i < storage.getSlots(); i++) {
            if(!storage.getStackInSlot(i).isEmpty()) {
                arrayList.add(storage.getStackInSlot(i));
            }
        }
        return arrayList.stream();
    }

    public void reloadItemTags() {
        this.tags.clear();
        if(this.filterTags != null) {
            this.filterTags.forEach(string -> {
                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, ResourceLocation.parse(string));
                if(!tags.contains(tagKey)) {
                    tags.add(tagKey);
                }
            });
        }
    }

    public void updateFilter(@Nullable List<ItemStack> items) {
        if(items == null) return;
        this.filterItems = items.stream().limit(this.slotLimit).filter(stack -> !stack.isEmpty()).toList();
    }

    public void updateFilterTags(List<String> tags) {
        if(tags == null) return;
        this.filterTags = new ArrayList<>(tags);
        reloadItemTags();
    }

    public void updateSettings(List<Integer> settings) {
        this.filterSettings = settings;
    }

    public List<ItemStack> getFilterItems() {
        return this.filterItems;
    }

    public List<String> getFilterTags() {
        return this.filterTags;
    }

    public List<String> getAddableTags() {
        List<String> addableTags = new ArrayList<>();
        ItemStack stack = ItemStack.EMPTY;
        if(filterItems != null && !filterItems.isEmpty()) {
            stack = filterItems.get(0);
        }
        if(!stack.isEmpty()) {
            stack.getTags().forEach(tag -> addableTags.add(tag.location().toString()));
        }
        addableTags.removeAll(this.filterTags);
        return addableTags;
    }
}