package com.tiviacz.travelersbackpack.capability.entity;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TravelersBackpackEntityCapability
{
    public static final Capability<IEntityTravelersBackpack> TRAVELERS_BACKPACK_ENTITY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static final Direction DEFAULT_FACING = null;

    public static final ResourceLocation ID = new ResourceLocation(TravelersBackpack.MODID, "travelers_backpack_entity");

    public static ICapabilityProvider createProvider(final IEntityTravelersBackpack backpack)
    {
        return new Provider(backpack);
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag>
    {
        final IEntityTravelersBackpack backpack;
        final LazyOptional<IEntityTravelersBackpack> optional;

        public Provider(final IEntityTravelersBackpack backpack)
        {
            this.backpack = backpack;
            this.optional = LazyOptional.of(() -> backpack);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            return TravelersBackpackEntityCapability.TRAVELERS_BACKPACK_ENTITY_CAPABILITY.orEmpty(cap, this.optional);
        }

        @Override
        public CompoundTag serializeNBT()
        {
            return backpack.saveTag();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            backpack.loadTag(nbt);
        }
    }
}