package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BackpackModel extends BackpackModelPart {
    public final SupporterBadgeModel supporterBadgeModel;
    private final ItemModelResolver resolver;

    public BackpackModel() {
        this.supporterBadgeModel = new SupporterBadgeModel();
        this.resolver = Minecraft.getInstance().getItemModelResolver();
    }

    public void render(PoseStack poseStack, int packedLightIn, SubmitNodeCollector collector, ItemStackRenderState backpackRenderState, StackModelPart tools, ItemStack stack) {
        poseStack.pushPose();
        translateAndRotate(poseStack);

        //Y - Front/Back
        //X - Left/Right
        //Z - Up/Down
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.translate(0, -0.145, 0.35);

        if(!stack.isEmpty()) {
            resolver.updateForTopItem(backpackRenderState, stack, ItemDisplayContext.NONE, null, null, 0);
            backpackRenderState.submit(poseStack, collector, packedLightIn, OverlayTexture.NO_OVERLAY, 0);
        }

        //ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        //itemRenderer.renderStatic(stack, ItemDisplayContext.NONE, packedLightIn, OverlayTexture.NO_OVERLAY, poseStack, bufferIn, Minecraft.getInstance().level, 0);

        //Render Tools
        tools.render(stack, collector, poseStack, packedLightIn, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }
}