package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;

public class FluidModelPart extends ModelPart
{
    public FluidModelPart(ModelPart parent)
    {
        super(parent.cubes, parent.children);
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, Player player, MultiBufferSource buffer, int combinedLight, int combinedOverlay, float r, float g, float b, float a)
    {
        poseStack.pushPose();
        this.translateAndRotate(poseStack);

        poseStack.pushPose();
        poseStack.scale(1F, 1.05F, 1F);

        ITravelersBackpackContainer container = CapabilityUtils.getBackpackInv(player);

        RenderUtils.renderFluidInTank(container, container.getRightTank(), poseStack, buffer, combinedLight,0.24F, -0.55F, -0.235F);
        RenderUtils.renderFluidInTank(container, container.getLeftTank(), poseStack, buffer, combinedLight, -0.66F, -0.55F, -0.235F);

        poseStack.popPose();

        poseStack.popPose();
    }
}