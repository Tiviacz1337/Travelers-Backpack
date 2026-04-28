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
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InventoryActions {
    public static boolean transferContainerTank(TanksUpgrade upgrade, ItemStack stackIn, FluidTank tank, int slotIn) {
        ItemStackHandler itemStackHandler = upgrade.getFluidSlotsHandler();
        int slotOut = slotIn + 1;

        if(tank == null || stackIn.isEmpty() || stackIn.getItem() == Items.AIR) return false;

        // --- POTION PART ---
        if(stackIn.getItem() instanceof PotionItem && stackIn.getItem() != Items.GLASS_BOTTLE) {
            //boolean hasFluidHandler = FluidUtil.getFluidHandler(stackIn).isPresent();

            //if(!hasFluidHandler) {
                int amount = Reference.POTION;
                FluidStack fluidStack = new FluidStack(ModFluids.POTION_FLUID.get(), amount);
                int potionType = 0;
                if(stackIn.getItem() == Items.SPLASH_POTION) potionType = 1;
                if(stackIn.getItem() == Items.LINGERING_POTION) potionType = 2;
                FluidStackHelper.setFluidStackNBT(stackIn, fluidStack, potionType);

                if(tank.isEmpty() || tank.getFluid().isFluidEqual(fluidStack)) {
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
            //}
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
        IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stackIn).orElse(null);

        if(fluidHandler != null) {
            FluidStack fluidStackCopy = fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);

            //Container ===> Tank

            if(fluidStackCopy.getAmount() > 0) {
                if(tank.getFluidAmount() <= 0 || fluidStackCopy.isFluidEqual(tank.getFluid())) {
                    //Fluid sound
                    SoundEvent fluidSound = fluidStackCopy.getFluid().getFluidType().getSound(fluidStackCopy, SoundActions.BUCKET_EMPTY);

                    if(transferFluid(itemStackHandler, tank, stackIn, slotOut, true)) {
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

            if(isSameFluid(stackIn, tank)) {
                //Fluid sound
                SoundEvent fluidSound = tank.getFluid().getFluid().getFluidType().getSound(tank.getFluid(), SoundActions.BUCKET_FILL);

                if(transferFluid(itemStackHandler, tank, stackIn, slotOut, false)) {
                    //Play fill sound
                    playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), fluidSound, true);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean transferFluid(ItemStackHandler handler, FluidTank tank, ItemStack stack, int slotOut, boolean isFilling) {
        ItemStackHandler tempHandler = new ItemStackHandler(1);
        //1 - Current output item in slotOut
        tempHandler.setStackInSlot(0, handler.getStackInSlot(slotOut));

        FluidActionResult result = isFilling ? FluidUtil.tryEmptyContainer(stack, tank, tank.getCapacity(), null, false) : FluidUtil.tryFillContainer(stack, tank, tank.getCapacity(), null, false);

        if(result.isSuccess()) {
            ItemStack stackResult = result.getResult();
            ItemStack insertResult = tempHandler.insertItem(0, stackResult, true);

            //Success
            if(insertResult.isEmpty()) {
                ItemStack finalResult = isFilling ? FluidUtil.tryEmptyContainer(stack, tank, tank.getCapacity(), null, true).getResult() : FluidUtil.tryFillContainer(stack, tank, tank.getCapacity(), null, true).getResult();

                //Forge only //#TODO check if it works natively
                if(isFilling) {
                    if(stack.getItem() == Items.WATER_BUCKET && EnchantmentHelper.getEnchantments(stack).containsKey(Enchantments.INFINITY_ARROWS)) {
                        finalResult = stack;
                    }
                }
                tempHandler.insertItem(0, finalResult, false);

                //Shrink the stack in input slot
                stack.shrink(1);

                //Set result stack in output slot
                handler.setStackInSlot(slotOut, tempHandler.getStackInSlot(0).copy());
                return true;
            }
        }
        return false;
    }

    private static boolean isSameFluid(ItemStack stack, FluidTank tank) {
        IFluidHandlerItem fluidHandler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if(fluidHandler != null) {
            //Search for same fluid or empty
            for(int i = 0; i < fluidHandler.getTanks(); i++) {
                FluidStack fluidStack = fluidHandler.getFluidInTank(i);
                boolean sameFluid = tank.getFluidAmount() > 0 && fluidStack.isFluidEqual(tank.getFluid());
                boolean emptyHandler = fluidStack.isEmpty();

                if(emptyHandler || sameFluid) {
                    return true;
                }
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