package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.handlers.ConfigHandler;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.tileentity.TileEntityTravelersBackpack;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemTravelersBackpack extends ItemBase
{
	public ItemTravelersBackpack(String name)
	{
		super(name);
		setMaxStackSize(1);
		setHasSubtypes(true);
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
		if(this.isInCreativeTab(tab))
		{
			for(int i = 0; i < Reference.BACKPACK_NAMES.length; i++)
	        {
	            ItemStack stack = new ItemStack(this, 1, i);
	            items.add(stack);
	        }
		}
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		int meta = stack.getMetadata();
		String name = Reference.BACKPACK_NAMES[meta];
		String localizedName = I18n.format("backpack." + name.toLowerCase() + ".name");
		
		if(ConfigHandler.client.obtainTips)
		{
			if(meta == 2)
			{
				tooltip.add(TextFormatting.BLUE + I18n.format("backpack.obtain.bat"));
			}
				
			if(meta == 11)
			{
				tooltip.add(TextFormatting.BLUE + I18n.format("backpack.obtain.iron_golem"));
			}
			
			if(meta == 24)
			{
				tooltip.add(TextFormatting.BLUE + I18n.format("backpack.obtain.electric"));
			}
			
			if(meta == 51)
			{
				tooltip.add(TextFormatting.BLUE + I18n.format("backpack.obtain.pigman"));
			}
			
			if(meta == 25)
			{
				tooltip.add(TextFormatting.BLUE + I18n.format("backpack.obtain.deluxe"));
			}
			
			if(meta == 71)
			{
				tooltip.add(TextFormatting.BLUE + I18n.format("backpack.obtain.villager"));
			}
		}

		tooltip.add(localizedName);
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		
		//if(!worldIn.isRemote)
        //{
			if(handIn == EnumHand.MAIN_HAND)
			{
				if(itemstack.getItem() == this && !playerIn.isSneaking())
	        	{
	        		playerIn.openGui(TravelersBackpack.INSTANCE, Reference.TRAVELERS_BACKPACK_ITEM_GUI_ID, worldIn, playerIn.getPosition().getX(), playerIn.getPosition().getY(), playerIn.getPosition().getZ());
	        		return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
	        	}
			}
        //}
		return new ActionResult<>(EnumActionResult.FAIL, itemstack);
    }
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		if(hand == EnumHand.MAIN_HAND && !player.isSneaking())
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

        if(!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && worldIn.mayPlace(ModBlocks.TRAVELERS_BACKPACK, pos, false, facing, player))
        {
            int i = this.getMetadata(itemstack.getMetadata());
            IBlockState iblockstate1 = ModBlocks.TRAVELERS_BACKPACK.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, player, hand);

            if(placeBlockAt(itemstack, player, worldIn, pos, iblockstate1))
            {
            	if(itemstack.getTagCompound() != null)
            	{
            		((TileEntityTravelersBackpack)worldIn.getTileEntity(pos)).loadAllData(itemstack.getTagCompound());
            	}
            	((TileEntityTravelersBackpack)worldIn.getTileEntity(pos)).setColorFromMeta(itemstack.getMetadata());
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
	
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, IBlockState newState)
    {
        if(!world.setBlockState(pos, newState, 11)) 
        {
        	return false;
        }

        IBlockState state = world.getBlockState(pos);
        
        if(state.getBlock() == ModBlocks.TRAVELERS_BACKPACK)
        {
            ItemBlock.setTileEntityNBT(world, player, pos, stack);
            ModBlocks.TRAVELERS_BACKPACK.onBlockPlacedBy(world, pos, state, player, stack);

            if(player instanceof EntityPlayerMP)
            {
            	CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
            }
        }

        return true;
    }
	
	@Override
	public void registerModels() 
	{
		for(int i = 0; i < Reference.BACKPACK_NAMES.length; i++)
		{
			TravelersBackpack.proxy.registerItemRenderer(this, i, "inventory");
		}
	} 
}