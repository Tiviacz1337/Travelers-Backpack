package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.common.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.items.SleepingBagItem;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.Direction;

public class RightClickHandler
{
    public static void registerListeners()
    {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) ->
        {
            if(player.isSneaking() && hand == Hand.MAIN_HAND && player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof SleepingBagItem item && player.world.getBlockEntity(hitResult.getBlockPos()) instanceof TravelersBackpackBlockEntity blockEntity)
            {
                ItemStack oldSleepingBag = blockEntity.getProperSleepingBag(blockEntity.getSleepingBagColor()).getBlock().asItem().getDefaultStack();
                blockEntity.setSleepingBagColor(ShapedBackpackRecipe.getProperColor(item));
                if(!world.isClient) ItemScatterer.spawn(world, hitResult.getBlockPos().getX(), hitResult.getBlockPos().up().getY(), hitResult.getBlockPos().getZ(), oldSleepingBag);
                player.world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (player.world.random.nextFloat() - player.world.random.nextFloat()) * 0.2F) * 0.7F);
                return ActionResult.SUCCESS;
            }

            if(world.isClient) return ActionResult.PASS;

            if(TravelersBackpackConfig.enableBackpackBlockQuickEquip && player.world.getBlockEntity(hitResult.getBlockPos()) instanceof TravelersBackpackBlockEntity blockEntity)
            {
                if(player.isSneaking())
                {
                    if(!ComponentUtils.isWearingBackpack(player))
                    {
                        if(!TravelersBackpack.enableTrinkets())
                        {
                            ItemStack stack = new ItemStack(player.world.getBlockState(hitResult.getBlockPos()).getBlock(), 1).copy();

                            Direction bagDirection = player.world.getBlockState(hitResult.getBlockPos()).get(TravelersBackpackBlock.FACING);

                            if(player.world.setBlockState(hitResult.getBlockPos(), Blocks.AIR.getDefaultState()))
                            {
                                blockEntity.transferToItemStack(stack);
                                ComponentUtils.equipBackpack(player, stack);

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
            }
            return ActionResult.PASS;
        });
    }
}