package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public record ServerboundSettingsPacket(byte screenID, byte dataArray, int place, byte value) implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(TravelersBackpack.MODID, "settings");

    public ServerboundSettingsPacket(final FriendlyByteBuf buffer)
    {
        this(buffer.readByte(), buffer.readByte(), buffer.readInt(), buffer.readByte());
    }

    @Override
    public void write(FriendlyByteBuf pBuffer)
    {
        pBuffer.writeByte(screenID);
        pBuffer.writeByte(dataArray);
        pBuffer.writeInt(place);
        pBuffer.writeByte(value);
    }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }

    public static void handle(final ServerboundSettingsPacket message, PlayPayloadContext ctx)
    {
        ctx.workHandler().submitAsync(() ->
        {
            final Optional<Player> player = ctx.player();

            if(player.isPresent() && player.get() instanceof ServerPlayer serverPlayer)
            {
                if(message.screenID == Reference.WEARABLE_SCREEN_ID)
                {
                    SettingsManager manager = AttachmentUtils.getBackpackInv(serverPlayer).getSettingsManager();
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
    }
}
