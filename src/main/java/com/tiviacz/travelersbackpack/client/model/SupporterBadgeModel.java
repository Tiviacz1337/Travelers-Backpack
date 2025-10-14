package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.renderer.v1.render.FabricBlockModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.EmptyBlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import org.joml.Quaternionf;

public class SupporterBadgeModel {
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
        BlockStateModel starModel = StarModelReloadListener.INSTANCE.getStarModel();

        //Y - Front/Back
        //X - Left/Right
        //Z - Up/Down
        poseStack.translate(0.05, 0.23, 0.405);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.scale(0.3F, 0.3F, 0.3F);

        renderModel(poseStack, starModel, packedLightIn);
        poseStack.popPose();
    }

    //Fabric

    private void renderModel(PoseStack matrixStack, BlockStateModel model, int packedLightIn) {
        MultiBufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
       /* Renderer renderer = Renderer.get();
        var emitter = renderer.mutableMesh().emitter();
        model.emitQuads(emitter, EmptyBlockAndTintGetter.INSTANCE, BlockPos.ZERO, Blocks.AIR.defaultBlockState(), RandomSource.create(42L), (d) -> {
            return false;
        });*/
        FabricBlockModelRenderer.render(matrixStack.last(), (chunkSectionLayer) -> buffer.getBuffer(RenderType.solid()), model, 1, 1, 1, packedLightIn, OverlayTexture.NO_OVERLAY, EmptyBlockAndTintGetter.INSTANCE, BlockPos.ZERO, Blocks.AIR.defaultBlockState());
    }
}