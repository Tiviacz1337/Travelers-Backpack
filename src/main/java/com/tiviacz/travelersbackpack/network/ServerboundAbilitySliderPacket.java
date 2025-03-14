package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record ServerboundAbilitySliderPacket(byte screenID, boolean sliderValue) implements CustomPacketPayload {
    public static final Type<ServerboundAbilitySliderPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "ability_slider"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundAbilitySliderPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, ServerboundAbilitySliderPacket::screenID,
            ByteBufCodecs.BOOL, ServerboundAbilitySliderPacket::sliderValue,
            ServerboundAbilitySliderPacket::new
    );

    public static void handle(final ServerboundAbilitySliderPacket message, ServerPlayNetworking.Context ctx) {
        ctx.player().getServer().execute(() -> {
            Player player = ctx.player();
            BackpackWrapper wrapper = ComponentUtils.getBackpackWrapper(player);

            //If ability slider is being switched in the backpack screen, then reassign the wrapper
            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
                wrapper = menu.getWrapper();
            }

            ServerActions.switchAbilitySlider(wrapper, message.sliderValue());
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}