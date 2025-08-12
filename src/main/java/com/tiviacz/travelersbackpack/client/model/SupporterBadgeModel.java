package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tiviacz.travelersbackpack.TravelersBackpackClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;

import java.util.List;

public class SupporterBadgeModel extends BackpackModelPart {
    public void render(PoseStack poseStack, int packedLightIn) {
        poseStack.pushPose();
        translateAndRotate(poseStack);
        BakedModel starModel = Minecraft.getInstance().getModelManager().getModel(TravelersBackpackClient.STAR_MODEL);

        //Y - Front/Back
        //X - Left/Right
        //Z - Up/Down
        poseStack.translate(0.05, 0.23, 0.405);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.scale(0.3F, 0.3F, 0.3F);
        // poseStack.translate(0, -0.18, 0.35);
        //poseStack.translate(0.15, 0.3, -0.2);
        poseStack.mulPose(Axis.YP.rotationDegrees(-10.0F));

        renderModel(poseStack, starModel, packedLightIn);
        poseStack.popPose();
    }

    //Fabric

    private void renderModel(PoseStack matrixStack, BakedModel model, int packedLightIn) {
        MultiBufferSource.BufferSource src = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer worldrenderer = src.getBuffer(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS)); //0x00F000F0
        List<BakedQuad> quads = model.getQuads(null, null, RANDOM);
        for(BakedQuad quad : quads) {
            worldrenderer.putBulkData(matrixStack.last(), quad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, 1.0f, 1.0f, 1.0f, new int[]{packedLightIn, packedLightIn, packedLightIn, packedLightIn}, OverlayTexture.NO_OVERLAY, true);
        }
        src.endBatch();
    }
}