package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundAbilitySliderPacket {
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

    public static void encode(final ServerboundAbilitySliderPacket message, final FriendlyByteBuf buffer) {
        buffer.writeByte(message.screenID);
        buffer.writeBoolean(message.sliderValue);
    }

    public static void handle(final ServerboundAbilitySliderPacket message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            final Player player = ctx.get().getSender();
            BackpackWrapper wrapper = CapabilityUtils.getBackpackWrapper(player);

            //If ability slider is being switched in the backpack screen, then reassign the wrapper
            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
                wrapper = menu.getWrapper();
            }

            ServerActions.switchAbilitySlider(wrapper, message.sliderValue);

            //Update backpack data on clients
            wrapper.sendDataToClients(ModDataHelper.ABILITY_ENABLED);
        });

        ctx.get().setPacketHandled(true);
    }
}