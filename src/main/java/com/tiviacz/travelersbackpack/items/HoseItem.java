package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;
import java.util.List;

public class HoseItem extends Item
{
    public HoseItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack)
    {
        if(getHoseMode(stack) == 3)
        {
            return UseAction.DRINK;
        }
        return UseAction.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return 24;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack stack = playerIn.getItemInHand(handIn);

        if(CapabilityUtils.isWearingBackpack(playerIn) && handIn == Hand.MAIN_HAND)
        {
            //Configure nbt

            if(stack.getTag() == null)
            {
                this.getCompoundTag(stack);
                return ActionResult.pass(stack);
            }

            TravelersBackpackInventory inv = CapabilityUtils.getBackpackInv(playerIn);
            FluidTank tank = this.getSelectedFluidTank(stack, inv);

            if(getHoseMode(stack) == 1)
            {
                //Pick fluid from block
                BlockRayTraceResult result = getPlayerPOVHitResult(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);

                BlockPos blockpos = result.getBlockPos();
                Direction direction1 = result.getDirection();
                BlockPos blockpos1 = blockpos.relative(result.getDirection());

                if(worldIn.mayInteract(playerIn, blockpos) && playerIn.mayUseItemAt(blockpos1, direction1, stack))
                {
                    BlockState blockstate1 = worldIn.getBlockState(blockpos);

                    if(blockstate1.getBlock() instanceof IBucketPickupHandler)
                    {
                        Fluid fluid = blockstate1.getFluidState().getType();

                        if(fluid != Fluids.EMPTY)
                        {
                            FluidStack fluidStack = new FluidStack(fluid, Reference.BUCKET);
                            int tankAmount = tank.isEmpty() ? 0 : tank.getFluidAmount();
                            boolean canFill = tank.isEmpty() || tank.getFluid().isFluidEqual(fluidStack);

                            if(canFill && (fluidStack.getAmount() + tankAmount <= tank.getCapacity()))
                            {
                                Fluid actualFluid = ((IBucketPickupHandler) blockstate1.getBlock()).takeLiquid(worldIn, blockpos, blockstate1);

                                if(actualFluid != Fluids.EMPTY)
                                {
                                    worldIn.playSound(playerIn, result.getBlockPos(), fluid.getAttributes().getFillSound() == null ? (fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL) : fluid.getAttributes().getFillSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                                    tank.fill(new FluidStack(actualFluid, Reference.BUCKET), IFluidHandler.FluidAction.EXECUTE);
                                    inv.setDataChanged(ITravelersBackpackInventory.TANKS_DATA);
                                    return ActionResult.success(stack);
                                }
                            }
                        }
                    }
                }
            }

            if(getHoseMode(stack) == 3)
            {
                if(!tank.isEmpty())
                {
                    if(EffectFluidRegistry.hasFluidEffectAndCanExecute(tank.getFluid(), worldIn, playerIn))
                    {
                        playerIn.startUsingItem(handIn);
                        return ActionResult.success(stack);
                    }
                }
            }
        }
        return ActionResult.pass(stack);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        PlayerEntity player = context.getPlayer();
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        ItemStack stack = player.getItemInHand(context.getHand());

        if(CapabilityUtils.isWearingBackpack(player) && context.getHand() == Hand.MAIN_HAND)
        {
            //Configure nbt

            if(stack.getTag() == null)
            {
                this.getCompoundTag(stack);
                return ActionResultType.PASS;
            }

            LazyOptional<IFluidHandler> fluidHandler = FluidUtil.getFluidHandler(world, pos, direction);
            TravelersBackpackInventory inv = CapabilityUtils.getBackpackInv(player);
            FluidTank tank = this.getSelectedFluidTank(stack, inv);

            if(getHoseMode(stack) == 1)
            {
                //Transfer fluid from fluid handler

                if(fluidHandler.isPresent())
                {
                    if(!fluidHandler.map(h -> h.getFluidInTank(0).isEmpty()).get())
                    {
                        FluidStack fluidStack = FluidUtil.tryFluidTransfer(tank, fluidHandler.orElse(null), Reference.BUCKET, true);

                        if(!fluidStack.isEmpty())
                        {
                            world.playSound(player, pos, fluidStack.getFluid().getAttributes().getFillSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                            inv.setDataChanged(ITravelersBackpackInventory.TANKS_DATA);
                            return ActionResultType.SUCCESS;
                        }
                    }
                }

                //Pick fluid from block

                BlockRayTraceResult result = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);

                BlockPos blockpos = result.getBlockPos();
                Direction direction1 = result.getDirection();
                BlockPos blockpos1 = blockpos.relative(direction);

                if(world.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos1, direction1, stack))
                {
                    BlockState blockstate1 = world.getBlockState(blockpos);

                    if(blockstate1.getBlock() instanceof IBucketPickupHandler)
                    {
                        Fluid fluid = blockstate1.getFluidState().getType();

                        if(fluid != Fluids.EMPTY)
                        {
                            FluidStack fluidStack = new FluidStack(fluid, Reference.BUCKET);
                            int tankAmount = tank.isEmpty() ? 0 : tank.getFluidAmount();
                            boolean canFill = tank.isEmpty() || tank.getFluid().isFluidEqual(fluidStack);

                            if(canFill && (fluidStack.getAmount() + tankAmount <= tank.getCapacity()))
                            {
                                Fluid actualFluid = ((IBucketPickupHandler)blockstate1.getBlock()).takeLiquid(world, blockpos, blockstate1);

                                if(actualFluid != Fluids.EMPTY)
                                {
                                    world.playSound(player, result.getBlockPos(), fluid.getAttributes().getFillSound() == null ? (fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL) : fluid.getAttributes().getFillSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                                    tank.fill(new FluidStack(actualFluid, Reference.BUCKET), IFluidHandler.FluidAction.EXECUTE);
                                    inv.setDataChanged(ITravelersBackpackInventory.TANKS_DATA);
                                    return ActionResultType.SUCCESS;
                                }
                            }
                        }
                    }
                }
            }
            if(getHoseMode(stack) == 2)
            {
                //Transfer fluid to fluid handler

                if(fluidHandler.isPresent() && !tank.isEmpty())
                {
                    FluidStack fluidStack = FluidUtil.tryFluidTransfer(fluidHandler.orElse(null), tank, Reference.BUCKET, true);

                    if(!fluidStack.isEmpty())
                    {
                        world.playSound(player, pos, fluidStack.getFluid().getAttributes().getFillSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                        inv.setDataChanged(ITravelersBackpackInventory.TANKS_DATA);
                        return ActionResultType.SUCCESS;
                    }
                }

                //Try to put fluid in the world

                if(!tank.isEmpty())
                {
                    BlockState blockState = world.getBlockState(pos);
                    Block block = blockState.getBlock();
                    Fluid fluid = tank.getFluid().getFluid();

                    if(tank.getFluidAmount() >= Reference.BUCKET && fluid instanceof FlowingFluid)
                    {
                        if(block instanceof ILiquidContainer && ((ILiquidContainer)block).canPlaceLiquid(world, pos, blockState, fluid))
                        {
                            ((ILiquidContainer)block).placeLiquid(world, pos, blockState, ((FlowingFluid)fluid).getSource(false));
                            world.playSound(player, pos, fluid.getAttributes().getEmptySound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                            tank.drain(Reference.BUCKET, IFluidHandler.FluidAction.EXECUTE);
                            return ActionResultType.SUCCESS;
                        }
                    }

                    int x = pos.getX();
                    int y = pos.getY();
                    int z = pos.getZ();

                    if(!world.getBlockState(pos).canBeReplaced(fluid))
                    {
                        switch(context.getClickedFace())
                        {
                            case WEST:
                                --x;
                                break;
                            case EAST:
                                ++x;
                                break;
                            case NORTH:
                                --z;
                                break;
                            case SOUTH:
                                ++z;
                                break;
                            case UP:
                                ++y;
                                break;
                            case DOWN:
                                --y;
                                break;
                            default:
                                break;
                        }
                    }

                    BlockPos newPos = new BlockPos(x,y,z);
                    FluidStack fluidStack = tank.getFluid();

                    if(world.getBlockState(newPos).canBeReplaced(fluid) && fluid.getAttributes().canBePlacedInWorld(world, newPos, fluidStack))
                    {
                        Material material = world.getBlockState(newPos).getMaterial();
                        boolean flag = !material.isSolid();

                        if(world.dimensionType().ultraWarm() && fluidStack.getFluid().is(FluidTags.WATER))
                        {
                            tank.drain(Reference.BUCKET, IFluidHandler.FluidAction.EXECUTE);
                            inv.setDataChanged(ITravelersBackpackInventory.TANKS_DATA);

                            world.playSound(null, newPos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

                            for(int i = 0; i < 3; ++i)
                            {
                                double d0 = newPos.getX() + world.random.nextDouble();
                                double d1 = newPos.getY() + world.random.nextDouble() * 0.5D + 0.5D;
                                double d2 = newPos.getZ() + world.random.nextDouble();
                                world.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                            }
                            return ActionResultType.SUCCESS;
                        }
                        if(fluidStack.getAmount() >= Reference.BUCKET)
                        {
                            if(!world.isClientSide && flag && !material.isLiquid())
                            {
                                world.destroyBlock(newPos, false);
                            }

                            if(world.setBlockAndUpdate(newPos, fluidStack.getFluid().defaultFluidState().createLegacyBlock()))
                            {
                                world.playSound(player, newPos, fluidStack.getFluid().getAttributes().getEmptySound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                                tank.drain(Reference.BUCKET, IFluidHandler.FluidAction.EXECUTE);
                                world.updateNeighborsAt(newPos, fluidStack.getFluid().defaultFluidState().createLegacyBlock().getBlock());
                            }

                            inv.setDataChanged(ITravelersBackpackInventory.TANKS_DATA);
                            return ActionResultType.SUCCESS;
                        }
                    }
                }
            }

            if(getHoseMode(stack) == 3)
            {
                if(!tank.isEmpty())
                {
                    if(EffectFluidRegistry.hasFluidEffectAndCanExecute(tank.getFluid(), world, player))
                    {
                        player.startUsingItem(context.getHand());
                        return ActionResultType.SUCCESS;
                    }
                }
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving)
    {
        if(entityLiving instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity)entityLiving;

            if(CapabilityUtils.isWearingBackpack(player))
            {
                TravelersBackpackInventory inv = CapabilityUtils.getBackpackInv(player);
                FluidTank tank = this.getSelectedFluidTank(stack, inv);

                if(getHoseMode(stack) == 3)
                {
                    if(tank != null)
                    {
                        if(ServerActions.setFluidEffect(worldIn, player, tank))
                        {
                            EffectFluid targetEffect = EffectFluidRegistry.getFluidEffect(tank.getFluid().getFluid());

                            tank.drain(targetEffect.amountRequired, IFluidHandler.FluidAction.EXECUTE);
                            inv.setDataChanged(ITravelersBackpackInventory.TANKS_DATA);
                        }
                    }
                }
            }
        }
        return stack;
    }

    public static int getHoseMode(ItemStack stack)
    {
        if(stack.getTag() != null)
        {
            return stack.getTag().getInt("Mode");
            //1 = Suck mode
            //2 = Spill mode
            //3 = Drink mode
        }
        return 0;
    }

    public static int getHoseTank(ItemStack stack)
    {
        if(stack.getTag() != null)
        {
            return stack.getTag().getInt("Tank");
            //1 = Left tank
            //2 = Right tank
        }
        return 0;
    }

    public FluidTank getSelectedFluidTank(ItemStack stack, TravelersBackpackInventory inv)
    {
        return getHoseTank(stack) == 1 ? inv.getLeftTank() : inv.getRightTank();
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        if(entityIn instanceof PlayerEntity)
        {
            if(!CapabilityUtils.isWearingBackpack((PlayerEntity)entityIn))
            {
                if(stack.getTag() != null)
                {
                    stack.setTag(null);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        if(getHoseMode(stack) == 0)
        {
            tooltip.add(new TranslationTextComponent("hose.travelersbackpack.not_assigned").withStyle(TextFormatting.BLUE));
        }
        else
        {
            if(stack.getTag() != null)
            {
                CompoundNBT compound = stack.getTag();

                if(compound.getInt("Mode") == 1)
                {
                    tooltip.add(new TranslationTextComponent("hose.travelersbackpack.current_mode_suck").withStyle(TextFormatting.BLUE));
                }

                if(compound.getInt("Mode") == 2)
                {
                    tooltip.add(new TranslationTextComponent("hose.travelersbackpack.current_mode_spill").withStyle(TextFormatting.BLUE));
                }

                if(compound.getInt("Mode") == 3)
                {
                    tooltip.add(new TranslationTextComponent("hose.travelersbackpack.current_mode_drink").withStyle(TextFormatting.BLUE));
                }

                if(compound.getInt("Tank") == 1)
                {
                    tooltip.add(new TranslationTextComponent("hose.travelersbackpack.current_tank_left").withStyle(TextFormatting.BLUE));
                }

                if(compound.getInt("Tank") == 2)
                {
                    tooltip.add(new TranslationTextComponent("hose.travelersbackpack.current_tank_right").withStyle(TextFormatting.BLUE));
                }
            }
        }
    }

    @Override
    public ITextComponent getName(ItemStack stack)
    {
        int x = getHoseMode(stack);
        String mode = "";
        String localizedName = new TranslationTextComponent("item.travelersbackpack.hose").getString();
        String suckMode = new TranslationTextComponent("item.travelersbackpack.hose.suck").getString();
        String spillMode = new TranslationTextComponent("item.travelersbackpack.hose.spill").getString();
        String drinkMode = new TranslationTextComponent("item.travelersbackpack.hose.drink").getString();

        if(x == 1)
        {
            mode = " " + suckMode;
        }
        else if(x == 2)
        {
            mode = " " + spillMode;
        }
        else if(x == 3)
        {
            mode = " " + drinkMode;
        }

        return new StringTextComponent(localizedName + mode);
    }

    public CompoundNBT getCompoundTag(ItemStack stack)
    {
        if(stack.getTag() == null)
        {
            stack.setTag(new CompoundNBT());
        }

        CompoundNBT tag = stack.getTag();

        if(!tag.hasUUID("Tank"))
        {
            tag.putInt("Tank", 1);
        }

        if(!tag.hasUUID("Mode"))
        {
            tag.putInt("Mode", 1);
        }

        return tag;
    }
}