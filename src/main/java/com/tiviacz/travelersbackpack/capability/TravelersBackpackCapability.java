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
    public static final Capability<ITravelersBackpack> TRAVELERS_BACKPACK_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static final Direction DEFAULT_FACING = null;

    public static final ResourceLocation ID = new ResourceLocation(TravelersBackpack.MODID, "travelers_backpack");

    //@SubscribeEvent
   // public void registerCaps(RegisterCapabilitiesEvent event)
  //  {
  //      event.register(ITravelersBackpack.class);
   // }

    /*, new Capability.IStorage<ITravelersBackpack>()
        {
            @Override
            public Tag writeNBT(final Capability<ITravelersBackpack> capability, final ITravelersBackpack instance, final Direction side)
            {
                CompoundTag compound = new CompoundTag();

                if(instance.hasWearable())
                {
                    ItemStack wearable = instance.getWearable();
                    wearable.save(compound);
                }
                if(!instance.hasWearable())
                {
                    ItemStack wearable = ItemStack.EMPTY;
                    wearable.save(compound);
                }
                return compound;
            }

            @Override
            public void readNBT(final Capability<ITravelersBackpack> capability, final ITravelersBackpack instance, final Direction side, final Tag nbt)
            {
                CompoundTag stackCompound = (CompoundTag)nbt;
                ItemStack wearable = ItemStack.of(stackCompound);
                instance.setWearable(wearable);
            }
        }, () -> new TravelersBackpackWearable(null));
    } */

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
