package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.util.FluidStackHelper;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import com.tiviacz.travelersbackpack.util.Reference;
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
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class InventoryActions {
    public static boolean transferContainerTank(TanksUpgrade upgrade, FluidTank tank, int slotIn) {
        ItemStackHandler itemStackHandler = upgrade.getFluidSlotsHandler();
        SingleSlotStorage<ItemVariant> slotStorage = InventoryStorage.of(itemStackHandler, null).getSlot(slotIn);

        ItemStack stackIn = itemStackHandler.getStackInSlot(slotIn);
        int slotOut = slotIn + 1;

        if(tank == null || stackIn.isEmpty() || stackIn.getItem() == Items.AIR) return false;

        // --- POTION PART ---
        if(stackIn.getItem() instanceof PotionItem && stackIn.getItem() != Items.GLASS_BOTTLE) {
            //boolean hasFluidHandler = FluidUtil.getFluidHandler(stackIn).isPresent();
            long amount = FluidConstants.BOTTLE;
            FluidVariant variant = FluidStackHelper.setPotionFluidVariant(stackIn);
            FluidVariantWrapper wrapper = new FluidVariantWrapper(variant, amount);

            //if(!hasFluidHandler) {
                //int amount = Reference.POTION;
                //FluidStack fluidStack = new FluidStack(ModFluids.POTION_FLUID.get(), amount);
                //FluidStackHelper.setFluidStackData(stackIn, fluidStack);

                if(tank.isEmpty() || (variant.isOf(tank.getFluid().fluidVariant().getFluid()) && variant.componentsMatch(tank.getFluid().fluidVariant().getComponents()))) {
                    if(tank.getFluidAmount() + amount <= tank.getCapacity()) {
                        ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                        ItemStack currentStackOut = itemStackHandler.getStackInSlot(slotOut);

                        if(currentStackOut.isEmpty() || currentStackOut.getItem() == bottle.getItem()) {
                            if(currentStackOut.getItem() == bottle.getItem()) {
                                if(currentStackOut.getCount() + 1 > currentStackOut.getMaxStackSize()) return false;

                                bottle.setCount(itemStackHandler.getStackInSlot(slotOut).getCount() + 1);
                            }

                            tank.fill(wrapper, true);
                            InventoryHelper.removeItem(upgrade.getFluidSlotsHandler(), slotIn, 1);
                            itemStackHandler.setStackInSlot(slotOut, bottle);

                            playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, true);

                            return true;
                        }
                    }
                }
           // }
        }

        if(stackIn.getItem() == Items.GLASS_BOTTLE) {
            if(tank.getFluid().fluidVariant().getFluid() == ModFluids.POTION_STILL && tank.getFluidAmount() >= FluidConstants.BOTTLE) {
                ItemStack stackOut = FluidStackHelper.getItemStackFromFluidStack(tank.getFluid().fluidVariant());
                ItemStack currentStackOut = itemStackHandler.getStackInSlot(slotOut);

                if(currentStackOut.isEmpty()) {
                    tank.drain(FluidConstants.BOTTLE, true);
                    InventoryHelper.removeItem(upgrade.getFluidSlotsHandler(), slotIn, 1);
                    itemStackHandler.setStackInSlot(slotOut, stackOut);

                    playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, false);

                    return true;
                }
            }
        }
        // --- POTION PART ---

        //Optional<IFluidHandlerItem> fluidHandler = FluidUtil.getFluidHandler(stackIn);
        Storage<FluidVariant> storage = ContainerItemContext.ofSingleSlot(slotStorage).find(FluidStorage.ITEM);

        if(storage != null) {
            FluidVariant fluidVariant = StorageUtil.findStoredResource(storage, p -> true);
            ResourceAmount<FluidVariant> resourceAmount = StorageUtil.findExtractableContent(storage, null);
            //Optional<FluidStack> fluidstack = FluidUtil.getFluidContained(stackIn);

            //Container ===> Tank
            if(fluidVariant != null && fluidVariant.getFluid() != null && resourceAmount != null && resourceAmount.amount() > 0)
            {
                if(tank.getAmount() > 0 && !tank.getResource().isOf(fluidVariant.getFluid())) return false;

                ItemStack slotOutStack = itemStackHandler.getStackInSlot(slotOut);
                //Fluid sound
                SoundEvent fluidSound = FluidVariantAttributes.getEmptySound(fluidVariant); //tank.getFluid().getFluidType().getSound(tank.getFluid(), SoundActions.BUCKET_EMPTY);

                try(Transaction transaction = Transaction.openOuter())
                {
                    if(StorageUtil.move(storage, tank, f -> true, FluidConstants.BUCKET, transaction) > 0)
                    {
                        boolean isEmpty = itemStackHandler.getStackInSlot(slotOut).isEmpty();

                        if(isEmpty || itemStackHandler.getStackInSlot(slotOut).is(slotStorage.getResource().getItem()))
                        {
                            if(isEmpty || (!isEmpty && slotOutStack.getCount() + 1 <= slotOutStack.getMaxStackSize()))
                            {
                                itemStackHandler.setStackInSlot(slotOut, isEmpty ? slotStorage.getResource().toStack() : slotOutStack.copyWithCount(slotOutStack.getCount() + 1));
                                InventoryHelper.removeItem(itemStackHandler, slotIn, 1);
                                playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), fluidSound, false);
                                transaction.commit();

                                //inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);
                                return true;
                            }
                        }
                    }
                }
            }

         /*   if(fluidstack.isPresent() && fluidstack.map(FluidStack::getAmount).orElse(0) > 0) {
                int amount = fluidstack.map(FluidStack::getAmount).orElse(0);

                if(tank.getFluidAmount() > 0 && !FluidStack.isSameFluidSameComponents(tank.getFluid(), fluidstack.orElse(FluidStack.EMPTY)))
                    return false;

                //Copies
                ItemStack stackInCopy = stackIn.copy();
                FluidTank tankCopy = new FluidTank(tank.getCapacity());
                tankCopy.fill(tank.getFluid(), IFluidHandler.FluidAction.EXECUTE);

                //Fluid sound
                SoundEvent fluidSound = tank.getFluid().getFluidType().getSound(tank.getFluid(), SoundActions.BUCKET_EMPTY);

                ItemStack stackOut = FluidUtil.tryEmptyContainer(stackInCopy, tankCopy, amount, null, false).getResult();

                if(!stackOut.isEmpty()) {
                    ItemStack slotOutStack = itemStackHandler.getStackInSlot(slotOut);

                    if(slotOutStack.isEmpty() || slotOutStack.getItem() == stackOut.getItem()) {
                        if(slotOutStack.getItem() == stackOut.getItem()) {
                            stackOut.setCount(slotOutStack.getCount() + 1);

                            if(stackOut.getCount() > slotOutStack.getMaxStackSize()) return false;
                        }

                       /* if(stackInCopy.getItem() == Items.WATER_BUCKET && EnchantmentHelper.getEnchantments(stackInCopy).containsKey(Enchantments.INFINITY_ARROWS))
                        {
                            stackOut = stackInCopy;
                        }
                        playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), fluidSound, false);
                        FluidUtil.tryEmptyContainer(stackIn, tank, amount, null, true);

                        itemStackHandler.setStackInSlot(slotOut, stackOut);
                        InventoryHelper.removeItem(upgrade.getFluidSlotsHandler(), slotIn, 1);

                        return true;
                    }
                }
            } */

            //Tank ===> Container

            if(tank.isEmpty() || tank.getFluidAmount() <= 0) return false;
            ItemStack slotOutStack = itemStackHandler.getStackInSlot(slotOut);

            //Fluid sound
            SoundEvent fluidSound = FluidVariantAttributes.getFillSound(fluidVariant); //tank.getFluid().getFluidType().getSound(tank.getFluid(), SoundActions.BUCKET_FILL);

            if(stackIn.getItem() == Items.BUCKET)
            {
                try(Transaction transaction = Transaction.openOuter())
                {
                    if(!tank.getResource().isBlank())
                    {
                        ItemStack bucketOutput = tank.getResource().getFluid().getBucket().getDefaultInstance().copy();

                        if(tank.extract(tank.getResource(), FluidConstants.BUCKET, transaction) > 0 && slotOutStack.isEmpty())
                        {
                            itemStackHandler.setStackInSlot(slotOut, bucketOutput);
                            InventoryHelper.removeItem(itemStackHandler, slotIn, 1);
                            playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), fluidSound, true);
                            //player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), FluidVariantAttributes.getFillSound(tank.getResource()), SoundCategory.PLAYERS, 1.0F, 1.0F);
                            transaction.commit();

                            //inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);
                            return true;
                        }
                    }
                }
            }

            //Logic for other fluid containers

            Predicate<FluidVariant> filter = fluidVariant == null ? f -> slotOutStack.isEmpty() : fluidVariant.isBlank() ? f -> slotOutStack.isEmpty() : f -> fluidVariant.isOf(tank.variant.getFluid()) && slotOutStack.isEmpty();

            try(Transaction transaction = Transaction.openOuter())
            {
                if(StorageUtil.move(tank, storage, filter, Long.MAX_VALUE, transaction) > 0)
                {
                    itemStackHandler.setStackInSlot(slotOut, slotStorage.getResource().toStack());
                    InventoryHelper.removeItem(itemStackHandler, slotIn, 1);
                    playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), fluidSound, true);
                    //player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), FluidVariantAttributes.getFillSound(tank.getResource()), SoundCategory.PLAYERS, 1.0F, 1.0F);
                    transaction.commit();

                    //inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);
                    return true;
                }
            }

            /*     if(isFluidEqual(stackIn, tank)) {
                int amount = FluidUtil.getFluidHandler(stackIn).map(iFluidHandlerItem -> iFluidHandlerItem.getTankCapacity(0)).orElse(0);

                ItemStack stackInCopy = stackIn.copy();
                FluidTank tankCopy = new FluidTank(tank.getCapacity());
                tankCopy.fill(tank.getFluid(), IFluidHandler.FluidAction.EXECUTE);

                //Fluid sound
                SoundEvent fluidSound = tank.getFluid().getFluidType().getSound(tank.getFluid(), SoundActions.BUCKET_FILL);

                ItemStack stackOut = FluidUtil.tryFillContainer(stackInCopy, tankCopy, amount, null, true).getResult();

                if(stackOut.isEmpty()) return false;

                ItemStack slotOutStack = itemStackHandler.getStackInSlot(slotOut);

                if(slotOutStack.isEmpty() || slotOutStack.getItem() == stackOut.getItem()) {
                    if(slotOutStack.getItem() == stackOut.getItem()) {
                        stackOut.setCount(slotOutStack.getCount() + 1);

                        if(stackOut.getCount() > slotOutStack.getMaxStackSize()) return false;
                    }

                    playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), fluidSound, true);
                    FluidUtil.tryFillContainer(stackIn, tank, amount, null, true);

                    itemStackHandler.setStackInSlot(slotOut, stackOut);
                    InventoryHelper.removeItem(upgrade.getFluidSlotsHandler(), slotIn, 1);

                    return true;
                }
            }*/
        }
        return false;
    }

   /* private static boolean isFluidEqual(ItemStack stackIn, FluidTank tank) {
        if(FluidUtil.getFluidContained(stackIn).isPresent() && FluidUtil.getFluidContained(stackIn).map(FluidStack::getAmount).orElse(0) > 0) {
            return FluidUtil.getFluidContained(stackIn).map(fluidstack -> FluidStack.isSameFluidSameComponents(fluidstack, tank.getFluid())).orElse(false);
        } else return !FluidUtil.getFluidContained(stackIn).isPresent();
    } */

    public static void playFluidSound(@Nullable Player player, List<Player> usingPlayers, SoundEvent soundEvent, boolean fill) {
        if(soundEvent == null) {
            if(fill) {
                soundEvent = SoundEvents.BUCKET_FILL;
            } else {
                soundEvent = SoundEvents.BUCKET_EMPTY;
            }
        }

        if(player != null) {
            player.level().playSound(null, player.position().x(), player.position().y() + 0.5, player.position().z(), soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
        } else if(!usingPlayers.isEmpty()) {
            Player user = usingPlayers.get(0);
            if(user.containerMenu instanceof BackpackBlockEntityMenu menu) {
                Vec3 backpackPos = menu.getWrapper().getBackpackPos().getCenter();
                menu.player.level().playSound(null, backpackPos.x(), backpackPos.y() + 0.5, backpackPos.z(), soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            if(user.containerMenu instanceof BackpackItemMenu menu && menu.getWrapper().getScreenID() == Reference.ITEM_SCREEN_ID && !menu.player.level().isClientSide) {
                menu.player.playNotifySound(soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
                //menu.player.level().playLocalSound(menu.player.position().x(), menu.player.position().y() + 0.5, menu.player.position().z(), soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }
        }
    }
}