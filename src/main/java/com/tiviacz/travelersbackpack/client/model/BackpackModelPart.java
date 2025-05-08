package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Quaternionf;

public class BackpackModelPart {
    public float x = 0;
    public float y = 0;
    public float z = 0;
    public float xRot = 0;
    public float yRot = 0;
    public float zRot = 0;
    public float xScale = 1.0f;
    public float yScale = 1.0f;
    public float zScale = 1.0f;

    public BackpackModelPart() {
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

    protected void translateAndRotate(PoseStack poseStack) {
        poseStack.translate(this.x / 16.0f, this.y / 16.0f, this.z / 16.0f);
        if(this.xRot != 0.0f || this.yRot != 0.0f || this.zRot != 0.0f) {
            poseStack.mulPose(new Quaternionf().rotationZYX(this.zRot, this.yRot, this.xRot));
        }
        if(this.xScale != 1.0f || this.yScale != 1.0f || this.zScale != 1.0f) {
            poseStack.scale(this.xScale, this.yScale, this.zScale);
        }
    }
}