package com.tiviacz.travelersbackpack.inventory.container.slot;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraftforge.items.SlotItemHandler;

public class ToolSlotItemHandler extends SlotItemHandler
{
    private final PlayerEntity player;
    private final ITravelersBackpackInventory inventory;
    private static final String[] validToolNames = {
            "wrench", "hammer", "axe", "pickaxe", "hoe", "shovel", "grafter", "scoop", "crowbar", "mattock", "drill", "chisel", "cutter", "dirt", "disassembler", "tool"
    };

    public ToolSlotItemHandler(PlayerEntity player, ITravelersBackpackInventory inventoryIn, int index, int xPosition, int yPosition)
    {
        super(inventoryIn.getInventory(), index, xPosition, yPosition);

        this.player = player;
        this.inventory = inventoryIn;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return isValid(stack);
    }

    public static boolean isValid(ItemStack stack)
    {
        if(stack.getMaxStackSize() == 1)
        {
            if(TravelersBackpackConfig.COMMON.toolSlotsAcceptSwords.get())
            {
                if(stack.getItem() instanceof SwordItem)
                {
                    return true;
                }
            }

            for(String name : validToolNames)
            {
                if(stack.getItem().getTranslationKey().toLowerCase().contains(name))
                {
                    return true;
                }
            }

            //Vanilla tools
            return stack.getItem() instanceof ToolItem || stack.getItem() instanceof HoeItem || stack.getItem() instanceof FishingRodItem || stack.getItem() instanceof ShearsItem || stack.getItem() instanceof FlintAndSteelItem;
        }
        return false;
    }

    @Override
    public void onSlotChanged()
    {
        super.onSlotChanged();

        if(inventory.getScreenID() == Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID)
        {
            CapabilityUtils.synchronise(this.player);
            CapabilityUtils.synchroniseToOthers(this.player);
        }
    }
}