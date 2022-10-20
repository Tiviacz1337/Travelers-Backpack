package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;

public class RightClickHandler
{
    public static void registerListeners()
    {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) ->
        {
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
