package com.tiviacz.travelersbackpack.inventory;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;

import java.util.function.Predicate;

public class InventoryActions
{
    public static boolean transferContainerTank(ITravelersBackpackInventory inv, SingleVariantStorage<FluidVariant> tank, int slotIn, PlayerEntity player)
    {
        InventoryImproved inventory = inv.getInventory();
        SingleSlotStorage<ItemVariant> slotStorage = InventoryStorage.of(inventory, null).getSlot(slotIn);

        ItemStack stackIn = inventory.getStack(slotIn);
        int slotOut = slotIn + 1;

        if(tank == null || stackIn.isEmpty() || stackIn.getItem() == Items.AIR) return false;

        Storage<FluidVariant> storage = ContainerItemContext.ofSingleSlot(slotStorage).find(FluidStorage.ITEM);

        if(storage != null)
        {
            FluidVariant fluidVariant = StorageUtil.findStoredResource(storage, null);
            ResourceAmount<FluidVariant> resourceAmount = StorageUtil.findExtractableContent(storage, null);
            //Storage ===> Tank

            if(fluidVariant != null && fluidVariant.getFluid() != null && resourceAmount != null && resourceAmount.amount() > 0) {
                long amount = resourceAmount.amount();

                if (tank.getAmount() + amount > tank.getCapacity()) return false;
                if (tank.getAmount() > 0 && !tank.getResource().isOf(fluidVariant.getFluid())) return false;

                ItemStack slotOutStack = inventory.getStack(slotOut);

                if (StorageUtil.move(storage, tank, f -> slotOutStack.isEmpty(), FluidConstants.BUCKET, null) > 0) {
                    inv.getInventory().setStack(slotOut, slotStorage.getResource().toStack());
                    inv.decrStackSize(slotIn, 1);
                    //TODO make fluid sensitive?
                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), FluidVariantAttributes.getEmptySound(fluidVariant), SoundCategory.PLAYERS, 1.0F, 1.0F);
                    inv.markTankDirty();

                    return true; //#TODO To be twekaed
                }
            }

                //Tank ===> Container
            if(tank.isResourceBlank()) return false;

            ItemStack slotOutStack = inventory.getStack(slotOut);

            //magic ;v

            Predicate<FluidVariant> filter = fluidVariant == null ? f -> slotOutStack.isEmpty() : fluidVariant.isBlank() ? f -> slotOutStack.isEmpty() : f -> fluidVariant.isOf(tank.variant.getFluid()) && slotOutStack.isEmpty();
            if(StorageUtil.move(tank, storage, filter, Long.MAX_VALUE, null) > 0)
            {
                inv.getInventory().setStack(slotOut, slotStorage.getResource().toStack());
                inv.decrStackSize(slotIn, 1);
                player.world.playSound(null, player.getX(), player.getY(), player.getZ(), FluidVariantAttributes.getFillSound(tank.getResource()), SoundCategory.PLAYERS, 1.0F, 1.0F);
                inv.markTankDirty();

                return true;
            }

          /*  if(fluidVariant == null)
            {
                ItemStack slotOutStack = inventory.getStack(slotOut);

                if(StorageUtil.move(tank, storage, f ->slotOutStack.isEmpty(), Long.MAX_VALUE, null) > 0)
                {
                    inv.getInventory().setStack(slotOut, slotStorage.getResource().toStack());
                    inv.decrStackSize(slotIn, 1);
                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BUCKET_FILL, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    inv.markTankDirty();

                    return true;
                }
            }

            else if(fluidVariant.isOf(tank.variant.getFluid()))
            {
                ItemStack slotOutStack = inventory.getStack(slotOut);

                if(StorageUtil.move(tank, storage, f ->slotOutStack.isEmpty(), Long.MAX_VALUE, null) > 0)
                {
                    inv.getInventory().setStack(slotOut, slotStorage.getResource().toStack());
                    inv.decrStackSize(slotIn, 1);
                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BUCKET_FILL, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    inv.markTankDirty();

                    return true;
                }
            } */
                //storage
                //ItemStack stackOut = FluidUtil.tryEmptyContainer(stackInCopy, tankCopy, amount, player, false).getResult();

                //if (stackOut.isEmpty()) return false;

            /*    ItemStack slotOutStack = inventory.getStack(slotOut);

                if (slotOutStack.isEmpty() || slotOutStack.getItem() == stackOut.getItem()) {
                    if (slotOutStack.getItem() == stackOut.getItem()) {
                        stackOut.setCount(slotOutStack.getCount() + 1);

                        if (stackOut.getCount() > slotOutStack.getMaxStackSize()) return false;
                    }

                    FluidUtil.tryEmptyContainer(stackIn, tank, amount, player, true);

                    inventory.setStackInSlot(slotOut, stackOut);
                    inv.decrStackSize(slotIn, 1);
                    inv.markTankDirty();

                    return true;
                }
            } */
            }

            //Tank ===> Container

          /*  if(tank.isResourceBlank() || tank.getAmount() <= 0) return false;

            if(isFluidEqual(fluidVariant, resourceAmount, tank))
            {
                //int amount = FluidUtil.getFluidHandler(stackIn).map(iFluidHandlerItem -> iFluidHandlerItem.getTankCapacity(0)).orElse(0);
               // long amount = resourceAmount.amount();

                //ItemStack stackInCopy = stackIn.copy();
                //FluidTank tankCopy = new FluidTank(tank.getCapacity());
                //tankCopy.fill(tank.getFluid(), IFluidHandler.FluidAction.EXECUTE);

                //ItemStack stackOut = FluidUtil.tryFillContainer(stackInCopy, tankCopy, amount, player, true).getResult();

                //if(stackOut.isEmpty()) return false;

               /* ItemStack slotOutStack = inventory.getStackInSlot(slotOut);

                if(slotOutStack.isEmpty() || slotOutStack.getItem() == stackOut.getItem())
                {
                    if(slotOutStack.getItem() == stackOut.getItem())
                    {
                        stackOut.setCount(slotOutStack.getCount() + 1);

                        if(stackOut.getCount() > slotOutStack.getMaxStackSize()) return false;
                    } */

                    //FluidUtil.tryFillContainer(stackIn, tank, amount, player, true);

            /*    if(StorageUtil.move(tank, storage, f -> true, FluidConstants.BUCKET, null) > 0)
                {
                    inv.markTankDirty();
                    return true;
                }
                    //inventory.setStackInSlot(slotOut, stackOut);
                   // inv.decrStackSize(slotIn, 1);
                   // inv.markTankDirty();

                  //  return true;
        } */
        return false;
       // }
    }

    private static boolean isFluidEqual(FluidVariant variant, ResourceAmount<FluidVariant> amount, SingleVariantStorage<FluidVariant> fluidStorage)
    {
        if(variant != null && amount != null)
        {
            return variant.isOf(fluidStorage.variant.getFluid());
        }
        else return variant == null;
    }

 /*   private static boolean isFluidEqual(ItemStack stackIn, SingleVariantStorage<FluidVariant> fluidStorage)
    {
        Storage<FluidVariant> fluidVariantStorage = ContainerItemContext.withInitial(stackIn).find(FluidStorage.ITEM);
        FluidVariant variant = StorageUtil.findStoredResource(fluidVariantStorage, null);
        ResourceAmount<FluidVariant> resourceAmount = StorageUtil.findExtractableContent(fluidVariantStorage, null);

        if(!variant.isBlank() && resourceAmount.amount() > 0)
        {
            return variant.isOf(fluidStorage.variant.getFluid());
        }
        else return variant.isBlank();
    } */
}

