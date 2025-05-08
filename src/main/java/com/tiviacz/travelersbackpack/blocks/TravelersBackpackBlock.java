package com.tiviacz.travelersbackpack.blocks;

import com.google.common.collect.Lists;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.BackpackDeathHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.ShulkerBoxDispenseBehavior;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class TravelersBackpackBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public TravelersBackpackBlock(Properties builder) {
        super(builder.strength(1.0F, Float.MAX_VALUE).forceSolidOn().pushReaction(PushReaction.DESTROY));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        boolean hasTanks = false;
        if(getter.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
            hasTanks = backpackBlockEntity.getWrapper().tanksVisible();
        }
        return switch(state.getValue(FACING)) {
            case SOUTH -> hasTanks ? BACKPACK_TANKS_SHAPE_SOUTH : BACKPACK_SHAPE_SOUTH;
            case EAST -> hasTanks ? BACKPACK_TANKS_SHAPE_EAST : BACKPACK_SHAPE_EAST;
            case WEST -> hasTanks ? BACKPACK_TANKS_SHAPE_WEST : BACKPACK_SHAPE_WEST;
            default -> hasTanks ? BACKPACK_TANKS_SHAPE_NORTH : BACKPACK_SHAPE_NORTH;
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if(level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            ((BackpackBlockEntity)level.getBlockEntity(pos)).openBackpack(player, ((BackpackBlockEntity)level.getBlockEntity(pos)), pos);
            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected void onExplosionHit(BlockState pState, Level pLevel, BlockPos pPos, Explosion pExplosion, BiConsumer<ItemStack, BlockPos> pDropConsumer) {
        return; //Do nothing here
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if(level.getBlockEntity(pos) instanceof BackpackBlockEntity blockEntity) {
            if(state.getBlock() == ModBlocks.MELON_TRAVELERS_BACKPACK.get()) {
                BackpackAbilities.melonAbility(blockEntity);
            }
            if(player.isCreative()) {
                ItemStack stack = blockEntity.toItemStack(asItem().getDefaultInstance());
                ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, stack);
                itementity.setDefaultPickUpDelay();
                level.addFreshEntity(itementity);
            }
            blockEntity.removeSleepingBag(level, state.getValue(FACING));
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if(!state.is(newState.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            super.onRemove(state, level, pos, newState, isMoving);
            if(blockentity instanceof BackpackBlockEntity) {
                level.updateNeighbourForOutputSignal(pos, state.getBlock());
            }
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        if(level.getBlockEntity(pos) == null || !(level.getBlockEntity(pos) instanceof BackpackBlockEntity backpack)) {
            return 0;
        } else {
            float f = 0.0F;

            for(int i = 0; i < backpack.getWrapper().getStorage().getSlots(); i++) {
                ItemStack itemstack = backpack.getWrapper().getStorage().getStackInSlot(i);
                if(!itemstack.isEmpty()) {
                    f += (float)itemstack.getCount() / (float)Math.min(backpack.getWrapper().getStorage().getSlotLimit(i), backpack.getWrapper().getStorage().getStackInSlot(i).getMaxStackSize());
                }
            }

            f /= (float)backpack.getWrapper().getStorage().getSlots();
            return Mth.lerpDiscrete(f, 0, 15);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BackpackBlockEntity(pos, state);
    }

    //Special

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide || !TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get() || !BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, state.getBlock().asItem().getDefaultInstance()) ? null : BackpackDeathHelper.getTicker(blockEntityType, ModBlockEntityTypes.BACKPACK.get(), BackpackBlockEntity::tick);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        super.animateTick(state, level, pos, rand);
        if(level.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
            BackpackAbilities.ABILITIES.animateTick(backpackBlockEntity, state, level, pos, rand);
        }
    }

    @Override
    public int getSignal(BlockState state, BlockGetter getter, BlockPos pos, Direction direction) {
        if(state.getBlock() == ModBlocks.REDSTONE_TRAVELERS_BACKPACK.get()) {
            if(getter.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity && backpackBlockEntity.getWrapper().isAbilityEnabled()) {
                return 15;
            }
        }
        return super.getSignal(state, getter, pos, direction);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return state.getBlock() == ModBlocks.REDSTONE_TRAVELERS_BACKPACK.get();
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        if(!pOldState.is(pState.getBlock()) && pState.getBlock() == ModBlocks.SPONGE_TRAVELERS_BACKPACK.get()) {
            this.tryAbsorbWater(pLevel, pPos);
        }
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
    }

    @Override
    public void neighborChanged(BlockState state, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if(state.getBlock() == ModBlocks.SPONGE_TRAVELERS_BACKPACK.get()) {
            this.tryAbsorbWater(pLevel, pPos);
        }
        super.neighborChanged(state, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
    }

    public void tryAbsorbWater(Level level, BlockPos pos) {
        if(level.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
            backpackBlockEntity.getWrapper().getUpgradeManager().getUpgrade(TanksUpgrade.class).ifPresent(tanksUpgrade -> {
                if(backpackBlockEntity.getWrapper().isAbilityEnabled()) {
                    if((tanksUpgrade.getLeftTank().isEmpty() || (tanksUpgrade.getLeftTank().getFluid().getFluid().isSame(Fluids.WATER) && tanksUpgrade.getLeftTank().getFluidAmount() < tanksUpgrade.getLeftTank().getCapacity())) || (tanksUpgrade.getRightTank().isEmpty() || (tanksUpgrade.getRightTank().getFluid().getFluid().isSame(Fluids.WATER) && tanksUpgrade.getRightTank().getFluidAmount() < tanksUpgrade.getRightTank().getCapacity()))) {
                        if(this.removeWaterBreadthFirstSearch(level, pos, tanksUpgrade)) {
                            level.levelEvent(2001, pos, Block.getId(Blocks.WATER.defaultBlockState()));
                        }
                    }
                }
            });
        }
    }

    private boolean removeWaterBreadthFirstSearch(Level level, BlockPos pos, TanksUpgrade tanksUpgrade) {
        Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(new Tuple<>(pos, 0));
        int i = 0;

        while(!queue.isEmpty()) {
            Tuple<BlockPos, Integer> tuple = queue.poll();
            BlockPos blockpos = tuple.getA();
            int j = tuple.getB();

            for(Direction direction : Direction.values()) {
                BlockPos blockpos1 = blockpos.relative(direction);
                BlockState blockstate = level.getBlockState(blockpos1);
                FluidState fluidstate = level.getFluidState(blockpos1);
                if(fluidstate.is(FluidTags.WATER)) {
                    if(blockstate.getBlock() instanceof BucketPickup && !((BucketPickup)blockstate.getBlock()).pickupBlock(null, level, blockpos1, blockstate).isEmpty()) {
                        ++i;
                        if(tanksUpgrade.getLeftTank().isEmpty() || (tanksUpgrade.getLeftTank().getFluid().getFluid().isSame(Fluids.WATER) && tanksUpgrade.getLeftTank().getFluidAmount() < tanksUpgrade.getLeftTank().getCapacity())) {
                            tanksUpgrade.getLeftTank().fill(new FluidStack(Fluids.WATER, Reference.BUCKET), IFluidHandler.FluidAction.EXECUTE);
                        } else {
                            if(tanksUpgrade.getRightTank().isEmpty() || (tanksUpgrade.getRightTank().getFluid().getFluid().isSame(Fluids.WATER) && tanksUpgrade.getRightTank().getFluidAmount() < tanksUpgrade.getRightTank().getCapacity())) {
                                tanksUpgrade.getRightTank().fill(new FluidStack(Fluids.WATER, Reference.BUCKET), IFluidHandler.FluidAction.EXECUTE);
                            }
                        }
                        if(j < 6) {
                            queue.add(new Tuple<>(blockpos1, j + 1));
                        }
                    } else if(blockstate.getBlock() instanceof LiquidBlock) {
                        level.setBlock(blockpos1, Blocks.AIR.defaultBlockState(), 3);
                        ++i;
                        if(j < 6) {
                            queue.add(new Tuple<>(blockpos1, j + 1));
                        }
                    } else {

                        if(!blockstate.is(Blocks.KELP) && !blockstate.is(Blocks.KELP_PLANT) && !blockstate.is(Blocks.SEAGRASS) && !blockstate.is(Blocks.TALL_SEAGRASS)) {
                            return false;
                        }

                        BlockEntity blockentity = blockstate.hasBlockEntity() ? level.getBlockEntity(blockpos1) : null;
                        dropResources(blockstate, level, blockpos1, blockentity);
                        level.setBlock(blockpos1, Blocks.AIR.defaultBlockState(), 3);
                        ++i;
                        if(j < 6) {
                            queue.add(new Tuple<>(blockpos1, j + 1));
                        }
                    }
                }
            }

            if(i > 64) {
                break;
            }
        }

        return i > 0;
    }

    public static final VoxelShape BACKPACK_TANKS_SHAPE_NORTH = Stream.of(
            // MainBody
            Block.box(4.1, 0.8, 7.1, 11.9, 7.8, 11.0),
            // Top
            Block.box(4.1, 7.8, 7.1, 11.9, 10.1, 11.0),
            // Bottom
            Block.box(4.1, 0.0, 7.9, 11.9, 0.8, 11.0),
            // PocketFace
            Block.box(4.9, 2.4, 5.5, 11.1, 7.1, 7.1),
            // LeftStrap
            Block.box(10.4, 1.6, 11.0, 11.2, 7.8, 11.8),
            // RightStrap
            Block.box(4.8, 1.6, 11.0, 5.6, 7.8, 11.8),
            Block.box(1, 0, 7.4, 4.1, 7.8, 10.5),
            Block.box(11.9, 0, 7.4, 15, 7.8, 10.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape BACKPACK_TANKS_SHAPE_EAST = Stream.of(
            // MainBody
            Block.box(5.0, 0.8, 4.1, 8.9, 7.8, 11.9),
            // Top
            Block.box(5.0, 7.8, 4.1, 8.9, 10.1, 11.9),
            // Bottom
            Block.box(5.0, 0.0, 4.1, 5.9, 0.8, 11.9),
            // PocketFace
            Block.box(8.9, 2.4, 4.9, 10.5, 7.1, 11.1),
            // LeftStrap
            Block.box(4.2, 1.6, 4.8, 5.0, 7.8, 5.6),
            // RightStrap
            Block.box(4.2, 1.6, 10.4, 5.0, 7.8, 11.2),
            Block.box(5.5, 0, 1, 8.6, 7.8, 4.1),
            Block.box(5.5, 0, 11.9, 8.6, 7.8, 15)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape BACKPACK_TANKS_SHAPE_SOUTH = Stream.of(
            // MainBody
            Block.box(4.1, 0.8, 5.0, 11.9, 7.8, 8.9),
            // Top
            Block.box(4.1, 7.8, 5.0, 11.9, 10.1, 8.9),
            // Bottom
            Block.box(4.1, 0.0, 5.0, 11.9, 0.8, 8.1),
            // PocketFace
            Block.box(4.9, 2.4, 8.9, 11.1, 7.1, 10.5),
            // LeftStrap
            Block.box(4.8, 1.6, 4.2, 5.6, 7.8, 5.0),
            // RightStrap
            Block.box(10.4, 1.6, 4.2, 11.2, 7.8, 5.0),
            Block.box(1, 0, 5.5, 4.1, 7.8, 8.6),
            Block.box(11.9, 0, 5.5, 15, 7.8, 8.6)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape BACKPACK_TANKS_SHAPE_WEST = Stream.of(
            // MainBody
            Block.box(7.1, 0.8, 4.1, 11.0, 7.8, 11.9),
            // Top
            Block.box(7.1, 7.8, 4.1, 11.0, 10.1, 11.9),
            // Bottom
            Block.box(7.9, 0.0, 4.1, 11.0, 0.8, 11.9),
            // PocketFace
            Block.box(5.5, 2.4, 4.9, 7.1, 7.1, 11.1),
            // LeftStrap
            Block.box(11.0, 1.6, 10.4, 11.8, 7.8, 11.2),
            // RightStrap
            Block.box(11.0, 1.6, 4.8, 11.8, 7.8, 5.6),
            Block.box(7.4, 0, 1, 10.5, 7.8, 4.1),
            Block.box(7.4, 0, 11.9, 10.5, 7.8, 15)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape BACKPACK_SHAPE_NORTH = Stream.of(
            // MainBody
            Block.box(4.1, 0.8, 7.1, 11.9, 7.8, 11.0),
            // Top
            Block.box(4.1, 7.8, 7.1, 11.9, 10.1, 11.0),
            // Bottom
            Block.box(4.1, 0.0, 7.9, 11.9, 0.8, 11.0),
            // PocketFace
            Block.box(4.9, 2.4, 5.5, 11.1, 7.1, 7.1),
            // LeftStrap
            Block.box(10.4, 1.6, 11.0, 11.2, 7.8, 11.8),
            // RightStrap
            Block.box(4.8, 1.6, 11.0, 5.6, 7.8, 11.8)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape BACKPACK_SHAPE_EAST = Stream.of(
            // MainBody
            Block.box(5.0, 0.8, 4.1, 8.9, 7.8, 11.9),
            // Top
            Block.box(5.0, 7.8, 4.1, 8.9, 10.1, 11.9),
            // Bottom
            Block.box(5.0, 0.0, 4.1, 5.9, 0.8, 11.9),
            // PocketFace
            Block.box(8.9, 2.4, 4.9, 10.5, 7.1, 11.1),
            // LeftStrap
            Block.box(4.2, 1.6, 4.8, 5.0, 7.8, 5.6),
            // RightStrap
            Block.box(4.2, 1.6, 10.4, 5.0, 7.8, 11.2)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape BACKPACK_SHAPE_SOUTH = Stream.of(
            // MainBody
            Block.box(4.1, 0.8, 5.0, 11.9, 7.8, 8.9),
            // Top
            Block.box(4.1, 7.8, 5.0, 11.9, 10.1, 8.9),
            // Bottom
            Block.box(4.1, 0.0, 5.0, 11.9, 0.8, 8.1),
            // PocketFace
            Block.box(4.9, 2.4, 8.9, 11.1, 7.1, 10.5),
            // LeftStrap
            Block.box(4.8, 1.6, 4.2, 5.6, 7.8, 5.0),
            // RightStrap
            Block.box(10.4, 1.6, 4.2, 11.2, 7.8, 5.0)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape BACKPACK_SHAPE_WEST = Stream.of(
            // MainBody
            Block.box(7.1, 0.8, 4.1, 11.0, 7.8, 11.9),
            // Top
            Block.box(7.1, 7.8, 4.1, 11.0, 10.1, 11.9),
            // Bottom
            Block.box(7.9, 0.0, 4.1, 11.0, 0.8, 11.9),
            // PocketFace
            Block.box(5.5, 2.4, 4.9, 7.1, 7.1, 11.1),
            // LeftStrap
            Block.box(11.0, 1.6, 10.4, 11.8, 7.8, 11.2),
            // RightStrap
            Block.box(11.0, 1.6, 4.8, 11.8, 7.8, 5.6)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    //Forge

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) {
        return;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack stack = new ItemStack(asItem(), 1);
        if(level.getBlockEntity(pos) instanceof BackpackBlockEntity blockEntity) {
            blockEntity.toItemStack(stack);
        }
        return stack;
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos) {
        if(state.getBlock() == ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK.get()) {
            if(world.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity && backpackBlockEntity.getWrapper().isAbilityEnabled()) {
                return 5.0F;
            }
        }
        return super.getEnchantPowerBonus(state, world, pos);
    }

    public static void registerDispenserBehaviour() {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> DispenserBlock.registerBehavior(holder.get(), new ShulkerBoxDispenseBehavior()));
    }
}