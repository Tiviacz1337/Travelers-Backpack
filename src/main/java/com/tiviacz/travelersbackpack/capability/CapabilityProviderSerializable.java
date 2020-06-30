package com.tiviacz.travelersbackpack.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class CapabilityProviderSerializable<HANDLER> implements ICapabilityProvider, INBTSerializable<NBTBase>
{
    protected final Capability<HANDLER> capability;
    protected final EnumFacing facing;
    protected final HANDLER instance;

    public CapabilityProviderSerializable(final Capability<HANDLER> capability, @Nullable final EnumFacing facing, @Nullable final HANDLER instance)
    {
        this.capability = capability;
        this.facing = facing;
        this.instance = instance;
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, @Nullable final EnumFacing facing)
    {
        return capability == getCapability();
    }

    @Override
    @Nullable
    public <T> T getCapability(final Capability<T> capability, @Nullable final EnumFacing facing)
    {
        if(capability == getCapability())
        {
            return getCapability().cast(getInstance());
        }
        return null;
    }

    public final Capability<HANDLER> getCapability()
    {
        return capability;
    }

    @Nullable
    public EnumFacing getFacing()
    {
        return facing;
    }

    @Nullable
    public final HANDLER getInstance()
    {
        return instance;
    }

    @Override
    public NBTBase serializeNBT()
    {
        return getCapability().writeNBT(getInstance(), getFacing());
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        getCapability().readNBT(getInstance(), getFacing(), nbt);
    }
}