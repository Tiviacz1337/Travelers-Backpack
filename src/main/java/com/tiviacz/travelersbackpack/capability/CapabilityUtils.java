package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityUtils
{
    public static LazyOptional<ITravelersBackpack> getCapability(final PlayerEntity player)
    {
        return player.getCapability(TravelersBackpackCapability.TRAVELERS_BACKPACK_CAPABILITY, TravelersBackpackCapability.DEFAULT_FACING);
    }

    public static void synchronise(PlayerEntity player)
    {
        CapabilityUtils.getCapability(player)
                .ifPresent(ITravelersBackpack::synchronise);
    }

    public static void synchroniseToOthers(PlayerEntity player)
    {
        CapabilityUtils.getCapability(player)
                .ifPresent(i -> i.synchroniseToOthers(player));
    }

    public static boolean isWearingBackpack(PlayerEntity player)
    {
        LazyOptional<ITravelersBackpack> cap = getCapability(player);
        ItemStack backpack = cap.lazyMap(ITravelersBackpack::getWearable).orElse(ItemStack.EMPTY);

        return cap.map(ITravelersBackpack::hasWearable).orElse(false) && backpack.getItem() instanceof TravelersBackpackItem;
    }

    public static ItemStack getWearingBackpack(PlayerEntity player)
    {
        LazyOptional<ITravelersBackpack> cap = getCapability(player);
        ItemStack backpack = cap.map(ITravelersBackpack::getWearable).orElse(ItemStack.EMPTY);

        return isWearingBackpack(player) ? backpack : ItemStack.EMPTY;
    }

    public static void equipBackpack(PlayerEntity player, ItemStack stack)
    {
        LazyOptional<ITravelersBackpack> cap = getCapability(player);

        if(!cap.map(ITravelersBackpack::hasWearable).orElse(false))
        {
            cap.ifPresent(inv -> inv.setWearable(stack));
            player.world.playSound(null, player.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.2F) * 0.7F);

            //Sync
            synchronise(player);
            synchroniseToOthers(player);
        }
    }

    public static TravelersBackpackInventory getBackpackInv(PlayerEntity player)
    {
        ItemStack wearable = getWearingBackpack(player);

        if(wearable.getItem() instanceof TravelersBackpackItem)
        {
            return new TravelersBackpackInventory(wearable, player, Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID);
        }
        return null;
    }
}
