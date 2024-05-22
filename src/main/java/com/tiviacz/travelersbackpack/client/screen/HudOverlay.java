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
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HudOverlay
{
    private static final Identifier OVERLAY = new Identifier(TravelersBackpack.MODID, "textures/gui/travelers_backpack_overlay.png");
    private static float animationProgress = 0.0F;

    public static void render(MatrixStack matrices, float tickDelta)
    {
        if(!TravelersBackpackConfig.getConfig().client.overlay.enableOverlay || !ComponentUtils.isWearingBackpack(MinecraftClient.getInstance().player)) return;

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        int scaledWidth = client.getWindow().getScaledWidth() - TravelersBackpackConfig.getConfig().client.overlay.offsetX;
        int scaledHeight = client.getWindow().getScaledHeight() - TravelersBackpackConfig.getConfig().client.overlay.offsetY;

        int textureX = 10;
        int textureY = 0;

        ITravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);

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
                    drawItemStack(client, getTools(inv.getToolSlotsInventory()).get(i), scaledWidth - 30, (int)(scaledHeight + 11 - (animationProgress * (i * 15))));
                }
            }
            else if(!tools.isEmpty())
            {
                if(animationProgress > 0.0F)
                {
                    for(int i = 0; i < getTools(inv.getToolSlotsInventory()).size(); i++)
                    {
                        drawItemStack(client, getTools(inv.getToolSlotsInventory()).get(i), scaledWidth - 30, (int)(scaledHeight + 11 - (animationProgress * (i * 15))));
                    }
                    animationProgress -= 0.05F;
                }
                else
                {
                    if(!inv.getToolSlotsInventory().getStack(0).isEmpty())
                    {
                        drawItemStack(client, inv.getToolSlotsInventory().getStack(0), scaledWidth - 30, scaledHeight - 4);
                    }
                    if(tools.size() > 1)
                    {
                        if(!inv.getToolSlotsInventory().getStack(tools.size() - 1).isEmpty())
                        {
                            drawItemStack(client, inv.getToolSlotsInventory().getStack(tools.size() - 1), scaledWidth - 30, scaledHeight + 11);
                        }
                    }
                }
            }
        }

        if(!inv.getRightTank().getResource().isBlank())
        {
            drawGuiTank(matrices, inv.getRightTank(), scaledWidth + 1, scaledHeight, 21, 8);
        }

        if(!inv.getLeftTank().getResource().isBlank())
        {
            drawGuiTank(matrices, inv.getLeftTank(), scaledWidth - 11, scaledHeight, 21, 8);
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, OVERLAY);

        if(player != null && player.getMainHandStack().getItem() instanceof HoseItem)
        {
            int tank = HoseItem.getHoseTank(player.getMainHandStack());

            int selectedTextureX = 0;
            int selectedTextureY = 0;

            if(tank == 1)
            {
                drawTexture(matrices, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                drawTexture(matrices, scaledWidth - 12, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
            }

            if(tank == 2)
            {
                drawTexture(matrices, scaledWidth, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
                drawTexture(matrices, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }

            if(tank == 0)
            {
                drawTexture(matrices, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                drawTexture(matrices, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }
        }
        else
        {
            drawTexture(matrices, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
            drawTexture(matrices, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
        }
    }

    public static void drawTexture(MatrixStack matrices, int width, int height, int x, int y, int u, int v)
    {
        DrawableHelper.drawTexture(matrices, width, height, x, y, u, v, 256, 256);
    }

    public static void drawGuiTank(MatrixStack matrixStackIn, SingleVariantStorage<FluidVariant> fluidStorage, int startX, int startY, int height, int width)
    {
        RenderUtils.renderScreenTank(matrixStackIn, fluidStorage, startX, startY, 0, height, width);
    }

    private static void drawItemStack(MinecraftClient client, ItemStack stack, int x, int y)
    {
        client.getItemRenderer().renderGuiItemIcon(stack, x, y);
        client.getItemRenderer().renderGuiItemOverlay(client.textRenderer, stack, x, y);
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