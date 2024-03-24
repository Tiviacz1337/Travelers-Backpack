package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ClientboundUpdateRecipePacket implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(TravelersBackpack.MODID, "update_recipe");
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

    public static ClientboundUpdateRecipePacket read(final FriendlyByteBuf buffer)
    {
        ResourceLocation recipeId = new ResourceLocation(buffer.readUtf());

        return new ClientboundUpdateRecipePacket(recipeId, recipeId.equals(NULL) ? ItemStack.EMPTY : buffer.readItem());
    }

    @Override
    public void write(FriendlyByteBuf pBuffer)
    {
        pBuffer.writeUtf(recipeId.toString());
        if(!recipeId.equals(NULL))
        {
            pBuffer.writeItem(output);
        }
    }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }

    public static void handle(final ClientboundUpdateRecipePacket message, PlayPayloadContext ctx)
    {
        ctx.workHandler().submitAsync(() ->
        {
            RecipeHolder<?> recipe = Minecraft.getInstance().level.getRecipeManager().byKey(message.recipeId).orElse(null);

            if(Minecraft.getInstance().screen instanceof TravelersBackpackScreen screen)
            {
                screen.getMenu().resultSlots.setRecipeUsed(recipe);
                screen.getMenu().resultSlots.setItem(0, message.output);
            }
        });
    }
}