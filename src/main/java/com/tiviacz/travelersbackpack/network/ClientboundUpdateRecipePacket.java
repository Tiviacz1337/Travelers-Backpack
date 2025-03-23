package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingUpgrade;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ClientboundUpdateRecipePacket {
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

    public static ClientboundUpdateRecipePacket decode(final FriendlyByteBuf buffer) {
        ResourceLocation recipeId = buffer.readResourceLocation();
        ItemStack output = buffer.readItem();
        return new ClientboundUpdateRecipePacket(recipeId, output);
    }

    public static void encode(final ClientboundUpdateRecipePacket message, final FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(message.id);
        buffer.writeItem(message.output);
    }

    public static void handle(final ClientboundUpdateRecipePacket message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
        {
            Recipe<?> recipe = Minecraft.getInstance().level.getRecipeManager().byKey(message.id).orElse(null);
            if(Minecraft.getInstance().screen instanceof BackpackScreen screen) {
                screen.getMenu().getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).ifPresent(upgrade -> {
                    screen.getMenu().getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).get().resultSlots.setRecipeUsed(recipe);
                    screen.getMenu().getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).get().resultSlots.setItem(0, message.output);
                });
            }
        }));

        ctx.get().setPacketHandled(true);
    }
}