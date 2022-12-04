package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class FluidPart extends ModelPart
{
    private final PlayerEntity player;
    private final VertexConsumerProvider vertices;

    public FluidPart(Model model, PlayerEntity player, VertexConsumerProvider vertices)
    {
        super(model);
        this.player = player;
        this.vertices = vertices;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay)
    {
        matrices.push();
        this.rotate(matrices);
        render(this.player, matrices, this.vertices, light);
        matrices.pop();
    }

    public void render(PlayerEntity player, MatrixStack matrices, VertexConsumerProvider vertices, int light)
    {
        RenderSystem.enableRescaleNormal();
        matrices.push();
        matrices.scale(1F, 1.05F, 1F);

        ITravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);

        RenderUtils.renderFluidInTank(inv.getRightTank(), matrices, vertices, light,0.24F, -0.55F, -0.235F);
        RenderUtils.renderFluidInTank(inv.getLeftTank(), matrices, vertices, light, -0.66F, -0.55F, -0.235F);

        matrices.pop();
        RenderSystem.disableRescaleNormal();
    }
}