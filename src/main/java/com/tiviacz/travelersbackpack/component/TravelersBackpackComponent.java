package com.tiviacz.travelersbackpack.component;

import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

public class TravelersBackpackComponent implements ITravelersBackpackComponent
{
    private String WEARABLE = "Wearable";
    private ItemStack wearable = null;
    private final PlayerEntity player;
    private final TravelersBackpackInventory inventory;

    public TravelersBackpackComponent(PlayerEntity player)
    {
        this.player = player;
        this.inventory = new TravelersBackpackInventory(this.wearable, player, Reference.WEARABLE_SCREEN_ID);
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
        this.inventory.setStack(null);
    }

    @Override
    public TravelersBackpackInventory getInventory()
    {
        return this.inventory;
    }

    @Override
    public void setContents(ItemStack stack)
    {
        if(stack == null)
        {
            this.inventory.setStack(null);
        }
        else
        {
            this.inventory.setStack(stack);

            if(!stack.isEmpty())
            {
                this.inventory.readAllData();
            }
        }
    }

    @Override
    public void sync()
    {
        ComponentUtils.WEARABLE.sync(this.player);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup)
    {
        ItemStack wearable = ItemStack.fromNbtOrEmpty(registryLookup, tag.getCompound(WEARABLE));

        if(wearable.isEmpty())
        {
            setWearable(null);
            setContents(null);
        }
        else
        {
            setWearable(wearable);
            setContents(wearable);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup)
    {
        NbtCompound compound = new NbtCompound();

        if(hasWearable())
        {
            ItemStack wearable = getWearable();
            compound = (NbtCompound)wearable.encodeAllowEmpty(registryLookup);
        }

        tag.put(WEARABLE, compound);
    }
}