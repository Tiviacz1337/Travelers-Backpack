package com.tiviacz.travelersbackpack.blocks;

import com.google.common.collect.Lists;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
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
import net.minecraft.world.InteractionHand;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.Queue;
import java.util.stream.Stream;

public class TravelersBackpackBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public TravelersBackpackBlock(Properties builder) {
        super(builder.strength(1.0F, Float.MAX_VALUE).forceSolidOn().pushReaction(PushReaction.DESTROY));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
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
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if(level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            ((BackpackBlockEntity)level.getBlockEntity(pos)).openBackpack(player, ((BackpackBlockEntity)level.getBlockEntity(pos)), pos);
            return InteractionResult.CONSUME;
        }
    }

    /*@Override
    protected void onExplosionHit(BlockState pState, Level pLevel, BlockPos pPos, Explosion pExplosion, BiConsumer<ItemStack, BlockPos> pDropConsumer) {
        return; //Do nothing here
    } */

    @Override
    public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) {
        return;
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
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
        super.playerWillDestroy(level, pos, state, player);
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
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if(!state.is(newState.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            super.onRemove(state, level, pos, newState, isMoving);
            if(blockentity instanceof BackpackBlockEntity) {
                level.updateNeighbourForOutputSignal(pos, state.getBlock());
            }
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        if(level.getBlockEntity(pos) == null || !(level.getBlockEntity(pos) instanceof BackpackBlockEntity backpack)) {
            return 0;
        } else {
            int i = 0;
            float f = 0.0F;

            for(int j = 0; j < backpack.getWrapper().getStorage().getSlots(); j++) {
                ItemStack itemstack = backpack.getWrapper().getStorage().getStackInSlot(j);
                if(!itemstack.isEmpty()) {
                    f += (float)itemstack.getCount() / (float)Math.min(backpack.getWrapper().getStorage().getSlotLimit(j), backpack.getWrapper().getStorage().getStackInSlot(j).getMaxStackSize());
                    i++;
                }
            }

            f /= (float)backpack.getWrapper().getStorage().getSlots();
            return Mth.floor(f * 14.0F) + (i > 0 ? 1 : 0);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        ItemStack stack = new ItemStack(asItem(), 1);
        if(world.getBlockEntity(pos) instanceof BackpackBlockEntity blockEntity) {
            blockEntity.toItemStack(stack);
        }
        return stack;
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
    public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos) {
        if(state.getBlock() == ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK.get()) {
            if(world.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity && backpackBlockEntity.getWrapper().isAbilityEnabled()) {
                return 5.0F;
            }
        }
        return super.getEnchantPowerBonus(state, world, pos);
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
            BackpackWrapper wrapper = backpackBlockEntity.getWrapper();
            if(wrapper.getUpgradeManager().tanksUpgrade.isPresent() && wrapper.isAbilityEnabled()) {
                TanksUpgrade tanksUpgrade = wrapper.getUpgradeManager().tanksUpgrade.get();
                if((tanksUpgrade.getLeftTank().isEmpty() || (tanksUpgrade.getLeftTank().getFluid().getFluid().isSame(Fluids.WATER) && tanksUpgrade.getLeftTank().getFluidAmount() < tanksUpgrade.getLeftTank().getCapacity())) || (tanksUpgrade.getRightTank().isEmpty() || (tanksUpgrade.getRightTank().getFluid().getFluid().isSame(Fluids.WATER) && tanksUpgrade.getRightTank().getFluidAmount() < tanksUpgrade.getRightTank().getCapacity()))) {
                    if(this.removeWaterBreadthFirstSearch(level, pos, tanksUpgrade)) {
                        level.levelEvent(2001, pos, Block.getId(Blocks.WATER.defaultBlockState()));
                    }
                }
            }
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
                    if(blockstate.getBlock() instanceof BucketPickup && !((BucketPickup)blockstate.getBlock()).pickupBlock(level, blockpos1, blockstate).isEmpty()) {
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

    public static void registerDispenserBehaviour() {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> DispenserBlock.registerBehavior(holder.get(), new ShulkerBoxDispenseBehavior()));
    }

    private static final double X = (double)14 / 18;
    private static final double Y = (double)10 / 13;
    private static final double Z = (double)7 / 9;
    private static final double OX = 1.775;
    private static final double OY = 1.655;
    private static final double OZ = 1.778;

    private static final VoxelShape BACKPACK_TANKS_SHAPE_NORTH = Stream.of(
            Block.box((3.0D * X) + OX, (-1.0D * Y) + OY, (6.0D * Z) + OZ, (13.0D * X) + OX, (11.0D * Y) + OY, (11.0D * Z) + OZ), //Main
            Block.box((3.0D * X) + OX, (-2.0D * Y) + OY, (7.0D * Z) + OZ, (13.0D * X) + OX, (-1.0D * Y) + OY, (11.0D * Z) + OZ), //Main
            Block.box((4.0D * X) + OX, (1.08D * Y) + OY, (4.0D * Z) + OZ, (12.0D * X) + OX, (7.08D * Y) + OY, (6.0D * Z) + OZ), //Pocket
            Block.box((4.0D * X) + OX, (0.0D * Y) + OY, (11.0D * Z) + OZ, (5.0D * X) + OX, (8.0D * Y) + OY, (12.0D * Z) + OZ), //Right Strap
            Block.box((11.0D * X) + OX, (0.0D * Y) + OY, (11.0D * Z) + OZ, (12.0D * X) + OX, (8.0D * Y) + OY, (12.0D * Z) + OZ), //Left Strap
            Block.box((-1.0D * X) + OX, (-2.0D * Y) + OY, (6.5D * Z) + OZ, (3.0D * X) + OX, (8.0D * Y) + OY, (10.5D * Z) + OZ),
            Block.box((13.0D * X) + OX, (-2.0D * Y) + OY, (6.5D * Z) + OZ, (17.0D * X) + OX, (8.0D * Y) + OY, (10.5D * Z) + OZ)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape BACKPACK_TANKS_SHAPE_SOUTH = Stream.of(
            Block.box((3.0D * X) + OX, (-1.0D * Y) + OY, (5.0D * Z) + OZ, (13.0D * X) + OX, (11.0D * Y) + OY, (10.0D * Z) + OZ), //Main
            Block.box((3.0D * X) + OX, (-2.0D * Y) + OY, (5.0D * Z) + OZ, (13.0D * X) + OX, (-1.0D * Y) + OY, (9.0D * Z) + OZ), //Main
            Block.box((4.0D * X) + OX, (1.08D * Y) + OY, (10.0D * Z) + OZ, (12.0D * X) + OX, (7.08D * Y) + OY, (12.0D * Z) + OZ), //Pocket
            Block.box((4.0D * X) + OX, (0.0D * Y) + OY, (4.0D * Z) + OZ, (5.0D * X) + OX, (8.0D * Y) + OY, (5.0D * Z) + OZ), //Right Strap
            Block.box((11.0D * X) + OX, (0.0D * Y) + OY, (4.0D * Z) + OZ, (12.0D * X) + OX, (8.0D * Y) + OY, (5.0D * Z) + OZ), //Left Strap
            Block.box((-1.0D * X) + OX, (-2.0D * Y) + OY, (5.5D * Z) + OZ, (3.0D * X) + OX, (8.0D * Y) + OY, (9.5D * Z) + OZ),
            Block.box((13.0D * X) + OX, (-2.0D * Y) + OY, (5.5D * Z) + OZ, (17.0D * X) + OX, (8.0D * Y) + OY, (9.5D * Z) + OZ)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape BACKPACK_TANKS_SHAPE_WEST = Stream.of(
            Block.box((6.0D * X) + OX, (-1.0D * Y) + OY, (3.0D * Z) + OZ, (11.0D * X) + OX, (11.0D * Y) + OY, (13.0D * Z) + OZ), //Main
            Block.box((7.0D * X) + OX, (-2.0D * Y) + OY, (3.0D * Z) + OZ, (11.0D * X) + OX, (-1.0D * Y) + OY, (13.0D * Z) + OZ), //Main
            Block.box((4.0D * X) + OX, (1.08D * Y) + OY, (4.0D * Z) + OZ, (6.0D * X) + OX, (7.08D * Y) + OY, (12.0D * Z) + OZ), //Pocket
            Block.box((11.0D * X) + OX, (0.0D * Y) + OY, (4.0D * Z) + OZ, (12.0D * X) + OX, (8.0D * Y) + OY, (5.0D * Z) + OZ), //Right Strap
            Block.box((11.0D * X) + OX, (0.0D * Y) + OY, (11.0D * Z) + OZ, (12.0D * X) + OX, (8.0D * Y) + OY, (12.0D * Z) + OZ), //Left Strap
            Block.box((6.5D * X) + OX, (-2.0D * Y) + OY, (-1.0D * Z) + OZ, (10.5D * X) + OX, (8.0D * Y) + OY, (3.0D * Z) + OZ),
            Block.box((6.5D * X) + OX, (-2.0D * Y) + OY, (13.0D * Z) + OZ, (10.5D * X) + OX, (8.0D * Y) + OY, (17.0D * Z) + OZ)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape BACKPACK_TANKS_SHAPE_EAST = Stream.of(
            Block.box((5.0D * X) + OX, (-1.0D * Y) + OY, (3.0D * Z) + OZ, (10.0D * X) + OX, (11.0D * Y) + OY, (13.0D * Z) + OZ), //Main
            Block.box((5.0D * X) + OX, (-2.0D * Y) + OY, (3.0D * Z) + OZ, (9.0D * X) + OX, (-1.0D * Y) + OY, (13.0D * Z) + OZ), //Main
            Block.box((10.0D * X) + OX, (1.08D * Y) + OY, (4.0D * Z) + OZ, (12.0D * X) + OX, (7.08D * Y) + OY, (12.0D * Z) + OZ), //Pocket
            Block.box((4.0D * X) + OX, (0.0D * Y) + OY, (4.0D * Z) + OZ, (5.0D * X) + OX, (8.0D * Y) + OY, (5.0D * Z) + OZ), //Right Strap
            Block.box((4.0D * X) + OX, (0.0D * Y) + OY, (11.0D * Z) + OZ, (5.0D * X) + OX, (8.0D * Y) + OY, (12.0D * Z) + OZ), //Left Strap
            Block.box((5.5D * X) + OX, (-2.0D * Y) + OY, (-1.0D * Z) + OZ, (9.5D * X) + OX, (8.0D * Y) + OY, (3.0D * Z) + OZ),
            Block.box((5.5D * X) + OX, (-2.0D * Y) + OY, (13.0D * Z) + OZ, (9.5D * X) + OX, (8.0D * Y) + OY, (17.0D * Z) + OZ)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape BACKPACK_SHAPE_NORTH = Stream.of(
            Block.box((3.0D * X) + OX, (-1.0D * Y) + OY, (6.0D * Z) + OZ, (13.0D * X) + OX, (11.0D * Y) + OY, (11.0D * Z) + OZ), //Main
            Block.box((3.0D * X) + OX, (-2.0D * Y) + OY, (7.0D * Z) + OZ, (13.0D * X) + OX, (-1.0D * Y) + OY, (11.0D * Z) + OZ), //Main
            Block.box((4.0D * X) + OX, (1.08D * Y) + OY, (4.0D * Z) + OZ, (12.0D * X) + OX, (7.08D * Y) + OY, (6.0D * Z) + OZ), //Pocket
            Block.box((4.0D * X) + OX, (0.0D * Y) + OY, (11.0D * Z) + OZ, (5.0D * X) + OX, (8.0D * Y) + OY, (12.0D * Z) + OZ), //Right Strap
            Block.box((11.0D * X) + OX, (0.0D * Y) + OY, (11.0D * Z) + OZ, (12.0D * X) + OX, (8.0D * Y) + OY, (12.0D * Z) + OZ) //Left Strap
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape BACKPACK_SHAPE_SOUTH = Stream.of(
            Block.box((3.0D * X) + OX, (-1.0D * Y) + OY, (5.0D * Z) + OZ, (13.0D * X) + OX, (11.0D * Y) + OY, (10.0D * Z) + OZ), //Main
            Block.box((3.0D * X) + OX, (-2.0D * Y) + OY, (5.0D * Z) + OZ, (13.0D * X) + OX, (-1.0D * Y) + OY, (9.0D * Z) + OZ), //Main
            Block.box((4.0D * X) + OX, (1.08D * Y) + OY, (10.0D * Z) + OZ, (12.0D * X) + OX, (7.08D * Y) + OY, (12.0D * Z) + OZ), //Pocket
            Block.box((4.0D * X) + OX, (0.0D * Y) + OY, (4.0D * Z) + OZ, (5.0D * X) + OX, (8.0D * Y) + OY, (5.0D * Z) + OZ), //Right Strap
            Block.box((11.0D * X) + OX, (0.0D * Y) + OY, (4.0D * Z) + OZ, (12.0D * X) + OX, (8.0D * Y) + OY, (5.0D * Z) + OZ) //Left Strap
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape BACKPACK_SHAPE_WEST = Stream.of(
            Block.box((6.0D * X) + OX, (-1.0D * Y) + OY, (3.0D * Z) + OZ, (11.0D * X) + OX, (11.0D * Y) + OY, (13.0D * Z) + OZ), //Main
            Block.box((7.0D * X) + OX, (-2.0D * Y) + OY, (3.0D * Z) + OZ, (11.0D * X) + OX, (-1.0D * Y) + OY, (13.0D * Z) + OZ), //Main
            Block.box((4.0D * X) + OX, (1.08D * Y) + OY, (4.0D * Z) + OZ, (6.0D * X) + OX, (7.08D * Y) + OY, (12.0D * Z) + OZ), //Pocket
            Block.box((11.0D * X) + OX, (0.0D * Y) + OY, (4.0D * Z) + OZ, (12.0D * X) + OX, (8.0D * Y) + OY, (5.0D * Z) + OZ), //Right Strap
            Block.box((11.0D * X) + OX, (0.0D * Y) + OY, (11.0D * Z) + OZ, (12.0D * X) + OX, (8.0D * Y) + OY, (12.0D * Z) + OZ) //Left Strap
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape BACKPACK_SHAPE_EAST = Stream.of(
            Block.box((5.0D * X) + OX, (-1.0D * Y) + OY, (3.0D * Z) + OZ, (10.0D * X) + OX, (11.0D * Y) + OY, (13.0D * Z) + OZ), //Main
            Block.box((5.0D * X) + OX, (-2.0D * Y) + OY, (3.0D * Z) + OZ, (9.0D * X) + OX, (-1.0D * Y) + OY, (13.0D * Z) + OZ), //Main
            Block.box((10.0D * X) + OX, (1.08D * Y) + OY, (4.0D * Z) + OZ, (12.0D * X) + OX, (7.08D * Y) + OY, (12.0D * Z) + OZ), //Pocket
            Block.box((4.0D * X) + OX, (0.0D * Y) + OY, (4.0D * Z) + OZ, (5.0D * X) + OX, (8.0D * Y) + OY, (5.0D * Z) + OZ), //Right Strap
            Block.box((4.0D * X) + OX, (0.0D * Y) + OY, (11.0D * Z) + OZ, (5.0D * X) + OX, (8.0D * Y) + OY, (12.0D * Z) + OZ) //Left Strap
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
}