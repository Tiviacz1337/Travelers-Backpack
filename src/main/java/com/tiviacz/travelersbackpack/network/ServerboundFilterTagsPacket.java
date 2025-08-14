package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.upgrades.FilterUpgradeBase;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ServerboundFilterTagsPacket implements IPacket<ServerboundFilterTagsPacket> {
    private final int slot;
    private final List<String> tags;

    public ServerboundFilterTagsPacket(int slot, List<String> tags) {
        this.slot = slot;
        this.tags = tags;
    }

    public static ServerboundFilterTagsPacket decode(final FriendlyByteBuf buffer) {
        final int slot = buffer.readInt();
        final List<String> tags = buffer.readList(FriendlyByteBuf::readUtf);

        return new ServerboundFilterTagsPacket(slot, tags);
    }

    public void encode(final ServerboundFilterTagsPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.slot);
        buffer.writeCollection(message.tags, FriendlyByteBuf::writeUtf);
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.FILTER_TAGS_ID;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ServerboundFilterTagsPacket message = decode(buf);

        server.execute(() -> {
            if(player.containerMenu instanceof BackpackBaseMenu menu) {
                BackpackWrapper wrapper = menu.getWrapper();
                if(!wrapper.getUpgrades().getStackInSlot(message.slot).isEmpty()) {

                    ItemStack upgradeStack = wrapper.getUpgrades().getStackInSlot(message.slot).copy();
                    NbtHelper.set(upgradeStack, ModDataHelper.FILTER_TAGS, message.tags);
                    wrapper.getUpgrades().setStackInSlot(message.slot, upgradeStack);

                    if(wrapper.getUpgradeManager().mappedUpgrades.get(message.slot).isPresent()) {
                        if(wrapper.getUpgradeManager().mappedUpgrades.get(message.slot).get() instanceof FilterUpgradeBase<?, ?> filterUpgrade) {
                            filterUpgrade.getFilterSettings().updateFilterTags(message.tags);
                        }
                    }
                    menu.getWrapper().saveHandler.run();
                }
            }
        });
    }
}