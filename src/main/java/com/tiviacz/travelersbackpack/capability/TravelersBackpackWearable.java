package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.network.SyncBackpackCapabilityClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.PacketDistributor;

public class TravelersBackpackWearable implements ITravelersBackpack
{
    private ItemStack wearable = ItemStack.EMPTY;
    private final PlayerEntity playerEntity;

    public TravelersBackpackWearable(final PlayerEntity playerEntity)
    {
        this.playerEntity = playerEntity;
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
    }

    @Override
    public void synchronise()
    {
        if(playerEntity != null && !playerEntity.getEntityWorld().isRemote)
        {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)playerEntity;
            CapabilityUtils.getCapability(serverPlayerEntity).ifPresent(cap -> TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> serverPlayerEntity), new SyncBackpackCapabilityClient(this.wearable.write(new CompoundNBT()), serverPlayerEntity.getEntityId())));
        }
    }

    @Override
    public void synchroniseToOthers(PlayerEntity player)
    {
        if(player != null && !player.getEntityWorld().isRemote)
        {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
            CapabilityUtils.getCapability(serverPlayerEntity).ifPresent(cap -> TravelersBackpack.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> serverPlayerEntity), new SyncBackpackCapabilityClient(this.wearable.write(new CompoundNBT()), serverPlayerEntity.getEntityId())));
        }
    }
}
