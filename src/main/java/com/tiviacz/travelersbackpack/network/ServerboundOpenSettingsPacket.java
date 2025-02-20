package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.inventory.BackpackSettingsContainer;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundOpenSettingsPacket {
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

    public static void encode(final ServerboundOpenSettingsPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.entityId);
        buffer.writeBoolean(message.open);
    }

    public static void handle(final ServerboundOpenSettingsPacket message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if(player.getId() == message.entityId) {
                if(player.containerMenu instanceof BackpackBaseMenu menu) {
                    if(message.open) {
                        if(menu.getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
                            if(player.level().getBlockEntity(menu.getWrapper().getBackpackPos()) instanceof BackpackBlockEntity backpackBlockEntity) {
                                backpackBlockEntity.openSettings(player, backpackBlockEntity, menu.getWrapper().getBackpackPos());
                            }
                        } else {
                            BackpackSettingsContainer.openSettings((ServerPlayer)player, menu.getWrapper().getBackpackStack(), menu.getWrapper().getScreenID(), menu.getWrapper().getBackpackSlotIndex());
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
                            BackpackContainer.openBackpack((ServerPlayer)player, menu.getWrapper().getBackpackStack(), menu.getWrapper().getScreenID(), menu.getWrapper().getBackpackSlotIndex());
                        }
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}