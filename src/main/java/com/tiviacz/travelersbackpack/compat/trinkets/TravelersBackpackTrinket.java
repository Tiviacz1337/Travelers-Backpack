package com.tiviacz.travelersbackpack.compat.trinkets;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackItemScreenHandler;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class TravelersBackpackTrinket implements Trinket
{
    public static void init()
    {
        Registries.ITEM.stream()
                .filter(item -> item instanceof TravelersBackpackItem)
                .forEach(item -> TrinketsApi.registerTrinket(item, new TravelersBackpackTrinket()));
    }

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
            if(player.currentScreenHandler instanceof TravelersBackpackItemScreenHandler) return;

            if(!player.getWorld().isClient)
            {
                ComponentUtils.getComponent(player).setContents(stack);
                ComponentUtils.getComponent(player).setWearable(stack);
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
            if(player.currentScreenHandler instanceof TravelersBackpackItemScreenHandler) return;

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
        if(!TravelersBackpackConfig.getConfig().backpackSettings.trinketsIntegration) return;

        if(entity instanceof PlayerEntity player)
        {
            if(player.currentScreenHandler instanceof TravelersBackpackItemScreenHandler || !ComponentUtils.isWearingBackpack(player)) return;

            TravelersBackpackInventory inventory = ComponentUtils.getComponent(player).getInventory();

            if(!ItemStack.areItemsAndComponentsEqual(inventory.getItemStack(), stack))
            {
                stack.applyChanges(inventory.getItemStack().getComponentChanges());
                //this.onEquip(stack, slot, entity);
            }
        }
    }
}