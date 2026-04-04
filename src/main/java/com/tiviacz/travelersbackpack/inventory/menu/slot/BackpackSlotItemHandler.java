package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

import java.util.ArrayList;
import java.util.List;

public class BackpackSlotItemHandler extends ResourceHandlerSlot {
    public static final List<Item> BLACKLISTED_ITEMS = new ArrayList<>();
    private final int containerIndex;

    public BackpackSlotItemHandler(ItemStacksResourceHandler handler, int index, int xPosition, int yPosition) {
        super(handler, handler::set, index, xPosition, yPosition);
        this.containerIndex = index;
    }

    public static boolean isItemValid(ItemStack stack) {
        if(BackpackSlotItemHandler.BLACKLISTED_ITEMS.contains(stack.getItem())) return false;

        return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.is(ModTags.BLACKLISTED_ITEMS) && (TravelersBackpackConfig.SERVER.backpackSettings.allowShulkerBoxes.get() || stack.getItem().canFitInsideContainerItems());
    }

    @Override
    public int getContainerSlot() {
        return this.containerIndex;
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return true;
    }
}