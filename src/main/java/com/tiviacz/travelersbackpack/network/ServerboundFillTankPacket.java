package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.InventoryActions;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.util.FluidStackHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundFillTankPacket {
    private final boolean leftTank;

    public ServerboundFillTankPacket(boolean leftTank) {
        this.leftTank = leftTank;
    }

    public static ServerboundFillTankPacket decode(final FriendlyByteBuf buffer) {
        final boolean leftTank = buffer.readBoolean();

        return new ServerboundFillTankPacket(leftTank);
    }

    public static void encode(final ServerboundFillTankPacket message, final FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.leftTank);
    }

    public static void handle(final ServerboundFillTankPacket message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
                BackpackWrapper wrapper = menu.getWrapper();
                FluidTank tank = message.leftTank ? wrapper.getUpgradeManager().tanksUpgrade.get().getLeftTank() : wrapper.getUpgradeManager().tanksUpgrade.get().getRightTank();
                ItemStack carried = menu.getCarried();
                if(FluidUtil.getFluidContained(carried).isPresent() && carried.getCount() == 1) {
                    //Fluid sound
                    SoundEvent fluidSound = tank.isEmpty() ? SoundEvents.BUCKET_EMPTY : tank.getFluid().getFluid().getFluidType().getSound(tank.getFluid(), SoundActions.BUCKET_EMPTY);

                    FluidActionResult result = FluidUtil.tryEmptyContainer(carried, tank, wrapper.getBackpackTankCapacity(), wrapper.getScreenID() == Reference.ITEM_SCREEN_ID ? null : serverPlayer, true);
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
                    SoundEvent fluidSound = tank.isEmpty() ? SoundEvents.BUCKET_FILL : tank.getFluid().getFluid().getFluidType().getSound(tank.getFluid(), SoundActions.BUCKET_FILL);

                    FluidActionResult result = FluidUtil.tryFillContainer(carried, tank, wrapper.getBackpackTankCapacity(), wrapper.getScreenID() == Reference.ITEM_SCREEN_ID ? null : serverPlayer, true);
                    if(result.isSuccess()) {
                        if(carriedCopy.getCount() > 0) {
                            serverPlayer.getInventory().placeItemBackInInventory(result.getResult());
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
                    ItemStack newCarried = tryFillPotion(carried, tank, serverPlayer, true);
                    if(!newCarried.isEmpty()) {
                        ItemStack result = tryFillPotion(carried, tank, serverPlayer, false);
                        InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, false);
                        menu.setCarried(result);
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }

    public static boolean tryEmptyPotion(ItemStack carried, FluidTank tank, int potionType) {
        int amount = Reference.POTION;
        FluidStack fluidStack = new FluidStack(ModFluids.POTION_FLUID.get(), amount);
        FluidStackHelper.setFluidStackNBT(carried, fluidStack, potionType);
        if(tank.isEmpty() || tank.getFluid().isFluidEqual(fluidStack)) {
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