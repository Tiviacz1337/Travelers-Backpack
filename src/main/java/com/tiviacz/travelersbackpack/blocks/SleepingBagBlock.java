package com.tiviacz.travelersbackpack.blocks;

import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
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

public class SleepingBagBlock extends BedBlock {
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
    public static final BooleanProperty CAN_DROP = BlockStateProperties.CONDITIONAL;
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

    public SleepingBagBlock(DyeColor color, Properties properties) {
        super(color, properties.forceSolidOn());
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, BedPart.FOOT).setValue(OCCUPIED, Boolean.FALSE).setValue(CAN_DROP, Boolean.TRUE));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return switch(state.getValue(PART)) {
            case FOOT -> SLEEPING_BAG;
            case HEAD -> switch(state.getValue(FACING)) {
                case EAST -> SLEEPING_BAG_EAST;
                case SOUTH -> SLEEPING_BAG_SOUTH;
                case WEST -> SLEEPING_BAG_WEST;
                default -> SLEEPING_BAG_NORTH;
            };
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if(pLevel.isClientSide) {
            return InteractionResult.CONSUME;
        } else {
            if(pState.getValue(PART) != BedPart.HEAD) {
                pPos = pPos.relative(pState.getValue(FACING));
                pState = pLevel.getBlockState(pPos);
                if(!pState.is(this)) {
                    return InteractionResult.CONSUME;
                }
            }

            if(!canSetSpawn(pLevel)) {
                pLevel.removeBlock(pPos, false);
                BlockPos blockpos = pPos.relative(pState.getValue(FACING).getOpposite());
                if(pLevel.getBlockState(blockpos).is(this)) {
                    pLevel.removeBlock(blockpos, false);
                }

                if(pLevel.getBlockEntity(pPos.relative(pState.getValue(FACING).getOpposite(), pState.getValue(PART) == BedPart.FOOT ? 1 : 2)) instanceof BackpackBlockEntity blockEntity) {
                    blockEntity.setSleepingBagDeployed(false);
                }

                //Vec3 vec3 = pPos.getCenter();
                //pLevel.explode(null, pLevel.damageSources().badRespawnPointExplosion(vec3), null, vec3, 5.0F, true, Level.ExplosionInteraction.BLOCK);
                return InteractionResult.SUCCESS;
            } else if(pState.getValue(OCCUPIED)) {
                if(!this.kickVillagerOutOfBed(pLevel, pPos)) {
                    pPlayer.displayClientMessage(Component.translatable("block.minecraft.bed.occupied"), true);
                }

                return InteractionResult.SUCCESS;
            } else {
                if(TravelersBackpackConfig.SERVER.backpackSettings.enableSleepingBagSpawnPoint.get()) {
                    if(pPlayer instanceof ServerPlayer serverPlayer) {
                        serverPlayer.setRespawnPosition(pLevel.dimension(), pPos, serverPlayer.getYRot(), true, true);
                    }
                }
                pPlayer.startSleepInBed(pPos).ifLeft(p_49477_ -> {
                    if(p_49477_.getMessage() != null) {
                        pPlayer.displayClientMessage(p_49477_.getMessage(), true);
                    }
                });
                return InteractionResult.SUCCESS;
            }
        }
    }

    private boolean kickVillagerOutOfBed(Level level, BlockPos pos) {
        List<Villager> var3 = level.getEntitiesOfClass(Villager.class, new AABB(pos), LivingEntity::isSleeping);
        if(var3.isEmpty()) {
            return false;
        } else {
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
        if(entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(getter, entity);
        } else {
            this.bounceUp(entity);
        }

    }

    private void bounceUp(Entity entity) {
        Vec3 var2 = entity.getDeltaMovement();
        if(var2.y < 0.0D) {
            double var3 = entity instanceof LivingEntity ? 1.0D : 0.8D;
            entity.setDeltaMovement(var2.x, -var2.y * 0.3300000262260437D * var3, var2.z);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor accessor, BlockPos pos, BlockPos newPos) {
        if(direction == getNeighbourDirection(state.getValue(PART), state.getValue(FACING))) {
            return newState.is(this) && newState.getValue(PART) != state.getValue(PART) ? state.setValue(OCCUPIED, newState.getValue(OCCUPIED)) : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(state, direction, newState, accessor, pos, newPos);
        }
    }

    private static Direction getNeighbourDirection(BedPart part, Direction direction) {
        return part == BedPart.FOOT ? direction : direction.getOpposite();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        boolean isFoot = state.getValue(PART) == BedPart.FOOT;

        BlockPos backpackPos = isFoot ? pos.relative(state.getValue(FACING).getOpposite()) : pos.relative(state.getValue(FACING).getOpposite(), 2);

        if(level.getBlockEntity(backpackPos) instanceof BackpackBlockEntity blockEntity) {
            blockEntity.setSleepingBagDeployed(false);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(direction);
        return context.getLevel().getBlockState(blockpos1).canBeReplaced(context) ? this.defaultBlockState().setValue(FACING, direction) : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, OCCUPIED, CAN_DROP);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity livingEntity, ItemStack itemstack) {
        super.setPlacedBy(level, pos, state, livingEntity, itemstack);
        if(!level.isClientSide) {
            BlockPos var6 = pos.relative(state.getValue(FACING));
            level.setBlock(var6, state.setValue(PART, BedPart.HEAD), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    @Override
    public long getSeed(BlockState state, BlockPos pos) {
        BlockPos var3 = pos.relative(state.getValue(FACING), state.getValue(PART) == BedPart.HEAD ? 0 : 1);
        return Mth.getSeed(var3.getX(), pos.getY(), var3.getZ());
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        if(!pState.getValue(CAN_DROP)) {
            return List.of();
        }
        return super.getDrops(pState, pParams);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_152175_, BlockState p_152176_) {
        return null;
    }
}