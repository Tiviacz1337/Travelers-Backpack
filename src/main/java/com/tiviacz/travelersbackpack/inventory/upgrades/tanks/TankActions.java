package com.tiviacz.travelersbackpack.inventory.upgrades.tanks;

import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.FluidTank;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import com.tiviacz.travelersbackpack.inventory.InventoryActions;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.util.FluidStackHelper;
import com.tiviacz.travelersbackpack.util.FluidTypeHelper;
import com.tiviacz.travelersbackpack.util.FluidUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class TankActions {
    public static void fillTank(ServerPlayer player, boolean leftTank) {
        if(player.containerMenu instanceof BackpackBaseMenu menu) {
            BackpackWrapper wrapper = menu.getWrapper();
            FluidTank tank = leftTank ? wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get().getLeftTank() : wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get().getRightTank();
            ItemStack carried = menu.getCarried();
            AtomicBoolean handled = new AtomicBoolean(false);
            FluidUtil.getFluidStorageAtCursor(player, menu).ifPresent(fluidStorage -> {
                if(FluidUtil.hasFluid(fluidStorage) && carried.getCount() == 1) {
                    SoundEvent fluidSound = tank.isEmpty() ? SoundEvents.BUCKET_EMPTY : FluidTypeHelper.getSound(tank.getFluid().fluidVariant(), FluidTypeHelper.BUCKET_EMPTY);

                    long result = FluidUtil.tryEmptyContainerAtCursor(tank, wrapper.getBackpackTankCapacity(), fluidStorage, true);
                    if(result > 0) {
                        //Play client only sound for item
                        //if(wrapper.getScreenID() == Reference.ITEM_SCREEN_ID) {
                        InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), fluidSound, FluidTypeHelper.BUCKET_EMPTY);
                        //}
                        handled.set(true);
                    }
                } else if(!handled.get() && fluidStorage.supportsInsertion()) {
                    ItemStack carriedCopy = carried.copy();
                    int count = carriedCopy.getCount();
                    carriedCopy.setCount(count - 1);

                    //Fluid sound
                    SoundEvent fluidSound = tank.isEmpty() ? SoundEvents.BUCKET_FILL : FluidTypeHelper.getSound(tank.getFluid().fluidVariant(), FluidTypeHelper.BUCKET_FILL);

                    if(carried.getItem() instanceof BucketItem) {
                        Item fullBucket = tank.getFluid().fluidVariant().getFluid().getBucket();
                        long result = FluidUtil.tryFillBucketAtCursor(tank, wrapper.getBackpackTankCapacity(), fluidStorage, true);
                        if(result > 0) {
                            if(carriedCopy.getCount() > 0) {
                                player.getInventory().placeItemBackInInventory(fullBucket.getDefaultInstance());
                                menu.setCarried(carriedCopy);
                            } else {
                                menu.setCarried(fullBucket.getDefaultInstance());
                            }
                            //Play client only sound for item
                            //if(wrapper.getScreenID() == Reference.ITEM_SCREEN_ID) {
                            InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), fluidSound, FluidTypeHelper.BUCKET_FILL);
                            //}
                            handled.set(true);
                        }
                    } else if(!handled.get() && carried.getItem() == Items.GLASS_BOTTLE) {
                        ItemStack newCarried = tryFillPotion(carried, tank, player, true);
                        if(!newCarried.isEmpty()) {
                            ItemStack result = tryFillPotion(carried, tank, player, false);
                            InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, false);
                            menu.setCarried(result);
                            handled.set(true);
                        }
                    } else if(!handled.get() && carried.getCount() == 1) {
                        long result = FluidUtil.tryFillContainerAtCursor(tank, wrapper.getBackpackTankCapacity(), fluidStorage, true);
                        if(result > 0) {
                            //Play client only sound for item
                            //if(wrapper.getScreenID() == Reference.ITEM_SCREEN_ID) {
                            InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), fluidSound, FluidTypeHelper.BUCKET_FILL);
                            //}
                            handled.set(true);
                        }
                    }
                }
            });
            if(!handled.get() && carried.getItem() instanceof PotionItem && carried.getItem() != Items.GLASS_BOTTLE) {
                if(carried.getCount() == 1) {
                    int potionType = 0;
                    if(carried.getItem() == Items.SPLASH_POTION) potionType = 1;
                    if(carried.getItem() == Items.LINGERING_POTION) potionType = 2;
                    if(tryEmptyPotion(carried, tank, potionType)) {
                        InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, true);
                        menu.setCarried(potionType != 0 ? ItemStack.EMPTY.copy() : new ItemStack(Items.GLASS_BOTTLE));
                    }
                }
            }
        }
    }

    public static boolean tryEmptyPotion(ItemStack carried, FluidTank tank, int potionType) {
        long amount = FluidConstants.BOTTLE;
        //FluidVariantWrapper fluidStack = new FluidVariantWrapper(FluidVariant.of(ModFluids.POTION_STILL), amount);
        FluidVariant potionVariant = FluidStackHelper.setPotionFluidVariant(carried, potionType);
        FluidVariantWrapper potionVariantWrapper = new FluidVariantWrapper(potionVariant, amount);
        if(tank.isEmpty() || (potionVariantWrapper.fluidVariant().isOf(tank.getFluid().fluidVariant().getFluid())) && potionVariantWrapper.fluidVariant().componentsMatch(tank.getFluid().fluidVariant().getComponents())) {
            if(tank.getFluidAmount() + amount <= tank.getCapacity()) {
                tank.fill(potionVariantWrapper, false);
                return true;
            }
        }
        return false;
    }

    public static ItemStack tryFillPotion(ItemStack carried, FluidTank tank, ServerPlayer player, boolean simulate) {
        if(tank.getFluid().fluidVariant().getFluid() == ModFluids.POTION_STILL && tank.getFluidAmount() >= FluidConstants.BOTTLE) {
            ItemStack filledPotion = FluidStackHelper.getItemStackFromFluidStack(tank.getFluid().fluidVariant());
            if(simulate) {
                return filledPotion; //Return for simulate to check if it's possible to fill the bottle
            }
            ItemStack carriedCopy = carried.copy();
            int count = carriedCopy.getCount();
            carriedCopy.setCount(count - 1);
            tank.drain(FluidConstants.BOTTLE, false);
            if(carriedCopy.getCount() > 0) {
                player.getInventory().placeItemBackInInventory(filledPotion);
                return carriedCopy;
            } else {
                return filledPotion;
            }
        }
        return ItemStack.EMPTY;
    }
}