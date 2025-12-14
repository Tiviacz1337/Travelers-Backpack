package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SleepHandler {
    public static void registerListener() {
        //Handled in SleepingBagBlock to create forced spawn point
        EntitySleepEvents.ALLOW_SETTING_SPAWN.register((player, sleepingPos) -> !(!player.level().isClientSide() && player.level().getBlockState(sleepingPos).getBlock() instanceof SleepingBagBlock));

        EntitySleepEvents.STOP_SLEEPING.register((player, sleepingPos) -> {
            if(!TravelersBackpackConfig.getConfig().backpackSettings.quickSleepingBag) {
                return;
            }
            Level level = player.level();
            if(level.getBlockState(sleepingPos).getBlock() instanceof SleepingBagBlock) {
                BlockState headPart = level.getBlockState(sleepingPos);
                if(headPart.hasProperty(SleepingBagBlock.CAN_DROP) && headPart.getValue(SleepingBagBlock.CAN_DROP)) {
                    return;
                }
                BlockPos backpackPos = sleepingPos.relative(headPart.getValue(SleepingBagBlock.FACING).getOpposite(), 2);
                if(!(level.getBlockState(backpackPos).getBlock() instanceof TravelersBackpackBlock)) {
                    if(!level.isClientSide()) {
                        level.setBlockAndUpdate(sleepingPos, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        });
    }
}