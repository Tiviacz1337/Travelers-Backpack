package com.tiviacz.travelersbackpack.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record FluidTanksOld(long capacity, Tank leftTank, Tank rightTank) {
    public static final Codec<FluidTanksOld> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidTanksOld> PACKET_CODEC;

    static {
        CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.LONG.fieldOf("capacity").forGetter(FluidTanksOld::capacity),
                    Tank.CODEC.fieldOf("leftTank").forGetter(FluidTanksOld::leftTank),
                    Tank.CODEC.fieldOf("rightTank").forGetter(FluidTanksOld::rightTank)).apply(instance, FluidTanksOld::new);
        });
        PACKET_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_LONG, FluidTanksOld::capacity, ByteBufCodecs.fromCodecWithRegistries(Tank.CODEC), FluidTanksOld::leftTank, ByteBufCodecs.fromCodecWithRegistries(Tank.CODEC), FluidTanksOld::rightTank, FluidTanksOld::new);
    }

    public static record Tank(FluidVariant fluidVariant, long amount) {
        public static final Codec<Tank> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(FluidVariant.CODEC.fieldOf("fluidVariant").forGetter(Tank::fluidVariant), Codec.LONG.fieldOf("amount").forGetter(Tank::amount)).apply(instance, Tank::new);
        });

        public Tank(FluidVariant fluidVariant, long amount) {
            this.fluidVariant = fluidVariant;
            this.amount = amount;
        }

        public FluidVariant fluidVariant() {
            return this.fluidVariant;
        }

        public long amount() {
            return this.amount;
        }
    }
}
