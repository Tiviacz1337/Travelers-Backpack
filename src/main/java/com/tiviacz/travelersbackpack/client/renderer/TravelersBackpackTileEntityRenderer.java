package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.client.model.TravelersBackpackBlockModel;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

public class TravelersBackpackTileEntityRenderer extends TileEntityRenderer<TravelersBackpackTileEntity>
{
    public static final TravelersBackpackBlockModel model = new TravelersBackpackBlockModel();

    public TravelersBackpackTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
    {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TravelersBackpackTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        TravelersBackpackTileEntityRenderer.render(tileEntityIn, tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }

    public static void render(ITravelersBackpackInventory inv, World world, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        boolean flag = world != null;
        boolean isTile = inv instanceof TravelersBackpackTileEntity;
        BlockState blockstate = flag && isTile ? ((TravelersBackpackTileEntity)inv).getBlockState() : ModBlocks.STANDARD_TRAVELERS_BACKPACK.get().defaultBlockState();

        if(blockstate.getBlock() instanceof TravelersBackpackBlock)
        {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5D, 0.5D, 0.5D);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180F));

            Direction direction;

            if(!flag || !isTile)
            {
                direction = Direction.SOUTH;
            }
            else
            {
                direction = ((TravelersBackpackTileEntity)inv).getBlockDirection((TravelersBackpackTileEntity)inv);
            }

            if(direction == Direction.NORTH)
            {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180F));
            }
            if(direction == Direction.EAST)
            {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270F));
            }
            if(direction == Direction.SOUTH)
            {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(0F));
            }
            if(direction == Direction.WEST)
            {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90F));
            }

            matrixStackIn.scale((float)14/18, (float)10/13, (float)7/9);
            matrixStackIn.translate(0.0D, 0.016D, 0.0D);
            model.render(inv, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);

            matrixStackIn.popPose();
        }
    }
}