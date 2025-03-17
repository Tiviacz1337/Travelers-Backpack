package com.tiviacz.travelersbackpack.common;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.BackpackEffect;
import com.tiviacz.travelersbackpack.config.Cooldown;
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
import net.minecraft.core.particles.BlockParticleOption;
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
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
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

import java.util.*;

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
     * Return TRUE to enable ability cooldown decreasing
     */
    public boolean abilityTick(@Nullable ItemStack backpack, @Nullable Player player) {
        boolean tickCooldown = false;
        if(backpack != null) {
            Item backpackItem = backpack.getItem();

            //Check if backpack has cooldown set in config
            boolean effectHasCooldown = false;

            //Has effect associated
            if(getBackpackEffects().containsKey(backpack.getItem())) {
                //Check if there's backpack entry in cooldowns config
                if(getCooldowns().containsKey(backpack.getItem())) {
                    effectHasCooldown = true;
                }
                //If not, then add timed mob effect and re-apply them, without any cooldown ticking
                if(!effectHasCooldown) {
                    Collection<BackpackEffect> backpackEffects = getBackpackEffects().get(backpackItem);
                    for(BackpackEffect backpackEffect : backpackEffects) {
                        addTimedMobEffect(player, backpackEffect.effect(), backpackEffect.minDuration(), backpackEffect.maxDuration(), backpackEffect.amplifier(), false, false, false);
                    }
                } else { //If yes, then check if there's active cooldown
                    //If no active cooldown
                    if(!hasCooldown(backpack)) {
                        //Apply effects
                        Collection<BackpackEffect> backpackEffects = getBackpackEffects().get(backpackItem);
                        for(BackpackEffect backpackEffect : backpackEffects) {
                            addTimedMobEffect(player, backpackEffect.effect(), backpackEffect.minDuration(), backpackEffect.maxDuration(), backpackEffect.amplifier(), false, false, false);
                        }
                        //Apply cooldown
                        setCooldown(ComponentUtils.getBackpackWrapper(player, backpack), backpackItem);
                    }
                    //Tick cooldown, but return at the end to check if there's any custom ability associated with backpack
                    tickCooldown = true;
                }
            }

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
                attributeAbility(player, false, Attributes.LUCK, LUCK_MODIFIER);
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
                cakeAbility(backpack, player);
                return true;
            }

            if(backpackItem == ModItems.CACTUS_TRAVELERS_BACKPACK) {
                cactusAbilityEquipped(player, backpack);
                return false;
            }

            if(backpackItem == ModItems.CHICKEN_TRAVELERS_BACKPACK) {
                chickenAbility(backpack, player, false);
                return true;
            }

            if(backpackItem == ModItems.CREEPER_TRAVELERS_BACKPACK) {
                return true;
            }

            if(backpackItem == ModItems.BLAZE_TRAVELERS_BACKPACK) {
                blazeAbility(player);
                return false;
            }

            if(backpackItem == ModItems.SPIDER_TRAVELERS_BACKPACK) {
                spiderAbility(player);
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

            if(backpackItem == ModItems.WITHER_TRAVELERS_BACKPACK) {
                witherAbilityTick(player);
                return false;
            }
        }
        return tickCooldown;
    }

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

        if(stack.getItem() == ModItems.EMERALD_TRAVELERS_BACKPACK) {
            attributeAbility(player, true, Attributes.LUCK, LUCK_MODIFIER);
        }
    }

    /**
     * Called in TravelersBackpackBlock#animateTick method to enable visual only abilities for BackpackBlockEntity
     */

    public void animateTick(BackpackBlockEntity backpackBlockEntity, BlockState stateIn, Level level, BlockPos pos, RandomSource rand) {
        if(backpackBlockEntity.getWrapper() != null && backpackBlockEntity.getWrapper().isAbilityEnabled()) {
            Block block = stateIn.getBlock();
            if(block == ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK) {
                bookshelfAbility(null, backpackBlockEntity);
            }

            if(block == ModBlocks.SPONGE_TRAVELERS_BACKPACK) {
                spongeAbility(backpackBlockEntity);
            }
        }
    }

    public final AttributeModifier NETHERITE_ARMOR_MODIFIER = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "netherite_backpack_armor"), 4.0D, AttributeModifier.Operation.ADD_VALUE);
    public final AttributeModifier DIAMOND_ARMOR_MODIFIER = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "diamond_backpack_armor"), 3.0D, AttributeModifier.Operation.ADD_VALUE);
    public final AttributeModifier IRON_ARMOR_MODIFIER = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "iron_backpack_armor"), 2.0D, AttributeModifier.Operation.ADD_VALUE);
    public final AttributeModifier GOLD_ARMOR_MODIFIER = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "gold_backpack_armor"), 2.0D, AttributeModifier.Operation.ADD_VALUE);
    public final AttributeModifier ENDERMAN_REACH_DISTANCE_MODIFIER = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "enderman_backpack_reach"), 1.0D, AttributeModifier.Operation.ADD_VALUE);
    public final AttributeModifier WARDEN_MAX_HEALTH_MODIFIER = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "warden_backpack_max_health"), 4.0D, AttributeModifier.Operation.ADD_VALUE);
    public final AttributeModifier LUCK_MODIFIER = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "emerald_backpack_luck"), 1.0D, AttributeModifier.Operation.ADD_VALUE);

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeAbilityMultimap(ItemStack backpack) {
        Multimap<Holder<Attribute>, AttributeModifier> multimap = ArrayListMultimap.create();
        if(backpack.getItem() == ModItems.NETHERITE_TRAVELERS_BACKPACK) {
            multimap.put(Attributes.ARMOR, NETHERITE_ARMOR_MODIFIER);
            return multimap;
        }
        if(backpack.getItem() == ModItems.DIAMOND_TRAVELERS_BACKPACK) {
            multimap.put(Attributes.ARMOR, DIAMOND_ARMOR_MODIFIER);
            return multimap;
        }
        if(backpack.getItem() == ModItems.GOLD_TRAVELERS_BACKPACK) {
            multimap.put(Attributes.ARMOR, GOLD_ARMOR_MODIFIER);
            return multimap;
        }
        if(backpack.getItem() == ModItems.IRON_TRAVELERS_BACKPACK) {
            multimap.put(Attributes.ARMOR, IRON_ARMOR_MODIFIER);
            return multimap;
        }
        if(backpack.getItem() == ModItems.ENDERMAN_TRAVELERS_BACKPACK) {
            multimap.put(Attributes.BLOCK_INTERACTION_RANGE, ENDERMAN_REACH_DISTANCE_MODIFIER);
            return multimap;
        }
        if(backpack.getItem() == ModItems.WARDEN_TRAVELERS_BACKPACK) {
            multimap.put(Attributes.MAX_HEALTH, WARDEN_MAX_HEALTH_MODIFIER);
            return multimap;
        }
       /* if(backpack.getItem() == ModItems.FOX_TRAVELERS_BACKPACK) {
            multimap.put(Attributes.MOVEMENT_SPEED, FOX_MOVEMENT_SPEED_MODIFIER);
            return multimap;
        }*/
        if(backpack.getItem() == ModItems.EMERALD_TRAVELERS_BACKPACK) {
            multimap.put(Attributes.LUCK, LUCK_MODIFIER);
            return multimap;
        }
        return multimap;
    }

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
        attributeAbility(player, true, Attributes.LUCK, LUCK_MODIFIER);
    }

    public int lapisAbility(Player player) {
        if(ABILITIES.checkBackpack(player, ModItems.LAPIS_TRAVELERS_BACKPACK)) {
            float random = player.getRandom().nextFloat();
            if(random <= 0.15F) {
                if(random <= 0.025F) {
                    sendParticlesPacket(ParticleTypes.GLOW, player, 2);
                }
                return 2;
            }
        }
        return 1;
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

    //Restores Hunger and grants Regeneration I for 10 seconds
    public void cakeAbility(ItemStack backpack, Player player) {
        if(!hasCooldown(backpack)) {
            player.getFoodData().eat(20, 0.1F);
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 10 * 20));
            player.level().playSound(null, player.blockPosition(), SoundEvents.GENERIC_EAT.value(), SoundSource.AMBIENT, 0.6F, (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.3F + 1.0F);

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
            if(getCooldowns().containsKey(backpack.getItem())) {
                Cooldown config = getCooldowns().get(backpack.getItem());
                backpack.set(ModDataComponents.COOLDOWN, CooldownHelper.createCooldown(config.minCooldown(), config.maxCooldown()));
            }
        }
    }

    public void chickenAbility(ItemStack backpack, Player player, boolean firstSwitch) {
        if(firstSwitch && !player.level().isClientSide) {
            if(!hasCooldown(backpack)) {
                BackpackWrapper wrapper = ComponentUtils.getBackpackWrapper(player);
                setCooldown(wrapper, wrapper.getBackpackStack().getItem());
                return;
            }
        }
        if(!hasCooldown(backpack)) {
            BackpackWrapper wrapper = ComponentUtils.getBackpackWrapper(player);
            player.level().playSound(null, player.blockPosition(), SoundEvents.CHICKEN_EGG, SoundSource.AMBIENT, 1.0F, (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.3F + 1.0F);
            if(player.level().isClientSide) return;
            if(player.level() instanceof ServerLevel serverLevel) {
                player.spawnAtLocation(serverLevel, Items.EGG);
            }
            setCooldown(wrapper, wrapper.getBackpackStack().getItem());
        }
    }

    public void cactusAbilityEquipped(@Nullable Player player, @Nullable ItemStack backpack) {
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

    public static void melonAbility(BackpackBlockEntity backpackBlockEntity) {
        if(backpackBlockEntity.getWrapper().isAbilityEnabled() && backpackBlockEntity.getWrapper().getCooldown() <= 0) {
            Block.popResource(backpackBlockEntity.getLevel(), backpackBlockEntity.getBlockPos(), new ItemStack(Items.MELON_SLICE, backpackBlockEntity.getLevel().random.nextInt(0, 3)));
            setCooldown(backpackBlockEntity.getWrapper(), backpackBlockEntity.getWrapper().getBackpackStack().getItem());
        }
    }

    public static void pumpkinAbility(Player player, CallbackInfoReturnable<Boolean> cir) {
        boolean flag = BackpackAbilities.ABILITIES.checkBackpack(player, ModItems.PUMPKIN_TRAVELERS_BACKPACK);
        if(flag) {
            cir.setReturnValue(false);
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
                setCooldown(wrapper, wrapper.getBackpackStack().getItem());
            }
            return true;
        }
        return false;
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

    public static void ghastAbility(Ghast ghast, LivingEntity livingEntity, CallbackInfo ci) {
        if(livingEntity instanceof Player player) {
            if(ABILITIES.checkBackpack(player, ModItems.GHAST_TRAVELERS_BACKPACK)) {
                if(ghast.getLastAttacker() != player) {
                    ci.cancel();
                }
            }
        }
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
                player.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state),
                        player.getX() + (level.random.nextDouble() - 0.5D) * (double)player.getDimensions(Pose.STANDING).width(),
                        player.getY() + 0.1D,
                        player.getZ() + (level.random.nextDouble() - 0.5D) * (double)player.getDimensions(Pose.STANDING).width(),
                        0.0D, 1.5D, 0.0D);
            }
        }
    }

    public static void witherAbilityTick(Player player) {
        if(ABILITIES.checkBackpack(player, ModItems.WITHER_TRAVELERS_BACKPACK)) {
            if(player.hasEffect(MobEffects.WITHER)) {
                player.removeEffect(MobEffects.WITHER);
            }
        }
    }

    public static void witherAbility(Player player, Entity target) {
        if(ABILITIES.checkBackpack(player, ModItems.WITHER_TRAVELERS_BACKPACK)) {
            if(target instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.WITHER, 3 * 20, 1));
            }
        }
    }

    public static void wardenAbility(Player player, Entity target) {
        if(ABILITIES.checkBackpack(player, ModItems.WARDEN_TRAVELERS_BACKPACK)) {
            if(target instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2 * 20, 1));
            }
        }
    }

    public static void beeAbility(Player player, Entity target) {
        if(ABILITIES.checkBackpack(player, ModItems.BEE_TRAVELERS_BACKPACK)) {
            if(player.level() instanceof ServerLevel serverLevel) {
                DamageSource damageSource = player.damageSources().sting(player);
                boolean flag = target.hurtServer(serverLevel, damageSource, 1.0F);
                if(flag) {
                    EnchantmentHelper.doPostAttackEffects(serverLevel, target, damageSource);
                    if(target instanceof LivingEntity living) {
                        living.setStingerCount(living.getStingerCount() + 1);
                        living.addEffect(new MobEffectInstance(MobEffects.POISON, 4 * 20, 0), player);
                    }
                }
            }
        }
    }

    private final TargetingConditions ocelotAbilityTargeting = TargetingConditions.forCombat().range(64.0D);

    public void ocelotAbility(Player player) {
        if(player.level() instanceof ServerLevel serverLevel) {
            if(serverLevel.getNearestEntity(Monster.class, ocelotAbilityTargeting, player, player.getX(), player.getY(), player.getZ(), player.getBoundingBox().inflate(6.0D, 2.0D, 6.0D)) != null) {
                addTimedMobEffect(player, MobEffects.MOVEMENT_SPEED, 20, 30, 0, false, false, false);
            }
        }
    }

    public void cowAbility(ItemStack stack, Player player) {
        if(!player.getActiveEffects().isEmpty() && !hasCooldown(stack)) {
            if(player.getActiveEffects().stream().anyMatch(effect -> effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL)) {
                BackpackWrapper wrapper = ComponentUtils.getBackpackWrapper(player, stack);
                if(!player.level().isClientSide) {
                    player.level().levelEvent(2007, player.blockPosition(), 16777215);
                    setCooldown(wrapper, stack.getItem());
                }
                player.level().playSound(null, player.blockPosition(), SoundEvents.HONEYCOMB_WAX_ON, SoundSource.PLAYERS, 1.0F, player.getRandom().nextFloat() * 0.1F + 0.9F);
                removeAllNegativeEffects(player.level(), player);
            }
        }
    }

    public boolean removeAllNegativeEffects(Level level, Player player) {
        if(level.isClientSide) {
            return false;
        } else if(player.getActiveEffects().isEmpty()) {
            return false;
        } else {
            Collection<MobEffectInstance> negativeEffects = player.getActiveEffects().stream().filter(effect -> effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL).toList();
            for(MobEffectInstance instance : negativeEffects) {
                player.removeEffect(instance.getEffect());
            }
            return true;
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
        return ComponentUtils.isWearingBackpack(player) && ComponentUtils.getWearingBackpack(player).getItem() == item && ComponentUtils.getWearingBackpack(player).getOrDefault(ModDataComponents.ABILITY_ENABLED, true);
    }

    public void addTimedMobEffect(Player player, Holder<MobEffect> effect, int minDuration, int maxDuration, int amplifier, boolean ambient, boolean showParticle, boolean showIcon) {
        if(!player.hasEffect(effect)) {
            player.addEffect(new MobEffectInstance(effect, maxDuration, amplifier, ambient, showParticle, showIcon));
        } else if(player.hasEffect(effect)) {
            if(player.getEffect(effect) != null && player.getEffect(effect).getDuration() <= minDuration) {
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

    public static boolean isOnList(List<Item> list, ItemStack stackToCheck) {
        return list.stream().anyMatch(s -> s == stackToCheck.getItem());
    }

    public static boolean hasCooldown(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.COOLDOWN, 0) > 0;
    }

    public static void setCooldown(BackpackWrapper wrapper, Item item) {
        if(getCooldowns().containsKey(item)) {
            Cooldown cooldown = getCooldowns().get(item);
            wrapper.setCooldown(CooldownHelper.createCooldown(cooldown.minCooldown(), cooldown.maxCooldown()));
        }
    }

    public static List<Item> getAllowedAbilities() {
        return ALLOWED_ABILITIES;
    }

    public static Map<Item, Cooldown> getCooldowns() {
        return COOLDOWNS;
    }

    public static Multimap<Item, BackpackEffect> getBackpackEffects() {
        return BACKPACK_EFFECTS;
    }

    public static final List<Item> ALLOWED_ABILITIES = new ArrayList<>();
    public static final Multimap<Item, BackpackEffect> BACKPACK_EFFECTS = ArrayListMultimap.create();
    public static final Map<Item, Cooldown> COOLDOWNS = new HashMap<>();

    //All equipped backpack abilities
    public static List<Item> ITEM_ABILITIES_LIST = new ArrayList<>(List.of(
            ModItems.NETHERITE_TRAVELERS_BACKPACK,
            ModItems.DIAMOND_TRAVELERS_BACKPACK,
            ModItems.GOLD_TRAVELERS_BACKPACK,
            ModItems.EMERALD_TRAVELERS_BACKPACK,
            ModItems.IRON_TRAVELERS_BACKPACK,
            ModItems.LAPIS_TRAVELERS_BACKPACK,

            ModItems.QUARTZ_TRAVELERS_BACKPACK,
            ModItems.CAKE_TRAVELERS_BACKPACK,

            ModItems.CACTUS_TRAVELERS_BACKPACK,
            ModItems.HAY_TRAVELERS_BACKPACK,
            ModItems.PUMPKIN_TRAVELERS_BACKPACK,

            ModItems.CREEPER_TRAVELERS_BACKPACK,
            ModItems.DRAGON_TRAVELERS_BACKPACK,
            ModItems.ENDERMAN_TRAVELERS_BACKPACK,
            ModItems.BLAZE_TRAVELERS_BACKPACK,
            ModItems.GHAST_TRAVELERS_BACKPACK,
            ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK,
            ModItems.SPIDER_TRAVELERS_BACKPACK,
            ModItems.WITHER_TRAVELERS_BACKPACK,
            ModItems.WARDEN_TRAVELERS_BACKPACK,

            ModItems.BAT_TRAVELERS_BACKPACK,
            ModItems.BEE_TRAVELERS_BACKPACK,
            ModItems.OCELOT_TRAVELERS_BACKPACK,
            ModItems.COW_TRAVELERS_BACKPACK,
            ModItems.CHICKEN_TRAVELERS_BACKPACK,
            ModItems.SQUID_TRAVELERS_BACKPACK
    ));

    //Removals for attribute modifier abilities
    public static List<Item> ITEM_ABILITIES_REMOVAL_LIST = new ArrayList<>(List.of(
            ModItems.NETHERITE_TRAVELERS_BACKPACK,
            ModItems.DIAMOND_TRAVELERS_BACKPACK,
            ModItems.GOLD_TRAVELERS_BACKPACK,
            ModItems.IRON_TRAVELERS_BACKPACK,
            ModItems.EMERALD_TRAVELERS_BACKPACK,

            ModItems.ENDERMAN_TRAVELERS_BACKPACK,
            ModItems.WARDEN_TRAVELERS_BACKPACK
    ));

    //All block backpack abilities
    public static List<Item> BLOCK_ABILITIES_LIST = new ArrayList<>(List.of(
            ModItems.REDSTONE_TRAVELERS_BACKPACK,

            ModItems.BOOKSHELF_TRAVELERS_BACKPACK,
            ModItems.SPONGE_TRAVELERS_BACKPACK,

            ModItems.CACTUS_TRAVELERS_BACKPACK,
            ModItems.MELON_TRAVELERS_BACKPACK
    ));

    //All equipped backpack abilities
    public static List<Item> CUSTOM_DESCRIPTIONS = new ArrayList<>(List.of(
            ModItems.LAPIS_TRAVELERS_BACKPACK,
            ModItems.REDSTONE_TRAVELERS_BACKPACK,

            ModItems.BOOKSHELF_TRAVELERS_BACKPACK,
            ModItems.SPONGE_TRAVELERS_BACKPACK,

            ModItems.CAKE_TRAVELERS_BACKPACK,

            ModItems.CACTUS_TRAVELERS_BACKPACK,
            ModItems.HAY_TRAVELERS_BACKPACK,
            ModItems.PUMPKIN_TRAVELERS_BACKPACK,
            ModItems.MELON_TRAVELERS_BACKPACK,

            ModItems.CREEPER_TRAVELERS_BACKPACK,
            ModItems.ENDERMAN_TRAVELERS_BACKPACK,
            ModItems.BLAZE_TRAVELERS_BACKPACK,
            ModItems.GHAST_TRAVELERS_BACKPACK,
            ModItems.SPIDER_TRAVELERS_BACKPACK,
            ModItems.WITHER_TRAVELERS_BACKPACK,
            ModItems.WARDEN_TRAVELERS_BACKPACK,

            ModItems.BEE_TRAVELERS_BACKPACK,
            ModItems.OCELOT_TRAVELERS_BACKPACK,
            ModItems.COW_TRAVELERS_BACKPACK,
            ModItems.CHICKEN_TRAVELERS_BACKPACK
    ));
}