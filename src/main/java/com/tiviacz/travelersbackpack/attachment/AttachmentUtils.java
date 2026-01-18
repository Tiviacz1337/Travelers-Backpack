package com.tiviacz.travelersbackpack.attachment;

import com.tiviacz.travelersbackpack.init.ModAttachmentTypes;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * Preparation for removal of the required Cardinal Components dependency.
 * This class is not used in version 1.21.x.
 * Use the component ComponentUtils instead.
 * Currently, this class is only used for data transfer purposes.
 */
public class AttachmentUtils {
    public static Optional<ITravelersBackpackAttachment> getAttachment(Player player) {
        if(player == null) {
            return Optional.empty();
        }
        return Optional.of(player.getAttachedOrCreate(ModAttachmentTypes.TRAVELERS_BACKPACK));
    }

    public static void registerJoinEquip() {
        ServerPlayConnectionEvents.JOIN.register((listener, sender, server) -> {
            AttachmentUtils.getAttachment(listener.getPlayer()).ifPresent(attachment -> {
                attachment.equipBackpack(attachment.getBackpack(), listener.getPlayer());
            });
        });
    }

 /*   public static void synchronise(Player player) {
        if(player instanceof ServerPlayer) {
            //getComponent(player).ifPresent(com.tiviacz.travelersbackpack.component.ITravelersBackpack::synchronise);
        }
    }

    public static boolean isWearingBackpack(Player player) {
        if(TravelersBackpack.enableIntegration()) {
            if(TravelersBackpack.enableTrinkets()) {
                if(TrinketsApi.getTrinketComponent(player).isPresent()) {
                    if(TrinketsApi.getTrinketComponent(player).get().isEquipped(t -> t.getItem() instanceof TravelersBackpackItem)) {
                        return true;
                    }
                }
            }
            if(TravelersBackpack.enableAccessories()) {
                if(AccessoriesCapability.get(player) != null) {
                    if(AccessoriesCapability.get(player).isEquipped(t -> t.getItem() instanceof TravelersBackpackItem)) {
                        return true;
                    }
                }
            }
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
                if(TrinketsApi.getTrinketComponent(player).isPresent()) {
                    if(TrinketsApi.getTrinketComponent(player).get().isEquipped(t -> t.getItem() instanceof TravelersBackpackItem)) {
                        return TrinketsApi.getTrinketComponent(player).get().getEquipped(t -> t.getItem() instanceof TravelersBackpackItem).getFirst().getB();
                    }
                }
            }
            if(TravelersBackpack.enableAccessories()) {
                if(isWearingBackpack(player) && AccessoriesCapability.getOptionally(player).isPresent()) {
                    if(AccessoriesCapability.get(player).getFirstEquipped(t -> t.getItem() instanceof TravelersBackpackItem) != null) {
                        return AccessoriesCapability.get(player).getFirstEquipped(t -> t.getItem() instanceof TravelersBackpackItem).stack();
                    }
                }
            }
            return ItemStack.EMPTY;
        }
        return isWearingBackpack(player) ? getAttachment(player).get().getBackpack() : ItemStack.EMPTY;
    }

    public static void equipBackpack(Player player, ItemStack stack) {
        if(getAttachment(player).isPresent() && !isWearingBackpack(player)) {
            getAttachment(player).ifPresent(attachment -> attachment.equipBackpack(stack, player));
            player.level().playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.2F) * 0.7F);

            //Sync
            synchronise(player);
        }
    }*/

  /*  @Nullable
    public static BackpackWrapper getBackpackWrapper(Player player, ItemStack stack) {
        return getBackpackWrapper(player, stack, LOAD_ALL);
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
            return AttachmentUtils.getAttachment(player).map(ITravelersBackpack::getWrapper).orElse(null);
        }
        return null;
    }*/

    //Artificial wrapper for actions that do not require loading items
   /* @Nullable
    public static BackpackWrapper getBackpackWrapperArtificial(Player player) {
        return getBackpackWrapper(player, NO_ITEMS);
    }

    //Fully loaded wrapper
    @Nullable
    public static BackpackWrapper getBackpackWrapper(Player player) {
        return getBackpackWrapper(player, LOAD_ALL);
    }

    public static final int[] LOAD_ALL = new int[]{1, 1, 1};
    public static final int[] NO_ITEMS = new int[]{0, 0, 0};
    public static final int[] STORAGE_ONLY = new int[]{1, 0, 0};
    public static final int[] UPGRADES_ONLY = new int[]{0, 1, 0};
    public static final int[] TOOLS_ONLY = new int[]{0, 0, 1};

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
            return AttachmentUtils.getAttachment(player).map(ITravelersBackpack::getWrapper).orElse(null);
        }
        return null;
    }*/
}