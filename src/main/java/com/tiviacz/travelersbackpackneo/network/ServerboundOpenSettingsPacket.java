package com.tiviacz.travelersbackpackneo.network;

import com.tiviacz.travelersbackpackneo.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpackneo.inventory.BackpackSettingsContainer;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundOpenSettingsPacket(int entityId, boolean open) implements CustomPacketPayload {
    public static final Type<ServerboundOpenSettingsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "open_settings"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundOpenSettingsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ServerboundOpenSettingsPacket::entityId,
            ByteBufCodecs.BOOL, ServerboundOpenSettingsPacket::open,
            ServerboundOpenSettingsPacket::new
    );

    public static void handle(final ServerboundOpenSettingsPacket message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if(player.getId() == message.entityId()) {
                if(player.containerMenu instanceof BackpackBaseMenu menu) {
                    if(message.open()) {
                        if(menu.getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
                            if(player.level().getBlockEntity(menu.getWrapper().getBackpackPos()) instanceof BackpackBlockEntity backpackBlockEntity) {
                                backpackBlockEntity.openSettings(player, backpackBlockEntity, menu.getWrapper().getBackpackPos());
                            }
                        } else {
                            BackpackSettingsContainer.openSettings((ServerPlayer)player, menu.getWrapper().getBackpackStack(), menu.getWrapper().getScreenID());
                        }
                    }
                } else if(player.containerMenu instanceof BackpackSettingsMenu menu) {
                    if(!message.open()) {
                        if(menu.getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
                            if(player.level().getBlockEntity(menu.getWrapper().getBackpackPos()) instanceof BackpackBlockEntity backpackBlockEntity) {
                                //backpackBlockEntity.removeSettingsUser();
                                backpackBlockEntity.openBackpack(player, backpackBlockEntity, menu.getWrapper().getBackpackPos());
                            }
                        } else {
                            BackpackContainer.openBackpack((ServerPlayer)player, menu.getWrapper().getBackpackStack(), menu.getWrapper().getScreenID());
                        }
                    }
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

