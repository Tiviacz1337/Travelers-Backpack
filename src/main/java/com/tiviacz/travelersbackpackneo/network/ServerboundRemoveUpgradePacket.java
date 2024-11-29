package com.tiviacz.travelersbackpackneo.network;

import com.tiviacz.travelersbackpackneo.TravelersBackpack;
import com.tiviacz.travelersbackpackneo.initold.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpackneo.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.upgrades.IUpgrade;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record ServerboundRemoveUpgradePacket(int slot) implements CustomPacketPayload {
    public static final Type<ServerboundRemoveUpgradePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "remove_upgrade"));

    public static final StreamCodec<FriendlyByteBuf, ServerboundRemoveUpgradePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ServerboundRemoveUpgradePacket::slot,
            ServerboundRemoveUpgradePacket::new
    );

    public static void handle(final ServerboundRemoveUpgradePacket message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
                BackpackWrapper wrapper = menu.getWrapper();
                if(!wrapper.getUpgrades().getStackInSlot(message.slot()).isEmpty()) {
                    Optional<? extends IUpgrade> upgrade = wrapper.getUpgradeManager().mappedUpgrades.get(message.slot());

                    ItemStack upgradeStack = wrapper.getUpgrades().getStackInSlot(message.slot()).copy();
                    upgradeStack.set(ModDataComponents.TAB_OPEN, false);
                    wrapper.getUpgrades().setStackInSlot(message.slot(), ItemStack.EMPTY);

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
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}