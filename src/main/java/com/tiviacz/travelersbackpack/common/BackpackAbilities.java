package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.TimeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.EnderManAngerEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.UUID;

public class BackpackAbilities
{
    /**
     * Main class for all available abilities
     * connects to few events and block methods to execute/remove proper abilities
     * It's such a mess right now, I might create better system for all of that in the future.
     *
     * //Connecting abilities to player, abilities removals
     * {@link com.tiviacz.travelersbackpack.handlers.ForgeEventHandler#playerTick(TickEvent.PlayerTickEvent)}
     *
     * //Connecting abilities to block entity
     * {@link TravelersBackpackBlockEntity#tick(Level, BlockPos, BlockState, TravelersBackpackBlockEntity)}
     *
     * //Ability removals
     * {@link ServerActions#switchAbilitySlider(Player, boolean)}
     * {@link ServerActions#switchAbilitySliderBlockEntity(Player, BlockPos, boolean)}
     *
     * //Cosmetic only
     * {@link com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock#animateTick(BlockState, Level, BlockPos, RandomSource)}
     *
     * //Few uses of block abilities
     * {@link com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock}
     *
     * //Creeper ability
     * {@link com.tiviacz.travelersbackpack.handlers.ForgeEventHandler#playerDeath(LivingDeathEvent)}
     */
    public static final BackpackAbilities ABILITIES = new BackpackAbilities();

