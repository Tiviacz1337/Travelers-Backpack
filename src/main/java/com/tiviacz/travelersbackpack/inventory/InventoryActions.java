package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.util.FluidStackHelper;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class InventoryActions {
    public static boolean transferContainerTank(TanksUpgrade upgrade, FluidTank tank, int slotIn) {
        ItemStackHandler itemStackHandler = upgrade.getFluidSlotsHandler();

        ItemStack stackIn = itemStackHandler.getStackInSlot(slotIn);
        int slotOut = slotIn + 1;

        if(tank == null || stackIn.isEmpty() || stackIn.getItem() == Items.AIR) return false;

        // --- POTION PART ---
        if(stackIn.getItem() instanceof PotionItem && stackIn.getItem() != Items.GLASS_BOTTLE) {
            boolean hasFluidHandler = FluidUtil.getFluidHandler(stackIn).isPresent();

            if(!hasFluidHandler) {
                int amount = Reference.POTION;
                FluidStack fluidStack = new FluidStack(ModFluids.POTION_FLUID.get(), amount);
                int potionType = 0;
                if(stackIn.getItem() == Items.SPLASH_POTION) potionType = 1;
                if(stackIn.getItem() == Items.LINGERING_POTION) potionType = 2;
                FluidStackHelper.setFluidStackData(stackIn, fluidStack, potionType);

                if(tank.isEmpty() || FluidStack.isSameFluidSameComponents(tank.getFluid(), fluidStack)) {
                    if(tank.getFluidAmount() + amount <= tank.getCapacity()) {
                        ItemStack bottle = potionType != 0 ? ItemStack.EMPTY.copy() : new ItemStack(Items.GLASS_BOTTLE);
                        ItemStack currentStackOut = itemStackHandler.getStackInSlot(slotOut);

                        if(currentStackOut.isEmpty() || currentStackOut.getItem() == bottle.getItem() || bottle.isEmpty()) {
                            if(currentStackOut.getItem() == bottle.getItem() && !bottle.isEmpty()) {
                                if(currentStackOut.getCount() + 1 > currentStackOut.getMaxStackSize()) return false;

                                bottle.setCount(itemStackHandler.getStackInSlot(slotOut).getCount() + 1);
                            }

                            tank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
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
            if(tank.getFluid().getFluid() == ModFluids.POTION_FLUID.get() && tank.getFluidAmount() >= Reference.POTION) {
                ItemStack stackOut = FluidStackHelper.getItemStackFromFluidStack(tank.getFluid());
                ItemStack currentStackOut = itemStackHandler.getStackInSlot(slotOut);

                if(currentStackOut.isEmpty()) {
                    tank.drain(Reference.POTION, IFluidHandler.FluidAction.EXECUTE);
                    InventoryHelper.removeItem(upgrade.getFluidSlotsHandler(), slotIn, 1);
                    itemStackHandler.setStackInSlot(slotOut, stackOut);

                    playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, false);

                    return true;
                }
            }
        }
        // --- POTION PART ---

        Optional<IFluidHandlerItem> fluidHandler = FluidUtil.getFluidHandler(stackIn);

        if(fluidHandler.isPresent()) {
            Optional<FluidStack> fluidstack = FluidUtil.getFluidContained(stackIn);

            //Container ===> Tank

            if(fluidstack.isPresent() && fluidstack.map(FluidStack::getAmount).orElse(0) > 0) {
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
                        } */
                        playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), fluidSound, false);
                        FluidUtil.tryEmptyContainer(stackIn, tank, amount, null, true);

                        itemStackHandler.setStackInSlot(slotOut, stackOut);
                        InventoryHelper.removeItem(upgrade.getFluidSlotsHandler(), slotIn, 1);

                        return true;
                    }
                }
            }

            //Tank ===> Container

            if(tank.isEmpty() || tank.getFluidAmount() <= 0) return false;

            if(isFluidEqual(stackIn, tank)) {
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
            }
        }
        return false;
    }

    private static boolean isFluidEqual(ItemStack stackIn, FluidTank tank) {
        if(FluidUtil.getFluidContained(stackIn).isPresent() && FluidUtil.getFluidContained(stackIn).map(FluidStack::getAmount).orElse(0) > 0) {
            return FluidUtil.getFluidContained(stackIn).map(fluidstack -> FluidStack.isSameFluidSameComponents(fluidstack, tank.getFluid())).orElse(false);
        } else return !FluidUtil.getFluidContained(stackIn).isPresent();
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
                //menu.player.level().playLocalSound(menu.player.position().x(), menu.player.position().y() + 0.5, menu.player.position().z(), soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }
        }
    }
}