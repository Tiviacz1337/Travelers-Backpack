package com.tiviacz.travelersbackpackneo.inventory.menu.slot;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class FluidSlotItemHandler extends SlotItemHandler {
    private final int index;
    public BackpackWrapper wrapper;
    public Player player;
    public TanksUpgrade upgrade;

    public FluidSlotItemHandler(Player player, TanksUpgrade upgrade, BackpackWrapper wrapper, ItemStackHandler handler, int index, int xPosition, int yPosition) {
        super(handler, index, xPosition, yPosition);
        this.wrapper = wrapper;
        this.index = index;
        this.player = player;
        this.upgrade = upgrade;

        //0 - left in
        //1 - left out
        //2 - right in
        //3 - right out
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        if(upgrade.isTabOpened()) {
            if(index == 1 || index == 3) {
                return super.mayPickup(playerIn) && this.hasItem();
            }
            return super.mayPickup(playerIn);
        }
        return false;
    }

    @Override
    public boolean isActive() {
        return super.isActive() && upgrade.isTabOpened();
    }
}