package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.network.ClientboundSyncAttachmentPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.UnknownNullability;

public class TravelersBackpackSerializable implements INBTSerializable<CompoundTag>, ITravelersBackpack
{
    public final Player player;
    public final TravelersBackpackContainer container;
    public ItemStack wearable = new ItemStack(Items.AIR, 0);

    public TravelersBackpackSerializable(IAttachmentHolder holder)
    {
        this.player = (Player)holder;
        this.container = new TravelersBackpackContainer(this.wearable, player, Reference.WEARABLE_SCREEN_ID);
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
        if(player != null && !player.level().isClientSide)
        {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            AttachmentUtils.getAttachment(serverPlayer).ifPresent(cap -> PacketDistributor.PLAYER.with(serverPlayer).send(new ClientboundSyncAttachmentPacket(serverPlayer.getId(), true, this.wearable.save(new CompoundTag()))));
        }
    }

    @Override
    public void synchroniseToOthers(Player player)
    {
        if(player != null && !player.level().isClientSide)
        {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            AttachmentUtils.getAttachment(serverPlayer).ifPresent(cap -> PacketDistributor.TRACKING_ENTITY.with(serverPlayer).send(new ClientboundSyncAttachmentPacket(serverPlayer.getId(), true, this.wearable.save(new CompoundTag()))));
        }
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT()
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
    public void deserializeNBT(CompoundTag nbt)
    {
        ItemStack wearable = ItemStack.of(nbt);
        setWearable(wearable);
        setContents(wearable);
    }
}