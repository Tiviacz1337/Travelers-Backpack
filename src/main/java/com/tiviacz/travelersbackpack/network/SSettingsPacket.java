package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackItemContainer;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackTileContainer;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SSettingsPacket
{
    private final byte screenID;
    private final byte dataArray;
    private final int place;
    private final byte value;

    public SSettingsPacket(byte screenID, byte dataArray, int place, byte value)
    {
        this.screenID = screenID;
        this.dataArray = dataArray;
        this.place = place;
        this.value = value;
    }

    public static SSettingsPacket decode(final PacketBuffer buffer)
    {
        final byte screenID = buffer.readByte();
        final byte dataArray = buffer.readByte();
        final int place = buffer.readInt();
        final byte value = buffer.readByte();

        return new SSettingsPacket(screenID, dataArray, place, value);
    }

    public static void encode(final SSettingsPacket message, final PacketBuffer buffer)
    {
        buffer.writeByte(message.screenID);
        buffer.writeByte(message.dataArray);
        buffer.writeInt(message.place);
        buffer.writeByte(message.value);
    }

    public static void handle(final SSettingsPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity serverPlayerEntity = ctx.get().getSender();

            if(serverPlayerEntity != null)
            {
                if(message.screenID == Reference.WEARABLE_SCREEN_ID)
                {
                    SettingsManager manager = CapabilityUtils.getBackpackInv(serverPlayerEntity).getSettingsManager();
                    manager.set(message.dataArray, message.place, message.value);
                }
                if(message.screenID == Reference.ITEM_SCREEN_ID)
                {
                    SettingsManager manager = ((TravelersBackpackItemContainer)serverPlayerEntity.containerMenu).inventory.getSettingsManager();
                    manager.set(message.dataArray, message.place, message.value);
                }
                if(message.screenID == Reference.TILE_SCREEN_ID)
                {
                    SettingsManager manager = ((TravelersBackpackTileContainer)serverPlayerEntity.containerMenu).inventory.getSettingsManager();
                    manager.set(message.dataArray, message.place, message.value);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
