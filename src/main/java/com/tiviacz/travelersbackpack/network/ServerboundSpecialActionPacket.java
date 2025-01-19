package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ServerboundSpecialActionPacket implements IPacket<ServerboundSpecialActionPacket> {
    private final byte screenID;
    private final byte typeOfAction;
    private final double scrollDelta;

    public ServerboundSpecialActionPacket(byte screenID, byte typeOfAction, double scrollDelta) {
        this.screenID = screenID;
        this.typeOfAction = typeOfAction;
        this.scrollDelta = scrollDelta;
    }

    public static ServerboundSpecialActionPacket decode(final FriendlyByteBuf buffer) {
        final byte screenID = buffer.readByte();
        final byte typeOfAction = buffer.readByte();
        final double scrollDelta = buffer.readDouble();

        return new ServerboundSpecialActionPacket(screenID, typeOfAction, scrollDelta);
    }

    public void encode(final ServerboundSpecialActionPacket message, final FriendlyByteBuf buffer) {
        buffer.writeByte(message.screenID);
        buffer.writeByte(message.typeOfAction);
        buffer.writeDouble(message.scrollDelta);
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.SPECIAL_ACTION_ID;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ServerboundSpecialActionPacket message = decode(buf);
        server.execute(() -> {
            if(message.typeOfAction == Reference.SWAP_TOOL) {
                ServerActions.swapTool(player, message.scrollDelta);
            } else if(message.typeOfAction == Reference.SWITCH_HOSE_MODE) {
                ServerActions.switchHoseMode(player, message.scrollDelta);
            } else if(message.typeOfAction == Reference.TOGGLE_HOSE_TANK) {
                ServerActions.toggleHoseTank(player);
            } else if(message.typeOfAction == Reference.OPEN_SCREEN) {
                if(ComponentUtils.isWearingBackpack(player)) {
                    BackpackContainer.openBackpack(player, ComponentUtils.getWearingBackpack(player), Reference.WEARABLE_SCREEN_ID);
                }
            } else if(message.typeOfAction == Reference.TOGGLE_VISIBILITY) {
                ServerActions.toggleVisibility(player);
            }
        });
    }
}