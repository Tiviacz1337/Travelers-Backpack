package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.upgrades.IUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ServerboundRemoveUpgradePacket(int slot) implements CustomPacketPayload {
    public static final Type<ServerboundRemoveUpgradePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "remove_upgrade"));

    public static final StreamCodec<FriendlyByteBuf, ServerboundRemoveUpgradePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ServerboundRemoveUpgradePacket::slot,
            ServerboundRemoveUpgradePacket::new
    );

    public static void handle(final ServerboundRemoveUpgradePacket message, ServerPlayNetworking.Context ctx) {
        ctx.player().getServer().execute(() -> {
            Player player = ctx.player();
            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
                BackpackWrapper wrapper = menu.getWrapper();
                if(!wrapper.getUpgrades().getStackInSlot(message.slot()).isEmpty()) {
                    Optional<? extends UpgradeBase<?>> upgrade = wrapper.getUpgradeManager().mappedUpgrades.get(message.slot());

                    ItemStack upgradeStack = wrapper.getUpgrades().getStackInSlot(message.slot()).copy();
                    upgradeStack.set(ModDataComponents.TAB_OPEN, false);
                    wrapper.getUpgrades().setStackInSlot(message.slot(), ItemStack.EMPTY);

                    upgrade.ifPresent(upgradeBase -> upgradeBase.onUpgradeRemoved(upgradeStack));

                    if(!serverPlayer.getInventory().add(upgradeStack)) {
                        serverPlayer.drop(upgradeStack, true);
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