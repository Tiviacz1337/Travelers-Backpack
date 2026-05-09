package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.item.HoseItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ToolSlotItemHandler extends ResourceHandlerSlot {
    private final BackpackWrapper wrapper;
    public static final List<Item> TOOL_SLOTS_ACCEPTABLE_ITEMS = new ArrayList<>();

    public ToolSlotItemHandler(BackpackWrapper wrapper, int index, int xPosition, int yPosition) {
        super(wrapper.getTools(), (slot, resource, count) -> wrapper.getTools().set(slot, resource, count), index, xPosition, yPosition);
        this.wrapper = wrapper;
    }

    @Override
    public boolean isActive() {
        return this.wrapper.showToolSlots();
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return super.mayPlace(stack) && isActive();
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return super.mayPickup(playerIn) && isActive();
    }

    public static boolean isValid(ItemStack stack) {
        if(stack.getItem() instanceof HoseItem) return false;

        if(TravelersBackpackConfig.SERVER.backpackSettings.toolSlotsAcceptEverything.get()) {
            return BackpackSlotItemHandler.isItemValid(stack);
        }

        //Datapacks :D
        if(stack.is(ModTags.ACCEPTABLE_TOOLS)) return true;

        if(TOOL_SLOTS_ACCEPTABLE_ITEMS.contains(stack.getItem())) return true;

        if(stack.getItem() instanceof HoeItem ||
                stack.getItem() instanceof FishingRodItem ||
                stack.getItem() instanceof ShearsItem ||
                stack.getItem() instanceof FlintAndSteelItem ||
                stack.getItem() instanceof ProjectileWeaponItem ||
                stack.getItem() instanceof BrushItem ||
                stack.getItem() instanceof TridentItem ||
                stack.getItem() instanceof MaceItem ||
                stack.getItem() instanceof SpyglassItem ||
                stack.getItem() instanceof ShieldItem) {
            return true;
        }
        return stack.has(DataComponents.TOOL) || stack.has(DataComponents.WEAPON) || stack.getMaxStackSize() == 1;
    }
}