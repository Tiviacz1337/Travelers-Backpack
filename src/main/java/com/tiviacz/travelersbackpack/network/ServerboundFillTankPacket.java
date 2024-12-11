package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.FluidTank;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import com.tiviacz.travelersbackpack.inventory.InventoryActions;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.util.FluidStackHelper;
import com.tiviacz.travelersbackpack.util.FluidTypeHelper;
import com.tiviacz.travelersbackpack.util.FluidUtil;
import com.tiviacz.travelersbackpack.util.Reference;
import com.tiviacz.travelersbackpack.init.ModFluids;
import dev.architectury.fluid.FluidStack;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;

public record ServerboundFillTankPacket(boolean leftTank) implements CustomPacketPayload {
    public static final Type<ServerboundFillTankPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "fill_tank"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundFillTankPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ServerboundFillTankPacket::leftTank,
            ServerboundFillTankPacket::new
    );

    public static void handle(final ServerboundFillTankPacket message, ServerPlayNetworking.Context ctx) {
        ctx.player().getServer().execute(() -> {
            Player player = ctx.player();
            if (player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
                BackpackWrapper wrapper = menu.getWrapper();
                FluidTank tank = message.leftTank() ? wrapper.getUpgradeManager().tanksUpgrade.get().getLeftTank() : wrapper.getUpgradeManager().tanksUpgrade.get().getRightTank();
                ItemStack carried = menu.getCarried();
                if (FluidUtil.getFluidStorageAtCursor(player, menu).isPresent() && FluidUtil.hasFluid(player, menu) && carried.getCount() == 1) {
                    //Fluid sound
                    SoundEvent fluidSound = tank.isEmpty() ? SoundEvents.BUCKET_EMPTY : FluidTypeHelper.getSound(tank.getFluid().fluidVariant(), FluidTypeHelper.BUCKET_EMPTY);

                    long result = FluidUtil.tryEmptyContainerAtCursor(tank, wrapper.getBackpackTankCapacity(), wrapper.getScreenID() == Reference.ITEM_SCREEN_ID ? null : serverPlayer, menu, true);
                    if (result > 0) {
                        //Play client only sound for item
                        if (wrapper.getScreenID() == Reference.ITEM_SCREEN_ID) {
                            InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), fluidSound, FluidTypeHelper.BUCKET_EMPTY);
                        }
                        //menu.setCarried();
                       // menu.setCarried(result.getResult());
                    }
                } else if (carried.getItem() instanceof BucketItem || (FluidUtil.getFluidStorageAtCursor(player, menu).isPresent() && FluidUtil.getFluidStorageAtCursor(player, menu).get().supportsInsertion())) {
                    ItemStack carriedCopy = carried.copy();
                    int count = carriedCopy.getCount();
                    carriedCopy.setCount(count - 1);

                    //Fluid sound
                    SoundEvent fluidSound = tank.isEmpty() ? SoundEvents.BUCKET_FILL : FluidTypeHelper.getSound(tank.getFluid().fluidVariant(), FluidTypeHelper.BUCKET_FILL);

                    if(carried.getItem() instanceof BucketItem) {
                        Item fullBucket = tank.getFluid().fluidVariant().getFluid().getBucket();
                        long result = FluidUtil.tryFillBucketAtCursor(tank, wrapper.getBackpackTankCapacity(), wrapper.getScreenID() == Reference.ITEM_SCREEN_ID ? null : serverPlayer, menu, true);
                        if(result > 0) {
                            if(carriedCopy.getCount() > 0) {
                                serverPlayer.getInventory().placeItemBackInInventory(fullBucket.getDefaultInstance());
                                menu.setCarried(carriedCopy);
                            } else {
                                menu.setCarried(fullBucket.getDefaultInstance());
                            }
                            //Play client only sound for item
                            if (wrapper.getScreenID() == Reference.ITEM_SCREEN_ID) {
                                InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), fluidSound, FluidTypeHelper.BUCKET_FILL);
                            }
                        }
                    } else if(carried.getCount() == 1){
                        long result = FluidUtil.tryFillContainerAtCursor(tank, wrapper.getBackpackTankCapacity(), wrapper.getScreenID() == Reference.ITEM_SCREEN_ID ? null : serverPlayer, menu, true);
                        if (result > 0) {
                           // if (carriedCopy.getCount() > 0) {
                                //serverPlayer.getInventory().placeItemBackInInventory(result.getResult());
                                //menu.setCarried(carriedCopy);
                          //  } else {
                                //menu.setCarried(result.getResult());
                           // }
                            //Play client only sound for item
                            if (wrapper.getScreenID() == Reference.ITEM_SCREEN_ID) {
                                InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), fluidSound, FluidTypeHelper.BUCKET_FILL);
                            }
                        }
                    }
                } else if (carried.getItem() instanceof PotionItem && carried.getItem() != Items.GLASS_BOTTLE) {
                    if (carried.getCount() == 1) {
                        if (tryEmptyPotion(carried, tank)) {
                            InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, true);
                            menu.setCarried(new ItemStack(Items.GLASS_BOTTLE));
                        }
                    }
                } else if (carried.getItem() == Items.GLASS_BOTTLE) {
                    ItemStack newCarried = tryFillPotion(carried, tank, serverPlayer, true);
                    if (!newCarried.isEmpty()) {
                        ItemStack result = tryFillPotion(carried, tank, serverPlayer, false);
                        InventoryActions.playFluidSound(wrapper.getBackpackOwner(), wrapper.getPlayersUsing(), SoundEvents.BREWING_STAND_BREW, false);
                        menu.setCarried(result);
                    }
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static boolean tryEmptyPotion(ItemStack carried, FluidTank tank) {
        long amount = FluidConstants.BOTTLE;
        //FluidVariantWrapper fluidStack = new FluidVariantWrapper(FluidVariant.of(ModFluids.POTION_STILL), amount);
        FluidVariant potionVariant = FluidStackHelper.setPotionFluidVariant(carried);
        FluidVariantWrapper potionVariantWrapper = new FluidVariantWrapper(potionVariant, amount);
        if (tank.isEmpty() || (potionVariantWrapper.fluidVariant().isOf(tank.getFluid().fluidVariant().getFluid())) && potionVariantWrapper.fluidVariant().componentsMatch(tank.getFluid().fluidVariant().getComponents())) {
            if (tank.getFluidAmount() + amount <= tank.getCapacity()) {
                tank.fill(potionVariantWrapper, false);
                return true;
            }
        }
        return false;
    }

    public static ItemStack tryFillPotion(ItemStack carried, FluidTank tank, ServerPlayer player, boolean simulate) {
        if (tank.getFluid().fluidVariant().getFluid() == ModFluids.POTION_STILL && tank.getFluidAmount() >= FluidConstants.BOTTLE) {
            ItemStack filledPotion = FluidStackHelper.getItemStackFromFluidStack(tank.getFluid().fluidVariant());
            if (simulate) {
                return filledPotion; //Return for simulate to check if it's possible to fill the bottle
            }
            ItemStack carriedCopy = carried.copy();
            int count = carriedCopy.getCount();
            carriedCopy.setCount(count - 1);
            tank.drain(FluidConstants.BOTTLE, false);
            if (carriedCopy.getCount() > 0) {
                player.getInventory().placeItemBackInInventory(filledPotion);
                return carriedCopy;
            } else {
                return filledPotion;
            }
        }
        return ItemStack.EMPTY;
    }
}
