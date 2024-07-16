package com.tiviacz.travelersbackpack.compat.trinkets;

import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class TrinketsCompat
{
    public static void init()
    {
        for(TravelersBackpackItem backpack : ModItems.BACKPACKS)
        {
            TrinketsApi.registerTrinket(backpack, new TravelersBackpackTrinket());
        }
    }

    public static void rightClickUnequip(PlayerEntity player, ItemStack stack)
    {
        TrinketsApi.getTrinketComponent(player).ifPresent(t -> t.forEach((slotReference, itemStack) ->
        {
            if(ItemStack.canCombine(stack, itemStack))
            {
                slotReference.inventory().clear();
            }
        }));
    }

    public static void rightClickEquip(PlayerEntity player, ItemStack stack)
    {
        TrinketItem.equipItem(player, stack);
    }
}