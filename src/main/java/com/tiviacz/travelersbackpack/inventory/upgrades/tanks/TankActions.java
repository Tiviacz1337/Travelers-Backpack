package com.tiviacz.travelersbackpack.inventory.upgrades.tanks;

import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.InventoryActions;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.util.FluidStackHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import com.tiviacz.travelersbackpack.util.StacksHandlerUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

public class TankActions {
    public static void fillTank(ServerPlayer player, boolean leftTank) {
        if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
            BackpackWrapper wrapper = menu.getWrapper();
            FluidStacksResourceHandler tank = leftTank ? wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get().getLeftTank() : wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get().getRightTank();
            ItemStack carried = menu.getCarried();
            ResourceHandler<FluidResource> carriedHandler = ItemAccess.forPlayerCursor(player, menu).getCapability(Capabilities.Fluid.ITEM);
            if(carriedHandler != null) {
                FluidResource carriedResource = ResourceHandlerUtil.findExtractableResource(carriedHandler, f -> true, null);
                if(carriedResource != null && carried.getCount() == 1) {
                    //Fluid sound
                    SoundEvent fluidSound = StacksHandlerUtils.isEmpty(tank) ? SoundEvents.BUCKET_EMPTY : StacksHandlerUtils.getFluid(tank).getFluidType().getSound(StacksHandlerUtils.getFluid(tank), SoundActions.BUCKET_EMPTY);

                    int movedAmount = ResourceHandlerUtil.move(carriedHandler, tank, p -> true, carriedHandler.getAmountAsInt(ResourceHandlerUtil.indexOf(carriedHandler, carriedResource)), null);
                    if(movedAmount > 0) {
                        //Play client only sound for item
                        InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), fluidSound, false);
                        return;
                    }
                }

                int remainingCount = carried.getCount();
                if(carried.getCount() > 1) {
                    carried.setCount(1);
                }

                //Fluid sound
                SoundEvent fluidSound = StacksHandlerUtils.isEmpty(tank) ? SoundEvents.BUCKET_FILL : StacksHandlerUtils.getFluid(tank).getFluidType().getSound(StacksHandlerUtils.getFluid(tank), SoundActions.BUCKET_FILL);

                int movedAmount = ResourceHandlerUtil.move(tank, carriedHandler, p -> true, wrapper.getBackpackTankCapacity(), null);
                if(movedAmount > 0) {
                    ItemStack result = menu.getCarried().copy();
                    if(remainingCount > 1) {
                        serverPlayer.getInventory().placeItemBackInInventory(result);
                        menu.setCarried(carried.copyWithCount(remainingCount - 1));
                    }
                    //Play client only sound for item
                    InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), fluidSound, true);
                } else {
                    carried.setCount(remainingCount);
                }
            }
            //FluidResource carriedResource = carriedHandler.map(fluidResourceResourceHandler -> fluidResourceResourceHandler.getResource(0)).orElse(FluidResource.EMPTY);
            /*if(carriedHandler != null && !carriedResource.isEmpty() && carried.getCount() == 1) {
                //Fluid sound
                SoundEvent fluidSound = StacksHandlerUtils.isEmpty(tank) ? SoundEvents.BUCKET_EMPTY : StacksHandlerUtils.getFluid(tank).getFluidType().getSound(StacksHandlerUtils.getFluid(tank), SoundActions.BUCKET_EMPTY);

                int movedAmount = ResourceHandlerUtil.move(carriedHandler, tank, p -> true, carriedHandler.getAmountAsInt(0), null);
                if(movedAmount > 0) {
                    //Play client only sound for item
                    InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), fluidSound, false);
                    return;
                }
            }
            if(carriedHandler != null) {
                int remainingCount = carried.getCount();
                if(carried.getCount() > 1) {
                    carried.setCount(1);
                }

                //Fluid sound
                SoundEvent fluidSound = StacksHandlerUtils.isEmpty(tank) ? SoundEvents.BUCKET_FILL : StacksHandlerUtils.getFluid(tank).getFluidType().getSound(StacksHandlerUtils.getFluid(tank), SoundActions.BUCKET_FILL);

                int movedAmount = ResourceHandlerUtil.move(tank, carriedHandler, p -> true, wrapper.getBackpackTankCapacity(), null);
                if(movedAmount > 0) {
                    ItemStack result = menu.getCarried().copy();
                    if(remainingCount > 1) {
                        serverPlayer.getInventory().placeItemBackInInventory(result);
                        menu.setCarried(carried.copyWithCount(remainingCount - 1));
                    }
                    //Play client only sound for item
                    InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), fluidSound, true);
                } else {
                    carried.setCount(remainingCount);
                }
            }*/ else if(carried.getItem() instanceof PotionItem && carried.getItem() != Items.GLASS_BOTTLE) {
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
                ItemStack newCarried = tryFillPotion(carried, tank, serverPlayer, true);
                if(!newCarried.isEmpty()) {
                    ItemStack result = tryFillPotion(carried, tank, serverPlayer, false);
                    InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, false);
                    menu.setCarried(result);
                }
            }
        }
    }

    public static boolean tryEmptyPotion(ItemStack carried, FluidStacksResourceHandler tank, int potionType) {
        int amount = Reference.POTION;
        FluidStack fluidStack = new FluidStack(ModFluids.POTION_FLUID.get(), amount);
        FluidStackHelper.setFluidStackData(carried, fluidStack, potionType);
        if(StacksHandlerUtils.isEmpty(tank) || FluidStack.isSameFluidSameComponents(StacksHandlerUtils.getFluid(tank), fluidStack)) {
            if(StacksHandlerUtils.getFluidAmount(tank) + amount <= StacksHandlerUtils.getCapacity(tank)) {
                StacksHandlerUtils.fill(tank, fluidStack, false);
                return true;
            }
        }
        return false;
    }

    public static ItemStack tryFillPotion(ItemStack carried, FluidStacksResourceHandler tank, ServerPlayer player, boolean simulate) {
        if(StacksHandlerUtils.getFluid(tank).getFluid() == ModFluids.POTION_FLUID.get() && StacksHandlerUtils.getFluidAmount(tank) >= Reference.POTION) {
            ItemStack filledPotion = FluidStackHelper.getItemStackFromFluidStack(StacksHandlerUtils.getFluid(tank));
            if(simulate) {
                return filledPotion; //Return for simulate to check if it's possible to fill the bottle
            }
            ItemStack carriedCopy = carried.copy();
            int count = carriedCopy.getCount();
            carriedCopy.setCount(count - 1);
            StacksHandlerUtils.drain(tank, Reference.POTION, false);
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