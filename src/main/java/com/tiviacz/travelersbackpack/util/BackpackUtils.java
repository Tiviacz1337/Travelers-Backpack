package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.common.BackpackManager;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.network.SendMessagePacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class BackpackUtils
{
    public static boolean onPlayerDrops(World world, PlayerEntity player, ItemStack stack)
    {
        if(!world.isClient) BackpackManager.addBackpack((ServerPlayerEntity)player, stack);

        //If grave mod installed, then skip. Backpack will be stored inside grave
        if(TravelersBackpack.isAnyGraveModInstalled()) return true;

        boolean drop = true;

        if(TravelersBackpackConfig.getConfig().backpackSettings.backpackDeathPlace)
        {
            if(TravelersBackpackConfig.getConfig().backpackSettings.backpackForceDeathPlace) drop = !placeBackpack(world, player, player.getBlockPos(), stack);
            else drop = !tryPlace(world, player, stack);
        }

        return drop;
    }

    private static boolean placeBackpack(World world, PlayerEntity player, BlockPos placePos, ItemStack stack)
    {
        Block block = Block.getBlockFromItem(stack.getItem());
        int y = placePos.getY();

        if(TravelersBackpackConfig.getConfig().backpackSettings.backpackForceDeathPlace)
        {
            BlockPos playerPos = player.getBlockPos();
            y = playerPos.getY();

            if(TravelersBackpackConfig.getConfig().backpackSettings.voidProtection)
            {
                if(y <= world.getBottomY())
                {
                    y = world.getBottomY() + 5;
                }
            }

            for(int i = y; i < world.getHeight(); i++)
            {
                if(world.getBlockState(new BlockPos(playerPos.getX(), i, playerPos.getZ())).isAir())
                {
                    y = i;
                    break;
                }
            }

            BlockPos targetPos = new BlockPos(playerPos.getX(), y, playerPos.getZ());

            if(world.getBlockState(targetPos).getBlock().getBlastResistance() > -1)
            {
                while(world.getBlockEntity(targetPos) != null)
                {
                    targetPos = targetPos.up();
                }

                if(!world.setBlockState(targetPos, block.getDefaultState()))
                {
                    return false;
                }

                placeBackpackInTheWorld(world, player, targetPos, block, stack);
                return true;
            }
            return false;
        }
        else
        {
            if(y <= world.getBottomY() || y >= world.getHeight()) return false;

            BlockPos targetPos = new BlockPos(placePos.getX(), y, placePos.getZ());

            if(!world.setBlockState(targetPos, block.getDefaultState()))
            {
                return false;
            }

            placeBackpackInTheWorld(world, player, targetPos, block, stack);
            return true;
        }
    }

    private static void placeBackpackInTheWorld(World world, PlayerEntity player, BlockPos targetPos, Block block, ItemStack stack)
    {
        ServerPlayNetworking.send((ServerPlayerEntity)player, new SendMessagePacket(false, targetPos));
        LogHelper.info("Your backpack has been placed at" + " X: " + targetPos.getX() + " Y: " + targetPos.getY() + " Z: " + targetPos.getZ());

        world.playSound(player, targetPos.getX(), targetPos.getY(), targetPos.getZ(), block.getDefaultState().getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 0.5F, 1.0F);
        world.getBlockEntity(targetPos).readComponents(stack);

        if(stack.contains(DataComponentTypes.CUSTOM_NAME))
        {
            ((TravelersBackpackBlockEntity)world.getBlockEntity(targetPos)).setCustomName(stack.getName());
        }

        if(ComponentUtils.isWearingBackpack(player) && !world.isClient)
        {
            ComponentUtils.getComponent(player).removeWearable();
        }
    }

    private static boolean tryPlace(World world, @NotNull PlayerEntity player, ItemStack stack)
    {
        int X = (int) player.getX();
        int Z = (int) player.getZ();
        int[] positions = {0,-1,1,-2,2,-3,3,-4,4,-5,5,-6,6};

        for(int Y: positions)
        {
            int y = (int)player.getY();

            if(TravelersBackpackConfig.getConfig().backpackSettings.voidProtection)
            {
                if(y <= world.getBottomY())
                {
                    y = world.getBottomY() + 5;
                }
            }

            BlockPos spawn = getNearestEmptyChunkCoordinatesSpiral(player, world, X, Z, new BlockPos(X, y + Y, Z), 12, true, 1, (byte) 0);

            if(spawn != null)
            {
                return placeBackpack(world, player, spawn, stack);
            }
        }
        return false;
    }

    public static String getConvertedTime(int ticks) {

        int i = ticks / 20;
        int minutes = i / 60;
        int seconds = i % 60;

        if (seconds < 10) {
            return minutes + ":" + "0" + seconds;
        }

        return minutes + ":" + seconds;
    }

    public static boolean isShiftPressed()
    {
        return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS || GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
    }

    public static boolean isCtrlPressed()
    {
        return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS || GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
    }

    /**
     * Gets you the nearest Empty Chunk Coordinates, free of charge! Looks in two dimensions and finds a block
     * that a: can have stuff placed on it and b: has space above it.
     * This is a spiral search, will begin at close range and move out.
     * @param world  The world object.
     * @param origX  Original X coordinate
     * @param origZ  Original Z coordinate
     * @param pos      Moving X coordinate, should be the same as origX when called.
     * @param pos      Y coordinate, does not move.
     * @param pos      Moving Z coordinate, should be the same as origZ when called.
     * @param radius The radius of the search. If set to high numbers, will create a ton of lag
     * @param except Wether to include the origin of the search as a valid block.
     * @param steps  Number of steps of the recursive recursiveness that recurses through the recursion. It is the first size of the spiral, should be one (1) always at the first call.
     * @param pass   Pass switch for the witchcraft I can't quite explain. Set to 0 always at the beggining.
     * @return The coordinates of the block in the chunk of the world of the game of the server of the owner of the computer, where you can place something above it.
     */
    public static BlockPos getNearestEmptyChunkCoordinatesSpiral(PlayerEntity player, World world, int origX, int origZ, BlockPos pos, int radius, boolean except, int steps, byte pass)
    {
        //Spiral search, because Mr Darkona is awesome :)
        //This is so the backpack tries to get placed near the death point first
        //And then goes looking farther away at each step
        //Steps mod 2 == 0 => X++, Z--
        //Steps mod 2 == 1 => X--, Z++

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
                    BlockPos blockPos = checkCoordsForBackpack(player, world, origX, origZ, pos, except);

                    if(blockPos != null)
                    {
                        return blockPos;
                    }
                }
                pass++;
                return getNearestEmptyChunkCoordinatesSpiral(player, world, origX, origZ, new BlockPos(i, pos.getY(), j), radius, except, steps, pass);
            }

            if(pass == 1)
            {
                for(; j >= pos.getZ() - steps; j--)
                {
                    BlockPos blockPos = checkCoordsForBackpack(player, world, origX, origZ, pos, except);

                    if(blockPos != null)
                    {
                        return blockPos;
                    }
                }
                pass--;
                steps++;
                return getNearestEmptyChunkCoordinatesSpiral(player, world, origX, origZ, new BlockPos(i, pos.getY(), j), radius, except, steps, pass);
            }
        }

        if(steps % 2 == 1)
        {
            if(pass == 0)
            {
                for(; i >= pos.getX() - steps; i--)
                {
                    BlockPos blockPos = checkCoordsForBackpack(player, world, origX, origZ, pos, except);

                    if(blockPos != null)
                    {
                        return blockPos;
                    }
                }
                pass++;
                return getNearestEmptyChunkCoordinatesSpiral(player, world, origX, origZ, new BlockPos(i, pos.getY(), j), radius, except, steps, pass);
            }

            if(pass == 1)
            {
                for(; j <= pos.getZ() + steps; j++)
                {
                    BlockPos blockPos = checkCoordsForBackpack(player, world, origX, origZ, pos, except);

                    if(blockPos != null)
                    {
                        return blockPos;
                    }
                }
                pass--;
                steps++;
                return getNearestEmptyChunkCoordinatesSpiral(player, world, origX, origZ, new BlockPos(i, pos.getY(), j), radius, except, steps, pass);
            }
        }
        return null;
    }

    private static BlockPos checkCoordsForBackpack(PlayerEntity player, World world, int origX, int origZ, BlockPos pos, boolean except)
    {
        if(except && world.isTopSolid(pos.offset(Direction.DOWN), player) && (world.isAir(pos) || world.getBlockState(pos).isReplaceable()) && !areCoordinatesTheSame(new BlockPos(origX, pos.getY(), origZ), pos))
        {
            return pos;
        }
        if(!except && world.isTopSolid(pos.offset(Direction.DOWN), player) && (world.isAir(pos) || world.getBlockState(pos).isReplaceable()))
        {
            return pos;
        }
        return null;
    }

    private static boolean areCoordinatesTheSame(BlockPos pos1, BlockPos pos2)
    {
        return pos1 == pos2;
    }

    public static BlockPos findBlock3D(World world, int x, int y, int z, Block block, int hRange, int vRange)
    {
        for(int i = (y - vRange); i <= (y + vRange); i++)
        {
            for(int j = (x - hRange); j <= (x + hRange); j++)
            {
                for(int k = (z - hRange); k <= (z + hRange); k++)
                {
                    if(world.getBlockState(new BlockPos(j, i, k)).getBlock() == block)
                    {
                        return new BlockPos(j, i, k);
                    }
                }
            }
        }
        return null;
    }
}