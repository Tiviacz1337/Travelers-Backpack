package com.tiviacz.travelersbackpack.component;

import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.Reference;
import dev.onyxstudios.cca.internal.entity.CardinalComponentsEntity;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class TravelersBackpackComponent implements ITravelersBackpackComponent
{
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
                this.inventory.readAllData(stack.getOrCreateNbt());
            }
        }
    }

    @Override
    public void sync()
    {
        syncWith((ServerPlayerEntity)this.player);
    }

    public void syncToTracking(ServerPlayerEntity player)
    {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(player.getId());
        buf.writeIdentifier(ComponentUtils.WEARABLE.getId());
        this.writeSyncPacket(buf, player);
        for(ServerPlayerEntity serverPlayer : PlayerLookup.tracking(player))
        {
            ServerPlayNetworking.send(serverPlayer, CardinalComponentsEntity.PACKET_ID, buf);
        }
    }

    public void syncWith(ServerPlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(player.getId());
        buf.writeIdentifier(ComponentUtils.WEARABLE.getId());
        this.writeSyncPacket(buf, player);
        ServerPlayNetworking.send(player, CardinalComponentsEntity.PACKET_ID, buf);
    }

    @Override
    public void readFromNbt(NbtCompound tag)
    {
        ItemStack wearable = ItemStack.fromNbt(tag);

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
    public void writeToNbt(NbtCompound tag)
    {
        if(hasWearable())
        {
            ItemStack wearable = getWearable();
            wearable.writeNbt(tag);
        }
        if(!hasWearable())
        {
            ItemStack wearable = new ItemStack((Item)null);
            wearable.writeNbt(tag);
        }
    }
}