package com.tiviacz.travelersbackpack.blocks;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

public class TravelersBackpackBlock extends Block implements EntityBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    //private static final VoxelShape BACKPACK_SHAPE_NORTH = Block.box(1.0D, 0.0D, 4.0D, 15.0D, 10.0D, 12.0D);
    //private static final VoxelShape BACKPACK_SHAPE_SOUTH = Block.box(1.0D, 0.0D, 4.0D, 15.0D, 10.0D, 12.0D);
    //private static final VoxelShape BACKPACK_SHAPE_EAST = Block.box(4.0D, 0.0D, 1.0D, 12.0D, 10.0D, 15.0D);
    //private static final VoxelShape BACKPACK_SHAPE_WEST = Block.box(4.0D, 0.0D, 1.0D, 12.0D, 10.0D, 15.0D);

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
        if(level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity)
        {
            TravelersBackpackBlockEntity blockEntity = (TravelersBackpackBlockEntity)level.getBlockEntity(pos);

            if(TravelersBackpackConfig.SERVER.enableBackpackBlockWearable.get())
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
                           // player.sendMessage(new TranslatableComponent(Reference.FAIL), player.getUUID());
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
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
    {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if(blockEntity instanceof TravelersBackpackBlockEntity && !level.isClientSide)
        {
            ((TravelersBackpackBlockEntity)blockEntity).drop(level, pos, asItem());

            if(((TravelersBackpackBlockEntity)blockEntity).isSleepingBagDeployed())
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

        if(world.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity)
        {
            TravelersBackpackBlockEntity te = (TravelersBackpackBlockEntity)world.getBlockEntity(pos);
            te.transferToItemStack(stack);
            if(te.hasCustomName()) stack.setHoverName(te.getCustomName());
        }
        return stack;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new TravelersBackpackBlockEntity(pos, state);
    }

    //Special

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random rand)
    {
        super.animateTick(state, level, pos, rand);

        if(state.getBlock() == ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK.get())
        {
            BlockPos enchTable = BackpackUtils.findBlock3D(level, pos.getX(), pos.getY(), pos.getZ(), Blocks.ENCHANTING_TABLE, 2, 2);

            if(enchTable != null)
            {
                if(!level.getBlockState(new BlockPos((enchTable.getX() - pos.getX()) / 2 + pos.getX(), enchTable.getY(), (enchTable.getZ() - pos.getZ()) / 2 + pos.getZ())).isAir())
                {
                    return;
                }

                for(int o = 0; o < 4; o++)
                {
                    level.addParticle(ParticleTypes.ENCHANT, enchTable.getX() + 0.5D, enchTable.getY() + 2.0D, enchTable.getZ() + 0.5D,
                            ((pos.getX() - enchTable.getX()) + rand.nextFloat()) - 0.5D,
                            ((pos.getY() - enchTable.getY()) - rand.nextFloat() - 1.0F),
                            ((pos.getZ() - enchTable.getZ()) + rand.nextFloat()) - 0.5D);
                }
            }
        }
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos)
    {
        return state.getBlock() == ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK.get() ? 5 : super.getEnchantPowerBonus(state, world, pos);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter getter, BlockPos pos, Direction direction)
    {
        return state.getBlock() == ModBlocks.REDSTONE_TRAVELERS_BACKPACK.get() ? 15 : super.getSignal(state, getter, pos, direction);
    }

    @Override
    public boolean isSignalSource(BlockState state)
    {
        return state.getBlock() == ModBlocks.REDSTONE_TRAVELERS_BACKPACK.get();
    }
}