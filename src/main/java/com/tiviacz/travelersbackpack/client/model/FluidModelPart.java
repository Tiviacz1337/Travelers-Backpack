package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.LogHelper;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;

public class FluidModelPart extends ModelPart
{
    private Player player;
    private MultiBufferSource buffer;

    public FluidModelPart(ModelPart parent)
    {
        super(parent.cubes, parent.children);
    }

    public void prepare(Player player, MultiBufferSource buffer)
    {
        this.player = player;
        this.buffer = buffer;
    }

    @Override
    public void render(PoseStack poseStack, VertexConsumer pBuffer, int light, int overlay)
    {
        if(this.buffer == null || this.player == null)
        {
            LogHelper.error("Rendering error! Trying to render FluidModelPart without passing player or buffer!");
            return;
        }

        poseStack.pushPose();
        this.translateAndRotate(poseStack);
        render(this.player, poseStack, this.buffer, light);
        poseStack.popPose();
    }

    public void render(Player player, PoseStack poseStack, MultiBufferSource buffer, int light)
    {
        poseStack.pushPose();
        poseStack.scale(1F, 1.05F, 1F);

        ITravelersBackpackContainer container = AttachmentUtils.getBackpackInv(player);

        RenderUtils.renderFluidInTank(container, container.getRightTank(), poseStack, buffer, light,0.24F, -0.55F, -0.235F);
        RenderUtils.renderFluidInTank(container, container.getLeftTank(), poseStack, buffer, light, -0.66F, -0.55F, -0.235F);

        poseStack.popPose();
    }
}