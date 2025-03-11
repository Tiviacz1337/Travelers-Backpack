package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.items.HoseItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ToolSlotItemHandler extends SlotItemHandler {
    private final BackpackWrapper wrapper;
    public static final List<Item> TOOL_SLOTS_ACCEPTABLE_ITEMS = new ArrayList<>();

    public ToolSlotItemHandler(BackpackWrapper wrapper, int index, int xPosition, int yPosition) {
        super(wrapper.getTools(), index, xPosition, yPosition);
        this.wrapper = wrapper;
    }

    @Override
    public boolean isActive() {
        return this.wrapper.showToolSlots();
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
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

        if(stack.getItem() instanceof TieredItem ||
                stack.getItem() instanceof HoeItem ||
                stack.getItem() instanceof FishingRodItem ||
                stack.getItem() instanceof ShearsItem ||
                stack.getItem() instanceof FlintAndSteelItem ||
                stack.getItem() instanceof ProjectileWeaponItem ||
                stack.getItem() instanceof BrushItem ||
                stack.getItem() instanceof TridentItem ||
                stack.getItem() instanceof MaceItem ||
                stack.getItem() instanceof SpyglassItem) {
            return true;
        }
        return stack.has(DataComponents.TOOL);
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }
}