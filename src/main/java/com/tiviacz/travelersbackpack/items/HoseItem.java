package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.advancements.ActionTypeTrigger;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.init.ModAdvancements;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.util.FluidStackHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import com.tiviacz.travelersbackpack.util.StacksHandlerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class HoseItem extends Item {
    public HoseItem(Properties properties) {
        //First int is always mode, second int is always tank
        super(properties.component(ModDataComponents.HOSE_MODES, List.of(1, 1)));
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        if(getHoseMode(stack) == DRINK_MODE) {
            return ItemUseAnimation.DRINK;
        }
        return ItemUseAnimation.NONE;
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity pEntity) {
        return consumeTicks();
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if(shouldEmitDrinkingSounds(remainingUseDuration)) {
            emitDrinkingSound(livingEntity.getRandom(), livingEntity);
        }
    }

    public boolean shouldEmitDrinkingSounds(int remainingUseDuration) {
        int i = this.consumeTicks() - remainingUseDuration;
        int j = (int)(this.consumeTicks() * 0.21875F);
        boolean flag = i > j;
        return flag && remainingUseDuration % 4 == 0;
    }

    public int consumeTicks() {
        return (int)(consumeSeconds() * 20.0F);
    }

    public float consumeSeconds() {
        return 1.6F;
    }

    public void emitDrinkingSound(RandomSource random, LivingEntity entity) {
        entity.playSound(SoundEvents.GENERIC_DRINK.value(), 0.5F, Mth.randomBetween(random, 0.9F, 1.0F));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(AttachmentUtils.isWearingBackpack(player) && hand == InteractionHand.MAIN_HAND) {
            BackpackWrapper wrapper = AttachmentUtils.getBackpackWrapper(player, AttachmentUtils.UPGRADES_ONLY.get());
            if(!wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                return InteractionResult.PASS;
            }
            FluidStacksResourceHandler tank = this.getSelectedFluidTank(stack, wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get());

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
                            int tankAmount = StacksHandlerUtils.isEmpty(tank) ? 0 : StacksHandlerUtils.getFluidAmount(tank);
                            boolean canFill = StacksHandlerUtils.isEmpty(tank) || FluidStack.isSameFluidSameComponents(StacksHandlerUtils.getFluid(tank), fluidStack);
                            if(canFill && (fluidStack.getAmount() + tankAmount <= StacksHandlerUtils.getCapacity(tank))) {
                                ItemStack actualFluid = pickup.pickupBlock(player, level, blockpos, blockstate1);
                                if(!actualFluid.isEmpty()) {
                                    SoundEvent bucketFill = Optional.ofNullable(fluidStack.getFluidType().getSound(SoundActions.BUCKET_FILL)).orElse(fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL);
                                    level.playSound(player, result.getBlockPos(), bucketFill, SoundSource.BLOCKS, 1.0F, 1.0F);
                                    StacksHandlerUtils.fill(tank, new FluidStack(fluid, Reference.BUCKET), false);
                                    triggerAdvancement(player, ActionTypeTrigger.HOSE_SUCK);
                                    return InteractionResult.SUCCESS;
                                }
                            }
                        }
                    }
                }
            }

            if(getHoseMode(stack) == SPILL_MODE) {
                //Try to splash potion in the world
                if(StacksHandlerUtils.getFluid(tank).getFluid() == ModFluids.POTION_FLUID.get()) {
                    if(StacksHandlerUtils.getFluid(tank).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains("PotionType")) {
                        int potionType = StacksHandlerUtils.getFluid(tank).get(DataComponents.CUSTOM_DATA).copyTag().getIntOr("PotionType", 0);
                        if(potionType == 1) {
                            if(StacksHandlerUtils.getFluidAmount(tank) >= Reference.POTION) {
                                ItemStack potionStack = FluidStackHelper.getSplashItemStackFromFluidStack(StacksHandlerUtils.getFluid(tank));
                                int drainAmount = ServerActions.throwPotion(level, player, potionStack, true);
                                StacksHandlerUtils.drain(tank, drainAmount, false);
                                triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL_POTION);
                                return InteractionResult.SUCCESS;
                            }
                        } else if(potionType == 2) {
                            if(StacksHandlerUtils.getFluidAmount(tank) >= Reference.POTION) {
                                ItemStack potionStack = FluidStackHelper.getLingeringItemStackFromFluidStack(StacksHandlerUtils.getFluid(tank));
                                int drainAmount = ServerActions.throwPotion(level, player, potionStack, false);
                                StacksHandlerUtils.drain(tank, drainAmount, false);
                                triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL_POTION);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
            }

            if(getHoseMode(stack) == DRINK_MODE) {
                if(!StacksHandlerUtils.isEmpty(tank)) {
                    if(EffectFluidRegistry.hasExecutableEffects(StacksHandlerUtils.getFluid(tank), level, player)) {
                        player.startUsingItem(hand);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        ItemStack stack = player.getItemInHand(context.getHand());
        if(AttachmentUtils.isWearingBackpack(player) && context.getHand() == InteractionHand.MAIN_HAND) {
            Optional<ResourceHandler<FluidResource>> fluidHandler = Optional.ofNullable(level.getCapability(Capabilities.Fluid.BLOCK, pos, direction));
            BackpackWrapper wrapper = AttachmentUtils.getBackpackWrapper(player, AttachmentUtils.UPGRADES_ONLY.get());
            if(!wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                return InteractionResult.PASS;
            }
            FluidStacksResourceHandler tank = this.getSelectedFluidTank(stack, wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get());

            if(getHoseMode(stack) == SUCK_MODE) {
                //Transfer fluid from fluid handler
                AtomicBoolean success = new AtomicBoolean(false);
                fluidHandler.ifPresent(handler -> {
                    if(!handler.getResource(0).isEmpty()) {
                        try(var tx = Transaction.openRoot()) {
                            FluidStack fluidStack = fluidHandler.get().getResource(0).toStack(Reference.BUCKET);
                            int moved = ResourceHandlerUtil.move(fluidHandler.get(), tank, p -> true, Reference.BUCKET, tx);
                            if(moved > 0) {
                                SoundEvent bucketFill = Optional.ofNullable(fluidStack.getFluidType().getSound(SoundActions.BUCKET_FILL)).orElse(SoundEvents.BUCKET_FILL);
                                level.playSound(player, pos, bucketFill, SoundSource.BLOCKS, 1.0F, 1.0F);
                                triggerAdvancement(player, ActionTypeTrigger.HOSE_SUCK);
                                tx.commit();
                                success.set(true);
                            }
                        }
                    }
                });
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
                            int tankAmount = StacksHandlerUtils.isEmpty(tank) ? 0 : StacksHandlerUtils.getFluidAmount(tank);
                            boolean canFill = StacksHandlerUtils.isEmpty(tank) || FluidStack.isSameFluidSameComponents(StacksHandlerUtils.getFluid(tank), fluidStack);
                            if(canFill && (fluidStack.getAmount() + tankAmount <= StacksHandlerUtils.getCapacity(tank))) {
                                ItemStack actualFluid = pickup.pickupBlock(player, level, blockpos, blockstate1);
                                if(!actualFluid.isEmpty()) {
                                    SoundEvent bucketFill = Optional.ofNullable(fluidStack.getFluidType().getSound(SoundActions.BUCKET_FILL)).orElse(fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL);
                                    level.playSound(player, result.getBlockPos(), bucketFill, SoundSource.BLOCKS, 1.0F, 1.0F);
                                    StacksHandlerUtils.fill(tank, new FluidStack(fluid, Reference.BUCKET), false);
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
                AtomicBoolean success = new AtomicBoolean(false);
                fluidHandler.ifPresent(handler -> {
                    if(!StacksHandlerUtils.isEmpty(tank)) {
                        FluidStack fluidStack = tank.getResource(0).toStack(Reference.BUCKET);
                        try(var tx = Transaction.openRoot()) {
                            int moved = ResourceHandlerUtil.move(tank, fluidHandler.get(), p -> true, Reference.BUCKET, tx);
                            if(moved > 0) {
                                SoundEvent bucketFill = Optional.ofNullable(fluidStack.getFluidType().getSound(SoundActions.BUCKET_FILL)).orElse(SoundEvents.BUCKET_FILL);
                                level.playSound(player, pos, bucketFill, SoundSource.BLOCKS, 1.0F, 1.0F);
                                triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL);
                                success.set(true);
                            }
                        }
                    }
                });
                if(success.get()) {
                    return InteractionResult.SUCCESS;
                }

                //Try to splash potion in the world
                if(StacksHandlerUtils.getFluid(tank).getFluid() == ModFluids.POTION_FLUID.get()) {
                    if(StacksHandlerUtils.getFluid(tank).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains("PotionType")) {
                        int potionType = StacksHandlerUtils.getFluid(tank).get(DataComponents.CUSTOM_DATA).copyTag().getIntOr("PotionType", 0);
                        if(potionType == 1) {
                            if(StacksHandlerUtils.getFluidAmount(tank) >= Reference.POTION) {
                                ItemStack potionStack = FluidStackHelper.getSplashItemStackFromFluidStack(StacksHandlerUtils.getFluid(tank));
                                int drainAmount = ServerActions.throwPotion(level, player, potionStack, true);
                                StacksHandlerUtils.drain(tank, drainAmount, false);
                                triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL_POTION);
                                return InteractionResult.SUCCESS;
                            }
                        } else if(potionType == 2) {
                            if(StacksHandlerUtils.getFluidAmount(tank) >= Reference.POTION) {
                                ItemStack potionStack = FluidStackHelper.getLingeringItemStackFromFluidStack(StacksHandlerUtils.getFluid(tank));
                                int drainAmount = ServerActions.throwPotion(level, player, potionStack, false);
                                StacksHandlerUtils.drain(tank, drainAmount, false);
                                triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL_POTION);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }

                //Try to put fluid in the world
                if(!StacksHandlerUtils.isEmpty(tank)) {
                    BlockState blockState = level.getBlockState(pos);
                    Block block = blockState.getBlock();
                    Fluid fluid = StacksHandlerUtils.getFluid(tank).getFluid();
                    if(StacksHandlerUtils.getFluidAmount(tank) >= Reference.BUCKET && fluid instanceof FlowingFluid flowingFluid) {
                        if(block instanceof LiquidBlockContainer container && container.canPlaceLiquid(player, level, pos, blockState, fluid)) {
                            container.placeLiquid(level, pos, blockState, flowingFluid.getSource(false));
                            SoundEvent bucketEmpty = Optional.ofNullable(fluid.getFluidType().getSound(SoundActions.BUCKET_EMPTY)).orElse(SoundEvents.BUCKET_EMPTY);
                            level.playSound(player, pos, bucketEmpty, SoundSource.BLOCKS, 1.0F, 1.0F);
                            StacksHandlerUtils.drain(tank, Reference.BUCKET, false);
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
                    FluidStack fluidStack = StacksHandlerUtils.getFluid(tank);
                    if(level.getBlockState(newPos).canBeReplaced(fluid) && fluid.getFluidType().canBePlacedInLevel(level, newPos, fluidStack)) {
                        boolean flag = !level.getBlockState(newPos).isSolid();
                        boolean ultraWarm = level.dimensionType().attributes().contains(EnvironmentAttributes.WATER_EVAPORATES) && (boolean)level.dimensionType().attributes().get(EnvironmentAttributes.WATER_EVAPORATES).argument();
                        if(ultraWarm && fluidStack.getFluid().is(FluidTags.WATER)) {
                            StacksHandlerUtils.drain(tank, Reference.BUCKET, false);
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
                            if(!level.isClientSide() && flag && !level.getBlockState(newPos).liquid()) {
                                level.destroyBlock(newPos, false);
                            }

                            if(level.setBlock(newPos, fluidStack.getFluid().defaultFluidState().createLegacyBlock(), 3)) {
                                SoundEvent bucketEmpty = Optional.ofNullable(fluidStack.getFluid().getFluidType().getSound(SoundActions.BUCKET_EMPTY)).orElse(SoundEvents.BUCKET_EMPTY);
                                level.playSound(player, newPos, bucketEmpty, SoundSource.BLOCKS, 1.0F, 1.0F);
                                StacksHandlerUtils.drain(tank, Reference.BUCKET, false);
                                level.updateNeighborsAt(newPos, fluidStack.getFluid().defaultFluidState().createLegacyBlock().getBlock());
                            }
                            triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
            if(getHoseMode(stack) == DRINK_MODE) {
                if(!StacksHandlerUtils.isEmpty(tank)) {
                    if(EffectFluidRegistry.hasExecutableEffects(StacksHandlerUtils.getFluid(tank), level, player)) {
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
                BackpackWrapper wrapper = AttachmentUtils.getBackpackWrapper(player, AttachmentUtils.UPGRADES_ONLY.get());
                if(!wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                    return stack;
                }
                FluidStacksResourceHandler tank = this.getSelectedFluidTank(stack, wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get());
                if(getHoseMode(stack) == DRINK_MODE) {
                    if(tank != null) {
                        if(ServerActions.setFluidEffect(level, player, tank)) {
                            int drainAmount = EffectFluidRegistry.getHighestFluidEffectAmount(StacksHandlerUtils.getFluid(tank).getFluid());
                            if(StacksHandlerUtils.getFluid(tank).getFluid() == ModFluids.POTION_FLUID.get()) {
                                triggerAdvancement(player, ActionTypeTrigger.HOSE_DRINK_POTION);
                            }
                            triggerAdvancement(player, ActionTypeTrigger.HOSE_DRINK);
                            StacksHandlerUtils.drain(tank, drainAmount, false);
                        }
                    }
                }
            }
        }
        return stack;
    }

    public void triggerAdvancement(Player player, String type) {
        if(player instanceof ServerPlayer serverPlayer) {
            ModAdvancements.ACTION_TRIGGER.get().trigger(serverPlayer, type);
        }
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if(AttachmentUtils.isWearingBackpack(player) && hand == InteractionHand.MAIN_HAND && getHoseMode(stack) == SUCK_MODE) {
            BackpackWrapper wrapper = AttachmentUtils.getBackpackWrapper(player, AttachmentUtils.UPGRADES_ONLY.get());
            if(!wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                return InteractionResult.PASS;
            }
            FluidStacksResourceHandler tank = this.getSelectedFluidTank(stack, wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get());
            Optional<Fluid> milk = BuiltInRegistries.FLUID.getOptional(Identifier.fromNamespaceAndPath("minecraft", "milk"));
            if(milk.isPresent()) {
                if(entity instanceof Cow) {
                    int tankAmount = StacksHandlerUtils.isEmpty(tank) ? 0 : StacksHandlerUtils.getFluidAmount(tank);
                    FluidStack milkStack = new FluidStack(milk.get(), Reference.BUCKET);
                    if(milkStack.getFluid() != Fluids.EMPTY) {
                        if((StacksHandlerUtils.isEmpty(tank) || FluidStack.isSameFluidSameComponents(StacksHandlerUtils.getFluid(tank), milkStack)) && milkStack.getAmount() + tankAmount <= StacksHandlerUtils.getCapacity(tank)) {
                            StacksHandlerUtils.fill(tank, milkStack, false);
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

    public FluidStacksResourceHandler getSelectedFluidTank(ItemStack stack, TanksUpgrade upgrade) {
        return getHoseTank(stack) == 1 ? upgrade.getLeftTank() : upgrade.getRightTank();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag) {
        if(stack.has(ModDataComponents.HOSE_MODES)) {
            int mode = stack.get(ModDataComponents.HOSE_MODES).get(0);
            if(mode == SUCK_MODE) {
                componentConsumer.accept(Component.translatable("item.travelersbackpack.hose.suck").withStyle(ChatFormatting.BLUE));
            }
            if(mode == SPILL_MODE) {
                componentConsumer.accept(Component.translatable("item.travelersbackpack.hose.spill").withStyle(ChatFormatting.BLUE));
            }
            if(mode == DRINK_MODE) {
                componentConsumer.accept(Component.translatable("item.travelersbackpack.hose.drink").withStyle(ChatFormatting.BLUE));
            }
            int tank = stack.get(ModDataComponents.HOSE_MODES).get(1);
            if(tank == 1) {
                componentConsumer.accept(Component.translatable("item.travelersbackpack.hose.tank_left").withStyle(ChatFormatting.BLUE));
            }
            if(tank == 2) {
                componentConsumer.accept(Component.translatable("item.travelersbackpack.hose.tank_right").withStyle(ChatFormatting.BLUE));
            }
        }
    }
}