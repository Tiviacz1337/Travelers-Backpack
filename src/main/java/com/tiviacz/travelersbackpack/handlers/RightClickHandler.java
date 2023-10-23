package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.items.SleepingBagItem;
import com.tiviacz.travelersbackpack.items.TierUpgradeItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class RightClickHandler
{
    public static void registerListeners()
    {
        UseBlockCallback.EVENT.register(((player, world, hand, hitResult) ->
        {
            if(TravelersBackpackConfig.enableBackpackRightClickUnequip)
            {
                if(ComponentUtils.isWearingBackpack(player) && !world.isClient)
                {
                    if(player.isSneaking() && hand == Hand.MAIN_HAND && player.getStackInHand(Hand.MAIN_HAND).isEmpty())
                    {
                        ItemStack backpackStack = ComponentUtils.getWearingBackpack(player);
                        ItemUsageContext context = new ItemUsageContext(world, player, Hand.MAIN_HAND, backpackStack, hitResult);

                        if(backpackStack.getItem() instanceof TravelersBackpackItem)
                        {
                            TravelersBackpackItem item = (TravelersBackpackItem)backpackStack.getItem();

                            if(item.place(new ItemPlacementContext(context)) == ActionResult.success(world.isClient))
                            {
                                player.swingHand(Hand.MAIN_HAND, true);
                                world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.05F, (1.0F + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F) * 0.7F);

                                ComponentUtils.getComponent(player).removeWearable();

                                ComponentUtils.sync(player);
                                ComponentUtils.syncToTracking(player);

                                return ActionResult.SUCCESS;
                            }
                        }
                    }
                }
            }

            if(player.isSneaking() && hand == Hand.MAIN_HAND && player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof SleepingBagItem && player.world.getBlockEntity(hitResult.getBlockPos()) instanceof TravelersBackpackBlockEntity)
            {
                TravelersBackpackBlockEntity blockEntity = (TravelersBackpackBlockEntity)player.world.getBlockEntity(hitResult.getBlockPos());
                ItemStack oldSleepingBag = blockEntity.getProperSleepingBag(blockEntity.getSleepingBagColor()).getBlock().asItem().getDefaultStack();
                blockEntity.setSleepingBagColor(ShapedBackpackRecipe.getProperColor((SleepingBagItem)player.getStackInHand(Hand.MAIN_HAND).getItem()));
                if(!world.isClient)
                {
                    ItemScatterer.spawn(world, hitResult.getBlockPos().getX(), hitResult.getBlockPos().up().getY(), hitResult.getBlockPos().getZ(), oldSleepingBag);
                    player.getStackInHand(Hand.MAIN_HAND).decrement(1);
                }
                player.world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (player.world.random.nextFloat() - player.world.random.nextFloat()) * 0.2F) * 0.7F);
                return ActionResult.SUCCESS;
            }

            //Reset backpack tiers
            if(player.isSneaking() && player.getStackInHand(Hand.MAIN_HAND).getItem() == ModItems.BLANK_UPGRADE && world.getBlockEntity(hitResult.getBlockPos()) instanceof TravelersBackpackBlockEntity)
            {
                TravelersBackpackBlockEntity blockEntity = (TravelersBackpackBlockEntity)world.getBlockEntity(hitResult.getBlockPos());

                if(blockEntity.getTier() != Tiers.LEATHER)
                {
                    int storageSlots = blockEntity.getTier().getAllSlots();
                    DefaultedList<ItemStack> list = DefaultedList.of();

                    for(int i = 0; i < storageSlots; i++)
                    {
                        ItemStack stackInSlot = blockEntity.getInventory().getStack(i);

                        if(!stackInSlot.isEmpty())
                        {
                            list.add(stackInSlot);
                            blockEntity.getInventory().setStack(i, ItemStack.EMPTY);
                        }

                        if(i < 9)
                        {
                            ItemStack stackInSlot1 = blockEntity.getCraftingGridInventory().getStack(i);

                            if(!stackInSlot1.isEmpty())
                            {
                                list.add(stackInSlot1);
                                blockEntity.getCraftingGridInventory().setStack(i, ItemStack.EMPTY);
                            }
                        }
                    }

                    list.addAll(TierUpgradeItem.getUpgradesForTier(blockEntity.getTier()));

                    if(!blockEntity.getSlotManager().getUnsortableSlots().isEmpty())
                    {
                        blockEntity.getSlotManager().getUnsortableSlots().removeIf(i -> i > Tiers.LEATHER.getStorageSlots() - 7);
                    }

                    if(!blockEntity.getSlotManager().getMemorySlots().isEmpty())
                    {
                        blockEntity.getSlotManager().getMemorySlots().removeIf(p -> p.getFirst() > Tiers.LEATHER.getStorageSlots() - 7);
                    }

                    long fluidAmountLeft = blockEntity.getLeftTank().getAmount();

                    if(fluidAmountLeft > Tiers.LEATHER.getTankCapacity())
                    {
                        blockEntity.getLeftTank().amount = fluidAmountLeft - Tiers.LEATHER.getTankCapacity();
                    }

                    long fluidAmountRight = blockEntity.getRightTank().getAmount();

                    if(fluidAmountRight > Tiers.LEATHER.getTankCapacity())
                    {
                        blockEntity.getRightTank().amount = fluidAmountRight - Tiers.LEATHER.getTankCapacity();
                    }

                    if(!world.isClient)
                    {
                        ItemScatterer.spawn(world, hitResult.getBlockPos().up(), list);
                    }

                    blockEntity.resetTier();
                    player.swingHand(Hand.MAIN_HAND, true);
                    return ActionResult.SUCCESS;
                }
            }

            if(world.isClient) return ActionResult.PASS;

            if(TravelersBackpackConfig.enableBackpackBlockQuickEquip && player.world.getBlockEntity(hitResult.getBlockPos()) instanceof TravelersBackpackBlockEntity)
            {
                if(player.isSneaking())
                {
                    if(!ComponentUtils.isWearingBackpack(player))
                    {
                        BlockPos pos = hitResult.getBlockPos();
                        TravelersBackpackBlockEntity blockEntity = (TravelersBackpackBlockEntity)player.world.getBlockEntity(hitResult.getBlockPos());

                        ItemStack stack = new ItemStack(player.world.getBlockState(pos).getBlock(), 1).copy();

                        Direction bagDirection = player.world.getBlockState(pos).get(TravelersBackpackBlock.FACING);

                        if(player.world.setBlockState(pos, Blocks.AIR.getDefaultState()))
                        {
                            blockEntity.transferToItemStack(stack);
                            ComponentUtils.equipBackpack(player, stack);

                            if(blockEntity.isSleepingBagDeployed())
                            {
                                player.world.setBlockState(pos.offset(bagDirection), Blocks.AIR.getDefaultState());
                                player.world.setBlockState(pos.offset(bagDirection).offset(bagDirection), Blocks.AIR.getDefaultState());
                            }
                            return ActionResult.SUCCESS;
                        }
                    }
                }
            }
            return ActionResult.PASS;
        }));
    }
}