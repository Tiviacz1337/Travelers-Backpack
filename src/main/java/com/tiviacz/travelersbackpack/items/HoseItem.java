package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.util.FluidStackHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.item.component.CustomData;
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
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.List;
import java.util.Optional;

public class HoseItem extends Item {
    public HoseItem(Properties properties) {
        //First int is always mode, second int is always tank
        super(properties.component(ModDataComponents.HOSE_MODES, List.of(1, 1)));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        if(getHoseMode(stack) == DRINK_MODE) {
            return UseAnim.DRINK;
        }
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity pEntity) {
        return 24;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(AttachmentUtils.isWearingBackpack(player) && hand == InteractionHand.MAIN_HAND) {
            BackpackWrapper wrapper = AttachmentUtils.getBackpackWrapper(player, AttachmentUtils.UPGRADES_ONLY);
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
                            boolean canFill = tank.isEmpty() || FluidStack.isSameFluidSameComponents(tank.getFluid(), fluidStack);
                            if(canFill && (fluidStack.getAmount() + tankAmount <= tank.getCapacity())) {
                                ItemStack actualFluid = pickup.pickupBlock(player, level, blockpos, blockstate1);
                                if(!actualFluid.isEmpty()) {
                                    SoundEvent bucketFill = Optional.ofNullable(fluidStack.getFluidType().getSound(SoundActions.BUCKET_FILL)).orElse(fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL);
                                    level.playSound(player, result.getBlockPos(), bucketFill, SoundSource.BLOCKS, 1.0F, 1.0F);
                                    tank.fill(new FluidStack(fluid, Reference.BUCKET), IFluidHandler.FluidAction.EXECUTE);
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
                    if(tank.getFluid().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains("PotionType")) {
                        int potionType = tank.getFluid().get(DataComponents.CUSTOM_DATA).copyTag().getInt("PotionType");
                        if(potionType == 1) {
                            if(tank.getFluidAmount() >= Reference.POTION) {
                                ItemStack potionStack = FluidStackHelper.getSplashItemStackFromFluidStack(tank.getFluid());
                                int drainAmount = ServerActions.throwPotion(level, player, potionStack, true);
                                tank.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
                                return InteractionResultHolder.success(stack);
                            }
                        } else if(potionType == 2) {
                            if(tank.getFluidAmount() >= Reference.POTION) {
                                ItemStack potionStack = FluidStackHelper.getLingeringItemStackFromFluidStack(tank.getFluid());
                                int drainAmount = ServerActions.throwPotion(level, player, potionStack, false);
                                tank.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
                                return InteractionResultHolder.success(stack);
                            }
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
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        ItemStack stack = player.getItemInHand(context.getHand());
        if(AttachmentUtils.isWearingBackpack(player) && context.getHand() == InteractionHand.MAIN_HAND) {
            Optional<IFluidHandler> fluidHandler = FluidUtil.getFluidHandler(level, pos, direction);
            BackpackWrapper wrapper = AttachmentUtils.getBackpackWrapper(player, AttachmentUtils.UPGRADES_ONLY);
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
                            SoundEvent bucketFill = Optional.ofNullable(fluidStack.getFluidType().getSound(SoundActions.BUCKET_FILL)).orElse(SoundEvents.BUCKET_FILL);
                            level.playSound(player, pos, bucketFill, SoundSource.BLOCKS, 1.0F, 1.0F);
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
                            boolean canFill = tank.isEmpty() || FluidStack.isSameFluidSameComponents(tank.getFluid(), fluidStack);
                            if(canFill && (fluidStack.getAmount() + tankAmount <= tank.getCapacity())) {
                                ItemStack actualFluid = pickup.pickupBlock(player, level, blockpos, blockstate1);
                                if(!actualFluid.isEmpty()) {
                                    SoundEvent bucketFill = Optional.ofNullable(fluidStack.getFluidType().getSound(SoundActions.BUCKET_FILL)).orElse(fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL);
                                    level.playSound(player, result.getBlockPos(), bucketFill, SoundSource.BLOCKS, 1.0F, 1.0F);
                                    tank.fill(new FluidStack(fluid, Reference.BUCKET), IFluidHandler.FluidAction.EXECUTE);
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
                        SoundEvent bucketFill = Optional.ofNullable(fluidStack.getFluidType().getSound(SoundActions.BUCKET_FILL)).orElse(SoundEvents.BUCKET_FILL);
                        level.playSound(player, pos, bucketFill, SoundSource.BLOCKS, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }

                //Try to splash potion in the world
                if(tank.getFluid().getFluid() == ModFluids.POTION_FLUID.get()) {
                    if(tank.getFluid().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains("PotionType")) {
                        int potionType = tank.getFluid().get(DataComponents.CUSTOM_DATA).copyTag().getInt("PotionType");
                        if(potionType == 1) {
                            if(tank.getFluidAmount() >= Reference.POTION) {
                                ItemStack potionStack = FluidStackHelper.getSplashItemStackFromFluidStack(tank.getFluid());
                                int drainAmount = ServerActions.throwPotion(level, player, potionStack, true);
                                tank.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
                                return InteractionResult.SUCCESS;
                            }
                        } else if(potionType == 2) {
                            if(tank.getFluidAmount() >= Reference.POTION) {
                                ItemStack potionStack = FluidStackHelper.getLingeringItemStackFromFluidStack(tank.getFluid());
                                int drainAmount = ServerActions.throwPotion(level, player, potionStack, false);
                                tank.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }

                //Try to put fluid in the world
                if(!tank.isEmpty()) {
                    BlockState blockState = level.getBlockState(pos);
                    Block block = blockState.getBlock();
                    Fluid fluid = tank.getFluid().getFluid();
                    if(tank.getFluidAmount() >= Reference.BUCKET && fluid instanceof FlowingFluid flowingFluid) {
                        if(block instanceof LiquidBlockContainer container && container.canPlaceLiquid(player, level, pos, blockState, fluid)) {
                            container.placeLiquid(level, pos, blockState, flowingFluid.getSource(false));
                            SoundEvent bucketEmpty = Optional.ofNullable(fluid.getFluidType().getSound(SoundActions.BUCKET_EMPTY)).orElse(SoundEvents.BUCKET_EMPTY);
                            level.playSound(player, pos, bucketEmpty, SoundSource.BLOCKS, 1.0F, 1.0F);
                            tank.drain(Reference.BUCKET, IFluidHandler.FluidAction.EXECUTE);
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
                            return InteractionResult.SUCCESS;
                        }
                        if(fluidStack.getAmount() >= Reference.BUCKET) {
                            if(!level.isClientSide && flag && !level.getBlockState(newPos).liquid()) {
                                level.destroyBlock(newPos, false);
                            }

                            if(level.setBlock(newPos, fluidStack.getFluid().defaultFluidState().createLegacyBlock(), 3)) {
                                SoundEvent bucketEmpty = Optional.ofNullable(fluidStack.getFluid().getFluidType().getSound(SoundActions.BUCKET_EMPTY)).orElse(SoundEvents.BUCKET_EMPTY);
                                level.playSound(player, newPos, bucketEmpty, SoundSource.BLOCKS, 1.0F, 1.0F);
                                tank.drain(Reference.BUCKET, IFluidHandler.FluidAction.EXECUTE);
                                level.updateNeighborsAt(newPos, fluidStack.getFluid().defaultFluidState().createLegacyBlock().getBlock());
                            }
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
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        if(entityLiving instanceof Player player) {
            if(AttachmentUtils.isWearingBackpack(player)) {
                BackpackWrapper wrapper = AttachmentUtils.getBackpackWrapper(player, AttachmentUtils.UPGRADES_ONLY);
                if(!wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                    return stack;
                }
                FluidTank tank = this.getSelectedFluidTank(stack, wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get());
                if(getHoseMode(stack) == DRINK_MODE) {
                    if(tank != null) {
                        if(ServerActions.setFluidEffect(level, player, tank)) {
                            int drainAmount = EffectFluidRegistry.getHighestFluidEffectAmount(tank.getFluid().getFluid());
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
        if(AttachmentUtils.isWearingBackpack(player) && hand == InteractionHand.MAIN_HAND && getHoseMode(stack) == SUCK_MODE) {
            BackpackWrapper wrapper = AttachmentUtils.getBackpackWrapper(player, AttachmentUtils.UPGRADES_ONLY);
            if(!wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                return InteractionResult.PASS;
            }
            FluidTank tank = this.getSelectedFluidTank(stack, wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get());
            Fluid milk = BuiltInRegistries.FLUID.get(ResourceLocation.fromNamespaceAndPath("minecraft", "milk"));
            if(milk != null) {
                if(entity instanceof Cow) {
                    int tankAmount = tank.isEmpty() ? 0 : tank.getFluidAmount();
                    FluidStack milkStack = new FluidStack(milk, Reference.BUCKET);
                    if(milkStack.getFluid() != Fluids.EMPTY) {
                        if((tank.isEmpty() || FluidStack.isSameFluidSameComponents(tank.getFluid(), milkStack)) && milkStack.getAmount() + tankAmount <= tank.getCapacity()) {
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
        if(stack.has(ModDataComponents.HOSE_MODES)) {
            //1 = Suck mode
            //2 = Spill mode
            //3 = Drink mode
            return stack.get(ModDataComponents.HOSE_MODES).get(0);
        }
        return NO_ASSIGN;
    }

    public static int getHoseTank(ItemStack stack) {
        //Weird check to avoid unknown crash
        if(stack.getOrDefault(ModDataComponents.HOSE_MODES, List.of()).size() == 2) {
            //1 = Left tank
            //2 = Right tank
            return stack.get(ModDataComponents.HOSE_MODES).get(1);
        }
        return 0;
    }

    public FluidTank getSelectedFluidTank(ItemStack stack, TanksUpgrade upgrade) {
        return getHoseTank(stack) == 1 ? upgrade.getLeftTank() : upgrade.getRightTank();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(stack.has(ModDataComponents.HOSE_MODES)) {
            int mode = stack.get(ModDataComponents.HOSE_MODES).get(0);
            if(mode == SUCK_MODE) {
                tooltipComponents.add(Component.translatable("item.travelersbackpack.hose.suck").withStyle(ChatFormatting.BLUE));
            }
            if(mode == SPILL_MODE) {
                tooltipComponents.add(Component.translatable("item.travelersbackpack.hose.spill").withStyle(ChatFormatting.BLUE));
            }
            if(mode == DRINK_MODE) {
                tooltipComponents.add(Component.translatable("item.travelersbackpack.hose.drink").withStyle(ChatFormatting.BLUE));
            }
            int tank = stack.get(ModDataComponents.HOSE_MODES).get(1);
            if(tank == 1) {
                tooltipComponents.add(Component.translatable("item.travelersbackpack.hose.tank_left").withStyle(ChatFormatting.BLUE));
            }
            if(tank == 2) {
                tooltipComponents.add(Component.translatable("item.travelersbackpack.hose.tank_right").withStyle(ChatFormatting.BLUE));
            }
        }
    }
}