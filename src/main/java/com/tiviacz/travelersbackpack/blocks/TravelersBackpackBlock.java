package com.tiviacz.travelersbackpack.blocks;

import com.google.common.collect.Lists;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.stream.Stream;

public class TravelersBackpackBlock extends BlockWithEntity
{
    public static final DirectionProperty FACING;
    private static final VoxelShape BACKPACK_SHAPE_NORTH;
    private static final VoxelShape BACKPACK_SHAPE_SOUTH;
    private static final VoxelShape BACKPACK_SHAPE_EAST;
    private static final VoxelShape BACKPACK_SHAPE_WEST;

    private static final double X = (double)14/18;
    private static final double Y = (double)10/13;
    private static final double Z = (double)7/9;
    private static final double OX = 1.775;
    private static final double OY = 1.655;
    private static final double OZ = 1.778;

    public TravelersBackpackBlock(Settings settings)
    {
        super(settings.strength(1.0F, Float.MAX_VALUE));
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        switch(state.get(FACING))
        {
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if(world.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity)
        {
            TravelersBackpackBlockEntity blockEntity = (TravelersBackpackBlockEntity)world.getBlockEntity(pos);

            if(TravelersBackpackConfig.enableBackpackBlockQuickEquip)
            {
                if(player.isSneaking() && !world.isClient)
                {
                    if(!ComponentUtils.isWearingBackpack(player))
                    {
                        if(!TravelersBackpack.enableTrinkets())
                        {
                            if(world.setBlockState(pos, Blocks.AIR.getDefaultState(), 7))
                            {
                                ItemStack stack = new ItemStack(asItem(), 1);
                                blockEntity.transferToItemStack(stack);
                                ComponentUtils.equipBackpack(player, stack);

                                if(blockEntity.isSleepingBagDeployed())
                                {
                                    Direction bagDirection = state.get(TravelersBackpackBlock.FACING);
                                    world.setBlockState(pos.offset(bagDirection), Blocks.AIR.getDefaultState());
                                    world.setBlockState(pos.offset(bagDirection).offset(bagDirection), Blocks.AIR.getDefaultState());
                                }
                            }
                            else
                            {
                                player.sendMessage(Text.translatable(Reference.FAIL), false);
                            }
                            return ActionResult.SUCCESS;
                        }
                        else
                        {
                            player.sendMessage(Text.translatable(Reference.FAIL), false);
                            return ActionResult.SUCCESS;
                        }
                       /* else
                        {
                            ItemStack stack = new ItemStack(asItem(), 1);
                            blockEntity.transferToItemStack(stack);

                            if(world.setBlockState(pos, Blocks.AIR.getDefaultState(), 7) && TrinketsApi.getTrinketComponent(player).get().equip(stack))
                            {
                                player.world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (player.world.random.nextFloat() - player.world.random.nextFloat()) * 0.2F) * 0.7F);

                                if(blockEntity.isSleepingBagDeployed())
                                {
                                    Direction bagDirection = state.get(TravelersBackpackBlock.FACING);
                                    world.setBlockState(pos.offset(bagDirection), Blocks.AIR.getDefaultState());
                                    world.setBlockState(pos.offset(bagDirection).offset(bagDirection), Blocks.AIR.getDefaultState());
                                }
                                return ActionResult.SUCCESS;
                            }
                        } */
                    }
                    else
                    {
                        player.sendMessage(Text.translatable(Reference.OTHER_BACKPACK), false);
                        return ActionResult.SUCCESS;
                    }
                }
                else
                {
                    blockEntity.openHandledScreen(player);
                    return ActionResult.SUCCESS;
                }
            }
            else
            {
                blockEntity.openHandledScreen(player);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
    {
        ItemStack stack = new ItemStack(asItem(), 1);

        if(world.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity)
        {
            TravelersBackpackBlockEntity blockEntity = (TravelersBackpackBlockEntity)world.getBlockEntity(pos);
            blockEntity.transferToItemStack(stack);
            if(blockEntity.hasCustomName()) stack.setCustomName(blockEntity.getCustomName());
        }
        return stack;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if(world.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity && !world.isClient())
        {
            ((TravelersBackpackBlockEntity)world.getBlockEntity(pos)).drop(world, pos, asItem());

            if(((TravelersBackpackBlockEntity)world.getBlockEntity(pos)).isSleepingBagDeployed())
            {
                Direction direction = state.get(FACING);
                world.setBlockState(pos.offset(direction), Blocks.AIR.getDefaultState(), 3);
                world.setBlockState(pos.offset(direction).offset(direction), Blocks.AIR.getDefaultState(), 3);
            }
        }
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isClient ? 11 : 3);

        super.onBreak(world, pos, state, player);
    }

 /*   @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state)
    {
        BlockEntity te = world.getBlockEntity(pos);

        if(te instanceof TravelersBackpackBlockEntity && !world.isClient())
        {
            ((TravelersBackpackBlockEntity)te).drop(world, pos, asItem());

            if(((TravelersBackpackBlockEntity)te).isSleepingBagDeployed())
            {
                Direction direction = state.get(FACING);
                world.setBlockState(pos.offset(direction), Blocks.AIR.getDefaultState(), 3);
                world.setBlockState(pos.offset(direction).offset(direction), Blocks.AIR.getDefaultState(), 3);
            }
        }

        world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isClient() ? 11 : 3);

        super.onBroken(world, pos, state);
    } */

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        if(itemStack.getNbt() != null && world.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity)
        {
            ((TravelersBackpackBlockEntity)world.getBlockEntity(pos)).readAllData(itemStack.getNbt());
        }
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new TravelersBackpackBlockEntity(pos, state);
    }

    //Special

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return world.isClient ? null : checkType(type, ModBlockEntityTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY_TYPE, TravelersBackpackBlockEntity::tick);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random)
    {
        super.randomDisplayTick(state, world, pos, random);

        if(world.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity)
        {
            BackpackAbilities.ABILITIES.animateTick(((TravelersBackpackBlockEntity)world.getBlockEntity(pos)), state, world, pos, random);
        }
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {
        return state.getBlock() == ModBlocks.REDSTONE_TRAVELERS_BACKPACK ? 15 : super.getWeakRedstonePower(state, world, pos, direction);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state)
    {
        return state.getBlock() == ModBlocks.REDSTONE_TRAVELERS_BACKPACK;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        if(oldState.isOf(state.getBlock()) && state.getBlock() == ModBlocks.SPONGE_TRAVELERS_BACKPACK)
        {
            return;
        }
        this.update(world, pos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        if(state.getBlock() == ModBlocks.SPONGE_TRAVELERS_BACKPACK)
        {
            this.update(world, pos);
        }
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }

    public void update(World world, BlockPos pos)
    {
        if(world.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity)
        {
            TravelersBackpackBlockEntity blockEntity = (TravelersBackpackBlockEntity)world.getBlockEntity(pos);

            boolean leftTank = blockEntity.getLeftTank().isResourceBlank() || (blockEntity.getLeftTank().getResource().getFluid().matchesType(Fluids.WATER) && blockEntity.getLeftTank().getAmount() < blockEntity.getLeftTank().getCapacity());
            boolean rightTank = blockEntity.getRightTank().isResourceBlank() || (blockEntity.getRightTank().getResource().getFluid().matchesType(Fluids.WATER) && blockEntity.getRightTank().getAmount() < blockEntity.getRightTank().getCapacity());

            if(blockEntity.getAbilityValue() && (leftTank || rightTank))
            {
                if(this.absorbWater(world, pos, blockEntity))
                {
                    world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(Blocks.WATER.getDefaultState()));
                }
            }
        }
    }

    private boolean absorbWater(World world, BlockPos pos, TravelersBackpackBlockEntity blockEntity) {
        Queue<Pair<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(new Pair(pos, 0));
        int i = 0;

        while(!queue.isEmpty()) {
            Pair<BlockPos, Integer> pair = (Pair)queue.poll();
            BlockPos blockPos = (BlockPos)pair.getLeft();
            int j = (Integer)pair.getRight();
            Direction[] var8 = Direction.values();
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                Direction direction = var8[var10];
                BlockPos blockPos2 = blockPos.offset(direction);
                BlockState blockState = world.getBlockState(blockPos2);
                FluidState fluidState = world.getFluidState(blockPos2);
                Material material = blockState.getMaterial();
                if (fluidState.isIn(FluidTags.WATER)) {
                    if (blockState.getBlock() instanceof FluidDrainable && !((FluidDrainable)blockState.getBlock()).tryDrainFluid(world, blockPos2, blockState).isEmpty()) {
                        ++i;

                        if(blockEntity.getLeftTank().isResourceBlank() || (blockEntity.getLeftTank().getResource().getFluid().matchesType(Fluids.WATER) && blockEntity.getLeftTank().getAmount() < blockEntity.getLeftTank().getCapacity()))
                        {
                            try(Transaction transaction = Transaction.openOuter())
                            {
                                long amount = blockEntity.getLeftTank().insert(FluidVariant.of(Fluids.WATER), FluidConstants.BUCKET, transaction);

                                if(amount == FluidConstants.BUCKET)
                                {
                                    transaction.commit();
                                }
                            }
                        }
                        else
                        {
                            if(blockEntity.getRightTank().isResourceBlank() || (blockEntity.getRightTank().getResource().getFluid().matchesType(Fluids.WATER) && blockEntity.getRightTank().getAmount() < blockEntity.getRightTank().getCapacity()))
                            {
                                try(Transaction transaction = Transaction.openOuter())
                                {
                                    long amount = blockEntity.getRightTank().insert(FluidVariant.of(Fluids.WATER), FluidConstants.BUCKET, transaction);

                                    if(amount == FluidConstants.BUCKET)
                                    {
                                        transaction.commit();
                                    }
                                }
                            }
                        }

                        if (j < 6) {
                            queue.add(new Pair(blockPos2, j + 1));
                        }
                    } else if (blockState.getBlock() instanceof FluidBlock) {
                        world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 3);
                        ++i;
                        if (j < 6) {
                            queue.add(new Pair(blockPos2, j + 1));
                        }
                    } else if (material == Material.UNDERWATER_PLANT || material == Material.REPLACEABLE_UNDERWATER_PLANT) {
                        BlockEntity blockEntity2 = blockState.hasBlockEntity() ? world.getBlockEntity(blockPos2) : null;
                        dropStacks(blockState, world, blockPos2, blockEntity2);
                        world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 3);
                        ++i;
                        if (j < 6) {
                            queue.add(new Pair(blockPos2, j + 1));
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

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    static {
        FACING = HorizontalFacingBlock.FACING;
        BACKPACK_SHAPE_NORTH = Stream.of(
                Block.createCuboidShape((3.0D*X)+OX, (-1.0D*Y)+OY, (6.0D*Z)+OZ, (13.0D*X)+OX, (11.0D*Y)+OY, (11.0D*Z)+OZ), //Main
                Block.createCuboidShape((3.0D*X)+OX, (-2.0D*Y)+OY, (7.0D*Z)+OZ, (13.0D*X)+OX, (-1.0D*Y)+OY, (11.0D*Z)+OZ), //Main
                Block.createCuboidShape((4.0D*X)+OX, (1.08D*Y)+OY, (4.0D*Z)+OZ, (12.0D*X)+OX, (7.08D*Y)+OY, (6.0D*Z)+OZ), //Pocket
                Block.createCuboidShape((4.0D*X)+OX, (0.0D*Y)+OY, (11.0D*Z)+OZ, (5.0D*X)+OX, (8.0D*Y)+OY, (12.0D*Z)+OZ), //Right Strap
                Block.createCuboidShape((11.0D*X)+OX, (0.0D*Y)+OY, (11.0D*Z)+OZ, (12.0D*X)+OX, (8.0D*Y)+OY, (12.0D*Z)+OZ), //Left Strap
                Block.createCuboidShape((-1.0D*X)+OX, (-2.0D*Y)+OY, (6.5D*Z)+OZ, (3.0D*X)+OX, (8.0D*Y)+OY, (10.5D*Z)+OZ),
                Block.createCuboidShape((13.0D*X)+OX, (-2.0D*Y)+OY, (6.5D*Z)+OZ, (17.0D*X)+OX, (8.0D*Y)+OY, (10.5D*Z)+OZ)
        ).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, BooleanBiFunction.OR)).get();

        BACKPACK_SHAPE_SOUTH = Stream.of(
                Block.createCuboidShape((3.0D*X)+OX, (-1.0D*Y)+OY, (5.0D*Z)+OZ, (13.0D*X)+OX, (11.0D*Y)+OY, (10.0D*Z)+OZ), //Main
                Block.createCuboidShape((3.0D*X)+OX, (-2.0D*Y)+OY, (5.0D*Z)+OZ, (13.0D*X)+OX, (-1.0D*Y)+OY, (9.0D*Z)+OZ), //Main
                Block.createCuboidShape((4.0D*X)+OX, (1.08D*Y)+OY, (10.0D*Z)+OZ, (12.0D*X)+OX, (7.08D*Y)+OY, (12.0D*Z)+OZ), //Pocket
                Block.createCuboidShape((4.0D*X)+OX, (0.0D*Y)+OY, (4.0D*Z)+OZ, (5.0D*X)+OX, (8.0D*Y)+OY, (5.0D*Z)+OZ), //Right Strap
                Block.createCuboidShape((11.0D*X)+OX, (0.0D*Y)+OY, (4.0D*Z)+OZ, (12.0D*X)+OX, (8.0D*Y)+OY, (5.0D*Z)+OZ), //Left Strap
                Block.createCuboidShape((-1.0D*X)+OX, (-2.0D*Y)+OY, (5.5D*Z)+OZ, (3.0D*X)+OX, (8.0D*Y)+OY, (9.5D*Z)+OZ),
                Block.createCuboidShape((13.0D*X)+OX, (-2.0D*Y)+OY, (5.5D*Z)+OZ, (17.0D*X)+OX, (8.0D*Y)+OY, (9.5D*Z)+OZ)
        ).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, BooleanBiFunction.OR)).get();

        BACKPACK_SHAPE_WEST = Stream.of(
                Block.createCuboidShape((6.0D*X)+OX, (-1.0D*Y)+OY, (3.0D*Z)+OZ, (11.0D*X)+OX, (11.0D*Y)+OY, (13.0D*Z)+OZ), //Main
                Block.createCuboidShape((7.0D*X)+OX, (-2.0D*Y)+OY, (3.0D*Z)+OZ, (11.0D*X)+OX, (-1.0D*Y)+OY, (13.0D*Z)+OZ), //Main
                Block.createCuboidShape((4.0D*X)+OX, (1.08D*Y)+OY, (4.0D*Z)+OZ, (6.0D*X)+OX, (7.08D*Y)+OY, (12.0D*Z)+OZ), //Pocket
                Block.createCuboidShape((11.0D*X)+OX, (0.0D*Y)+OY, (4.0D*Z)+OZ, (12.0D*X)+OX, (8.0D*Y)+OY, (5.0D*Z)+OZ), //Right Strap
                Block.createCuboidShape((11.0D*X)+OX, (0.0D*Y)+OY, (11.0D*Z)+OZ, (12.0D*X)+OX, (8.0D*Y)+OY, (12.0D*Z)+OZ), //Left Strap
                Block.createCuboidShape((6.5D*X)+OX, (-2.0D*Y)+OY, (-1.0D*Z)+OZ, (10.5D*X)+OX, (8.0D*Y)+OY, (3.0D*Z)+OZ),
                Block.createCuboidShape((6.5D*X)+OX, (-2.0D*Y)+OY, (13.0D*Z)+OZ, (10.5D*X)+OX, (8.0D*Y)+OY, (17.0D*Z)+OZ)
        ).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, BooleanBiFunction.OR)).get();

