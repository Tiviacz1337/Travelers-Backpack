package com.tiviacz.travelersbackpack.inventory.container.slot;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.List;

public class ToolSlotItemHandler extends SlotItemHandler
{
    private final PlayerEntity player;
    private final ITravelersBackpackInventory inventory;
    public static final List<Item> TOOL_SLOTS_ACCEPTABLE_ITEMS = new ArrayList<>();

    public ToolSlotItemHandler(PlayerEntity player, ITravelersBackpackInventory inventoryIn, int index, int xPosition, int yPosition)
    {
        super(inventoryIn.getInventory(), index, xPosition, yPosition);

        this.player = player;
        this.inventory = inventoryIn;
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return isValid(stack);
    }

    public static boolean isValid(ItemStack stack)
    {
        //Datapacks :D
        ResourceLocation acceptableToolsTag = new ResourceLocation(TravelersBackpack.MODID, "acceptable_tools");
        if(stack.getItem().is(ItemTags.getAllTags().getTag(acceptableToolsTag))) return true;

        if(TOOL_SLOTS_ACCEPTABLE_ITEMS.contains(stack.getItem())) return true;

        if(stack.getMaxStackSize() == 1)
        {
            if(TravelersBackpackConfig.toolSlotsAcceptSwords)
            {
                if(stack.getItem() instanceof SwordItem)
                {
                    return true;
                }
            }

            //Vanilla tools
            return stack.getItem() instanceof TieredItem || stack.getItem() instanceof ToolItem || stack.getItem() instanceof HoeItem || stack.getItem() instanceof FishingRodItem || stack.getItem() instanceof ShearsItem || stack.getItem() instanceof FlintAndSteelItem;
        }
        return false;
    }

    @Override
    public void setChanged()
    {
        super.setChanged();

        if(inventory.getScreenID() == Reference.WEARABLE_SCREEN_ID)
        {
            CapabilityUtils.synchronise(this.player);
            CapabilityUtils.synchroniseToOthers(this.player);
        }
    }
}