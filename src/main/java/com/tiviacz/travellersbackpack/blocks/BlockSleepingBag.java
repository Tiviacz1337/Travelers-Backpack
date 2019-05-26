package com.tiviacz.travellersbackpack.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.init.ModBlocks;
import com.tiviacz.travellersbackpack.init.ModItems;
import com.tiviacz.travellersbackpack.tileentity.TileEntityTravellersBackpack;
import com.tiviacz.travellersbackpack.util.Bounds;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSleepingBag extends BlockHorizontal
{
    public static final PropertyBool OCCUPIED = PropertyBool.create("occupied");
   
    private static final AxisAlignedBB BOUNDING_BOX = new Bounds(0, 0, 0, 16, 2, 16).toAABB();
    
	public BlockSleepingBag(String name, Material materialIn) 
	{
		super(materialIn);
		
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(TravellersBackpack.TRAVELLERSBACKPACKTAB);
		setSoundType(SoundType.CLOTH);
		setHardness(1.0F);
		setResistance(3.0F);
		setHarvestLevel("hand", 0);
		
		ModBlocks.BLOCKS.add(this);
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
		return BOUNDING_BOX;
	}

	@Override
	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
	{
		super.onFallenUpon(worldIn, pos, entityIn, fallDistance * 0.25F);
	}
	
	@Override
    public void onLanded(World worldIn, Entity entityIn)
    {
        if(entityIn.isSneaking())
        {
            super.onLanded(worldIn, entityIn);
        }
        else if(entityIn.motionY < 0.0D)
        {
            entityIn.motionY = -entityIn.motionY * 0.8D;

            if(!(entityIn instanceof EntityLivingBase))
            {
                entityIn.motionY *= 0.8D;
            }
        }
    }
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
		EnumFacing blockFacing = state.getValue(FACING);
		
		if(state.getBlock() == ModBlocks.SLEEPING_BAG_BOTTOM)
		{
			if(!world.isRemote)
			{
				BlockPos mayTilePos = pos.offset(blockFacing.getOpposite());
				
				if(world.getTileEntity(mayTilePos) instanceof TileEntityTravellersBackpack)
				{
					TileEntityTravellersBackpack tile = (TileEntityTravellersBackpack)world.getTileEntity(mayTilePos);
					tile.setSleepingBagDeployed(false);
					tile.markDirty();
				}
			}
		}
		
		else if(state.getBlock() == ModBlocks.SLEEPING_BAG_TOP)
		{
			if(!world.isRemote)
			{
				BlockPos mayTilePos = pos.offset(blockFacing.getOpposite(), 2);
				
				if(world.getTileEntity(mayTilePos) instanceof TileEntityTravellersBackpack)
				{
					TileEntityTravellersBackpack tile = (TileEntityTravellersBackpack)world.getTileEntity(mayTilePos);
					tile.setSleepingBagDeployed(false);
					tile.markDirty();
				}
			}
		}
		return super.removedByPlayer(state, world, pos, player, willHarvest);
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(worldIn.isRemote)
        {
            return true;
        }
        else
        {
            if(this == ModBlocks.SLEEPING_BAG_BOTTOM)
            {
                pos = pos.offset(state.getValue(FACING));
                state = worldIn.getBlockState(pos);
                if(state.getBlock() != ModBlocks.SLEEPING_BAG_TOP)
                {
                    return true;
                }
            }

            if(worldIn.provider.canRespawnHere() && worldIn.getBiome(pos) != Biomes.HELL)
            {
                if(state.getValue(OCCUPIED))
                {
                    EntityPlayer entityplayer = this.getPlayerInBed(worldIn, pos);

                    if(entityplayer != null)
                    {
                        playerIn.sendStatusMessage(new TextComponentTranslation("tile.bed.occupied"), true);
                        return true;
                    }

                    TileEntity tileEntity = worldIn.getTileEntity(pos);
                    state = state.withProperty(OCCUPIED, Boolean.FALSE);
                    worldIn.setBlockState(pos, state, 3);
                    if(tileEntity != null)
                    {
                        tileEntity.validate();
                        worldIn.setTileEntity(pos, tileEntity);
                    }
                }

                EntityPlayer.SleepResult sleepResult = playerIn.trySleep(pos);
                if(sleepResult == EntityPlayer.SleepResult.OK)
                {
                    TileEntity tileEntity = worldIn.getTileEntity(pos);
                    state = state.withProperty(OCCUPIED, Boolean.TRUE);
                    worldIn.setBlockState(pos, state, 3);
                    if(tileEntity != null)
                    {
                        tileEntity.validate();
                        worldIn.setTileEntity(pos, tileEntity);
                    }
                    return true;
                }
                else
                {
                    if(sleepResult == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW)
                    {
                        playerIn.sendStatusMessage(new TextComponentTranslation("tile.bed.noSleep"), true);
                    }
                    else if(sleepResult == EntityPlayer.SleepResult.NOT_SAFE)
                    {
                        playerIn.sendStatusMessage(new TextComponentTranslation("tile.bed.notSafe"), true);
                    }
                    else if(sleepResult == EntityPlayer.SleepResult.TOO_FAR_AWAY)
                    {
                        playerIn.sendStatusMessage(new TextComponentTranslation("tile.bed.tooFarAway"), true);
                    }
                    return true;
                }
            }
            else
            {
                worldIn.setBlockToAir(pos);
                EnumFacing facing1 = state.getValue(FACING);
                pos = pos.offset(this == ModBlocks.SLEEPING_BAG_TOP ? facing1.getOpposite() : facing1);
                if(worldIn.getBlockState(pos).getBlock() == (this == ModBlocks.SLEEPING_BAG_TOP ? ModBlocks.SLEEPING_BAG_BOTTOM : ModBlocks.SLEEPING_BAG_TOP))
                {
                    worldIn.setBlockToAir(pos);
                }
                worldIn.newExplosion(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 5.0F, true, true);
                return true;
            }
        }
    }
    
    @Nullable
    private EntityPlayer getPlayerInBed(World worldIn, BlockPos pos)
    {
        for(EntityPlayer entityplayer : worldIn.playerEntities)
        {
            if(entityplayer.isPlayerSleeping() && entityplayer.bedLocation.equals(pos))
            {
                return entityplayer;
            }
        }
        return null;
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(OCCUPIED, meta > 3);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int meta = state.getValue(FACING).getHorizontalIndex();
        if(state.getValue(OCCUPIED))
        {
            meta += 4;
        }
        return meta;
    }
    
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, OCCUPIED);
    }
    
    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        EnumFacing facing = state.getValue(FACING);
        
        if(this == ModBlocks.SLEEPING_BAG_TOP)
        {
            if(worldIn.getBlockState(pos.offset(facing.getOpposite())).getBlock() == ModBlocks.SLEEPING_BAG_BOTTOM)
            {
                worldIn.destroyBlock(pos.offset(facing.getOpposite()), false);
            }
        }
        else if(this == ModBlocks.SLEEPING_BAG_BOTTOM)
        {
            if(worldIn.getBlockState(pos.offset(facing)).getBlock() == ModBlocks.SLEEPING_BAG_TOP)
            {
                worldIn.destroyBlock(pos.offset(facing), false);
            }
        }
    }
    
    @Override
    public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable Entity player)
    {
        return true;
    }
    
    @Override
    public void setBedOccupied(IBlockAccess blockAccess, BlockPos pos, EntityPlayer player, boolean occupied)
    {
        if(blockAccess instanceof World)
        {
            World world = (World) blockAccess;
            TileEntity tileEntity = world.getTileEntity(pos);
            IBlockState state = world.getBlockState(pos);
            state = state.getBlock().getActualState(state, world, pos);
            state = state.withProperty(OCCUPIED, occupied);
            world.setBlockState(pos, state, 4);
            if(tileEntity != null)
            {
                tileEntity.validate();
                world.setTileEntity(pos, tileEntity);
            }
        }
    }
    
    @Override
    public EnumFacing getBedDirection(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return getActualState(state, world, pos).getValue(FACING);
    }

    @Override
    public boolean isBedFoot(IBlockAccess world, BlockPos pos)
    {
        return this == ModBlocks.SLEEPING_BAG_BOTTOM;
    }
    
    @Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) 
    {
		return new ItemStack(ModItems.SLEEPING_BAG);
	}
    
    @Override
	public int quantityDropped(Random random)
    {
        return 0;
    }
}