package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.inventory.FluidTank;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FluidUtil {
    public static Optional<Storage<FluidVariant>> getFluidStorageAtCursor(Player player, AbstractContainerMenu menu) {
        var ctx = ContainerItemContext.ofPlayerCursor(player, menu).find(FluidStorage.ITEM);
        if(ctx != null) {
            return Optional.of(ctx);
        }
        return Optional.empty();
    }

    public static boolean hasFluid(Player player, AbstractContainerMenu menu) {
        if(getFluidStorageAtCursor(player, menu).isPresent()) {
            return getFluidStorageAtCursor(player, menu).get().supportsExtraction();
        }
        return false;
    }

    public static long tryEmptyContainerAtCursor(FluidTank tank, long maxTransferAmount, @Nullable ServerPlayer player, @Nullable AbstractContainerMenu menu, boolean execute) {
        Storage<FluidVariant> fluidStorage = getFluidStorageAtCursor(player, menu).get();
        try(Transaction transaction = Transaction.openOuter()) {
            long amount = StorageUtil.move(fluidStorage, tank, f -> true, maxTransferAmount, transaction);
            if(amount > 0) {
                if(execute) {
                    transaction.commit();
                    return amount;
                } else {
                    transaction.abort();
                    return amount;
                }
            }
        }
        return 0;
    }

    public static long tryFillContainerAtCursor(FluidTank tank, long maxTransferAmount, @Nullable ServerPlayer player, @Nullable AbstractContainerMenu menu, boolean execute) {
        Storage<FluidVariant> fluidStorage = getFluidStorageAtCursor(player, menu).get();
        try(Transaction transaction = Transaction.openOuter()) {
            long amount = StorageUtil.move(tank, fluidStorage, f -> true, maxTransferAmount, transaction);
            if(amount > 0) {
                if(execute) {
                    transaction.commit();
                    return amount;
                } else {
                    transaction.abort();
                    return amount;
                }
            }
        }
        return 0;
    }

    public static long tryFillBucketAtCursor(FluidTank tank, long maxTransferAmount, @Nullable ServerPlayer player, @Nullable AbstractContainerMenu menu, boolean execute) {
        try(Transaction transaction = Transaction.openOuter()) {
            long extractedAmount = tank.extract(tank.getFluid().fluidVariant(), FluidConstants.BUCKET, transaction);
            if(extractedAmount == FluidConstants.BUCKET) {
                if(execute) {
                    transaction.commit();
                    return extractedAmount;
                } else {
                    transaction.abort();
                    return  extractedAmount;
                }
            }
        }
        return 0;
    }
}
