package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

public class ClientboundUpdateRecipePacket {
    public static final ResourceLocation NULL = ResourceLocation.fromNamespaceAndPath("null", "null");

    private final ResourceLocation id;
    private final ItemStack output;

    public ClientboundUpdateRecipePacket(@Nullable RecipeHolder<CraftingRecipe> recipe, ItemStack output) {
        this(recipe == null ? NULL : recipe.id(), output);
    }

    public ClientboundUpdateRecipePacket(ResourceLocation id, ItemStack output) {
        this.id = id;
        this.output = output;
    }

    public static ClientboundUpdateRecipePacket decode(final RegistryFriendlyByteBuf buffer) {
        ResourceLocation recipeId = ResourceLocation.STREAM_CODEC.decode(buffer);
        ItemStack output = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
        return new ClientboundUpdateRecipePacket(recipeId, output);
    }

    public static void encode(final ClientboundUpdateRecipePacket message, final RegistryFriendlyByteBuf buffer) {
        ResourceLocation.STREAM_CODEC.encode(buffer, message.id);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, message.output);
    }

    public static void handle(final ClientboundUpdateRecipePacket message, final CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
        {
            RecipeHolder<CraftingRecipe> recipe = (RecipeHolder<CraftingRecipe>)Minecraft.getInstance().level.getRecipeManager().byKey(message.id).orElse(null);
            if(Minecraft.getInstance().screen instanceof BackpackScreen screen) {
                screen.getMenu().getWrapper().getUpgradeManager().craftingUpgrade.ifPresent(upgrade -> {
                    screen.getMenu().getWrapper().getUpgradeManager().craftingUpgrade.get().resultSlots.setRecipeUsed(recipe);
                    screen.getMenu().getWrapper().getUpgradeManager().craftingUpgrade.get().resultSlots.setItem(0, message.output);
                });
            }
        }));

        ctx.setPacketHandled(true);
    }
}