package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.inventory.BackpackSettingsContainer;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ServerboundOpenSettingsPacket implements IPacket<ServerboundOpenSettingsPacket> {
    private final int entityId;
    private final boolean open;

    public ServerboundOpenSettingsPacket(int entityId, boolean open) {
        this.entityId = entityId;
        this.open = open;
    }

    public static ServerboundOpenSettingsPacket decode(final FriendlyByteBuf buffer) {
        final int entityId = buffer.readInt();
        final boolean open = buffer.readBoolean();

        return new ServerboundOpenSettingsPacket(entityId, open);
    }

    public void encode(final ServerboundOpenSettingsPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.entityId);
        buffer.writeBoolean(message.open);
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.OPEN_SETTINGS_ID;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ServerboundOpenSettingsPacket message = decode(buf);
        server.execute(() -> {
            if(player.getId() == message.entityId) {
                if(player.containerMenu instanceof BackpackBaseMenu menu) {
                    if(message.open) {
                        if(menu.getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
                            if(player.level().getBlockEntity(menu.getWrapper().getBackpackPos()) instanceof BackpackBlockEntity backpackBlockEntity) {
                                backpackBlockEntity.openSettings(player, backpackBlockEntity, menu.getWrapper().getBackpackPos());
                            }
                        } else {
                            BackpackSettingsContainer.openSettings(player, menu.getWrapper().getBackpackStack(), menu.getWrapper().getScreenID());
                        }
                    }
                } else if(player.containerMenu instanceof BackpackSettingsMenu menu) {
                    if(!message.open) {
                        if(menu.getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
                            if(player.level().getBlockEntity(menu.getWrapper().getBackpackPos()) instanceof BackpackBlockEntity backpackBlockEntity) {
                                //backpackBlockEntity.removeSettingsUser();
                                backpackBlockEntity.openBackpack(player, backpackBlockEntity, menu.getWrapper().getBackpackPos());
                            }
                        } else {
                            BackpackContainer.openBackpack(player, menu.getWrapper().getBackpackStack(), menu.getWrapper().getScreenID());
                        }
                    }
                }
            }
        });
    }
}