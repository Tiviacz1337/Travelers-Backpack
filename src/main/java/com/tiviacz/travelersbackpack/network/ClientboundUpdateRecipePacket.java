package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingUpgrade;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

public class ClientboundUpdateRecipePacket implements IPacket<ClientboundUpdateRecipePacket> {
    public static final ResourceLocation NULL = new ResourceLocation("null", "null");

    private final ResourceLocation id;
    private final ItemStack output;

    public ClientboundUpdateRecipePacket(@Nullable Recipe recipe, ItemStack output) {
        this(recipe == null ? NULL : recipe.getId(), output);
    }

    public ClientboundUpdateRecipePacket(ResourceLocation id, ItemStack output) {
        this.id = id;
        this.output = output;
    }

    public static ClientboundUpdateRecipePacket decode(FriendlyByteBuf buffer) {
        ResourceLocation recipeId = buffer.readResourceLocation();
        ItemStack output = buffer.readItem();
        return new ClientboundUpdateRecipePacket(recipeId, output);
    }

    public void encode(ClientboundUpdateRecipePacket message, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(message.id);
        buffer.writeItem(message.output);
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.UPDATE_RECIPE_ID;
    }

    public static void handle(Minecraft client, ClientPacketListener listener, FriendlyByteBuf buf, PacketSender sender) {
        ClientboundUpdateRecipePacket message = decode(buf);
        client.execute(() -> {
            Recipe<?> recipe = Minecraft.getInstance().level.getRecipeManager().byKey(message.id).orElse(null);
            if(Minecraft.getInstance().screen instanceof BackpackScreen screen) {
                screen.getMenu().getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).ifPresent(upgrade -> {
                    screen.getMenu().getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).get().resultSlots.setRecipeUsed(recipe);
                    screen.getMenu().getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).get().resultSlots.setItem(0, message.output);
                });
            }
        });
    }
}