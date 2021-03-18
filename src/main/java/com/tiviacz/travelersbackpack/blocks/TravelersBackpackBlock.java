package com.tiviacz.travelersbackpack.blocks;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackTileEntity;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class TravelersBackpackBlock extends Block
{
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    private static final VoxelShape BACKPACK_SHAPE_NORTH = Block.makeCuboidShape(1.0D, 0.0D, 4.0D, 15.0D, 10.0D, 12.0D);
    private static final VoxelShape BACKPACK_SHAPE_SOUTH = Block.makeCuboidShape(1.0D, 0.0D, 4.0D, 15.0D, 10.0D, 12.0D);
    private static final VoxelShape BACKPACK_SHAPE_EAST = Block.makeCuboidShape(4.0D, 0.0D, 1.0D, 12.0D, 10.0D, 15.0D);
    private static final VoxelShape BACKPACK_SHAPE_WEST = Block.makeCuboidShape(4.0D, 0.0D, 1.0D, 12.0D, 10.0D, 15.0D);

    public TravelersBackpackBlock(Block.Properties builder)
    {
        super(builder.hardnessAndResistance(1.0F, Float.MAX_VALUE).harvestLevel(0));
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(FACING)) {
            case SOUTH:
                return BACKPACK_SHAPE_SOUTH;
            case EAST:
                return BACKPACK_SHAPE_EAST;
            case WEST:
                return BACKPACK_SHAPE_WEST;
            default:
                return BACKPACK_SHAPE_NORTH;
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if(worldIn.getTileEntity(pos) instanceof TravelersBackpackTileEntity)
        {
            TravelersBackpackTileEntity te = (TravelersBackpackTileEntity)worldIn.getTileEntity(pos);

            if(TravelersBackpackConfig.SERVER.enableBackpackBlockWearable.get())
            {
                if(player.isSneaking() && !worldIn.isRemote)
                {
                    if(!CapabilityUtils.isWearingBackpack(player))
                    {
                        if(!TravelersBackpack.enableCurios())
                        {
                            if(worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 7))
                            {
                                ItemStack stack = new ItemStack(asItem(), 1);
                                te.transferToItemStack(stack);
                                CapabilityUtils.equipBackpack(player, stack);

                                if(te.isSleepingBagDeployed())
                                {
                                    Direction bagDirection = state.get(TravelersBackpackBlock.FACING);
                                    worldIn.setBlockState(pos.offset(bagDirection), Blocks.AIR.getDefaultState());
                                    worldIn.setBlockState(pos.offset(bagDirection).offset(bagDirection), Blocks.AIR.getDefaultState());
                                }
                            }
                            else
                            {
                                player.sendMessage(new TranslationTextComponent(Reference.FAIL), player.getUniqueID());
                            }
                        }
                        else
                        {
                            player.sendMessage(new TranslationTextComponent(Reference.FAIL), player.getUniqueID());
                        }
                    }
                    else
                    {
                        player.sendMessage(new TranslationTextComponent(Reference.OTHER_BACKPACK), player.getUniqueID());
                    }
                }
                else
                {
                    te.openGUI(player, te, pos);
                }
            }
            else
            {
                te.openGUI(player, te, pos);
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) { }

    @Override
    public void onBlockExploded(final BlockState state, final World world, final BlockPos pos, final Explosion explosion) { return; }

 /*   @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        TileEntity te = world.getTileEntity(pos);

        if(te instanceof TravelersBackpackTileEntity && !world.isRemote())
        {
            ((TravelersBackpackTileEntity)te).drop(world, pos, asItem());

            if(((TravelersBackpackTileEntity)te).isSleepingBagDeployed())
            {
                Direction direction = state.get(FACING);
                world.setBlockState(pos.offset(direction), Blocks.AIR.getDefaultState());
                world.setBlockState(pos.offset(direction).offset(direction), Blocks.AIR.getDefaultState());
            }
        }
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
        super.onReplaced(state, world, pos, newState, isMoving);
    } */

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
        TileEntity te = worldIn.getTileEntity(pos);

        if(te instanceof TravelersBackpackTileEntity && !worldIn.isRemote())
        {
            ((TravelersBackpackTileEntity)te).drop(worldIn, pos, asItem());

            if(((TravelersBackpackTileEntity)te).isSleepingBagDeployed())
            {
                Direction direction = state.get(FACING);
                worldIn.setBlockState(pos.offset(direction), Blocks.AIR.getDefaultState());
                worldIn.setBlockState(pos.offset(direction).offset(direction), Blocks.AIR.getDefaultState());
            }
        }

        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), worldIn.isRemote ? 11 : 3);

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        ItemStack stack = new ItemStack(asItem(), 1);

        if(world.getTileEntity(pos) instanceof TravelersBackpackTileEntity)
        {
            TravelersBackpackTileEntity te = (TravelersBackpackTileEntity)world.getTileEntity(pos);
            te.transferToItemStack(stack);
        }
        return stack;
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new TravelersBackpackTileEntity();
    }

    //Special

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        super.animateTick(stateIn, worldIn, pos, rand);

        if(stateIn.getBlock() == ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK.get())
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
                    worldIn.addParticle(ParticleTypes.ENCHANT, enchTable.getX() + 0.5D, enchTable.getY() + 2.0D, enchTable.getZ() + 0.5D,
                            ((pos.getX() - enchTable.getX()) + worldIn.rand.nextFloat()) - 0.5D,
                            ((pos.getY() - enchTable.getY()) - worldIn.rand.nextFloat() - 1.0F),
                            ((pos.getZ() - enchTable.getZ()) + worldIn.rand.nextFloat()) - 0.5D);
                }
            }
        }
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos)
    {
        return state.getBlock() == ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK.get() ? 5 : super.getEnchantPowerBonus(state, world, pos);
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        return blockState.getBlock() == ModBlocks.REDSTONE_TRAVELERS_BACKPACK.get() ? 15 : super.getWeakPower(blockState, blockAccess, pos, side);
    }

    @Override
    public boolean canProvidePower(BlockState state)
    {
        return state.getBlock() == ModBlocks.REDSTONE_TRAVELERS_BACKPACK.get();
    }
}