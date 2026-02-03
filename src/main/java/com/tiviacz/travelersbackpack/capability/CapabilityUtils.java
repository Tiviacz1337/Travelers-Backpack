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
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Supplier;

public class CapabilityUtils {
    public static LazyOptional<ITravelersBackpack> getCapability(Player player) {
        if(player == null) {
            return LazyOptional.empty();
        }
        return player.getCapability(TravelersBackpackCapability.TRAVELERS_BACKPACK_CAPABILITY, TravelersBackpackCapability.DEFAULT_FACING);
    }

    public static void synchronise(Player player) {
        CapabilityUtils.getCapability(player).ifPresent(ITravelersBackpack::synchronise);
    }

    public static boolean isWearingBackpack(Player player) {
        if(TravelersBackpack.enableIntegration()) {
            if(CuriosApi.getCuriosInventory(player).isPresent()) {
                if(CuriosApi.getCuriosInventory(player).resolve().isPresent()) {
                    return CuriosApi.getCuriosInventory(player).resolve().get().isEquipped(t -> t.getItem() instanceof TravelersBackpackItem);
                }
            }
            return false;
        }
        if(getCapability(player).isPresent()) {
            return getCapability(player).resolve().get().hasBackpack() && getCapability(player).resolve().get().getBackpack().getItem() instanceof TravelersBackpackItem;
        }
        return false;
    }

    public static ItemStack getWearingBackpack(Player player) {
        if(TravelersBackpack.enableIntegration()) {
            return isWearingBackpack(player) ? CuriosApi.getCuriosInventory(player).resolve().get().findFirstCurio(t -> t.getItem() instanceof TravelersBackpackItem).get().stack() : ItemStack.EMPTY;
        }
        return isWearingBackpack(player) ? getCapability(player).resolve().get().getBackpack() : ItemStack.EMPTY;
    }

    public static void equipBackpack(Player player, ItemStack stack) {
        if(getCapability(player).isPresent() && !isWearingBackpack(player)) {
            getCapability(player).ifPresent(attachment -> attachment.equipBackpack(stack));
            player.level().playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, (1.0F + (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.2F) * 0.7F);

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
            return CapabilityUtils.getCapability(player).map(ITravelersBackpack::getWrapper).orElse(null);
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

    @Nullable
    public static BackpackWrapper getBackpackWrapper(Player player, int[] dataLoad) {
        if(TravelersBackpack.enableIntegration()) {
            if(isWearingBackpack(player)) {
                return BackpackWrapper.getBackpackWrapper(player, getWearingBackpack(player), dataLoad);
            }
            return null;
        }
        if(isWearingBackpack(player)) {
            return CapabilityUtils.getCapability(player).map(ITravelersBackpack::getWrapper).orElse(null);
        }
        return null;
    }
}