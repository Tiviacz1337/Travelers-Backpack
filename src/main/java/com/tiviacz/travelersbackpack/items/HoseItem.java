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
import net.minecraft.advancements.CriteriaTriggers;
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
import net.minecraft.stats.Stats;
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
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
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
            ResourceHandler<FluidResource> fluidHandler = null;

            int hoseMode = getHoseMode(stack);
            BlockHitResult hitResult = getPlayerPOVHitResult(level, player, hoseMode == SUCK_MODE ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
            var hitType = hitResult.getType();
            if(hitType == HitResult.Type.BLOCK) {
                BlockPos pos = hitResult.getBlockPos();
                Direction direction = hitResult.getDirection();
                BlockPos directionOffsetPos = pos.relative(direction);

                //Check for fluid storage like in-world tanks
                fluidHandler = level.getCapability(Capabilities.Fluid.BLOCK, pos, direction);

                if(hoseMode == SUCK_MODE) {
                    //Transfer fluid from fluid handler
                    if(fluidHandler != null) {
                        if(!fluidHandler.getResource(0).isEmpty()) {
                            try(var tx = Transaction.openRoot()) {
                                FluidStack fluidStack = fluidHandler.getResource(0).toStack(Reference.BUCKET);
                                int moved = ResourceHandlerUtil.move(fluidHandler, tank, p -> true, Reference.BUCKET, tx);
                                if(moved > 0) {
                                    SoundEvent bucketFill = Optional.ofNullable(fluidStack.getFluidType().getSound(SoundActions.BUCKET_FILL)).orElse(SoundEvents.BUCKET_FILL);
                                    level.playSound(player, pos, bucketFill, SoundSource.BLOCKS, 1.0F, 1.0F);
                                    triggerAdvancement(player, ActionTypeTrigger.HOSE_SUCK);
                                    tx.commit();
                                    return InteractionResult.SUCCESS;
                                } else {
                                    tx.close();
                                }
                            }
                        }
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
                                int tankAmount = StacksHandlerUtils.isEmpty(tank) ? 0 : StacksHandlerUtils.getFluidAmount(tank);
                                boolean canFill = StacksHandlerUtils.isEmpty(tank) || FluidStack.isSameFluidSameComponents(StacksHandlerUtils.getFluid(tank), fluidStack);
                                if(canFill && (fluidStack.getAmount() + tankAmount <= StacksHandlerUtils.getCapacity(tank))) {
                                    ItemStack taken = bucketPickupBlock.pickupBlock(player, level, pos, blockState);
                                    if(!taken.isEmpty()) {
                                        player.awardStat(Stats.ITEM_USED.get(this));
                                        bucketPickupBlock.getPickupSound().ifPresent(soundEvent -> player.playSound(soundEvent, 1.0F, 1.0F));
                                        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
                                        StacksHandlerUtils.fill(tank, new FluidStack(fluid, Reference.BUCKET), false);
                                        triggerAdvancement(player, ActionTypeTrigger.HOSE_SUCK);
                                        //ItemStack result = ItemUtils.createFilledResult(itemStack, player, taken);
                                        if(!level.isClientSide()) {
                                            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, taken);
                                        }

                                        return InteractionResult.SUCCESS;
                                    }
                                }
                            }
                        }
                    }
                }

                if(hoseMode == SPILL_MODE) {
                    //Transfer fluid to fluid handler
                    if(fluidHandler != null) {
                        if(!StacksHandlerUtils.isEmpty(tank)) {
                            FluidStack fluidStack = tank.getResource(0).toStack(Reference.BUCKET);
                            try(var tx = Transaction.openRoot()) {
                                int moved = ResourceHandlerUtil.move(tank, fluidHandler, p -> true, Reference.BUCKET, tx);
                                if(moved > 0) {
                                    SoundEvent bucketFill = Optional.ofNullable(fluidStack.getFluidType().getSound(SoundActions.BUCKET_FILL)).orElse(SoundEvents.BUCKET_FILL);
                                    level.playSound(player, pos, bucketFill, SoundSource.BLOCKS, 1.0F, 1.0F);
                                    triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL);
                                    tx.commit();
                                    return InteractionResult.SUCCESS;
                                } else {
                                    tx.close();
                                }
                            }
                        }
                    }

                    //Try to splash potion in the world
                    if(spillPotion(tank, level, player) == InteractionResult.SUCCESS) {
                        return InteractionResult.SUCCESS;
                    }

                    FluidStack fluidStack = StacksHandlerUtils.getFluid(tank);
                    Fluid fluid = fluidStack.getFluid();
                    BlockState clicked = level.getBlockState(pos);
                    BlockPos placePos = clicked.getBlock() instanceof LiquidBlockContainer && fluid == Fluids.WATER ? pos : directionOffsetPos;
                    if(StacksHandlerUtils.getFluidAmount(tank) >= Reference.BUCKET && this.emptyContents(fluidStack, player, level, placePos, hitResult)) {
                        //this.checkExtraContent(player, level, itemStack, placePos);
                        if(player instanceof ServerPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, placePos, stack);
                        }

                        player.awardStat(Stats.ITEM_USED.get(this));
                        StacksHandlerUtils.drain(tank, Reference.BUCKET, false);
                        triggerAdvancement(player, ActionTypeTrigger.HOSE_SPILL);
                        //ItemStack emptyResult = ItemUtils.createFilledResult(itemStack, player, getEmptySuccessItem(itemStack, player));
                        return InteractionResult.SUCCESS;
                    }
                }

                if(hoseMode == DRINK_MODE) {
                    if(drink(tank, level, player, hand) == InteractionResult.SUCCESS) {
                        return InteractionResult.SUCCESS;
                    }
                }
            } else {
                if(hoseMode == SPILL_MODE) {
                    //Try to splash potion in the world
                    if(spillPotion(tank, level, player) == InteractionResult.SUCCESS) {
                        return InteractionResult.SUCCESS;
                    }
                }
                if(hoseMode == DRINK_MODE) {
                    if(drink(tank, level, player, hand) == InteractionResult.SUCCESS) {
                        return InteractionResult.SUCCESS;
                    }
                }
                return InteractionResult.PASS;
            }
        }
        return InteractionResult.FAIL;
    }

    public InteractionResult drink(FluidStacksResourceHandler tank, Level level, Player player, InteractionHand hand) {
        if(!StacksHandlerUtils.isEmpty(tank)) {
            if(EffectFluidRegistry.hasExecutableEffects(StacksHandlerUtils.getFluid(tank), level, player)) {
                player.startUsingItem(hand);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public InteractionResult spillPotion(FluidStacksResourceHandler tank, Level level, Player player) {
        //Try to splash potion in the world
        if(StacksHandlerUtils.getFluid(tank).getFluid() == ModFluids.POTION_FLUID.get()) {
            if(StacksHandlerUtils.getFluid(tank).getComponents().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().contains("PotionType")) {
                int potionType = StacksHandlerUtils.getFluid(tank).getComponents().get(DataComponents.CUSTOM_DATA).copyTag().getIntOr("PotionType", 0);
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
        return InteractionResult.PASS;
    }

    public boolean emptyContents(FluidStack fluidStack, @Nullable LivingEntity user, Level level, BlockPos pos, @Nullable BlockHitResult hitResult) {
        Fluid fluid = fluidStack.getFluid();
        if(!(fluid instanceof FlowingFluid flowingFluid)) {
            return false;
        } else {
            BlockState blockState = level.getBlockState(pos);
            Block block = blockState.getBlock();
            boolean mayReplace = blockState.canBeReplaced(fluid);
            boolean shiftKeyDown = user != null && user.isShiftKeyDown();
            boolean placeLiquid = mayReplace || block instanceof LiquidBlockContainer container && container.canPlaceLiquid(user, level, pos, blockState, fluid);
            boolean canPlaceFluidInsideBlock = blockState.isAir() || placeLiquid && (!shiftKeyDown || hitResult == null);
            if(!canPlaceFluidInsideBlock) {
                return hitResult != null && this.emptyContents(fluidStack, user, level, hitResult.getBlockPos().relative(hitResult.getDirection()), null);
            } else if(level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos) && fluid.is(FluidTags.WATER)) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                RandomSource random = level.getRandom();
                level.playSound(user, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);

                for(int i = 0; i < 8; i++) {
                    level.addParticle(ParticleTypes.LARGE_SMOKE, x + random.nextFloat(), y + random.nextFloat(), z + random.nextFloat(), 0.0, 0.0, 0.0);
                }

                return true;
            } else if(block instanceof LiquidBlockContainer containerx && fluid == Fluids.WATER) {
                containerx.placeLiquid(level, pos, blockState, flowingFluid.getSource(false));
                this.playEmptySound(fluidStack, user, level, pos);
                return true;
            } else {
                if(!level.isClientSide() && mayReplace && !blockState.liquid()) {
                    level.destroyBlock(pos, true);
                }

                if(!level.setBlock(pos, fluid.defaultFluidState().createLegacyBlock(), 11) && !blockState.getFluidState().isSource()) {
                    return false;
                } else {
                    this.playEmptySound(fluidStack, user, level, pos);
                    return true;
                }
            }
        }
    }

    protected void playEmptySound(FluidStack fluidStack, @Nullable LivingEntity user, LevelAccessor level, BlockPos pos) {
        SoundEvent soundEvent = fluidStack.getFluidType().getSound(user, level, pos, SoundActions.BUCKET_EMPTY);
        if(soundEvent == null) soundEvent = fluidStack.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        level.playSound(user, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(user, GameEvent.FLUID_PLACE, pos);
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