package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Optional;

public class FilterSlotItemHandler extends SlotItemHandler {
    protected final UpgradeBase upgrade;
    protected final int activeSlotCount;

    public FilterSlotItemHandler(UpgradeBase upgrade, IItemHandler itemHandler, int index, int xPosition, int yPosition, int activeSlotCount) {
        super(itemHandler, index, xPosition, yPosition);
        this.upgrade = upgrade;
        this.activeSlotCount = activeSlotCount;
    }

    @Override
    public boolean isActive() {
        return upgrade.isTabOpened() && this.getContainerSlot() < this.activeSlotCount;
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
        return upgrade.isTabOpened() && this.getContainerSlot() < this.activeSlotCount && super.mayPlace(pStack);
    }

    @Override
    public boolean mayPickup(Player player) {
        return upgrade.getUpgradeManager().getWrapper().isOwner(player) && this.getContainerSlot() < this.activeSlotCount;
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