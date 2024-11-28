package com.tiviacz.travelersbackpack.client.renderer;

import com.tiviacz.travelersbackpack.inventory.FluidTank;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.LogHelper;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class FluidPart extends ModelPart
{
    private final FluidTank leftTank = createFluidTank(1);
    private final FluidTank rightTank = createFluidTank(1);

    private VertexConsumerProvider vertices;

    public FluidPart(ModelPart parent)
    {
        super(parent.cuboids, parent.children);
    }

    public void prepare(ItemStack stack, VertexConsumerProvider vertices)
    {
        if (stack.hasNbt()) {
            if(stack.getNbt().contains(ITravelersBackpackInventory.LEFT_TANK)) {
                this.leftTank.readNbt(stack.getNbt().getCompound(ITravelersBackpackInventory.LEFT_TANK));
            } else {
                if (!this.leftTank.isResourceBlank()) {
                    this.leftTank.variant = FluidVariant.blank();
                    this.leftTank.amount = 0;
                }
            }
            if(stack.getNbt().contains(ITravelersBackpackInventory.RIGHT_TANK)) {
                this.rightTank.readNbt(stack.getNbt().getCompound(ITravelersBackpackInventory.RIGHT_TANK));
            } else {
                if (!this.rightTank.isResourceBlank()) {
                    this.rightTank.variant = FluidVariant.blank();
                    this.rightTank.amount = 0;
                }
            }
        }
        this.vertices = vertices;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay)
    {
        if(this.vertices == null)
        {
            LogHelper.error("Rendering error! Trying to render FluidPart without passing vertices!");
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

        //Swap places!!
        RenderUtils.renderFluidInTank(this.leftTank, matrices, vertices, light, -0.66F, -0.55F, -0.235F);
        RenderUtils.renderFluidInTank(this.rightTank, matrices, vertices, light,0.24F, -0.55F, -0.235F);

        matrices.pop();
    }

    public FluidTank createFluidTank(long tankCapacity)
    {
        return new FluidTank(tankCapacity)
        {
            @Override
            public FluidTank readNbt(NbtCompound nbt)
            {
                setCapacity(nbt.contains("capacity") ? nbt.getLong("capacity") : 1000);
                this.variant = FluidVariantImpl.fromNbt(nbt.getCompound("variant"));
                this.amount = nbt.getLong("amount");
                return this;
            }
        };
    }
}