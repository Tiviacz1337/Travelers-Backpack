package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
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
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.item.PotionItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class InventoryActions
{
    public static boolean transferContainerTank(ITravelersBackpackInventory inv, SingleVariantStorage<FluidVariant> tank, int slotIn, @Nullable PlayerEntity player)
    {
        InventoryImproved inventory = inv.getFluidSlotsInventory();
        SingleSlotStorage<ItemVariant> slotStorage = InventoryStorage.of(inventory, null).getSlot(slotIn);

        ItemStack stackIn = inventory.getStack(slotIn);
        int slotOut = slotIn + 1;

        if(tank == null || stackIn.isEmpty() || stackIn.getItem() == Items.AIR) return false;

        // --- POTION PART ---
        if(stackIn.getItem() instanceof PotionItem && stackIn.getItem() != Items.GLASS_BOTTLE)
        {
            long amount = FluidConstants.BOTTLE;
            FluidVariant variant = FluidUtils.setPotionFluidVariant(stackIn);

            if(tank.isResourceBlank() || variant.getNbt().equals(tank.getResource().getNbt()))
            {
                if(tank.getAmount() + amount <= tank.getCapacity())
                {
                    ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                    ItemStack currentStackOut = inventory.getStack(slotOut);

                    if(currentStackOut.isEmpty() || currentStackOut.getItem() == bottle.getItem())
                    {
                        if(currentStackOut.getItem() == bottle.getItem())
                        {
                            if(currentStackOut.getCount() + 1 > currentStackOut.getMaxCount()) return false;

                            bottle.setCount(inventory.getStack(slotOut).getCount() + 1);
                        }

                        try(Transaction transaction = Transaction.openOuter())
                        {
                            long amountInserted = tank.insert(tank.isResourceBlank() ? variant : tank.getResource(), FluidConstants.BOTTLE, transaction);

                            if(amountInserted == FluidConstants.BOTTLE)
                            {
                                ItemStackUtils.decrStackSize(inv, slotIn, 1);
                                inventory.setStack(slotOut, bottle);
                                inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);

                                if(player != null)
                                {
                                    player.world.playSound(null, player.getBlockPos().getX(), player.getBlockPos().getY() + 0.5, player.getBlockPos().getZ(), SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                }

                                transaction.commit();
                                return true;
                            }
                        }
                    }
                }
            }
        }

        if(stackIn.getItem() == Items.GLASS_BOTTLE)
        {
            if(tank.getResource().getFluid() == ModFluids.POTION_STILL && tank.getAmount() >= FluidConstants.BOTTLE)
            {
                ItemStack stackOut = FluidUtils.getItemStackFromFluidStack(tank.getResource());
                ItemStack currentStackOut = inventory.getStack(slotOut);
                FluidVariant currentVariant = tank.getResource();

                if(currentStackOut.isEmpty())
                {
                    try(Transaction transaction = Transaction.openOuter())
                    {
                        long amountExtracted = tank.extract(currentVariant, FluidConstants.BOTTLE, transaction);
                        if(amountExtracted == FluidConstants.BOTTLE)
                        {
                            //tank.drain(Reference.POTION, IFluidHandler.FluidAction.EXECUTE);
                            ItemStackUtils.decrStackSize(inv, slotIn, 1);
                            inventory.setStack(slotOut, stackOut);
                            inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);

                            if(player != null)
                            {
                                player.world.playSound(null, player.getBlockPos().getX(), player.getBlockPos().getY() + 0.5, player.getBlockPos().getZ(), SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            }
                            transaction.commit();
                            return true;
                        }
                    }
                }
            }
        }
        // --- POTION PART ---

        // --- MILK PART ---
        if(stackIn.getItem() instanceof MilkBucketItem)
        {
            long amount = FluidConstants.BUCKET;
            FluidVariant variant = FluidVariant.of(ModFluids.MILK_STILL);

            if(tank.isResourceBlank() || variant.getFluid().matchesType(tank.getResource().getFluid()))
            {
                if(tank.getAmount() + amount <= tank.getCapacity())
                {
                    ItemStack bucket = new ItemStack(Items.BUCKET);
                    ItemStack currentStackOut = inventory.getStack(slotOut);

                    if(currentStackOut.isEmpty() || currentStackOut.getItem() == bucket.getItem())
                    {
                        if(currentStackOut.getItem() == bucket.getItem())
                        {
                            if(currentStackOut.getCount() + 1 > currentStackOut.getMaxCount()) return false;

                            bucket.setCount(inventory.getStack(slotOut).getCount() + 1);
                        }

                        try(Transaction transaction = Transaction.openOuter())
                        {
                            long amountInserted = tank.insert(tank.isResourceBlank() ? variant : tank.getResource(), FluidConstants.BUCKET, transaction);

                            if(amountInserted == FluidConstants.BUCKET)
                            {
                                ItemStackUtils.decrStackSize(inv, slotIn, 1);
                                inventory.setStack(slotOut, bucket);
                                inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);

                                if(player != null)
                                {
                                    player.world.playSound(null, player.getBlockPos().getX(), player.getBlockPos().getY() + 0.5, player.getBlockPos().getZ(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                }

                                transaction.commit();
                                return true;
                            }
                        }
                    }
                }
            }
        }
        // --- MILK PART ---

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
                    inv.getFluidSlotsInventory().setStack(slotOut, slotStorage.getResource().toStack());
                    ItemStackUtils.decrStackSize(inv, slotIn, 1);
                    //TODO make fluid sensitive?
                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), FluidVariantAttributes.getEmptySound(fluidVariant), SoundCategory.PLAYERS, 1.0F, 1.0F);
                    inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);

                    return true; //#TODO To be twekaed
                }
            }

                //Tank ===> Container
            if(tank.isResourceBlank()) return false;

            ItemStack slotOutStack = inventory.getStack(slotOut);

            // --- MILK PART ---

            if(tank.getResource().getFluid() == ModFluids.MILK_STILL)
            {
                if(stackIn.getItem() == Items.BUCKET)
                {
                    if(tank.getAmount() >= FluidConstants.BUCKET)
                    {
                        ItemStack stackOut = Items.MILK_BUCKET.getDefaultStack();
                        FluidVariant currentVariant = tank.getResource();

                        if(slotOutStack.isEmpty())
                        {
                            try(Transaction transaction = Transaction.openOuter())
                            {
                                long amountExtracted = tank.extract(currentVariant, FluidConstants.BUCKET, transaction);
                                if(amountExtracted == FluidConstants.BUCKET)
                                {
                                    //tank.drain(Reference.POTION, IFluidHandler.FluidAction.EXECUTE);
                                    ItemStackUtils.decrStackSize(inv, slotIn, 1);
                                    inventory.setStack(slotOut, stackOut);
                                    inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);

                                    if(player != null)
                                    {
                                        player.world.playSound(null, player.getBlockPos().getX(), player.getBlockPos().getY() + 0.5, player.getBlockPos().getZ(), SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                    }
                                    transaction.commit();
                                    return true;
                                }
                            }
                        }
                    }
                }
            }

            // --- MILK PART ---

            //magic ;v

            Predicate<FluidVariant> filter = fluidVariant == null ? f -> slotOutStack.isEmpty() : fluidVariant.isBlank() ? f -> slotOutStack.isEmpty() : f -> fluidVariant.isOf(tank.variant.getFluid()) && slotOutStack.isEmpty();
            if(StorageUtil.move(tank, storage, filter, Long.MAX_VALUE, null) > 0)
            {
                inv.getFluidSlotsInventory().setStack(slotOut, slotStorage.getResource().toStack());
                ItemStackUtils.decrStackSize(inv, slotIn, 1);
                player.world.playSound(null, player.getX(), player.getY(), player.getZ(), FluidVariantAttributes.getFillSound(tank.getResource()), SoundCategory.PLAYERS, 1.0F, 1.0F);
                inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);

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