package com.tiviacz.travelersbackpack.capability.entity;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.SerializableCapabilityProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class TravelersBackpackEntityCapability
{
    @CapabilityInject(IEntityTravelersBackpack.class)
    public static final Capability<IEntityTravelersBackpack> TRAVELERS_BACKPACK_ENTITY_CAPABILITY = null;

    public static final Direction DEFAULT_FACING = null;

    public static final ResourceLocation ID = new ResourceLocation(TravelersBackpack.MODID, "travelers_backpack_entity");

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IEntityTravelersBackpack.class, new Capability.IStorage<IEntityTravelersBackpack>()
        {
            @Override
            public INBT writeNBT(final Capability<IEntityTravelersBackpack> capability, final IEntityTravelersBackpack instance, final Direction side)
            {
                CompoundNBT compound = new CompoundNBT();

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
            public void readNBT(final Capability<IEntityTravelersBackpack> capability, final IEntityTravelersBackpack instance, final Direction side, final INBT nbt)
            {
                CompoundNBT stackCompound = (CompoundNBT)nbt;
                ItemStack wearable = ItemStack.of(stackCompound);

                instance.setWearable(wearable);
            }
        }, () -> new TravelersBackpackEntityWearable(null));
    }

    public static ICapabilityProvider createProvider(final IEntityTravelersBackpack backpack)
    {
        return new SerializableCapabilityProvider<>(TRAVELERS_BACKPACK_ENTITY_CAPABILITY, DEFAULT_FACING, backpack);
    }
}