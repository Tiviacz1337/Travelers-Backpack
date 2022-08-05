package com.tiviacz.travelersbackpack.inventory.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

public class DisabledSlot extends Slot
{
    public DisabledSlot(Container container, int index, int xPosition, int yPosition)
    {
        super(container, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }
}