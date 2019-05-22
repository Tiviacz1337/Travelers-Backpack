package com.tiviacz.travellersbackpack.items;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.init.ModItems;
import com.tiviacz.travellersbackpack.util.IHasModel;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSleepingBag extends ItemBlock implements IHasModel
{
	public ItemSleepingBag(Block block)
    {
        super(block);
        
		setRegistryName(block.getRegistryName());
		
		ModItems.ITEMS.add(this);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
/*        if(worldIn.isRemote)
        {
        	return EnumActionResult.SUCCESS;
        }

        IBlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();
        
        if(!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(facing);
        }

        EnumFacing playerFacing = player.getHorizontalFacing();
        BlockPos otherPos = pos.offset(playerFacing);
        ItemStack stack = player.getHeldItem(hand);
        
        if(worldIn.isAirBlock(otherPos))
        {
            if(player.canPlayerEdit(pos, facing, stack) && player.canPlayerEdit(otherPos, facing, stack))
            {
                if(worldIn.getBlockState(pos.down()).isTopSolid() && worldIn.getBlockState(otherPos.down()).isTopSolid())
                {
                    worldIn.setBlockState(pos, ModBlocks.SLEEPING_BAG_BOTTOM.getDefaultState().withProperty(BlockSleepingBag.FACING, playerFacing));
                    worldIn.setBlockState(otherPos, ModBlocks.SLEEPING_BAG_TOP.getDefaultState().withProperty(BlockSleepingBag.FACING, playerFacing));

                    worldIn.notifyNeighborsRespectDebug(pos, ModBlocks.SLEEPING_BAG_BOTTOM, false);
                    worldIn.notifyNeighborsRespectDebug(otherPos, ModBlocks.SLEEPING_BAG_TOP, false);

                    if(player instanceof EntityPlayerMP)
                    {
                        CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, stack);
                    }

                    stack.shrink(1);
                    return EnumActionResult.SUCCESS;
                }
            }
        } */
        return EnumActionResult.FAIL;
    } 

	@Override
	public void registerModels() 
	{
		TravellersBackpack.proxy.registerItemRenderer(this, 0, "inventory");
	}
}