package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ToolSlotItemHandler extends SlotItemHandler
{
    private final Player player;
    private final ITravelersBackpackContainer container;
    public static final List<Item> TOOL_SLOTS_ACCEPTABLE_ITEMS = new ArrayList<>();

    public ToolSlotItemHandler(Player player, ITravelersBackpackContainer container, int index, int xPosition, int yPosition)
    {
        super(container.getHandler(), index, xPosition, yPosition);

        this.player = player;
        this.container = container;
    }

    public boolean canAccessPlace()
    {
        return true;
    }

    public boolean canAccessPickup()
    {
        return true;
    }

    @Override
    public boolean isActive()
    {
        return container.getSettingsManager().showToolSlots();
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack)
    {
        return isValid(stack);
    }

    public static boolean isValid(ItemStack stack)
    {
        //Datapacks :D
        if(stack.is(ModTags.ACCEPTABLE_TOOLS)) return true;

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
            return stack.getItem() instanceof TieredItem || stack.getItem() instanceof HoeItem || stack.getItem() instanceof FishingRodItem || stack.getItem() instanceof ShearsItem || stack.getItem() instanceof FlintAndSteelItem || stack.getItem() instanceof ProjectileWeaponItem;
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