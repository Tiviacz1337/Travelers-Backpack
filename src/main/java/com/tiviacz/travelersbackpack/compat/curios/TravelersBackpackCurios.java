package com.tiviacz.travelersbackpack.compat.curios;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.Optional;
import java.util.function.Predicate;

public class TravelersBackpackCurios
{
    public static ICurio createBackpackProvider()
    {
        return new TravelersBackpackCurio(ItemStack.EMPTY);
    }

    public static Optional<ImmutableTriple<String, Integer, ItemStack>> getCurioTravelersBackpack(LivingEntity livingEntity)
    {
        Predicate<ItemStack> backpack = stack -> stack.getItem() instanceof TravelersBackpackItem;
        return CuriosApi.getCuriosHelper().findEquippedCurio(backpack, livingEntity);
    }

    public static ItemStack getCurioTravelersBackpackStack(Player player)
    {
        if(getCurioTravelersBackpack(player).isPresent())
        {
            return getCurioTravelersBackpack(player).get().getRight();
        }
        return ItemStack.EMPTY;
    }

    public static TravelersBackpackContainer getCurioTravelersBackpackInventory(Player player)
    {
        TravelersBackpackContainer curioContainer = CapabilityUtils.getCapability(player).map(ITravelersBackpack::getContainer).orElse(null);
        curioContainer.setStack(getCurioTravelersBackpackStack(player));
        curioContainer.loadAllData(getCurioTravelersBackpackStack(player).getOrCreateTag());

        return curioContainer;
    }
}