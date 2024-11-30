package com.tiviacz.travelersbackpackneo.network;

import com.tiviacz.travelersbackpackneo.TravelersBackpack;
import com.tiviacz.travelersbackpackneo.initold.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.DeferredHolder;

public record ServerboundTabPacket(int slot, boolean open, int packetType) implements CustomPacketPayload {
    public static final Type<ServerboundTabPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "tab"));
    public static final StreamCodec<FriendlyByteBuf, ServerboundTabPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ServerboundTabPacket::slot,
            ByteBufCodecs.BOOL, ServerboundTabPacket::open,
            ByteBufCodecs.INT, ServerboundTabPacket::packetType,
            ServerboundTabPacket::new
    );

    public static final int TAB_OPEN = 0;
    public static final int UPGRADE_ENABLED = 1;
    public static final int SHIFT_CLICK_TO_BACKPACK = 2;
    public static final int PLAY_RECORD = 3;

    public static void handle(final ServerboundTabPacket message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
                ItemStack upgradeStack = menu.getWrapper().getUpgrades().getStackInSlot(message.slot());
                if(!upgradeStack.isEmpty()) {
                    ItemStack updateStack = upgradeStack.copy();
                    updateStack.set(getPacketType(message.packetType()), message.open());
                    menu.getWrapper().getUpgrades().setStackInSlot(message.slot(), updateStack);
                }
            }
        });
    }

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> getPacketType(int type) {
        return switch(type) {
            case 0 -> ModDataComponents.TAB_OPEN;
            case 1 -> ModDataComponents.UPGRADE_ENABLED;
            case 2 -> ModDataComponents.SHIFT_CLICK_TO_BACKPACK;
            case 3 -> ModDataComponents.IS_PLAYING;
            default -> ModDataComponents.TAB_OPEN;
        };
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}