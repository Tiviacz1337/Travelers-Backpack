package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.LogHelper;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FluidModelPart extends ModelPart
{
    private final FluidTank leftTank = createFluidHandler(1);
    private final FluidTank rightTank = createFluidHandler(1);

    private MultiBufferSource buffer;

    public FluidModelPart(ModelPart parent)
    {
        super(parent.cubes, parent.children);
    }

    public void prepare(ItemStack stack, MultiBufferSource buffer)
    {
        if (stack.hasTag()) {
            if(stack.getTag().contains(ITravelersBackpackContainer.LEFT_TANK)) {
                this.leftTank.readFromNBT(stack.getTag().getCompound(ITravelersBackpackContainer.LEFT_TANK));
            } else {
                if (!this.leftTank.isEmpty()) {
                    this.leftTank.setFluid(FluidStack.EMPTY);
                }
            }
            if(stack.getTag().contains(ITravelersBackpackContainer.RIGHT_TANK)) {
                this.rightTank.readFromNBT(stack.getTag().getCompound(ITravelersBackpackContainer.RIGHT_TANK));
            } else {
                if (!this.rightTank.isEmpty()) {
                    this.rightTank.setFluid(FluidStack.EMPTY);
                }
            }
        }
        this.buffer = buffer;
    }

    @Override
    public void render(PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay) {
        if(this.buffer == null)
        {
            LogHelper.error("Rendering error! Trying to render FluidPart without passing buffer!");
            return;
        }

        pPoseStack.pushPose();
        this.translateAndRotate(pPoseStack);
        render(pPoseStack, this.buffer, pPackedLight);
        pPoseStack.popPose();
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int combinedLight)
    {
        poseStack.pushPose();
        poseStack.scale(1F, 1.05F, 1F);

        RenderUtils.renderFluidInTank(null, this.leftTank, poseStack, buffer, combinedLight, -0.66F, -0.55F, -0.235F);
        RenderUtils.renderFluidInTank(null, this.rightTank, poseStack, buffer, combinedLight,0.24F, -0.55F, -0.235F);

        poseStack.popPose();
    }

    private FluidTank createFluidHandler(int capacity)
    {
        return new FluidTank(capacity)
        {
            @Override
            public FluidTank readFromNBT(CompoundTag nbt)
            {
                setCapacity(nbt.contains("Capacity", 3) ? nbt.getInt("Capacity") : 1000);
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
                setFluid(fluid);
                return this;
            }
        };
    }
}