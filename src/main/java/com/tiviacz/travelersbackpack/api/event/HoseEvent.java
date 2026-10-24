package com.tiviacz.travelersbackpack.api.event;

import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class HoseEvent {
    private final Player player;
    private final Level level;
    protected FluidVariantWrapper fluid;
    private boolean canceled;

    protected HoseEvent(Player player, Level level, FluidVariantWrapper fluid) {
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

    public FluidVariantWrapper getFluid() {
        return fluid;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void cancel() {
        canceled = true;
    }

    public static class PickUp extends HoseEvent {
        private final BlockPos pos;
        private final BlockState state;

        @FunctionalInterface
        public interface Callback {
            void onPickUp(PickUp event);
        }

        public static final Event<Callback> EVENT = EventFactory.createArrayBacked(
                Callback.class,
                callbacks -> event -> {
                    for(Callback callback : callbacks) {
                        callback.onPickUp(event);
                    }
                }
        );

        public PickUp(Player player, Level level, BlockPos pos, BlockState state, FluidVariantWrapper fluid) {
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

        public void setFluid(FluidVariantWrapper fluid) {
            this.fluid = fluid;
        }
    }

    public static class Drink extends HoseEvent {
        private long amount;

        @FunctionalInterface
        public interface Callback {
            void onDrink(Drink event);
        }

        public static final Event<Callback> EVENT = EventFactory.createArrayBacked(
                Callback.class,
                callbacks -> event -> {
                    for(Callback callback : callbacks) {
                        callback.onDrink(event);
                    }
                }
        );

        public Drink(Player player, Level level, FluidVariantWrapper fluid, long amount) {
            super(player, level, fluid);
            this.amount = amount;
        }

        public long getAmount() {
            return amount;
        }

        public void setAmount(long amount) {
            this.amount = amount;
        }
    }

    public static class Spill extends HoseEvent {
        private final BlockPos pos;

        @FunctionalInterface
        public interface Callback {
            void onSpill(Spill event);
        }

        public static final Event<Callback> EVENT = EventFactory.createArrayBacked(
                Callback.class,
                callbacks -> event -> {
                    for(Callback callback : callbacks) {
                        callback.onSpill(event);
                    }
                }
        );

        public Spill(Player player, Level level, BlockPos pos, FluidVariantWrapper fluid) {
            super(player, level, fluid);
            this.pos = pos;
        }

        public BlockPos getPos() {
            return pos;
        }
    }
}