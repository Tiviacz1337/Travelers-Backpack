package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.init.ModAttachmentTypes;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AttachmentUtils {
    public static Optional<ITravelersBackpack> getAttachment(final Player player) {
        return Optional.of(player.getData(ModAttachmentTypes.TRAVELERS_BACKPACK.get()));
    }

    public static void synchronise(Player player) {
        AttachmentUtils.getAttachment(player).ifPresent(ITravelersBackpack::synchronise);
    }

    public static void synchroniseToOthers(Player player) {
        AttachmentUtils.getAttachment(player).ifPresent(data -> data.synchroniseToOthers(player));
    }

    public static boolean isWearingBackpack(Player player) {
        if (getAttachment(player).isPresent()) {
            return getAttachment(player).get().hasWearable() && getAttachment(player).get().getWearable().getItem() instanceof TravelersBackpackItem;
        }
        return false;
    }

    public static ItemStack getWearingBackpack(Player player) {
        return isWearingBackpack(player) ? getAttachment(player).get().getWearable() : ItemStack.EMPTY;
    }

    public static void equipBackpack(Player player, ItemStack stack) {
        if (getAttachment(player).isPresent() && !isWearingBackpack(player)) {
            getAttachment(player).ifPresent(attachment -> {
                attachment.setWearable(stack);
                attachment.setContents(stack);
            });
            player.level().playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.2F) * 0.7F);

            //Sync
            synchronise(player);
            synchroniseToOthers(player);
        }
    }

    @Nullable
    public static TravelersBackpackContainer getBackpackInv(Player player) {
        if (isWearingBackpack(player)) {
            return AttachmentUtils.getAttachment(player).map(ITravelersBackpack::getContainer).orElse(null);
        }
        return null;
    }
}