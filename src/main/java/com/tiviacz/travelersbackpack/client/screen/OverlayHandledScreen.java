package com.tiviacz.travelersbackpack.client.screen;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.Reference;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class OverlayHandledScreen extends Screen
{
    public MinecraftClient mc;
    public ItemRenderer itemRenderer;
    public Window mainWindow;

    public OverlayHandledScreen()
    {
        super(new LiteralText(""));

        this.mc = MinecraftClient.getInstance();
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        this.mainWindow = MinecraftClient.getInstance().getWindow();
    }

    public void renderOverlay(MatrixStack matrices)
    {
        PlayerEntity player = mc.player;

        //RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        //RenderSystem.disableLighting();
        //RenderSystem.enableAlphaTest();
        //RenderSystem.disableBlend();

        int offsetX = TravelersBackpackConfig.offsetX;
        int offsetY = TravelersBackpackConfig.offsetY;
        int scaledWidth = mainWindow.getScaledWidth() - offsetX;
        int scaledHeight = mainWindow.getScaledHeight() - offsetY;

        int textureX = 10;
        int textureY = 0;

        ITravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);
        SingleVariantStorage<FluidVariant> rightFluidStorage = inv.getRightTank();
        SingleVariantStorage<FluidVariant> leftFluidStorage = inv.getLeftTank();

        if(!rightFluidStorage.getResource().isBlank())
        {
            this.drawGuiTank(matrices, rightFluidStorage, scaledWidth + 1, scaledHeight, 21, 8);
        }

        if(!leftFluidStorage.getResource().isBlank())
        {
            this.drawGuiTank(matrices, leftFluidStorage, scaledWidth - 11, scaledHeight, 21, 8);
        }

        if(!inv.getInventory().getStack(Reference.TOOL_UPPER).isEmpty())
        {
            this.drawItemStack(inv.getInventory().getStack(Reference.TOOL_UPPER), scaledWidth - 30, scaledHeight - 4);
        }

        if(!inv.getInventory().getStack(Reference.TOOL_LOWER).isEmpty())
        {
            this.drawItemStack(inv.getInventory().getStack(Reference.TOOL_LOWER), scaledWidth - 30, scaledHeight + 9);
        }

        Identifier id = new Identifier(TravelersBackpack.MODID, "textures/gui/travelers_backpack_overlay.png");
        mc.getTextureManager().bindTexture(id);

        /*if(player.getMainHandStack().getItem() instanceof HoseItem)
        {
            int tank = HoseItem.getHoseTank(player.getHeldItemMainhand());

            int selectedTextureX = 0;
            int selectedTextureY = 0;

            if(tank == 1)
            {
                blit(matrixStack, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                blit(matrixStack, scaledWidth - 12, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
            }

            if(tank == 2)
            {
                blit(matrixStack, scaledWidth, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
                blit(matrixStack, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }

            if(tank == 0)
            {
                blit(matrixStack, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                blit(matrixStack, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }
        }
        else */
        {
            drawTexture(matrices, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
            drawTexture(matrices, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
        }
    }

    public void drawGuiTank(MatrixStack matrixStackIn, SingleVariantStorage<FluidVariant> fluidStorage, int startX, int startY, int height, int width)
    {
        RenderUtils.renderScreenTank(matrixStackIn, fluidStorage, startX, startY, height, width);
    }

   /* public void drawLeftTank(MatrixStack matrixStackIn, FluidTank tank, int startX, int startY, int height, int width)
    {
        RenderUtils.renderScreenTank(matrixStackIn, tank, startX, startY, height, width);
    } */

    //I don't undestand rendering itemstack into gui at all, if I'm missing something crucial PR is appreciated
    private void drawItemStack(ItemStack stack, int x, int y)
    {
        DiffuseLighting.enable();
        this.itemRenderer.renderGuiItemIcon(stack, x, y);
        DiffuseLighting.disable();
    }
}
