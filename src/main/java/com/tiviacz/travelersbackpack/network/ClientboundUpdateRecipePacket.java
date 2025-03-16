package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingUpgrade;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundUpdateRecipePacket(ItemStack output) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "update_recipe");
    public static final Type<ClientboundUpdateRecipePacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateRecipePacket> STREAM_CODEC = StreamCodec.composite(
            // RecipeHolder.STREAM_CODEC, ClientboundUpdateRecipePacket::holder,
            ItemStack.OPTIONAL_STREAM_CODEC, ClientboundUpdateRecipePacket::output,
            ClientboundUpdateRecipePacket::new
    );

    //public static final ResourceLocation NULL = ResourceLocation.fromNamespaceAndPath("null", "null");

    //public ClientboundUpdateRecipePacket(@Nullable RecipeHolder<CraftingRecipe> recipe, ItemStack output) {
    //     this(recipe == null ? NULL : recipe.id().location(), output);
    // }

    public static void handle(final ClientboundUpdateRecipePacket message, IPayloadContext ctx) {
        if(ctx.flow().isClientbound()) {
            ctx.enqueueWork(() -> {
                //RecipeHolder<?> recipe = message.holder(); //(RecipeHolder<CraftingRecipe>)Minecraft.getInstance().level.recipeAccess().byKey(message.id()).orElse(null);
                if(Minecraft.getInstance().screen instanceof BackpackScreen screen) {
                    screen.getMenu().getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).ifPresent(upgrade -> {
                        //screen.getMenu().getWrapper().getUpgradeManager().craftingUpgrade.get().resultSlots.setRecipeUsed(recipe);
                        screen.getMenu().getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).get().resultSlots.setItem(0, message.output());
                    });
                }
            });
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}