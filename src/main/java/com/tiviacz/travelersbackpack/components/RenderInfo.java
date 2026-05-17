package com.tiviacz.travelersbackpack.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.util.RegistryHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
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
            return FluidStack.parseOptional(RegistryHelper.getRegistryAccess().get(), this.compoundTag.getCompound(LEFT_TANK));
        }
        return FluidStack.EMPTY;
    }

    public FluidStack getRightFluidStack() {
        if(this.compoundTag.contains(RIGHT_TANK)) {
            return FluidStack.parseOptional(RegistryHelper.getRegistryAccess().get(), this.compoundTag.getCompound(RIGHT_TANK));
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
            return this.compoundTag.getInt(CAPACITY);
        }
        return 0;
    }

    public static RenderInfo createCreativeTabInfo() {
        CompoundTag tag = new CompoundTag();
        tag.put(LEFT_TANK, new FluidStack(Fluids.WATER, 1).save(RegistryHelper.getRegistryAccess().get()));
        tag.put(RIGHT_TANK, new FluidStack(Fluids.LAVA, 1).save(RegistryHelper.getRegistryAccess().get()));
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