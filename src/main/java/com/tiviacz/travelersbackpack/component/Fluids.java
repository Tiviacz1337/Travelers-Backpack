package com.tiviacz.travelersbackpack.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record Fluids(FluidVariantWrapper leftFluidStack, FluidVariantWrapper rightFluidStack) {
    public static final Codec<Fluids> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    FluidVariantWrapper.CODEC.fieldOf("leftFluidStack").forGetter(Fluids::leftFluidStack),
                    FluidVariantWrapper.CODEC.fieldOf("rightFluidStack").forGetter(Fluids::rightFluidStack)
            ).apply(instance, Fluids::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, Fluids> STREAM_CODEC = StreamCodec.composite(
            FluidVariantWrapper.STREAM_CODEC, Fluids::leftFluidStack,
            FluidVariantWrapper.STREAM_CODEC, Fluids::rightFluidStack,
            Fluids::new
    );

    public static Fluids empty() {
        return new Fluids(FluidVariantWrapper.blank(), FluidVariantWrapper.blank());
    }
}
