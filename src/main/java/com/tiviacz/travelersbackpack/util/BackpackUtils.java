package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

public class BackpackUtils
{
    public static void onPlayerDeath(World world, PlayerEntity player, ItemStack stack)
    {
        //    if(CapabilityUtils.getWearingBackpack(player).getMetadata() == 64)
         //   {
          //      world.createExplosion(player, player.posX, player.posY, player.posZ, 4.0F, false);
         //   }
        LazyOptional<ITravelersBackpack> cap = CapabilityUtils.getCapability(player);

        if(TravelersBackpackConfig.COMMON.backpackDeathPlace.get())
        {
            if(!tryPlace(world, player, stack))
            {
                player.entityDropItem(stack, 1);
                cap.ifPresent(ITravelersBackpack::removeWearable);

                if(TravelersBackpackConfig.CLIENT.enableBackpackCoordsMessage.get())
                {
                    String translation = new TranslationTextComponent("information.travelersbackpack.backpack_drop").getString();
                    player.sendMessage(new StringTextComponent(translation + " X: " + player.getPosition().getX() + " Y: " + player.getPosition().getY() + " Z: " + player.getPosition().getZ()), player.getUniqueID());
                }
            }
        }
        else
        {
            player.entityDropItem(stack, 1);
            cap.ifPresent(ITravelersBackpack::removeWearable);
        }
    }

    private static boolean tryPlace(World world, PlayerEntity player, ItemStack stack)
    {
        int X = (int) player.getPosX();
        int Z = (int) player.getPosZ();
        int[] positions = {0,-1,1,-2,2,-3,3,-4,4,-5,5,-6,6};

        for(int Y: positions)
        {
            BlockPos spawn = getNearestEmptyChunkCoordinatesSpiral(player, world, X, Z, new BlockPos(X, (int)player.getPosY() + Y, Z), 12, true, 1, (byte) 0, false);

            if(spawn != null)
            {
                return placeBackpack(stack, player, world, spawn.getX(), spawn.getY(), spawn.getZ(), Direction.UP);
            }
        }
        return false;
    }

    public static boolean placeBackpack(ItemStack stack, PlayerEntity player, World world, int x, int y, int z, Direction facing)
    {
        if(stack.getTag() == null)
        {
            stack.setTag(new CompoundNBT());
        }

        Block block = Block.getBlockFromItem(stack.getItem());

        if(y <= 0 || y >= world.getHeight()) return false;

        BlockPos targetPos = new BlockPos(x, y, z);

        if(world.getBlockState(targetPos).getMaterial().isReplaceable())
        {
            if(!world.getBlockState(targetPos).getMaterial().isSolid())
            {
                if(!world.setBlockState(targetPos, block.getDefaultState()))
                {
                    return false;
                }

                if(TravelersBackpackConfig.CLIENT.enableBackpackCoordsMessage.get())
                {
                    String translation = new TranslationTextComponent("information.travelersbackpack.backpack_coords").getString();
                    player.sendMessage(new StringTextComponent(translation + " X: " + x + " Y: " + y + " Z: " + z), player.getUniqueID());
                }

                world.playSound(player, x, y, z, block.getDefaultState().getSoundType().getPlaceSound(), SoundCategory.BLOCKS, 0.5F, 1.0F);
                ((TravelersBackpackTileEntity)world.getTileEntity(targetPos)).loadAllData(stack.getTag());

                if(CapabilityUtils.isWearingBackpack(player))
                {
                    CapabilityUtils.getCapability(player).ifPresent(cap -> cap.setWearable(ItemStack.EMPTY));
                }

                return true;
            }
        }
        return false;
    }

    public static BlockPos getNearestEmptyChunkCoordinatesSpiral(PlayerEntity player, World world, int origX, int origZ, BlockPos pos, int radius, boolean except, int steps, byte pass, boolean type)
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
                    BlockPos blockPos = type ? checkCoordsForPlayer(player, world, origX, origZ, pos, except) : checkCoordsForBackpack(player, world, origX, origZ, pos, except);

                    if(blockPos != null)
                    {
                        return blockPos;
                    }
                }
                pass++;
                return getNearestEmptyChunkCoordinatesSpiral(player, world, origX, origZ, new BlockPos(i, pos.getY(), j), radius, except, steps, pass, type);
            }

            if(pass == 1)
            {
                for(; j >= pos.getZ() - steps; j--)
                {
                    BlockPos blockPos = type ? checkCoordsForPlayer(player, world, origX, origZ, pos, except) : checkCoordsForBackpack(player, world, origX, origZ, pos, except);

                    if(blockPos != null)
                    {
                        return blockPos;
                    }
                }
                pass--;
                steps++;
                return getNearestEmptyChunkCoordinatesSpiral(player, world, origX, origZ, new BlockPos(i, pos.getY(), j), radius, except, steps, pass, type);
            }
        }

        if(steps % 2 == 1)
        {
            if(pass == 0)
            {
                for(; i >= pos.getX() - steps; i--)
                {
                    BlockPos blockPos = type ? checkCoordsForPlayer(player, world, origX, origZ, pos, except) : checkCoordsForBackpack(player, world, origX, origZ, pos, except);

                    if(blockPos != null)
                    {
                        return blockPos;
                    }
                }
                pass++;
                return getNearestEmptyChunkCoordinatesSpiral(player, world, origX, origZ, new BlockPos(i, pos.getY(), j), radius, except, steps, pass, type);
            }

            if(pass == 1)
            {
                for(; j <= pos.getZ() + steps; j++)
                {
                    BlockPos blockPos = type ? checkCoordsForPlayer(player, world, origX, origZ, pos, except) : checkCoordsForBackpack(player, world, origX, origZ, pos, except);

                    if(blockPos != null)
                    {
                        return blockPos;
                    }
                }
                pass--;
                steps++;
                return getNearestEmptyChunkCoordinatesSpiral(player, world, origX, origZ, new BlockPos(i, pos.getY(), j), radius, except, steps, pass, type);
            }
        }
        return null;
    }

    private static BlockPos checkCoordsForBackpack(PlayerEntity player, World world, int origX, int origZ, BlockPos pos, boolean except)
    {
        if(except && world.isTopSolid(pos.offset(Direction.DOWN), player) && world.isAirBlock(pos) && !areCoordinatesTheSame(new BlockPos(origX, pos.getY(), origZ), pos))
        {
            return pos;
        }
        if(!except && world.isTopSolid(pos.offset(Direction.DOWN), player) && world.isAirBlock(pos))
        {
            return pos;
        }
        return null;
    }

    private static BlockPos checkCoordsForPlayer(PlayerEntity player, World world, int origX, int origZ, BlockPos pos, boolean except)
    {
        if(except && world.isTopSolid(pos.offset(Direction.DOWN), player) && world.isAirBlock(pos) && world.isAirBlock(pos.offset(Direction.UP)) && !areCoordinatesTheSame2D(origX, origZ, pos.getX(), pos.getZ()))
        {
            return pos;
        }
        if(!except && world.isTopSolid(pos.offset(Direction.DOWN), player) && world.isAirBlock(pos) && world.isAirBlock(pos.offset(Direction.UP)))
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
