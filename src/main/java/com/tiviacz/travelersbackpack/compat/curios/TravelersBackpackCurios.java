package com.tiviacz.travelersbackpack.compat.curios;

import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;

import java.util.Optional;
import java.util.function.Predicate;

public class TravelersBackpackCurios
{
    public static void registerCurio(RegisterCapabilitiesEvent event)
    {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> event.registerItem(CuriosCapability.ITEM, (stack, context) -> new TravelersBackpackCurio(stack), holder::get));
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
        return AttachmentUtils.getAttachment(player).map(ITravelersBackpack::getContainer).orElse(null);
    }
}