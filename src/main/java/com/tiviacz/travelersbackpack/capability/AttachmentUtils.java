package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModAttachmentTypes;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

public class AttachmentUtils {
    public static Optional<ITravelersBackpack> getAttachment(Player player) {
        if(player == null) {
            return Optional.empty();
        }
        return Optional.of(player.getData(ModAttachmentTypes.TRAVELERS_BACKPACK.get()));
    }

    public static void synchronise(Player player) {
        AttachmentUtils.getAttachment(player).ifPresent(ITravelersBackpack::synchronise);
    }

    public static boolean isWearingBackpack(Player player) {
        if(TravelersBackpack.enableIntegration()) {
            if(TravelersBackpack.enableCurios()) {
                if(CuriosApi.getCuriosInventory(player).isPresent()) {
                    if(CuriosApi.getCuriosInventory(player).get().isEquipped(t -> t.getItem() instanceof TravelersBackpackItem)) {
                        return true;
                    }
                    //return CuriosApi.getCuriosInventory(player).get().isEquipped(t -> t.getItem() instanceof TravelersBackpackItem);
                }
            }
            if(TravelersBackpack.enableAccessories()) {
                if(AccessoriesCapability.getOptionally(player).isPresent()) {
                    if(AccessoriesCapability.get(player).isEquipped(t -> t.getItem() instanceof TravelersBackpackItem)) {
                        return true;
                    }
                    //return AccessoriesCapability.get(player).isEquipped(t -> t.getItem() instanceof TravelersBackpackItem);
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
            if(TravelersBackpack.enableCurios()) {
                if(CuriosApi.getCuriosInventory(player).isPresent()) {
                    if(CuriosApi.getCuriosInventory(player).get().isEquipped(t -> t.getItem() instanceof TravelersBackpackItem)) {
                        return CuriosApi.getCuriosInventory(player).get().findFirstCurio(t -> t.getItem() instanceof TravelersBackpackItem).get().stack();
                    }
                }
                //return isWearingBackpack(player) ? CuriosApi.getCuriosInventory(player).get().findFirstCurio(t -> t.getItem() instanceof TravelersBackpackItem).get().stack() : ItemStack.EMPTY;
            }
            if(TravelersBackpack.enableAccessories()) {
                if(AccessoriesCapability.getOptionally(player).isPresent()) {
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
            getAttachment(player).ifPresent(attachment -> attachment.equipBackpack(stack));
            player.level().playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.2F) * 0.7F);

            //Sync
            synchronise(player);
        }
    }

    @Nullable
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
    }

    //Artificial wrapper for actions that do not require loading items
    @Nullable
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
    public static final int[] UPGRADES_ONLY = new int[]{0, 1, 0};
    public static final int[] TOOLS_ONLY = new int[]{0, 0, 1};

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
    }
}