package com.tiviacz.travelersbackpack.blocks;

import com.google.common.collect.Lists;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.material.Material;
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
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

public class TravelersBackpackBlock extends Block implements EntityBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

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
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape BACKPACK_SHAPE_SOUTH = Stream.of(
            Block.box((3.0D*X)+OX, (-1.0D*Y)+OY, (5.0D*Z)+OZ, (13.0D*X)+OX, (11.0D*Y)+OY, (10.0D*Z)+OZ), //Main
            Block.box((3.0D*X)+OX, (-2.0D*Y)+OY, (5.0D*Z)+OZ, (13.0D*X)+OX, (-1.0D*Y)+OY, (9.0D*Z)+OZ), //Main
            Block.box((4.0D*X)+OX, (1.08D*Y)+OY, (10.0D*Z)+OZ, (12.0D*X)+OX, (7.08D*Y)+OY, (12.0D*Z)+OZ), //Pocket
            Block.box((4.0D*X)+OX, (0.0D*Y)+OY, (4.0D*Z)+OZ, (5.0D*X)+OX, (8.0D*Y)+OY, (5.0D*Z)+OZ), //Right Strap
            Block.box((11.0D*X)+OX, (0.0D*Y)+OY, (4.0D*Z)+OZ, (12.0D*X)+OX, (8.0D*Y)+OY, (5.0D*Z)+OZ), //Left Strap
            Block.box((-1.0D*X)+OX, (-2.0D*Y)+OY, (5.5D*Z)+OZ, (3.0D*X)+OX, (8.0D*Y)+OY, (9.5D*Z)+OZ),
            Block.box((13.0D*X)+OX, (-2.0D*Y)+OY, (5.5D*Z)+OZ, (17.0D*X)+OX, (8.0D*Y)+OY, (9.5D*Z)+OZ)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape BACKPACK_SHAPE_WEST = Stream.of(
            Block.box((6.0D*X)+OX, (-1.0D*Y)+OY, (3.0D*Z)+OZ, (11.0D*X)+OX, (11.0D*Y)+OY, (13.0D*Z)+OZ), //Main
            Block.box((7.0D*X)+OX, (-2.0D*Y)+OY, (3.0D*Z)+OZ, (11.0D*X)+OX, (-1.0D*Y)+OY, (13.0D*Z)+OZ), //Main
            Block.box((4.0D*X)+OX, (1.08D*Y)+OY, (4.0D*Z)+OZ, (6.0D*X)+OX, (7.08D*Y)+OY, (12.0D*Z)+OZ), //Pocket
            Block.box((11.0D*X)+OX, (0.0D*Y)+OY, (4.0D*Z)+OZ, (12.0D*X)+OX, (8.0D*Y)+OY, (5.0D*Z)+OZ), //Right Strap
            Block.box((11.0D*X)+OX, (0.0D*Y)+OY, (11.0D*Z)+OZ, (12.0D*X)+OX, (8.0D*Y)+OY, (12.0D*Z)+OZ), //Left Strap
            Block.box((6.5D*X)+OX, (-2.0D*Y)+OY, (-1.0D*Z)+OZ, (10.5D*X)+OX, (8.0D*Y)+OY, (3.0D*Z)+OZ),
            Block.box((6.5D*X)+OX, (-2.0D*Y)+OY, (13.0D*Z)+OZ, (10.5D*X)+OX, (8.0D*Y)+OY, (17.0D*Z)+OZ)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape BACKPACK_SHAPE_EAST = Stream.of(
            Block.box((5.0D*X)+OX, (-1.0D*Y)+OY, (3.0D*Z)+OZ, (10.0D*X)+OX, (11.0D*Y)+OY, (13.0D*Z)+OZ), //Main
            Block.box((5.0D*X)+OX, (-2.0D*Y)+OY, (3.0D*Z)+OZ, (9.0D*X)+OX, (-1.0D*Y)+OY, (13.0D*Z)+OZ), //Main
            Block.box((10.0D*X)+OX, (1.08D*Y)+OY, (4.0D*Z)+OZ, (12.0D*X)+OX, (7.08D*Y)+OY, (12.0D*Z)+OZ), //Pocket
            Block.box((4.0D*X)+OX, (0.0D*Y)+OY, (4.0D*Z)+OZ, (5.0D*X)+OX, (8.0D*Y)+OY, (5.0D*Z)+OZ), //Right Strap
            Block.box((4.0D*X)+OX, (0.0D*Y)+OY, (11.0D*Z)+OZ, (5.0D*X)+OX, (8.0D*Y)+OY, (12.0D*Z)+OZ), //Left Strap
            Block.box((5.5D*X)+OX, (-2.0D*Y)+OY, (-1.0D*Z)+OZ, (9.5D*X)+OX, (8.0D*Y)+OY, (3.0D*Z)+OZ),
            Block.box((5.5D*X)+OX, (-2.0D*Y)+OY, (13.0D*Z)+OZ, (9.5D*X)+OX, (8.0D*Y)+OY, (17.0D*Z)+OZ)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();


    public TravelersBackpackBlock(Block.Properties builder)
    {
        super(builder.strength(1.0F, Float.MAX_VALUE));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
    {
        return switch (state.getValue(FACING)) {
            case SOUTH -> BACKPACK_SHAPE_SOUTH;
            case EAST -> BACKPACK_SHAPE_EAST;
            case WEST -> BACKPACK_SHAPE_WEST;
            default -> BACKPACK_SHAPE_NORTH;
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if(level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity)
        {
            if(TravelersBackpackConfig.enableBackpackBlockWearable)
            {
                if(player.isCrouching() && !level.isClientSide)
                {
                    if(!CapabilityUtils.isWearingBackpack(player))
                    {
                        if(!TravelersBackpack.enableCurios())
                        {
                            if(level.setBlock(pos, Blocks.AIR.defaultBlockState(), 7))
                            {
                                ItemStack stack = new ItemStack(asItem(), 1);
                                blockEntity.transferToItemStack(stack);
                                CapabilityUtils.equipBackpack(player, stack);

                                if(blockEntity.isSleepingBagDeployed())
                                {
                                    Direction bagDirection = state.getValue(TravelersBackpackBlock.FACING);
                                    level.setBlock(pos.relative(bagDirection), Blocks.AIR.defaultBlockState(), 3);
                                    level.setBlock(pos.relative(bagDirection).relative(bagDirection), Blocks.AIR.defaultBlockState(), 3);
                                }
                            }
                            else
                            {
                                player.sendMessage(new TranslatableComponent(Reference.FAIL), player.getUUID());
                            }
                        }
                        else
                        {
                            ItemStack stack = new ItemStack(asItem(), 1);
                            blockEntity.transferToItemStack(stack);

                            CuriosApi.getCuriosHelper().getCurio(stack).ifPresent(curio -> CuriosApi.getCuriosHelper().getCuriosHandler(player).ifPresent(handler ->
                            {
                                Map<String, ICurioStacksHandler> curios = handler.getCurios();
                                for(Map.Entry<String, ICurioStacksHandler> entry : curios.entrySet())
                                {
                                    IDynamicStackHandler stackHandler = entry.getValue().getStacks();
                                    for(int i = 0; i < stackHandler.getSlots(); i++)
                                    {
                                        ItemStack present = stackHandler.getStackInSlot(i);
                                        Set<String> tags = CuriosApi.getCuriosHelper().getCurioTags(stack.getItem());
                                        String id = entry.getKey();

                                        if(present.isEmpty() && ((tags.contains(id) || tags.contains(SlotTypePreset.CURIO.getIdentifier()))
                                                || (!tags.isEmpty() && id.equals(SlotTypePreset.CURIO.getIdentifier()))) && curio.canEquip(id, player))
                                        {
                                            if(level.setBlock(pos, Blocks.AIR.defaultBlockState(), 7))
                                            {
                                                stackHandler.setStackInSlot(i, stack.copy());
                                                player.level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, (1.0F + (player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.2F) * 0.7F);

                                                if(blockEntity.isSleepingBagDeployed())
                                                {
                                                    Direction bagDirection = state.getValue(TravelersBackpackBlock.FACING);
                                                    level.setBlockAndUpdate(pos.relative(bagDirection), Blocks.AIR.defaultBlockState());
                                                    level.setBlockAndUpdate(pos.relative(bagDirection).relative(bagDirection), Blocks.AIR.defaultBlockState());
                                                }
                                            }
                                        }
                                    }
                                }
                            }));
                        }
                    }
                    else
                    {
                        player.sendMessage(new TranslatableComponent(Reference.OTHER_BACKPACK), player.getUUID());
                    }
                }
                else
                {
                    blockEntity.openGUI(player, blockEntity, pos);
                }
            }
            else
            {
                blockEntity.openGUI(player, blockEntity, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) { return; }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity)
    {
        return false;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
    {
        if(level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity && !level.isClientSide)
        {
            blockEntity.drop(level, pos, asItem());

            if(blockEntity.isSleepingBagDeployed())
            {
                Direction direction = state.getValue(FACING);
                level.setBlockAndUpdate(pos.relative(direction), Blocks.AIR.defaultBlockState());
                level.setBlockAndUpdate(pos.relative(direction).relative(direction), Blocks.AIR.defaultBlockState());
            }
        }

        level.setBlock(pos, Blocks.AIR.defaultBlockState(), level.isClientSide ? 11 : 3);

        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player)
    {
        ItemStack stack = new ItemStack(asItem(), 1);

        if(world.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity)
        {
            blockEntity.transferToItemStack(stack);
            if(blockEntity.hasCustomName()) stack.setHoverName(blockEntity.getCustomName());
        }
        return stack;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new TravelersBackpackBlockEntity(pos, state);
    }

    //Special

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType)
    {
        return level.isClientSide || !BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, state.getBlock().asItem().getDefaultInstance()) ? null : BackpackUtils.getTicker(blockEntityType, ModBlockEntityTypes.TRAVELERS_BACKPACK.get(), TravelersBackpackBlockEntity::tick);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random rand)
    {
        super.animateTick(state, level, pos, rand);

        if(level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity)
        {
            BackpackAbilities.ABILITIES.animateTick(blockEntity, state, level, pos, rand);
        }
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos)
    {
        if(world.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity)
        {
            if(blockEntity.getAbilityValue() && state.getBlock() == ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK.get())
            {
                return 5.0F;
            }
        }
        return super.getEnchantPowerBonus(state, world, pos);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter getter, BlockPos pos, Direction direction)
    {
        if(getter.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity)
        {
            if(blockEntity.getAbilityValue() && state.getBlock() == ModBlocks.REDSTONE_TRAVELERS_BACKPACK.get())
            {
                return 15;
            }
        }
        return super.getSignal(state, getter, pos, direction);
    }

    @Override
    public boolean isSignalSource(BlockState state)
    {
        return state.getBlock() == ModBlocks.REDSTONE_TRAVELERS_BACKPACK.get();
    }

    @Override
    public void onPlace(BlockState p_220082_1_, Level p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_)
    {
        if(!p_220082_4_.is(p_220082_1_.getBlock()) && p_220082_1_.getBlock() == ModBlocks.SPONGE_TRAVELERS_BACKPACK.get())
        {
            this.tryAbsorbWater(p_220082_2_, p_220082_3_);
        }
        super.onPlace(p_220082_1_, p_220082_2_, p_220082_3_, p_220082_4_, p_220082_5_);
    }

    @Override
    public void neighborChanged(BlockState state, Level p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_)
    {
        if(state.getBlock() == ModBlocks.SPONGE_TRAVELERS_BACKPACK.get())
        {
            this.tryAbsorbWater(p_220069_2_, p_220069_3_);
        }
        super.neighborChanged(state, p_220069_2_, p_220069_3_, p_220069_4_, p_220069_5_, p_220069_6_);
    }

    public void tryAbsorbWater(Level level, BlockPos pos)
    {
        if(level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity)
        {
            if(blockEntity.getAbilityValue() && ((blockEntity.getLeftTank().isEmpty() || (blockEntity.getLeftTank().getFluid().getFluid().isSame(Fluids.WATER) && blockEntity.getLeftTank().getFluidAmount() < blockEntity.getLeftTank().getCapacity())) || (blockEntity.getRightTank().isEmpty() || (blockEntity.getRightTank().getFluid().getFluid().isSame(Fluids.WATER) && blockEntity.getRightTank().getFluidAmount() < blockEntity.getRightTank().getCapacity()))))
            {
                if(this.removeWaterBreadthFirstSearch(level, pos, blockEntity))
                {
                    level.levelEvent(2001, pos, Block.getId(Blocks.WATER.defaultBlockState()));
                }
            }
        }
    }

    private boolean removeWaterBreadthFirstSearch(Level p_56808_, BlockPos p_56809_, TravelersBackpackBlockEntity blockEntity) {
        Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(new Tuple<>(p_56809_, 0));
        int i = 0;

        while(!queue.isEmpty()) {
            Tuple<BlockPos, Integer> tuple = queue.poll();
            BlockPos blockpos = tuple.getA();
            int j = tuple.getB();

            for(Direction direction : Direction.values()) {
                BlockPos blockpos1 = blockpos.relative(direction);
                BlockState blockstate = p_56808_.getBlockState(blockpos1);
                FluidState fluidstate = p_56808_.getFluidState(blockpos1);
                Material material = blockstate.getMaterial();
                if (fluidstate.is(FluidTags.WATER)) {
                    if (blockstate.getBlock() instanceof BucketPickup && !((BucketPickup)blockstate.getBlock()).pickupBlock(p_56808_, blockpos1, blockstate).isEmpty()) {
                        ++i;
                        if(blockEntity.getLeftTank().isEmpty() || (blockEntity.getLeftTank().getFluid().getFluid().isSame(Fluids.WATER) && blockEntity.getLeftTank().getFluidAmount() < blockEntity.getLeftTank().getCapacity()))
                        {
                            blockEntity.getLeftTank().fill(new FluidStack(Fluids.WATER, Reference.BUCKET), IFluidHandler.FluidAction.EXECUTE);
                        }
                        else
                        {
                            if(blockEntity.getRightTank().isEmpty() || (blockEntity.getRightTank().getFluid().getFluid().isSame(Fluids.WATER) && blockEntity.getRightTank().getFluidAmount() < blockEntity.getRightTank().getCapacity()))
                            {
                                blockEntity.getRightTank().fill(new FluidStack(Fluids.WATER, Reference.BUCKET), IFluidHandler.FluidAction.EXECUTE);
                            }
                        }
                        if (j < 6) {
                            queue.add(new Tuple<>(blockpos1, j + 1));
                        }
                    } else if (blockstate.getBlock() instanceof LiquidBlock) {
                        p_56808_.setBlock(blockpos1, Blocks.AIR.defaultBlockState(), 3);
                        ++i;
                        if (j < 6) {
                            queue.add(new Tuple<>(blockpos1, j + 1));
                        }
                    } else if (material == Material.WATER_PLANT || material == Material.REPLACEABLE_WATER_PLANT) {
                        BlockEntity blockentity = blockstate.hasBlockEntity() ? p_56808_.getBlockEntity(blockpos1) : null;
                        dropResources(blockstate, p_56808_, blockpos1, blockentity);
                        p_56808_.setBlock(blockpos1, Blocks.AIR.defaultBlockState(), 3);
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