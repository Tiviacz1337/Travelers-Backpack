package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

public class CapabilityUtils
{
    public static LazyOptional<ITravelersBackpack> getCapability(final Player player) {
        return player.getCapability(TravelersBackpackCapability.TRAVELERS_BACKPACK_CAPABILITY, TravelersBackpackCapability.DEFAULT_FACING);
    }

    public static void synchronise(Player player) {
        CapabilityUtils.getCapability(player).ifPresent(ITravelersBackpack::synchronise);
    }

    public static void synchroniseToOthers(Player player) {
        CapabilityUtils.getCapability(player).ifPresent(i -> i.synchroniseToOthers(player));
    }

    public static boolean isWearingBackpack(Player player) {
        if (getCapability(player).isPresent() && getCapability(player).resolve().isPresent()) {
            return getCapability(player).resolve().get().hasWearable() && getCapability(player).resolve().get().getWearable().getItem() instanceof TravelersBackpackItem;
        }
        return false;
    }

    public static ItemStack getWearingBackpack(Player player) {
        return isWearingBackpack(player) ? getCapability(player).resolve().get().getWearable() : ItemStack.EMPTY;
    }

    public static void equipBackpack(Player player, ItemStack stack) {
        if (getCapability(player).isPresent() && !isWearingBackpack(player)) {
            getCapability(player).ifPresent(cap -> {
                cap.setWearable(stack);
                cap.setContents(stack);

                cap.synchronise();
                cap.synchroniseToOthers(player);
            });

            player.level().playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.2F) * 0.7F);
        }
    }

    @Nullable
    public static TravelersBackpackContainer getBackpackInv(Player player) {
        if (isWearingBackpack(player)) {
            return CapabilityUtils.getCapability(player).map(ITravelersBackpack::getContainer).orElse(null);
        }
        return null;
    }
}