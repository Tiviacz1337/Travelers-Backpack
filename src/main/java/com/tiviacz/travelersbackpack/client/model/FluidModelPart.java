package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.util.RenderHelper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FluidModelPart extends ModelPart {
    private final FluidTank leftTank = new FluidTank(3000); //Will be changed anyway later
    private final FluidTank rightTank = new FluidTank(3000);
    private MultiBufferSource buffer;
    private boolean render;

    public FluidModelPart(ModelPart parent) {
        super(parent.cubes, parent.children);
    }

    public void prepare(ItemStack stack, MultiBufferSource buffer) {
        this.buffer = buffer;
        RenderInfo info = stack.getOrDefault(ModDataComponents.RENDER_INFO.get(), RenderInfo.EMPTY);
        if(info.isEmpty()) {
            this.render = false;
            if(!this.leftTank.isEmpty()) {
                this.leftTank.setFluid(FluidStack.EMPTY);
            }
            if(!this.rightTank.isEmpty()) {
                this.rightTank.setFluid(FluidStack.EMPTY);
            }
            return;
        }
        this.render = true;
        this.leftTank.setCapacity(info.getCapacity());
        this.leftTank.setFluid(info.getLeftFluidStack());
        this.rightTank.setCapacity(info.getCapacity());
        this.rightTank.setFluid(info.getRightFluidStack());
    }

    @Override
    public void render(PoseStack poseStack, VertexConsumer pBuffer, int light, int overlay) {
        if(this.buffer == null || !this.render) {
            //LogHelper.error("Rendering error! Trying to render FluidModelPart without passing player or buffer!");
            return;
        }
        poseStack.pushPose();
        this.translateAndRotate(poseStack);
        render(poseStack, this.buffer, light);
        poseStack.popPose();
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int light) {
        poseStack.pushPose();
        poseStack.scale(1F, 1.05F, 1F);
        RenderHelper.renderFluidInTank(this.leftTank, poseStack, buffer, light, -0.66F, -0.55F, -0.235F);
        RenderHelper.renderFluidInTank(this.rightTank, poseStack, buffer, light, 0.24F, -0.55F, -0.235F);
        poseStack.popPose();
    }
}