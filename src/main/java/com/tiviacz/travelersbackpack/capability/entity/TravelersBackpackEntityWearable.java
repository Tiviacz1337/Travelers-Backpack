package com.tiviacz.travelersbackpack.capability.entity;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.network.CSyncCapabilityPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.PacketDistributor;

public class TravelersBackpackEntityWearable implements IEntityTravelersBackpack
{
    private ItemStack wearable = new ItemStack(Items.AIR, 0);
    private final LivingEntity livingEntity;

    public TravelersBackpackEntityWearable(final LivingEntity livingEntity)
    {
        this.livingEntity = livingEntity;
    }

    @Override
    public boolean hasWearable()
    {
        return !this.wearable.isEmpty();
    }

    @Override
    public ItemStack getWearable()
    {
        return this.wearable;
    }

    @Override
    public void setWearable(ItemStack stack)
    {
        this.wearable = stack;
    }

    @Override
    public void removeWearable()
    {
        this.wearable = new ItemStack(Items.AIR, 0);
    }

    @Override
    public void synchronise()
    {
        if(livingEntity != null && !livingEntity.level.isClientSide)
        {
            CapabilityUtils.getEntityCapability(livingEntity).ifPresent(cap -> TravelersBackpack.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> livingEntity), new CSyncCapabilityPacket(this.wearable.save(new CompoundNBT()), livingEntity.getId(), false)));
        }
    }

    @Override
    public CompoundNBT saveTag()
    {
        CompoundNBT compound = new CompoundNBT();

        if(hasWearable())
        {
            ItemStack wearable = getWearable();
            wearable.save(compound);
        }
        if(!hasWearable())
        {
            ItemStack wearable = new ItemStack(Items.AIR, 0);
            wearable.save(compound);
        }
        return compound;
    }

    @Override
    public void loadTag(CompoundNBT compoundTag)
    {
        ItemStack wearable = ItemStack.of(compoundTag);
        setWearable(wearable);
    }
}
