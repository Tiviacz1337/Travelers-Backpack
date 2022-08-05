package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CUpdateRecipePacket
{
    public static final ResourceLocation NULL = new ResourceLocation("null", "null");

    private final ResourceLocation recipeId;
    private final ItemStack output;

    public CUpdateRecipePacket(IRecipe recipe, ItemStack output)
    {
        this.recipeId = recipe == null ? NULL : recipe.getId();
        this.output = output;
    }

    public CUpdateRecipePacket(ResourceLocation recipeId, ItemStack output)
    {
        this.recipeId = recipeId;
        this.output = output;
    }

    public static CUpdateRecipePacket decode(final PacketBuffer buffer)
    {
        ResourceLocation recipeId = new ResourceLocation(buffer.readUtf());

        return new CUpdateRecipePacket(recipeId, recipeId.equals(NULL) ? ItemStack.EMPTY : buffer.readItem());
    }

    public static void encode(final CUpdateRecipePacket message, final PacketBuffer buffer)
    {
        buffer.writeUtf(message.recipeId.toString());
        if(!message.recipeId.equals(NULL))
        {
            buffer.writeItem(message.output);
        }
    }

    public static void handle(final CUpdateRecipePacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {

            IRecipe<?> recipe = Minecraft.getInstance().level.getRecipeManager().byKey(message.recipeId).orElse(null);
            if(Minecraft.getInstance().screen instanceof TravelersBackpackScreen)
            {
                ((TravelersBackpackScreen)Minecraft.getInstance().screen).getMenu().craftResult.setRecipeUsed(recipe); // = (ICraftingRecipe)recipe;
                ((TravelersBackpackScreen)Minecraft.getInstance().screen).getMenu().craftResult.setItem(0, message.output);
            }
        }));

        ctx.get().setPacketHandled(true);
    }
}