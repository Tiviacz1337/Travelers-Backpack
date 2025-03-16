package com.tiviacz.travelersbackpack.inventory.upgrades.tanks;

import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.InventoryActions;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.util.FluidStackHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class TankActions {
    public static void fillTank(ServerPlayer player, boolean leftTank) {
        if(player.containerMenu instanceof BackpackBaseMenu menu) {
            BackpackWrapper wrapper = menu.getWrapper();
            FluidTank tank = leftTank ? wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get().getLeftTank() : wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get().getRightTank();
            ItemStack carried = menu.getCarried();
            if(FluidUtil.getFluidContained(carried).isPresent() && carried.getCount() == 1) {
                //Fluid sound
                SoundEvent fluidSound = tank.isEmpty() ? SoundEvents.BUCKET_EMPTY : tank.getFluid().getFluidType().getSound(tank.getFluid(), SoundActions.BUCKET_EMPTY);

                FluidActionResult result = FluidUtil.tryEmptyContainer(carried, tank, wrapper.getBackpackTankCapacity(), wrapper.getScreenID() == Reference.ITEM_SCREEN_ID ? null : player, true);
                if(result.isSuccess()) {
                    //Play client only sound for item
                    if(wrapper.getScreenID() == Reference.ITEM_SCREEN_ID) {
                        InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), fluidSound, false);
                    }
                    menu.setCarried(result.getResult());
                }
            } else if(FluidUtil.getFluidHandler(carried).isPresent() && FluidUtil.getFluidContained(carried).isEmpty()) {
                ItemStack carriedCopy = carried.copy();
                int count = carriedCopy.getCount();
                carriedCopy.setCount(count - 1);

                //Fluid sound
                SoundEvent fluidSound = tank.isEmpty() ? SoundEvents.BUCKET_FILL : tank.getFluid().getFluidType().getSound(tank.getFluid(), SoundActions.BUCKET_FILL);

                FluidActionResult result = FluidUtil.tryFillContainer(carried, tank, wrapper.getBackpackTankCapacity(), wrapper.getScreenID() == Reference.ITEM_SCREEN_ID ? null : player, true);
                if(result.isSuccess()) {
                    if(carriedCopy.getCount() > 0) {
                        player.getInventory().placeItemBackInInventory(result.getResult());
                        menu.setCarried(carriedCopy);
                    } else {
                        menu.setCarried(result.getResult());
                    }
                    //Play client only sound for item
                    if(wrapper.getScreenID() == Reference.ITEM_SCREEN_ID) {
                        InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), fluidSound, true);
                    }
                }
            } else if(carried.getItem() instanceof PotionItem && carried.getItem() != Items.GLASS_BOTTLE) {
                if(carried.getCount() == 1) {
                    int potionType = 0;
                    if(carried.getItem() == Items.SPLASH_POTION) potionType = 1;
                    if(carried.getItem() == Items.LINGERING_POTION) potionType = 2;
                    if(tryEmptyPotion(carried, tank, potionType)) {
                        InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, true);
                        menu.setCarried(potionType != 0 ? ItemStack.EMPTY.copy() : new ItemStack(Items.GLASS_BOTTLE));
                    }
                }
            } else if(carried.getItem() == Items.GLASS_BOTTLE) {
                ItemStack newCarried = tryFillPotion(carried, tank, player, true);
                if(!newCarried.isEmpty()) {
                    ItemStack result = tryFillPotion(carried, tank, player, false);
                    InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, false);
                    menu.setCarried(result);
                }
            }
        }
    }

    public static boolean tryEmptyPotion(ItemStack carried, FluidTank tank, int potionType) {
        int amount = Reference.POTION;
        FluidStack fluidStack = new FluidStack(ModFluids.POTION_FLUID.get(), amount);
        FluidStackHelper.setFluidStackData(carried, fluidStack, potionType);
        if(tank.isEmpty() || FluidStack.isSameFluidSameComponents(tank.getFluid(), fluidStack)) {
            if(tank.getFluidAmount() + amount <= tank.getCapacity()) {
                tank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                return true;
            }
        }
        return false;
    }

    public static ItemStack tryFillPotion(ItemStack carried, FluidTank tank, ServerPlayer player, boolean simulate) {
        if(tank.getFluid().getFluid() == ModFluids.POTION_FLUID.get() && tank.getFluidAmount() >= Reference.POTION) {
            ItemStack filledPotion = FluidStackHelper.getItemStackFromFluidStack(tank.getFluid());
            if(simulate) {
                return filledPotion; //Return for simulate to check if it's possible to fill the bottle
            }
            ItemStack carriedCopy = carried.copy();
            int count = carriedCopy.getCount();
            carriedCopy.setCount(count - 1);
            tank.drain(Reference.POTION, IFluidHandler.FluidAction.EXECUTE);
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
