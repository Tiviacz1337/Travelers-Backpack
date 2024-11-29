package com.tiviacz.travelersbackpackold.network;

import com.tiviacz.travelersbackpackold.TravelersBackpack;
import com.tiviacz.travelersbackpackold.common.ServerActions;
import com.tiviacz.travelersbackpackold.component.ComponentUtils;
import com.tiviacz.travelersbackpackold.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpackold.util.Reference;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SpecialActionPacket(byte screenID, byte typeOfAction, double scrollDelta) implements CustomPayload
{
    public static final CustomPayload.Id<SpecialActionPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(TravelersBackpack.MODID, "special_action"));
    public static final PacketCodec<RegistryByteBuf, SpecialActionPacket> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.BYTE, SpecialActionPacket::screenID, PacketCodecs.BYTE, SpecialActionPacket::typeOfAction, PacketCodecs.DOUBLE, SpecialActionPacket::scrollDelta, SpecialActionPacket::new);

    public static void apply(SpecialActionPacket message, ServerPlayNetworking.Context context)
    {
        context.player().getServer().execute(() ->
        {
            if(context.player() != null)
            {
                if(message.typeOfAction() == Reference.SWAP_TOOL)
                {
                    ServerActions.swapTool(context.player(), message.scrollDelta());
                }

                else if(message.typeOfAction() == Reference.SWITCH_HOSE_MODE)
                {
                    ServerActions.switchHoseMode(context.player(), message.scrollDelta());
                }

                else if(message.typeOfAction() == Reference.TOGGLE_HOSE_TANK)
                {
                    ServerActions.toggleHoseTank(context.player());
                }

                else if(message.typeOfAction() == Reference.EMPTY_TANK)
                {
                    ServerActions.emptyTank(message.scrollDelta(), context.player(), context.player().getServerWorld(), message.screenID());
                }

                else if(message.typeOfAction() == Reference.OPEN_SCREEN)
                {
                    if(ComponentUtils.isWearingBackpack(context.player()))
                    {
                        TravelersBackpackInventory.openHandledScreen(context.player(), ComponentUtils.getWearingBackpack(context.player()), Reference.WEARABLE_SCREEN_ID);
                    }
                }

                else if (message.typeOfAction() == Reference.TOGGLE_VISIBILITY) {
                    ServerActions.toggleVisibility(context.player());
                }
            }
        });
    }

    @Override
    public Id<? extends CustomPayload> getId()
    {
        return PACKET_ID;
    }
}
