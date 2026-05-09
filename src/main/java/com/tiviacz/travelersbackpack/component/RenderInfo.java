package com.tiviacz.travelersbackpack.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public record RenderInfo(CompoundTag compoundTag) {
    public static final RenderInfo EMPTY = new RenderInfo(new CompoundTag());
    public static final Codec<RenderInfo> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    CompoundTag.CODEC.fieldOf("RenderInfo").forGetter(RenderInfo::compoundTag)
            ).apply(instance, RenderInfo::new)
    );
    public static final StreamCodec<ByteBuf, RenderInfo> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(CompoundTag.CODEC), RenderInfo::compoundTag, RenderInfo::new
    );

    public static final String LEFT_TANK = "LeftTank";
    public static final String RIGHT_TANK = "RightTank";
    public static final String CAPACITY = "Capacity";
    public static final String LANTERN = "Lantern";

    public boolean isEmpty() {
        return this.compoundTag.isEmpty();
    }

    public boolean hasTanks() {
        return this.compoundTag.contains(LEFT_TANK) || this.compoundTag.contains(RIGHT_TANK);
    }

    public boolean hasLantern() {
        return this.compoundTag.contains(LANTERN);
    }

    public FluidStack getLeftFluidStack() {
        if(this.compoundTag.contains(LEFT_TANK)) {
            return FluidStack.CODEC.parse(NbtOps.INSTANCE, this.compoundTag.getCompoundOrEmpty(LEFT_TANK)).result().orElse(FluidStack.EMPTY);
        }
        return FluidStack.EMPTY;
    }

    public FluidStack getRightFluidStack() {
        if(this.compoundTag.contains(RIGHT_TANK)) {
            return FluidStack.CODEC.parse(NbtOps.INSTANCE, this.compoundTag.getCompoundOrEmpty(RIGHT_TANK)).result().orElse(FluidStack.EMPTY);
        }
        return FluidStack.EMPTY;
    }

    public void updateCapacity(int capacity) {
        if(this.compoundTag.contains(CAPACITY)) {
            this.compoundTag.putInt(CAPACITY, capacity);
        }
    }

    public int getCapacity() {
        if(this.compoundTag.contains(CAPACITY)) {
            return this.compoundTag.getIntOr(CAPACITY, 0);
        }
        return 0;
    }

    public static RenderInfo createCreativeTabInfo() {
        CompoundTag tag = new CompoundTag();
        tag.put(LEFT_TANK, FluidStack.CODEC.encodeStart(NbtOps.INSTANCE, new FluidStack(Fluids.WATER, 1)).result().orElseGet(CompoundTag::new));
        tag.put(RIGHT_TANK, FluidStack.CODEC.encodeStart(NbtOps.INSTANCE, new FluidStack(Fluids.LAVA, 1)).result().orElseGet(CompoundTag::new));
        tag.putInt(CAPACITY, 1);
        return new RenderInfo(tag);
    }

    @Override
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        } else {
            return other instanceof RenderInfo(CompoundTag tag) && this.compoundTag.equals(tag);
        }
    }

    @Override
    public int hashCode() {
        return this.compoundTag.hashCode();
    }
}