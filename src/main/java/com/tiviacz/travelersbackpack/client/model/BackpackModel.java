package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BackpackModel extends BackpackModelPart {
    public final SupporterBadgeModel supporterBadgeModel;
    public final StackModelPart tools;

    public BackpackModel() {
        this.supporterBadgeModel = new SupporterBadgeModel();
        this.tools = new StackModelPart();
    }

    public void render(PoseStack poseStack, int packedLightIn, MultiBufferSource bufferIn, ItemStack stack) {
        poseStack.pushPose();
        translateAndRotate(poseStack);

        //Y - Front/Back
        //X - Left/Right
        //Z - Up/Down
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.translate(0, -0.145, 0.35);
        poseStack.scale(1.03F, 1.03F, 1.03F);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderStatic(stack, ItemDisplayContext.NONE, packedLightIn, OverlayTexture.NO_OVERLAY, poseStack, bufferIn, Minecraft.getInstance().level, 0);

        //Render Tools
        this.tools.render(stack, bufferIn, poseStack, packedLightIn, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }
}