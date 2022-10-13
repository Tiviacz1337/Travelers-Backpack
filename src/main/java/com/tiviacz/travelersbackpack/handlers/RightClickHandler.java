package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public class RightClickHandler
{
    public static void registerListeners()
    {
        InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, direction) ->
        {
            if(TravelersBackpackConfig.enableBackpackBlockQuickEquip && player.world.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity)
            {
                if(player.isSneaking())
                {
                    if(!ComponentUtils.isWearingBackpack(player))
                    {
                        if(!TravelersBackpack.enableTrinkets())
                        {
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
                                return EventResult.interruptTrue();
                            }
                        }
                    }
                }
            }
            return EventResult.pass();
        });
    }
}