package com.tiviacz.travelersbackpack.component;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.ItemStack;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.entity.RespawnableComponent;

public interface ITravelersBackpack extends ComponentV3, AutoSyncedComponent, RespawnableComponent {
    boolean hasBackpack();

    ItemStack getBackpack();

    void updateBackpack(ItemStack stack);

    void applyComponents(DataComponentMap map);

    void equipBackpack(ItemStack stack);

    void removeWearable();

    void removeWrapper();

    void remove();

    BackpackWrapper getWrapper();

    void synchronise();

    void synchronise(DataComponentMap map);
}