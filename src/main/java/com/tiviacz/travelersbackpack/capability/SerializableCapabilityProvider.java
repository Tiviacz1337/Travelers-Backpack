package com.tiviacz.travelersbackpack.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class SerializableCapabilityProvider<HANDLER> implements ICapabilityProvider, INBTSerializable<INBT>
{
    protected final Capability<HANDLER> capability;

    protected final Direction facing;

    protected final HANDLER instance;

    protected final LazyOptional<HANDLER> lazyOptional;

    public SerializableCapabilityProvider(final Capability<HANDLER> capability, @Nullable final Direction facing, @Nullable final HANDLER instance)
    {
        this.capability = capability;
        this.facing = facing;

        this.instance = instance;

        if (this.instance != null) {
            lazyOptional = LazyOptional.of(() -> this.instance);
        } else {
            lazyOptional = LazyOptional.empty();
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(final Capability<T> capability, @Nullable final Direction facing)
    {
        return getCapability().orEmpty(capability, lazyOptional);
    }

    public final Capability<HANDLER> getCapability()
    {
        return capability;
    }

    @Nullable
    public Direction getFacing()
    {
        return facing;
    }

    @Nullable
    public final HANDLER getInstance()
    {
        return instance;
    }

    @Override
    public INBT serializeNBT()
    {
        final HANDLER instance = getInstance();

        if (instance == null) {
            return null;
        }

        return getCapability().writeNBT(instance, getFacing());
    }

    @Override
    public void deserializeNBT(INBT nbt)
    {
        final HANDLER instance = getInstance();

        if (instance == null) {
            return;
        }

        getCapability().readNBT(instance, getFacing(), nbt);
    }
}