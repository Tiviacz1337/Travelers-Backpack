package com.tiviacz.travelersbackpack.blocks;

import com.google.common.collect.Lists;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackTileEntity;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Queue;
import java.util.Random;
import java.util.stream.Stream;

public class TravelersBackpackBlock extends Block
{
    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    private static final double X = (double)14/18;
    private static final double Y = (double)10/13;
    private static final double Z = (double)7/9;
    private static final double OX = 1.775;
    private static final double OY = 1.655;
    private static final double OZ = 1.778;

    private static final VoxelShape BACKPACK_SHAPE_NORTH = Stream.of(
            Block.box((3.0D*X)+OX, (-1.0D*Y)+OY, (6.0D*Z)+OZ, (13.0D*X)+OX, (11.0D*Y)+OY, (11.0D*Z)+OZ), //Main
            Block.box((3.0D*X)+OX, (-2.0D*Y)+OY, (7.0D*Z)+OZ, (13.0D*X)+OX, (-1.0D*Y)+OY, (11.0D*Z)+OZ), //Main
            Block.box((4.0D*X)+OX, (1.08D*Y)+OY, (4.0D*Z)+OZ, (12.0D*X)+OX, (7.08D*Y)+OY, (6.0D*Z)+OZ), //Pocket
            Block.box((4.0D*X)+OX, (0.0D*Y)+OY, (11.0D*Z)+OZ, (5.0D*X)+OX, (8.0D*Y)+OY, (12.0D*Z)+OZ), //Right Strap
            Block.box((11.0D*X)+OX, (0.0D*Y)+OY, (11.0D*Z)+OZ, (12.0D*X)+OX, (8.0D*Y)+OY, (12.0D*Z)+OZ), //Left Strap
            Block.box((-1.0D*X)+OX, (-2.0D*Y)+OY, (6.5D*Z)+OZ, (3.0D*X)+OX, (8.0D*Y)+OY, (10.5D*Z)+OZ),
            Block.box((13.0D*X)+OX, (-2.0D*Y)+OY, (6.5D*Z)+OZ, (17.0D*X)+OX, (8.0D*Y)+OY, (10.5D*Z)+OZ)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape BACKPACK_SHAPE_SOUTH = Stream.of(
            Block.box((3.0D*X)+OX, (-1.0D*Y)+OY, (5.0D*Z)+OZ, (13.0D*X)+OX, (11.0D*Y)+OY, (10.0D*Z)+OZ), //Main
            Block.box((3.0D*X)+OX, (-2.0D*Y)+OY, (5.0D*Z)+OZ, (13.0D*X)+OX, (-1.0D*Y)+OY, (9.0D*Z)+OZ), //Main
            Block.box((4.0D*X)+OX, (1.08D*Y)+OY, (10.0D*Z)+OZ, (12.0D*X)+OX, (7.08D*Y)+OY, (12.0D*Z)+OZ), //Pocket
            Block.box((4.0D*X)+OX, (0.0D*Y)+OY, (5.0D*Z)+OZ, (5.0D*X)+OX, (8.0D*Y)+OY, (4.0D*Z)+OZ), //Right Strap
            Block.box((11.0D*X)+OX, (0.0D*Y)+OY, (5.0D*Z)+OZ, (12.0D*X)+OX, (8.0D*Y)+OY, (4.0D*Z)+OZ), //Left Strap
            Block.box((-1.0D*X)+OX, (-2.0D*Y)+OY, (5.5D*Z)+OZ, (3.0D*X)+OX, (8.0D*Y)+OY, (9.5D*Z)+OZ),
            Block.box((13.0D*X)+OX, (-2.0D*Y)+OY, (5.5D*Z)+OZ, (17.0D*X)+OX, (8.0D*Y)+OY, (9.5D*Z)+OZ)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape BACKPACK_SHAPE_WEST = Stream.of(
            Block.box((6.0D*X)+OX, (-1.0D*Y)+OY, (3.0D*Z)+OZ, (11.0D*X)+OX, (11.0D*Y)+OY, (13.0D*Z)+OZ), //Main
            Block.box((7.0D*X)+OX, (-2.0D*Y)+OY, (3.0D*Z)+OZ, (11.0D*X)+OX, (-1.0D*Y)+OY, (13.0D*Z)+OZ), //Main
            Block.box((4.0D*X)+OX, (1.08D*Y)+OY, (4.0D*Z)+OZ, (6.0D*X)+OX, (7.08D*Y)+OY, (12.0D*Z)+OZ), //Pocket
            Block.box((11.0D*X)+OX, (0.0D*Y)+OY, (4.0D*Z)+OZ, (12.0D*X)+OX, (8.0D*Y)+OY, (5.0D*Z)+OZ), //Right Strap
            Block.box((11.0D*X)+OX, (0.0D*Y)+OY, (11.0D*Z)+OZ, (12.0D*X)+OX, (8.0D*Y)+OY, (12.0D*Z)+OZ), //Left Strap
            Block.box((6.5D*X)+OX, (-2.0D*Y)+OY, (-1.0D*Z)+OZ, (10.5D*X)+OX, (8.0D*Y)+OY, (3.0D*Z)+OZ),
            Block.box((6.5D*X)+OX, (-2.0D*Y)+OY, (13.0D*Z)+OZ, (10.5D*X)+OX, (8.0D*Y)+OY, (17.0D*Z)+OZ)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape BACKPACK_SHAPE_EAST = Stream.of(
            Block.box((5.0D*X)+OX, (-1.0D*Y)+OY, (3.0D*Z)+OZ, (10.0D*X)+OX, (11.0D*Y)+OY, (13.0D*Z)+OZ), //Main
            Block.box((5.0D*X)+OX, (-2.0D*Y)+OY, (3.0D*Z)+OZ, (9.0D*X)+OX, (-1.0D*Y)+OY, (13.0D*Z)+OZ), //Main
            Block.box((10.0D*X)+OX, (1.08D*Y)+OY, (4.0D*Z)+OZ, (12.0D*X)+OX, (7.08D*Y)+OY, (12.0D*Z)+OZ), //Pocket
            Block.box((5.0D*X)+OX, (0.0D*Y)+OY, (4.0D*Z)+OZ, (4.0D*X)+OX, (8.0D*Y)+OY, (5.0D*Z)+OZ), //Right Strap
            Block.box((5.0D*X)+OX, (0.0D*Y)+OY, (11.0D*Z)+OZ, (4.0D*X)+OX, (8.0D*Y)+OY, (12.0D*Z)+OZ), //Left Strap
            Block.box((5.5D*X)+OX, (-2.0D*Y)+OY, (-1.0D*Z)+OZ, (9.5D*X)+OX, (8.0D*Y)+OY, (3.0D*Z)+OZ),
            Block.box((5.5D*X)+OX, (-2.0D*Y)+OY, (13.0D*Z)+OZ, (9.5D*X)+OX, (8.0D*Y)+OY, (17.0D*Z)+OZ)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    public TravelersBackpackBlock(Block.Properties builder)
    {
        super(builder.strength(1.0F, Float.MAX_VALUE).harvestLevel(0));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.getValue(FACING)) {
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
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if(worldIn.getBlockEntity(pos) instanceof TravelersBackpackTileEntity)
        {
            TravelersBackpackTileEntity te = (TravelersBackpackTileEntity)worldIn.getBlockEntity(pos);
            te.openGUI(player, te, pos);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void wasExploded(World worldIn, BlockPos pos, Explosion explosionIn) { }

    @Override
    public void onBlockExploded(final BlockState state, final World world, final BlockPos pos, final Explosion explosion) { return; }

    @Override
    public boolean canDropFromExplosion(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion)
    {
        return false;
    }

    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if(worldIn.getBlockEntity(pos) instanceof TravelersBackpackTileEntity && !worldIn.isClientSide())
        {
            TravelersBackpackTileEntity tileEntity = (TravelersBackpackTileEntity)worldIn.getBlockEntity(pos);

            if(player.isCreative() && tileEntity.hasData())
            {
                ItemStack stack = tileEntity.transferToItemStack(asItem().getDefaultInstance());
                ItemEntity itementity = new ItemEntity(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, stack);
                itementity.setDefaultPickUpDelay();
                worldIn.addFreshEntity(itementity);
            }

            if(state.getBlock() == ModBlocks.MELON_TRAVELERS_BACKPACK.get())
            {
                BackpackAbilities.melonAbility(tileEntity);
            }

            if(tileEntity.isSleepingBagDeployed())
            {
                Direction direction = state.getValue(FACING);
                worldIn.setBlockAndUpdate(pos.relative(direction), Blocks.AIR.defaultBlockState());
                worldIn.setBlockAndUpdate(pos.relative(direction).relative(direction), Blocks.AIR.defaultBlockState());
            }
        }

        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        ItemStack stack = new ItemStack(asItem(), 1);

        if(world.getBlockEntity(pos) instanceof TravelersBackpackTileEntity)
        {
            TravelersBackpackTileEntity te = (TravelersBackpackTileEntity)world.getBlockEntity(pos);
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

    // --- SPECIAL ABILITIES ---

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        super.animateTick(stateIn, worldIn, pos, rand);

        if(worldIn.getBlockEntity(pos) instanceof TravelersBackpackTileEntity)
        {
            BackpackAbilities.ABILITIES.animateTick(((TravelersBackpackTileEntity)worldIn.getBlockEntity(pos)), stateIn, worldIn, pos, rand);
        }
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos)
    {
        if(world.getBlockEntity(pos) instanceof TravelersBackpackTileEntity)
        {
            if(((TravelersBackpackTileEntity)world.getBlockEntity(pos)).getAbilityValue() && state.getBlock() == ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK.get())
            {
                return 5.0F;
            }
        }
        return super.getEnchantPowerBonus(state, world, pos);
    }

    @Override
    public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        if(blockAccess.getBlockEntity(pos) instanceof TravelersBackpackTileEntity)
        {
            if(((TravelersBackpackTileEntity)blockAccess.getBlockEntity(pos)).getAbilityValue() && blockState.getBlock() == ModBlocks.REDSTONE_TRAVELERS_BACKPACK.get())
            {
                return 15;
            }
        }
        return super.getSignal(blockState, blockAccess, pos, side);
    }

    @Override
    public boolean isSignalSource(BlockState state)
    {
        return state.getBlock() == ModBlocks.REDSTONE_TRAVELERS_BACKPACK.get();
    }

    @Override
    public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_)
    {
        if(!p_220082_4_.is(p_220082_1_.getBlock()) && p_220082_1_.getBlock() == ModBlocks.SPONGE_TRAVELERS_BACKPACK.get())
        {
            this.tryAbsorbWater(p_220082_2_, p_220082_3_);
        }
        super.onPlace(p_220082_1_, p_220082_2_, p_220082_3_, p_220082_4_, p_220082_5_);
    }

    @Override
    public void neighborChanged(BlockState state, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_)
    {
        if(state.getBlock() == ModBlocks.SPONGE_TRAVELERS_BACKPACK.get())
        {
            this.tryAbsorbWater(p_220069_2_, p_220069_3_);
        }
        super.neighborChanged(state, p_220069_2_, p_220069_3_, p_220069_4_, p_220069_5_, p_220069_6_);
    }

    public void tryAbsorbWater(World world, BlockPos pos)
    {
        if(world.getBlockEntity(pos) instanceof TravelersBackpackTileEntity)
        {
            TravelersBackpackTileEntity tile = (TravelersBackpackTileEntity)world.getBlockEntity(pos);

            if(tile.getAbilityValue() && ((tile.getLeftTank().isEmpty() || (tile.getLeftTank().getFluid().getFluid().isSame(Fluids.WATER) && tile.getLeftTank().getFluidAmount() < tile.getLeftTank().getCapacity())) || (tile.getRightTank().isEmpty() || (tile.getRightTank().getFluid().getFluid().isSame(Fluids.WATER) && tile.getRightTank().getFluidAmount() < tile.getRightTank().getCapacity()))))
            {
                if(this.removeWaterBreadthFirstSearch(world, pos, tile))
                {
                    world.levelEvent(2001, pos, Block.getId(Blocks.WATER.defaultBlockState()));
                }
            }
        }
    }

    private boolean removeWaterBreadthFirstSearch(World world, BlockPos pos, TravelersBackpackTileEntity tile) {
        Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(new Tuple<>(pos, 0));
        int i = 0;

        while(!queue.isEmpty()) {
            Tuple<BlockPos, Integer> tuple = queue.poll();
            BlockPos blockpos = tuple.getA();
            int j = tuple.getB();

            for(Direction direction : Direction.values()) {
                BlockPos blockpos1 = blockpos.relative(direction);
                BlockState blockstate = world.getBlockState(blockpos1);
                FluidState fluidstate = world.getFluidState(blockpos1);
                Material material = blockstate.getMaterial();
                if (fluidstate.is(FluidTags.WATER)) {
                    if (blockstate.getBlock() instanceof IBucketPickupHandler && ((IBucketPickupHandler)blockstate.getBlock()).takeLiquid(world, blockpos1, blockstate) != Fluids.EMPTY) {
                        ++i;

                        if(tile.getLeftTank().isEmpty() || (tile.getLeftTank().getFluid().getFluid().isSame(Fluids.WATER) && tile.getLeftTank().getFluidAmount() < tile.getLeftTank().getCapacity()))
                        {
                            tile.getLeftTank().fill(new FluidStack(Fluids.WATER, Reference.BUCKET), IFluidHandler.FluidAction.EXECUTE);
                        }
                        else
                        {
                            if(tile.getRightTank().isEmpty() || (tile.getRightTank().getFluid().getFluid().isSame(Fluids.WATER) && tile.getRightTank().getFluidAmount() < tile.getRightTank().getCapacity()))
                            {
                                tile.getRightTank().fill(new FluidStack(Fluids.WATER, Reference.BUCKET), IFluidHandler.FluidAction.EXECUTE);
                            }
                        }

                        if (j < 6) {
                            queue.add(new Tuple<>(blockpos1, j + 1));
                        }
                    } else if (blockstate.getBlock() instanceof FlowingFluidBlock) {
                        world.setBlock(blockpos1, Blocks.AIR.defaultBlockState(), 3);
                        ++i;
                        if (j < 6) {
                            queue.add(new Tuple<>(blockpos1, j + 1));
                        }
                    } else if (material == Material.WATER_PLANT || material == Material.REPLACEABLE_WATER_PLANT) {
                        TileEntity tileentity = blockstate.hasTileEntity() ? world.getBlockEntity(blockpos1) : null;
                        dropResources(blockstate, world, blockpos1, tileentity);
                        world.setBlock(blockpos1, Blocks.AIR.defaultBlockState(), 3);
                        ++i;
                        if (j < 6) {
                            queue.add(new Tuple<>(blockpos1, j + 1));
                        }
                    }
                }
            }

            if (i > 64) {
                break;
            }
        }

        return i > 0;
    }
}