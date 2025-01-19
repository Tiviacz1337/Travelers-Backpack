package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.util.*;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
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
import java.util.Optional;

public class InventoryActions {
    public static boolean transferContainerTank(TanksUpgrade upgrade, FluidTank tank, int slotIn) {
        ItemStackHandler itemStackHandler = upgrade.getFluidSlotsHandler();

        //Copy ItemStackHandler and set stack size to 1 to not break FluidStorage for multiple buckets
        ItemStackHandler itemStackHandlerCopy = new ItemStackHandler(itemStackHandler.getSlots());
        itemStackHandlerCopy.setStackInSlot(slotIn, itemStackHandler.getStackInSlot(slotIn).copyWithCount(1));

        SingleSlotStorage<ItemVariant> slotStorage = InventoryStorage.of(itemStackHandlerCopy, null).getSlot(slotIn);

        ItemStack stackIn = itemStackHandler.getStackInSlot(slotIn);
        int slotOut = slotIn + 1;

        if(tank == null || stackIn.isEmpty() || stackIn.getItem() == Items.AIR) return false;

        // --- POTION PART ---
        if(stackIn.getItem() instanceof PotionItem && stackIn.getItem() != Items.GLASS_BOTTLE) {
            boolean hasFluidStorage = FluidUtil.hasFluidStorageConstant(stackIn);
            if(!hasFluidStorage) {
                long amount = FluidConstants.BOTTLE;
                FluidVariant variant = FluidStackHelper.setFluidStackNBT(stackIn);
                FluidVariantWrapper wrapper = new FluidVariantWrapper(variant, amount);

                if(tank.isEmpty() || FluidUtil.isSameVariant(variant, tank.getFluid().fluidVariant())) {
                    if(tank.getFluidAmount() + amount <= tank.getCapacity()) {
                        ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                        ItemStack currentStackOut = itemStackHandler.getStackInSlot(slotOut);

                        if(currentStackOut.isEmpty() || currentStackOut.getItem() == bottle.getItem()) {
                            if(currentStackOut.getItem() == bottle.getItem()) {
                                if(currentStackOut.getCount() + 1 > currentStackOut.getMaxStackSize()) return false;

                                bottle.setCount(itemStackHandler.getStackInSlot(slotOut).getCount() + 1);
                            }

                            tank.fill(wrapper, false);
                            InventoryHelper.removeItem(upgrade.getFluidSlotsHandler(), slotIn, 1);
                            itemStackHandler.setStackInSlot(slotOut, bottle);

                            playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, true);

                            return true;
                        }
                    }
                }
            }
        }

