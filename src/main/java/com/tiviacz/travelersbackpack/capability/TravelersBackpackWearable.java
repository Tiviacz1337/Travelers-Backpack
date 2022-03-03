package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.network.SyncBackpackCapabilityClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

public class TravelersBackpackWearable implements ITravelersBackpack
{
    private ItemStack wearable = ItemStack.EMPTY;
    private final Player playerEntity;

    public TravelersBackpackWearable(final Player playerEntity)
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
        if(playerEntity != null && !playerEntity.level.isClientSide)
        {
            ServerPlayer serverPlayer = (ServerPlayer)playerEntity;
            CapabilityUtils.getCapability(serverPlayer).ifPresent(cap -> TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncBackpackCapabilityClient(this.wearable.save(new CompoundTag()), serverPlayer.getId())));
        }
    }

    @Override
    public void synchroniseToOthers(Player player)
    {
        if(player != null && !player.level.isClientSide)
        {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            CapabilityUtils.getCapability(serverPlayer).ifPresent(cap -> TravelersBackpack.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> serverPlayer), new SyncBackpackCapabilityClient(this.wearable.save(new CompoundTag()), serverPlayer.getId())));
        }
    }

    @Override
    public CompoundTag saveTag()
    {
        CompoundTag compound = new CompoundTag();

        if(hasWearable())
        {
            ItemStack wearable = getWearable();
            wearable.save(compound);
        }
        if(!hasWearable())
        {
            ItemStack wearable = ItemStack.EMPTY;
            wearable.save(compound);
        }
        return compound;
    }

    @Override
    public void loadTag(CompoundTag compoundTag)
    {
        ItemStack wearable = ItemStack.of(compoundTag);
        setWearable(wearable);
    }
}
