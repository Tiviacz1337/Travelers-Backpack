package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TravelersBackpackCapability
{
    @CapabilityInject(ITravelersBackpack.class)
    public static final Capability<ITravelersBackpack> TRAVELERS_BACKPACK_CAPABILITY = null;

    public static final Direction DEFAULT_FACING = null;

    public static final ResourceLocation ID = new ResourceLocation(TravelersBackpack.MODID, "travelers_backpack");

    public static void register()
    {
        CapabilityManager.INSTANCE.register(ITravelersBackpack.class);
    }

    public static ICapabilityProvider createProvider(final ITravelersBackpack backpack)
    {
        return new Provider(backpack);
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag>
    {
        final ITravelersBackpack backpack;
        final LazyOptional<ITravelersBackpack> optional;

        public Provider(final ITravelersBackpack backpack)
        {
            this.backpack = backpack;
            this.optional = LazyOptional.of(() -> backpack);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            return TravelersBackpackCapability.TRAVELERS_BACKPACK_CAPABILITY.orEmpty(cap, this.optional);
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
