package com.tiviacz.travelersbackpack.compat.trinkets;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketEnums;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameRules;

public class TravelersBackpackTrinket implements Trinket
{
    @Override
    public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        return TravelersBackpackConfig.getConfig().backpackSettings.trinketsIntegration && !slot.inventory().containsAny(p -> p.getItem() instanceof TravelersBackpackItem);
    }

    @Override
    public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        if(TravelersBackpackConfig.getConfig().backpackSettings.backpackDeathPlace && !TravelersBackpack.isAnyGraveModInstalled())
        {
            if(entity.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY))
            {
                return TrinketEnums.DropRule.KEEP;
            }
            else
            {
                return TrinketEnums.DropRule.DESTROY;
            }
        }
        return TrinketEnums.DropRule.DEFAULT;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        if(entity instanceof PlayerEntity player)
        {
            ComponentUtils.getComponent(player).setContents(stack);
            ComponentUtils.getComponent(player).setWearable(stack);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        if(entity instanceof PlayerEntity player)
        {
            ComponentUtils.getComponent(player).removeWearable();
        }
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        if(entity instanceof PlayerEntity player)
        {
            TravelersBackpackInventory inventory = ComponentUtils.getComponent(player).getInventory();

            if(!ItemStack.canCombine(inventory.getItemStack(), TrinketsCompat.getTravelersBackpackTrinket(player)))
            {
                TrinketsCompat.getTravelersBackpackTrinket(player).setNbt(inventory.getItemStack().getOrCreateNbt());
            }
        }
    }
}