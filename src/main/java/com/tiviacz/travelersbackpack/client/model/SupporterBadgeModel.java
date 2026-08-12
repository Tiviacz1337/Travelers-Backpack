package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.util.RandomSource;
import org.joml.Quaternionf;

import java.util.List;

public class SupporterBadgeModel {
    private static final RandomSource RANDOM = RandomSource.create(42L);
    public float x = 0;
    public float y = 0;
    public float z = 0;
    public float xRot = 0;
    public float yRot = 0;
    public float zRot = 0;
    public float xScale = 1.0f;
    public float yScale = 1.0f;
    public float zScale = 1.0f;

    public SupporterBadgeModel() {
    }

    public void copyFrom(ModelPart parentModelPart) {
        this.xScale = parentModelPart.xScale;
        this.yScale = parentModelPart.yScale;
        this.zScale = parentModelPart.zScale;
        this.xRot = parentModelPart.xRot;
        this.yRot = parentModelPart.yRot;
        this.zRot = parentModelPart.zRot;
        this.x = parentModelPart.x;
        this.y = parentModelPart.y;
        this.z = parentModelPart.z;
    }

    private void translateAndRotate(PoseStack poseStack) {
        poseStack.translate(this.x / 16.0f, this.y / 16.0f, this.z / 16.0f);
        if(this.xRot != 0.0f || this.yRot != 0.0f || this.zRot != 0.0f) {
            poseStack.mulPose(new Quaternionf().rotationZYX(this.zRot, this.yRot, this.xRot));
        }
        if(this.xScale != 1.0f || this.yScale != 1.0f || this.zScale != 1.0f) {
            poseStack.scale(this.xScale, this.yScale, this.zScale);
        }
    }

    public void render(PoseStack poseStack, int packedLightIn) {
        poseStack.pushPose();
        translateAndRotate(poseStack);
        BlockModelPart starModel = Minecraft.getInstance().getModelManager().getStandaloneModel(ModClientEventHandler.STAR_MODEL);

        //Y - Front/Back
        //X - Left/Right
        //Z - Up/Down
        poseStack.translate(0.05, 0.23, 0.405);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.scale(0.3F, 0.3F, 0.3F);

        renderModel(poseStack, starModel, packedLightIn);
        poseStack.popPose();
    }

    //Forge

    private void renderModel(PoseStack matrixStack, BlockModelPart model, int packedLightIn) {
        MultiBufferSource.BufferSource src = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer worldrenderer = src.getBuffer(RenderTypes.entityCutout(TextureAtlas.LOCATION_BLOCKS));
        List<BakedQuad> quads = model.getQuads(null);
        for(BakedQuad quad : quads) {
            worldrenderer.putBulkData(matrixStack.last(), quad, 1.0f, 1.0f, 1.0f, 1.0f, packedLightIn, OverlayTexture.NO_OVERLAY);
        }
        src.endBatch();
    }
}