package com.tiviacz.travelersbackpack.compat.curios;

import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.Optional;
import java.util.function.Predicate;

public class TravelersBackpackCurios
{
    public static ICurio createBackpackProvider()
    {
        return new TravelersBackpackCurio();
    }

    public static Optional<ImmutableTriple<String, Integer, ItemStack>> getCurioTravelersBackpack(LivingEntity livingEntity)
    {
        Predicate<ItemStack> backpack = stack -> stack.getItem() instanceof TravelersBackpackItem;
        return CuriosApi.getCuriosHelper().findEquippedCurio(backpack, livingEntity);
    }

    public static ItemStack getCurioTravelersBackpackStack(PlayerEntity player)
    {
        if(getCurioTravelersBackpack(player).isPresent())
        {
            return getCurioTravelersBackpack(player).get().getRight();
        }
        return ItemStack.EMPTY;
    }

    public static TravelersBackpackInventory getCurioTravelersBackpackInventory(PlayerEntity player)
    {
        return new TravelersBackpackInventory(getCurioTravelersBackpackStack(player), player, Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID);
    }
}
