package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpackneo.TravelersBackpack;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public record ClientboundUpdateRecipePacket(ResourceLocation id, ItemStack output) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "update_recipe");
    public static final Type<ClientboundUpdateRecipePacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateRecipePacket> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ClientboundUpdateRecipePacket::id,
            ItemStack.OPTIONAL_STREAM_CODEC, ClientboundUpdateRecipePacket::output,
            ClientboundUpdateRecipePacket::new
    );

    public static final ResourceLocation NULL = ResourceLocation.fromNamespaceAndPath("null", "null");

    public ClientboundUpdateRecipePacket(@Nullable RecipeHolder<CraftingRecipe> recipe, ItemStack output) {
        this(recipe == null ? NULL : recipe.id(), output);
    }

    public static void handle(final ClientboundUpdateRecipePacket message, ClientPlayNetworking.Context ctx) {
        ctx.client().execute(() -> {
            RecipeHolder<CraftingRecipe> recipe = (RecipeHolder<CraftingRecipe>) Minecraft.getInstance().level.getRecipeManager().byKey(message.id()).orElse(null);
            if (Minecraft.getInstance().screen instanceof BackpackScreen screen) {
                screen.getMenu().getWrapper().getUpgradeManager().craftingUpgrade.ifPresent(upgrade -> {
                    screen.getMenu().getWrapper().getUpgradeManager().craftingUpgrade.get().resultSlots.setRecipeUsed(recipe);
                    screen.getMenu().getWrapper().getUpgradeManager().craftingUpgrade.get().resultSlots.setItem(0, message.output());
                });
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}