package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class TravelersBackpackCapability
{
    @CapabilityInject(ITravelersBackpack.class)
    public static final Capability<ITravelersBackpack> TRAVELERS_BACKPACK_CAPABILITY = null;

    public static final Direction DEFAULT_FACING = null;

    public static final ResourceLocation ID = new ResourceLocation(TravelersBackpack.MODID, "travelers_backpack");

    public static void register()
    {
        CapabilityManager.INSTANCE.register(ITravelersBackpack.class, new Capability.IStorage<ITravelersBackpack>()
        {
            @Override
            public INBT writeNBT(final Capability<ITravelersBackpack> capability, final ITravelersBackpack instance, final Direction side)
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
            public void readNBT(final Capability<ITravelersBackpack> capability, final ITravelersBackpack instance, final Direction side, final INBT nbt)
            {
                CompoundNBT stackCompound = (CompoundNBT)nbt;
                ItemStack wearable = ItemStack.of(stackCompound);

                instance.setWearable(wearable);
                instance.setContents(wearable);
            }
        }, () -> new TravelersBackpackWearable(null));
    }

    public static ICapabilityProvider createProvider(final ITravelersBackpack backpack)
    {
        return new SerializableCapabilityProvider<>(TRAVELERS_BACKPACK_CAPABILITY, DEFAULT_FACING, backpack);
    }
}