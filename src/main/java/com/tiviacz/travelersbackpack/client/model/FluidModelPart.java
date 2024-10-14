package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.components.FluidTanks;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FluidModelPart extends ModelPart
{
    private final FluidTank leftTank = new FluidTank(3000); //Will be changed anyway later
    private final FluidTank rightTank = new FluidTank(3000);

    private MultiBufferSource buffer;

    public FluidModelPart(ModelPart parent)
    {
        super(parent.cubes, parent.children);
    }

    public void prepare(ItemStack stack, MultiBufferSource buffer)
    {
        if(stack.has(ModDataComponents.FLUID_TANKS.get()))
        {
            FluidTanks tanks = stack.get(ModDataComponents.FLUID_TANKS.get());
            this.leftTank.setCapacity(tanks.capacity());
            this.leftTank.setFluid(tanks.leftFluidStack());

            this.rightTank.setCapacity(tanks.capacity());
            this.rightTank.setFluid(tanks.rightFluidStack());
        } else {
            if(!this.leftTank.isEmpty()) {
                this.leftTank.setFluid(FluidStack.EMPTY);
            }
            if(!this.rightTank.isEmpty()) {
                this.rightTank.setFluid(FluidStack.EMPTY);
            }
        }
        this.buffer = buffer;
    }

    @Override
    public void render(PoseStack poseStack, VertexConsumer pBuffer, int light, int overlay)
    {
        if(this.buffer == null)
        {
            //LogHelper.error("Rendering error! Trying to render FluidModelPart without passing player or buffer!");
            return;
        }

        poseStack.pushPose();
        this.translateAndRotate(poseStack);
        render(poseStack, this.buffer, light);
        poseStack.popPose();
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int light)
    {
        poseStack.pushPose();
        poseStack.scale(1F, 1.05F, 1F);

        RenderUtils.renderFluidInTank(null, this.leftTank, poseStack, buffer, light, -0.66F, -0.55F, -0.235F);
        RenderUtils.renderFluidInTank(null, this.rightTank, poseStack, buffer, light,0.24F, -0.55F, -0.235F);

        poseStack.popPose();
    }
}