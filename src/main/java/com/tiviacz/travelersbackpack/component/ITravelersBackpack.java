package com.tiviacz.travelersbackpack.component;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

//Named ITravelersBackpackComponent instead of ITravelersBackpack to keep compatibility with mods (like yigd)
public interface ITravelersBackpack extends ComponentV3, AutoSyncedComponent, ITravelersBackpackComponent {
    boolean hasBackpack();

    ItemStack getBackpack();

    void updateBackpack(ItemStack stack);

    void applyComponents(CompoundTag map);

    void equipBackpack(ItemStack stack);

    void removeBackpack();

    void removeWrapper();

    void remove();

    BackpackWrapper getWrapper();

    void synchronise();

    void synchronise(CompoundTag map);

    default ItemStack getWearable() {
        return getBackpack();
    }

    default void removeWearable() {
        removeBackpack();
        removeWrapper();
    }
}