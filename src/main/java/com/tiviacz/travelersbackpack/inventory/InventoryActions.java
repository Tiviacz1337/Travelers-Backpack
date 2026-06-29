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
import net.neoforged.neoforge.transfer.resource.ResourceStack;
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
                FluidStack fluidStack = new FluidStack(ModFluids.POTION_FLUID.get(), Reference.POTION);
                int potionType = 0;
                if(stackIn.getItem() == Items.SPLASH_POTION) potionType = 1;
                if(stackIn.getItem() == Items.LINGERING_POTION) potionType = 2;
                FluidStackHelper.setFluidStackData(stackIn, fluidStack, potionType);
                ItemStack bottle = potionType != 0 ? ItemStack.EMPTY.copy() : new ItemStack(Items.GLASS_BOTTLE);

                if(transferPotion(itemStackHandler, new ResourceStack<>(FluidResource.of(fluidStack), fluidStack.amount()), 0, tank, stackIn, bottle, slotOut, true)) {
                    playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, true);
                    return true;
                }
            }
        }

        if(stackIn.getItem() == Items.GLASS_BOTTLE) {
            if(StacksHandlerUtils.getFluid(tank).getFluid() == ModFluids.POTION_FLUID.get() && StacksHandlerUtils.getFluidAmount(tank) >= Reference.POTION) {
                ItemStack stackOut = FluidStackHelper.getItemStackFromFluidStack(StacksHandlerUtils.getFluid(tank));
                if(transferPotion(itemStackHandler, new ResourceStack<>(FluidResource.EMPTY, 0), Reference.POTION, tank, stackIn, stackOut, slotOut, false)) {
                    playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, false);
                    return true;
                }
            }
        }
        // --- POTION PART ---

        ResourceHandler<FluidResource> fluidHandler = ItemAccess.forStack(stackIn).getCapability(Capabilities.Fluid.ITEM);

        //Insert to handler because it does not extract otherwise
        ItemStacksResourceHandler handler = new ItemStacksResourceHandler(1);
        handler.set(0, ItemResource.of(stackIn), 1);
        ResourceHandler<FluidResource> fluidHandlerCopy = ItemAccess.forHandlerIndex(handler, 0).getCapability(Capabilities.Fluid.ITEM);

        if(fluidHandler != null && fluidHandlerCopy != null) {
            FluidResource resource = ResourceHandlerUtil.findExtractableResource(fluidHandlerCopy, p -> true, null);
            int index = 0;
            if(resource == null) {
                resource = FluidResource.EMPTY;
            }
            if(!resource.isEmpty()) {
                index = ResourceHandlerUtil.indexOf(fluidHandler, resource);
            }
            int resourceAmount = fluidHandler.getAmountAsInt(index);
            FluidStack fluidStackCopy = resource.toStack(resourceAmount);

            //Container ===> Tank

            if(resourceAmount > 0) {
                if(StacksHandlerUtils.getFluidAmount(tank) <= 0 || FluidStack.isSameFluidSameComponents(StacksHandlerUtils.getFluid(tank), fluidStackCopy)) {
                    //Fluid sound
                    SoundEvent fluidSound = fluidStackCopy.getFluidType().getSound(fluidStackCopy, SoundActions.BUCKET_EMPTY);

                    if(transferFluid(itemStackHandler, tank, stackIn, slotOut, index, true)) {
                        //Play fill sound
                        playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), fluidSound, false);
                        return true;
                    }
                }
            }

            //Tank ===> Container

            if(StacksHandlerUtils.isEmpty(tank) || StacksHandlerUtils.getFluidAmount(tank) <= 0) {
                return false;
            }

            if(isSameFluid(stackIn, tank)) {
                //Fluid sound
                SoundEvent fluidSound = StacksHandlerUtils.getFluid(tank).getFluidType().getSound(StacksHandlerUtils.getFluid(tank), SoundActions.BUCKET_FILL);

                if(transferFluid(itemStackHandler, tank, stackIn, slotOut, index, false)) {
                    //Play fill sound
                    playFluidSound(upgrade.getUpgradeManager().getWrapper().getBackpackOwner(), upgrade.getUpgradeManager().getWrapper().getPlayersUsing(), fluidSound, true);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean transferFluid(ItemStacksResourceHandler stacksResourceHandler, FluidStacksResourceHandler fluidResourceHandler, ItemStack stack, int slotOut, int index, boolean isFilling) {
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
                    txNested.close();
                    return false;
                }
            }

            //Insert current output to slot 1
            StacksHandlerUtils.setStackInSlot(tempHandler, 1, StacksHandlerUtils.getStackInSlot(stacksResourceHandler, slotOut)); //Set current output item to temp

            //Mutable resource handler in temp handler
            ResourceHandler<FluidResource> fluidHandlerSlot = ItemAccess.forHandlerIndex(tempHandler, 0).getCapability(Capabilities.Fluid.ITEM);

            if(fluidHandlerSlot != null) {
                int fill = ResourceHandlerUtil.move(isFilling ? fluidHandlerSlot : fluidResourceHandler, isFilling ? fluidResourceHandler : fluidHandlerSlot, p -> true, isFilling ? fluidHandlerSlot.getAmountAsInt(index) : fluidResourceHandler.getAmountAsInt(0), tx);
                if(fill > 0) {
                    try(var txNested = Transaction.open(tx)) {
                        int inserted = tempHandler.insert(1, tempHandler.getResource(0), tempHandler.getAmountAsInt(0), txNested);
                        if(inserted > 0) {
                            txNested.commit();
                        } else {
                            txNested.close();
                            return false;
                        }
                    }

                    //Shrink the stack in input slot
                    stack.shrink(1);

                    //Set result stack in output slot
                    StacksHandlerUtils.setStackInSlot(stacksResourceHandler, slotOut, StacksHandlerUtils.getStackInSlot(tempHandler, 1));
                    tx.commit();
                    return true;
                } else {
                    tx.close();
                }
            }
        }
        return false;
    }

    public static boolean transferPotion(ItemStacksResourceHandler stacksResourceHandler, ResourceStack<FluidResource> fluidResource, int fluidAmount, FluidStacksResourceHandler fluidResourceHandler, ItemStack stackIn, ItemStack bottleResult, int slotOut, boolean isFilling) {
        try(var tx = Transaction.openRoot()) {
            int filledFluid = 0;
            if(isFilling) {
                filledFluid = ResourceHandlerUtil.insertStacking(fluidResourceHandler, fluidResource.resource(), fluidResource.amount(), tx);
            } else {
                var extractedResource = ResourceHandlerUtil.extractFirst(fluidResourceHandler, _ -> true, fluidAmount, tx);
                if(extractedResource != null) {
                    filledFluid = extractedResource.amount();
                }
            }

            if(filledFluid <= 0) {
                return false;
            }

            ItemStacksResourceHandler tempHandler = new ItemStacksResourceHandler(1);
            StacksHandlerUtils.setStackInSlot(tempHandler, 0, StacksHandlerUtils.getStackInSlot(stacksResourceHandler, slotOut));

            int inserted = tempHandler.insert(0, ItemResource.of(bottleResult), 1, tx);

            if(inserted != 1) {
                return false;
            }

            stackIn.shrink(1);
            StacksHandlerUtils.setStackInSlot(stacksResourceHandler, slotOut, StacksHandlerUtils.getStackInSlot(tempHandler, 0));

            tx.commit();
            return true;
        }
    }

    private static boolean isSameFluid(ItemStack stack, FluidStacksResourceHandler tank) {
        ResourceHandler<FluidResource> handler = ItemAccess.forStack(stack).getCapability(Capabilities.Fluid.ITEM);
        if(handler != null) {
            //Search for same fluid or empty
            for(int i = 0; i < handler.size(); i++) {
                FluidResource resource = handler.getResource(i);
                int resourceAmount = handler.getAmountAsInt(i);
                FluidStack fluidStackCopy = resource.toStack(resourceAmount);

                boolean sameFluid = StacksHandlerUtils.getFluidAmount(tank) > 0 && FluidStack.isSameFluidSameComponents(StacksHandlerUtils.getFluid(tank), fluidStackCopy);
                boolean emptyHandler = resource.isEmpty();

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
            if(user.containerMenu instanceof BackpackItemMenu menu && menu.getWrapper().getScreenID() == Reference.ITEM_SCREEN_ID && !menu.player.level().isClientSide()) {
                var vec3 = menu.player.blockPosition().getCenter();
                menu.player.level().playSound(null, vec3.x(), vec3.y(), vec3.z(), soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }
}