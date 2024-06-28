package com.tiviacz.travelersbackpack.inventory.screen.slot;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.screen.slot.Slot;

public class ToolSlot extends Slot
{
    private final PlayerEntity player;
    private final ITravelersBackpackInventory inventory;

    public ToolSlot(PlayerEntity player, ITravelersBackpackInventory inventoryIn, int index, int x, int y)
    {
        super(inventoryIn.getToolSlotsInventory(), index, x, y);

        this.player = player;
        this.inventory = inventoryIn;
    }

    @Override
    public boolean isEnabled()
    {
        return inventory.getSettingsManager().showToolSlots();
    }

    @Override
    public boolean canInsert(ItemStack stack)
    {
        return inventory.getToolSlotsInventory().isValid(getIndex(), stack) && isEnabled();
    }

    public static boolean isValid(ItemStack stack)
    {
        if(stack.getItem() instanceof HoseItem) return false;

        if(TravelersBackpackConfig.getConfig().backpackSettings.toolSlotsAcceptEverything)
        {
            return BackpackSlot.isValid(stack);
        }

        //Datapacks :D
        if(stack.isIn(ModTags.ACCEPTABLE_TOOLS)) return true;

        if(TravelersBackpackConfig.isToolAllowed(stack)) return true;

        if(stack.getMaxCount() == 1)
        {
            //Vanilla tools
            return stack.getItem() instanceof ToolItem ||
                    stack.getItem() instanceof HoeItem ||
                    stack.getItem() instanceof FishingRodItem ||
                    stack.getItem() instanceof ShearsItem ||
                    stack.getItem() instanceof FlintAndSteelItem ||
                    stack.getItem() instanceof RangedWeaponItem ||
                    stack.getItem() instanceof TridentItem;
        }
        return false;
    }

    @Override
    public void markDirty()
    {
        super.markDirty();

        if(inventory.getScreenID() == Reference.WEARABLE_SCREEN_ID && !player.world.isClient)
        {
            ComponentUtils.sync(this.player);
        }
    }
}