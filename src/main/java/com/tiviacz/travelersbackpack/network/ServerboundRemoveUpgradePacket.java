package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.upgrades.IUpgrade;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class ServerboundRemoveUpgradePacket {
    private final int slot;

    public ServerboundRemoveUpgradePacket(int slot) {
        this.slot = slot;
    }

    public static ServerboundRemoveUpgradePacket decode(final FriendlyByteBuf buffer) {
        final int slot = buffer.readInt();

        return new ServerboundRemoveUpgradePacket(slot);
    }

    public static void encode(final ServerboundRemoveUpgradePacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.slot);
    }

    public static void handle(final ServerboundRemoveUpgradePacket message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
                BackpackWrapper wrapper = menu.getWrapper();
                if(!wrapper.getUpgrades().getStackInSlot(message.slot).isEmpty()) {
                    Optional<? extends IUpgrade> upgrade = wrapper.getUpgradeManager().mappedUpgrades.get(message.slot);

                    ItemStack upgradeStack = wrapper.getUpgrades().getStackInSlot(message.slot).copy();
                    NbtHelper.set(upgradeStack, ModDataHelper.TAB_OPEN, false);
                    wrapper.getUpgrades().setStackInSlot(message.slot, ItemStack.EMPTY);

                    upgrade.ifPresent(iUpgrade -> iUpgrade.onUpgradeRemoved(upgradeStack));

                    if(!serverPlayer.getInventory().add(upgradeStack)) {
                        serverPlayer.drop(upgradeStack, true);
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

        ctx.get().setPacketHandled(true);
    }
}