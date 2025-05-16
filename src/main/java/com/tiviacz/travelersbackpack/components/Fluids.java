package com.tiviacz.travelersbackpack.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

@Deprecated(forRemoval = true, since = "1.22")
public record Fluids(FluidStack leftFluidStack, FluidStack rightFluidStack) {
    public static final Codec<Fluids> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    FluidStack.OPTIONAL_CODEC.fieldOf("leftFluidStack").forGetter(Fluids::leftFluidStack),
                    FluidStack.OPTIONAL_CODEC.fieldOf("rightFluidStack").forGetter(Fluids::rightFluidStack)
            ).apply(instance, Fluids::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, Fluids> STREAM_CODEC = StreamCodec.composite(
            FluidStack.OPTIONAL_STREAM_CODEC, Fluids::leftFluidStack,
            FluidStack.OPTIONAL_STREAM_CODEC, Fluids::rightFluidStack,
            Fluids::new
    );

    public static Fluids empty() {
        return new Fluids(FluidStack.EMPTY, FluidStack.EMPTY);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Fluids o)) {
            return false;
        } else {
            return FluidStack.matches(this.leftFluidStack, o.leftFluidStack) && FluidStack.matches(this.rightFluidStack, o.rightFluidStack);
        }
    }

    @Override
    public int hashCode() {
        return hashCode(this.leftFluidStack, this.rightFluidStack);
    }

    public static int hashCode(FluidStack fluidStack) {
        return fluidStack.getAmount() * 31 + FluidStack.hashFluidAndComponents(fluidStack);
    }

    public static int hashCode(FluidStack leftFluidStack, FluidStack rightFluidStack) {
        int i = 0;

        i = i * 31 + hashCode(leftFluidStack);
        i = i * 31 + hashCode(rightFluidStack);

        return i;
    }
}
