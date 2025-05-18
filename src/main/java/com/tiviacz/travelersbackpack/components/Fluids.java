package com.tiviacz.travelersbackpack.components;

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

    /*@Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Fluids o)) {
            return false;
        } else {
            return FluidVariantWrapper.matches(this.leftFluidStack, o.leftFluidStack) && FluidVariantWrapper.matches(this.rightFluidStack, o.rightFluidStack);
        }
    }

    @Override
    public int hashCode() {
        return hashCode(this.leftFluidStack, this.rightFluidStack);
    }

    public static int hashCode(FluidVariantWrapper fluidStack) {
        return (int)fluidStack.getAmount() * 31 + fluidStack.fluidVariant().hashCode();
    }

    public static int hashCode(FluidVariantWrapper leftFluidStack, FluidVariantWrapper rightFluidStack) {
        int i = 0;

        i = i * 31 + hashCode(leftFluidStack);
        i = i * 31 + hashCode(rightFluidStack);

        return i;
    }*/
}