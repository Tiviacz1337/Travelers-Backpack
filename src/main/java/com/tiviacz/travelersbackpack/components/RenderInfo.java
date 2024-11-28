package com.tiviacz.travelersbackpack.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

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

    public boolean isEmpty() {
        return this.compoundTag.isEmpty();
    }

    public boolean hasTanks() {
        if(this.compoundTag.contains("LeftTank") || this.compoundTag.contains("RightTank")) {
            return true;
        }
        return false;
    }

    public FluidStack getLeftFluidStack() {
        if(this.compoundTag.contains("LeftTank")) {
            return FluidStack.loadFluidStackFromNBT(this.compoundTag.getCompound("LeftTank"));
        }
        return FluidStack.EMPTY;
    }

    public FluidStack getRightFluidStack() {
        if(this.compoundTag.contains("RightTank")) {
            return FluidStack.loadFluidStackFromNBT(this.compoundTag.getCompound("RightTank"));
        }
        return FluidStack.EMPTY;
    }

    public void updateCapacity(int capacity) {
        if(this.compoundTag.contains("Capacity")) {
            this.compoundTag.putInt("Capacity", capacity);
        }
    }

    public int getCapacity() {
        if(this.compoundTag.contains("Capacity")) {
            return this.compoundTag.getInt("Capacity");
        }
        return 0;
    }

    public static RenderInfo createCreativeTabInfo() {
        CompoundTag tag = new CompoundTag();
        tag.put("LeftTank", new FluidStack(Fluids.WATER, 1).writeToNBT(new CompoundTag()));
        tag.put("RightTank", new FluidStack(Fluids.LAVA, 1).writeToNBT(new CompoundTag()));
        tag.putInt("Capacity", 1);
        return new RenderInfo(tag);
    }

    @Override
    public boolean equals(Object pOther) {
        if(this == pOther) {
            return true;
        } else {
            if(pOther instanceof RenderInfo renderInfo && this.compoundTag.toString().equals(renderInfo.compoundTag.toString())) {
                return true;
            }
            return false;
        }
    }
}
