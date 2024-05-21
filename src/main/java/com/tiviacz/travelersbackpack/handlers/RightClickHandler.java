package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.items.SleepingBagItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.items.UpgradeItem;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
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

public class RightClickHandler
{
    public static void registerListeners()
    {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) ->
        {
            if(TravelersBackpackConfig.getConfig().backpackSettings.enableBackpackRightClickUnequip)
            {
                if(ComponentUtils.isWearingBackpack(player) && !world.isClient)
                {
                    if(player.isSneaking() && hand == Hand.MAIN_HAND && player.getStackInHand(Hand.MAIN_HAND).isEmpty())
                    {
                        ItemStack backpackStack = ComponentUtils.getWearingBackpack(player);
                        ItemUsageContext context = new ItemUsageContext(world, player, Hand.MAIN_HAND, backpackStack, hitResult);

                        if(backpackStack.getItem() instanceof TravelersBackpackItem item)
                        {
                            if(item.place(new ItemPlacementContext(context)) == ActionResult.success(world.isClient))
                            {
                                player.swingHand(Hand.MAIN_HAND, true);
                                world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.05F, (1.0F + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F) * 0.7F);

                                if(TravelersBackpack.enableTrinkets())
                                {
                                    TrinketsApi.getTrinketComponent(player).ifPresent(t -> t.forEach((slotReference, itemStack) ->
                                    {
                                        if(ItemStack.canCombine(backpackStack, itemStack))
                                        {
                                            slotReference.inventory().clear();
                                        }
                                    }));
                                }

                                ComponentUtils.getComponent(player).removeWearable();

                                ComponentUtils.sync(player);

                                return ActionResult.SUCCESS;
                            }
                        }
                    }
                }
            }

            if(player.isSneaking() && hand == Hand.MAIN_HAND && player.getStackInHand(Hand.MAIN_HAND).isIn(ModTags.SLEEPING_BAGS) && player.world.getBlockEntity(hitResult.getBlockPos()) instanceof TravelersBackpackBlockEntity blockEntity)
            {
                ItemStack oldSleepingBag = blockEntity.getProperSleepingBag(blockEntity.getSleepingBagColor()).getBlock().asItem().getDefaultStack();
                blockEntity.setSleepingBagColor(ShapedBackpackRecipe.getProperColor(player.getStackInHand(Hand.MAIN_HAND).getItem()));
                if(!world.isClient)
                {
                    ItemScatterer.spawn(world, hitResult.getBlockPos().getX(), hitResult.getBlockPos().up().getY(), hitResult.getBlockPos().getZ(), oldSleepingBag);
                    player.getStackInHand(Hand.MAIN_HAND).decrement(1);
                }
                player.world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (player.world.random.nextFloat() - player.world.random.nextFloat()) * 0.2F) * 0.7F);
                return ActionResult.SUCCESS;
            }

            //Reset backpack tiers
            if(player.isSneaking() && player.getStackInHand(Hand.MAIN_HAND).getItem() == ModItems.BLANK_UPGRADE && world.getBlockEntity(hitResult.getBlockPos()) instanceof TravelersBackpackBlockEntity blockEntity)
            {
                DefaultedList<ItemStack> list = DefaultedList.of();

                for(int i = 0; i < blockEntity.getCombinedInventory().size(); i++)
                {
                    ItemStack stackInSlot = blockEntity.getCombinedInventory().getStack(i);

                    if(!stackInSlot.isEmpty())
                    {
                        list.add(stackInSlot);
                        blockEntity.getCombinedInventory().setStack(i, ItemStack.EMPTY);
                    }
                }

                list.addAll(UpgradeItem.getUpgrades(blockEntity));

                //Remove unsortable slots
                if(!blockEntity.getSlotManager().getUnsortableSlots().isEmpty())
                {
                    blockEntity.getSlotManager().getUnsortableSlots().clear();
                }

                //Remove memory slots
                if(!blockEntity.getSlotManager().getMemorySlots().isEmpty())
                {
                    blockEntity.getSlotManager().getMemorySlots().clear();
                }

                //Drain excessive fluid
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

                //Change size of Tool slots and Storage slots
                blockEntity.getInventory().setSize(Tiers.LEATHER.getStorageSlots());
                blockEntity.getToolSlotsInventory().setSize(Tiers.LEATHER.getToolSlots());

                //Reset tier
                blockEntity.resetTier();

                //Reset Tanks
                blockEntity.getLeftTank().setCapacity(Tiers.LEATHER.getTankCapacity());
                blockEntity.getRightTank().setCapacity(Tiers.LEATHER.getTankCapacity());

                //Reset Settings
                blockEntity.getSettingsManager().loadDefaults();

                player.swingHand(Hand.MAIN_HAND, true);
                return ActionResult.SUCCESS;
            }

            if(world.isClient) return ActionResult.PASS;

            if(TravelersBackpackConfig.getConfig().backpackSettings.enableBackpackBlockQuickEquip && player.world.getBlockEntity(hitResult.getBlockPos()) instanceof TravelersBackpackBlockEntity blockEntity)
            {
                if(player.isSneaking())
                {
                    if(!ComponentUtils.isWearingBackpack(player))
                    {
                        ItemStack stack = new ItemStack(player.world.getBlockState(hitResult.getBlockPos()).getBlock(), 1).copy();
                        Direction bagDirection = player.world.getBlockState(hitResult.getBlockPos()).get(TravelersBackpackBlock.FACING);

                        if(player.world.setBlockState(hitResult.getBlockPos(), Blocks.AIR.getDefaultState()))
                        {
                            blockEntity.transferToItemStack(stack);

                            if(TravelersBackpack.enableTrinkets())
                            {
                                TrinketItem.equipItem(player, stack);
                            }
                            else
                            {
                                ComponentUtils.equipBackpack(player, stack);
                            }

                            if(blockEntity.isSleepingBagDeployed())
                            {
                                player.world.setBlockState(hitResult.getBlockPos().offset(bagDirection), Blocks.AIR.getDefaultState());
                                player.world.setBlockState(hitResult.getBlockPos().offset(bagDirection).offset(bagDirection), Blocks.AIR.getDefaultState());
                            }
                            return ActionResult.SUCCESS;
                        }
                    }
                }
            }
            return ActionResult.PASS;
        });
    }
}