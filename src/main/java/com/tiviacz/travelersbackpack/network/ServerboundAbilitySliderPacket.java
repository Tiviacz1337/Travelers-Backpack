package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ServerboundAbilitySliderPacket implements IPacket<ServerboundAbilitySliderPacket> {
    private final byte screenID;
    private final boolean sliderValue;

    public ServerboundAbilitySliderPacket(byte screenID, boolean sliderValue) {
        this.screenID = screenID;
        this.sliderValue = sliderValue;
    }

    public static ServerboundAbilitySliderPacket decode(final FriendlyByteBuf buffer) {
        final byte screenID = buffer.readByte();
        final boolean sliderValue = buffer.readBoolean();

        return new ServerboundAbilitySliderPacket(screenID, sliderValue);
    }

    public void encode(final ServerboundAbilitySliderPacket message, final FriendlyByteBuf buffer) {
        buffer.writeByte(message.screenID);
        buffer.writeBoolean(message.sliderValue);
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.ABILITY_SLIDER_ID;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ServerboundAbilitySliderPacket message = decode(buf);
        server.execute(() -> {
            BackpackWrapper wrapper = ComponentUtils.getBackpackWrapper(player);

            //If ability slider is being switched in the backpack screen, then reassign the wrapper
            if(player.containerMenu instanceof BackpackBaseMenu menu) {
                wrapper = menu.getWrapper();
            }

            ServerActions.switchAbilitySlider(wrapper, message.sliderValue);

            //Update backpack data on clients
            wrapper.sendDataToClients(ModDataHelper.ABILITY_ENABLED);
        });
    }
}