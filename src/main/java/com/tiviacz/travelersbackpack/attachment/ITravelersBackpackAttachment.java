package com.tiviacz.travelersbackpack.attachment;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ITravelersBackpackAttachment {
    boolean hasBackpack();

    ItemStack getBackpack();

    void updateBackpack(ItemStack stack, Player player);

    void applyComponents(DataComponentMap map);

    void equipBackpack(ItemStack stack, Player player);

    void removeWearable();

    void removeWrapper();

    void remove(Player player);

    BackpackWrapper getWrapper();

    void synchronise(Player player);

    void synchronise(DataComponentMap map, Player player);
}