        if(stackIn.getItem() == Items.GLASS_BOTTLE) {
            if(tank.getFluid().fluidVariant().getFluid() == ModFluids.POTION_STILL && tank.getFluidAmount() >= FluidConstants.BOTTLE) {
                ItemStack stackOut = FluidStackHelper.getItemStackFromFluidStack(tank.getFluid().fluidVariant());
                ItemStack currentStackOut = itemStackHandler.getStackInSlot(slotOut);

                if(currentStackOut.isEmpty()) {
                    tank.drain(FluidConstants.BOTTLE, false);
                    InventoryHelper.removeItem(upgrade.getFluidSlotsHandler(), slotIn, 1);
                    itemStackHandler.setStackInSlot(slotOut, stackOut);

                    playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, false);

                    return true;
                }
            }
        }
        // --- POTION PART ---

        Optional<Storage<FluidVariant>> fluidStorage = FluidUtil.getFluidStorageAtSlot(slotStorage);

        if(!Transaction.isOpen()) {
            try(Transaction transaction = Transaction.openOuter()) {
                if(fluidStorage.isPresent()) {
                    ResourceAmount<FluidVariant> fluidVariantWrapper;
                    try(Transaction transaction1 = transaction.openNested()) {
                        fluidVariantWrapper = StorageUtil.findExtractableContent(fluidStorage.get(), transaction1);
                        transaction1.commit();
                    }
                    //ResourceAmount<FluidVariant> fluidVariantWrapper = StorageUtil.findExtractableContent(fluidStorage.get(), transaction);

                    //Container ===> Tank
                    if(fluidVariantWrapper != null && fluidVariantWrapper.resource().getFluid() != null && fluidVariantWrapper.amount() > 0) {
                        if(tank.getAmount() > 0 && !FluidUtil.isSameVariant(fluidVariantWrapper.resource(), tank.getResource())) {
                            transaction.close();
                            return false;
                        }
                        ItemStack slotOutStack = itemStackHandler.getStackInSlot(slotOut);
                        //Fluid sound
                        SoundEvent fluidSound = FluidVariantAttributes.getEmptySound(fluidVariantWrapper.resource());

                        if(StorageUtil.move(fluidStorage.get(), tank, f -> true, fluidVariantWrapper.amount(), transaction) > 0) {
                            ItemStack transferResultStack = slotStorage.getResource().toStack();
                            if(!transferResultStack.isEmpty()) {
                                if(slotOutStack.isEmpty() || slotOutStack.getItem() == transferResultStack.getItem()) {
                                    if(slotOutStack.getItem() == transferResultStack.getItem()) {
                                        transferResultStack.setCount(slotOutStack.getCount() + 1);
                                        if(transferResultStack.getCount() > slotOutStack.getMaxStackSize()) {
                                            transaction.abort();
                                            return false;
                                        }
                                    }
                                    itemStackHandler.setStackInSlot(slotOut, transferResultStack);
                                }
                            }
                            InventoryHelper.removeItem(upgrade.getFluidSlotsHandler(), slotIn, 1);
                            playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), fluidSound, false);
                            transaction.commit();
                            return true;
                        }
                    }
                }

                //Tank ===> Container

                if(tank.isEmpty() || tank.getFluidAmount() <= 0) return false;

                if(isFluidEqual(stackIn, tank, transaction)) {
                    long amount = 0;
                    try(Transaction transaction1 = transaction.openNested()) {
                        amount = StorageUtil.simulateInsert(fluidStorage.get(), tank.getFluid().fluidVariant(), tank.getFluidAmount(), transaction1);
                        transaction1.commit();
                    }

                    //Fluid sound
                    SoundEvent fluidSound = FluidTypeHelper.getSound(tank.getFluid().fluidVariant(), FluidTypeHelper.BUCKET_FILL);

                    long transferAmount = 0;
                    try(Transaction transaction1 = transaction.openNested()) {
                        transferAmount = FluidUtil.tryFillContainerAtSlot(tank, amount, fluidStorage.get(), true, transaction1);
                    }
                    ItemStack stackOut = slotStorage.getResource().toStack();

                    if(stackOut.isEmpty()) {
                        transaction.abort();
                        return false;
                    }

                    if(transferAmount > 0) {
                        ItemStack slotOutStack = itemStackHandler.getStackInSlot(slotOut);

                        if(slotOutStack.isEmpty() || slotOutStack.getItem() == stackOut.getItem()) {
                            if(slotOutStack.getItem() == stackOut.getItem()) {
                                stackOut.setCount(slotOutStack.getCount() + 1);

                                if(stackOut.getCount() > slotOutStack.getMaxStackSize()) {
                                    transaction.abort();
                                    return false;
                                }
                            }

                            playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), fluidSound, true);
                            itemStackHandler.setStackInSlot(slotOut, stackOut);
                            InventoryHelper.removeItem(upgrade.getFluidSlotsHandler(), slotIn, 1);
                            transaction.commit();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean isFluidEqual(ItemStack stackIn, FluidTank tank, Transaction main) {
        if(FluidUtil.hasFluidStorageConstant(stackIn)) {
            ResourceAmount<FluidVariant> fluidVariantWrapper = null;
            try(Transaction transaction = main.openNested()) {
                fluidVariantWrapper = StorageUtil.findExtractableContent(FluidUtil.getFluidStorageConstant(stackIn).get(), transaction);
                transaction.commit();
            }
            if(fluidVariantWrapper == null) {
                return true;
            } else if(!fluidVariantWrapper.resource().isBlank() && fluidVariantWrapper.amount() > 0) {
                return FluidUtil.isSameVariant(fluidVariantWrapper.resource(), tank.getFluid().fluidVariant());
            }
        }
        return false;
    }

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
            }
        }
    }
}