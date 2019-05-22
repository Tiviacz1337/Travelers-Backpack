package com.tiviacz.travellersbackpack.items;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.init.ModBlocks;
import com.tiviacz.travellersbackpack.tileentity.TileEntityTravellersBackpack;
import com.tiviacz.travellersbackpack.util.Reference;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemTravellersBackpack extends ItemBase
{
	public ItemTravellersBackpack(String name)
	{
		super(name);
		setMaxStackSize(1);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
		ItemStack itemstack = playerIn.getHeldItemMainhand();
		
		if(!worldIn.isRemote)
        {
        	if(itemstack.getItem() == this && !playerIn.isSneaking())
        	{
        		playerIn.openGui(TravellersBackpack.INSTANCE, Reference.TRAVELLERS_BACKPACK_GUI_ID, worldIn, playerIn.getPosition().getX(), playerIn.getPosition().getY(), playerIn.getPosition().getZ());
        		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        	}
        }
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
    }
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		if(!player.isSneaking())
		{
			return EnumActionResult.FAIL;
		}
		
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if(!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(facing);
        }

        ItemStack itemstack = player.getHeldItem(hand);

        if(!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && worldIn.mayPlace(ModBlocks.TRAVELLERS_BACKPACK, pos, false, facing, player))
        {
            int i = this.getMetadata(itemstack.getMetadata());
            IBlockState iblockstate1 = ModBlocks.TRAVELLERS_BACKPACK.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, player, hand);

            if(placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1))
            {
            	if(itemstack.getTagCompound() != null)
            	{
            		((TileEntityTravellersBackpack)worldIn.getTileEntity(pos)).loadAllData(itemstack.getTagCompound());
            	}
                iblockstate1 = worldIn.getBlockState(pos);
                SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, worldIn, pos, player);
                worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }
	
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
        if(!world.setBlockState(pos, newState, 11)) 
        {
        	return false;
        }

        IBlockState state = world.getBlockState(pos);
        
        if(state.getBlock() == ModBlocks.TRAVELLERS_BACKPACK)
        {
            ItemBlock.setTileEntityNBT(world, player, pos, stack);
            ModBlocks.TRAVELLERS_BACKPACK.onBlockPlacedBy(world, pos, state, player, stack);

            if (player instanceof EntityPlayerMP)
            {
            	CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
            }
        }

        return true;
    }

	@Override
	public void registerModels() 
	{
		TravellersBackpack.proxy.registerItemRenderer(this, 0, "inventory");
	}
}