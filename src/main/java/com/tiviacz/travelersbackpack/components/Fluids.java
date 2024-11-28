package com.tiviacz.travelersbackpack.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraftforge.fluids.FluidStack;

public record Fluids(FluidStack leftFluidStack, FluidStack rightFluidStack) {
    public static final Codec<Fluids> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    FluidStack.CODEC.fieldOf("leftFluidStack").forGetter(Fluids::leftFluidStack),
                    FluidStack.CODEC.fieldOf("rightFluidStack").forGetter(Fluids::rightFluidStack)
            ).apply(instance, Fluids::new)
    );

    public static final StreamCodec<ByteBuf, Fluids> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(FluidStack.CODEC), Fluids::leftFluidStack,
            ByteBufCodecs.fromCodec(FluidStack.CODEC), Fluids::rightFluidStack,
            Fluids::new
    );

    public static Fluids empty() {
        return new Fluids(FluidStack.EMPTY, FluidStack.EMPTY);
    }
}
