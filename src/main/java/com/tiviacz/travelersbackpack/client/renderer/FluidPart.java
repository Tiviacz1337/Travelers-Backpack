package com.tiviacz.travelersbackpack.client.renderer;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.LogHelper;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class FluidPart extends ModelPart
{
    private PlayerEntity player;
    private VertexConsumerProvider vertices;

    public FluidPart(ModelPart parent)
    {
        super(parent.cuboids, parent.children);
    }

    public void prepare(PlayerEntity player, VertexConsumerProvider provider)
    {
        this.player = player;
        this.vertices = provider;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay)
    {
        if(this.vertices == null || this.player == null)
        {
            LogHelper.error("Rendering error! Trying to render FluidPart without passing player or vertices!");
            return;
        }

        matrices.push();
        this.rotate(matrices);
        render(this.player, matrices, this.vertices, light);
        matrices.pop();
    }

    public void render(PlayerEntity player, MatrixStack matrices, VertexConsumerProvider vertices, int light)
    {
        matrices.push();
        matrices.scale(1F, 1.05F, 1F);

        ITravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);

        RenderUtils.renderFluidInTank(inv.getRightTank(), matrices, vertices, light,0.24F, -0.55F, -0.235F);
        RenderUtils.renderFluidInTank(inv.getLeftTank(), matrices, vertices, light, -0.66F, -0.55F, -0.235F);

        matrices.pop();
    }
}