        BACKPACK_SHAPE_EAST = Stream.of(
                Block.createCuboidShape((5.0D*X)+OX, (-1.0D*Y)+OY, (3.0D*Z)+OZ, (10.0D*X)+OX, (11.0D*Y)+OY, (13.0D*Z)+OZ), //Main
                Block.createCuboidShape((5.0D*X)+OX, (-2.0D*Y)+OY, (3.0D*Z)+OZ, (9.0D*X)+OX, (-1.0D*Y)+OY, (13.0D*Z)+OZ), //Main
                Block.createCuboidShape((10.0D*X)+OX, (1.08D*Y)+OY, (4.0D*Z)+OZ, (12.0D*X)+OX, (7.08D*Y)+OY, (12.0D*Z)+OZ), //Pocket
                Block.createCuboidShape((4.0D*X)+OX, (0.0D*Y)+OY, (4.0D*Z)+OZ, (5.0D*X)+OX, (8.0D*Y)+OY, (5.0D*Z)+OZ), //Right Strap
                Block.createCuboidShape((4.0D*X)+OX, (0.0D*Y)+OY, (11.0D*Z)+OZ, (5.0D*X)+OX, (8.0D*Y)+OY, (12.0D*Z)+OZ), //Left Strap
                Block.createCuboidShape((5.5D*X)+OX, (-2.0D*Y)+OY, (-1.0D*Z)+OZ, (9.5D*X)+OX, (8.0D*Y)+OY, (3.0D*Z)+OZ),
                Block.createCuboidShape((5.5D*X)+OX, (-2.0D*Y)+OY, (13.0D*Z)+OZ, (9.5D*X)+OX, (8.0D*Y)+OY, (17.0D*Z)+OZ)
        ).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, BooleanBiFunction.OR)).get();
    }
}