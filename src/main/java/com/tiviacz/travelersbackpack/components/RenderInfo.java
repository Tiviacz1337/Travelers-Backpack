package com.tiviacz.travelersbackpack.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import com.tiviacz.travelersbackpack.util.RegistryHelper;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluids;

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
        return this.compoundTag.contains("LeftTank") || this.compoundTag.contains("RightTank");
    }

    public FluidVariantWrapper getLeftFluidStack() {
        if(this.compoundTag.contains("LeftTank")) {
            if(RegistryHelper.getRegistryAccess().isPresent()) {
                return FluidVariantWrapper.parseOptional(RegistryHelper.getRegistryAccess().get(), this.compoundTag.getCompoundOrEmpty("LeftTank"));
            }
        }
        return FluidVariantWrapper.blank();
    }

    public FluidVariantWrapper getRightFluidStack() {
        if(this.compoundTag.contains("RightTank")) {
            if(RegistryHelper.getRegistryAccess().isPresent()) {
                return FluidVariantWrapper.parseOptional(RegistryHelper.getRegistryAccess().get(), this.compoundTag.getCompoundOrEmpty("RightTank"));
            }
        }
        return FluidVariantWrapper.blank();
    }

    public void updateCapacity(long capacity) {
        if(this.compoundTag.contains("Capacity")) {
            this.compoundTag.putLong("Capacity", capacity);
        }
    }

    public long getCapacity() {
        if(this.compoundTag.contains("Capacity")) {
            return this.compoundTag.getLongOr("Capacity", 0);
        }
        return 0;
    }

    public static RenderInfo createCreativeTabInfo() {
        CompoundTag tag = new CompoundTag();
        if(RegistryHelper.getRegistryAccess().isPresent()) {
            tag.put("LeftTank", new FluidVariantWrapper(FluidVariant.of(Fluids.WATER), 1).saveOptional(RegistryHelper.getRegistryAccess().get()));
            tag.put("RightTank", new FluidVariantWrapper(FluidVariant.of(Fluids.LAVA), 1).saveOptional(RegistryHelper.getRegistryAccess().get()));
        }
        tag.putLong("Capacity", 1);
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
