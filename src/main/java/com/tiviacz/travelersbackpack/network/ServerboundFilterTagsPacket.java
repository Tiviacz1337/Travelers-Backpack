package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.upgrades.FilterUpgradeBase;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class ServerboundFilterTagsPacket {
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

    public static void encode(final ServerboundFilterTagsPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.slot);
        buffer.writeCollection(message.tags, FriendlyByteBuf::writeUtf);
    }

    public static void handle(final ServerboundFilterTagsPacket message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();

            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
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
        ctx.get().setPacketHandled(true);
    }
}