    /**
     * Called in TravelersBackpackTileEntity#Tick and ForgeEventHandler#playerTick method to enable abilities
     */
    public void abilityTick(@Nullable ItemStack stack, @Nullable Player player, @Nullable TravelersBackpackBlockEntity blockEntity)
    {
        if(blockEntity == null) //WEARABLE ABILITIES
        {
            if(stack.getItem() == ModItems.NETHERITE_TRAVELERS_BACKPACK.get())
            {
                attributeAbility(player, false, Attributes.ARMOR, NETHERITE_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.DIAMOND_TRAVELERS_BACKPACK.get())
            {
                attributeAbility(player, false, Attributes.ARMOR, DIAMOND_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.GOLD_TRAVELERS_BACKPACK.get())
            {
                attributeAbility(player, false, Attributes.ARMOR, GOLD_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.EMERALD_TRAVELERS_BACKPACK.get())
            {
                emeraldAbility(player, null);
            }

            if(stack.getItem() == ModItems.IRON_TRAVELERS_BACKPACK.get())
            {
                attributeAbility(player, false, Attributes.ARMOR, IRON_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.CAKE_TRAVELERS_BACKPACK.get())
            {
                cakeAbility(player);
            }

            if(stack.getItem() == ModItems.CACTUS_TRAVELERS_BACKPACK.get())
            {
                cactusAbility(player, null);
            }

            if(stack.getItem() == ModItems.DRAGON_TRAVELERS_BACKPACK.get())
            {
                dragonAbility(player);
            }

            if(stack.getItem() == ModItems.ENDERMAN_TRAVELERS_BACKPACK.get())
            {
                attributeAbility(player, false, ForgeMod.REACH_DISTANCE.get(), ENDERMAN_REACH_DISTANCE_MODIFIER);
            }

            if(stack.getItem() == ModItems.BLAZE_TRAVELERS_BACKPACK.get())
            {
                blazeAbility(player);
            }

            if(stack.getItem() == ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK.get())
            {
                magmaCubeAbility(player);
            }

            if(stack.getItem() == ModItems.SPIDER_TRAVELERS_BACKPACK.get())
            {
                spiderAbility(player);
            }

            if(stack.getItem() == ModItems.WITHER_TRAVELERS_BACKPACK.get())
            {
                witherAbility(player);
            }

            if(stack.getItem() == ModItems.BAT_TRAVELERS_BACKPACK.get())
            {
                batAbility(player);
            }

            if(stack.getItem() == ModItems.OCELOT_TRAVELERS_BACKPACK.get())
            {
                ocelotAbility(player);
            }

            if(stack.getItem() == ModItems.COW_TRAVELERS_BACKPACK.get())
            {
                cowAbility(player);
            }

            if(stack.getItem() == ModItems.CHICKEN_TRAVELERS_BACKPACK.get())
            {
                chickenAbility(player, false);
            }

            if(stack.getItem() == ModItems.SQUID_TRAVELERS_BACKPACK.get())
            {
                squidAbility(player);
            }
        }
        else //TILE ABILITIES
        {
            Item item = blockEntity.getItemStack().getItem();

            if(item == ModItems.CACTUS_TRAVELERS_BACKPACK.get())
            {
                cactusAbility(null, blockEntity);
            }
        }
    }

    public void abilityRemoval(@Nullable ItemStack stack, @Nullable Player player)
    {
        if(stack.getItem() == ModItems.NETHERITE_TRAVELERS_BACKPACK.get())
        {
            attributeAbility(player, true, Attributes.ARMOR, NETHERITE_ARMOR_MODIFIER);
        }

        if(stack.getItem() == ModItems.DIAMOND_TRAVELERS_BACKPACK.get())
        {
            attributeAbility(player, true, Attributes.ARMOR, DIAMOND_ARMOR_MODIFIER);
        }

        if(stack.getItem() == ModItems.IRON_TRAVELERS_BACKPACK.get())
        {
            attributeAbility(player, true, Attributes.ARMOR, IRON_ARMOR_MODIFIER);
        }

        if(stack.getItem() == ModItems.GOLD_TRAVELERS_BACKPACK.get())
        {
            attributeAbility(player, true, Attributes.ARMOR, GOLD_ARMOR_MODIFIER);
        }

        if(stack.getItem() == ModItems.ENDERMAN_TRAVELERS_BACKPACK.get())
        {
            attributeAbility(player, true, ForgeMod.REACH_DISTANCE.get(), ENDERMAN_REACH_DISTANCE_MODIFIER);
        }
    }

    /**
     * Called in TravelersBackpackBlock#animateTick method to enable visual only abilities for TravelersBackpackTileEntity
     */

    public void animateTick(TravelersBackpackBlockEntity blockEntity, BlockState stateIn, Level level, BlockPos pos, RandomSource rand)
    {
        if(blockEntity != null && blockEntity.getAbilityValue())
        {
            Block block = stateIn.getBlock();

            if(block == ModBlocks.EMERALD_TRAVELERS_BACKPACK.get())
            {
                emeraldAbility(null, blockEntity);
            }

            if(block == ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK.get())
            {
                bookshelfAbility(null, blockEntity);
            }

            if(block == ModBlocks.SPONGE_TRAVELERS_BACKPACK.get())
            {
                spongeAbility(blockEntity);
            }
        }
    }

    public void emeraldAbility(@Nullable Player player, @Nullable TravelersBackpackBlockEntity blockEntity)
    {
        Level level = player == null ? blockEntity.getLevel() : player.level;

        if(player == null || level.random.nextInt(10) == 1)
        {
            float f = level.random.nextFloat() * (float) Math.PI * 2.0F;
            float f1 = level.random.nextFloat() * 0.5F + 0.5F;
            float f2 = Mth.sin(f) * 0.5F * f1;
            float f3 = Mth.cos(f) * 0.5F * f1;
            level.addParticle(ParticleTypes.HAPPY_VILLAGER,
                    player == null ? blockEntity.getBlockPos().getX() + f2 + 0.5F : player.position().x + f2,
                    player == null ? blockEntity.getBlockPos().getY() + level.random.nextFloat() : player.getBoundingBox().minY + level.random.nextFloat() + 0.5F,
                    player == null ? blockEntity.getBlockPos().getZ() + f3 + 0.5F : player.position().z + f3, (double)(float)Math.pow(2.0D, (level.random.nextInt(169) - 12) / 12.0D) / 24.0D, -1.0D, 0.0D);
        }
    }

    public final AttributeModifier NETHERITE_ARMOR_MODIFIER = new AttributeModifier(UUID.fromString("49d951a4-ca9c-48b5-b549-61ef67ee53aa"), "NetheriteBackpackBonusArmor", 4.0D, AttributeModifier.Operation.ADDITION);
    public final AttributeModifier DIAMOND_ARMOR_MODIFIER = new AttributeModifier(UUID.fromString("294425c4-8dc6-4640-a336-d9fd72950e20"), "DiamondBackpackBonusArmor", 3.0D, AttributeModifier.Operation.ADDITION);
    public final AttributeModifier IRON_ARMOR_MODIFIER = new AttributeModifier(UUID.fromString("fcf6706b-dfd9-40d6-aa25-62c4fb7a83fa"), "IronBackpackBonusArmor", 2.0D, AttributeModifier.Operation.ADDITION);
    public final AttributeModifier GOLD_ARMOR_MODIFIER = new AttributeModifier(UUID.fromString("21060f97-da7a-4460-a4e4-c94fae72ab00"), "GoldBackpackBonusArmor", 2.0D, AttributeModifier.Operation.ADDITION);
    public final AttributeModifier ENDERMAN_REACH_DISTANCE_MODIFIER = new AttributeModifier(UUID.fromString("a3d7a647-1ed9-4317-94c2-ca889cd33657"), "EndermanReachDistanceBonus", 1.0D, AttributeModifier.Operation.ADDITION);


    public void attributeAbility(Player player, boolean isRemoval, Attribute attribute, AttributeModifier modifier)
    {
        AttributeInstance armor = player.getAttribute(attribute);

        if(isRemoval && armor != null && armor.hasModifier(modifier))
        {
            armor.removePermanentModifier(modifier.getId());
        }

        if(!isRemoval && armor != null && !armor.hasModifier(modifier))
        {
            armor.addPermanentModifier(modifier);
        }
    }

    public void armorAbilityRemovals(Player player)
    {
        attributeAbility(player, true, Attributes.ARMOR, NETHERITE_ARMOR_MODIFIER);
        attributeAbility(player, true, Attributes.ARMOR, DIAMOND_ARMOR_MODIFIER);
        attributeAbility(player, true, Attributes.ARMOR, IRON_ARMOR_MODIFIER);
        attributeAbility(player, true, Attributes.ARMOR, GOLD_ARMOR_MODIFIER);

        attributeAbility(player, true, ForgeMod.REACH_DISTANCE.get(), ENDERMAN_REACH_DISTANCE_MODIFIER);
    }

    public void lapisAbility(Player player)
    {
        if(ABILITIES.checkBackpack(player, ModItems.LAPIS_TRAVELERS_BACKPACK.get()))
        {
            int number = player.getRandom().nextIntBetweenInclusive(0, 1);
            player.giveExperiencePoints(number);
            sendParticlesPacket(ParticleTypes.GLOW, player, number);
        }
    }

    public void bookshelfAbility(@Nullable Player player, @Nullable TravelersBackpackBlockEntity blockEntity)
    {
        BlockPos enchanting = BackpackUtils.findBlock3D(blockEntity.getLevel(), blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ(), Blocks.ENCHANTING_TABLE, 2, 2);

        if(enchanting != null)
        {
            if(!blockEntity.getLevel().isEmptyBlock(new BlockPos((enchanting.getX() - blockEntity.getBlockPos().getX()) / 2 + blockEntity.getBlockPos().getX(), enchanting.getY(), (enchanting.getZ() - blockEntity.getBlockPos().getZ()) / 2 + blockEntity.getBlockPos().getZ())))
            {
                return;
            }

            for(int o = 0; o < 4; o++)
            {
                blockEntity.getLevel().addParticle(ParticleTypes.ENCHANT, enchanting.getX() + 0.5D, enchanting.getY() + 2.0D, enchanting.getZ() + 0.5D,
                        ((blockEntity.getBlockPos().getX() - enchanting.getX()) + blockEntity.getLevel().random.nextFloat()) - 0.5D,
                        ((blockEntity.getBlockPos().getY() - enchanting.getY()) - blockEntity.getLevel().random.nextFloat() - 1.0F),
                        ((blockEntity.getBlockPos().getZ() - enchanting.getZ()) + blockEntity.getLevel().random.nextFloat()) - 0.5D);
            }
        }
    }

    public void spongeAbility(TravelersBackpackBlockEntity blockEntity)
    {
        if(!blockEntity.getLeftTank().isEmpty() && !blockEntity.getRightTank().isEmpty())
        {
            if(blockEntity.getLeftTank().getFluid().getFluid().isSame(Fluids.WATER) && blockEntity.getRightTank().getFluid().getFluid().isSame(Fluids.WATER))
            {
                if(blockEntity.getLeftTank().getFluidAmount() == blockEntity.getLeftTank().getCapacity() && blockEntity.getRightTank().getFluidAmount() == blockEntity.getRightTank().getCapacity())
                {
                    float f = blockEntity.getLevel().random.nextFloat() * (float) Math.PI * 2.0F;
                    float f1 = blockEntity.getLevel().random.nextFloat() * 0.5F + 0.5F;
                    float f2 = Mth.sin(f) * 0.5F * f1;
                    float f3 = Mth.cos(f) * 0.5F * f1;
                    blockEntity.getLevel().addParticle(ParticleTypes.SPLASH,
                            blockEntity.getBlockPos().getX() + f2 + 0.5F,
                            blockEntity.getBlockPos().getY() + blockEntity.getLevel().random.nextFloat(),
                            blockEntity.getBlockPos().getZ() + f3 + 0.5F, (double)(float)Math.pow(2.0D, (blockEntity.getLevel().random.nextInt(169) - 12) / 12.0D) / 24.0D, -1.0D, 0.0D);
                }
            }
        }
    }

    public void cakeAbility(Player player)
    {
        TravelersBackpackContainer container = CapabilityUtils.getBackpackInv(player);

        if(container.getLastTime() <= 0)
        {
            player.getFoodData().eat(2, 0.1F);
            player.level.playSound(null, player.blockPosition(), SoundEvents.CAKE_ADD_CANDLE, SoundSource.AMBIENT, 1.0F, (player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.3F + 1.0F);

            if(!player.level.isClientSide)
            {
                if(player.level instanceof ServerLevel server)
                {
                    for(int i = 0; i < 3; i++)
                    {
                        float f = server.random.nextFloat() * (float) Math.PI * 2.0F;
                        float f1 = server.random.nextFloat() * 0.5F + 0.5F;
                        float f2 = Mth.sin(f) * 0.5F * f1;
                        float f3 = Mth.cos(f) * 0.5F * f1;
                        server.sendParticles(ParticleTypes.HEART,
                                player.position().x + f2,
                                player.getBoundingBox().minY + player.level.random.nextFloat() + 0.5F,
                                player.position().z + f3, 3, (double)(float)Math.pow(2.0D, (player.level.random.nextInt(169) - 12) / 12.0D) / 24.0D, -1.0D, 0.0D, 0);
                    }
                }

                container.setLastTime(TimeUtils.randomTime(player.level.random, 360, 360 + player.getFoodData().getFoodLevel() * 12));
                container.setDataChanged(ITravelersBackpackContainer.LAST_TIME_DATA);
            }
        }
    }

    public void chickenAbility(Player player, boolean firstSwitch)
    {
        TravelersBackpackContainer container = CapabilityUtils.getBackpackInv(player);

        if(firstSwitch)
        {
            if(container.getLastTime() <= 0)
            {
                if(!player.level.isClientSide)
                {
                    container.setLastTime(TimeUtils.randomTime(player.level.random, 360, 600));
                    container.setDataChanged(ITravelersBackpackContainer.LAST_TIME_DATA);
                    return;
                }
            }
        }

        if(container.getLastTime() <= 0)
        {
            player.level.playSound(null, player.blockPosition(), SoundEvents.CHICKEN_EGG, SoundSource.AMBIENT, 1.0F, (player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.3F + 1.0F);
            player.spawnAtLocation(Items.EGG);

            if(!player.level.isClientSide)
            {
                container.setLastTime(TimeUtils.randomTime(player.level.random, 360, 600));
                container.setDataChanged(ITravelersBackpackContainer.LAST_TIME_DATA);
            }
        }
    }

    public void cactusAbility(@Nullable Player player, @Nullable TravelersBackpackBlockEntity blockEntity)
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

                    if(leftTank.isEmpty() || leftTank.getFluid().isFluidEqual(water))
                    {
                        leftTank.fill(water, IFluidHandler.FluidAction.EXECUTE);
                    }

                    if(rightTank.isEmpty() || rightTank.getFluid().isFluidEqual(water))
                    {
                        rightTank.fill(water, IFluidHandler.FluidAction.EXECUTE);
                    }

                    blockEntity.setDataChanged(ITravelersBackpackContainer.TANKS_DATA);
                }
            }
        }
        else if(player != null && blockEntity == null)
        {
            TravelersBackpackContainer container = CapabilityUtils.getBackpackInv(player);

            FluidTank leftTank = container.getLeftTank();
            FluidTank rightTank = container.getRightTank();

            int drops = 0;

            if(player.isInWater())
            {
                drops += 2;
            }

            if(isUnderRain(player.blockPosition(), player.level))
            {
                drops += 1;
            }

            FluidStack water = new FluidStack(Fluids.WATER, drops);

            if(!container.getLevel().isClientSide)
            {
                if(container.getLastTime() <= 0 && drops > 0)
                {
                    container.setLastTime(5);

                    if(leftTank.isEmpty() || leftTank.getFluid().isFluidEqual(water))
                    {
                        leftTank.fill(water, IFluidHandler.FluidAction.EXECUTE);
                    }

                    if(rightTank.isEmpty() || rightTank.getFluid().isFluidEqual(water))
                    {
                        rightTank.fill(water, IFluidHandler.FluidAction.EXECUTE);
                    }

                    container.setDataChanged(ITravelersBackpackContainer.TANKS_DATA);
                }
            }
        }
    }

    public static void melonAbility(TravelersBackpackBlockEntity blockEntity)
    {
        if(blockEntity.getAbilityValue() && blockEntity.getLastTime() <= 0)
        {
            Block.popResource(blockEntity.getLevel(), blockEntity.getBlockPos(), new ItemStack(Items.MELON_SLICE, blockEntity.getLevel().random.nextInt(0, 3)));
            blockEntity.setLastTime(TimeUtils.randomTime(blockEntity.getLevel().random, 120, 480));
            blockEntity.setDataChanged();
        }
    }

    public static void pumpkinAbility(EnderManAngerEvent event)
    {
        if(ABILITIES.checkBackpack(event.getPlayer(), ModItems.PUMPKIN_TRAVELERS_BACKPACK.get()))
        {
            event.setCanceled(true);
        }
    }

    public static boolean creeperAbility(LivingDeathEvent event)
    {
        if(event.getEntity() instanceof Player player)
        {
            TravelersBackpackContainer container = CapabilityUtils.getBackpackInv(player);

            if(player.isDeadOrDying() && container != null && container.getItemStack().getItem() == ModItems.CREEPER_TRAVELERS_BACKPACK.get() && container.getAbilityValue() && container.getLastTime() <= 0)
            {
                player.setHealth(1.0F);
                player.removeAllEffects();
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 450, 1));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 400, 0));
                player.level.explode(player, player.m_269291_().m_269075_(player), null, player.getRandomX(0.5F), player.getY(), player.getRandomZ(0.5F), 3.0F, false, Level.ExplosionInteraction.NONE);
                player.level.playSound(null, player.blockPosition(), SoundEvents.CREEPER_PRIMED, SoundSource.AMBIENT, 1.2F, 0.5F);

                if(!player.level.isClientSide)
                {
                    container.setLastTime(TimeUtils.randomTime(player.level.random, 600, 900));
                    container.setDataChanged(ITravelersBackpackContainer.LAST_TIME_DATA);
                }
                event.setCanceled(true);
                return true;
            }
        }
        return false;
    }

    public void dragonAbility(Player player)
    {
        magmaCubeAbility(player);
        squidAbility(player);

        addTimedMobEffect(player, MobEffects.REGENERATION, 0, 210, 0, false, false, true);
        addTimedMobEffect(player, MobEffects.DAMAGE_BOOST, 210, 240, 0, false, false, true);
    }

    public void blazeAbility(Player player)
    {
        if(player.fallDistance >= 3.0F)
        {
            for(int i = 0; i < 4; ++i)
            {
                player.level.addParticle(ParticleTypes.LARGE_SMOKE, player.getRandomX(0.5D), player.getRandomY(), player.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
            }

            player.fallDistance = 0.0F;
        }
    }

    public static void blazeAbility(ProjectileImpactEvent event)
    {
        if(event.getProjectile() instanceof SmallFireball fireball && event.getRayTraceResult().getType() == HitResult.Type.ENTITY)
        {
            EntityHitResult result = (EntityHitResult)event.getRayTraceResult();

            if(result.getEntity() instanceof Player player && ABILITIES.checkBackpack(player, ModItems.BLAZE_TRAVELERS_BACKPACK.get()))
            {
                player.level.playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 0.8F + player.level.random.nextFloat() * 0.4F);
                sendParticlesPacket(ParticleTypes.FLAME, player, 3);

                fireball.discard();
                event.setCanceled(true);
            }
        }
    }

    public static void ghastAbility(LivingChangeTargetEvent event)
    {
        if(event.getEntity() instanceof Ghast ghast && event.getNewTarget() instanceof Player player)
        {
            if(ABILITIES.checkBackpack(player, ModItems.GHAST_TRAVELERS_BACKPACK.get()))
            {
                if(ghast.getLastHurtByMob() != player)
                {
                    event.setCanceled(true);
                }
            }
        }
    }

    public void magmaCubeAbility(Player player)
    {
        addTimedMobEffect(player, MobEffects.FIRE_RESISTANCE, 210, 240, 0, false, false, true);
    }

    public void spiderAbility(Player player)
    {
        if(player.horizontalCollision && !player.isInFluidType())
        {
            if(!player.isOnGround() && player.isCrouching())
            {
                player.setDeltaMovement(player.getDeltaMovement().x, 0.0D, player.getDeltaMovement().z);
            }
            else
            {
                player.setDeltaMovement(player.getDeltaMovement().x, 0.20D, player.getDeltaMovement().z);

                Level level = player.level;
                BlockState state = level.getBlockState(player.blockPosition().relative(player.getDirection()));
                player.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state).setPos(player.blockPosition()),
                        player.getX() + (level.random.nextDouble() - 0.5D) * (double)player.getDimensions(Pose.STANDING).width,
                        player.getY() + 0.1D,
                        player.getZ() + (level.random.nextDouble() - 0.5D) * (double)player.getDimensions(Pose.STANDING).width,
                        0.0D, 1.5D, 0.0D);
            }
        }
    }

    public void witherAbility(Player player)
    {
        if(player.getEffect(MobEffects.WITHER) != null)
        {
            player.removeEffect(MobEffects.WITHER);
        }
    }

    public void batAbility(Player player)
    {
        addTimedMobEffect(player, MobEffects.NIGHT_VISION, 210, 240, 0, false, false, true);
    }

    public static void beeAbility(AttackEntityEvent event)
    {
        if(ABILITIES.checkBackpack(event.getEntity(), ModItems.BEE_TRAVELERS_BACKPACK.get()))
        {
            boolean flag = event.getTarget().hurt(event.getEntity().m_269291_().m_269396_(event.getEntity()), 1.0F);

            if(flag)
            {
                event.getEntity().doEnchantDamageEffects(event.getEntity(), event.getTarget());

                if(event.getTarget() instanceof LivingEntity living)
                {
                    living.setStingerCount(living.getStingerCount() + 1);
                    living.addEffect(new MobEffectInstance(MobEffects.POISON, 4 * 20, 0), event.getEntity());
                }
            }
        }
    }

    private final TargetingConditions ocelotAbilityTargeting = TargetingConditions.forCombat().range(64.0D); //#TODO check

    public void ocelotAbility(Player player)
    {
        if(player.level.getNearestEntity(Mob.class, ocelotAbilityTargeting, player, player.getX(), player.getY(), player.getZ(), player.getBoundingBox().inflate(6.0D, 2.0D, 6.0D)) != null)
        {
            addTimedMobEffect(player, MobEffects.MOVEMENT_SPEED, 20, 30, 0, false, false, true);
        }
    }

    public void cowAbility(Player player)
    {
        if(!player.getActiveEffects().isEmpty() && CapabilityUtils.getBackpackInv(player).getLastTime() <= 0)
        {
            player.curePotionEffects(new ItemStack(Items.MILK_BUCKET));

            if(!player.level.isClientSide)
            {
                player.level.levelEvent(2007, player.blockPosition(), 16777215);
            }
            player.level.playSound(null, player.blockPosition(), SoundEvents.HONEYCOMB_WAX_ON, SoundSource.PLAYERS, 1.0F, player.getRandom().nextFloat() * 0.1F + 0.9F);

            CapabilityUtils.getBackpackInv(player).setLastTime(TimeUtils.randomTime(player.level.random, 450, 600));
            CapabilityUtils.getBackpackInv(player).setDataChanged(ITravelersBackpackContainer.LAST_TIME_DATA);
        }
    }

    public void squidAbility(Player player)
    {
        if(player.isInWater())
        {
            addTimedMobEffect(player, MobEffects.WATER_BREATHING, 210, 240, 0, false, false, true);
            batAbility(player);
        }
    }

    //Utility methods

    private boolean isUnderRain(BlockPos pos, Level level)
    {
        return level.canSeeSky(pos) && level.isRaining();
    }

    public boolean checkBackpack(Player player, Item item)
    {
        return CapabilityUtils.isWearingBackpack(player) && CapabilityUtils.getBackpackInv(player).getItemStack().getItem() == item && CapabilityUtils.getBackpackInv(player).getAbilityValue();
    }

    public void addTimedMobEffect(Player player, MobEffect effect, int minDuration, int maxDuration, int amplifier, boolean ambient, boolean showParticle, boolean showIcon)
    {
        if(!player.hasEffect(effect))
        {
            player.addEffect(new MobEffectInstance(effect, maxDuration, amplifier, ambient, showParticle, showIcon));
        }

        else if(player.hasEffect(effect))
        {
            if(player.getEffect(effect).getDuration() <= minDuration)
            {
                player.addEffect(new MobEffectInstance(effect, maxDuration, amplifier, ambient, showParticle, showIcon));
            }
        }
    }

    public static void sendParticlesPacket(ParticleOptions type, Player player, int count)
    {
        for(int i = 0; i < count; i++)
        {
            double d0 = player.level.random.nextGaussian() * 0.02D;
            double d1 = player.level.random.nextGaussian() * 0.02D;
            double d2 = player.level.random.nextGaussian() * 0.02D;

            if(player.level instanceof ServerLevel server)
            {
                server.sendParticles(type, player.getRandomX(1.0D), player.getRandomY() + 0.5D, player.getRandomZ(1.0D), 1, d0, d1, d2, 0.0F);
            }
        }
    }

    public static boolean isOnList(Item[] list, ItemStack stackToCheck)
    {
        return Arrays.stream(list).anyMatch(s -> s == stackToCheck.getItem());
    }

    public static final Item[] ALL_ABILITIES_LIST = {

            ModItems.NETHERITE_TRAVELERS_BACKPACK.get(),
            ModItems.DIAMOND_TRAVELERS_BACKPACK.get(),
            ModItems.GOLD_TRAVELERS_BACKPACK.get(),
            ModItems.EMERALD_TRAVELERS_BACKPACK.get(), //#TODO niy
            ModItems.IRON_TRAVELERS_BACKPACK.get(),
            ModItems.LAPIS_TRAVELERS_BACKPACK.get(),
            ModItems.REDSTONE_TRAVELERS_BACKPACK.get(),

            ModItems.BOOKSHELF_TRAVELERS_BACKPACK.get(),
            //ModItems.END_TRAVELERS_BACKPACK.get(),
            //ModItems.NETHER_TRAVELERS_BACKPACK.get(),
            //ModItems.SANDSTONE_TRAVELERS_BACKPACK.get(),
            //ModItems.SNOW_TRAVELERS_BACKPACK.get(),
            ModItems.SPONGE_TRAVELERS_BACKPACK.get(),

            ModItems.CAKE_TRAVELERS_BACKPACK.get(),

            ModItems.CACTUS_TRAVELERS_BACKPACK.get(),
           // ModItems.HAY_TRAVELERS_BACKPACK.get(),
            ModItems.MELON_TRAVELERS_BACKPACK.get(),
            ModItems.PUMPKIN_TRAVELERS_BACKPACK.get(),

            ModItems.CREEPER_TRAVELERS_BACKPACK.get(),
            ModItems.DRAGON_TRAVELERS_BACKPACK.get(),
            ModItems.ENDERMAN_TRAVELERS_BACKPACK.get(),
            ModItems.BLAZE_TRAVELERS_BACKPACK.get(),
            ModItems.GHAST_TRAVELERS_BACKPACK.get(),
            ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK.get(),
            //ModItems.SKELETON_TRAVELERS_BACKPACK.get(),
            ModItems.SPIDER_TRAVELERS_BACKPACK.get(),
            ModItems.WITHER_TRAVELERS_BACKPACK.get(),

            ModItems.BAT_TRAVELERS_BACKPACK.get(),
            ModItems.BEE_TRAVELERS_BACKPACK.get(),
           // ModItems.WOLF_TRAVELERS_BACKPACK.get(),
           // ModItems.FOX_TRAVELERS_BACKPACK.get(),
            ModItems.OCELOT_TRAVELERS_BACKPACK.get(),
           // ModItems.HORSE_TRAVELERS_BACKPACK.get(),
            ModItems.COW_TRAVELERS_BACKPACK.get(),
          //  ModItems.PIG_TRAVELERS_BACKPACK.get(),
          //  ModItems.SHEEP_TRAVELERS_BACKPACK.get(),
            ModItems.CHICKEN_TRAVELERS_BACKPACK.get(),
            ModItems.SQUID_TRAVELERS_BACKPACK.get(),
          //  ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get()
        };

    public static final Item[] ITEM_ABILITIES_LIST = {

            ModItems.NETHERITE_TRAVELERS_BACKPACK.get(),
            ModItems.DIAMOND_TRAVELERS_BACKPACK.get(),
            ModItems.GOLD_TRAVELERS_BACKPACK.get(),
            ModItems.EMERALD_TRAVELERS_BACKPACK.get(),
            ModItems.IRON_TRAVELERS_BACKPACK.get(),
            ModItems.LAPIS_TRAVELERS_BACKPACK.get(),

            //ModItems.BOOKSHELF_TRAVELERS_BACKPACK.get(),

            //ModItems.END_TRAVELERS_BACKPACK.get(),
            //ModItems.NETHER_TRAVELERS_BACKPACK.get(),
            //ModItems.SANDSTONE_TRAVELERS_BACKPACK.get(),
            //ModItems.SNOW_TRAVELERS_BACKPACK.get(),

            ModItems.CAKE_TRAVELERS_BACKPACK.get(),

            ModItems.CACTUS_TRAVELERS_BACKPACK.get(),
            //ModItems.HAY_TRAVELERS_BACKPACK.get(),
            //ModItems.MELON_TRAVELERS_BACKPACK.get(),
            ModItems.PUMPKIN_TRAVELERS_BACKPACK.get(),

            ModItems.CREEPER_TRAVELERS_BACKPACK.get(),
            ModItems.DRAGON_TRAVELERS_BACKPACK.get(),
            ModItems.ENDERMAN_TRAVELERS_BACKPACK.get(),
            ModItems.BLAZE_TRAVELERS_BACKPACK.get(),
            ModItems.GHAST_TRAVELERS_BACKPACK.get(),
            ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK.get(),
            //ModItems.SKELETON_TRAVELERS_BACKPACK.get(),
            ModItems.SPIDER_TRAVELERS_BACKPACK.get(),
            ModItems.WITHER_TRAVELERS_BACKPACK.get(),

            ModItems.BAT_TRAVELERS_BACKPACK.get(),
            ModItems.BEE_TRAVELERS_BACKPACK.get(),
           // ModItems.WOLF_TRAVELERS_BACKPACK.get(),
            //ModItems.FOX_TRAVELERS_BACKPACK.get(),
            ModItems.OCELOT_TRAVELERS_BACKPACK.get(),
            //ModItems.HORSE_TRAVELERS_BACKPACK.get(),
            ModItems.COW_TRAVELERS_BACKPACK.get(),
            //ModItems.PIG_TRAVELERS_BACKPACK.get(),
            //ModItems.SHEEP_TRAVELERS_BACKPACK.get(),
            ModItems.CHICKEN_TRAVELERS_BACKPACK.get(),
            ModItems.SQUID_TRAVELERS_BACKPACK.get(),
            //ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get()
    };

    public static final Item[] ITEM_ABILITIES_REMOVAL_LIST = {

            ModItems.NETHERITE_TRAVELERS_BACKPACK.get(),
            ModItems.DIAMOND_TRAVELERS_BACKPACK.get(),
            ModItems.GOLD_TRAVELERS_BACKPACK.get(),
            //ModItems.EMERALD_TRAVELERS_BACKPACK.get(),
            ModItems.IRON_TRAVELERS_BACKPACK.get(),

            //ModItems.END_TRAVELERS_BACKPACK.get(),
            //ModItems.NETHER_TRAVELERS_BACKPACK.get(),
            //ModItems.SANDSTONE_TRAVELERS_BACKPACK.get(),
            //ModItems.SNOW_TRAVELERS_BACKPACK.get(),

            //ModItems.CAKE_TRAVELERS_BACKPACK.get(),

            //ModItems.CACTUS_TRAVELERS_BACKPACK.get(),
            //ModItems.HAY_TRAVELERS_BACKPACK.get(),
            //ModItems.MELON_TRAVELERS_BACKPACK.get(),
            //ModItems.PUMPKIN_TRAVELERS_BACKPACK.get(),

            //ModItems.CREEPER_TRAVELERS_BACKPACK.get(),
            //ModItems.DRAGON_TRAVELERS_BACKPACK.get(),
            ModItems.ENDERMAN_TRAVELERS_BACKPACK.get(),
            //ModItems.BLAZE_TRAVELERS_BACKPACK.get(),
            //ModItems.GHAST_TRAVELERS_BACKPACK.get(),
            //ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK.get(),
            //ModItems.SKELETON_TRAVELERS_BACKPACK.get(),
            //ModItems.SPIDER_TRAVELERS_BACKPACK.get(),
            //ModItems.WITHER_TRAVELERS_BACKPACK.get(),

            //ModItems.BAT_TRAVELERS_BACKPACK.get(),
            // ModItems.BEE_TRAVELERS_BACKPACK.get(),
            // ModItems.WOLF_TRAVELERS_BACKPACK.get(),
            //ModItems.FOX_TRAVELERS_BACKPACK.get(),
            //ModItems.OCELOT_TRAVELERS_BACKPACK.get(),
            //ModItems.HORSE_TRAVELERS_BACKPACK.get(),
            //ModItems.COW_TRAVELERS_BACKPACK.get(),
            //ModItems.PIG_TRAVELERS_BACKPACK.get(),
            //ModItems.SHEEP_TRAVELERS_BACKPACK.get(),
            // ModItems.CHICKEN_TRAVELERS_BACKPACK.get(),
            // ModItems.SQUID_TRAVELERS_BACKPACK.get(),
            //ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get()
    };

    public static final Item[] ITEM_TIMER_ABILITIES_LIST = {
            ModItems.CAKE_TRAVELERS_BACKPACK.get(),

            ModItems.CREEPER_TRAVELERS_BACKPACK.get(),

            ModItems.COW_TRAVELERS_BACKPACK.get(),
            ModItems.CHICKEN_TRAVELERS_BACKPACK.get()
    };

    public static final Item[] BLOCK_TIMER_ABILITIES_LIST = {
            ModItems.MELON_TRAVELERS_BACKPACK.get()
    };

    public static final Item[] BLOCK_ABILITIES_LIST = {

            ModItems.EMERALD_TRAVELERS_BACKPACK.get(),
            //ModItems.LAPIS_TRAVELERS_BACKPACK.get(),
            ModItems.REDSTONE_TRAVELERS_BACKPACK.get(),

            ModItems.BOOKSHELF_TRAVELERS_BACKPACK.get(),
            //ModItems.END_TRAVELERS_BACKPACK.get(),
            //ModItems.NETHER_TRAVELERS_BACKPACK.get(),
            //ModItems.SANDSTONE_TRAVELERS_BACKPACK.get(),
            //ModItems.SNOW_TRAVELERS_BACKPACK.get(),
            ModItems.SPONGE_TRAVELERS_BACKPACK.get(),

            //ModItems.CAKE_TRAVELERS_BACKPACK.get(),

            ModItems.CACTUS_TRAVELERS_BACKPACK.get(),
            //ModItems.HAY_TRAVELERS_BACKPACK.get(),
            ModItems.MELON_TRAVELERS_BACKPACK.get(),
           // ModItems.PUMPKIN_TRAVELERS_BACKPACK.get(),

           // ModItems.ENDERMAN_TRAVELERS_BACKPACK.get(),
           // ModItems.GHAST_TRAVELERS_BACKPACK.get(),
           // ModItems.SKELETON_TRAVELERS_BACKPACK.get(),
           // ModItems.SPIDER_TRAVELERS_BACKPACK.get(),

           // ModItems.BEE_TRAVELERS_BACKPACK.get(),
          //  ModItems.WOLF_TRAVELERS_BACKPACK.get(),
          //  ModItems.FOX_TRAVELERS_BACKPACK.get(),
          //  ModItems.HORSE_TRAVELERS_BACKPACK.get(),
          //  ModItems.COW_TRAVELERS_BACKPACK.get(),
           // ModItems.PIG_TRAVELERS_BACKPACK.get(),
           // ModItems.SHEEP_TRAVELERS_BACKPACK.get(),
           // ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get()
    };
}