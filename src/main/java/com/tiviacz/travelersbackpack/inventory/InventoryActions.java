package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.util.FluidStackHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import com.tiviacz.travelersbackpack.util.StacksHandlerUtils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class InventoryActions {
    public static boolean transferContainerTank(TanksUpgrade upgrade, ItemStack stackIn, FluidStacksResourceHandler tank, int slotIn) {
        ItemStacksResourceHandler itemStackHandler = upgrade.getFluidSlotsHandler();
        int slotOut = slotIn + 1;

        if(tank == null || stackIn.isEmpty() || stackIn.getItem() == Items.AIR) return false;

        // --- POTION PART ---
        if(stackIn.getItem() instanceof PotionItem && stackIn.getItem() != Items.GLASS_BOTTLE) {
            boolean hasFluidHandler = Optional.ofNullable(ItemAccess.forStack(stackIn).getCapability(Capabilities.Fluid.ITEM)).isPresent();

            if(!hasFluidHandler) {
                int amount = Reference.POTION;
                FluidStack fluidStack = new FluidStack(ModFluids.POTION_FLUID.get(), amount);
                int potionType = 0;
                if(stackIn.getItem() == Items.SPLASH_POTION) potionType = 1;
                if(stackIn.getItem() == Items.LINGERING_POTION) potionType = 2;
                FluidStackHelper.setFluidStackData(stackIn, fluidStack, potionType);

                if(StacksHandlerUtils.isEmpty(tank) || FluidStack.isSameFluidSameComponents(StacksHandlerUtils.getFluid(tank), fluidStack)) {
                    if(StacksHandlerUtils.getFluidAmount(tank) + amount <= StacksHandlerUtils.getCapacity(tank)) {
                        ItemStack bottle = potionType != 0 ? ItemStack.EMPTY.copy() : new ItemStack(Items.GLASS_BOTTLE);
                        ItemStack currentStackOut = StacksHandlerUtils.getStackInSlot(itemStackHandler, slotOut);

                        if(currentStackOut.isEmpty() || currentStackOut.getItem() == bottle.getItem() || bottle.isEmpty()) {
                            if(currentStackOut.getItem() == bottle.getItem() && !bottle.isEmpty()) {
                                if(currentStackOut.getCount() + 1 > currentStackOut.getMaxStackSize()) return false;

                                bottle.setCount(StacksHandlerUtils.getStackInSlot(itemStackHandler, slotOut).getCount() + 1);
                            }

                            StacksHandlerUtils.fill(tank, fluidStack, false);
                            stackIn.shrink(1);
                            if(!bottle.isEmpty()) {
                                StacksHandlerUtils.setStackInSlot(itemStackHandler, slotOut, bottle);
                            }
                            playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, true);

                            return true;
                        }
                    }
                }
            }
        }

        if(stackIn.getItem() == Items.GLASS_BOTTLE) {
            if(StacksHandlerUtils.getFluid(tank).getFluid() == ModFluids.POTION_FLUID.get() && StacksHandlerUtils.getFluidAmount(tank) >= Reference.POTION) {
                ItemStack stackOut = FluidStackHelper.getItemStackFromFluidStack(StacksHandlerUtils.getFluid(tank));
                ItemStack currentStackOut = StacksHandlerUtils.getStackInSlot(itemStackHandler, slotOut);

                if(currentStackOut.isEmpty()) {
                    StacksHandlerUtils.drain(tank, Reference.POTION, false);
                    stackIn.shrink(1);
                    StacksHandlerUtils.setStackInSlot(itemStackHandler, slotOut, stackOut);

                    playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, false);

                    return true;
                }
            }
        }
        // --- POTION PART ---

        ResourceHandler<FluidResource> fluidHandler = ItemAccess.forStack(stackIn).getCapability(Capabilities.Fluid.ITEM);

        if(fluidHandler != null) {
            FluidResource resource = fluidHandler.getResource(0);
            int resourceAmount = fluidHandler.getAmountAsInt(0);
            FluidStack fluidStackCopy = resource.toStack(resourceAmount);

            //Container ===> Tank

            if(resourceAmount > 0) {
                if(StacksHandlerUtils.getFluidAmount(tank) > 0 && !FluidStack.isSameFluidSameComponents(StacksHandlerUtils.getFluid(tank), fluidStackCopy))
                    return false;

                //Fluid sound
                SoundEvent fluidSound = fluidStackCopy.getFluidType().getSound(fluidStackCopy, SoundActions.BUCKET_EMPTY);

                if(transferFluid(itemStackHandler, tank, stackIn, slotOut, true)) {
                    //Play fill sound
                    playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), fluidSound, false);
                    return true;
                }
            }

            //Tank ===> Container

            if(StacksHandlerUtils.isEmpty(tank) || StacksHandlerUtils.getFluidAmount(tank) <= 0) {
                return false;
            }

            if(isSameFluid(stackIn, tank)) {
                //Fluid sound
                SoundEvent fluidSound = StacksHandlerUtils.getFluid(tank).getFluidType().getSound(StacksHandlerUtils.getFluid(tank), SoundActions.BUCKET_FILL);

                if(transferFluid(itemStackHandler, tank, stackIn, slotOut, false)) {
                    //Play fill sound
                    playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), fluidSound, true);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean transferFluid(ItemStacksResourceHandler stacksResourceHandler, FluidStacksResourceHandler fluidResourceHandler, ItemStack stack, int slotOut, boolean isFilling) {
        ItemStacksResourceHandler tempHandler = new ItemStacksResourceHandler(2);
        //0 - Input/Result
        //1 - Current output item in slotOut

        try(var tx = Transaction.openRoot()) {
            //Insert input to slot 0
            try(var txNested = Transaction.open(tx)) {
                int inserted = tempHandler.insert(0, ItemResource.of(stack), 1, txNested);
                if(inserted == 1) {
                    txNested.commit();
                } else {
                    return false;
                }
            }

            //Insert current output to slot 1
            StacksHandlerUtils.setStackInSlot(tempHandler, 1, StacksHandlerUtils.getStackInSlot(stacksResourceHandler, slotOut)); //Set current output item to temp

            //Mutable resource handler in temp handler
            ResourceHandler<FluidResource> fluidHandlerSlot = ItemAccess.forHandlerIndex(tempHandler, 0).getCapability(Capabilities.Fluid.ITEM);

            if(fluidHandlerSlot != null) {
                int fill = ResourceHandlerUtil.move(isFilling ? fluidHandlerSlot : fluidResourceHandler, isFilling ? fluidResourceHandler : fluidHandlerSlot, p -> true, isFilling ? fluidHandlerSlot.getAmountAsInt(0) : fluidResourceHandler.getAmountAsInt(0), tx);
                if(fill > 0) {
                    try(var txNested = Transaction.open(tx)) {
                        int inserted = tempHandler.insert(1, tempHandler.getResource(0), tempHandler.getAmountAsInt(0), txNested);
                        if(inserted > 0) {
                            txNested.commit();
                        } else {
                            return false;
                        }
                    }

                    //Shrink the stack in input slot
                    stack.shrink(1);

                    //Set result stack in output slot
                    StacksHandlerUtils.setStackInSlot(stacksResourceHandler, slotOut, StacksHandlerUtils.getStackInSlot(tempHandler, 1));
                    tx.commit();
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isSameFluid(ItemStack stack, FluidStacksResourceHandler tank) {
        ResourceHandler<FluidResource> handler = ItemAccess.forStack(stack).getCapability(Capabilities.Fluid.ITEM);
        if(handler != null) {
            FluidResource resource = handler.getResource(0);
            int resourceAmount = handler.getAmountAsInt(0);
            FluidStack fluidStackCopy = resource.toStack(resourceAmount);

            boolean sameFluid = StacksHandlerUtils.getFluidAmount(tank) > 0 && FluidStack.isSameFluidSameComponents(StacksHandlerUtils.getFluid(tank), fluidStackCopy);
            boolean emptyHandler = resource.isEmpty();

            return emptyHandler || sameFluid;
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