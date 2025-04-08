package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.InventoryActions;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class FluidSlotItemHandler extends UpgradeSlotItemHandler<TanksUpgrade> {
    private final int index;
    public BackpackWrapper wrapper;
    public Player player;
    public TanksUpgrade upgrade;

    public FluidSlotItemHandler(Player player, TanksUpgrade upgrade, BackpackWrapper wrapper, ItemStackHandler handler, int index, int xPosition, int yPosition) {
        super(upgrade, handler, index, xPosition, yPosition);
        this.wrapper = wrapper;
        this.index = index;
        this.player = player;
        this.upgrade = upgrade;

        //0 - left in
        //1 - left out
        //2 - right in
        //3 - right out
    }

    //Fix for buckets bug
    @Override
    public void set(@NotNull ItemStack stack) {
        super.set(stack);
        if(index == 0 || index == 2) {
            InventoryActions.transferContainerTank(upgrade, index == 0 ? upgrade.getLeftTank() : upgrade.getRightTank(), index);
        }
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