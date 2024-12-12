package com.tiviacz.travelersbackpack.inventory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record FluidVariantWrapper(FluidVariant fluidVariant, long amount) {
    public static final Codec<FluidVariantWrapper> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    FluidVariant.CODEC.fieldOf("fluidVariant").forGetter(FluidVariantWrapper::fluidVariant),
                    Codec.LONG.fieldOf("amount").forGetter(FluidVariantWrapper::amount)
            ).apply(instance, FluidVariantWrapper::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidVariantWrapper> STREAM_CODEC = StreamCodec.composite(
            FluidVariant.PACKET_CODEC, FluidVariantWrapper::fluidVariant,
            ByteBufCodecs.VAR_LONG, FluidVariantWrapper::amount,
            FluidVariantWrapper::new
    );

    public static Optional<FluidVariantWrapper> parse(HolderLookup.Provider provider, Tag tag) {
        return CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag).result(); //.resultOrPartial(p_330102_ -> TravelersBackpack.LOGGER.error("Tried to load invalid FluidVariantWrapper: '{}'", p_330102_));
    }

    public static FluidVariantWrapper parseOptional(HolderLookup.Provider provider, Tag tag) {
        return parse(provider, tag).isPresent() ? parse(provider, tag).get() : blank();
    }

    public Optional<Tag> save(HolderLookup.Provider provider) {
        return CODEC.encode(this, provider.createSerializationContext(NbtOps.INSTANCE), new CompoundTag()).resultOrPartial(p_330104_ -> TravelersBackpack.LOGGER.error("Tried to save invalid FluidVariantWrapper: '{}'", p_330104_));
    }

    public Tag saveOptional(HolderLookup.Provider provider) {
        return save(provider).isPresent() ? save(provider).get() : new CompoundTag();
    }

    public boolean isEmpty() {
        return fluidVariant.isBlank() || amount <= 0;
    }

    public long getAmount() {
        return amount;
    }

    public static FluidVariantWrapper blank() {
        return new FluidVariantWrapper(FluidVariant.blank(), 0);
    }

    public FluidVariantWrapper copyWithAmount(long amount) {
        if(this.isEmpty()) {
            return blank();
        } else {
            if(amount <= 0) {
                return blank();
            }
            FluidVariantWrapper fluidVariant = this.copy();
            fluidVariant.setAmount(amount);
            return fluidVariant;
        }
    }

    public FluidVariantWrapper copy() {
        if(this.isEmpty()) {
            return blank();
        } else {
            return new FluidVariantWrapper(this.fluidVariant, this.amount);
        }
    }

    public FluidVariantWrapper setAmount(long amount) {
        return new FluidVariantWrapper(this.fluidVariant, amount);
    }

    public FluidVariantWrapper grow(long addedAmount) {
        return this.setAmount(this.getAmount() + addedAmount);
    }

    public FluidVariantWrapper shrink(long removedAmount) {
        return this.grow(-removedAmount);
    }
}
