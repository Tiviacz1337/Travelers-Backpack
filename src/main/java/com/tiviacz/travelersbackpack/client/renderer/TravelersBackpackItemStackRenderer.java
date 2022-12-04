package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackItemStackRenderer extends ItemStackTileEntityRenderer
{
    public TravelersBackpackItemStackRenderer() { }

    @Override
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        //TravelersBackpackTileEntityRenderer.render(new TravelersBackpackInventory(stack, Minecraft.getInstance().player, (byte)0), null, matrixStack, buffer, combinedLight, combinedOverlay);
        TravelersBackpackTileEntityRenderer.renderByItem(new RenderData(Minecraft.getInstance().player, stack, stack.hasTag()), matrixStack, buffer, combinedLight, combinedOverlay);
    }
}