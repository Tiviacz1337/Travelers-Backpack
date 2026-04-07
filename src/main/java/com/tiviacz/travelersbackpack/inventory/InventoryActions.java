package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.util.*;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
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
    public static boolean transferContainerTank(TanksUpgrade upgrade, ItemStack stackIn, FluidTank tank, int slotIn) {
        ItemStackHandler itemStackHandler = upgrade.getFluidSlotsHandler();
        int slotOut = slotIn + 1;

        if(tank == null || stackIn.isEmpty() || stackIn.getItem() == Items.AIR) return false;

        // --- POTION PART ---
        if(stackIn.getItem() instanceof PotionItem && stackIn.getItem() != Items.GLASS_BOTTLE) {
            boolean hasFluidStorage = FluidUtil.hasFluidStorageConstant(stackIn);
            if(!hasFluidStorage) {
                long amount = FluidConstants.BOTTLE;
                int potionType = 0;
                if(stackIn.getItem() == Items.SPLASH_POTION) potionType = 1;
                if(stackIn.getItem() == Items.LINGERING_POTION) potionType = 2;
                FluidVariant variant = FluidStackHelper.setPotionFluidVariant(stackIn, potionType);
                FluidVariantWrapper wrapper = new FluidVariantWrapper(variant, amount);

                if(tank.isEmpty() || FluidUtil.isSameVariant(variant, tank.getFluid().fluidVariant())) {
                    if(tank.getFluidAmount() + amount <= tank.getCapacity()) {
                        ItemStack bottle = potionType != 0 ? ItemStack.EMPTY.copy() : new ItemStack(Items.GLASS_BOTTLE);
                        ItemStack currentStackOut = itemStackHandler.getStackInSlot(slotOut);

                        if(currentStackOut.isEmpty() || currentStackOut.getItem() == bottle.getItem() || bottle.isEmpty()) {
                            if(currentStackOut.getItem() == bottle.getItem() && !bottle.isEmpty()) {
                                if(currentStackOut.getCount() + 1 > currentStackOut.getMaxStackSize()) return false;

                                bottle.setCount(itemStackHandler.getStackInSlot(slotOut).getCount() + 1);
                            }

                            tank.fill(wrapper, false);
                            InventoryHelper.removeItem(upgrade.getFluidSlotsHandler(), slotIn, 1);
                            if(!bottle.isEmpty()) {
                                itemStackHandler.setStackInSlot(slotOut, bottle);
                            }

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
        Optional<Storage<FluidVariant>> fluidStorage = FluidUtil.getFluidStorageConstant(stackIn.copyWithCount(1));

        if(fluidStorage.isPresent()) {
            ResourceAmount<FluidVariant> resource = StorageUtil.findExtractableContent(fluidStorage.get(), null);

            //Container ===> Tank

            if(resource != null) {
                if(tank.getAmount() > 0 && !resource.resource().equals(tank.getResource())) {
                    return false;
                }

                //Fluid Sound
                SoundEvent fluidSound = FluidTypeHelper.getSound(tank.getFluid().fluidVariant(), FluidTypeHelper.BUCKET_EMPTY);

                if(transferFluid(resource, itemStackHandler, tank, stackIn, slotOut, true)) {
                    //Play fill sound
                    playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), fluidSound, false);
                    return true;
                }
            }

            //Tank ===> Container

            if(tank.isEmpty() || tank.getFluidAmount() <= 0) return false;

            if(isSameFluid(resource, tank)) {
                //Fluid sound
                SoundEvent fluidSound = FluidTypeHelper.getSound(tank.getFluid().fluidVariant(), FluidTypeHelper.BUCKET_FILL);

                if(transferFluid(resource, itemStackHandler, tank, stackIn, slotOut, false)) {
                    //Play fill sound
                    playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), fluidSound, true);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean transferFluid(ResourceAmount<FluidVariant> resource, ItemStackHandler handler, FluidTank tank, ItemStack stack, int slotOut, boolean isFilling) {
        ItemStackHandler handlerCopy = new ItemStackHandler(2);
        //0 - Input/Result
        //1 - Current output item in slotOut

        handlerCopy.setStackInSlot(0, stack.copyWithCount(1));
        handlerCopy.setStackInSlot(1, handler.getStackInSlot(slotOut).copy());

        //Slot Storage to mutate ItemStack
        SingleSlotStorage<ItemVariant> slotStorage = ContainerStorage.of(handlerCopy, null).getSlot(0);
        Optional<Storage<FluidVariant>> fluidStorage = FluidUtil.getFluidStorageAtSlot(slotStorage);

        //Tank copy to work on
        FluidTank tankCopy = new FluidTank(tank.getCapacity());
        tankCopy.setFluid(tank.getFluid());

        try(var tx = Transaction.openOuter()) {
            long fill = StorageUtil.move(isFilling ? fluidStorage.get() : tankCopy, isFilling ? tankCopy : fluidStorage.get(), f -> true, isFilling ? resource.amount() : tankCopy.getFluidAmount(), tx);
            if(fill > 0) {
                ItemStack result = slotStorage.getResource().toStack((int)slotStorage.getAmount());
                ItemStack insertResult = handlerCopy.insertItem(1, result, false);

                if(!insertResult.isEmpty()) {
                    tx.abort();
                    return false;
                }

                stack.shrink(1);
                tank.setFluid(tankCopy.getFluid());
                tank.onContentsChanged();

                //Set result stack in output slot
                handler.setStackInSlot(slotOut, handlerCopy.getStackInSlot(1).copy());
                tx.commit();
                return true;
            }
        }
        return false;
    }

    private static boolean isSameFluid(@Nullable ResourceAmount<FluidVariant> resource, FluidTank tank) {
        if(resource == null) {
            return true;
        } else if(!resource.resource().isBlank() && resource.amount() > 0) {
            return FluidUtil.isSameVariant(resource.resource(), tank.getFluid().fluidVariant());
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
            if(user.containerMenu instanceof BackpackItemMenu menu && menu.getWrapper().getScreenID() == Reference.ITEM_SCREEN_ID && !menu.player.level().isClientSide()) {
                var vec3 = menu.player.blockPosition().getCenter();
                menu.player.level().playSound(null, vec3.x(), vec3.y(), vec3.z(), soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }
}