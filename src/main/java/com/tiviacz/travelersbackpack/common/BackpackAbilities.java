package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.FluidTank;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.util.BackpackDeathHelper;
import com.tiviacz.travelersbackpack.util.CooldownHelper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BackpackAbilities {
    /**
     * Main class for all available abilities
     * connects to few events and block methods to execute/remove proper abilities
     * It's such a mess right now, I might create better system for all of that in the future.
     * <p>
     * //Connecting abilities to player, abilities removals
     * {@link NeoForgeEventHandler#playerTick(PlayerTickEvent.Post)}
     * <p>
     * //Connecting abilities to block entity
     * {@link BackpackBlockEntity#tick(Level, BlockPos, BlockState, BackpackBlockEntity)}
     * <p>
     * //Ability removals
     * {@link ServerActions#switchAbilitySlider(BackpackWrapper, boolean)} (Player, boolean)}
     * <p>
     * //Cosmetic only
     * {@link TravelersBackpackBlock#animateTick(BlockState, Level, BlockPos, RandomSource)}
     * <p>
     * //Few uses of block abilities
     * {@link TravelersBackpackBlock}
     * <p>
     * //Creeper ability
     * {@link NeoForgeEventHandler#playerDeath(LivingDeathEvent)}
     */
    public static final BackpackAbilities ABILITIES = new BackpackAbilities();

    /**
     * Called in TravelersBackpackTileEntity#Tick and ForgeEventHandler#playerTick method to enable abilities
     */
    public boolean abilityTick(@Nullable ItemStack backpack, @Nullable Player player) {
        if(backpack != null) {
            Item backpackItem = backpack.getItem();

            if(backpackItem == ModItems.NETHERITE_TRAVELERS_BACKPACK) {
                attributeAbility(player, false, Attributes.ARMOR, NETHERITE_ARMOR_MODIFIER);
                return false;
            }

            if(backpackItem == ModItems.DIAMOND_TRAVELERS_BACKPACK) {
                attributeAbility(player, false, Attributes.ARMOR, DIAMOND_ARMOR_MODIFIER);
                return false;
            }

            if(backpackItem == ModItems.GOLD_TRAVELERS_BACKPACK) {
                attributeAbility(player, false, Attributes.ARMOR, GOLD_ARMOR_MODIFIER);
                return false;
            }

            if(backpackItem == ModItems.EMERALD_TRAVELERS_BACKPACK) {
                emeraldAbility(player, null);
                return false;
            }

            if(backpackItem == ModItems.IRON_TRAVELERS_BACKPACK) {
                attributeAbility(player, false, Attributes.ARMOR, IRON_ARMOR_MODIFIER);
                return false;
            }

            if(backpackItem == ModItems.ENDERMAN_TRAVELERS_BACKPACK) {
                attributeAbility(player, false, Attributes.BLOCK_INTERACTION_RANGE, ENDERMAN_REACH_DISTANCE_MODIFIER);
                return false;
            }

            if(backpackItem == ModItems.WARDEN_TRAVELERS_BACKPACK) {
                attributeAbility(player, false, Attributes.MAX_HEALTH, WARDEN_MAX_HEALTH_MODIFIER);
                return false;
            }

            if(backpackItem == ModItems.CAKE_TRAVELERS_BACKPACK) {
                cakeAbilityNew(backpack, player); //#TODO
                return true;
            }

            if(backpackItem == ModItems.CACTUS_TRAVELERS_BACKPACK) {
                cactusAbilityWearable(player, backpack);
                return false;
            }

            if(backpackItem == ModItems.CHICKEN_TRAVELERS_BACKPACK) {
                chickenAbilityNew(backpack, player, false);
                return true;
            }

            if(backpackItem == ModItems.DRAGON_TRAVELERS_BACKPACK) {
                dragonAbility(player);
                return false;
            }

            if(backpackItem == ModItems.BLAZE_TRAVELERS_BACKPACK) {
                blazeAbility(player);
                return false;
            }

            if(backpackItem == ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK) {
                magmaCubeAbility(player);
                return false;
            }

            if(backpackItem == ModItems.SPIDER_TRAVELERS_BACKPACK) {
                spiderAbility(player);
                return false;
            }

            if(backpackItem == ModItems.WITHER_TRAVELERS_BACKPACK) {
                witherAbility(player);
                return false;
            }

            if(backpackItem == ModItems.BAT_TRAVELERS_BACKPACK) {
                batAbility(player);
                return false;
            }

            if(backpackItem == ModItems.OCELOT_TRAVELERS_BACKPACK) {
                ocelotAbility(player);
                return false;
            }

            if(backpackItem == ModItems.COW_TRAVELERS_BACKPACK) {
                cowAbility(backpack, player);
                return true;
            }

            if(backpackItem == ModItems.SQUID_TRAVELERS_BACKPACK) {
                squidAbility(player);
                return false;
            }
        }
        return false;
    }

    /*
    public void abilityTick(@Nullable BackpackWrapper wrapper, @Nullable Player player) {
        if(wrapper != null) {
            Item backpackItem = wrapper.getBackpackStack().getItem();

            if(backpackItem == ModItems.NETHERITE_TRAVELERS_BACKPACK.get()) {
                attributeAbility(player, false, Attributes.ARMOR, NETHERITE_ARMOR_MODIFIER);
            }

            if(backpackItem == ModItems.DIAMOND_TRAVELERS_BACKPACK.get()) {
                attributeAbility(player, false, Attributes.ARMOR, DIAMOND_ARMOR_MODIFIER);
            }

            if(backpackItem == ModItems.GOLD_TRAVELERS_BACKPACK.get()) {
                attributeAbility(player, false, Attributes.ARMOR, GOLD_ARMOR_MODIFIER);
            }

            if(backpackItem == ModItems.EMERALD_TRAVELERS_BACKPACK.get()) {
                emeraldAbility(player, null);
            }

            if(backpackItem == ModItems.IRON_TRAVELERS_BACKPACK.get()) {
                attributeAbility(player, false, Attributes.ARMOR, IRON_ARMOR_MODIFIER);
            }

            if(backpackItem == ModItems.ENDERMAN_TRAVELERS_BACKPACK.get()) {
                attributeAbility(player, false, Attributes.BLOCK_INTERACTION_RANGE, ENDERMAN_REACH_DISTANCE_MODIFIER);
            }

            if(backpackItem == ModItems.CAKE_TRAVELERS_BACKPACK.get()) {
                //cakeAbility(wrapper, player); //#TODO
            }

            if(backpackItem == ModItems.CACTUS_TRAVELERS_BACKPACK.get()) {
                //cactusAbilityNew(player, wrapper, null);
            }

            if(backpackItem == ModItems.CHICKEN_TRAVELERS_BACKPACK.get()) {
                chickenAbilityNew(player, false);
            }

            if(backpackItem == ModItems.DRAGON_TRAVELERS_BACKPACK.get()) {
                dragonAbility(player);
            }

            if(backpackItem == ModItems.BLAZE_TRAVELERS_BACKPACK.get()) {
                blazeAbility(player);
            }

            if(backpackItem == ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK.get()) {
                magmaCubeAbility(player);
            }

            if(backpackItem == ModItems.SPIDER_TRAVELERS_BACKPACK.get()) {
                spiderAbility(player);
            }

            if(backpackItem == ModItems.WITHER_TRAVELERS_BACKPACK.get()) {
                witherAbility(player);
            }

            if(backpackItem == ModItems.BAT_TRAVELERS_BACKPACK.get()) {
                batAbility(player);
            }

            if(backpackItem == ModItems.OCELOT_TRAVELERS_BACKPACK.get()) {
                ocelotAbility(player);
            }

            if(backpackItem == ModItems.COW_TRAVELERS_BACKPACK.get()) {
                cowAbility(player);
            }

            if(backpackItem == ModItems.SQUID_TRAVELERS_BACKPACK.get()) {
                squidAbility(player);
            }
        }
    }
     */

    public boolean abilityTickBlock(@Nullable BackpackBlockEntity blockEntity) {
        if(blockEntity.getWrapper() != null) {
            Item backpackItem = blockEntity.getWrapper().getBackpackStack().getItem();
            if(backpackItem == ModItems.CACTUS_TRAVELERS_BACKPACK) {
                cactusAbilityBlockEntity(blockEntity.getWrapper(), blockEntity);
                return false;
            }
        }
        return false;
    }

    public void abilityRemoval(@Nullable ItemStack stack, @Nullable Player player) {
        if(stack.getItem() == ModItems.NETHERITE_TRAVELERS_BACKPACK) {
            attributeAbility(player, true, Attributes.ARMOR, NETHERITE_ARMOR_MODIFIER);
        }

        if(stack.getItem() == ModItems.DIAMOND_TRAVELERS_BACKPACK) {
            attributeAbility(player, true, Attributes.ARMOR, DIAMOND_ARMOR_MODIFIER);
        }

        if(stack.getItem() == ModItems.IRON_TRAVELERS_BACKPACK) {
            attributeAbility(player, true, Attributes.ARMOR, IRON_ARMOR_MODIFIER);
        }

        if(stack.getItem() == ModItems.GOLD_TRAVELERS_BACKPACK) {
            attributeAbility(player, true, Attributes.ARMOR, GOLD_ARMOR_MODIFIER);
        }

        if(stack.getItem() == ModItems.ENDERMAN_TRAVELERS_BACKPACK) {
            attributeAbility(player, true, Attributes.BLOCK_INTERACTION_RANGE, ENDERMAN_REACH_DISTANCE_MODIFIER);
        }

        if(stack.getItem() == ModItems.WARDEN_TRAVELERS_BACKPACK) {
            attributeAbility(player, true, Attributes.MAX_HEALTH, WARDEN_MAX_HEALTH_MODIFIER);
        }
    }

    /**
     * Called in TravelersBackpackBlock#animateTick method to enable visual only abilities for BackpackBlockEntity
     */

    public void animateTick(BackpackBlockEntity backpackBlockEntity, BlockState stateIn, Level level, BlockPos pos, RandomSource rand) {
        if(backpackBlockEntity.getWrapper() != null && backpackBlockEntity.getWrapper().isAbilityEnabled()) {
            Block block = stateIn.getBlock();
            if(block == ModBlocks.EMERALD_TRAVELERS_BACKPACK) {
                emeraldAbility(null, backpackBlockEntity);
            }

            if(block == ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK) {
                bookshelfAbility(null, backpackBlockEntity);
            }

            if(block == ModBlocks.SPONGE_TRAVELERS_BACKPACK) {
                spongeAbility(backpackBlockEntity);
            }
        }
    }

    public void emeraldAbility(@Nullable Player player, @Nullable BackpackBlockEntity backpackBlockEntity) {
        Level level = player == null ? backpackBlockEntity.getLevel() : player.level();
        if(player == null || level.random.nextInt(10) == 1) {
            float f = level.random.nextFloat() * (float)Math.PI * 2.0F;
            float f1 = level.random.nextFloat() * 0.5F + 0.5F;
            float f2 = Mth.sin(f) * 0.5F * f1;
            float f3 = Mth.cos(f) * 0.5F * f1;
            level.addParticle(ParticleTypes.HAPPY_VILLAGER,
                    player == null ? backpackBlockEntity.getBlockPos().getX() + f2 + 0.5F : player.position().x + f2,
                    player == null ? backpackBlockEntity.getBlockPos().getY() + level.random.nextFloat() : player.getBoundingBox().minY + level.random.nextFloat() + 0.5F,
                    player == null ? backpackBlockEntity.getBlockPos().getZ() + f3 + 0.5F : player.position().z + f3, (double)(float)Math.pow(2.0D, (level.random.nextInt(169) - 12) / 12.0D) / 24.0D, -1.0D, 0.0D);
        }
    }

    public final AttributeModifier NETHERITE_ARMOR_MODIFIER = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "netherite_backpack_armor"), 4.0D, AttributeModifier.Operation.ADD_VALUE);
    public final AttributeModifier DIAMOND_ARMOR_MODIFIER = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "diamond_backpack_armor"), 3.0D, AttributeModifier.Operation.ADD_VALUE);
    public final AttributeModifier IRON_ARMOR_MODIFIER = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "iron_backpack_armor"), 2.0D, AttributeModifier.Operation.ADD_VALUE);
    public final AttributeModifier GOLD_ARMOR_MODIFIER = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "gold_backpack_armor"), 2.0D, AttributeModifier.Operation.ADD_VALUE);
    public final AttributeModifier ENDERMAN_REACH_DISTANCE_MODIFIER = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "enderman_backpack_reach"), 1.0D, AttributeModifier.Operation.ADD_VALUE);
    public final AttributeModifier WARDEN_MAX_HEALTH_MODIFIER = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "warden_backpack_max_health"), 4.0D, AttributeModifier.Operation.ADD_VALUE);

    public void attributeAbility(Player player, boolean isRemoval, Holder<Attribute> attribute, AttributeModifier modifier) {
        AttributeInstance armor = player.getAttribute(attribute);
        if(isRemoval && armor != null && armor.hasModifier(modifier.id())) {
            armor.removeModifier(modifier.id());
        }
        if(!isRemoval && armor != null && !armor.hasModifier(modifier.id())) {
            armor.addPermanentModifier(modifier);
        }
    }

    public void armorAbilityRemovals(Player player) {
        attributeAbility(player, true, Attributes.ARMOR, NETHERITE_ARMOR_MODIFIER);
        attributeAbility(player, true, Attributes.ARMOR, DIAMOND_ARMOR_MODIFIER);
        attributeAbility(player, true, Attributes.ARMOR, IRON_ARMOR_MODIFIER);
        attributeAbility(player, true, Attributes.ARMOR, GOLD_ARMOR_MODIFIER);

        attributeAbility(player, true, Attributes.BLOCK_INTERACTION_RANGE, ENDERMAN_REACH_DISTANCE_MODIFIER);
        attributeAbility(player, true, Attributes.MAX_HEALTH, WARDEN_MAX_HEALTH_MODIFIER);
    }

    public void lapisAbility(Player player) {
        if(ABILITIES.checkBackpack(player, ModItems.LAPIS_TRAVELERS_BACKPACK)) {
            int number = player.getRandom().nextIntBetweenInclusive(0, 1);
            player.giveExperiencePoints(number);
            sendParticlesPacket(ParticleTypes.GLOW, player, number);
        }
    }

    public void bookshelfAbility(@Nullable Player player, @Nullable BackpackBlockEntity backpackBlockEntity) {
        BlockPos enchanting = BackpackDeathHelper.findBlock3D(backpackBlockEntity.getLevel(), backpackBlockEntity.getBlockPos().getX(), backpackBlockEntity.getBlockPos().getY(), backpackBlockEntity.getBlockPos().getZ(), Blocks.ENCHANTING_TABLE, 2, 2);
        if(enchanting != null) {
            if(!backpackBlockEntity.getLevel().isEmptyBlock(new BlockPos((enchanting.getX() - backpackBlockEntity.getBlockPos().getX()) / 2 + backpackBlockEntity.getBlockPos().getX(), enchanting.getY(), (enchanting.getZ() - backpackBlockEntity.getBlockPos().getZ()) / 2 + backpackBlockEntity.getBlockPos().getZ()))) {
                return;
            }
            for(int o = 0; o < 4; o++) {
                backpackBlockEntity.getLevel().addParticle(ParticleTypes.ENCHANT, enchanting.getX() + 0.5D, enchanting.getY() + 2.0D, enchanting.getZ() + 0.5D,
                        ((backpackBlockEntity.getBlockPos().getX() - enchanting.getX()) + backpackBlockEntity.getLevel().random.nextFloat()) - 0.5D,
                        ((backpackBlockEntity.getBlockPos().getY() - enchanting.getY()) - backpackBlockEntity.getLevel().random.nextFloat() - 1.0F),
                        ((backpackBlockEntity.getBlockPos().getZ() - enchanting.getZ()) + backpackBlockEntity.getLevel().random.nextFloat()) - 0.5D);
            }
        }
    }

    public void spongeAbility(BackpackBlockEntity backpackBlockEntity) {
        if(backpackBlockEntity.getWrapper().getUpgradeManager().tanksUpgrade.isPresent()) {
            TanksUpgrade tanksUpgrade = backpackBlockEntity.getWrapper().getUpgradeManager().tanksUpgrade.get();
            if(!tanksUpgrade.getLeftTank().isEmpty() && !tanksUpgrade.getRightTank().isEmpty()) {
                if(tanksUpgrade.getLeftTank().getFluid().fluidVariant().getFluid().isSame(Fluids.WATER) && tanksUpgrade.getRightTank().getFluid().fluidVariant().getFluid().isSame(Fluids.WATER)) {
                    if(tanksUpgrade.getLeftTank().getFluidAmount() == tanksUpgrade.getLeftTank().getCapacity() && tanksUpgrade.getRightTank().getFluidAmount() == tanksUpgrade.getRightTank().getCapacity()) {
                        float f = backpackBlockEntity.getLevel().random.nextFloat() * (float)Math.PI * 2.0F;
                        float f1 = backpackBlockEntity.getLevel().random.nextFloat() * 0.5F + 0.5F;
                        float f2 = Mth.sin(f) * 0.5F * f1;
                        float f3 = Mth.cos(f) * 0.5F * f1;
                        backpackBlockEntity.getLevel().addParticle(ParticleTypes.SPLASH,
                                backpackBlockEntity.getBlockPos().getX() + f2 + 0.5F,
                                backpackBlockEntity.getBlockPos().getY() + backpackBlockEntity.getLevel().random.nextFloat(),
                                backpackBlockEntity.getBlockPos().getZ() + f3 + 0.5F, (double)(float)Math.pow(2.0D, (backpackBlockEntity.getLevel().random.nextInt(169) - 12) / 12.0D) / 24.0D, -1.0D, 0.0D);
                    }
                }
            }
        }
    }

    //Restores 2 Shanks (4 hunger points) and grants Regeneration I for 5 seconds
    public void cakeAbilityNew(ItemStack backpack, Player player) {
        if(backpack.getOrDefault(ModDataComponents.COOLDOWN, 0) <= 0) {
            player.getFoodData().eat(4, 0.1F);
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 5 * 20));
            player.level().playSound(null, player.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.AMBIENT, 0.6F, (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.3F + 1.0F);

            if(player.level() instanceof ServerLevel server) {
                for(int i = 0; i < 3; i++) {
                    float f = server.random.nextFloat() * (float)Math.PI * 2.0F;
                    float f1 = server.random.nextFloat() * 0.5F + 0.5F;
                    float f2 = Mth.sin(f) * 0.5F * f1;
                    float f3 = Mth.cos(f) * 0.5F * f1;
                    server.sendParticles(ParticleTypes.HEART,
                            player.position().x + f2,
                            player.getBoundingBox().minY + player.level().random.nextFloat() + 0.5F,
                            player.position().z + f3, 3, (double)(float)Math.pow(2.0D, (player.level().random.nextInt(169) - 12) / 12.0D) / 24.0D, -1.0D, 0.0D, 0);
                }
            }
            backpack.set(ModDataComponents.COOLDOWN, CooldownHelper.createCooldown(360, 360 + player.getFoodData().getFoodLevel() * 12));
            //wrapper.setCooldown(CooldownHelper.createCooldown(360, 360 + player.getFoodData().getFoodLevel() * 12));
        }
    }

    //Restores 2 Shanks (4 hunger points) and grants Regeneration I for 5 seconds
  /*  public void cakeAbility(BackpackWrapper wrapper, Player player) {
        if(wrapper.getCooldown() <= 0) {
            player.getFoodData().eat(4, 0.1F);
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 5 * 20));
            player.level().playSound(null, player.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.AMBIENT, 0.6F, (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.3F + 1.0F);

            if(player.level() instanceof ServerLevel server) {
                for(int i = 0; i < 3; i++) {
                    float f = server.random.nextFloat() * (float) Math.PI * 2.0F;
                    float f1 = server.random.nextFloat() * 0.5F + 0.5F;
                    float f2 = Mth.sin(f) * 0.5F * f1;
                    float f3 = Mth.cos(f) * 0.5F * f1;
                    server.sendParticles(ParticleTypes.HEART,
                            player.position().x + f2,
                            player.getBoundingBox().minY + player.level().random.nextFloat() + 0.5F,
                            player.position().z + f3, 3, (double)(float)Math.pow(2.0D, (player.level().random.nextInt(169) - 12) / 12.0D) / 24.0D, -1.0D, 0.0D, 0);
                }
            }
            wrapper.setCooldown(CooldownHelper.createCooldown(360, 360 + player.getFoodData().getFoodLevel() * 12));
        }
    } */

    public void chickenAbilityNew(ItemStack backpack, Player player, boolean firstSwitch) {
        //BackpackWrapper wrapper = AttachmentUtils.getBackpackWrapper(player, stack);

        if(firstSwitch && !player.level().isClientSide) {
            if(backpack.getOrDefault(ModDataComponents.COOLDOWN, 0) <= 0) {
                BackpackWrapper wrapper = ComponentUtils.getBackpackWrapper(player);
                wrapper.setCooldown(CooldownHelper.createCooldown(360, 600));
                return;
            }
        }
        if(backpack.getOrDefault(ModDataComponents.COOLDOWN, 0) <= 0) {
            BackpackWrapper wrapper = ComponentUtils.getBackpackWrapper(player);
            player.level().playSound(null, player.blockPosition(), SoundEvents.CHICKEN_EGG, SoundSource.AMBIENT, 1.0F, (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.3F + 1.0F);
            player.spawnAtLocation(Items.EGG);
            if(player.level().isClientSide) return;
            wrapper.setCooldown(CooldownHelper.createCooldown(360, 600));
        }
    }

    public void cactusAbilityWearable(@Nullable Player player, @Nullable ItemStack backpack) {
        int gameTime = (int)player.level().getGameTime();
        BackpackWrapper wrapper;
        int cooldown = backpack.getOrDefault(ModDataComponents.COOLDOWN, 0);
        if(cooldown >= 1000) {
            wrapper = ComponentUtils.getBackpackWrapper(player);
            if(wrapper.getUpgradeManager().tanksUpgrade.isPresent()) {
                TanksUpgrade upgrade = wrapper.getUpgradeManager().tanksUpgrade.get();
                FluidTank leftTank = upgrade.getLeftTank();
                FluidTank rightTank = upgrade.getRightTank();
                FluidVariantWrapper water = new FluidVariantWrapper(FluidVariant.of(Fluids.WATER), FluidConstants.BUCKET);
                if(!player.level().isClientSide) {
                    leftTank.fill(water, true);
                    rightTank.fill(water, true);
                }

                if(player.level().isClientSide) return;

                wrapper.setCooldown(0);
            } else {
                return;
            }
        }

        int drops = 0;
        if(gameTime % 100 == 0) {
            wrapper = ComponentUtils.getBackpackWrapper(player);
            if(player.isInWater()) {
                drops += 5 * 10;
            }

            if(isUnderRain(player.blockPosition(), player.level())) {
                drops += 5 * 10;
            }

            int getCurrentDrops = wrapper.getCooldown();
            if(drops > 0) {
                if(player.level().isClientSide) return;

                wrapper.setCooldown(getCurrentDrops + drops);
            }
        }
    }

    public void cactusAbilityBlockEntity(@Nullable BackpackWrapper wrapper, @Nullable BackpackBlockEntity blockEntity) {
        int cooldown = wrapper.getCooldown();
        if(cooldown >= 1000) {
            if(wrapper.getUpgradeManager().tanksUpgrade.isPresent()) {
                TanksUpgrade upgrade = wrapper.getUpgradeManager().tanksUpgrade.get();
                FluidTank leftTank = upgrade.getLeftTank();
                FluidTank rightTank = upgrade.getRightTank();
                FluidVariantWrapper water = new FluidVariantWrapper(FluidVariant.of(Fluids.WATER), FluidConstants.BUCKET);
                leftTank.fill(water, true);
                rightTank.fill(water, true);
                wrapper.setCooldown(0);
            } else {
                return;
            }
        }

        int drops = 0;
        int gameTime = (int)blockEntity.getLevel().getGameTime();

        if(gameTime % 100 == 0) {
            if(isUnderRain(blockEntity.getBlockPos(), blockEntity.getLevel())) {
                drops += 5 * 10;
            }
            int getCurrentDrops = wrapper.getCooldown();
            if(drops > 0) {
                wrapper.setCooldown(getCurrentDrops + drops);
            }
        }
    }


     /*   if(player != null && wrapper != null) {
            if(wrapper.getUpgradeManager().tanksUpgrade.isPresent()) {
                TanksUpgrade upgrade = wrapper.getUpgradeManager().tanksUpgrade.get();
                FluidTank leftTank = upgrade.getLeftTank();
                FluidTank rightTank = upgrade.getRightTank();

                int drops = 0;

                if(player.isInWater()) {
                    drops += 2;
                }

                if(isUnderRain(player.blockPosition(), player.level())) {
                    drops += 1;
                }

                FluidStack water = new FluidStack(Fluids.WATER, drops);

                if(wrapper.getCooldown() == 0 && drops > 0) {
                    if(leftTank.isEmpty() || FluidStack.isSameFluidSameComponents(leftTank.getFluid(), water)) {
                        leftTank.fill(water, IFluidHandler.FluidAction.EXECUTE);
                    }
                    if(rightTank.isEmpty() || FluidStack.isSameFluidSameComponents(rightTank.getFluid(), water)) {
                        rightTank.fill(water, IFluidHandler.FluidAction.EXECUTE);
                    }
                    wrapper.setCooldown(20);
                }
            }
        } */

   /* public void cactusAbilityNew(@Nullable Player player, @Nullable BackpackWrapper wrapper, @Nullable BackpackBlockEntity blockEntity) {
        if(player != null && wrapper != null && blockEntity == null) {
            if(wrapper.getUpgradeManager().tanksUpgrade.isPresent()) {
                TanksUpgrade upgrade = wrapper.getUpgradeManager().tanksUpgrade.get();
                FluidTank leftTank = upgrade.getLeftTank();
                FluidTank rightTank = upgrade.getRightTank();

                int drops = 0;

                if(player.isInWater()) {
                    drops += 2;
                }

                if(isUnderRain(player.blockPosition(), player.level())) {
                    drops += 1;
                }

                FluidStack water = new FluidStack(Fluids.WATER, drops);

                if(wrapper.getCooldown() == 0 && drops > 0) {
                    if(leftTank.isEmpty() || FluidStack.isSameFluidSameComponents(leftTank.getFluid(), water)) {
                        leftTank.fill(water, IFluidHandler.FluidAction.EXECUTE);
                    }
                    if(rightTank.isEmpty() || FluidStack.isSameFluidSameComponents(rightTank.getFluid(), water)) {
                        rightTank.fill(water, IFluidHandler.FluidAction.EXECUTE);
                    }
                    wrapper.setCooldown(20);
                }
            }
        } else if(blockEntity != null && wrapper != null) {
            if(wrapper.getUpgradeManager().tanksUpgrade.isPresent()) {
                TanksUpgrade upgrade = wrapper.getUpgradeManager().tanksUpgrade.get();
                FluidTank leftTank = upgrade.getLeftTank();
                FluidTank rightTank = upgrade.getRightTank();

                int drops = 0;

                if (isUnderRain(blockEntity.getBlockPos(), blockEntity.getLevel())) {
                    drops += 1;
                }

                FluidStack water = new FluidStack(Fluids.WATER, drops);

                if (!blockEntity.getLevel().isClientSide) {
                    if (wrapper.getCooldown() <= 0 && drops > 0) {
                        if (leftTank.isEmpty() || FluidStack.isSameFluidSameComponents(leftTank.getFluid(), water)) {
                            leftTank.fill(water, IFluidHandler.FluidAction.EXECUTE);
                        }
                        if (rightTank.isEmpty() || FluidStack.isSameFluidSameComponents(rightTank.getFluid(), water)) {
                            rightTank.fill(water, IFluidHandler.FluidAction.EXECUTE);
                        }
                        wrapper.setCooldown(20);
                    }
                }
            }
        }
    } */

    /*public void cactusAbility(@Nullable Player player, @Nullable TravelersBackpackBlockEntity blockEntity)
    {
        if(player == null && blockEntity != null)
        {
            FluidTank leftTank = blockEntity.getLeftTank();
            FluidTank rightTank = blockEntity.getRightTank();

            int drops = 0;

            if(isUnderRain(blockEntity.getBlockPos(), blockEntity.getLevel()))
            {
                drops += 1;
            }

            FluidStack water = new FluidStack(Fluids.WATER, drops);

            if(!blockEntity.getLevel().isClientSide)
            {
                if(blockEntity.getLastTime() <= 0 && drops > 0)
                {
                    blockEntity.setLastTime(5);

                    if(leftTank.isEmpty() || FluidStack.isSameFluidSameComponents(leftTank.getFluid(), water))
                    {
                        leftTank.fill(water, IFluidHandler.FluidAction.EXECUTE);
                    }

                    if(rightTank.isEmpty() || FluidStack.isSameFluidSameComponents(rightTank.getFluid(), water))
                    {
                        rightTank.fill(water, IFluidHandler.FluidAction.EXECUTE);
                    }

                    blockEntity.setDataChanged(ITravelersBackpackContainer.TANKS_DATA);
                }
            }
        }
        else if(player != null && blockEntity == null)
        {
            TravelersBackpackContainer container = AttachmentUtils.getBackpackInv(player);

            FluidTank leftTank = container.getLeftTank();
            FluidTank rightTank = container.getRightTank();

            int drops = 0;

            if(player.isInWater())
            {
                drops += 2;
            }

            if(isUnderRain(player.blockPosition(), player.level()))
            {
                drops += 1;
            }

            FluidStack water = new FluidStack(Fluids.WATER, drops);

            if(!container.getLevel().isClientSide)
            {
                if(container.getLastTime() <= 0 && drops > 0)
                {
                    container.setLastTime(5);

                    if(leftTank.isEmpty() || FluidStack.isSameFluidSameComponents(leftTank.getFluid(), water))
                    {
                        leftTank.fill(water, IFluidHandler.FluidAction.EXECUTE);
                    }

                    if(rightTank.isEmpty() || FluidStack.isSameFluidSameComponents(rightTank.getFluid(), water))
                    {
                        rightTank.fill(water, IFluidHandler.FluidAction.EXECUTE);
                    }

                    container.setDataChanged(ITravelersBackpackContainer.TANKS_DATA);
                }
            }
        }
    }
 */
    public static void melonAbility(BackpackBlockEntity backpackBlockEntity) {
        if(backpackBlockEntity.getWrapper().isAbilityEnabled() && backpackBlockEntity.getWrapper().getCooldown() <= 0) {
            Block.popResource(backpackBlockEntity.getLevel(), backpackBlockEntity.getBlockPos(), new ItemStack(Items.MELON_SLICE, backpackBlockEntity.getLevel().random.nextInt(0, 3)));
            backpackBlockEntity.getWrapper().setCooldown(CooldownHelper.createCooldown(120, 480));
        }
    }

   /* public static void melonAbilityOld(TravelersBackpackBlockEntity blockEntity)
    {
        if(blockEntity.getAbilityValue() && blockEntity.getLastTime() <= 0)
        {
            Block.popResource(blockEntity.getLevel(), blockEntity.getBlockPos(), new ItemStack(Items.MELON_SLICE, blockEntity.getLevel().random.nextInt(0, 3)));
            //blockEntity.setLastTime(CooldownHelper.randomTime(blockEntity.getLevel().random, 120, 480));
            blockEntity.setDataChanged();
        }
    } */

    public static void pumpkinAbility(Player player, CallbackInfoReturnable<Boolean> cir) {
        if(ABILITIES.checkBackpack(player, ModItems.PUMPKIN_TRAVELERS_BACKPACK)) {
            cir.setReturnValue(true);
        }
    }

    public static boolean creeperAbility(Player player) {
        BackpackWrapper wrapper = ComponentUtils.getBackpackWrapper(player);
        if(player.isDeadOrDying() && wrapper != null && wrapper.getBackpackStack().getItem() == ModItems.CREEPER_TRAVELERS_BACKPACK && wrapper.isAbilityEnabled() && wrapper.getCooldown() <= 0) {
            player.setHealth(1.0F);
            player.removeAllEffects();
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 450, 1));
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 400, 0));
            player.level().explode(player, player.damageSources().playerAttack(player), null, player.getRandomX(0.5F), player.getY(), player.getRandomZ(0.5F), 3.0F, false, Level.ExplosionInteraction.NONE);
            player.level().playSound(null, player.blockPosition(), SoundEvents.CREEPER_PRIMED, SoundSource.AMBIENT, 1.2F, 0.5F);

            if(!player.level().isClientSide) {
                wrapper.setCooldown(CooldownHelper.createCooldown(1200, 1800));
            }
            // event.setCanceled(true);
            return true;
        }
        return false;
    }

    /*public static boolean creeperAbility(LivingDeathEvent event)
    {
        if(event.getEntity() instanceof Player player)
        {
            TravelersBackpackContainer container = AttachmentUtils.getBackpackInv(player);

            if(player.isDeadOrDying() && container != null && container.getItemStack().getItem() == ModItems.CREEPER_TRAVELERS_BACKPACK.get() && container.getAbilityValue() && container.getLastTime() <= 0)
            {
                player.setHealth(1.0F);
                player.removeAllEffects();
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 450, 1));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 400, 0));
                player.level().explode(player, player.damageSources().playerAttack(player), null, player.getRandomX(0.5F), player.getY(), player.getRandomZ(0.5F), 3.0F, false, Level.ExplosionInteraction.NONE);
                player.level().playSound(null, player.blockPosition(), SoundEvents.CREEPER_PRIMED, SoundSource.AMBIENT, 1.2F, 0.5F);

                if(!player.level().isClientSide)
                {
                    //container.setLastTime(CooldownHelper.randomTime(player.level().random, 600, 900));
                    container.setDataChanged(ITravelersBackpackContainer.LAST_TIME_DATA);
                }
                event.setCanceled(true);
                return true;
            }
        }
        return false;
    }
 */
    public void dragonAbility(Player player) {
        magmaCubeAbility(player);
        squidAbility(player);

        addTimedMobEffect(player, MobEffects.REGENERATION, 210, 240, 0, false, false, true);
        addTimedMobEffect(player, MobEffects.DAMAGE_BOOST, 210, 240, 0, false, false, true);
    }

    public void blazeAbility(Player player) {
        if(player.fallDistance >= 3.0F) {
            for(int i = 0; i < 4; ++i) {
                player.level().addParticle(ParticleTypes.LARGE_SMOKE, player.getRandomX(0.5D), player.getRandomY(), player.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
            }
            player.fallDistance = 0.0F;
        }
    }

    public static void blazeAbility(EntityHitResult result, SmallFireball fireball, CallbackInfo ci) {
        if(result.getEntity() instanceof Player player && ABILITIES.checkBackpack(player, ModItems.BLAZE_TRAVELERS_BACKPACK)) {
            player.level().playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 0.8F + player.level().random.nextFloat() * 0.4F);
            sendParticlesPacket(ParticleTypes.FLAME, player, 3);

            fireball.discard();
            ci.cancel();
        }
    }

  /*  public static void blazeAbility(ProjectileImpactEvent event) {
        if (event.getProjectile() instanceof SmallFireball fireball && event.getRayTraceResult().getType() == HitResult.Type.ENTITY) {
            EntityHitResult result = (EntityHitResult) event.getRayTraceResult();
            if (result.getEntity() instanceof Player player && ABILITIES.checkBackpack(player, ModItems.BLAZE_TRAVELERS_BACKPACK)) {
                player.level().playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 0.8F + player.level().random.nextFloat() * 0.4F);
                sendParticlesPacket(ParticleTypes.FLAME, player, 3);
                fireball.discard();
                event.setCanceled(true);
            }
        }
    } */

    public static void ghastAbility(Ghast ghast, LivingEntity livingEntity, CallbackInfo ci) {
        if(livingEntity instanceof Player player) {
            if(ABILITIES.checkBackpack(player, ModItems.GHAST_TRAVELERS_BACKPACK)) {
                if(ghast.getLastAttacker() != player) {
                    ci.cancel();
                }
            }
        }
    }

   /* public static void ghastAbility(LivingChangeTargetEvent event) {
        if (event.getEntity() instanceof Ghast ghast && event.getOriginalAboutToBeSetTarget() instanceof Player player) {
            if (ABILITIES.checkBackpack(player, ModItems.GHAST_TRAVELERS_BACKPACK)) {
                if (ghast.getLastHurtByMob() != player) {
                    event.setCanceled(true);
                }
            }
        }
    } */

    public void magmaCubeAbility(Player player) {
        addTimedMobEffect(player, MobEffects.FIRE_RESISTANCE, 210, 240, 0, false, false, true);
    }

    public void spiderAbility(Player player) {
        if(player.horizontalCollision && !player.isInLiquid()) {
            //Make player climb the wall if crashed with elytra
            if(player.isFallFlying()) {
                player.stopFallFlying();
            }

            if(!player.onGround() && player.isCrouching()) {
                player.setDeltaMovement(player.getDeltaMovement().x, 0.0D, player.getDeltaMovement().z);
            } else {
                player.setDeltaMovement(player.getDeltaMovement().x, 0.20D, player.getDeltaMovement().z);
                Level level = player.level();
                BlockState state = level.getBlockState(player.blockPosition().relative(player.getDirection()));
                /*player.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state).setPos(player.blockPosition()),
                        player.getX() + (level.random.nextDouble() - 0.5D) * (double) player.getDimensions(Pose.STANDING).width(),
                        player.getY() + 0.1D,
                        player.getZ() + (level.random.nextDouble() - 0.5D) * (double) player.getDimensions(Pose.STANDING).width(),
                        0.0D, 1.5D, 0.0D); */
            }
        }
    }

    public void witherAbility(Player player) {
        if(player.getEffect(MobEffects.WITHER) != null) {
            player.removeEffect(MobEffects.WITHER);
        }
    }

    public void batAbility(Player player) {
        addTimedMobEffect(player, MobEffects.NIGHT_VISION, 210, 240, 0, false, false, true);
    }

    public static void beeAbility(Player player, Entity target) {
        if(ABILITIES.checkBackpack(player, ModItems.BEE_TRAVELERS_BACKPACK)) {
            DamageSource damageSource = player.damageSources().sting(player);
            boolean flag = target.hurt(damageSource, 1.0F);

            if(flag) {
                if(player.level() instanceof ServerLevel serverLevel) {
                    EnchantmentHelper.doPostAttackEffects(serverLevel, target, damageSource);
                }

                if(target instanceof LivingEntity living) {
                    living.setStingerCount(living.getStingerCount() + 1);
                    living.addEffect(new MobEffectInstance(MobEffects.POISON, 4 * 20, 0), player);
                }
            }
        }
    }

    /*public static void beeAbility(AttackEntityEvent event) {
        if (ABILITIES.checkBackpack(event.getEntity(), ModItems.BEE_TRAVELERS_BACKPACK)) {
            DamageSource damageSource = event.getEntity().damageSources().sting(event.getEntity());
            boolean flag = event.getTarget().hurt(damageSource, 1.0F);
            if (flag) {
                if (event.getEntity().level() instanceof ServerLevel serverLevel) {
                    EnchantmentHelper.doPostAttackEffects(serverLevel, event.getTarget(), damageSource);
                }
                if (event.getTarget() instanceof LivingEntity living) {
                    living.setStingerCount(living.getStingerCount() + 1);
                    living.addEffect(new MobEffectInstance(MobEffects.POISON, 4 * 20, 0), event.getEntity());
                }
            }
        }
    } */

    private final TargetingConditions ocelotAbilityTargeting = TargetingConditions.forCombat().range(64.0D);

    public void ocelotAbility(Player player) {
        if(player.level().getNearestEntity(Monster.class, ocelotAbilityTargeting, player, player.getX(), player.getY(), player.getZ(), player.getBoundingBox().inflate(6.0D, 2.0D, 6.0D)) != null) {
            addTimedMobEffect(player, MobEffects.MOVEMENT_SPEED, 20, 30, 0, false, false, true);
        }
    }

    public void cowAbility(ItemStack stack, Player player) {
        //BackpackWrapper wrapper = AttachmentUtils.getBackpackWrapper(player, stack);
        if(!player.getActiveEffects().isEmpty() && stack.getOrDefault(ModDataComponents.COOLDOWN, 0) <= 0) {
            BackpackWrapper wrapper = ComponentUtils.getBackpackWrapper(player, stack);
            if(!player.level().isClientSide) {
                player.level().levelEvent(2007, player.blockPosition(), 16777215);
                wrapper.setCooldown(CooldownHelper.createCooldown(450, 600));
            }
            player.level().playSound(null, player.blockPosition(), SoundEvents.HONEYCOMB_WAX_ON, SoundSource.PLAYERS, 1.0F, player.getRandom().nextFloat() * 0.1F + 0.9F);
            player.removeAllEffects();//.removeEffectsCuredBy(EffectCures.MILK);
            //wrapper.setCooldown(CooldownHelper.createCooldown(450, 600));
        }
    }

    public void squidAbility(Player player) {
        if(player.isInWater()) {
            addTimedMobEffect(player, MobEffects.WATER_BREATHING, 210, 240, 0, false, false, true);
            batAbility(player);
        }
    }

    //Utility methods

    private boolean isUnderRain(BlockPos pos, Level level) {
        return level.canSeeSky(pos) && level.isRaining();
    }

    public static boolean isAbilityEnabledInConfig(ItemStack stack) {
        if(!TravelersBackpackConfig.getConfig().backpackAbilities.enableBackpackAbilities || !BackpackAbilities.ALLOWED_ABILITIES.contains(stack.getItem())) {
            return false;
        }
        return true;
    }

    public boolean checkBackpack(Player player, Item item) {
        if(!TravelersBackpackConfig.getConfig().backpackAbilities.enableBackpackAbilities || !BackpackAbilities.ALLOWED_ABILITIES.contains(item)) {
            return false;
        }
        return ComponentUtils.isWearingBackpack(player) && ComponentUtils.getWearingBackpack(player).getItem() == item && ComponentUtils.getWearingBackpack(player).getOrDefault(ModDataComponents.ABILITY_ENABLED, false);
    }

    public void addTimedMobEffect(Player player, Holder<MobEffect> effect, int minDuration, int maxDuration, int amplifier, boolean ambient, boolean showParticle, boolean showIcon) {
        if(!player.hasEffect(effect)) {
            player.addEffect(new MobEffectInstance(effect, maxDuration, amplifier, ambient, showParticle, showIcon));
        } else if(player.hasEffect(effect)) {
            if(player.getEffect(effect).getDuration() <= minDuration) {
                player.addEffect(new MobEffectInstance(effect, maxDuration, amplifier, ambient, showParticle, showIcon));
            }
        }
    }

    public static void sendParticlesPacket(ParticleOptions type, Player player, int count) {
        for(int i = 0; i < count; i++) {
            double d0 = player.level().random.nextGaussian() * 0.02D;
            double d1 = player.level().random.nextGaussian() * 0.02D;
            double d2 = player.level().random.nextGaussian() * 0.02D;
            if(player.level() instanceof ServerLevel server) {
                server.sendParticles(type, player.getRandomX(1.0D), player.getRandomY() + 0.5D, player.getRandomZ(1.0D), 1, d0, d1, d2, 0.0F);
            }
        }
    }

    public static boolean isOnList(Item[] list, ItemStack stackToCheck) {
        return Arrays.stream(list).anyMatch(s -> s == stackToCheck.getItem());
    }

    public static final List<Item> ALLOWED_ABILITIES = new ArrayList<>();

    public static final Item[] ALL_ABILITIES_LIST = {

            ModItems.NETHERITE_TRAVELERS_BACKPACK,
            ModItems.DIAMOND_TRAVELERS_BACKPACK,
            ModItems.GOLD_TRAVELERS_BACKPACK,
            ModItems.EMERALD_TRAVELERS_BACKPACK,
            ModItems.IRON_TRAVELERS_BACKPACK,
            ModItems.LAPIS_TRAVELERS_BACKPACK,
            ModItems.REDSTONE_TRAVELERS_BACKPACK,

            ModItems.BOOKSHELF_TRAVELERS_BACKPACK,
            //ModItems.END_TRAVELERS_BACKPACK,
            //ModItems.NETHER_TRAVELERS_BACKPACK,
            //ModItems.SANDSTONE_TRAVELERS_BACKPACK,
            //ModItems.SNOW_TRAVELERS_BACKPACK,
            ModItems.SPONGE_TRAVELERS_BACKPACK,

            ModItems.CAKE_TRAVELERS_BACKPACK,

            ModItems.CACTUS_TRAVELERS_BACKPACK,
            // ModItems.HAY_TRAVELERS_BACKPACK,
            ModItems.MELON_TRAVELERS_BACKPACK,
            ModItems.PUMPKIN_TRAVELERS_BACKPACK,

            ModItems.CREEPER_TRAVELERS_BACKPACK,
            ModItems.DRAGON_TRAVELERS_BACKPACK,
            ModItems.ENDERMAN_TRAVELERS_BACKPACK,
            ModItems.BLAZE_TRAVELERS_BACKPACK,
            ModItems.GHAST_TRAVELERS_BACKPACK,
            ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK,
            //ModItems.SKELETON_TRAVELERS_BACKPACK,
            ModItems.SPIDER_TRAVELERS_BACKPACK,
            ModItems.WITHER_TRAVELERS_BACKPACK,
            ModItems.WARDEN_TRAVELERS_BACKPACK,

            ModItems.BAT_TRAVELERS_BACKPACK,
            ModItems.BEE_TRAVELERS_BACKPACK,
            // ModItems.WOLF_TRAVELERS_BACKPACK,
            // ModItems.FOX_TRAVELERS_BACKPACK,
            ModItems.OCELOT_TRAVELERS_BACKPACK,
            // ModItems.HORSE_TRAVELERS_BACKPACK,
            ModItems.COW_TRAVELERS_BACKPACK,
            //  ModItems.PIG_TRAVELERS_BACKPACK,
            //  ModItems.SHEEP_TRAVELERS_BACKPACK,
            ModItems.CHICKEN_TRAVELERS_BACKPACK,
            ModItems.SQUID_TRAVELERS_BACKPACK
            //  ModItems.IRON_GOLEM_TRAVELERS_BACKPACK
    };

    public static final Item[] ITEM_ABILITIES_LIST = {

            ModItems.NETHERITE_TRAVELERS_BACKPACK,
            ModItems.DIAMOND_TRAVELERS_BACKPACK,
            ModItems.GOLD_TRAVELERS_BACKPACK,
            ModItems.EMERALD_TRAVELERS_BACKPACK,
            ModItems.IRON_TRAVELERS_BACKPACK,
            ModItems.LAPIS_TRAVELERS_BACKPACK,

            //ModItems.BOOKSHELF_TRAVELERS_BACKPACK,

            //ModItems.END_TRAVELERS_BACKPACK,
            //ModItems.NETHER_TRAVELERS_BACKPACK,
            //ModItems.SANDSTONE_TRAVELERS_BACKPACK,
            //ModItems.SNOW_TRAVELERS_BACKPACK,

            ModItems.CAKE_TRAVELERS_BACKPACK,

            ModItems.CACTUS_TRAVELERS_BACKPACK,
            //ModItems.HAY_TRAVELERS_BACKPACK,
            //ModItems.MELON_TRAVELERS_BACKPACK,
            ModItems.PUMPKIN_TRAVELERS_BACKPACK,

            ModItems.CREEPER_TRAVELERS_BACKPACK,
            ModItems.DRAGON_TRAVELERS_BACKPACK,
            ModItems.ENDERMAN_TRAVELERS_BACKPACK,
            ModItems.BLAZE_TRAVELERS_BACKPACK,
            ModItems.GHAST_TRAVELERS_BACKPACK,
            ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK,
            //ModItems.SKELETON_TRAVELERS_BACKPACK,
            ModItems.SPIDER_TRAVELERS_BACKPACK,
            ModItems.WITHER_TRAVELERS_BACKPACK,
            ModItems.WARDEN_TRAVELERS_BACKPACK,

            ModItems.BAT_TRAVELERS_BACKPACK,
            ModItems.BEE_TRAVELERS_BACKPACK,
            // ModItems.WOLF_TRAVELERS_BACKPACK,
            //ModItems.FOX_TRAVELERS_BACKPACK,
            ModItems.OCELOT_TRAVELERS_BACKPACK,
            //ModItems.HORSE_TRAVELERS_BACKPACK,
            ModItems.COW_TRAVELERS_BACKPACK,
            //ModItems.PIG_TRAVELERS_BACKPACK,
            //ModItems.SHEEP_TRAVELERS_BACKPACK,
            ModItems.CHICKEN_TRAVELERS_BACKPACK,
            ModItems.SQUID_TRAVELERS_BACKPACK
            //ModItems.IRON_GOLEM_TRAVELERS_BACKPACK
    };

    public static final Item[] ITEM_ABILITIES_REMOVAL_LIST = {

            ModItems.NETHERITE_TRAVELERS_BACKPACK,
            ModItems.DIAMOND_TRAVELERS_BACKPACK,
            ModItems.GOLD_TRAVELERS_BACKPACK,
            //ModItems.EMERALD_TRAVELERS_BACKPACK,
            ModItems.IRON_TRAVELERS_BACKPACK,

            //ModItems.END_TRAVELERS_BACKPACK,
            //ModItems.NETHER_TRAVELERS_BACKPACK,
            //ModItems.SANDSTONE_TRAVELERS_BACKPACK,
            //ModItems.SNOW_TRAVELERS_BACKPACK,

            //ModItems.CAKE_TRAVELERS_BACKPACK,

            //ModItems.CACTUS_TRAVELERS_BACKPACK,
            //ModItems.HAY_TRAVELERS_BACKPACK,
            //ModItems.MELON_TRAVELERS_BACKPACK,
            //ModItems.PUMPKIN_TRAVELERS_BACKPACK,

            //ModItems.CREEPER_TRAVELERS_BACKPACK,
            //ModItems.DRAGON_TRAVELERS_BACKPACK,
            ModItems.ENDERMAN_TRAVELERS_BACKPACK,
            //ModItems.BLAZE_TRAVELERS_BACKPACK,
            //ModItems.GHAST_TRAVELERS_BACKPACK,
            //ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK,
            //ModItems.SKELETON_TRAVELERS_BACKPACK,
            //ModItems.SPIDER_TRAVELERS_BACKPACK,
            //ModItems.WITHER_TRAVELERS_BACKPACK,
            ModItems.WARDEN_TRAVELERS_BACKPACK

            //ModItems.BAT_TRAVELERS_BACKPACK,
            // ModItems.BEE_TRAVELERS_BACKPACK,
            // ModItems.WOLF_TRAVELERS_BACKPACK,
            //ModItems.FOX_TRAVELERS_BACKPACK,
            //ModItems.OCELOT_TRAVELERS_BACKPACK,
            //ModItems.HORSE_TRAVELERS_BACKPACK,
            //ModItems.COW_TRAVELERS_BACKPACK,
            //ModItems.PIG_TRAVELERS_BACKPACK,
            //ModItems.SHEEP_TRAVELERS_BACKPACK,
            // ModItems.CHICKEN_TRAVELERS_BACKPACK,
            // ModItems.SQUID_TRAVELERS_BACKPACK,
            //ModItems.IRON_GOLEM_TRAVELERS_BACKPACK
    };

    public static final Item[] ITEM_TIMER_ABILITIES_LIST = {
            ModItems.CAKE_TRAVELERS_BACKPACK,

            ModItems.CREEPER_TRAVELERS_BACKPACK,

            ModItems.COW_TRAVELERS_BACKPACK,
            ModItems.CHICKEN_TRAVELERS_BACKPACK
    };

    public static final Item[] BLOCK_TIMER_ABILITIES_LIST = {
            ModItems.MELON_TRAVELERS_BACKPACK
    };

    public static final Item[] BLOCK_ABILITIES_LIST = {

            ModItems.EMERALD_TRAVELERS_BACKPACK,
            //ModItems.LAPIS_TRAVELERS_BACKPACK,
            ModItems.REDSTONE_TRAVELERS_BACKPACK,

            ModItems.BOOKSHELF_TRAVELERS_BACKPACK,
            //ModItems.END_TRAVELERS_BACKPACK,
            //ModItems.NETHER_TRAVELERS_BACKPACK,
            //ModItems.SANDSTONE_TRAVELERS_BACKPACK,
            //ModItems.SNOW_TRAVELERS_BACKPACK,
            ModItems.SPONGE_TRAVELERS_BACKPACK,

            //ModItems.CAKE_TRAVELERS_BACKPACK,

            ModItems.CACTUS_TRAVELERS_BACKPACK,
            //ModItems.HAY_TRAVELERS_BACKPACK,
            ModItems.MELON_TRAVELERS_BACKPACK
            // ModItems.PUMPKIN_TRAVELERS_BACKPACK,

            // ModItems.ENDERMAN_TRAVELERS_BACKPACK,
            // ModItems.GHAST_TRAVELERS_BACKPACK,
            // ModItems.SKELETON_TRAVELERS_BACKPACK,
            // ModItems.SPIDER_TRAVELERS_BACKPACK,

            // ModItems.BEE_TRAVELERS_BACKPACK,
            //  ModItems.WOLF_TRAVELERS_BACKPACK,
            //  ModItems.FOX_TRAVELERS_BACKPACK,
            //  ModItems.HORSE_TRAVELERS_BACKPACK,
            //  ModItems.COW_TRAVELERS_BACKPACK,
            // ModItems.PIG_TRAVELERS_BACKPACK,
            // ModItems.SHEEP_TRAVELERS_BACKPACK,
            // ModItems.IRON_GOLEM_TRAVELERS_BACKPACK
    };
}