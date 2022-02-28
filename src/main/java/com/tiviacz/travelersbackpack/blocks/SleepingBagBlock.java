package com.tiviacz.travelersbackpack.blocks;

import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
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
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public class SleepingBagBlock extends BedBlock
{
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
    protected static final VoxelShape SLEEPING_BAG = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

    protected static final VoxelShape SLEEPING_BAG_NORTH = Stream.of(
            Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.makeCuboidShape(0.0D, 2.0D, 0.0D, 16.0D, 2.5D, 8.0D)
    ).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, IBooleanFunction.OR)).get();

    protected static final VoxelShape SLEEPING_BAG_EAST = Stream.of(
            Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.makeCuboidShape(8.0D, 2.0D, 0.0D, 16.0D, 2.5D, 16.0D)
    ).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, IBooleanFunction.OR)).get();

    protected static final VoxelShape SLEEPING_BAG_SOUTH = Stream.of(
            Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.makeCuboidShape(0.0D, 2.0D, 8.0D, 16.0D, 2.5D, 16.0D)
    ).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, IBooleanFunction.OR)).get();

    protected static final VoxelShape SLEEPING_BAG_WEST = Stream.of(
            Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.makeCuboidShape(0.0D, 2.0D, 0.0D, 8.0D, 2.5D, 16.0D)
    ).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, IBooleanFunction.OR)).get();

    public SleepingBagBlock(Block.Properties properties)
    {
        super(DyeColor.RED, properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(PART, BedPart.FOOT).with(OCCUPIED, Boolean.FALSE));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        switch(state.get(PART))
        {
            case FOOT: return SLEEPING_BAG;
            case HEAD:
                switch(state.get(HORIZONTAL_FACING))
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
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if(worldIn.isRemote)
        {
            return ActionResultType.CONSUME;
        }
        else
        {
            if(state.get(PART) != BedPart.HEAD)
            {
                pos = pos.offset(state.get(HORIZONTAL_FACING));
                state = worldIn.getBlockState(pos);

                if(!state.isIn(this))
                {
                    return ActionResultType.CONSUME;
                }
            }

            if(!doesBedWork(worldIn))
            {
                worldIn.removeBlock(pos, false);
                BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING).getOpposite());

                if(worldIn.getBlockState(blockpos).isIn(this))
                {
                    worldIn.removeBlock(blockpos, false);
                }

                //world.explode(null, DamageSource.badRespawnPointExplosion(), null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 5.0F, true, Explosion.Mode.DESTROY);
                return ActionResultType.SUCCESS;
            }
            else if(state.get(OCCUPIED))
            {
                if(!this.wakeVillagers(worldIn, pos))
                {
                    player.sendStatusMessage(new TranslationTextComponent("block.minecraft.bed.occupied"), true);
                }

                return ActionResultType.SUCCESS;
            }
            else
            {
                BlockPos finalPos = pos;
                player.trySleep(pos).ifLeft((result) ->
                {
                    if(result != null)
                    {
                        player.sendStatusMessage(result.getMessage(), true);

                        if(result == PlayerEntity.SleepResult.NOT_POSSIBLE_NOW)
                        {
                            ((ServerPlayerEntity)player).func_242111_a(((ServerPlayerEntity)player).func_241141_L_(), finalPos, 1.0F, true, false);
                        }
                    }

                });
                return ActionResultType.SUCCESS;
            }
        }
    }


    private boolean wakeVillagers(World world, BlockPos pos)
    {
        List<VillagerEntity> list = world.getEntitiesWithinAABB(VillagerEntity.class, new AxisAlignedBB(pos), LivingEntity::isSleeping);

        if(list.isEmpty())
        {
            return false;
        }
        else
        {
            list.get(0).wakeUp();
            return true;
        }
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
    {
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance * 0.75F);
    }

    @Override
    public void onLanded(IBlockReader worldIn, Entity entityIn)
    {
        if(entityIn.isSuppressingBounce())
        {
            super.onLanded(worldIn, entityIn);
        }
        else
        {
            this.updateMotion(entityIn);
        }

    }

    private void updateMotion(Entity entity)
    {
        Vector3d vec3d = entity.getMotion();
        if(vec3d.y < 0.0D)
        {
            double d0 = entity instanceof LivingEntity ? 1.0D : 0.8D;
            entity.setMotion(vec3d.x, -vec3d.y * (double) 0.33F * d0, vec3d.z);
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if(facing == getDirectionToOther(stateIn.get(PART), stateIn.get(HORIZONTAL_FACING)))
        {
            return facingState.getBlock() == this && facingState.get(PART) != stateIn.get(PART) ? stateIn.with(OCCUPIED, facingState.get(OCCUPIED)) : Blocks.AIR.getDefaultState();
        }
        else
        {
            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    private static Direction getDirectionToOther(BedPart part, Direction direction)
    {
        return part == BedPart.FOOT ? direction : direction.getOpposite();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
        boolean isFoot = state.get(PART) == BedPart.FOOT;

        BlockPos backpackPos = isFoot ? pos.offset(state.get(HORIZONTAL_FACING).getOpposite()) : pos.offset(state.get(HORIZONTAL_FACING).getOpposite(), 2);

        if(worldIn.getBlockState(backpackPos).getBlock() instanceof TravelersBackpackBlock)
        {
            if(worldIn.getTileEntity(backpackPos) instanceof TravelersBackpackTileEntity)
            {
                ((TravelersBackpackTileEntity)worldIn.getTileEntity(backpackPos)).setSleepingBagDeployed(false);
            }
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        Direction direction = context.getPlacementHorizontalFacing();
        BlockPos blockpos = context.getPos();
        BlockPos blockpos1 = blockpos.offset(direction);

        return context.getWorld().getBlockState(blockpos1).isReplaceable(context) ? this.getDefaultState().with(HORIZONTAL_FACING, direction) : null;
    }

    @Override
    public PushReaction getPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(HORIZONTAL_FACING, PART, OCCUPIED);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        if(!worldIn.isRemote)
        {
            BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING));
            worldIn.setBlockState(blockpos, state.with(PART, BedPart.HEAD), 3);
            worldIn.notifyNeighborsOfStateChange(pos, Blocks.AIR);
            state.updateNeighbours(worldIn, pos, 3);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public long getPositionRandom(BlockState state, BlockPos pos)
    {
        BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING), state.get(PART) == BedPart.HEAD ? 0 : 1);
        return MathHelper.getCoordinateRandom(blockpos.getX(), pos.getY(), blockpos.getZ());
    }

 /*   @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
    {
        return false;
    } */

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) { return null; }
}