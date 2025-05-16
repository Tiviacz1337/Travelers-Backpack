package com.tiviacz.travelersbackpack.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

@Deprecated(forRemoval = true, since = "1.22")
public record FluidTanksOld(int capacity, FluidStack leftFluidStack, FluidStack rightFluidStack) {
    public static final Codec<FluidTanksOld> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("capacity").forGetter(FluidTanksOld::capacity),
                    FluidStack.OPTIONAL_CODEC.fieldOf("leftFluidStack").forGetter(FluidTanksOld::leftFluidStack),
                    FluidStack.OPTIONAL_CODEC.fieldOf("rightFluidStack").forGetter(FluidTanksOld::rightFluidStack)
            ).apply(instance, FluidTanksOld::new)
    );

    public static final StreamCodec<ByteBuf, FluidTanksOld> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, FluidTanksOld::capacity,
            ByteBufCodecs.fromCodec(FluidStack.OPTIONAL_CODEC), FluidTanksOld::leftFluidStack,
            ByteBufCodecs.fromCodec(FluidStack.OPTIONAL_CODEC), FluidTanksOld::rightFluidStack,
            FluidTanksOld::new
    );

    public static FluidTanksOld createTanks(int capacity) {
        return new FluidTanksOld(capacity, FluidStack.EMPTY, FluidStack.EMPTY);
    }

    public static FluidTanksOld createTanksForCreativeTab() {
        return null;
        //return new FluidTanks(Tiers.LEATHER.getTankCapacity(), new FluidStack(Fluids.WATER, Tiers.LEATHER.getTankCapacity()), new FluidStack(Fluids.LAVA, Tiers.LEATHER.getTankCapacity()));
    }
}