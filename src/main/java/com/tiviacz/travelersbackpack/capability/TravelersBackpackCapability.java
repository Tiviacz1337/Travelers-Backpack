package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class TravelersBackpackCapability
{
    @CapabilityInject(ITravelersBackpack.class)
    public static final Capability<ITravelersBackpack> TRAVELERS_BACKPACK_CAPABILITY = null;

    public static final EnumFacing DEFAULT_FACING = null;

    public static final ResourceLocation ID = new ResourceLocation(TravelersBackpack.MODID, "travelers_backpack");

    public static void register()
    {
        CapabilityManager.INSTANCE.register(ITravelersBackpack.class, new Capability.IStorage<ITravelersBackpack>()
        {
            @Override
            public NBTBase writeNBT(final Capability<ITravelersBackpack> capability, final ITravelersBackpack instance, final EnumFacing side)
            {
                NBTTagCompound tag = new NBTTagCompound();

                if(instance.hasWearable())
                {
                    ItemStack wearable = instance.getWearable();
                    wearable.writeToNBT(tag);
                }
                if(!instance.hasWearable())
                {
                    ItemStack wearable = ItemStack.EMPTY;
                    wearable.writeToNBT(tag);
                }

                return tag;
            }

            @Override
            public void readNBT(final Capability<ITravelersBackpack> capability, final ITravelersBackpack instance, final EnumFacing side, final NBTBase nbt)
            {
                NBTTagCompound tag = (NBTTagCompound) nbt;
                ItemStack wearable = new ItemStack(tag);
                instance.setWearable(wearable);
            }
        }, () -> new TravelersBackpackWearable(null));
    }

    public static ICapabilityProvider createProvider(final ITravelersBackpack instance)
    {
        return new CapabilityProviderSerializable<>(TRAVELERS_BACKPACK_CAPABILITY, DEFAULT_FACING, instance);
    }
}