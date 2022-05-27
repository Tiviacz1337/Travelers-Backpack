package com.tiviacz.travelersbackpack.blocks;

import com.mojang.datafixers.util.Either;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackTileEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public class SleepingBagBlock extends BedBlock
{
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
    protected static final VoxelShape SLEEPING_BAG = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

    protected static final VoxelShape SLEEPING_BAG_NORTH = Stream.of(
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 2.0D, 0.0D, 16.0D, 2.5D, 8.0D)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    protected static final VoxelShape SLEEPING_BAG_EAST = Stream.of(
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(8.0D, 2.0D, 0.0D, 16.0D, 2.5D, 16.0D)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    protected static final VoxelShape SLEEPING_BAG_SOUTH = Stream.of(
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 2.0D, 8.0D, 16.0D, 2.5D, 16.0D)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    protected static final VoxelShape SLEEPING_BAG_WEST = Stream.of(
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 2.0D, 0.0D, 8.0D, 2.5D, 16.0D)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    public SleepingBagBlock(Block.Properties properties)
    {
        super(DyeColor.RED, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, BedPart.FOOT).setValue(OCCUPIED, Boolean.FALSE));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        switch(state.getValue(PART))
        {
            case FOOT: return SLEEPING_BAG;
            case HEAD:
                switch(state.getValue(FACING))
                {
                    case EAST: return SLEEPING_BAG_EAST;
                    case SOUTH: return SLEEPING_BAG_SOUTH;
                    case WEST: return SLEEPING_BAG_WEST;
                    default: return SLEEPING_BAG_NORTH;
                }
        }
        return SLEEPING_BAG;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        if(world.isClientSide)
        {
            return ActionResultType.CONSUME;
        }
        else
        {
            if(state.getValue(PART) != BedPart.HEAD)
            {
                pos = pos.relative(state.getValue(FACING));
                state = world.getBlockState(pos);

                if(!state.is(this))
                {
                    return ActionResultType.CONSUME;
                }
            }

            if(!canSetSpawn(world))
            {
                world.removeBlock(pos, false);
                BlockPos blockpos = pos.relative(state.getValue(FACING).getOpposite());

                if(world.getBlockState(blockpos).is(this))
                {
                    world.removeBlock(blockpos, false);
                }

                //world.explode(null, DamageSource.badRespawnPointExplosion(), null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 5.0F, true, Explosion.Mode.DESTROY);
                return ActionResultType.SUCCESS;
            }
            else if(state.getValue(OCCUPIED))
            {
                if(!this.kickVillagerOutOfBed(world, pos))
                {
                    player.displayClientMessage(new TranslationTextComponent("block.minecraft.bed.occupied"), true);
                }

                return ActionResultType.SUCCESS;
            }
            else
            {
                if(player instanceof ServerPlayerEntity)
                {
                    startSleepInBed((ServerPlayerEntity)player, pos).ifLeft((sleepFailureReason) ->
                    {
                        if(sleepFailureReason != null)
                        {
                            player.displayClientMessage(sleepFailureReason.getMessage(), true);
                        }
                    });
                }
               /* BlockPos finalPos = pos;
                player.startSleepInBed(pos).ifLeft((result) ->
                {
                    if(result != null)
                    {
                        player.displayClientMessage(result.getMessage(), true);

                        if(result == PlayerEntity.SleepResult.NOT_POSSIBLE_NOW)
                        {
                            ((ServerPlayerEntity)player).setRespawnPosition(((ServerPlayerEntity)player).getRespawnDimension(), finalPos, 1.0F, true, false);
                        }
                    }

                }); */
                return ActionResultType.SUCCESS;
            }
        }
    }

    public Either<PlayerEntity.SleepResult, Unit> startSleepInBed(ServerPlayerEntity player, BlockPos pos) {
        java.util.Optional<BlockPos> optAt = java.util.Optional.of(pos);
        PlayerEntity.SleepResult ret = net.minecraftforge.event.ForgeEventFactory.onPlayerSleepInBed(player, optAt);
        if (ret != null)
        {
            if(TravelersBackpackConfig.enableSleepingBagSpawnPoint) player.setRespawnPosition(player.level.dimension(), pos, player.yRot, true, true);
            return Either.left(ret);
        }
        Direction direction = player.level.getBlockState(pos).getValue(HorizontalBlock.FACING);
        if (!player.isSleeping() && player.isAlive()) {
            if (!player.level.dimensionType().natural()) {
                if(TravelersBackpackConfig.enableSleepingBagSpawnPoint) player.setRespawnPosition(player.level.dimension(), pos, player.yRot, true, true);
                return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_HERE);
            } else if (!bedInRange(player, pos, direction)) {
                if(TravelersBackpackConfig.enableSleepingBagSpawnPoint) player.setRespawnPosition(player.level.dimension(), pos, player.yRot, true, true);
                return Either.left(PlayerEntity.SleepResult.TOO_FAR_AWAY);
            } else if (bedBlocked(player, pos, direction)) {
                if(TravelersBackpackConfig.enableSleepingBagSpawnPoint) player.setRespawnPosition(player.level.dimension(), pos, player.yRot, true, true);
                return Either.left(PlayerEntity.SleepResult.OBSTRUCTED);
            } else {
                if (!net.minecraftforge.event.ForgeEventFactory.fireSleepingTimeCheck(player, optAt)) {
                    if(TravelersBackpackConfig.enableSleepingBagSpawnPoint) player.setRespawnPosition(player.level.dimension(), pos, player.yRot, true, true);
                    return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_NOW);
                } else {
                    if (!player.isCreative()) {
                        double d0 = 8.0D;
                        double d1 = 5.0D;
                        Vector3d vector3d = Vector3d.atBottomCenterOf(pos);
                        List<MonsterEntity> list = player.level.getEntitiesOfClass(MonsterEntity.class, new AxisAlignedBB(vector3d.x() - 8.0D, vector3d.y() - 5.0D, vector3d.z() - 8.0D, vector3d.x() + 8.0D, vector3d.y() + 5.0D, vector3d.z() + 8.0D), (p_241146_1_) -> {
                            return p_241146_1_.isPreventingPlayerRest(player);
                        });
                        if (!list.isEmpty()) {
                            if(TravelersBackpackConfig.enableSleepingBagSpawnPoint) player.setRespawnPosition(player.level.dimension(), pos, player.yRot, true, true);
                            return Either.left(PlayerEntity.SleepResult.NOT_SAFE);
                        }
                    }
                    if(TravelersBackpackConfig.enableSleepingBagSpawnPoint)
                    {
                        Either<PlayerEntity.SleepResult, Unit> either = player.startSleepInBed(pos).ifRight((p_241144_1_) -> {
                            player.awardStat(Stats.SLEEP_IN_BED);
                            CriteriaTriggers.SLEPT_IN_BED.trigger(player);
                        });
                        ((ServerWorld)player.level).updateSleepingPlayerList();
                        if(TravelersBackpackConfig.enableSleepingBagSpawnPoint) player.setRespawnPosition(player.level.dimension(), pos, player.yRot, true, true);
                        return either;
                    }
                    else
                    {
                        player.startSleeping(pos);
                        player.sleepCounter = 0;
                        player.awardStat(Stats.SLEEP_IN_BED);
                        CriteriaTriggers.SLEPT_IN_BED.trigger(player);
                        ((ServerWorld) player.level).updateSleepingPlayerList();
                        if(TravelersBackpackConfig.enableSleepingBagSpawnPoint) player.setRespawnPosition(player.level.dimension(), pos, player.yRot, true, true);
                        return Either.right(Unit.INSTANCE);
                    }
                }
            }
        } else {
            if(TravelersBackpackConfig.enableSleepingBagSpawnPoint) player.setRespawnPosition(player.level.dimension(), pos, player.yRot, true, true);
            return Either.left(PlayerEntity.SleepResult.OTHER_PROBLEM);
        }
    }

    private boolean bedInRange(PlayerEntity player, BlockPos pos, Direction direction) {
        if (direction == null) return false;
        return isReachableBedBlock(player, pos) || isReachableBedBlock(player, pos.relative(direction.getOpposite()));
    }

    private boolean isReachableBedBlock(PlayerEntity player, BlockPos p_241158_1_) {
        Vector3d vector3d = Vector3d.atBottomCenterOf(p_241158_1_);
        return Math.abs(player.getX() - vector3d.x()) <= 3.0D && Math.abs(player.getY() - vector3d.y()) <= 2.0D && Math.abs(player.getZ() - vector3d.z()) <= 3.0D;
    }

    private boolean bedBlocked(PlayerEntity player, BlockPos pos, Direction direction) {
        BlockPos blockpos = pos.above();
        return !freeAt(player, blockpos) || !freeAt(player, blockpos.relative(direction.getOpposite()));
    }

    protected boolean freeAt(PlayerEntity player, BlockPos pos) {
        return !player.level.getBlockState(pos).isSuffocating(player.level, pos);
    }

    private boolean kickVillagerOutOfBed(World world, BlockPos pos)
    {
        List<VillagerEntity> list = world.getEntitiesOfClass(VillagerEntity.class, new AxisAlignedBB(pos), LivingEntity::isSleeping);

        if(list.isEmpty())
        {
            return false;
        }
        else
        {
            list.get(0).stopSleeping();
            return true;
        }
    }

    @Override
    public void fallOn(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
    {
        super.fallOn(worldIn, pos, entityIn, fallDistance * 0.75F);
    }

    @Override
    public void updateEntityAfterFallOn(IBlockReader worldIn, Entity entityIn)
    {
        if(entityIn.isSuppressingBounce())
        {
            super.updateEntityAfterFallOn(worldIn, entityIn);
        }
        else
        {
            this.bounceUp(entityIn);
        }

    }

    private void bounceUp(Entity entity)
    {
        Vector3d vec3d = entity.getDeltaMovement();
        if(vec3d.y < 0.0D)
        {
            double d0 = entity instanceof LivingEntity ? 1.0D : 0.8D;
            entity.setDeltaMovement(vec3d.x, -vec3d.y * (double) 0.33F * d0, vec3d.z);
        }
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if(facing == getNeighbourDirection(stateIn.getValue(PART), stateIn.getValue(FACING)))
        {
            return facingState.is(this) && facingState.getValue(PART) != stateIn.getValue(PART) ? stateIn.setValue(OCCUPIED, facingState.getValue(OCCUPIED)) : Blocks.AIR.defaultBlockState();
        }
        else
        {
            return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    private static Direction getNeighbourDirection(BedPart part, Direction direction)
    {
        return part == BedPart.FOOT ? direction : direction.getOpposite();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
        boolean isFoot = state.getValue(PART) == BedPart.FOOT;

        BlockPos backpackPos = isFoot ? pos.relative(state.getValue(FACING).getOpposite()) : pos.relative(state.getValue(FACING).getOpposite(), 2);

        if(worldIn.getBlockState(backpackPos).getBlock() instanceof TravelersBackpackBlock)
        {
            if(worldIn.getBlockEntity(backpackPos) instanceof TravelersBackpackTileEntity)
            {
                ((TravelersBackpackTileEntity)worldIn.getBlockEntity(backpackPos)).setSleepingBagDeployed(false);
            }
        }

        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        Direction direction = context.getHorizontalDirection();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(direction);
        return context.getLevel().getBlockState(blockpos1).canBeReplaced(context) ? this.defaultBlockState().setValue(FACING, direction) : null;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, PART, OCCUPIED);
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        super.setPlacedBy(worldIn, pos, state, placer, stack);

        if(!worldIn.isClientSide)
        {
            BlockPos blockpos = pos.relative(state.getValue(FACING));
            worldIn.setBlock(blockpos, state.setValue(PART, BedPart.HEAD), 3);
            worldIn.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(worldIn, pos, 3);
        }

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public long getSeed(BlockState state, BlockPos pos)
    {
        BlockPos blockpos = pos.relative(state.getValue(FACING), state.getValue(PART) == BedPart.HEAD ? 0 : 1);
        return MathHelper.getSeed(blockpos.getX(), pos.getY(), blockpos.getZ());
    }

 /*   @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
    {
        return false;
    } */

    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) { return null; }
}