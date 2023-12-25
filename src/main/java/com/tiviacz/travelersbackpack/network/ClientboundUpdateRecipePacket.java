package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.DistExecutor;
import net.neoforged.neoforge.network.NetworkEvent;

public class ClientboundUpdateRecipePacket
{
    public static final ResourceLocation NULL = new ResourceLocation("null", "null");

    private final ResourceLocation recipeId;
    private final ItemStack output;

    public ClientboundUpdateRecipePacket(RecipeHolder recipe, ItemStack output)
    {
        this.recipeId = recipe == null ? NULL : recipe.id();
        this.output = output;
    }

    public ClientboundUpdateRecipePacket(ResourceLocation recipeId, ItemStack output)
    {
        this.recipeId = recipeId;
        this.output = output;
    }

    public static ClientboundUpdateRecipePacket decode(final FriendlyByteBuf buffer)
    {
        ResourceLocation recipeId = new ResourceLocation(buffer.readUtf());

        return new ClientboundUpdateRecipePacket(recipeId, recipeId.equals(NULL) ? ItemStack.EMPTY : buffer.readItem());
    }

    public static void encode(final ClientboundUpdateRecipePacket message, final FriendlyByteBuf buffer)
    {
        buffer.writeUtf(message.recipeId.toString());
        if(!message.recipeId.equals(NULL))
        {
            buffer.writeItem(message.output);
        }
    }

    public static void handle(final ClientboundUpdateRecipePacket message, final NetworkEvent.Context ctx)
    {
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
        {
            RecipeHolder<?> recipe = Minecraft.getInstance().level.getRecipeManager().byKey(message.recipeId).orElse(null);

            if(Minecraft.getInstance().screen instanceof TravelersBackpackScreen screen)
            {
                screen.getMenu().resultSlots.setRecipeUsed(recipe);
                screen.getMenu().resultSlots.setItem(0, message.output);
            }
        }));

        ctx.setPacketHandled(true);
    }
}