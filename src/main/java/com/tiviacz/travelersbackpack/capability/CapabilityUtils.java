package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.entity.IEntityTravelersBackpack;
import com.tiviacz.travelersbackpack.capability.entity.TravelersBackpackEntityCapability;
import com.tiviacz.travelersbackpack.compat.curios.TravelersBackpackCurios;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class CapabilityUtils
{
    public static LazyOptional<ITravelersBackpack> getCapability(final PlayerEntity player)
    {
        return player.getCapability(TravelersBackpackCapability.TRAVELERS_BACKPACK_CAPABILITY, TravelersBackpackCapability.DEFAULT_FACING);
    }

    public static LazyOptional<IEntityTravelersBackpack> getEntityCapability(final LivingEntity livingEntity)
    {
        return livingEntity.getCapability(TravelersBackpackEntityCapability.TRAVELERS_BACKPACK_ENTITY_CAPABILITY, TravelersBackpackEntityCapability.DEFAULT_FACING);
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

    public static void synchroniseEntity(LivingEntity livingEntity)
    {
        CapabilityUtils.getEntityCapability(livingEntity)
                .ifPresent(IEntityTravelersBackpack::synchronise);
    }

    public static boolean isWearingBackpack(PlayerEntity player)
    {
        if(TravelersBackpack.enableCurios())
        {
            return TravelersBackpackCurios.getCurioTravelersBackpack(player).isPresent();
        }

        LazyOptional<ITravelersBackpack> cap = getCapability(player);
        ItemStack backpack = cap.lazyMap(ITravelersBackpack::getWearable).orElse(ItemStack.EMPTY);

        return cap.map(ITravelersBackpack::hasWearable).orElse(false) && backpack.getItem() instanceof TravelersBackpackItem;
    }

    public static boolean isWearingBackpack(LivingEntity livingEntity)
    {
        LazyOptional<IEntityTravelersBackpack> cap = getEntityCapability(livingEntity);
        ItemStack backpack = cap.lazyMap(IEntityTravelersBackpack::getWearable).orElse(ItemStack.EMPTY);

        return cap.map(IEntityTravelersBackpack::hasWearable).orElse(false) && backpack.getItem() instanceof TravelersBackpackItem;
    }

    public static ItemStack getWearingBackpack(PlayerEntity player)
    {
        if(TravelersBackpack.enableCurios())
        {
            return TravelersBackpackCurios.getCurioTravelersBackpackStack(player);
        }

        LazyOptional<ITravelersBackpack> cap = getCapability(player);
        ItemStack backpack = cap.map(ITravelersBackpack::getWearable).orElse(ItemStack.EMPTY);

        return isWearingBackpack(player) ? backpack : ItemStack.EMPTY;
    }

    public static ItemStack getWearingBackpack(LivingEntity livingEntity)
    {
        LazyOptional<IEntityTravelersBackpack> cap = getEntityCapability(livingEntity);
        ItemStack backpack = cap.map(IEntityTravelersBackpack::getWearable).orElse(ItemStack.EMPTY);

        return isWearingBackpack(livingEntity) ? backpack : ItemStack.EMPTY;
    }

    public static void equipBackpack(PlayerEntity player, ItemStack stack)
    {
        if(player.level.isClientSide) return;

        LazyOptional<ITravelersBackpack> cap = getCapability(player);

        if(!cap.map(ITravelersBackpack::hasWearable).orElse(false))
        {
            cap.ifPresent(inv -> inv.setWearable(stack));
            cap.ifPresent(inv -> inv.setContents(stack));
            player.level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.2F) * 0.7F);

            //Sync
            synchronise(player);
            synchroniseToOthers(player);
        }
    }

    @Nullable
    public static TravelersBackpackInventory getBackpackInv(PlayerEntity player)
    {
        if(TravelersBackpack.enableCurios())
        {
            return TravelersBackpackCurios.getCurioTravelersBackpackInventory(player);
        }

        ItemStack wearable = getWearingBackpack(player);

        if(wearable.getItem() instanceof TravelersBackpackItem)
        {
            return CapabilityUtils.getCapability(player).map(ITravelersBackpack::getInventory).orElse(null);
        }
        return null;
    }
}