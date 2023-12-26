package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ServerboundSettingsPacket
{
    private final byte screenID;
    private final byte dataArray;
    private final int place;
    private final byte value;

    public ServerboundSettingsPacket(byte screenID, byte dataArray, int place, byte value)
    {
        this.screenID = screenID;
        this.dataArray = dataArray;
        this.place = place;
        this.value = value;
    }

    public static ServerboundSettingsPacket decode(final FriendlyByteBuf buffer)
    {
        final byte screenID = buffer.readByte();
        final byte dataArray = buffer.readByte();
        final int place = buffer.readInt();
        final byte value = buffer.readByte();

        return new ServerboundSettingsPacket(screenID, dataArray, place, value);
    }

    public static void encode(final ServerboundSettingsPacket message, final FriendlyByteBuf buffer)
    {
        buffer.writeByte(message.screenID);
        buffer.writeByte(message.dataArray);
        buffer.writeInt(message.place);
        buffer.writeByte(message.value);
    }

    public static void handle(final ServerboundSettingsPacket message, final CustomPayloadEvent.Context ctx)
    {
        ctx.enqueueWork(() -> {
            final ServerPlayer serverPlayer = ctx.getSender();

            if(serverPlayer != null)
            {
                if(message.screenID == Reference.WEARABLE_SCREEN_ID)
                {
                    SettingsManager manager = CapabilityUtils.getBackpackInv(serverPlayer).getSettingsManager();
                    manager.set(message.dataArray, message.place, message.value);
                }
                if(message.screenID == Reference.ITEM_SCREEN_ID)
                {
                    SettingsManager manager = ((TravelersBackpackItemMenu)serverPlayer.containerMenu).container.getSettingsManager();
                    manager.set(message.dataArray, message.place, message.value);
                }
                if(message.screenID == Reference.BLOCK_ENTITY_SCREEN_ID)
                {
                    SettingsManager manager = ((TravelersBackpackBlockEntityMenu)serverPlayer.containerMenu).container.getSettingsManager();
                    manager.set(message.dataArray, message.place, message.value);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
