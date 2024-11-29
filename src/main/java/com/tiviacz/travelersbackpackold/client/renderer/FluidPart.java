package com.tiviacz.travelersbackpackold.client.renderer;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpackold.components.FluidTanks;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpackold.inventory.FluidTank;
import com.tiviacz.travelersbackpackold.util.RenderUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class FluidPart extends ModelPart
{
    private Pair<FluidVariant, Long> leftTank = Pair.of(FluidVariant.blank(), (long)0);
    private Pair<FluidVariant, Long> rightTank = Pair.of(FluidVariant.blank(), (long)0);
    private long capacity = 0;

    private VertexConsumerProvider vertices;

    public FluidPart(ModelPart parent)
    {
        super(parent.cuboids, parent.children);
    }

    public void prepare(ItemStack stack, VertexConsumerProvider vertices)
    {
        if(stack.contains(ModDataComponents.FLUID_TANKS))
        {
            FluidTanks tanks = stack.get(ModDataComponents.FLUID_TANKS);

            this.capacity = tanks.capacity();
            this.leftTank = Pair.of(tanks.leftTank().fluidVariant(), tanks.leftTank().amount());
            this.rightTank = Pair.of(tanks.rightTank().fluidVariant(), tanks.rightTank().amount());
        } else {
            if (!this.leftTank.getFirst().isBlank()) {
                this.leftTank = Pair.of(FluidVariant.blank(), (long)0);
            }
            if (!this.rightTank.getFirst().isBlank()) {
                this.rightTank = Pair.of(FluidVariant.blank(), (long)0);
            }
        }
        this.vertices = vertices;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay)
    {
        if(this.vertices == null)
        {
            //LogHelper.error("Rendering error! Trying to render FluidPart without passing player or vertices!");
            return;
        }

        matrices.push();
        this.rotate(matrices);
        render(matrices, this.vertices, light);
        matrices.pop();
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light)
    {
        matrices.push();
        matrices.scale(1F, 1.05F, 1F);

        RenderUtils.renderFluidInTank(new FluidTank(this.capacity, this.leftTank.getFirst(), this.leftTank.getSecond()), matrices, vertices, light, -0.66F, -0.55F, -0.235F);
        RenderUtils.renderFluidInTank(new FluidTank(this.capacity, this.rightTank.getFirst(), this.rightTank.getSecond()), matrices, vertices, light,0.24F, -0.55F, -0.235F);

        matrices.pop();
    }

}