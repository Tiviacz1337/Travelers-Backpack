package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ToolSlotItemHandler extends SlotItemHandler
{
    private final Player player;
    private final ITravelersBackpackContainer container;

    public ToolSlotItemHandler(Player player, ITravelersBackpackContainer container, int index, int xPosition, int yPosition)
    {
        super(container.getHandler(), index, xPosition, yPosition);

        this.player = player;
        this.container = container;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack)
    {
        return isValid(stack);
    }

    public static boolean isValid(ItemStack stack)
    {
        //Datapacks :D
        ResourceLocation acceptableToolsTag = new ResourceLocation(TravelersBackpack.MODID, "acceptable_tools");
        if(stack.is(ItemTags.getAllTags().getTag(acceptableToolsTag))) return true;

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
            return stack.getItem() instanceof TieredItem || stack.getItem() instanceof HoeItem || stack.getItem() instanceof FishingRodItem || stack.getItem() instanceof ShearsItem || stack.getItem() instanceof FlintAndSteelItem;
        }
        return false;
    }

    @Override
    public void setChanged()
    {
        super.setChanged();

        if(container.getScreenID() == Reference.WEARABLE_SCREEN_ID)
        {
            CapabilityUtils.synchronise(this.player);
            CapabilityUtils.synchroniseToOthers(this.player);
        }
    }
}