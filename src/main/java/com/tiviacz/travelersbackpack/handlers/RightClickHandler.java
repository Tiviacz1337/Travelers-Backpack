package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;

public class RightClickHandler {


    public static void registerListeners() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {

            BlockPos pos = hitResult.getBlockPos();

            //Quick Unequip
            if(TravelersBackpackConfig.getConfig().backpackSettings.rightClickUnequip && !TravelersBackpack.enableIntegration()) {
                if(ComponentUtils.isWearingBackpack(player) && !level.isClientSide) {
                    if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
                        ItemStack backpackStack = ComponentUtils.getWearingBackpack(player).copy();
                        UseOnContext context = new UseOnContext(level, player, hand, backpackStack, hitResult);
                        boolean quickPickupFlag = level.getBlockState(pos).getBlock() instanceof TravelersBackpackBlock;

                        if(!quickPickupFlag && backpackStack.getItem() instanceof TravelersBackpackItem item) {
                            if(item.place(new BlockPlaceContext(context)) == InteractionResult.sidedSuccess(level.isClientSide)) {
                                player.swing(hand, true);
                                level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.05F, (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);
                                ComponentUtils.getComponent(player).ifPresent(data -> {
                                    data.remove();
                                    data.synchronise();
                                });
                                return InteractionResult.SUCCESS;
                                //event.setCanceled(true);
                                //event.setCancellationResult(InteractionResult.SUCCESS);
                                //return;
                            }
                        }
                    }
                }
            }

            //Change Sleeping Bag
            if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().is(ModTags.SLEEPING_BAGS) && level.getBlockEntity(pos) instanceof BackpackBlockEntity blockEntity) {
                ItemStack oldSleepingBag = blockEntity.getProperSleepingBag().getBlock().asItem().getDefaultInstance();
                blockEntity.getWrapper().setSleepingBagColor(ShapedBackpackRecipe.getProperColor(player.getMainHandItem().getItem()));

                if(!level.isClientSide) {
                    Containers.dropItemStack(level, pos.getX(), pos.above().getY(), pos.getZ(), oldSleepingBag);
                    player.getMainHandItem().shrink(1);
                }
                level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);
                return InteractionResult.SUCCESS;
                //event.setCancellationResult(InteractionResult.SUCCESS);
                //event.setCanceled(true);
                //return;
            }

            //Remove custom backpack design (go back to standard)
            if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().is(Items.SHEARS) && level.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
                if(!backpackBlockEntity.getWrapper().getBackpackStack().is(ModItems.STANDARD_TRAVELERS_BACKPACK)) {
                    ItemStack standardBackpack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK, 1);
                    backpackBlockEntity.toItemStack(standardBackpack);
                    Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);
                    if(!level.isClientSide && level.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())) {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), standardBackpack);
                        backpackBlockEntity.removeSleepingBag(level, direction);
                        level.playSound(null, backpackBlockEntity.getBlockPos(), SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
                        player.gameEvent(GameEvent.SHEAR, player);
                        player.getMainHandItem().hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                    }
                    return InteractionResult.SUCCESS;
                    //event.setCancellationResult(InteractionResult.SUCCESS);
                    // event.setCanceled(true);
                    //return;
                }
            }

        /*if (player.isShiftKeyDown() && player.getMainHandItem().getItem() == ModItems.BLANK_UPGRADE.get() && level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity) {
            NonNullList<ItemStack> list = NonNullList.create(); //#TODO decide what to do with it

            for (int i = 0; i < blockEntity.getCombinedHandler().getSlots(); i++) {
                ItemStack stackInSlot = blockEntity.getCombinedHandler().getStackInSlot(i);

                if (!stackInSlot.isEmpty()) {
                    list.add(stackInSlot);
                    blockEntity.getCombinedHandler().setStackInSlot(i, ItemStack.EMPTY);
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
            int fluidAmountLeft = blockEntity.getLeftTank().isEmpty() ? 0 : blockEntity.getLeftTank().getFluidAmount();

            if (fluidAmountLeft > Tiers.LEATHER.getTankCapacity()) {
                blockEntity.getLeftTank().drain(fluidAmountLeft - Tiers.LEATHER.getTankCapacity(), IFluidHandler.FluidAction.EXECUTE);
            }

            int fluidAmountRight = blockEntity.getRightTank().isEmpty() ? 0 : blockEntity.getRightTank().getFluidAmount();

            if (fluidAmountRight > Tiers.LEATHER.getTankCapacity()) {
                blockEntity.getRightTank().drain(fluidAmountRight - Tiers.LEATHER.getTankCapacity(), IFluidHandler.FluidAction.EXECUTE);
            }

            if (!level.isClientSide) {
                Containers.dropContents(level, pos.above(), list);
            }

            //Change size of Tool slots and Storage slots
            blockEntity.getHandler().setSize(Tiers.LEATHER.getStorageSlots());
            blockEntity.getToolSlotsHandler().setSize(Tiers.LEATHER.getToolSlots());

            //Reset tier
            blockEntity.resetTier();

            //Reset Tanks
            blockEntity.getLeftTank().setCapacity(Tiers.LEATHER.getTankCapacity());
            blockEntity.getRightTank().setCapacity(Tiers.LEATHER.getTankCapacity());

            //Reset Settings
            blockEntity.getSettingsManager().loadDefaults();

            //player.swing(InteractionHand.MAIN_HAND, true);

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        } */

            //Quick Equip
            if(TravelersBackpackConfig.getConfig().backpackSettings.rightClickEquip && level.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
                if(player.isShiftKeyDown() && !ComponentUtils.isWearingBackpack(player) && !TravelersBackpack.enableIntegration()) {
                    //Prioritize placing block
                    if(player.getItemInHand(hand).getItem() instanceof BlockItem) {
                        return InteractionResult.PASS; //#TODO check
                    }
                    ItemStack backpack = new ItemStack(level.getBlockState(pos).getBlock(), 1).copy();
                    backpackBlockEntity.toItemStack(backpack);
                    Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                    if(!level.isClientSide && level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())) {
                        ComponentUtils.equipBackpack(player, backpack);
                        backpackBlockEntity.removeSleepingBag(level, direction);

                        return InteractionResult.SUCCESS;
                        //event.setCanceled(true);
                        //event.setCancellationResult(InteractionResult.SUCCESS);
                        //return;
                    }
                }
            }

            //Quick Pick-Up
            if(level.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
                if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
                    ItemStack backpack = new ItemStack(level.getBlockState(pos).getBlock(), 1).copy();
                    backpackBlockEntity.toItemStack(backpack);
                    Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                    if(!level.isClientSide && level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, backpack);
                        backpackBlockEntity.removeSleepingBag(level, direction);
                        level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);

                        return InteractionResult.SUCCESS;
                        //
                        // event.setCanceled(true);
                        //event.setCancellationResult(InteractionResult.SUCCESS);
                    }
                }
            }

            //Quick Unequip
       /*     if (TravelersBackpackConfig.getConfig().backpackSettings.rightClickUnequip && !TravelersBackpack.enableIntegration()) {
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
            } */
            return InteractionResult.PASS;
        });
    }
}