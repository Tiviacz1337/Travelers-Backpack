package com.tiviacz.travellersbackpack.util;

import com.tiviacz.travellersbackpack.capability.CapabilityUtils;
import com.tiviacz.travellersbackpack.capability.IBackpack;
import com.tiviacz.travellersbackpack.handlers.ConfigHandler;
import com.tiviacz.travellersbackpack.init.ModBlocks;
import com.tiviacz.travellersbackpack.tileentity.TileEntityTravellersBackpack;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BackpackUtils 
{
	public static String getBackpackColor(World world, BlockPos pos)
	{
		TileEntityTravellersBackpack backpack = (TileEntityTravellersBackpack)world.getTileEntity(pos);
		return backpack.getColor();
	}
	
	public static int convertNameToMeta(String name)
	{
		for(int x = 0; x < Reference.BACKPACK_NAMES.length; x++)
		{
			String string = Reference.BACKPACK_NAMES[x];
			
			if(string.toLowerCase().equals(name.toLowerCase()))
			{
				return x;
			}
		}
		return 0;
	}
	
	public static void onPlayerDeath(World world, EntityPlayer player, ItemStack stack)
	{
		if(!world.isRemote)
		{
			if(CapabilityUtils.getWearingBackpack(player).getMetadata() == 64)
			{
				world.createExplosion(player, player.posX, player.posY, player.posZ, 4.0F, false);
			}
			
			if(ConfigHandler.server.backpackDeathPlace)
			{
				if(!tryPlace(world, player, stack))
				{
					player.entityDropItem(stack, 1);
				}
			}
			else
			{
				player.entityDropItem(stack, 1);
				IBackpack cap = CapabilityUtils.getCapability(player);
				cap.removeWearable();
			}
		}
	}
	
	private static boolean tryPlace(World world, EntityPlayer player, ItemStack backpack)
	{
		int X = (int) player.posX;
		int Z = (int) player.posZ;
		int positions[] = {0,-1,1,-2,2,-3,3,-4,4,-5,5,-6,6};

		for(int Y: positions)
		{
			BlockPos spawn = getNearestEmptyChunkCoordinatesSpiral(world, X, Z, new BlockPos(X, (int)player.posY + Y, Z), 12, true, 1, (byte) 0, false);
	            
			if(spawn != null)
			{
				return placeBackpack(backpack, player, world, spawn.getX(), spawn.getY(), spawn.getZ(), EnumFacing.UP, false);
			}
		}
		return false;
	}
	 
	public static boolean placeBackpack(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, EnumFacing facing, boolean from)
	{
		if(stack.getTagCompound() == null)
		{
			stack.setTagCompound(new NBTTagCompound());
		}

		Block backpack = ModBlocks.TRAVELLERS_BACKPACK;

		if(y <= 0 || y >= world.getHeight())
		{
			return false;
		}
	        
		if(backpack.canPlaceBlockOnSide(world, new BlockPos(x, y, z), facing))
		{
			if(world.getBlockState(new BlockPos(x,y,z)).getMaterial().isSolid())
			{
				switch(facing)
				{
					case DOWN:
						--y;
						break;
					case UP:
						++y;
						break;
					case NORTH:
						--z;
						break;
					case SOUTH:
						++z;
						break;
					case WEST:
						--x;
						break;
					case EAST:
						++x;
						break;
				}
			}
			
			if(y <= 0 || y >= world.getHeight())
			{
				return false;
			}
			
			if(backpack.canPlaceBlockAt(world, new BlockPos(x, y, z)))
			{
				BlockPos targetPos = new BlockPos(x,y,z);
	            	
				if(world.getBlockState(targetPos).getMaterial().isReplaceable())
				{
					if(!world.getBlockState(targetPos).getMaterial().isSolid())
					{
						world.setBlockState(targetPos, ModBlocks.TRAVELLERS_BACKPACK.getDefaultState());
						world.playSound(player, x, y, z, SoundEvents.BLOCK_CLOTH_PLACE, SoundCategory.BLOCKS, 0.5F, 1.0F);
						((TileEntityTravellersBackpack)world.getTileEntity(targetPos)).loadAllData(stack.getTagCompound());
						((TileEntityTravellersBackpack)world.getTileEntity(targetPos)).setColorFromMeta(stack.getMetadata());
	        				
						if(CapabilityUtils.isWearingBackpack(player))
						{
							IBackpack cap = CapabilityUtils.getCapability(player);
							cap.setWearable(ItemStack.EMPTY);
						}
	        				
						return true;
					}
				}
			}
		}
	  	return false;
	}
	
	public static BlockPos findBlock3D(World world, int x, int y, int z, Block block, int hRange, int vRange)
    {
        for (int i = (y - vRange); i <= (y + vRange); i++)
        {
            for (int j = (x - hRange); j <= (x + hRange); j++)
            {
                for (int k = (z - hRange); k <= (z + hRange); k++)
                {
                    if (world.getBlockState(new BlockPos(j, i, k)).getBlock() == block)
                    {
                        return new BlockPos(j, i, k);
                    }
                }
            }
        }
        return null;
    }
	
	public static BlockPos getNearestEmptyChunkCoordinatesSpiral(IBlockAccess world, int origX, int origZ, BlockPos pos, int radius, boolean except, int steps, byte pass, boolean type)
    {
        if(steps >= radius) 
        {
        	return null;
        }
        
        int i = pos.getX();
        int j = pos.getZ();
        
        if(steps % 2 == 0)
        {
            if(pass == 0)
            {
                for(; i <= pos.getX() + steps; i++)
                {
                	BlockPos blockPos = type ? checkCoordsForPlayer(world, origX, origZ, pos, except) : checkCoordsForBackpack(world, origX, origZ, pos, except);
                   
                	if(blockPos != null)
                    {
                        return blockPos;
                    }
                }
                pass++;
                return getNearestEmptyChunkCoordinatesSpiral(world, origX, origZ, new BlockPos(i, pos.getY(), j), radius, except, steps, pass, type);
            }
            
            if(pass == 1)
            {
                for(; j >= pos.getZ() - steps; j--)
                {
                	BlockPos blockPos = type ? checkCoordsForPlayer(world, origX, origZ, pos, except) : checkCoordsForBackpack(world, origX, origZ, pos, except);
                    
                	if(blockPos != null)
                    {
                        return blockPos;
                    }
                }
                pass--;
                steps++;
                return getNearestEmptyChunkCoordinatesSpiral(world, origX, origZ, new BlockPos(i, pos.getY(), j), radius, except, steps, pass, type);
            }
        }

        if(steps % 2 == 1)
        {
            if(pass == 0)
            {
                for(; i >= pos.getX() - steps; i--)
                {
                	BlockPos blockPos = type ? checkCoordsForPlayer(world, origX, origZ, pos, except) : checkCoordsForBackpack(world, origX, origZ, pos, except);
                    
                	if(blockPos != null)
                    {
                        return blockPos;
                    }
                }
                pass++;
                return getNearestEmptyChunkCoordinatesSpiral(world, origX, origZ, new BlockPos(i, pos.getY(), j), radius, except, steps, pass, type);
            }
            
            if(pass == 1)
            {
                for(; j <= pos.getZ() + steps; j++)
                {
                	BlockPos blockPos = type ? checkCoordsForPlayer(world, origX, origZ, pos, except) : checkCoordsForBackpack(world, origX, origZ, pos, except);
                    
                	if(blockPos != null)
                    {
                        return blockPos;
                    }
                }
                pass--;
                steps++;
                return getNearestEmptyChunkCoordinatesSpiral(world, origX, origZ, new BlockPos(i, pos.getY(), j), radius, except, steps, pass, type);
            }
        }
        return null;
    }
	
	private static BlockPos checkCoordsForBackpack(IBlockAccess world, int origX, int origZ, BlockPos pos, boolean except)
    {
        if(except && world.isSideSolid(pos.offset(EnumFacing.DOWN), EnumFacing.UP,true) && world.isAirBlock(pos) && !areCoordinatesTheSame(new BlockPos(origX, pos.getY(), origZ), pos))
        {
            return pos;
        }
        if(!except && world.isSideSolid(pos.offset(EnumFacing.DOWN), EnumFacing.UP,true) && world.isAirBlock(pos))
        {
            return pos;
        }
        return null;
    }

    private static BlockPos checkCoordsForPlayer(IBlockAccess world, int origX, int origZ, BlockPos pos, boolean except)
    {
        if(except && world.isSideSolid(pos.offset(EnumFacing.DOWN), EnumFacing.UP, true) && world.isAirBlock(pos) && world.isAirBlock(pos.offset(EnumFacing.UP)) && !areCoordinatesTheSame2D(origX, origZ, pos.getX(), pos.getZ()))
        {
            return pos;
        }
        if(!except && world.isSideSolid(pos.offset(EnumFacing.DOWN), EnumFacing.UP, true) && world.isAirBlock(pos) && world.isAirBlock(pos.offset(EnumFacing.UP)))
        {
            return pos;
        }
        return null;
    }
    
    private static boolean areCoordinatesTheSame(BlockPos pos1, BlockPos pos2)
    {
        return pos1 == pos2;
    }

    private static boolean areCoordinatesTheSame2D(int X1, int Z1, int X2, int Z2)
    {
        return (X1 == X2 &&  Z1 == Z2);
    }
}