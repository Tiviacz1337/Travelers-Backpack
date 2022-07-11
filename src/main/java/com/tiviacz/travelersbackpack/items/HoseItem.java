package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
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
    public UseAnim getUseAnimation(ItemStack stack)
    {
        if(getHoseMode(stack) == 3)
        {
            return UseAnim.DRINK;
        }
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return 24;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);

        if(CapabilityUtils.isWearingBackpack(player) && hand == InteractionHand.MAIN_HAND)
        {
            //Configure nbt

            if(stack.getTag() == null)
            {
                this.getCompoundTag(stack);
                return InteractionResultHolder.pass(stack);
            }

            TravelersBackpackContainer inv = CapabilityUtils.getBackpackInv(player);
            FluidTank tank = this.getSelectedFluidTank(stack, inv);

            if(getHoseMode(stack) == 1)
            {
                //Pick fluid from block
                BlockHitResult result = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

                BlockPos blockpos = result.getBlockPos();
                Direction direction1 = result.getDirection();
                BlockPos blockpos1 = blockpos.relative(result.getDirection());

                if(level.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos1, direction1, stack))
                {
                    BlockState blockstate1 = level.getBlockState(blockpos);

                    if(blockstate1.getBlock() instanceof BucketPickup)
                    {
                        Fluid fluid = blockstate1.getFluidState().getType();

                        if(fluid != Fluids.EMPTY)
                        {
                            FluidStack fluidStack = new FluidStack(fluid, Reference.BUCKET);
                            int tankAmount = tank.isEmpty() ? 0 : tank.getFluidAmount();
                            boolean canFill = tank.isEmpty() || tank.getFluid().isFluidEqual(fluidStack);

                            if(canFill && (fluidStack.getAmount() + tankAmount <= tank.getCapacity()))
                            {
                                ItemStack actualFluid = ((BucketPickup)blockstate1.getBlock()).pickupBlock(level, blockpos, blockstate1);

                                if(!actualFluid.isEmpty())
                                {
                                    level.playSound(player, result.getBlockPos(), fluid.getAttributes().getFillSound() == null ? (fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL) : fluid.getAttributes().getFillSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                                    tank.fill(new FluidStack(fluid, Reference.BUCKET), IFluidHandler.FluidAction.EXECUTE);
                                    inv.setTankChanged();
                                    return InteractionResultHolder.success(stack);
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
                    if(EffectFluidRegistry.hasFluidEffectAndCanExecute(tank.getFluid(), level, player))
                    {
                        player.startUsingItem(hand);
                        return InteractionResultHolder.success(stack);
                    }
                }
            }

        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        ItemStack stack = player.getItemInHand(context.getHand());

        if(CapabilityUtils.isWearingBackpack(player) && context.getHand() == InteractionHand.MAIN_HAND)
        {
            //Configure nbt

            if(stack.getTag() == null)
            {
                this.getCompoundTag(stack);
                return InteractionResult.PASS;
            }

            LazyOptional<IFluidHandler> fluidHandler = FluidUtil.getFluidHandler(level, pos, direction);
            TravelersBackpackContainer inv = CapabilityUtils.getBackpackInv(player);
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
                            level.playSound(player, pos, fluidStack.getFluid().getAttributes().getFillSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                            inv.setTankChanged();
                            return InteractionResult.SUCCESS;
                        }
                    }
                }

                //Pick fluid from block

                BlockHitResult result = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

                BlockPos blockpos = result.getBlockPos();
                Direction direction1 = result.getDirection();
                BlockPos blockpos1 = blockpos.relative(direction);

                if(level.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos1, direction1, stack))
                {
                    BlockState blockstate1 = level.getBlockState(blockpos);

                    if(blockstate1.getBlock() instanceof BucketPickup)
                    {
                        Fluid fluid = blockstate1.getFluidState().getType();

                        if(fluid != Fluids.EMPTY)
                        {
                            FluidStack fluidStack = new FluidStack(fluid, Reference.BUCKET);
                            int tankAmount = tank.isEmpty() ? 0 : tank.getFluidAmount();
                            boolean canFill = tank.isEmpty() || tank.getFluid().isFluidEqual(fluidStack);

                            if(canFill && (fluidStack.getAmount() + tankAmount <= tank.getCapacity()))
                            {
                                ItemStack actualFluid = ((BucketPickup)blockstate1.getBlock()).pickupBlock(level, blockpos, blockstate1);

                                if(!actualFluid.isEmpty())
                                //if(actualFluid != Fluids.EMPTY)
                                {
                                    level.playSound(player, result.getBlockPos(), fluid.getAttributes().getFillSound() == null ? (fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL) : fluid.getAttributes().getFillSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                                    tank.fill(new FluidStack(fluid, Reference.BUCKET), IFluidHandler.FluidAction.EXECUTE);
                                    inv.setTankChanged();
                                    return InteractionResult.SUCCESS;
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
                        level.playSound(player, pos, fluidStack.getFluid().getAttributes().getFillSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        inv.setTankChanged();
                        return InteractionResult.SUCCESS;
                    }
                }

                //Try to put fluid in the world

                if(!tank.isEmpty())
                {
                    BlockState blockState = level.getBlockState(pos);
                    Block block = blockState.getBlock();
                    Fluid fluid = tank.getFluid().getFluid();

                    if(tank.getFluidAmount() >= Reference.BUCKET && fluid instanceof FlowingFluid)
                    {
                        if(block instanceof LiquidBlockContainer && ((LiquidBlockContainer)block).canPlaceLiquid(level, pos, blockState, fluid))
                        {
                            ((LiquidBlockContainer)block).placeLiquid(level, pos, blockState, ((FlowingFluid)fluid).getSource(false));
                            level.playSound(player, pos, fluid.getAttributes().getEmptySound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                            tank.drain(Reference.BUCKET, IFluidHandler.FluidAction.EXECUTE);
                            return InteractionResult.SUCCESS;
                        }
                    }

                    int x = pos.getX();
                    int y = pos.getY();
                    int z = pos.getZ();

                    if(!level.getBlockState(pos).canBeReplaced(fluid))
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

                    if(level.getBlockState(newPos).canBeReplaced(fluid) && fluid.getAttributes().canBePlacedInWorld(level, newPos, fluidStack))
                    {
                        Material material = level.getBlockState(newPos).getMaterial();
                        boolean flag = !material.isSolid();

                        if(level.dimensionType().ultraWarm() && fluidStack.getFluid().is(FluidTags.WATER))
                        {
                            tank.drain(Reference.BUCKET, IFluidHandler.FluidAction.EXECUTE);
                            inv.setTankChanged();

                            level.playSound(null, newPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);

                            for(int i = 0; i < 3; ++i)
                            {
                                double d0 = newPos.getX() + level.getRandom().nextDouble();
                                double d1 = newPos.getY() + level.getRandom().nextDouble() * 0.5D + 0.5D;
                                double d2 = newPos.getZ() + level.getRandom().nextDouble();
                                level.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                            }
                            return InteractionResult.SUCCESS;
                        }
                        if(fluidStack.getAmount() >= Reference.BUCKET)
                        {
                            if(!level.isClientSide && flag && !material.isLiquid())
                            {
                                level.destroyBlock(newPos, false);
                            }

                            if(level.setBlock(newPos, fluidStack.getFluid().defaultFluidState().createLegacyBlock(), 3))
                            {
                                level.playSound(player, newPos, fluidStack.getFluid().getAttributes().getEmptySound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                                tank.drain(Reference.BUCKET, IFluidHandler.FluidAction.EXECUTE);
                                level.updateNeighborsAt(newPos, fluidStack.getFluid().defaultFluidState().createLegacyBlock().getBlock());
                            }

                            inv.setTankChanged();
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }

            if(getHoseMode(stack) == 3)
            {
                if(!tank.isEmpty())
                {
                    if(EffectFluidRegistry.hasFluidEffectAndCanExecute(tank.getFluid(), level, player))
                    {
                        player.startUsingItem(context.getHand());
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving)
    {
        if(entityLiving instanceof Player)
        {
            Player player = (Player)entityLiving;

            if(CapabilityUtils.isWearingBackpack(player))
            {
                TravelersBackpackContainer inv = CapabilityUtils.getBackpackInv(player);
                FluidTank tank = this.getSelectedFluidTank(stack, inv);

                if(getHoseMode(stack) == 3)
                {
                    if(tank != null)
                    {
                        if(ServerActions.setFluidEffect(level, player, tank))
                        {
                            EffectFluid targetEffect = EffectFluidRegistry.getFluidEffect(tank.getFluid().getFluid());

                            tank.drain(targetEffect.amountRequired, IFluidHandler.FluidAction.EXECUTE);
                            inv.setTankChanged();
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

    public FluidTank getSelectedFluidTank(ItemStack stack, TravelersBackpackContainer inv)
    {
        return getHoseTank(stack) == 1 ? inv.getLeftTank() : inv.getRightTank();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entityIn, int p_41407_, boolean selected)
    {
        if(entityIn instanceof Player)
        {
            if(!CapabilityUtils.isWearingBackpack((Player)entityIn))
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
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        if(getHoseMode(stack) == 0)
        {
            tooltip.add(new TranslatableComponent("hose.travelersbackpack.not_assigned").withStyle(ChatFormatting.BLUE));
        }
        else
        {
            if(stack.getTag() != null)
            {
                CompoundTag compound = stack.getTag();

                if(compound.getInt("Mode") == 1)
                {
                    tooltip.add(new TranslatableComponent("hose.travelersbackpack.current_mode_suck").withStyle(ChatFormatting.BLUE));
                }

                if(compound.getInt("Mode") == 2)
                {
                    tooltip.add(new TranslatableComponent("hose.travelersbackpack.current_mode_spill").withStyle(ChatFormatting.BLUE));
                }

                if(compound.getInt("Mode") == 3)
                {
                    tooltip.add(new TranslatableComponent("hose.travelersbackpack.current_mode_drink").withStyle(ChatFormatting.BLUE));
                }

                if(compound.getInt("Tank") == 1)
                {
                    tooltip.add(new TranslatableComponent("hose.travelersbackpack.current_tank_left").withStyle(ChatFormatting.BLUE));
                }

                if(compound.getInt("Tank") == 2)
                {
                    tooltip.add(new TranslatableComponent("hose.travelersbackpack.current_tank_right").withStyle(ChatFormatting.BLUE));
                }
            }
        }
    }

    @Override
    public Component getName(ItemStack stack)
    {
        int x = getHoseMode(stack);
        String mode = "";
        String localizedName = new TranslatableComponent("item.travelersbackpack.hose").getString();
        String suckMode = new TranslatableComponent("item.travelersbackpack.hose.suck").getString();
        String spillMode = new TranslatableComponent("item.travelersbackpack.hose.spill").getString();
        String drinkMode = new TranslatableComponent("item.travelersbackpack.hose.drink").getString();

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

        return new TextComponent(localizedName + mode);
    }

    public CompoundTag getCompoundTag(ItemStack stack)
    {
        if(stack.getTag() == null)
        {
            stack.setTag(new CompoundTag());
        }

        CompoundTag tag = stack.getTag();

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