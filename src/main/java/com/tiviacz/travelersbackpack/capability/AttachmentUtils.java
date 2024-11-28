package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

public class AttachmentUtils {
    public static LazyOptional<ITravelersBackpack> getCapability(Player player) {
        if(player == null) {
            return LazyOptional.empty();
        }
        return player.getCapability(TravelersBackpackCapability.TRAVELERS_BACKPACK_CAPABILITY, TravelersBackpackCapability.DEFAULT_FACING);
    }

    public static void synchronise(Player player) {
        AttachmentUtils.getCapability(player).ifPresent(ITravelersBackpack::synchronise);
    }

    public static boolean isWearingBackpack(Player player) {
        if(TravelersBackpack.enableIntegration()) {
            /*if(TravelersBackpack.enableCurios()) {
                if(CuriosApi.getCuriosInventory(player).isPresent()) {
                    return CuriosApi.getCuriosInventory(player).get().isEquipped(t -> t.getItem() instanceof TravelersBackpackItem);
                }
            } else {
                return AccessoriesCapability.get(player).isEquipped(t -> t.getItem() instanceof TravelersBackpackItem);
            }
            return false; */
        }
        if(getCapability(player).isPresent()) {
            return getCapability(player).resolve().get().hasBackpack() && getCapability(player).resolve().get().getBackpack().getItem() instanceof TravelersBackpackItem;
        }
        return false;
    }

    public static ItemStack getWearingBackpack(Player player) {
        if(TravelersBackpack.enableIntegration()) {
            /*if(TravelersBackpack.enableCurios()) {
                return isWearingBackpack(player) ? CuriosApi.getCuriosInventory(player).get().findFirstCurio(t -> t.getItem() instanceof TravelersBackpackItem).get().stack() : ItemStack.EMPTY;
            } else {
                if(isWearingBackpack(player)) {
                    if(AccessoriesCapability.get(player).getFirstEquipped(t -> t.getItem() instanceof TravelersBackpackItem) != null) {
                        return AccessoriesCapability.get(player).getFirstEquipped(t -> t.getItem() instanceof TravelersBackpackItem).stack();
                    }
                }
            } */
            return ItemStack.EMPTY;
        }
        return isWearingBackpack(player) ? getCapability(player).resolve().get().getBackpack() : ItemStack.EMPTY;
    }

    public static void equipBackpack(Player player, ItemStack stack) {
        if(getCapability(player).isPresent() && !isWearingBackpack(player)) {
            getCapability(player).ifPresent(attachment -> attachment.equipBackpack(stack));
            player.level().playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.2F) * 0.7F);

            //Sync
            synchronise(player);
        }
    }

    @Nullable
    public static BackpackWrapper getBackpackWrapper(Player player, ItemStack stack) {
        if(TravelersBackpack.enableIntegration()) {
            if(isWearingBackpack(player)) {
                return BackpackWrapper.getBackpackWrapper(player, stack);
            }
            return null;
        }
        if(isWearingBackpack(player)) {
            return AttachmentUtils.getCapability(player).map(ITravelersBackpack::getWrapper).orElse(null);
        }
        return null;
    }

    @Nullable
    public static BackpackWrapper getBackpackWrapper(Player player) {
        if(TravelersBackpack.enableIntegration()) {
            if(isWearingBackpack(player)) {
                return BackpackWrapper.getBackpackWrapper(player, getWearingBackpack(player));
            }
            return null;
        }
        if(isWearingBackpack(player)) {
            return AttachmentUtils.getCapability(player).map(ITravelersBackpack::getWrapper).orElse(null);
        }
        return null;
    }
}