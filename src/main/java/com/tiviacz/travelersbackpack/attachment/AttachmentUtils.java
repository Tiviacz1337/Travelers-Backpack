package com.tiviacz.travelersbackpack.attachment;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModAttachmentTypes;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class AttachmentUtils {
    public static Optional<BackpackAttachment> getAttachment(Player player) {
        if(player == null) {
            return Optional.empty();
        }
        return Optional.of(player.getAttachedOrCreate(ModAttachmentTypes.TRAVELERS_BACKPACK));
    }

    public static void registerJoinEquip() {
        ServerPlayerEvents.JOIN.register((player) -> {
            AttachmentUtils.getAttachment(player).ifPresent(attachment -> {
                attachment.equipBackpack(attachment.getBackpack(), player);
            });
        });
    }

    public static void synchronise(Player player) {
        AttachmentUtils.getAttachment(player).ifPresent(a -> a.synchronise(player));
    }

    public static boolean isWearingBackpack(Player player) {
        if(TravelersBackpack.enableIntegration()) {
            if(TravelersBackpack.enableTrinkets()) {
                return TrinketsApi.getAttachment(player).isEquipped(t -> t.getItem() instanceof TravelersBackpackItem);
            }/* else {
                if(AccessoriesCapability.get(player) != null) {
                    return AccessoriesCapability.get(player).isEquipped(t -> t.getItem() instanceof TravelersBackpackItem);
                }
            }*/
            return false;
        }
        if(getAttachment(player).isPresent()) {
            return getAttachment(player).get().hasBackpack() && getAttachment(player).get().getBackpack().getItem() instanceof TravelersBackpackItem;
        }
        return false;
    }

    public static ItemStack getWearingBackpack(Player player) {
        if(TravelersBackpack.enableIntegration()) {
            if(TravelersBackpack.enableTrinkets()) {
                return isWearingBackpack(player) ? TrinketsApi.getAttachment(player).findFirst(t -> t.getItem() instanceof TravelersBackpackItem).map(TrinketSlotAccess::get).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
            } /*else {
                if(isWearingBackpack(player)) {
                    if(AccessoriesCapability.get(player).getFirstEquipped(t -> t.getItem() instanceof TravelersBackpackItem) != null) {
                        return AccessoriesCapability.get(player).getFirstEquipped(t -> t.getItem() instanceof TravelersBackpackItem).stack();
                    }
                }
            } */
            return ItemStack.EMPTY;
        }
        return isWearingBackpack(player) ? getAttachment(player).get().getBackpack() : ItemStack.EMPTY;
    }

    public static void equipBackpack(Player player, ItemStack stack) {
        if(getAttachment(player).isPresent() && !isWearingBackpack(player)) {
            getAttachment(player).ifPresent(attachment -> attachment.equipBackpack(stack, player));
            player.level().playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (player.level().getRandom().nextFloat() - player.level().getRandom().nextFloat()) * 0.2F) * 0.7F);

            //Sync
            synchronise(player);
        }
    }

    @Nullable
    public static BackpackWrapper getBackpackWrapper(Player player, ItemStack stack) {
        return getBackpackWrapper(player, stack, LOAD_ALL.get());
    }

    @Nullable
    public static BackpackWrapper getBackpackWrapper(Player player, ItemStack stack, int[] dataLoad) {
        if(TravelersBackpack.enableIntegration()) {
            if(isWearingBackpack(player)) {
                return BackpackWrapper.getBackpackWrapper(player, stack, dataLoad);
            }
            return null;
        }
        if(isWearingBackpack(player)) {
            return AttachmentUtils.getAttachment(player).map(BackpackAttachment::getWrapper).orElse(null);
        }
        return null;
    }

    //Artificial wrapper for actions that do not require loading items
    @Nullable
    public static BackpackWrapper getBackpackWrapperArtificial(Player player) {
        return getBackpackWrapper(player, NO_ITEMS.get());
    }

    //Fully loaded wrapper
    @Nullable
    public static BackpackWrapper getBackpackWrapper(Player player) {
        return getBackpackWrapper(player, LOAD_ALL.get());
    }

    public static final Supplier<int[]> LOAD_ALL = () -> new int[]{1, 1, 1};
    public static final Supplier<int[]> NO_ITEMS = () -> new int[]{0, 0, 0};
    public static final Supplier<int[]> STORAGE_ONLY = () -> new int[]{1, 0, 0};
    public static final Supplier<int[]> UPGRADES_ONLY = () -> new int[]{0, 1, 0};
    public static final Supplier<int[]> TOOLS_ONLY = () -> new int[]{0, 0, 1};

    //Situational wrapper
    @Nullable
    public static BackpackWrapper getBackpackWrapper(Player player, int[] dataLoad) {
        if(TravelersBackpack.enableIntegration()) {
            if(isWearingBackpack(player)) {
                return BackpackWrapper.getBackpackWrapper(player, getWearingBackpack(player), dataLoad);
            }
            return null;
        }
        if(isWearingBackpack(player)) {
            return AttachmentUtils.getAttachment(player).map(BackpackAttachment::getWrapper).orElse(null);
        }
        return null;
    }
}