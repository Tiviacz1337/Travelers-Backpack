package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.network.ClientboundSyncCapabilityPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.PacketDistributor;

public class TravelersBackpackWearable implements ITravelersBackpack
{
    private ItemStack wearable = new ItemStack(Items.AIR, 0);
    private final Player playerEntity;
    private final TravelersBackpackContainer container;

    public TravelersBackpackWearable(final Player playerEntity)
    {
        this.playerEntity = playerEntity;
        this.container = new TravelersBackpackContainer(this.wearable, playerEntity, Reference.WEARABLE_SCREEN_ID);
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
        this.container.setStack(new ItemStack(Items.AIR, 0));
    }

    @Override
    public TravelersBackpackContainer getContainer()
    {
        return this.container;
    }

    @Override
    public void setContents(ItemStack stack)
    {
        this.container.setStack(stack);

        if(!stack.isEmpty())
        {
            this.container.loadAllData(stack.getOrCreateTag());
        }
    }

    @Override
    public void synchronise()
    {
        if(playerEntity != null && !playerEntity.level().isClientSide)
        {
            ServerPlayer serverPlayer = (ServerPlayer)playerEntity;
            CapabilityUtils.getCapability(serverPlayer).ifPresent(cap -> TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ClientboundSyncCapabilityPacket(this.wearable.save(new CompoundTag()), serverPlayer.getId(), true)));
        }
    }

    @Override
    public void synchroniseToOthers(Player player)
    {
        if(player != null && !player.level().isClientSide)
        {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            CapabilityUtils.getCapability(serverPlayer).ifPresent(cap -> TravelersBackpack.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> serverPlayer), new ClientboundSyncCapabilityPacket(this.wearable.save(new CompoundTag()), serverPlayer.getId(), true)));
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
            ItemStack wearable = new ItemStack(Items.AIR, 0);
            wearable.save(compound);
        }
        return compound;
    }

    @Override
    public void loadTag(CompoundTag compoundTag)
    {
        ItemStack wearable = ItemStack.of(compoundTag);
        setWearable(wearable);
        setContents(wearable);
    }
}