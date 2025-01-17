package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.upgrades.IUpgrade;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ServerboundRemoveUpgradePacket implements IPacket<ServerboundRemoveUpgradePacket> {
    private final int slot;

    public ServerboundRemoveUpgradePacket(int slot) {
        this.slot = slot;
    }

    public static ServerboundRemoveUpgradePacket decode(final FriendlyByteBuf buffer) {
        final int slot = buffer.readInt();

        return new ServerboundRemoveUpgradePacket(slot);
    }

    public void encode(final ServerboundRemoveUpgradePacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.slot);
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.REMOVE_UPGRADE_ID;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ServerboundRemoveUpgradePacket message = decode(buf);
        server.execute(() -> {
            if(player.containerMenu instanceof BackpackBaseMenu menu) {
                BackpackWrapper wrapper = menu.getWrapper();
                if(!wrapper.getUpgrades().getStackInSlot(message.slot).isEmpty()) {
                    Optional<? extends IUpgrade> upgrade = wrapper.getUpgradeManager().mappedUpgrades.get(message.slot);

                    ItemStack upgradeStack = wrapper.getUpgrades().getStackInSlot(message.slot).copy();
                    NbtHelper.set(upgradeStack, ModDataHelper.TAB_OPEN, false);
                    wrapper.getUpgrades().setStackInSlot(message.slot, ItemStack.EMPTY);

                    upgrade.ifPresent(iUpgrade -> iUpgrade.onUpgradeRemoved(upgradeStack));

                    if(!player.getInventory().add(upgradeStack)) {
                        player.drop(upgradeStack, true);
                    }
                    for(Player user : wrapper.getPlayersUsing()) {
                        if(user.containerMenu instanceof BackpackBaseMenu) {
                            user.containerMenu.broadcastFullState();
                        }
                    }
                    wrapper.saveHandler.run();
                }
            }
        });
    }
}