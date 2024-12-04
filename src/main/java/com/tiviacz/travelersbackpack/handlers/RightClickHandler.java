package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpackneo.init.ModItems;
import com.tiviacz.travelersbackpackneo.init.ModTags;
import com.tiviacz.travelersbackpackold.TravelersBackpack;
import com.tiviacz.travelersbackpackold.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpackold.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpackold.common.recipes.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpackold.component.ComponentUtils;
import com.tiviacz.travelersbackpackold.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpackold.inventory.Tiers;
import com.tiviacz.travelersbackpackold.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpackold.items.UpgradeItem;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

public class RightClickHandler {
    public static void registerListeners() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            //Quick Unequip
            if (TravelersBackpackConfig.getConfig().backpackSettings.rightClickUnequip && !TravelersBackpack.enableIntegration()) {
                if (ComponentUtils.isWearingBackpack(player) && !world.isClient) {
                    if (player.isSneaking() && hand == Hand.MAIN_HAND && player.getMainHandStack().isEmpty()) {
                        ItemStack backpackStack = ComponentUtils.getWearingBackpack(player);
                        ItemUsageContext context = new ItemUsageContext(world, player, hand, backpackStack, hitResult);
                        boolean quickPickupFlag = world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof TravelersBackpackBlock;

                        if (!quickPickupFlag && backpackStack.getItem() instanceof TravelersBackpackItem item) {
                            if (item.place(new ItemPlacementContext(context)) == ActionResult.success(world.isClient)) {
                                player.swingHand(hand, true);
                                world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER.value(), SoundCategory.PLAYERS, 1.05F, (1.0F + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F) * 0.7F);
                                ComponentUtils.getComponent(player).removeWearable();
                                ComponentUtils.sync(player);
                                return ActionResult.SUCCESS;
                            }
                        }
                    }
                }
            }

            if (player.isSneaking() && hand == Hand.MAIN_HAND && player.getMainHandStack().isIn(ModTags.SLEEPING_BAGS) && world.getBlockEntity(hitResult.getBlockPos()) instanceof TravelersBackpackBlockEntity blockEntity) {
                ItemStack oldSleepingBag = blockEntity.getProperSleepingBag().getBlock().asItem().getDefaultStack();
                blockEntity.setSleepingBagColor(ShapedBackpackRecipe.getProperColor(player.getMainHandStack().getItem()));
                if (!world.isClient) {
                    ItemScatterer.spawn(world, hitResult.getBlockPos().getX(), hitResult.getBlockPos().up().getY(), hitResult.getBlockPos().getZ(), oldSleepingBag);
                    player.getMainHandStack().decrement(1);
                }
                world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER.value(), SoundCategory.PLAYERS, 1.0F, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);
                return ActionResult.SUCCESS;
            }

            //Reset backpack tiers
            if (player.isSneaking() && player.getMainHandStack().getItem() == ModItems.BLANK_UPGRADE && world.getBlockEntity(hitResult.getBlockPos()) instanceof TravelersBackpackBlockEntity blockEntity) {
                DefaultedList<ItemStack> list = DefaultedList.of();

                for (int i = 0; i < blockEntity.getCombinedInventory().size(); i++) {
                    ItemStack stackInSlot = blockEntity.getCombinedInventory().getStack(i);

                    if (!stackInSlot.isEmpty()) {
                        list.add(stackInSlot);
                        blockEntity.getCombinedInventory().setStack(i, ItemStack.EMPTY);
                    }
                }

                list.addAll(UpgradeItem.getUpgrades(blockEntity));

                //Remove unsortable slots
                if (!blockEntity.getSlotManager().getUnsortableSlots().isEmpty()) {
                    blockEntity.getSlotManager().getUnsortableSlots().clear();
                }

                //Remove memory slots
                if (!blockEntity.getSlotManager().getMemorySlots().isEmpty()) {
                    blockEntity.getSlotManager().getMemorySlots().clear();
                }

                //Drain excessive fluid
                long fluidAmountLeft = blockEntity.getLeftTank().getAmount();

                if (fluidAmountLeft > Tiers.LEATHER.getTankCapacity()) {
                    blockEntity.getLeftTank().amount = fluidAmountLeft - Tiers.LEATHER.getTankCapacity();
                }

                long fluidAmountRight = blockEntity.getRightTank().getAmount();

                if (fluidAmountRight > Tiers.LEATHER.getTankCapacity()) {
                    blockEntity.getRightTank().amount = fluidAmountRight - Tiers.LEATHER.getTankCapacity();
                }

                if (!world.isClient) {
                    ItemScatterer.spawn(world, hitResult.getBlockPos().up(), list);
                }

                //Change size of Tool slots and Storage slots
                blockEntity.getInventory().setSize(Tiers.LEATHER.getStorageSlots());
                blockEntity.getToolSlotsInventory().setSize(Tiers.LEATHER.getToolSlots());

                //Reset tier
                blockEntity.resetTier();

                //Reset Tanks
                blockEntity.getLeftTank().setCapacity(Tiers.LEATHER.getTankCapacity());
                blockEntity.getRightTank().setCapacity(Tiers.LEATHER.getTankCapacity());

                //Reset Settings
                blockEntity.getSettingsManager().readDefaults();

                player.swingHand(Hand.MAIN_HAND, true);
                return ActionResult.SUCCESS;
            }

            //Quick Equip
            if (TravelersBackpackConfig.getConfig().backpackSettings.rightClickEquip && world.getBlockEntity(hitResult.getBlockPos()) instanceof TravelersBackpackBlockEntity blockEntity) {
                if (player.isSneaking() && !ComponentUtils.isWearingBackpack(player) && !TravelersBackpack.enableIntegration()) {
                    ItemStack stack = new ItemStack(world.getBlockState(hitResult.getBlockPos()).getBlock(), 1).copy();
                    blockEntity.transferToItemStack(stack);
                    Direction direction = world.getBlockState(hitResult.getBlockPos()).get(TravelersBackpackBlock.FACING);

                    if (!world.isClient && world.setBlockState(hitResult.getBlockPos(), Blocks.AIR.getDefaultState())) {
                        ComponentUtils.equipBackpack(player, stack);
                        blockEntity.removeSleepingBag(world, direction);
                        return ActionResult.SUCCESS;
                    }
                }
            }

            //Quick Pick-Up
            if (world.getBlockEntity(hitResult.getBlockPos()) instanceof TravelersBackpackBlockEntity blockEntity) {
                if (player.isSneaking() && hand == Hand.MAIN_HAND && player.getMainHandStack().isEmpty()) {
                    ItemStack stack = new ItemStack(world.getBlockState(hitResult.getBlockPos()).getBlock(), 1).copy();
                    blockEntity.transferToItemStack(stack);
                    Direction direction = world.getBlockState(hitResult.getBlockPos()).get(TravelersBackpackBlock.FACING);

                    if (!world.isClient && world.setBlockState(hitResult.getBlockPos(), Blocks.AIR.getDefaultState())) {
                        player.setStackInHand(Hand.MAIN_HAND, stack);
                        blockEntity.removeSleepingBag(world, direction);
                        world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER.value(), SoundCategory.PLAYERS, 1.05F, (1.0F + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F) * 0.7F);
                        return ActionResult.SUCCESS;
                    }
                }
            }
            return ActionResult.PASS;
        });
    }
}