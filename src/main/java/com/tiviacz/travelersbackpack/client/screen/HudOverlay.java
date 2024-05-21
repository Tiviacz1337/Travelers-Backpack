package com.tiviacz.travelersbackpack.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.InventoryImproved;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HudOverlay
{
    private static final Identifier OVERLAY = new Identifier(TravelersBackpack.MODID, "textures/gui/travelers_backpack_overlay.png");
    private static float animationProgress = 0.0F;

    public static void render(DrawContext context, float tickDelta)
    {
        if(!TravelersBackpackConfig.getConfig().client.overlay.enableOverlay || !ComponentUtils.isWearingBackpack(MinecraftClient.getInstance().player)) return;

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        int scaledWidth = client.getWindow().getScaledWidth() - TravelersBackpackConfig.getConfig().client.overlay.offsetX;
        int scaledHeight = client.getWindow().getScaledHeight() - TravelersBackpackConfig.getConfig().client.overlay.offsetY;

        int textureX = 10;
        int textureY = 0;

        ITravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);

        if(!inv.getRightTank().getResource().isBlank())
        {
            drawGuiTank(context, inv.getRightTank(), scaledWidth + 1, scaledHeight, 21, 8);
        }

        if(!inv.getLeftTank().getResource().isBlank())
        {
            drawGuiTank(context, inv.getLeftTank(), scaledWidth - 11, scaledHeight, 21, 8);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if(inv.getTier() != null)
        {
            KeyBinding key = KeybindHandler.SWITCH_TOOL;
            List<ItemStack> tools = getTools(inv.getToolSlotsInventory());

            if(key.isPressed() && tools.size() > 2)
            {
                if(animationProgress < 1.0F)
                {
                    animationProgress += 0.05F;
                }
                for(int i = 0; i < getTools(inv.getToolSlotsInventory()).size(); i++)
                {
                    drawItemStack(client, context, getTools(inv.getToolSlotsInventory()).get(i), scaledWidth - 30, (int)(scaledHeight + 11 - (animationProgress * (i * 15))));
                }
            }
            else if(!tools.isEmpty())
            {
                if(animationProgress > 0.0F)
                {
                    for(int i = 0; i < getTools(inv.getToolSlotsInventory()).size(); i++)
                    {
                        drawItemStack(client, context, getTools(inv.getToolSlotsInventory()).get(i), scaledWidth - 30, (int)(scaledHeight + 11 - (animationProgress * (i * 15))));
                    }
                    animationProgress -= 0.05F;
                }
                else
                {
                    if(!inv.getToolSlotsInventory().getStack(0).isEmpty())
                    {
                        drawItemStack(client, context, inv.getToolSlotsInventory().getStack(0), scaledWidth - 30, scaledHeight - 4);
                    }
                    if(tools.size() > 1)
                    {
                        if(!inv.getToolSlotsInventory().getStack(tools.size() - 1).isEmpty())
                        {
                            drawItemStack(client, context, inv.getToolSlotsInventory().getStack(tools.size() - 1), scaledWidth - 30, scaledHeight + 11);
                        }
                    }
                }
            }
        }

        if(player != null && player.getMainHandStack().getItem() instanceof HoseItem)
        {
            int tank = HoseItem.getHoseTank(player.getMainHandStack());

            int selectedTextureX = 0;
            int selectedTextureY = 0;

            if(tank == 1)
            {
                context.drawTexture(OVERLAY, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                context.drawTexture(OVERLAY, scaledWidth - 12, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
            }

            if(tank == 2)
            {
                context.drawTexture(OVERLAY, scaledWidth, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
                context.drawTexture(OVERLAY, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }

            if(tank == 0)
            {
                context.drawTexture(OVERLAY, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                context.drawTexture(OVERLAY, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }
        }
        else
        {
            context.drawTexture(OVERLAY, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
            context.drawTexture(OVERLAY, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
        }
    }

    public static void drawGuiTank(DrawContext drawContext, SingleVariantStorage<FluidVariant> fluidStorage, int startX, int startY, int height, int width)
    {
        RenderUtils.renderScreenTank(drawContext, fluidStorage, startX, startY, 0, height, width);
    }

    public static void drawItemStack(MinecraftClient client, DrawContext context, ItemStack stack, int x, int y)
    {
        //Item
        context.drawItem(stack, x, y);

        //Extras
        if(!stack.isEmpty())
        {
            context.getMatrices().push();

            int k;
            int l;

            if(stack.isItemBarVisible())
            {
                int i = stack.getItemBarStep();
                int j = stack.getItemBarColor();
                k = x + 2;
                l = y + 13;
                context.fill(RenderLayer.getGuiOverlay(), k, l, k + 13, l + 2, -16777216);
                context.fill(RenderLayer.getGuiOverlay(), k, l, k + i, l + 1, j | -16777216);
            }

            if(client != null)
            {
                ClientPlayerEntity clientPlayerEntity = client.player;

                float f = clientPlayerEntity == null ? 0.0F : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), client.getTickDelta());

                if(f > 0.0F)
                {
                    k = y + MathHelper.floor(16.0F * (1.0F - f));
                    l = k + MathHelper.ceil(16.0F * f);
                    context.fill(RenderLayer.getGuiOverlay(), x, k, x + 16, l, Integer.MAX_VALUE);
                }
            }
            context.getMatrices().pop();
        }
    }

    public static List<ItemStack> getTools(InventoryImproved inventory)
    {
        List<ItemStack> tools = new ArrayList<>();

        for(int i = 0; i < inventory.size(); i++)
        {
            if(!inventory.getStack(i).isEmpty())
            {
                tools.add(inventory.getStack(i));
            }
        }
        Collections.reverse(tools);
        return tools;
    }
}