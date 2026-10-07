package com.tiviacz.travelersbackpack.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fluids.FluidStack;

public abstract class HoseEvent extends Event {
    private final Player player;
    private final Level level;
    protected FluidStack fluid;

    protected HoseEvent(Player player, Level level, FluidStack fluid) {
        this.player = player;
        this.level = level;
        this.fluid = fluid;
    }

    public Player getPlayer() {
        return player;
    }

    public Level getLevel() {
        return level;
    }

    public FluidStack getFluid() {
        return fluid;
    }

    @Cancelable
    public static class PickUp extends HoseEvent {
        private final BlockPos pos;
        private final BlockState state;

        public PickUp(Player player, Level level, BlockPos pos, BlockState state, FluidStack fluid) {
            super(player, level, fluid);
            this.pos = pos;
            this.state = state;
        }

        public BlockPos getPos() {
            return pos;
        }

        public BlockState getState() {
            return state;
        }

        public void setFluid(FluidStack fluid) {
            this.fluid = fluid;
        }
    }

    @Cancelable
    public static class Drink extends HoseEvent {
        private int amount;

        public Drink(Player player, Level level, FluidStack fluid, int amount) {
            super(player, level, fluid);
            this.amount = amount;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

    @Cancelable
    public static class Spill extends HoseEvent {
        private final BlockPos pos;

        public Spill(Player player, Level level, BlockPos pos, FluidStack fluid) {
            super(player, level, fluid);
            this.pos = pos;
        }

        public BlockPos getPos() {
            return pos;
        }
    }
}