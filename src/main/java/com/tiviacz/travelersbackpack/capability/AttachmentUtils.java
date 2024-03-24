package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.entity.IEntityTravelersBackpack;
import com.tiviacz.travelersbackpack.compat.curios.TravelersBackpackCurios;
import com.tiviacz.travelersbackpack.init.ModAttachmentTypes;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AttachmentUtils
{
    public static Optional<ITravelersBackpack> getAttachment(final Player player)
    {
        return Optional.of(player.getData(ModAttachmentTypes.TRAVELERS_BACKPACK.get()));
    }

    public static Optional<IEntityTravelersBackpack> getEntityAttachment(final LivingEntity livingEntity)
    {
        if(Reference.ALLOWED_TYPE_ENTRIES.contains(livingEntity.getType()))
        {
            return Optional.of(livingEntity.getData(ModAttachmentTypes.TRAVELERS_BACKPACK_ENTITY.get()));
        }
        return Optional.empty();
    }

    public static void synchronise(Player player)
    {
        AttachmentUtils.getAttachment(player).ifPresent(ITravelersBackpack::synchronise);
    }

    public static void synchroniseToOthers(Player player)
    {
        AttachmentUtils.getAttachment(player).ifPresent(data -> data.synchroniseToOthers(player));
    }

    public static void synchroniseEntity(LivingEntity livingEntity)
    {
        AttachmentUtils.getEntityAttachment(livingEntity).ifPresent(IEntityTravelersBackpack::synchronise);
    }

    public static boolean isWearingBackpack(Player player)
    {
        if(TravelersBackpack.enableCurios())
        {
            return TravelersBackpackCurios.getCurioTravelersBackpack(player).isPresent();
        }

        Optional<ITravelersBackpack> data = getAttachment(player);
        ItemStack backpack = data.map(ITravelersBackpack::getWearable).orElse(ItemStack.EMPTY);

        return data.map(ITravelersBackpack::hasWearable).orElse(false) && backpack.getItem() instanceof TravelersBackpackItem;
    }

    public static boolean isWearingBackpack(LivingEntity livingEntity)
    {
        Optional<IEntityTravelersBackpack> data = getEntityAttachment(livingEntity);
        ItemStack backpack = data.map(IEntityTravelersBackpack::getWearable).orElse(ItemStack.EMPTY);

        return data.map(IEntityTravelersBackpack::hasWearable).orElse(false) && backpack.getItem() instanceof TravelersBackpackItem;
    }

    public static ItemStack getWearingBackpack(Player player)
    {
        if(TravelersBackpack.enableCurios())
        {
            return TravelersBackpackCurios.getCurioTravelersBackpackStack(player);
        }

        Optional<ITravelersBackpack> data = getAttachment(player);
        ItemStack backpack = data.map(ITravelersBackpack::getWearable).orElse(ItemStack.EMPTY);

        return isWearingBackpack(player) ? backpack : ItemStack.EMPTY;
    }

    public static ItemStack getWearingBackpack(LivingEntity livingEntity)
    {
        Optional<IEntityTravelersBackpack> data = getEntityAttachment(livingEntity);
        ItemStack backpack = data.map(IEntityTravelersBackpack::getWearable).orElse(ItemStack.EMPTY);

        return isWearingBackpack(livingEntity) ? backpack : ItemStack.EMPTY;
    }

    public static void equipBackpack(Player player, ItemStack stack)
    {
        if(player.level().isClientSide) return;

        Optional<ITravelersBackpack> data = getAttachment(player);

        if(!data.map(ITravelersBackpack::hasWearable).orElse(false))
        {
            data.ifPresent(inv -> inv.setWearable(stack));
            data.ifPresent(inv -> inv.setContents(stack));
            player.level().playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, (1.0F + (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.2F) * 0.7F);

            //Sync
            synchronise(player);
            synchroniseToOthers(player);
        }
    }

    @Nullable
    public static TravelersBackpackContainer getBackpackInv(Player player)
    {
        if(TravelersBackpack.enableCurios())
        {
            return TravelersBackpackCurios.getCurioTravelersBackpackInventory(player);
        }

        ItemStack wearable = getWearingBackpack(player);

        if(wearable.getItem() instanceof TravelersBackpackItem)
        {
            return AttachmentUtils.getAttachment(player).map(ITravelersBackpack::getContainer).orElse(null);
        }
        return null;
    }
}