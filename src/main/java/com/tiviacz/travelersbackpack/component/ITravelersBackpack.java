package com.tiviacz.travelersbackpack.component;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface ITravelersBackpack extends ComponentV3, AutoSyncedComponent {
    boolean hasBackpack();

    ItemStack getBackpack();

    void updateBackpack(ItemStack stack);

    void applyComponents(CompoundTag map);

    void equipBackpack(ItemStack stack);

    void removeWearable();

    void removeWrapper();

    void remove();

    BackpackWrapper getWrapper();

    void synchronise();

    void synchronise(CompoundTag map);
}