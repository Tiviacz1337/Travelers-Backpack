package com.tiviacz.travelersbackpack.blocks;

import com.mojang.datafixers.util.Either;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

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
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    protected static final VoxelShape SLEEPING_BAG_EAST = Stream.of(
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(8.0D, 2.0D, 0.0D, 16.0D, 2.5D, 16.0D)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    protected static final VoxelShape SLEEPING_BAG_SOUTH = Stream.of(
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 2.0D, 8.0D, 16.0D, 2.5D, 16.0D)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    protected static final VoxelShape SLEEPING_BAG_WEST = Stream.of(
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 2.0D, 0.0D, 8.0D, 2.5D, 16.0D)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public SleepingBagBlock(DyeColor color, Properties properties)
    {
        super(color, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, BedPart.FOOT).setValue(OCCUPIED, Boolean.FALSE));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
    {
        return switch (state.getValue(PART)) {
            case FOOT -> SLEEPING_BAG;
            case HEAD -> switch (state.getValue(FACING)) {
                case EAST -> SLEEPING_BAG_EAST;
                case SOUTH -> SLEEPING_BAG_SOUTH;
                case WEST -> SLEEPING_BAG_WEST;
                default -> SLEEPING_BAG_NORTH;
            };
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if(level.isClientSide)
        {
            return InteractionResult.CONSUME;
        }
        else
        {
            if(state.getValue(PART) != BedPart.HEAD)
            {
                pos = pos.relative(state.getValue(FACING));
                state = level.getBlockState(pos);

                if(!state.is(this))
                {
                    return InteractionResult.CONSUME;
                }
            }

            if(!canSetSpawn(level))
            {
                level.removeBlock(pos, false);
                BlockPos var7 = pos.relative((state.getValue(FACING)).getOpposite());

                if(level.getBlockState(var7).is(this))
                {
                    level.removeBlock(var7, false);
                }

                //level.explode(null, DamageSource.badRespawnPointExplosion(), null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 5.0F, true, Explosion.BlockInteraction.DESTROY);
                return InteractionResult.SUCCESS;
            }
            else if(state.getValue(OCCUPIED))
            {
                if(!this.kickVillagerOutOfBed(level, pos))
                {
                    player.displayClientMessage(Component.translatable("block.minecraft.bed.occupied"), true);
                }

                return InteractionResult.SUCCESS;
            }
            else
            {
                if(player instanceof ServerPlayer serverPlayer)
                {
                    startSleepInBed(serverPlayer, pos).ifLeft((p_49477_) ->
                    {
                        if(p_49477_.getMessage() != null)
                        {
                            player.displayClientMessage(p_49477_.getMessage(), true);
                        }

                    });
                }
                return InteractionResult.SUCCESS;
            }
        }
    }

    public Either<Player.BedSleepingProblem, Unit> startSleepInBed(ServerPlayer player, BlockPos pos) {
        java.util.Optional<BlockPos> optAt = java.util.Optional.of(pos);
        ServerPlayer.BedSleepingProblem ret = net.minecraftforge.event.ForgeEventFactory.onPlayerSleepInBed(player, optAt);
        if (ret != null)
        {
            if(TravelersBackpackConfig.enableSleepingBagSpawnPoint) player.setRespawnPosition(player.level.dimension(), pos, player.getYRot(), true, true);
            return Either.left(ret);
        }
        Direction direction = player.level.getBlockState(pos).getValue(HorizontalDirectionalBlock.FACING);
        if (!player.isSleeping() && player.isAlive()) {
            if (!player.level.dimensionType().natural()) {
                if(TravelersBackpackConfig.enableSleepingBagSpawnPoint) player.setRespawnPosition(player.level.dimension(), pos, player.getYRot(), true, true);
                return Either.left(Player.BedSleepingProblem.NOT_POSSIBLE_HERE);
            } else if (!bedInRange(player, pos, direction)) {
                if(TravelersBackpackConfig.enableSleepingBagSpawnPoint) player.setRespawnPosition(player.level.dimension(), pos, player.getYRot(), true, true);
                return Either.left(Player.BedSleepingProblem.TOO_FAR_AWAY);
            } else if (bedBlocked(player, pos, direction)) {
                if(TravelersBackpackConfig.enableSleepingBagSpawnPoint) player.setRespawnPosition(player.level.dimension(), pos, player.getYRot(), true, true);
                return Either.left(Player.BedSleepingProblem.OBSTRUCTED);
            } else {
                if (!net.minecraftforge.event.ForgeEventFactory.fireSleepingTimeCheck(player, optAt)) {
                    if(TravelersBackpackConfig.enableSleepingBagSpawnPoint) player.setRespawnPosition(player.level.dimension(), pos, player.getYRot(), true, true);
                    return Either.left(Player.BedSleepingProblem.NOT_POSSIBLE_NOW);
                } else {
                    if (!player.isCreative()) {
                        double d0 = 8.0D;
                        double d1 = 5.0D;
                        Vec3 vector3d = Vec3.atBottomCenterOf(pos);
                        List<Monster> list = player.level.getEntitiesOfClass(Monster.class, new AABB(vector3d.x() - 8.0D, vector3d.y() - 5.0D, vector3d.z() - 8.0D, vector3d.x() + 8.0D, vector3d.y() + 5.0D, vector3d.z() + 8.0D), (p_241146_1_) -> {
                            return p_241146_1_.isPreventingPlayerRest(player);
                        });
                        if (!list.isEmpty()) {
                            if(TravelersBackpackConfig.enableSleepingBagSpawnPoint) player.setRespawnPosition(player.level.dimension(), pos, player.getYRot(), true, true);
                            return Either.left(Player.BedSleepingProblem.NOT_SAFE);
                        }
                    }
                    if(TravelersBackpackConfig.enableSleepingBagSpawnPoint)
                    {
                        Either<Player.BedSleepingProblem, Unit> either = player.startSleepInBed(pos).ifRight((p_241144_1_) -> {
                            player.awardStat(Stats.SLEEP_IN_BED);
                            CriteriaTriggers.SLEPT_IN_BED.trigger(player);
                        });
                        ((ServerLevel)player.level).updateSleepingPlayerList();
                        if(TravelersBackpackConfig.enableSleepingBagSpawnPoint) player.setRespawnPosition(player.level.dimension(), pos, player.getYRot(), true, true);
                        return either;
                    }
                    else
                    {
                        player.startSleeping(pos);
                        player.sleepCounter = 0;
                        player.awardStat(Stats.SLEEP_IN_BED);
                        CriteriaTriggers.SLEPT_IN_BED.trigger(player);
                        ((ServerLevel) player.level).updateSleepingPlayerList();
                        if(TravelersBackpackConfig.enableSleepingBagSpawnPoint) player.setRespawnPosition(player.level.dimension(), pos, player.getYRot(), true, true);
                        return Either.right(Unit.INSTANCE);
                    }
                }
            }
        } else {
            if(TravelersBackpackConfig.enableSleepingBagSpawnPoint) player.setRespawnPosition(player.level.dimension(), pos, player.getYRot(), true, true);
            return Either.left(Player.BedSleepingProblem.OTHER_PROBLEM);
        }
    }

    private boolean bedInRange(Player player, BlockPos pos, Direction direction) {
        if (direction == null) return false;
        return isReachableBedBlock(player, pos) || isReachableBedBlock(player, pos.relative(direction.getOpposite()));
    }

    private boolean isReachableBedBlock(Player player, BlockPos p_241158_1_) {
        Vec3 vector3d = Vec3.atBottomCenterOf(p_241158_1_);
        return Math.abs(player.getX() - vector3d.x()) <= 3.0D && Math.abs(player.getY() - vector3d.y()) <= 2.0D && Math.abs(player.getZ() - vector3d.z()) <= 3.0D;
    }

    private boolean bedBlocked(Player player, BlockPos pos, Direction direction) {
        BlockPos blockpos = pos.above();
        return !freeAt(player, blockpos) || !freeAt(player, blockpos.relative(direction.getOpposite()));
    }

    protected boolean freeAt(Player player, BlockPos pos) {
        return !player.level.getBlockState(pos).isSuffocating(player.level, pos);
    }

    private boolean kickVillagerOutOfBed(Level level, BlockPos pos)
    {
        List<Villager> var3 = level.getEntitiesOfClass(Villager.class, new AABB(pos), LivingEntity::isSleeping);
        if(var3.isEmpty())
        {
            return false;
        }
        else
        {
            var3.get(0).stopSleeping();
            return true;
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float factor) {
        super.fallOn(level, state, pos, entity, factor * 0.75F);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter getter, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(getter, entity);
        } else {
            this.bounceUp(entity);
        }

    }

    private void bounceUp(Entity entity)
    {
        Vec3 var2 = entity.getDeltaMovement();
        if(var2.y < 0.0D)
        {
            double var3 = entity instanceof LivingEntity ? 1.0D : 0.8D;
            entity.setDeltaMovement(var2.x, -var2.y * 0.3300000262260437D * var3, var2.z);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor accessor, BlockPos pos, BlockPos newPos)
    {
        if(direction == getNeighbourDirection(state.getValue(PART), state.getValue(FACING)))
        {
            return newState.is(this) && newState.getValue(PART) != state.getValue(PART) ? state.setValue(OCCUPIED, newState.getValue(OCCUPIED)) : Blocks.AIR.defaultBlockState();
        }
        else
        {
            return super.updateShape(state, direction, newState, accessor, pos, newPos);
        }
    }

    private static Direction getNeighbourDirection(BedPart part, Direction direction)
    {
        return part == BedPart.FOOT ? direction : direction.getOpposite();
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
    {
        boolean isFoot = state.getValue(PART) == BedPart.FOOT;

        BlockPos backpackPos = isFoot ? pos.relative(state.getValue(FACING).getOpposite()) : pos.relative(state.getValue(FACING).getOpposite(), 2);

        if(level.getBlockState(backpackPos).getBlock() instanceof TravelersBackpackBlock)
        {
            if(level.getBlockEntity(backpackPos) instanceof TravelersBackpackBlockEntity blockEntity)
            {
                blockEntity.setSleepingBagDeployed(false);
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, PART, OCCUPIED);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity livingEntity, ItemStack itemstack)
    {
        super.setPlacedBy(level, pos, state, livingEntity, itemstack);
        if(!level.isClientSide)
        {
            BlockPos var6 = pos.relative(state.getValue(FACING));
            level.setBlock(var6, state.setValue(PART, BedPart.HEAD), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    @Override
    public long getSeed(BlockState state, BlockPos pos)
    {
        BlockPos var3 = pos.relative(state.getValue(FACING), state.getValue(PART) == BedPart.HEAD ? 0 : 1);
        return Mth.getSeed(var3.getX(), pos.getY(), var3.getZ());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_152175_, BlockState p_152176_)
    {
        return null;
    }
}