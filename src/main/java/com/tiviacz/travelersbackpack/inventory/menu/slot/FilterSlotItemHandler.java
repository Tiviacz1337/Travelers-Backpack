package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.inventory.transfer.BackpackResourceHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

import java.util.Optional;

public class FilterSlotItemHandler extends ResourceHandlerSlot {
    protected final UpgradeBase upgrade;
    protected final int activeSlotCount;
    protected final int index;

    public FilterSlotItemHandler(UpgradeBase upgrade, BackpackResourceHandler itemHandler, int index, int xPosition, int yPosition, int activeSlotCount) {
        super(itemHandler, itemHandler::set, index, xPosition, yPosition);
        this.upgrade = upgrade;
        this.activeSlotCount = activeSlotCount;
        this.index = index;
    }

    @Override
    public boolean isActive() {
        return upgrade.isTabOpened() && this.index < this.activeSlotCount;
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
        return upgrade.isTabOpened() && this.index < this.activeSlotCount && super.mayPlace(pStack);
    }

    @Override
    public boolean isFake() {
        return true;
    }

    @Override
    public boolean mayPickup(Player player) {
        return upgrade.getUpgradeManager().getWrapper().isOwner(player) && this.index < this.activeSlotCount;
    }

    @Override
    public Optional<ItemStack> tryRemove(int count, int decrement, Player player) {
        if(!this.mayPickup(player)) {
            return Optional.empty();
        }
        this.set(ItemStack.EMPTY);
        return Optional.empty();
    }

    @Override
    public ItemStack safeInsert(ItemStack stack, int increment) {
        if(!stack.isEmpty() && this.mayPlace(stack)) {
            this.set(stack.copyWithCount(1));
        }
        return stack;
    }
}