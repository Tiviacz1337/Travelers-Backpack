package com.tiviacz.travelersbackpack.item;

import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.FluidTank;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.util.FluidStackHelper;
import com.tiviacz.travelersbackpack.util.FluidTypeHelper;
import com.tiviacz.travelersbackpack.util.FluidUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
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

import java.util.List;

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
        return 24;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(ComponentUtils.isWearingBackpack(player) && hand == InteractionHand.MAIN_HAND) {
            BackpackWrapper wrapper = ComponentUtils.getBackpackWrapper(player, ComponentUtils.UPGRADES_ONLY);
            if(!wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                return InteractionResult.PASS;
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
                            FluidVariantWrapper fluidStack = new FluidVariantWrapper(FluidVariant.of(fluid), FluidConstants.BUCKET);
                            long tankAmount = tank.isEmpty() ? 0 : tank.getFluidAmount();
                            boolean canFill = tank.isEmpty() || FluidUtil.isSameVariant(tank.getFluid().fluidVariant(), fluidStack.fluidVariant());
                            if(canFill && (fluidStack.getAmount() + tankAmount <= tank.getCapacity())) {
                                ItemStack actualFluid = pickup.pickupBlock(player, level, blockpos, blockstate1);
                                if(!actualFluid.isEmpty()) {
                                    level.playSound(player, result.getBlockPos(), FluidTypeHelper.getSound(fluidStack.fluidVariant(), FluidTypeHelper.BUCKET_FILL) == null ? (fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL) : FluidTypeHelper.getSound(fluidStack.fluidVariant(), FluidTypeHelper.BUCKET_FILL), SoundSource.BLOCKS, 1.0F, 1.0F);
                                    tank.fill(new FluidVariantWrapper(FluidVariant.of(fluid), FluidConstants.BUCKET), false);
                                    return InteractionResult.SUCCESS;
                                }
                            }
                        }
                    }
                }
            }

            if(getHoseMode(stack) == SPILL_MODE) {
                //Try to splash potion in the world
                if(tank.getFluid().fluidVariant().getFluid() == ModFluids.POTION_STILL) {
                    if(tank.getFluid().fluidVariant().getComponentMap().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains("PotionType")) {
                        int potionType = tank.getFluid().fluidVariant().getComponentMap().get(DataComponents.CUSTOM_DATA).copyTag().getInt("PotionType");
                        if(potionType == 1) {
                            if(tank.getFluidAmount() >= FluidConstants.BOTTLE) {
                                ItemStack potionStack = FluidStackHelper.getSplashItemStackFromFluidStack(tank.getFluid().fluidVariant());
                                long drainAmount = ServerActions.throwPotion(level, player, potionStack, true);
                                tank.drain(drainAmount, false);
                                return InteractionResult.SUCCESS;
                            }
                        } else if(potionType == 2) {
                            if(tank.getFluidAmount() >= FluidConstants.BOTTLE) {
                                ItemStack potionStack = FluidStackHelper.getLingeringItemStackFromFluidStack(tank.getFluid().fluidVariant());
                                long drainAmount = ServerActions.throwPotion(level, player, potionStack, false);
                                tank.drain(drainAmount, false);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
            }

            if(getHoseMode(stack) == DRINK_MODE) {
                if(!tank.isEmpty()) {
                    if(EffectFluidRegistry.hasExecutableEffects(tank.getFluid(), level, player)) {
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
        if(ComponentUtils.isWearingBackpack(player) && context.getHand() == InteractionHand.MAIN_HAND) {
            Storage<FluidVariant> fluidVariantStorage = null;
            if(!level.isClientSide) {
                fluidVariantStorage = FluidStorage.SIDED.find(level, pos, direction);
            }
            BackpackWrapper wrapper = ComponentUtils.getBackpackWrapper(player, ComponentUtils.UPGRADES_ONLY);
            if(!wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                return InteractionResult.PASS;
            }
            FluidTank tank = this.getSelectedFluidTank(stack, wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get());

            if(getHoseMode(stack) == SUCK_MODE) {
                //Transfer fluid from fluid handler
                if(fluidVariantStorage != null) {
                    try(Transaction transaction = Transaction.openOuter()) {
                        if(fluidVariantStorage.supportsExtraction()) {
                            ResourceAmount<FluidVariant> fluidVariantResource = StorageUtil.findExtractableContent(fluidVariantStorage, transaction);
                            if(fluidVariantResource != null && fluidVariantResource.amount() > 0 && !fluidVariantResource.resource().isBlank()) {
                                long amountInserted = tank.insert(fluidVariantResource.resource(), Math.min(fluidVariantResource.amount(), FluidConstants.BUCKET), transaction);
                                long amountExtracted = fluidVariantStorage.extract(fluidVariantResource.resource(), Math.min(fluidVariantResource.amount(), FluidConstants.BUCKET), transaction);
                                if(amountExtracted == amountInserted) {
                                    level.playSound(player, pos, FluidTypeHelper.getSound(fluidVariantResource.resource(), FluidTypeHelper.BUCKET_FILL), SoundSource.BLOCKS, 1.0F, 1.0F);
                                    transaction.commit();
                                    return InteractionResult.SUCCESS;
                                }
                            }
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
                            FluidVariantWrapper fluidStack = new FluidVariantWrapper(FluidVariant.of(fluid), FluidConstants.BUCKET);
                            long tankAmount = tank.isEmpty() ? 0 : tank.getFluidAmount();
                            boolean canFill = tank.isEmpty() || FluidUtil.isSameVariant(tank.getFluid().fluidVariant(), fluidStack.fluidVariant());
                            if(canFill && (fluidStack.getAmount() + tankAmount <= tank.getCapacity())) {
                                ItemStack actualFluid = pickup.pickupBlock(player, level, blockpos, blockstate1);
                                if(!actualFluid.isEmpty()) {
                                    level.playSound(player, result.getBlockPos(), FluidTypeHelper.getSound(fluidStack.fluidVariant(), FluidTypeHelper.BUCKET_FILL) == null ? (fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL) : FluidTypeHelper.getSound(fluidStack.fluidVariant(), FluidTypeHelper.BUCKET_FILL), SoundSource.BLOCKS, 1.0F, 1.0F);
                                    tank.fill(new FluidVariantWrapper(FluidVariant.of(fluid), FluidConstants.BUCKET), false);
                                    return InteractionResult.SUCCESS;
                                }
                            }
                        }
                    }
                }
            }
            if(getHoseMode(stack) == SPILL_MODE) {
                //Transfer fluid to fluid handler
                if(fluidVariantStorage != null && !tank.isEmpty()) {
                    FluidVariantWrapper fluidStack = tank.getFluid();
                    try(Transaction transaction = Transaction.openOuter()) {
                        long amountExtracted = tank.extract(fluidStack.fluidVariant(), Math.min(fluidStack.amount(), FluidConstants.BUCKET), transaction);
                        long amountInserted = fluidVariantStorage.insert(fluidStack.fluidVariant(), Math.min(fluidStack.amount(), FluidConstants.BUCKET), transaction);
                        if(amountExtracted > 0 && amountExtracted == amountInserted) {
                            level.playSound(player, pos, FluidTypeHelper.getSound(fluidStack.fluidVariant(), FluidTypeHelper.BUCKET_FILL), SoundSource.BLOCKS, 1.0F, 1.0F);
                            transaction.commit();
                            return InteractionResult.SUCCESS;
                        }
                    }
                }

                //Try to splash potion in the world
                if(tank.getFluid().fluidVariant().getFluid() == ModFluids.POTION_STILL) {
                    if(tank.getFluid().fluidVariant().getComponentMap().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains("PotionType")) {
                        int potionType = tank.getFluid().fluidVariant().getComponentMap().get(DataComponents.CUSTOM_DATA).copyTag().getInt("PotionType");
                        if(potionType == 1) {
                            if(tank.getFluidAmount() >= FluidConstants.BOTTLE) {
                                ItemStack potionStack = FluidStackHelper.getSplashItemStackFromFluidStack(tank.getFluid().fluidVariant());
                                long drainAmount = ServerActions.throwPotion(level, player, potionStack, true);
                                tank.drain(drainAmount, false);
                                return InteractionResult.SUCCESS;
                            }
                        } else if(potionType == 2) {
                            if(tank.getFluidAmount() >= FluidConstants.BOTTLE) {
                                ItemStack potionStack = FluidStackHelper.getLingeringItemStackFromFluidStack(tank.getFluid().fluidVariant());
                                long drainAmount = ServerActions.throwPotion(level, player, potionStack, false);
                                tank.drain(drainAmount, false);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }

                //Try to put fluid in the world
                if(!tank.isEmpty()) {
                    BlockState blockState = level.getBlockState(pos);
                    Block block = blockState.getBlock();
                    Fluid fluid = tank.getFluid().fluidVariant().getFluid();
                    if(tank.getFluidAmount() >= FluidConstants.BUCKET && fluid instanceof FlowingFluid flowingFluid) {
                        if(block instanceof LiquidBlockContainer container && container.canPlaceLiquid(player, level, pos, blockState, fluid)) {
                            container.placeLiquid(level, pos, blockState, flowingFluid.getSource(false));
                            level.playSound(player, pos, FluidTypeHelper.getSound(tank.getFluid().fluidVariant(), FluidTypeHelper.BUCKET_EMPTY), SoundSource.BLOCKS, 1.0F, 1.0F);
                            tank.drain(FluidConstants.BUCKET, false);
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
                    FluidVariantWrapper fluidStack = tank.getFluid();
                    if(level.getBlockState(newPos).canBeReplaced(fluid)) {
                        boolean flag = !level.getBlockState(newPos).isSolid();
                        if(level.dimensionType().ultraWarm() && fluidStack.fluidVariant().getFluid().is(FluidTags.WATER)) {
                            tank.drain(FluidConstants.BUCKET, false);
                            level.playSound(null, newPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);
                            for(int i = 0; i < 3; ++i) {
                                double d0 = newPos.getX() + level.getRandom().nextDouble();
                                double d1 = newPos.getY() + level.getRandom().nextDouble() * 0.5D + 0.5D;
                                double d2 = newPos.getZ() + level.getRandom().nextDouble();
                                level.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                            }
                            return InteractionResult.SUCCESS;
                        }
                        if(fluidStack.getAmount() >= FluidConstants.BUCKET) {
                            if(!level.isClientSide && flag && !level.getBlockState(newPos).liquid()) {
                                level.destroyBlock(newPos, false);
                            }

                            if(level.setBlock(newPos, fluidStack.fluidVariant().getFluid().defaultFluidState().createLegacyBlock(), 3)) {
                                level.playSound(player, newPos, FluidTypeHelper.getSound(fluidStack.fluidVariant(), FluidTypeHelper.BUCKET_EMPTY), SoundSource.BLOCKS, 1.0F, 1.0F);
                                tank.drain(FluidConstants.BUCKET, false);
                                level.updateNeighborsAt(newPos, fluidStack.fluidVariant().getFluid().defaultFluidState().createLegacyBlock().getBlock());
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
            if(ComponentUtils.isWearingBackpack(player)) {
                BackpackWrapper wrapper = ComponentUtils.getBackpackWrapper(player, ComponentUtils.UPGRADES_ONLY);
                if(!wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                    return stack;
                }
                FluidTank tank = this.getSelectedFluidTank(stack, wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get());
                if(getHoseMode(stack) == DRINK_MODE) {
                    if(tank != null) {
                        if(ServerActions.setFluidEffect(level, player, tank)) {
                            long drainAmount = EffectFluidRegistry.getHighestFluidEffectAmount(tank.getFluid().fluidVariant().getFluid());
                            tank.drain(drainAmount, false);
                        }
                    }
                }
            }
        }
        return stack;
    }

/*    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if(ComponentUtils.isWearingBackpack(player) && hand == InteractionHand.MAIN_HAND && getHoseMode(stack) == SUCK_MODE) {
            BackpackWrapper wrapper = ComponentUtils.getBackpackWrapper(player);
            if(!wrapper.getUpgradeManager().tanksUpgrade.isPresent()) {
                return InteractionResult.PASS;
            }
            FluidTank tank = this.getSelectedFluidTank(stack, wrapper.getUpgradeManager().tanksUpgrade.get());
            Fluid milk = ModFluids.MILK_STILL;
            if(milk != null) {
                if(entity instanceof Cow) {
                    long tankAmount = tank.isEmpty() ? 0 : tank.getFluidAmount();
                    FluidVariantWrapper milkStack = new FluidVariantWrapper(FluidVariant.of(milk), FluidConstants.BUCKET);
                    if(milkStack.fluidVariant().getFluid() != Fluids.EMPTY) {
                        if((tank.isEmpty() || FluidUtil.isSameVariant(tank.getFluid().fluidVariant(), milkStack.fluidVariant())) && milkStack.getAmount() + tankAmount <= tank.getCapacity()) {
                            tank.fill(milkStack, false);
                            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    } */

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

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(stack.has(ModDataComponents.HOSE_MODES)) {
            int mode = stack.get(ModDataComponents.HOSE_MODES).get(0);
            if(mode == SUCK_MODE) {
                tooltipComponents.add(Component.translatable("hose.travelersbackpack.mode_suck").withStyle(ChatFormatting.BLUE));
            }
            if(mode == SPILL_MODE) {
                tooltipComponents.add(Component.translatable("hose.travelersbackpack.mode_spill").withStyle(ChatFormatting.BLUE));
            }
            if(mode == DRINK_MODE) {
                tooltipComponents.add(Component.translatable("hose.travelersbackpack.mode_drink").withStyle(ChatFormatting.BLUE));
            }
            int tank = stack.get(ModDataComponents.HOSE_MODES).get(1);
            if(tank == 1) {
                tooltipComponents.add(Component.translatable("hose.travelersbackpack.tank_left").withStyle(ChatFormatting.BLUE));
            }
            if(tank == 2) {
                tooltipComponents.add(Component.translatable("hose.travelersbackpack.tank_right").withStyle(ChatFormatting.BLUE));
            }
        }
    }
}