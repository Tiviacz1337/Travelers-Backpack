package com.tiviacz.travelersbackpack.component.entity;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class EntityTravelersBackpackComponent implements IEntityTravelersBackpackComponent
{
    private ItemStack wearable = null;
    private final LivingEntity livingEntity;

    public EntityTravelersBackpackComponent(LivingEntity livingEntity)
    {
        this.livingEntity = livingEntity;
    }

    @Override
    public boolean hasWearable()
    {
        return this.wearable != null;
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
        this.wearable = null;
    }

    @Override
    public void sync()
    {
        ComponentUtils.ENTITY_WEARABLE.sync(livingEntity);
    }

    @Override
    public void readFromNbt(NbtCompound tag)
    {
        ItemStack wearable = ItemStack.fromNbt(tag);

        if(wearable.isEmpty())
        {
            setWearable(null);
        }
        else
        {
            setWearable(wearable);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag)
    {
        if(hasWearable())
        {
            ItemStack wearable = getWearable();
            wearable.writeNbt(tag);
        }
        if(!hasWearable())
        {
            ItemStack wearable = ItemStack.EMPTY;
            wearable.writeNbt(tag);
        }
    }
}