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
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
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

public class TravelersBackpackBlock extends Block
{
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    //private static final VoxelShape BACKPACK_SHAPE_NORTH = Block.makeCuboidShape(1.0D, 0.0D, 4.0D, 15.0D, 10.0D, 12.0D);
    //private static final VoxelShape BACKPACK_SHAPE_SOUTH = Block.makeCuboidShape(1.0D, 0.0D, 4.0D, 15.0D, 10.0D, 12.0D);
    //private static final VoxelShape BACKPACK_SHAPE_EAST = Block.makeCuboidShape(4.0D, 0.0D, 1.0D, 12.0D, 10.0D, 15.0D);
    //private static final VoxelShape BACKPACK_SHAPE_WEST = Block.makeCuboidShape(4.0D, 0.0D, 1.0D, 12.0D, 10.0D, 15.0D);

    private static final double X = (double)14/18;
    private static final double Y = (double)10/13;
    private static final double Z = (double)7/9;
    private static final double OX = 1.775;
    private static final double OY = 1.655;
    private static final double OZ = 1.778;

    private static final VoxelShape BACKPACK_SHAPE_NORTH = Stream.of(
            Block.makeCuboidShape((3.0D*X)+OX, (-1.0D*Y)+OY, (6.0D*Z)+OZ, (13.0D*X)+OX, (11.0D*Y)+OY, (11.0D*Z)+OZ), //Main
            Block.makeCuboidShape((3.0D*X)+OX, (-2.0D*Y)+OY, (7.0D*Z)+OZ, (13.0D*X)+OX, (-1.0D*Y)+OY, (11.0D*Z)+OZ), //Main
            Block.makeCuboidShape((4.0D*X)+OX, (1.08D*Y)+OY, (4.0D*Z)+OZ, (12.0D*X)+OX, (7.08D*Y)+OY, (6.0D*Z)+OZ), //Pocket
            Block.makeCuboidShape((4.0D*X)+OX, (0.0D*Y)+OY, (11.0D*Z)+OZ, (5.0D*X)+OX, (8.0D*Y)+OY, (12.0D*Z)+OZ), //Right Strap
            Block.makeCuboidShape((11.0D*X)+OX, (0.0D*Y)+OY, (11.0D*Z)+OZ, (12.0D*X)+OX, (8.0D*Y)+OY, (12.0D*Z)+OZ), //Left Strap
            Block.makeCuboidShape((-1.0D*X)+OX, (-2.0D*Y)+OY, (6.5D*Z)+OZ, (3.0D*X)+OX, (8.0D*Y)+OY, (10.5D*Z)+OZ),
            Block.makeCuboidShape((13.0D*X)+OX, (-2.0D*Y)+OY, (6.5D*Z)+OZ, (17.0D*X)+OX, (8.0D*Y)+OY, (10.5D*Z)+OZ)
    ).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape BACKPACK_SHAPE_SOUTH = Stream.of(
            Block.makeCuboidShape((3.0D*X)+OX, (-1.0D*Y)+OY, (5.0D*Z)+OZ, (13.0D*X)+OX, (11.0D*Y)+OY, (10.0D*Z)+OZ), //Main
            Block.makeCuboidShape((3.0D*X)+OX, (-2.0D*Y)+OY, (5.0D*Z)+OZ, (13.0D*X)+OX, (-1.0D*Y)+OY, (9.0D*Z)+OZ), //Main
            Block.makeCuboidShape((4.0D*X)+OX, (1.08D*Y)+OY, (10.0D*Z)+OZ, (12.0D*X)+OX, (7.08D*Y)+OY, (12.0D*Z)+OZ), //Pocket
            Block.makeCuboidShape((4.0D*X)+OX, (0.0D*Y)+OY, (5.0D*Z)+OZ, (5.0D*X)+OX, (8.0D*Y)+OY, (4.0D*Z)+OZ), //Right Strap
            Block.makeCuboidShape((11.0D*X)+OX, (0.0D*Y)+OY, (5.0D*Z)+OZ, (12.0D*X)+OX, (8.0D*Y)+OY, (4.0D*Z)+OZ), //Left Strap
            Block.makeCuboidShape((-1.0D*X)+OX, (-2.0D*Y)+OY, (5.5D*Z)+OZ, (3.0D*X)+OX, (8.0D*Y)+OY, (9.5D*Z)+OZ),
            Block.makeCuboidShape((13.0D*X)+OX, (-2.0D*Y)+OY, (5.5D*Z)+OZ, (17.0D*X)+OX, (8.0D*Y)+OY, (9.5D*Z)+OZ)
    ).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape BACKPACK_SHAPE_WEST = Stream.of(
            Block.makeCuboidShape((6.0D*X)+OX, (-1.0D*Y)+OY, (3.0D*Z)+OZ, (11.0D*X)+OX, (11.0D*Y)+OY, (13.0D*Z)+OZ), //Main
            Block.makeCuboidShape((7.0D*X)+OX, (-2.0D*Y)+OY, (3.0D*Z)+OZ, (11.0D*X)+OX, (-1.0D*Y)+OY, (13.0D*Z)+OZ), //Main
            Block.makeCuboidShape((4.0D*X)+OX, (1.08D*Y)+OY, (4.0D*Z)+OZ, (6.0D*X)+OX, (7.08D*Y)+OY, (12.0D*Z)+OZ), //Pocket
            Block.makeCuboidShape((11.0D*X)+OX, (0.0D*Y)+OY, (4.0D*Z)+OZ, (12.0D*X)+OX, (8.0D*Y)+OY, (5.0D*Z)+OZ), //Right Strap
            Block.makeCuboidShape((11.0D*X)+OX, (0.0D*Y)+OY, (11.0D*Z)+OZ, (12.0D*X)+OX, (8.0D*Y)+OY, (12.0D*Z)+OZ), //Left Strap
            Block.makeCuboidShape((6.5D*X)+OX, (-2.0D*Y)+OY, (-1.0D*Z)+OZ, (10.5D*X)+OX, (8.0D*Y)+OY, (3.0D*Z)+OZ),
            Block.makeCuboidShape((6.5D*X)+OX, (-2.0D*Y)+OY, (13.0D*Z)+OZ, (10.5D*X)+OX, (8.0D*Y)+OY, (17.0D*Z)+OZ)
    ).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape BACKPACK_SHAPE_EAST = Stream.of(
            Block.makeCuboidShape((5.0D*X)+OX, (-1.0D*Y)+OY, (3.0D*Z)+OZ, (10.0D*X)+OX, (11.0D*Y)+OY, (13.0D*Z)+OZ), //Main
            Block.makeCuboidShape((5.0D*X)+OX, (-2.0D*Y)+OY, (3.0D*Z)+OZ, (9.0D*X)+OX, (-1.0D*Y)+OY, (13.0D*Z)+OZ), //Main
            Block.makeCuboidShape((10.0D*X)+OX, (1.08D*Y)+OY, (4.0D*Z)+OZ, (12.0D*X)+OX, (7.08D*Y)+OY, (12.0D*Z)+OZ), //Pocket
            Block.makeCuboidShape((5.0D*X)+OX, (0.0D*Y)+OY, (4.0D*Z)+OZ, (4.0D*X)+OX, (8.0D*Y)+OY, (5.0D*Z)+OZ), //Right Strap
            Block.makeCuboidShape((5.0D*X)+OX, (0.0D*Y)+OY, (11.0D*Z)+OZ, (4.0D*X)+OX, (8.0D*Y)+OY, (12.0D*Z)+OZ), //Left Strap
            Block.makeCuboidShape((5.5D*X)+OX, (-2.0D*Y)+OY, (-1.0D*Z)+OZ, (9.5D*X)+OX, (8.0D*Y)+OY, (3.0D*Z)+OZ),
            Block.makeCuboidShape((5.5D*X)+OX, (-2.0D*Y)+OY, (13.0D*Z)+OZ, (9.5D*X)+OX, (8.0D*Y)+OY, (17.0D*Z)+OZ)
    ).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, IBooleanFunction.OR)).get();

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
                            ItemStack stack = new ItemStack(asItem(), 1);
                            te.transferToItemStack(stack);

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
                                            if(worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 7))
                                            {
                                                stackHandler.setStackInSlot(i, stack.copy());
                                                player.world.playSound(null, player.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.2F) * 0.7F);

                                                if(te.isSleepingBagDeployed())
                                                {
                                                    Direction bagDirection = state.get(TravelersBackpackBlock.FACING);
                                                    worldIn.setBlockState(pos.offset(bagDirection), Blocks.AIR.getDefaultState());
                                                    worldIn.setBlockState(pos.offset(bagDirection).offset(bagDirection), Blocks.AIR.getDefaultState());
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