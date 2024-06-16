package com.tiviacz.travelersbackpack.compat.trinkets;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.Optional;

public class TrinketsCompat
{
    public static void init()
    {
        for(TravelersBackpackItem backpack : ModItems.BACKPACKS)
        {
            TrinketsApi.registerTrinket(backpack, new TravelersBackpackTrinket());
        }
    }

    public static ItemStack getTravelersBackpackTrinket(PlayerEntity player)
    {
        Optional<TrinketComponent> optional = TrinketsApi.getTrinketComponent(player);
        if(optional.isPresent())
        {
            TrinketComponent comp = optional.get();
            List<Pair<SlotReference, ItemStack>> matchingStacks = comp.getEquipped(stack -> stack.getItem() instanceof TravelersBackpackItem);
            if(!matchingStacks.isEmpty())
            {
                return matchingStacks.get(0).getRight();
            }
        }
        return ItemStack.EMPTY;
    }

    public static TravelersBackpackInventory getTrinketsTravelersBackpackInventory(PlayerEntity player)
    {
        return ComponentUtils.getComponent(player).getInventory();
    }

    @Environment(value = EnvType.CLIENT)
    public static boolean renderTrinketsLayer(AbstractClientPlayerEntity clientPlayer)
    {
        return TrinketsCompat.getTravelersBackpackTrinket(clientPlayer).getItem() instanceof TravelersBackpackItem;
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