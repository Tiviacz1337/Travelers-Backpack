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
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
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
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
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
                int potionType = 0;
                if(stackIn.getItem() == Items.SPLASH_POTION) potionType = 1;
                if(stackIn.getItem() == Items.LINGERING_POTION) potionType = 2;
                FluidVariant variant = FluidStackHelper.setPotionFluidVariant(stackIn, potionType);
                FluidVariantWrapper wrapper = new FluidVariantWrapper(variant, FluidConstants.BOTTLE);
                ItemStack bottle = potionType != 0 ? ItemStack.EMPTY.copy() : new ItemStack(Items.GLASS_BOTTLE);

                if(transferPotion(itemStackHandler, wrapper, 0, tank, stackIn, bottle, slotOut, true)) {
                    playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, true);
                    return true;
                }
            }
        }

        if(stackIn.getItem() == Items.GLASS_BOTTLE) {
            if(tank.getFluid().fluidVariant().getFluid() == ModFluids.POTION_STILL && tank.getFluidAmount() >= FluidConstants.BOTTLE) {
                ItemStack stackOut = FluidStackHelper.getItemStackFromFluidStack(tank.getFluid().fluidVariant());
                if(transferPotion(itemStackHandler, FluidVariantWrapper.blank(), FluidConstants.BOTTLE, tank, stackIn, stackOut, slotOut, false)) {
                    playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, false);
                    return true;
                }
            }
        }
        // --- POTION PART ---

        Storage<FluidVariant> fluidStorage = FluidUtil.getFluidStorageConstant(stackIn.copyWithCount(1)).orElse(null);

        if(fluidStorage != null) {
            ResourceAmount<FluidVariant> resource = StorageUtil.findExtractableContent(fluidStorage, null);

            //Container ===> Tank

            if(resource != null) {
                if(tank.getAmount() <= 0 || resource.resource().equals(tank.getResource())) {
                    //Fluid Sound
                    SoundEvent fluidSound = FluidTypeHelper.getSound(tank.getFluid().fluidVariant(), FluidTypeHelper.BUCKET_EMPTY);

                    if(transferFluid(resource, itemStackHandler, tank, stackIn, slotOut, true)) {
                        //Play fill sound
                        playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), fluidSound, false);
                        return true;
                    }
                }
            }

            //Tank ===> Container

            if(tank.isEmpty() || tank.getFluidAmount() <= 0) {
                return false;
            }

            if(isSameFluid(fluidStorage, tank)) {
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
        SingleSlotStorage<ItemVariant> slotStorage = InventoryStorage.of(handlerCopy, null).getSlot(0);
        Storage<FluidVariant> fluidStorage = FluidUtil.getFluidStorageAtSlot(slotStorage).orElse(null);

        //Tank copy to work on
        FluidTank tankCopy = new FluidTank(tank.getCapacity());
        tankCopy.setFluid(tank.getFluid());

        try(var tx = Transaction.openOuter()) {
            long fill = StorageUtil.move(isFilling ? fluidStorage : tankCopy, isFilling ? tankCopy : fluidStorage, f -> true, isFilling ? resource.amount() : tankCopy.getFluidAmount(), tx);
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

    public static boolean transferPotion(ItemStackHandler handler, FluidVariantWrapper potionFluidStack, long fluidAmount, FluidTank tank, ItemStack stackIn, ItemStack bottleResult, int slotOut, boolean isFilling) {
        long filledFluid = isFilling ? tank.fill(potionFluidStack, true) : tank.drain(fluidAmount, true).getAmount();

        //Can't fill tank
        if(filledFluid <= 0) {
            return false;
        }

        ItemStackHandler tempHandler = new ItemStackHandler(1);
        tempHandler.setStackInSlot(0, handler.getStackInSlot(slotOut).copy());

        ItemStack insertResult = tempHandler.insertItem(0, bottleResult, true);

        //Correctly inserted to the slot out
        if(insertResult.isEmpty()) {
            if(isFilling) {
                tank.fill(potionFluidStack, false);
            } else {
                tank.drain(fluidAmount, false);
            }

            tempHandler.insertItem(0, bottleResult, false);

            //Shrink input stack
            stackIn.shrink(1);

            //Set result stack in output slot
            handler.setStackInSlot(slotOut, tempHandler.getStackInSlot(0).copy());
            return true;
        }
        return false;
    }

    private static boolean isSameFluid(Storage<FluidVariant> fluidStorage, FluidTank tank) {
        for(StorageView<FluidVariant> view : fluidStorage) {
            FluidVariant resource = view.getResource();
            boolean sameFluid = tank.getFluidAmount() > 0 && FluidUtil.isSameVariant(resource, tank.getFluid().fluidVariant());
            boolean emptyHandler = resource.isBlank();

            if(emptyHandler || sameFluid) {
                return true;
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
            if(user.containerMenu instanceof BackpackItemMenu menu && menu.getWrapper().getScreenID() == Reference.ITEM_SCREEN_ID && !menu.player.level().isClientSide()) {
                var vec3 = menu.player.blockPosition().getCenter();
                menu.player.level().playSound(null, vec3.x(), vec3.y(), vec3.z(), soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }
}