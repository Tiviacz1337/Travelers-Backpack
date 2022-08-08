package com.tiviacz.travelersbackpack.compat.trinkets;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
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
        return TravelersBackpackConfig.trinketsIntegration;
    }

    @Override
    public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        return entity.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) ? TrinketEnums.DropRule.KEEP : TrinketEnums.DropRule.DESTROY;
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
}