package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
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
import net.minecraftforge.fluids.IFluidBlock;
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
    public UseAction getUseAction(ItemStack stack)
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
    public ActionResultType onItemUse(ItemUseContext context)
    {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        Direction direction = context.getFace();
        ItemStack stack = player.getHeldItem(context.getHand());

        if(CapabilityUtils.isWearingBackpack(player) && context.getHand() == Hand.MAIN_HAND)
        {
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
                if(fluidHandler.isPresent())
                {
                    FluidUtil.tryFluidTransfer(tank, fluidHandler.orElse(null), Reference.BUCKET, true);
                    inv.markTankDirty();
                    world.playSound(player, pos, fluidHandler.map(f -> f.getFluidInTank(0).getFluid().getAttributes().getFillSound()).orElse(SoundEvents.ITEM_BUCKET_FILL), SoundCategory.BLOCKS, 1.0F, 1.0F);
                    return ActionResultType.SUCCESS;
                }

                RayTraceResult result = rayTrace(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);

                if(result.getType() == RayTraceResult.Type.BLOCK)
                {
                    BlockPos fluidPos = new BlockPos(result.getHitVec().getX(), result.getHitVec().getY(), result.getHitVec().getZ());
                    BlockState fluidState = world.getBlockState(fluidPos);

                    if(fluidState.getBlock() instanceof IFluidBlock)
                    {
                        IFluidHandler targetFluidHandler = FluidUtil.getFluidHandler(world, fluidPos, direction).orElse(null);

                        if(targetFluidHandler != null)
                        {
                            FluidUtil.tryFluidTransfer(tank, targetFluidHandler, Reference.BUCKET, true);
                            world.playSound(player, fluidPos, targetFluidHandler.getFluidInTank(0).getFluid().getAttributes().getFillSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                            inv.markTankDirty();
                            return ActionResultType.SUCCESS;
                        }
                    }

                    else if(fluidState.getBlock() instanceof FlowingFluidBlock)
                    {
                        Fluid fluid = ((FlowingFluidBlock)fluidState.getBlock()).getFluid();
                        FluidStack fluidStack = new FluidStack(fluid, Reference.BUCKET);
                        int tankAmount = tank.isEmpty() ? 0 : tank.getFluidAmount();
                        boolean canFill = tank.isEmpty() || tank.getFluid().isFluidEqual(fluidStack);

                        if(canFill && (fluidStack.getAmount() + tankAmount <= tank.getCapacity()))
                        {
                            world.playSound(player, fluidPos, fluid.getAttributes().getFillSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                            tank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                            world.setBlockState(fluidPos, Blocks.AIR.getDefaultState());
                            world.notifyNeighborsOfStateChange(fluidPos, fluidState.getBlock());
                            inv.markTankDirty();
                            return ActionResultType.SUCCESS;
                        }
                    }
                }
            }
            if(getHoseMode(stack) == 2)
            {
                if(fluidHandler.isPresent())
                {
                    FluidUtil.tryFluidTransfer(fluidHandler.orElse(null), tank, Reference.BUCKET, true);
                    inv.markTankDirty();
                    return ActionResultType.SUCCESS;
                }

                if(!tank.isEmpty())
                {
                    int x = pos.getX();
                    int y = pos.getY();
                    int z = pos.getZ();

                    if(!world.getBlockState(pos).isReplaceable(tank.getFluid().getFluid()))
                    {
                        switch(context.getFace())
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

                    if(fluidStack.getFluid().getAttributes().canBePlacedInWorld(world, newPos, fluidStack))
                    {
                        Material material = world.getBlockState(newPos).getMaterial();
                        boolean flag = !material.isSolid();

                        if(world.getDimensionType().isUltrawarm() && fluidStack.getFluid().isIn(FluidTags.WATER))
                        {
                            tank.drain(Reference.BUCKET, IFluidHandler.FluidAction.EXECUTE);
                            inv.markTankDirty();

                            world.playSound(null, newPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

                            for(int i = 0; i < 3; ++i)
                            {
                                double d0 = newPos.getX() + world.rand.nextDouble();
                                double d1 = newPos.getY() + world.rand.nextDouble() * 0.5D + 0.5D;
                                double d2 = newPos.getZ() + world.rand.nextDouble();
                                world.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                                return ActionResultType.SUCCESS;
                            }
                        }
                        if(fluidStack.getAmount() >= Reference.BUCKET)
                        {
                            if(!world.isRemote && flag && !material.isLiquid())
                            {
                                world.destroyBlock(newPos, false);
                            }

                            if(world.setBlockState(newPos, fluidStack.getFluid().getDefaultState().getBlockState()))
                            {
                                tank.drain(Reference.BUCKET, IFluidHandler.FluidAction.EXECUTE);
                                world.notifyNeighborsOfStateChange(newPos, fluidStack.getFluid().getDefaultState().getBlockState().getBlock());
                            }

                            world.playSound(player, newPos, fluidStack.getFluid().getAttributes().getEmptySound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                            inv.markTankDirty();
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
                        player.setActiveHand(Hand.MAIN_HAND);
                        return ActionResultType.SUCCESS;
                    }
                }
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving)
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
                            inv.markTankDirty();
                        }
                    }
                }
            }
        }
        return stack;
    }
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if(CapabilityUtils.isWearingBackpack(playerIn) && handIn == Hand.MAIN_HAND)
        {
            if(stack.getTag() == null)
            {
                this.getCompoundTag(stack);
                return ActionResult.resultPass(stack);
            }

            FluidTank tank = getSelectedFluidTank(stack, CapabilityUtils.getBackpackInv(playerIn));

            if(getHoseMode(stack) == 3)
            {
                if(!tank.isEmpty())
                {
                    if(tank.getFluidAmount() >= Reference.BUCKET)
                    {
                        if(EffectFluidRegistry.hasFluidEffect(tank.getFluid().getFluid()))
                        {
                            playerIn.setActiveHand(Hand.MAIN_HAND);
                            return ActionResult.resultSuccess(stack);
                        }
                    }
                }
            }
        }
        return ActionResult.resultFail(stack);
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
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        if(getHoseMode(stack) == 0)
        {
            tooltip.add(new TranslationTextComponent("hose.travelersbackpack.not_assigned").mergeStyle(TextFormatting.BLUE));
        }
        else
        {
            if(stack.getTag() != null)
            {
                CompoundNBT compound = stack.getTag();

                if(compound.getInt("Mode") == 1)
                {
                    tooltip.add(new TranslationTextComponent("hose.travelersbackpack.current_mode_suck").mergeStyle(TextFormatting.BLUE));
                }

                if(compound.getInt("Mode") == 2)
                {
                    tooltip.add(new TranslationTextComponent("hose.travelersbackpack.current_mode_spill").mergeStyle(TextFormatting.BLUE));
                }

                if(compound.getInt("Mode") == 3)
                {
                    tooltip.add(new TranslationTextComponent("hose.travelersbackpack.current_mode_drink").mergeStyle(TextFormatting.BLUE));
                }

                if(compound.getInt("Tank") == 1)
                {
                    tooltip.add(new TranslationTextComponent("hose.travelersbackpack.current_tank_left").mergeStyle(TextFormatting.BLUE));
                }

                if(compound.getInt("Tank") == 2)
                {
                    tooltip.add(new TranslationTextComponent("hose.travelersbackpack.current_tank_right").mergeStyle(TextFormatting.BLUE));
                }
            }
        }
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack)
    {
        int x = getHoseMode(stack);
        String mode = "";
        String localizedName = new TranslationTextComponent("item.travelersbackpack.hose.name").getString();
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

        if(!tag.hasUniqueId("Tank"))
        {
            tag.putInt("Tank", 1);
        }

        if(!tag.hasUniqueId("Mode"))
        {
            tag.putInt("Mode", 1);
        }

        return tag;
    }
}