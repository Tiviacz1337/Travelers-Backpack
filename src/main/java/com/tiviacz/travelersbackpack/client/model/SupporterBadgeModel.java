package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.List;

public class SupporterBadgeModel extends BackpackModelPart {
    private static final RandomSource RANDOM = RandomSource.create(42L);

    public SupporterBadgeModel() {
    }

    public void render(PoseStack poseStack, int packedLightIn) {
        poseStack.pushPose();
        translateAndRotate(poseStack);
        BakedModel starModel = Minecraft.getInstance().getModelManager().getModel(ModClientEventHandler.STAR_MODEL);

        //Y - Front/Back
        //X - Left/Right
        //Z - Up/Down
        poseStack.translate(0.05, 0.23, 0.405);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.scale(0.3F, 0.3F, 0.3F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-10.0F));

        renderModel(poseStack, starModel, packedLightIn);
        poseStack.popPose();
    }

    //Forge

    private void renderModel(PoseStack matrixStack, BakedModel model, int packedLightIn) {
        MultiBufferSource.BufferSource src = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer worldrenderer = src.getBuffer(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
        List<BakedQuad> quads = model.getQuads(null, null, RANDOM, ModelData.EMPTY, null);
        for(BakedQuad quad : quads) {
            worldrenderer.putBulkData(matrixStack.last(), quad, 1.0f, 1.0f, 1.0f, 1.0f, packedLightIn, OverlayTexture.NO_OVERLAY, true);
        }
        src.endBatch();
    }
}