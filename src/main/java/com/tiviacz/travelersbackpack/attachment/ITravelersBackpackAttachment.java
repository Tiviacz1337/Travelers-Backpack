package com.tiviacz.travelersbackpack.attachment;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Preparation for removal of the required Cardinal Components dependency.
 * This class is not used in version 1.21.x.
 * Use the component ITravelersBackpack instead.
 * Currently, this class is only used for data transfer purposes.
 */
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