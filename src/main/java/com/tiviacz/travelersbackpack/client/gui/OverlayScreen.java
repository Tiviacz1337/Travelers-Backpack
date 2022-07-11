package com.tiviacz.travelersbackpack.client.gui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class OverlayScreen
{
    public static void renderOverlay(ForgeIngameGui gui, Minecraft mc, PoseStack matrixStack)
    {
        Player player = mc.player;
        Window mainWindow = mc.getWindow();

        //RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        //RenderSystem.disableLighting();
        //RenderSystem.enableAlphaTest();
        //RenderSystem.disableBlend();

        int offsetX = TravelersBackpackConfig.CLIENT.overlay.offsetX.get();
        int offsetY = TravelersBackpackConfig.CLIENT.overlay.offsetY.get();
        int scaledWidth = mainWindow.getGuiScaledWidth() - offsetX;
        int scaledHeight = mainWindow.getGuiScaledHeight() - offsetY;

        int textureX = 10;
        int textureY = 0;

        ITravelersBackpackContainer inv = BackpackUtils.getCurrentContainer(player);
        FluidTank rightTank = inv.getRightTank();
        FluidTank leftTank = inv.getLeftTank();

        if(!rightTank.getFluid().isEmpty())
        {
            drawGuiTank(matrixStack, rightTank, scaledWidth + 1, scaledHeight, 21, 8);
        }

        if(!leftTank.getFluid().isEmpty())
        {
            drawGuiTank(matrixStack, leftTank, scaledWidth - 11, scaledHeight, 21, 8);
        }

        if(!inv.getHandler().getStackInSlot(Reference.TOOL_UPPER).isEmpty())
        {
            drawItemStack(mc.getItemRenderer(), inv.getHandler().getStackInSlot(Reference.TOOL_UPPER), scaledWidth - 30, scaledHeight - 4);
        }

        if(!inv.getHandler().getStackInSlot(Reference.TOOL_LOWER).isEmpty())
        {
            drawItemStack(mc.getItemRenderer(), inv.getHandler().getStackInSlot(Reference.TOOL_LOWER), scaledWidth - 30, scaledHeight + 9);
        }

        ResourceLocation texture = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack_overlay.png");

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);

        if(player.getMainHandItem().getItem() instanceof HoseItem)
        {
            int tank = HoseItem.getHoseTank(player.getMainHandItem());

            int selectedTextureX = 0;
            int selectedTextureY = 0;

            if(tank == 1)
            {
                gui.blit(matrixStack, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                gui.blit(matrixStack, scaledWidth - 12, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
            }

            if(tank == 2)
            {
                gui.blit(matrixStack, scaledWidth, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
                gui.blit(matrixStack, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }

            if(tank == 0)
            {
                gui.blit(matrixStack, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                gui.blit(matrixStack, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }
        }
        else
        {
            gui.blit(matrixStack, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
            gui.blit(matrixStack, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
        }
    }

    public static void drawGuiTank(PoseStack matrixStackIn, FluidTank tank, int startX, int startY, int height, int width)
    {
        RenderUtils.renderScreenTank(matrixStackIn, tank, startX, startY, height, width);
    }

    //I don't undestand rendering itemstack into gui at all, if I'm missing something crucial PR is appreciated
    private static void drawItemStack(ItemRenderer itemRenderer, ItemStack stack, int x, int y)
    {
        //RenderHelper.enableStandardItemLighting();
        itemRenderer.renderGuiItem(stack, x, y);
        //RenderHelper.disableStandardItemLighting();
    }
}
