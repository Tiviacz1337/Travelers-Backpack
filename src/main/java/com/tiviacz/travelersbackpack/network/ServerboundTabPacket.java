package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;

public class ServerboundTabPacket implements IPacket<ServerboundTabPacket> {
    private final int slot;
    private final boolean open;
    private final int packetType;

    public ServerboundTabPacket(int slot, boolean open, int packetType) {
        this.slot = slot;
        this.open = open;
        this.packetType = packetType;
    }

    public static ServerboundTabPacket decode(final FriendlyByteBuf buffer) {
        final int slot = buffer.readInt();
        final boolean open = buffer.readBoolean();
        final int packetType = buffer.readInt();

        return new ServerboundTabPacket(slot, open, packetType);
    }

    public void encode(final ServerboundTabPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.slot);
        buffer.writeBoolean(message.open);
        buffer.writeInt(message.packetType);
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.TAB_ID;
    }

    public static final int TAB_OPEN = 0;
    public static final int UPGRADE_ENABLED = 1;
    public static final int SHIFT_CLICK_TO_BACKPACK = 2;
    public static final int PLAY_RECORD = 3;

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ServerboundTabPacket message = decode(buf);
        server.execute(() -> {
            if(player.containerMenu instanceof BackpackBaseMenu menu) {
                ItemStack upgradeStack = menu.getWrapper().getUpgrades().getStackInSlot(message.slot);
                if(!upgradeStack.isEmpty()) {
                    ItemStack updateStack = upgradeStack.copy();
                    NbtHelper.set(updateStack, getPacketType(message.packetType), message.open);
                    menu.getWrapper().getUpgrades().setStackInSlot(message.slot, updateStack);
                }
            }
        });
    }

    public static String getPacketType(int type) {
        return switch(type) {
            case 0 -> ModDataHelper.TAB_OPEN;
            case 1 -> ModDataHelper.UPGRADE_ENABLED;
            case 2 -> ModDataHelper.SHIFT_CLICK_TO_BACKPACK;
            case 3 -> ModDataHelper.IS_PLAYING;
            default -> ModDataHelper.TAB_OPEN;
        };
    }
}