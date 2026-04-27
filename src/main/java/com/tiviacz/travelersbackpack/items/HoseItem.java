package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.advancements.ActionTypeTrigger;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.util.FluidStackHelper;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class HoseItem extends Item {
    public HoseItem(Properties properties) {
        //First int is always mode, second int is always tank
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        if(getHoseMode(stack) == DRINK_MODE) {
            return UseAnim.DRINK;
        }
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 24;
    }

   /* @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!stack.hasTag()) {
            this.setCompoundTag(stack);
        }
        if(CapabilityUtils.isWearingBackpack(player) && hand == InteractionHand.MAIN_HAND) {
            BackpackWrapper wrapper = CapabilityUtils.getBackpackWrapper(player, CapabilityUtils.UPGRADES_ONLY.get());
            if(!wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                return InteractionResultHolder.pass(stack);
            }
            FluidTank tank = this.getSelectedFluidTank(stack, wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get());

            if(getHoseMode(stack) == SUCK_MODE) {
                //Pick fluid from block
                BlockHitResult result = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
                BlockPos blockpos = result.getBlockPos();
                Direction direction1 = result.getDirection();
                BlockPos blockpos1 = blockpos.relative(result.getDirection());

                if(level.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos1, direction1, stack)) {
                    BlockState blockstate1 = level.getBlockState(blockpos);
                    if(blockstate1.getBlock() instanceof BucketPickup pickup) {
                        Fluid fluid = blockstate1.getFluidState().getType();
                        if(fluid != Fluids.EMPTY) {
                            FluidStack fluidStack = new FluidStack(fluid, Reference.BUCKET);
                            int tankAmount = tank.isEmpty() ? 0 : tank.getFluidAmount();
                            boolean canFill = tank.isEmpty() || FluidStack.areFluidStackTagsEqual(tank.getFluid(), fluidStack);
                            if(canFill && (fluidStack.getAmount() + tankAmount <= tank.getCapacity())) {
                                ItemStack actualFluid = pickup.pickupBlock(level, blockpos, blockstate1);
                                if(!actualFluid.isEmpty()) {
                                    SoundEvent bucketFill = Optional.ofNullable(fluidStack.getFluid().getFluidType().getSound(SoundActions.BUCKET_FILL)).orElse(fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL);
                                    level.playSound(player, result.getBlockPos(), bucketFill, SoundSource.BLOCKS, 1.0F, 1.0F);
                                    // level.playSound(player, result.getBlockPos(), fluidStack.getFluid().getFluidType().getSound(SoundActions.BUCKET_FILL) == null ? (fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL) : fluidStack.getFluid().getFluidType().getSound(SoundActions.BUCKET_FILL), SoundSource.BLOCKS, 1.0F, 1.0F);
                                    tank.fill(new FluidStack(fluid, Reference.BUCKET), IFluidHandler.FluidAction.EXECUTE);
                                    triggerAdvancement(player, ActionTypeTrigger.HOSE_SUCK);
                                    return InteractionResultHolder.success(stack);
                                }
                            }
                        }
                    }
                }
            }

            if(getHoseMode(stack) == SPILL_MODE) {
                //Try to splash potion in the world
                if(tank.getFluid().getFluid() == ModFluids.POTION_FLUID.get()) {
                    if(tank.getFluid().getOrCreateTag().contains("Splash")) {
                        if(tank.getFluidAmount() >= Reference.POTION) {
                            ItemStack potionStack = FluidStackHelper.getSplashItemStackFromFluidStack(tank.getFluid());
                            int drainAmount = ServerActions.throwPotion(level, player, potionStack, true);
                            tank.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
                            triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL_POTION);
                            return InteractionResultHolder.success(stack);
                        }
                    }
                    if(tank.getFluid().getOrCreateTag().contains("Lingering")) {
                        if(tank.getFluidAmount() >= Reference.POTION) {
                            ItemStack potionStack = FluidStackHelper.getLingeringItemStackFromFluidStack(tank.getFluid());
                            int drainAmount = ServerActions.throwPotion(level, player, potionStack, false);
                            tank.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
                            triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL_POTION);
                            return InteractionResultHolder.success(stack);
                        }
                    }
                }
            }

            if(getHoseMode(stack) == DRINK_MODE) {
                if(!tank.isEmpty()) {
                    if(EffectFluidRegistry.hasExecutableEffects(tank.getFluid(), level, player)) {
                        player.startUsingItem(hand);
                        return InteractionResultHolder.success(stack);
                    }
                }
            }
        }
        return InteractionResultHolder.pass(stack);
    } */

  /*  @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        ItemStack stack = player.getItemInHand(context.getHand());
        if(!stack.hasTag()) {
            this.setCompoundTag(stack);
        }
        if(CapabilityUtils.isWearingBackpack(player) && context.getHand() == InteractionHand.MAIN_HAND) {
            LazyOptional<IFluidHandler> fluidHandler = FluidUtil.getFluidHandler(level, pos, direction);
            BackpackWrapper wrapper = CapabilityUtils.getBackpackWrapper(player, CapabilityUtils.UPGRADES_ONLY.get());
            if(!wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                return InteractionResult.PASS;
            }
            FluidTank tank = this.getSelectedFluidTank(stack, wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get());

            if(getHoseMode(stack) == SUCK_MODE) {
                //Transfer fluid from fluid handler
                if(fluidHandler.isPresent()) {
                    if(!fluidHandler.map(h -> h.getFluidInTank(0).isEmpty()).get()) {
                        FluidStack fluidStack = FluidUtil.tryFluidTransfer(tank, fluidHandler.orElse(null), Reference.BUCKET, true);
                        if(!fluidStack.isEmpty()) {
                            SoundEvent bucketFill = Optional.ofNullable(fluidStack.getFluid().getFluidType().getSound(SoundActions.BUCKET_FILL)).orElse(SoundEvents.BUCKET_FILL);
                            level.playSound(player, pos, bucketFill, SoundSource.BLOCKS, 1.0F, 1.0F);
                            //level.playSound(player, pos, fluidStack.getFluid().getFluidType().getSound(SoundActions.BUCKET_FILL), SoundSource.BLOCKS, 1.0F, 1.0F);
                            triggerAdvancement(player, ActionTypeTrigger.HOSE_SUCK);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
                //Pick fluid from block
                BlockHitResult result = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
                BlockPos blockpos = result.getBlockPos();
                Direction direction1 = result.getDirection();
                BlockPos blockpos1 = blockpos.relative(direction);

                if(level.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos1, direction1, stack)) {
                    BlockState blockstate1 = level.getBlockState(blockpos);
                    if(blockstate1.getBlock() instanceof BucketPickup pickup) {
                        Fluid fluid = blockstate1.getFluidState().getType();
                        if(fluid != Fluids.EMPTY) {
                            FluidStack fluidStack = new FluidStack(fluid, Reference.BUCKET);
                            int tankAmount = tank.isEmpty() ? 0 : tank.getFluidAmount();
                            boolean canFill = tank.isEmpty() || FluidStack.areFluidStackTagsEqual(tank.getFluid(), fluidStack);
                            if(canFill && (fluidStack.getAmount() + tankAmount <= tank.getCapacity())) {
                                ItemStack actualFluid = pickup.pickupBlock(level, blockpos, blockstate1);
                                if(!actualFluid.isEmpty()) {
                                    SoundEvent bucketFill = Optional.ofNullable(fluidStack.getFluid().getFluidType().getSound(SoundActions.BUCKET_FILL)).orElse(fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL);
                                    level.playSound(player, result.getBlockPos(), bucketFill, SoundSource.BLOCKS, 1.0F, 1.0F);
                                    // level.playSound(player, result.getBlockPos(), fluidStack.getFluid().getFluidType().getSound(SoundActions.BUCKET_FILL) == null ? (fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL) : fluidStack.getFluid().getFluidType().getSound(SoundActions.BUCKET_FILL), SoundSource.BLOCKS, 1.0F, 1.0F);
                                    tank.fill(new FluidStack(fluid, Reference.BUCKET), IFluidHandler.FluidAction.EXECUTE);
                                    triggerAdvancement(player, ActionTypeTrigger.HOSE_SUCK);
                                    return InteractionResult.SUCCESS;
                                }
                            }
                        }
                    }
                }
            }
            if(getHoseMode(stack) == SPILL_MODE) {
                //Transfer fluid to fluid handler
                if(fluidHandler.isPresent() && !tank.isEmpty()) {
                    FluidStack fluidStack = FluidUtil.tryFluidTransfer(fluidHandler.orElse(null), tank, Reference.BUCKET, true);
                    if(!fluidStack.isEmpty()) {
                        SoundEvent bucketFill = Optional.ofNullable(fluidStack.getFluid().getFluidType().getSound(SoundActions.BUCKET_FILL)).orElse(SoundEvents.BUCKET_FILL);
                        level.playSound(player, pos, bucketFill, SoundSource.BLOCKS, 1.0F, 1.0F);
                        //level.playSound(player, pos, fluidStack.getFluid().getFluidType().getSound(SoundActions.BUCKET_FILL), SoundSource.BLOCKS, 1.0F, 1.0F);
                        triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL);
                        return InteractionResult.SUCCESS;
                    }
                }

                //Try to splash potion in the world
                if(tank.getFluid().getFluid() == ModFluids.POTION_FLUID.get()) {
                    if(tank.getFluid().getOrCreateTag().contains("Splash")) {
                        if(tank.getFluidAmount() >= Reference.POTION) {
                            ItemStack potionStack = FluidStackHelper.getSplashItemStackFromFluidStack(tank.getFluid());
                            int drainAmount = ServerActions.throwPotion(level, player, potionStack, true);
                            tank.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
                            triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL_POTION);
                            return InteractionResult.SUCCESS;
                        }
                    }
                    if(tank.getFluid().getOrCreateTag().contains("Lingering")) {
                        if(tank.getFluidAmount() >= Reference.POTION) {
                            ItemStack potionStack = FluidStackHelper.getLingeringItemStackFromFluidStack(tank.getFluid());
                            int drainAmount = ServerActions.throwPotion(level, player, potionStack, false);
                            tank.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
                            triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL_POTION);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }

                //Try to put fluid in the world
                if(!tank.isEmpty()) {
                    BlockState blockState = level.getBlockState(pos);
                    Block block = blockState.getBlock();
                    Fluid fluid = tank.getFluid().getFluid();
                    if(tank.getFluidAmount() >= Reference.BUCKET && fluid instanceof FlowingFluid flowingFluid) {
                        if(block instanceof LiquidBlockContainer container && container.canPlaceLiquid(level, pos, blockState, fluid)) {
                            container.placeLiquid(level, pos, blockState, flowingFluid.getSource(false));
                            SoundEvent bucketEmpty = Optional.ofNullable(fluid.getFluidType().getSound(SoundActions.BUCKET_EMPTY)).orElse(SoundEvents.BUCKET_EMPTY);
                            level.playSound(player, pos, bucketEmpty, SoundSource.BLOCKS, 1.0F, 1.0F);
                            //level.playSound(player, pos, fluid.getFluidType().getSound(SoundActions.BUCKET_EMPTY), SoundSource.BLOCKS, 1.0F, 1.0F);
                            tank.drain(Reference.BUCKET, IFluidHandler.FluidAction.EXECUTE);
                            triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL);
                            return InteractionResult.SUCCESS;
                        }
                    }
                    int x = pos.getX();
                    int y = pos.getY();
                    int z = pos.getZ();
                    if(!level.getBlockState(pos).canBeReplaced(fluid)) {
                        switch(context.getClickedFace()) {
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

                    BlockPos newPos = new BlockPos(x, y, z);
                    FluidStack fluidStack = tank.getFluid();
                    if(level.getBlockState(newPos).canBeReplaced(fluid) && fluid.getFluidType().canBePlacedInLevel(level, newPos, fluidStack)) {
                        boolean flag = !level.getBlockState(newPos).isSolid();
                        if(level.dimensionType().ultraWarm() && fluidStack.getFluid().is(FluidTags.WATER)) {
                            tank.drain(Reference.BUCKET, IFluidHandler.FluidAction.EXECUTE);
                            level.playSound(null, newPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);
                            for(int i = 0; i < 3; ++i) {
                                double d0 = newPos.getX() + level.getRandom().nextDouble();
                                double d1 = newPos.getY() + level.getRandom().nextDouble() * 0.5D + 0.5D;
                                double d2 = newPos.getZ() + level.getRandom().nextDouble();
                                level.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                            }
                            triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL);
                            return InteractionResult.SUCCESS;
                        }
                        if(fluidStack.getAmount() >= Reference.BUCKET) {
                            if(!level.isClientSide && flag && !level.getBlockState(newPos).liquid()) {
                                level.destroyBlock(newPos, false);
                            }

                            if(level.setBlock(newPos, fluidStack.getFluid().defaultFluidState().createLegacyBlock(), 3)) {
                                SoundEvent bucketEmpty = Optional.ofNullable(fluidStack.getFluid().getFluidType().getSound(SoundActions.BUCKET_EMPTY)).orElse(SoundEvents.BUCKET_EMPTY);
                                level.playSound(player, newPos, bucketEmpty, SoundSource.BLOCKS, 1.0F, 1.0F);
                                //level.playSound(player, newPos, fluidStack.getFluid().getFluidType().getSound(SoundActions.BUCKET_EMPTY), SoundSource.BLOCKS, 1.0F, 1.0F);
                                tank.drain(Reference.BUCKET, IFluidHandler.FluidAction.EXECUTE);
                                level.updateNeighborsAt(newPos, fluidStack.getFluid().defaultFluidState().createLegacyBlock().getBlock());
                            }
                            triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
            if(getHoseMode(stack) == DRINK_MODE) {
                if(!tank.isEmpty()) {
                    if(EffectFluidRegistry.hasExecutableEffects(tank.getFluid(), level, player)) {
                        player.startUsingItem(context.getHand());
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.FAIL;
    }*/

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(CapabilityUtils.isWearingBackpack(player) && hand == InteractionHand.MAIN_HAND) {
            BackpackWrapper wrapper = CapabilityUtils.getBackpackWrapper(player, CapabilityUtils.UPGRADES_ONLY.get());
            if(!wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                return InteractionResultHolder.pass(stack);
            }
            FluidTank tank = this.getSelectedFluidTank(stack, wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get());
            LazyOptional<IFluidHandler> fluidHandler = LazyOptional.empty();

            int hoseMode = getHoseMode(stack);
            BlockHitResult hitResult = getPlayerPOVHitResult(level, player, hoseMode == SUCK_MODE ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
            var hitType = hitResult.getType();
            if(hitType == BlockHitResult.Type.BLOCK) {
                BlockPos pos = hitResult.getBlockPos();
                Direction direction = hitResult.getDirection();
                BlockPos directionOffsetPos = pos.relative(direction);

                //Check for fluid storage like in-world tanks
                fluidHandler = FluidUtil.getFluidHandler(level, pos, direction);

                if(hoseMode == SUCK_MODE) {
                    //Transfer fluid from fluid handler
                    AtomicBoolean success = new AtomicBoolean(false);
                    fluidHandler.ifPresent(handler -> {
                        if(!handler.getFluidInTank(0).isEmpty()) {
                            FluidStack result = FluidUtil.tryFluidTransfer(tank, handler, handler.getFluidInTank(0).getAmount(), false).copy();
                            if(result != null && !result.isEmpty()) {
                                FluidUtil.tryFluidTransfer(tank, handler, handler.getFluidInTank(0).getAmount(), true);
                                SoundEvent bucketFill = Optional.ofNullable(result.getFluid().getFluidType().getSound(SoundActions.BUCKET_FILL)).orElse(SoundEvents.BUCKET_FILL);
                                level.playSound(player, pos, bucketFill, SoundSource.BLOCKS, 1.0F, 1.0F);
                                triggerAdvancement(player, ActionTypeTrigger.HOSE_SUCK);
                                success.set(true);
                            }
                        }
                    });
                    if(success.get()) {
                        return InteractionResultHolder.success(stack);
                    }

                    //Pick fluid from block
                    BlockHitResult result = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
                    BlockPos blockpos = result.getBlockPos();
                    Direction direction1 = result.getDirection();
                    BlockPos blockpos1 = blockpos.relative(result.getDirection());
                    if(level.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos1, direction1, stack)) {
                        BlockState blockState = level.getBlockState(pos);
                        if(blockState.getBlock() instanceof BucketPickup bucketPickupBlock) {
                            Fluid fluid = blockState.getFluidState().getType();
                            if(fluid != Fluids.EMPTY) {
                                FluidStack fluidStack = new FluidStack(fluid, Reference.BUCKET);
                                int tankAmount = tank.isEmpty() ? 0 : tank.getFluidAmount();
                                boolean canFill = tank.isEmpty() || tank.getFluid().isFluidEqual(fluidStack);
                                if(canFill && (fluidStack.getAmount() + tankAmount <= tank.getCapacity())) {
                                    ItemStack taken = bucketPickupBlock.pickupBlock(level, pos, blockState);
                                    if(!taken.isEmpty()) {
                                        player.awardStat(Stats.ITEM_USED.get(this));
                                        bucketPickupBlock.getPickupSound().ifPresent(soundEvent -> player.playSound(soundEvent, 1.0F, 1.0F));
                                        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
                                        tank.fill(new FluidStack(fluid, Reference.BUCKET), IFluidHandler.FluidAction.EXECUTE);
                                        triggerAdvancement(player, ActionTypeTrigger.HOSE_SUCK);
                                        //ItemStack result = ItemUtils.createFilledResult(itemStack, player, taken);
                                        if(!level.isClientSide()) {
                                            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, taken);
                                        }
                                        return InteractionResultHolder.success(stack);
                                    }
                                }
                            }
                        }
                    }
                }
                if(hoseMode == SPILL_MODE) {
                    //Transfer fluid to fluid handler
                    AtomicBoolean success = new AtomicBoolean(false);
                    fluidHandler.ifPresent(handler -> {
                        if(!tank.isEmpty()) {
                            FluidStack result = FluidUtil.tryFluidTransfer(handler, tank, tank.getFluidAmount(), false).copy();
                            if(result != null && !result.isEmpty()) {
                                FluidUtil.tryFluidTransfer(handler, tank, tank.getFluidAmount(), true);
                                SoundEvent bucketFill = Optional.ofNullable(result.getFluid().getFluidType().getSound(SoundActions.BUCKET_FILL)).orElse(SoundEvents.BUCKET_FILL);
                                level.playSound(player, pos, bucketFill, SoundSource.BLOCKS, 1.0F, 1.0F);
                                triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL);
                                success.set(true);
                            }
                        }
                    });
                    if(success.get()) {
                        return InteractionResultHolder.success(stack);
                    }

                    //Try to splash potion in the world
                    if(spillPotion(tank, level, player) == InteractionResult.SUCCESS) {
                        return InteractionResultHolder.success(stack);
                    }

                    FluidStack fluidStack = tank.getFluid();
                    Fluid fluid = fluidStack.getFluid();
                    BlockState clicked = level.getBlockState(pos);
                    BlockPos placePos = clicked.getBlock() instanceof LiquidBlockContainer && fluid == Fluids.WATER ? pos : directionOffsetPos;
                    if(tank.getFluidAmount() >= Reference.BUCKET && this.emptyContents(fluidStack, player, level, placePos, hitResult)) {
                        //this.checkExtraContent(player, level, itemStack, placePos);
                        if(player instanceof ServerPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, placePos, stack);
                        }

                        player.awardStat(Stats.ITEM_USED.get(this));
                        tank.drain(Reference.BUCKET, IFluidHandler.FluidAction.EXECUTE);
                        triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL);
                        //ItemStack emptyResult = ItemUtils.createFilledResult(itemStack, player, getEmptySuccessItem(itemStack, player));
                        return InteractionResultHolder.success(stack);
                    }
                }

                if(hoseMode == DRINK_MODE) {
                    if(drink(tank, level, player, hand) == InteractionResult.SUCCESS) {
                        return InteractionResultHolder.success(stack);
                    }
                }
            } else {
                if(hoseMode == SPILL_MODE) {
                    //Try to splash potion in the world
                    if(spillPotion(tank, level, player) == InteractionResult.SUCCESS) {
                        return InteractionResultHolder.success(stack);
                    }
                }
                if(hoseMode == DRINK_MODE) {
                    if(drink(tank, level, player, hand) == InteractionResult.SUCCESS) {
                        return InteractionResultHolder.success(stack);
                    }
                }
                return InteractionResultHolder.pass(stack);
            }
        }
        return InteractionResultHolder.fail(stack);
    }

    public InteractionResult drink(FluidTank tank, Level level, Player player, InteractionHand hand) {
        if(!tank.isEmpty()) {
            if(EffectFluidRegistry.hasExecutableEffects(tank.getFluid(), level, player)) {
                player.startUsingItem(hand);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public InteractionResult spillPotion(FluidTank tank, Level level, Player player) {
        //Try to splash potion in the world
        if(tank.getFluid().getFluid() == ModFluids.POTION_FLUID.get()) {
            if(tank.getFluid().getOrCreateTag().contains("Splash")) {
                if(tank.getFluidAmount() >= Reference.POTION) {
                    ItemStack potionStack = FluidStackHelper.getSplashItemStackFromFluidStack(tank.getFluid());
                    int drainAmount = ServerActions.throwPotion(level, player, potionStack, true);
                    tank.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
                    triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL_POTION);
                    return InteractionResult.SUCCESS;
                }
            }
            if(tank.getFluid().getOrCreateTag().contains("Lingering")) {
                if(tank.getFluidAmount() >= Reference.POTION) {
                    ItemStack potionStack = FluidStackHelper.getLingeringItemStackFromFluidStack(tank.getFluid());
                    int drainAmount = ServerActions.throwPotion(level, player, potionStack, false);
                    tank.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
                    triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL_POTION);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    public boolean emptyContents(FluidStack fluidStack, @Nullable Player pPlayer, Level pLevel, BlockPos pPos, @Nullable BlockHitResult pResult) {
        Fluid fluid = fluidStack.getFluid();
        if (!(fluid instanceof FlowingFluid)) {
            return false;
        } else {
            BlockState blockstate = pLevel.getBlockState(pPos);
            Block block = blockstate.getBlock();
            boolean flag = blockstate.canBeReplaced(fluid);
            boolean flag1 = blockstate.isAir() || flag || block instanceof LiquidBlockContainer && ((LiquidBlockContainer)block).canPlaceLiquid(pLevel, pPos, blockstate, fluid);
            if (!flag1) {
                return pResult != null && this.emptyContents(fluidStack, pPlayer, pLevel, pResult.getBlockPos().relative(pResult.getDirection()), null);
            } else if (pLevel.dimensionType().ultraWarm() && fluid.is(FluidTags.WATER)) {
                int i = pPos.getX();
                int j = pPos.getY();
                int k = pPos.getZ();
                pLevel.playSound(pPlayer, pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (pLevel.random.nextFloat() - pLevel.random.nextFloat()) * 0.8F);

                for(int l = 0; l < 8; ++l) {
                    pLevel.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
                }

                return true;
            } else if (block instanceof LiquidBlockContainer && ((LiquidBlockContainer)block).canPlaceLiquid(pLevel,pPos,blockstate,fluid)) {
                ((LiquidBlockContainer)block).placeLiquid(pLevel, pPos, blockstate, ((FlowingFluid)fluid).getSource(false));
                this.playEmptySound(fluidStack, pPlayer, pLevel, pPos);
                return true;
            } else {
                if (!pLevel.isClientSide && flag && !blockstate.liquid()) {
                    pLevel.destroyBlock(pPos, true);
                }

                if (!pLevel.setBlock(pPos, fluid.defaultFluidState().createLegacyBlock(), 11) && !blockstate.getFluidState().isSource()) {
                    return false;
                } else {
                    this.playEmptySound(fluidStack, pPlayer, pLevel, pPos);
                    return true;
                }
            }
        }
    }

    protected void playEmptySound(FluidStack fluidStack, @Nullable Player pPlayer, LevelAccessor pLevel, BlockPos pPos) {
        SoundEvent soundevent = fluidStack.getFluid().getFluidType().getSound(pPlayer, pLevel, pPos, net.minecraftforge.common.SoundActions.BUCKET_EMPTY);
        if(soundevent == null) soundevent = fluidStack.getFluid().is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        pLevel.playSound(pPlayer, pPos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
        pLevel.gameEvent(pPlayer, GameEvent.FLUID_PLACE, pPos);
    }

    public void triggerAdvancement(Player player, String type) {
        if(player instanceof ServerPlayer serverPlayer) {
            ActionTypeTrigger.INSTANCE.trigger(serverPlayer, type);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        if(entityLiving instanceof Player player) {
            if(CapabilityUtils.isWearingBackpack(player)) {
                BackpackWrapper wrapper = CapabilityUtils.getBackpackWrapper(player, CapabilityUtils.UPGRADES_ONLY.get());
                if(!wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                    return stack;
                }
                FluidTank tank = this.getSelectedFluidTank(stack, wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get());
                if(getHoseMode(stack) == DRINK_MODE) {
                    if(tank != null) {
                        if(ServerActions.setFluidEffect(level, player, tank)) {
                            int drainAmount = EffectFluidRegistry.getHighestFluidEffectAmount(tank.getFluid().getFluid());
                            if(tank.getFluid().getFluid() == ModFluids.POTION_FLUID.get()) {
                                triggerAdvancement(player, ActionTypeTrigger.HOSE_DRINK_POTION);
                            }
                            triggerAdvancement(player, ActionTypeTrigger.HOSE_DRINK);

                            tank.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                }
            }
        }
        return stack;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if(CapabilityUtils.isWearingBackpack(player) && hand == InteractionHand.MAIN_HAND && getHoseMode(stack) == SUCK_MODE) {
            BackpackWrapper wrapper = CapabilityUtils.getBackpackWrapper(player, CapabilityUtils.UPGRADES_ONLY.get());
            if(!wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                return InteractionResult.PASS;
            }
            FluidTank tank = this.getSelectedFluidTank(stack, wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get());
            Fluid milk = BuiltInRegistries.FLUID.get(new ResourceLocation("minecraft", "milk"));
            if(milk != null) {
                if(entity instanceof Cow) {
                    int tankAmount = tank.isEmpty() ? 0 : tank.getFluidAmount();
                    FluidStack milkStack = new FluidStack(milk, Reference.BUCKET);
                    if(milkStack.getFluid() != Fluids.EMPTY) {
                        if((tank.isEmpty() || FluidStack.areFluidStackTagsEqual(tank.getFluid(), milkStack)) && milkStack.getAmount() + tankAmount <= tank.getCapacity()) {
                            tank.fill(milkStack, IFluidHandler.FluidAction.EXECUTE);
                            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    public static final int NO_ASSIGN = 0;
    public static final int SUCK_MODE = 1;
    public static final int SPILL_MODE = 2;
    public static final int DRINK_MODE = 3;

    public static int getHoseMode(ItemStack stack) {
        if(NbtHelper.has(stack, ModDataHelper.HOSE_MODES)) {
            //1 = Suck mode
            //2 = Spill mode
            //3 = Drink mode
            return ((List<Integer>)NbtHelper.get(stack, ModDataHelper.HOSE_MODES)).get(0);
        }
        return NO_ASSIGN;
    }

    public static int getHoseTank(ItemStack stack) {
        if(NbtHelper.has(stack, ModDataHelper.HOSE_MODES)) {
            //1 = Left tank
            //2 = Right tank
            return ((List<Integer>)NbtHelper.get(stack, ModDataHelper.HOSE_MODES)).get(1);
        }
        return 0;
    }

    public FluidTank getSelectedFluidTank(ItemStack stack, TanksUpgrade upgrade) {
        return getHoseTank(stack) == 1 ? upgrade.getLeftTank() : upgrade.getRightTank();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Level context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(NbtHelper.has(stack, ModDataHelper.HOSE_MODES)) {
            int mode = ((List<Integer>)NbtHelper.get(stack, ModDataHelper.HOSE_MODES)).get(0);
            if(mode == SUCK_MODE) {
                tooltipComponents.add(Component.translatable("item.travelersbackpack.hose.suck").withStyle(ChatFormatting.BLUE));
            }
            if(mode == SPILL_MODE) {
                tooltipComponents.add(Component.translatable("item.travelersbackpack.hose.spill").withStyle(ChatFormatting.BLUE));
            }
            if(mode == DRINK_MODE) {
                tooltipComponents.add(Component.translatable("item.travelersbackpack.hose.drink").withStyle(ChatFormatting.BLUE));
            }
            int tank = ((List<Integer>)NbtHelper.get(stack, ModDataHelper.HOSE_MODES)).get(1);
            if(tank == 1) {
                tooltipComponents.add(Component.translatable("item.travelersbackpack.hose.tank_left").withStyle(ChatFormatting.BLUE));
            }
            if(tank == 2) {
                tooltipComponents.add(Component.translatable("item.travelersbackpack.hose.tank_right").withStyle(ChatFormatting.BLUE));
            }
        }
    }

    public void setCompoundTag(ItemStack stack) {
        if(!stack.getOrCreateTag().contains(ModDataHelper.HOSE_MODES)) {
            NbtHelper.set(stack, ModDataHelper.HOSE_MODES, List.of(1, 1));
        }
    }
}