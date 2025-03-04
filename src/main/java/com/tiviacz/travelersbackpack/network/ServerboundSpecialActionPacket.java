package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundSpecialActionPacket {
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

    public static void encode(final ServerboundSpecialActionPacket message, final FriendlyByteBuf buffer) {
        buffer.writeByte(message.screenID);
        buffer.writeByte(message.typeOfAction);
        buffer.writeDouble(message.scrollDelta);
    }

    public static void handle(final ServerboundSpecialActionPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if(player instanceof ServerPlayer serverPlayer) {
                if(message.typeOfAction == Reference.SWAP_TOOL) {
                    ServerActions.swapTool(serverPlayer, message.scrollDelta);
                } else if(message.typeOfAction == Reference.SWITCH_HOSE_MODE) {
                    ServerActions.switchHoseMode(serverPlayer, message.scrollDelta);
                } else if(message.typeOfAction == Reference.TOGGLE_HOSE_TANK) {
                    ServerActions.toggleHoseTank(serverPlayer);
                } else if(message.typeOfAction == Reference.OPEN_SCREEN) {
                    if(CapabilityUtils.isWearingBackpack(serverPlayer)) {
                        BackpackContainer.openBackpack(serverPlayer, CapabilityUtils.getWearingBackpack(serverPlayer), Reference.WEARABLE_SCREEN_ID);
                    }
                } else if(message.typeOfAction == Reference.TOGGLE_VISIBILITY) {
                    ServerActions.toggleVisibility(serverPlayer);
                } else if(message.typeOfAction == Reference.TOGGLE_BUTTONS_VISIBILITY) {
                    ServerActions.toggleButtonsVisibility(serverPlayer);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}