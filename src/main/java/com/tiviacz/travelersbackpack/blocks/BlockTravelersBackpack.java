package com.tiviacz.travelersbackpack.blocks;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.handlers.ConfigHandler;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.tileentity.TileEntityTravelersBackpack;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Bounds;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockTravelersBackpack extends BlockContainer
{
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final AxisAlignedBB BACKPACK_NORTH_AABB = new Bounds(1, 0, 4, 15, 10, 12).toAABB();
	public static final AxisAlignedBB BACKPACK_SOUTH_AABB = new Bounds(1, 0, 4, 15, 10, 12).toAABB();
	public static final AxisAlignedBB BACKPACK_EAST_AABB = new Bounds(4, 0, 1, 12, 10, 15).toAABB();
	public static final AxisAlignedBB BACKPACK_WEST_AABB = new Bounds(4, 0, 1, 12, 10, 15).toAABB();
	
	public BlockTravelersBackpack(String name, Material materialIn)
	{
		super(materialIn);
		
		setRegistryName(name);
		setUnlocalizedName(name);
		setSoundType(SoundType.CLOTH);
		setHardness(1.0F);
		setResistance(100000.0F);
		setHarvestLevel("hand", 0);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH)); 
		
		ModBlocks.BLOCKS.add(this);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(worldIn.getTileEntity(pos) instanceof TileEntityTravelersBackpack)
		{
			if(!worldIn.isRemote)
			{
				if(ConfigHandler.server.enableBackpackBlockWearable)
				{
					if(playerIn.isSneaking())
					{
						TileEntityTravelersBackpack te = (TileEntityTravelersBackpack)worldIn.getTileEntity(pos);
						
						if(!CapabilityUtils.isWearingBackpack(playerIn))
						{
							if(worldIn.setBlockToAir(pos))
							{
								ItemStack stack = new ItemStack(ModItems.TRAVELERS_BACKPACK, 1, BackpackUtils.convertNameToMeta(te.getColor()));
						        te.transferToItemStack(stack);
						        CapabilityUtils.equipBackpack(playerIn, stack);
						        
						        if(te.isSleepingBagDeployed())
					        	{
					        		EnumFacing bagFacing = state.getValue(FACING);
					        		worldIn.setBlockToAir(pos.offset(bagFacing));
					        		worldIn.setBlockToAir(pos.offset(bagFacing).offset(bagFacing));
					        	}
							}
							else
							{
								playerIn.sendMessage(new TextComponentTranslation("actions.equip_backpack.fail"));
							}
						}
						else
						{
							playerIn.sendMessage(new TextComponentTranslation("actions.equip_backpack.otherbackpack"));
						}
					}
					else
					{
						playerIn.openGui(TravelersBackpack.INSTANCE, Reference.TRAVELERS_BACKPACK_TILE_GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
					}
				}
				else
				{
					playerIn.openGui(TravelersBackpack.INSTANCE, Reference.TRAVELERS_BACKPACK_TILE_GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
		TileEntity tile = world.getTileEntity(pos);

        if(tile instanceof TileEntityTravelersBackpack && !world.isRemote && player != null)
        {
        	TileEntityTravelersBackpack te = (TileEntityTravelersBackpack)tile;
        	te.drop(world, player, pos.getX(), pos.getY(), pos.getZ());
        	world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
        	
        	if(te.isSleepingBagDeployed())
        	{
        		EnumFacing facing = state.getValue(FACING);
        		world.setBlockToAir(pos.offset(facing));
        		world.setBlockToAir(pos.offset(facing).offset(facing));
        	}
        } 
        else
        {
        	world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
        }
        return false;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
        if(BackpackUtils.getBackpackColor(worldIn, pos).equals("Bookshelf"))
        {
            BlockPos enchTable = BackpackUtils.findBlock3D(worldIn, pos.getX(), pos.getY(), pos.getZ(), Blocks.ENCHANTING_TABLE, 2, 2);
            
            if(enchTable != null)
            {
                if(!worldIn.isAirBlock(new BlockPos((enchTable.getX() - pos.getX()) / 2 + pos.getX(), enchTable.getY(), (enchTable.getZ() - pos.getZ()) / 2 + pos.getZ())))
                {
                    return;
                }
                
                for(int o = 0; o < 4; o++)
                {
                    worldIn.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, enchTable.getX() + 0.5D, enchTable.getY() + 2.0D, enchTable.getZ() + 0.5D,
                    		((pos.getX() - enchTable.getX()) + worldIn.rand.nextFloat()) - 0.5D,
                            ((pos.getY() - enchTable.getY()) - worldIn.rand.nextFloat() - 1.0F),
                            ((pos.getZ() - enchTable.getZ()) + worldIn.rand.nextFloat()) - 0.5D);
                }
            }
        }
    }

    @Override
	public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) { }

	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion) { }
	
	@Override
	public float getEnchantPowerBonus(World world, BlockPos pos)
    {
        return BackpackUtils.getBackpackColor(world, pos).equals("Bookshelf") ? 10F : 0F;
    }
	 
	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	} 
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
		switch(state.getValue(FACING))
        {
            case NORTH:
                return BACKPACK_NORTH_AABB;
            case SOUTH:
                return BACKPACK_SOUTH_AABB;
            case EAST:
                return BACKPACK_EAST_AABB;
            case WEST:
                return BACKPACK_WEST_AABB;
		default:
			return BACKPACK_NORTH_AABB;
        }
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }
	
	public EnumFacing getFacing(IBlockState state)
	{
		return state.getValue(FACING);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) 
	{
	    EnumFacing facing = EnumFacing.getFront(meta);

	    if(facing.getAxis() == EnumFacing.Axis.Y) 
	    {
	    	facing = EnumFacing.NORTH;
	    }
	    return getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state) 
	{
	    return state.getValue(FACING).getIndex();
	}
	    
	@Override
	protected BlockStateContainer createBlockState() 
	{
		return new BlockStateContainer(this, new IProperty[]{FACING});
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
	    return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}
	
	@Override
	public int quantityDropped(Random random)
    {
        return 1;
    }
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
		ItemStack stack = new ItemStack(ModItems.TRAVELERS_BACKPACK, 1);
		
		if(world.getTileEntity(target.getBlockPos()) instanceof TileEntityTravelersBackpack)
		{
			TileEntityTravelersBackpack te = (TileEntityTravelersBackpack)world.getTileEntity(target.getBlockPos());
			stack = new ItemStack(ModItems.TRAVELERS_BACKPACK, 1, BackpackUtils.convertNameToMeta(te.getColor()));
			te.transferToItemStack(stack);
		}
        return stack;
    }
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return ModItems.TRAVELERS_BACKPACK;
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new TileEntityTravelersBackpack();
	}
}