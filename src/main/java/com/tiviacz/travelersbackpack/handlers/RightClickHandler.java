package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class RightClickHandler
{
    public static void registerListeners()
    {
        UseBlockCallback.EVENT.register(((player, world, hand, hitResult) ->
        {
            if(TravelersBackpackConfig.enableBackpackBlockQuickEquip && player.world.getBlockEntity(hitResult.getBlockPos()) instanceof TravelersBackpackBlockEntity)
            {
                if(player.isSneaking())
                {
                    if(!ComponentUtils.isWearingBackpack(player))
                    {
                        BlockPos pos = hitResult.getBlockPos();
                        TravelersBackpackBlockEntity blockEntity = (TravelersBackpackBlockEntity)player.world.getBlockEntity(hitResult.getBlockPos());

                        ItemStack stack = new ItemStack(player.world.getBlockState(pos).getBlock(), 1).copy();

                        if(player.world.setBlockState(pos, Blocks.AIR.getDefaultState()))
                        {
                            blockEntity.transferToItemStack(stack);
                            ComponentUtils.equipBackpack(player, stack);

                            if(blockEntity.isSleepingBagDeployed())
                            {
                                Direction bagDirection = player.world.getBlockState(pos).get(TravelersBackpackBlock.FACING);
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
