package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.network.CSyncCapabilityPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.PacketDistributor;

public class TravelersBackpackWearable implements ITravelersBackpack
{
    private ItemStack wearable = new ItemStack(Items.AIR, 0);
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
        this.wearable = new ItemStack(Items.AIR, 0);
        this.inventory.setStack(new ItemStack(Items.AIR, 0));
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
            CapabilityUtils.getCapability(serverPlayerEntity).ifPresent(cap -> TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> serverPlayerEntity), new CSyncCapabilityPacket(this.wearable.save(new CompoundNBT()), serverPlayerEntity.getId(), true)));
        }
    }

    @Override
    public void synchroniseToOthers(PlayerEntity player)
    {
        if(player != null && !player.level.isClientSide)
        {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
            CapabilityUtils.getCapability(serverPlayerEntity).ifPresent(cap -> TravelersBackpack.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> serverPlayerEntity), new CSyncCapabilityPacket(this.wearable.save(new CompoundNBT()), serverPlayerEntity.getId(), true)));
        }
    }
}