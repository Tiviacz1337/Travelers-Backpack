package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.network.SyncBackpackCapabilityClient;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.ItemStackHandler;

public class TravelersBackpackWearable implements ITravelersBackpack
{
    private ItemStack wearable = ItemStack.EMPTY;
    private final PlayerEntity playerEntity;
    private final TravelersBackpackInventory inventory;

    public TravelersBackpackWearable(final PlayerEntity playerEntity)
    {
        this.playerEntity = playerEntity;
        this.inventory = new TravelersBackpackInventory(this.wearable, playerEntity, Reference.WEARABLE_SCREEN_ID);
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
        this.wearable = ItemStack.EMPTY;
        this.inventory.setStack(ItemStack.EMPTY);
    }

    @Override
    public TravelersBackpackInventory getInventory()
    {
        return this.inventory;
    }

    @Override
    public void setContents(ItemStack stack)
    {
        this.inventory.setStack(stack);

        if(!stack.isEmpty())
        {
            this.inventory.loadAllData(stack.getOrCreateTag());
        }
    }

    @Override
    public void synchronise()
    {
        if(playerEntity != null && !playerEntity.level.isClientSide)
        {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)playerEntity;
            CapabilityUtils.getCapability(serverPlayerEntity).ifPresent(cap -> TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> serverPlayerEntity), new SyncBackpackCapabilityClient(this.wearable.save(new CompoundNBT()), serverPlayerEntity.getId())));
        }
    }

    @Override
    public void synchroniseToOthers(PlayerEntity player)
    {
        if(player != null && !player.level.isClientSide)
        {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
            CapabilityUtils.getCapability(serverPlayerEntity).ifPresent(cap -> TravelersBackpack.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> serverPlayerEntity), new CSyncCapabilityPacket(this.wearable.save(new CompoundNBT()), serverPlayerEntity.getId())));
        }
    }

    public static CompoundNBT synchroniseMinimumData(ItemStack stack)
    {
        CompoundNBT compound = new CompoundNBT();
        stack.save(compound);

        if(compound.contains("tag"))
        {
            if(compound.getCompound("tag").contains("Inventory"))
            {
                CompoundNBT inventoryCopy = compound.getCompound("tag").getCompound("Inventory").copy();
                compound.getCompound("tag").remove("Inventory");
                compound.getCompound("tag").put("Inventory", slimInventory(inventoryCopy));
            }

            if(compound.getCompound("tag").contains("CraftingInventory"))
            {
                compound.getCompound("tag").remove("CraftingInventory");
            }
        }
        return compound;
    }

    public static CompoundNBT slimInventory(CompoundNBT inventory)
    {
        ItemStackHandler tempHandler = new ItemStackHandler(Reference.INVENTORY_SIZE);
        tempHandler.deserializeNBT(inventory);

        for(int i = 0; i < tempHandler.getSlots(); i++)
        {
            if(i != 39 && i != 40)
            {
                if(!tempHandler.getStackInSlot(i).isEmpty())
                {
                    tempHandler.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        }
        return tempHandler.serializeNBT();
    }
}