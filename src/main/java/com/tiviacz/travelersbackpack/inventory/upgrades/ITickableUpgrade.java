package com.tiviacz.travelersbackpack.inventory.upgrades;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface ITickableUpgrade {
    void tick(@Nullable Player player, Level level, BlockPos pos, int currentTick);

    int getTickRate();
}
