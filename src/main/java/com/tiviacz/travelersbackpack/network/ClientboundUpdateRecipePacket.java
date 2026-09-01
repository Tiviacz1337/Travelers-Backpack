package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingUpgrade;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record ClientboundUpdateRecipePacket(ItemStack output) implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "update_recipe");
    public static final Type<ClientboundUpdateRecipePacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateRecipePacket> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, ClientboundUpdateRecipePacket::output,
            ClientboundUpdateRecipePacket::new);

    public static void handle(ClientboundUpdateRecipePacket message, ClientPlayNetworking.Context ctx) {
        ctx.client().execute(() -> {
            if(Minecraft.getInstance().screen instanceof BackpackScreen screen) {
                screen.getMenu().getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).ifPresent(upgrade -> upgrade.resultSlots.setItem(0, message.output()));
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}