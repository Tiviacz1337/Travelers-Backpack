package com.tiviacz.travelersbackpack.compat.trinkets;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketEnums;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class TravelersBackpackTrinket implements Trinket
{
    @Override
    public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        return TravelersBackpackConfig.getConfig().backpackSettings.trinketsIntegration;
    }

    @Override
    public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        return TrinketEnums.DropRule.DEFAULT;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        if(!TravelersBackpackConfig.getConfig().backpackSettings.trinketsIntegration) return;

        if(entity instanceof PlayerEntity player)
        {
            if(!player.getWorld().isClient)
            {
                ComponentUtils.getComponent(player).setContents(TravelersBackpack.accessoriesLoaded ? stack : stack.copy());
                ComponentUtils.getComponent(player).setWearable(TravelersBackpack.accessoriesLoaded ? stack : stack.copy());
            }
            ComponentUtils.sync(player);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        if(!TravelersBackpackConfig.getConfig().backpackSettings.trinketsIntegration) return;

        if(entity instanceof PlayerEntity player)
        {
            if(!player.getWorld().isClient)
            {
                ComponentUtils.getComponent(player).removeWearable();
            }
            ComponentUtils.sync(player);
        }
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        if(!TravelersBackpackConfig.getConfig().backpackSettings.trinketsIntegration || entity.getWorld().isClient || TravelersBackpack.accessoriesLoaded) return;

        if(entity instanceof PlayerEntity player)
        {
            TravelersBackpackInventory inventory = ComponentUtils.getComponent(player).getInventory();

            if(!ItemStack.canCombine(inventory.getItemStack(), stack))
            {
                stack.setNbt(inventory.getItemStack().getNbt());
                this.onEquip(stack, slot, entity);
            }
        }
    }
}