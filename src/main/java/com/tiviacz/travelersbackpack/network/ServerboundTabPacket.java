package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.registries.RegistryObject;

public class ServerboundTabPacket {
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

    public static void encode(final ServerboundTabPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.slot);
        buffer.writeBoolean(message.open);
        buffer.writeInt(message.packetType);
    }

    public static final int TAB_OPEN = 0;
    public static final int UPGRADE_ENABLED = 1;
    public static final int SHIFT_CLICK_TO_BACKPACK = 2;
    public static final int PLAY_RECORD = 3;

    public static void handle(final ServerboundTabPacket message, final CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.getSender();
            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
                ItemStack upgradeStack = menu.getWrapper().getUpgrades().getStackInSlot(message.slot);
                if(!upgradeStack.isEmpty()) {
                    ItemStack updateStack = upgradeStack.copy();
                    updateStack.set(getPacketType(message.packetType).get(), message.open);
                    menu.getWrapper().getUpgrades().setStackInSlot(message.slot, updateStack);
                }
            }
        });
        ctx.setPacketHandled(true);
    }

    public static RegistryObject<DataComponentType<Boolean>> getPacketType(int type) {
        return switch(type) {
            case 0 -> ModDataComponents.TAB_OPEN;
            case 1 -> ModDataComponents.UPGRADE_ENABLED;
            case 2 -> ModDataComponents.SHIFT_CLICK_TO_BACKPACK;
            case 3 -> ModDataComponents.IS_PLAYING;
            default -> ModDataComponents.TAB_OPEN;
        };
    }
}