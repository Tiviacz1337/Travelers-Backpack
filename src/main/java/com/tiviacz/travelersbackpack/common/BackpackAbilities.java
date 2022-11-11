package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackTileEntity;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.TimeUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Random;
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
     * {@link TravelersBackpackTileEntity#tick()}
     *
     * //Ability removals
     * {@link ServerActions#switchAbilitySlider(PlayerEntity, boolean)}
     * {@link ServerActions#switchAbilitySliderTileEntity(PlayerEntity, BlockPos, boolean)}
     *
     * //Cosmetic only
     * {@link com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock#animateTick(BlockState, World, BlockPos, Random)}
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
    public void abilityTick(@Nullable ItemStack stack, @Nullable PlayerEntity player, @Nullable TravelersBackpackTileEntity tile)
    {
        if(tile == null) //WEARABLE ABILITIES
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
            Item item = tile.getItemStack().getItem();

            if(item == ModItems.CACTUS_TRAVELERS_BACKPACK.get())
            {
                cactusAbility(null, tile);
            }
        }
    }

    public void abilityRemoval(@Nullable ItemStack stack, @Nullable PlayerEntity player)
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

    public void animateTick(TravelersBackpackTileEntity tile, BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if(tile != null && tile.getAbilityValue())
        {
            Block block = stateIn.getBlock();

            if(block == ModBlocks.EMERALD_TRAVELERS_BACKPACK.get())
            {
                emeraldAbility(null, tile);
            }

            if(block == ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK.get())
            {
                bookshelfAbility(null, tile);
            }

            if(block == ModBlocks.SPONGE_TRAVELERS_BACKPACK.get())
            {
                spongeAbility(tile);
            }
        }
    }

    public void emeraldAbility(@Nullable PlayerEntity player, @Nullable TravelersBackpackTileEntity tile)
    {
        World world = player == null ? tile.getLevel() : player.level;

        if(player == null || world.random.nextInt(10) == 1)
        {
            float f = world.random.nextFloat() * (float) Math.PI * 2.0F;
            float f1 = world.random.nextFloat() * 0.5F + 0.5F;
            float f2 = MathHelper.sin(f) * 0.5F * f1;
            float f3 = MathHelper.cos(f) * 0.5F * f1;
            world.addParticle(ParticleTypes.HAPPY_VILLAGER,
                    player == null ? tile.getBlockPos().getX() + f2 + 0.5F : player.position().x + f2,
                    player == null ? tile.getBlockPos().getY() + world.random.nextFloat() : player.getBoundingBox().minY + world.random.nextFloat() + 0.5F,
                    player == null ? tile.getBlockPos().getZ() + f3 + 0.5F : player.position().z + f3, (double)(float)Math.pow(2.0D, (world.random.nextInt(169) - 12) / 12.0D) / 24.0D, -1.0D, 0.0D);
        }
    }

    public final AttributeModifier NETHERITE_ARMOR_MODIFIER = new AttributeModifier(UUID.fromString("49d951a4-ca9c-48b5-b549-61ef67ee53aa"), "NetheriteBackpackBonusArmor", 4.0D, AttributeModifier.Operation.ADDITION);
    public final AttributeModifier DIAMOND_ARMOR_MODIFIER = new AttributeModifier(UUID.fromString("294425c4-8dc6-4640-a336-d9fd72950e20"), "DiamondBackpackBonusArmor", 3.0D, AttributeModifier.Operation.ADDITION);
    public final AttributeModifier IRON_ARMOR_MODIFIER = new AttributeModifier(UUID.fromString("fcf6706b-dfd9-40d6-aa25-62c4fb7a83fa"), "IronBackpackBonusArmor", 2.0D, AttributeModifier.Operation.ADDITION);
    public final AttributeModifier GOLD_ARMOR_MODIFIER = new AttributeModifier(UUID.fromString("21060f97-da7a-4460-a4e4-c94fae72ab00"), "GoldBackpackBonusArmor", 2.0D, AttributeModifier.Operation.ADDITION);
    public final AttributeModifier ENDERMAN_REACH_DISTANCE_MODIFIER = new AttributeModifier(UUID.fromString("a3d7a647-1ed9-4317-94c2-ca889cd33657"), "EndermanReachDistanceBonus", 1.0D, AttributeModifier.Operation.ADDITION);

    public void attributeAbility(PlayerEntity player, boolean isRemoval, Attribute attribute, AttributeModifier modifier)
    {
        ModifiableAttributeInstance armor = player.getAttribute(attribute);

        if(isRemoval && armor != null && armor.hasModifier(modifier))
        {
            armor.removePermanentModifier(modifier.getId());
        }

        if(!isRemoval && armor != null && !armor.hasModifier(modifier))
        {
            armor.addPermanentModifier(modifier);
        }
    }

    public void armorAbilityRemovals(PlayerEntity player)
    {
        attributeAbility(player, true, Attributes.ARMOR, NETHERITE_ARMOR_MODIFIER);
        attributeAbility(player, true, Attributes.ARMOR, DIAMOND_ARMOR_MODIFIER);
        attributeAbility(player, true, Attributes.ARMOR, IRON_ARMOR_MODIFIER);
        attributeAbility(player, true, Attributes.ARMOR, GOLD_ARMOR_MODIFIER);

        attributeAbility(player, true, ForgeMod.REACH_DISTANCE.get(), ENDERMAN_REACH_DISTANCE_MODIFIER);
    }

    public void bookshelfAbility(@Nullable PlayerEntity player, @Nullable TravelersBackpackTileEntity tile)
    {
        BlockPos enchanting = BackpackUtils.findBlock3D(tile.getLevel(), tile.getBlockPos().getX(), tile.getBlockPos().getY(), tile.getBlockPos().getZ(), Blocks.ENCHANTING_TABLE, 2, 2);

        if(enchanting != null)
        {
            if(!tile.getLevel().isEmptyBlock(new BlockPos((enchanting.getX() - tile.getBlockPos().getX()) / 2 + tile.getBlockPos().getX(), enchanting.getY(), (enchanting.getZ() - tile.getBlockPos().getZ()) / 2 + tile.getBlockPos().getZ())))
            {
                return;
            }

            for(int o = 0; o < 4; o++)
            {
                tile.getLevel().addParticle(ParticleTypes.ENCHANT, enchanting.getX() + 0.5D, enchanting.getY() + 2.0D, enchanting.getZ() + 0.5D,
                        ((tile.getBlockPos().getX() - enchanting.getX()) + tile.getLevel().random.nextFloat()) - 0.5D,
                        ((tile.getBlockPos().getY() - enchanting.getY()) - tile.getLevel().random.nextFloat() - 1.0F),
                        ((tile.getBlockPos().getZ() - enchanting.getZ()) + tile.getLevel().random.nextFloat()) - 0.5D);
            }
        }
    }

    public void spongeAbility(TravelersBackpackTileEntity tile)
    {
        if(!tile.getLeftTank().isEmpty() && !tile.getRightTank().isEmpty())
        {
            if(tile.getLeftTank().getFluid().getFluid().isSame(Fluids.WATER) && tile.getRightTank().getFluid().getFluid().isSame(Fluids.WATER))
            {
                if(tile.getLeftTank().getFluidAmount() == tile.getLeftTank().getCapacity() && tile.getRightTank().getFluidAmount() == tile.getRightTank().getCapacity())
                {
                    float f = tile.getLevel().random.nextFloat() * (float) Math.PI * 2.0F;
                    float f1 = tile.getLevel().random.nextFloat() * 0.5F + 0.5F;
                    float f2 = MathHelper.sin(f) * 0.5F * f1;
                    float f3 = MathHelper.cos(f) * 0.5F * f1;
                    tile.getLevel().addParticle(ParticleTypes.SPLASH,
                            tile.getBlockPos().getX() + f2 + 0.5F,
                            tile.getBlockPos().getY() + tile.getLevel().random.nextFloat(),
                            tile.getBlockPos().getZ() + f3 + 0.5F, (double)(float)Math.pow(2.0D, (tile.getLevel().random.nextInt(169) - 12) / 12.0D) / 24.0D, -1.0D, 0.0D);
                }
            }
        }
    }

    public void cakeAbility(PlayerEntity player)
    {
        TravelersBackpackInventory inventory = CapabilityUtils.getBackpackInv(player);

        if(inventory.getLastTime() <= 0)
        {
            player.getFoodData().eat(2, 0.1F);
            player.level.playSound(null, player.blockPosition(), SoundEvents.HONEY_BLOCK_PLACE, SoundCategory.AMBIENT, 1.0F, (player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.3F + 1.0F);

            if(!player.level.isClientSide)
            {
                if(player.level instanceof ServerWorld)
                {
                    for(int i = 0; i < 3; i++)
                    {
                        float f = player.level.random.nextFloat() * (float) Math.PI * 2.0F;
                        float f1 = player.level.random.nextFloat() * 0.5F + 0.5F;
                        float f2 = MathHelper.sin(f) * 0.5F * f1;
                        float f3 = MathHelper.cos(f) * 0.5F * f1;
                        ((ServerWorld)player.level).sendParticles(ParticleTypes.HEART,
                                player.position().x + f2,
                                player.getBoundingBox().minY + player.level.random.nextFloat() + 0.5F,
                                player.position().z + f3, 3, (double)(float)Math.pow(2.0D, (player.level.random.nextInt(169) - 12) / 12.0D) / 24.0D, -1.0D, 0.0D, 0);
                    }
                }

                inventory.setLastTime(TimeUtils.randomTime(player.level.random, 360, 360 + player.getFoodData().getFoodLevel() * 12));
                inventory.setDataChanged(ITravelersBackpackInventory.LAST_TIME_DATA);
            }
        }
    }

    public void chickenAbility(PlayerEntity player, boolean firstSwitch)
    {
        TravelersBackpackInventory inv = CapabilityUtils.getBackpackInv(player);

        if(firstSwitch)
        {
            if(inv.getLastTime() <= 0)
            {
                if(!inv.getLevel().isClientSide)
                {
                    inv.setLastTime(TimeUtils.randomTime(player.level.random, 360, 600));
                    inv.setDataChanged(ITravelersBackpackInventory.LAST_TIME_DATA);
                    return;
                }
            }
        }

        if(inv.getLastTime() <= 0)
        {
            player.level.playSound(null, player.blockPosition(), SoundEvents.CHICKEN_EGG, SoundCategory.AMBIENT, 1.0F, (player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.3F + 1.0F);
            player.spawnAtLocation(Items.EGG);

            if(!inv.getLevel().isClientSide)
            {
                inv.setLastTime(TimeUtils.randomTime(player.level.random, 360, 600));
                inv.setDataChanged(ITravelersBackpackInventory.LAST_TIME_DATA);
            }
        }
    }

    public void cactusAbility(@Nullable PlayerEntity player, @Nullable TravelersBackpackTileEntity tile)
    {
        if(player == null && tile != null)
        {
            FluidTank leftTank = tile.getLeftTank();
            FluidTank rightTank = tile.getRightTank();

            int drops = 0;

            if(isUnderRain(tile.getBlockPos(), tile.getLevel()))
            {
                drops += 1;
            }

            FluidStack water = new FluidStack(Fluids.WATER, drops);

            if(!tile.getLevel().isClientSide)
            {
                if(tile.getLastTime() <= 0 && drops > 0)
                {
                    tile.setLastTime(5);

                    if(leftTank.isEmpty() || leftTank.getFluid().isFluidEqual(water))
                    {
                        leftTank.fill(water, IFluidHandler.FluidAction.EXECUTE);
                    }

                    if(rightTank.isEmpty() || rightTank.getFluid().isFluidEqual(water))
                    {
                        rightTank.fill(water, IFluidHandler.FluidAction.EXECUTE);
                    }

                    tile.setDataChanged(ITravelersBackpackInventory.TANKS_DATA);
                }
            }
        }
        else if(player != null && tile == null)
        {
            TravelersBackpackInventory inv = CapabilityUtils.getBackpackInv(player);

            FluidTank leftTank = inv.getLeftTank();
            FluidTank rightTank = inv.getRightTank();

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

            if(!inv.getLevel().isClientSide)
            {
                if(inv.getLastTime() <= 0 && drops > 0)
                {
                    inv.setLastTime(5);

                    if(leftTank.isEmpty() || leftTank.getFluid().isFluidEqual(water))
                    {
                        leftTank.fill(water, IFluidHandler.FluidAction.EXECUTE);
                    }

                    if(rightTank.isEmpty() || rightTank.getFluid().isFluidEqual(water))
                    {
                        rightTank.fill(water, IFluidHandler.FluidAction.EXECUTE);
                    }

                    inv.setDataChanged(ITravelersBackpackInventory.TANKS_DATA);
                }
            }
        }
    }

    public static void melonAbility(TravelersBackpackTileEntity tile)
    {
        if(tile.getAbilityValue() && tile.getLastTime() <= 0)
        {
            Block.popResource(tile.getLevel(), tile.getBlockPos(), new ItemStack(Items.MELON_SLICE, tile.getLevel().random.nextInt( 3)));
            tile.setLastTime(TimeUtils.randomTime(tile.getLevel().random, 120, 480));
            tile.setChanged();
        }
    }

    public static void pumpkinAbility(LivingSetAttackTargetEvent event)
    {
        if(event.getTarget() instanceof PlayerEntity)
        {
            if(ABILITIES.checkBackpack((PlayerEntity)event.getTarget(), ModItems.PUMPKIN_TRAVELERS_BACKPACK.get()))
            {
                if(event.getEntity() instanceof EndermanEntity)
                {
                    EndermanEntity enderman = (EndermanEntity)event.getEntity();

                    if(enderman.getLastHurtByMob() != event.getTarget())
                    {
                        enderman.setTarget(null);
                    }
                }
            }
        }
    }

    public static boolean creeperAbility(LivingDeathEvent event)
    {
        if(event.getEntity() instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity)event.getEntity();

            TravelersBackpackInventory inv = CapabilityUtils.getBackpackInv(player);

            if(player.isDeadOrDying() && inv != null && inv.getItemStack().getItem() == ModItems.CREEPER_TRAVELERS_BACKPACK.get() && inv.getAbilityValue() && inv.getLastTime() <= 0)
            {
                player.setHealth(1.0F);
                player.removeAllEffects();
                player.addEffect(new EffectInstance(Effects.REGENERATION, 450, 1));
                player.addEffect(new EffectInstance(Effects.ABSORPTION, 100, 1));
                player.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 400, 0));
                player.level.explode(player, DamageSource.playerAttack(player), null, player.getRandomX(0.5F), player.getY(), player.getRandomZ(0.5F), 3.0F, false, Explosion.Mode.NONE);
                player.level.playSound(null, player.blockPosition(), SoundEvents.CREEPER_PRIMED, SoundCategory.AMBIENT, 1.2F, 0.5F);

                if(!inv.getLevel().isClientSide)
                {
                    inv.setLastTime(TimeUtils.randomTime(player.level.random, 600, 900));
                    inv.setDataChanged(ITravelersBackpackInventory.LAST_TIME_DATA);
                }
                event.setCanceled(true);
                return true;
            }
        }
        return false;
    }

    public void dragonAbility(PlayerEntity player)
    {
        magmaCubeAbility(player);
        squidAbility(player);

        addTimedEffect(player, Effects.REGENERATION, 0, 210, 0, false, false, true);
        addTimedEffect(player, Effects.DAMAGE_BOOST, 210, 240, 0, false, false, true);
    }

    public void blazeAbility(PlayerEntity player)
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

    public static void blazeAbility(ProjectileImpactEvent.Fireball event)
    {
        if(event.getFireball() instanceof SmallFireballEntity && event.getRayTraceResult().getType() == RayTraceResult.Type.ENTITY)
        {
            EntityRayTraceResult result = (EntityRayTraceResult)event.getRayTraceResult();

            if(result.getEntity() instanceof PlayerEntity && ABILITIES.checkBackpack((PlayerEntity)result.getEntity(), ModItems.BLAZE_TRAVELERS_BACKPACK.get()))
            {
                result.getEntity().level.playSound(null, result.getEntity().blockPosition(), SoundEvents.SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0F, 0.8F + result.getEntity().level.random.nextFloat() * 0.4F);
                sendParticlesPacket(ParticleTypes.FLAME, (PlayerEntity)result.getEntity(), 3);

                event.getFireball().remove();
                event.setCanceled(true);
            }
        }
    }

    public static void ghastAbility(LivingSetAttackTargetEvent event)
    {
        if(event.getEntity() instanceof GhastEntity && event.getTarget() instanceof PlayerEntity)
        {
            if(ABILITIES.checkBackpack((PlayerEntity)event.getTarget(), ModItems.GHAST_TRAVELERS_BACKPACK.get()))
            {
                if(((GhastEntity)event.getEntity()).getLastHurtByMob() != event.getTarget())
                {
                    ((GhastEntity)event.getEntity()).setTarget(null);
                }
            }
        }
    }

    public void magmaCubeAbility(PlayerEntity player)
    {
        addTimedEffect(player, Effects.FIRE_RESISTANCE, 210, 240, 0, false, false, true);
    }

    public void spiderAbility(PlayerEntity player)
    {
        if(player.horizontalCollision && !(player.getFeetBlockState().getBlock() instanceof FlowingFluidBlock))
        {
            if(!player.isOnGround() && player.isCrouching())
            {
                player.setDeltaMovement(player.getDeltaMovement().x, 0.0D, player.getDeltaMovement().z);
            }
            else
            {
                player.setDeltaMovement(player.getDeltaMovement().x, 0.20D, player.getDeltaMovement().z);

                World world = player.level;
                BlockState state = world.getBlockState(player.blockPosition().relative(player.getDirection()));
                world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, state).setPos(player.blockPosition()),
                        player.getX() + (world.random.nextDouble() - 0.5D) * (double)player.getDimensions(Pose.STANDING).width,
                        player.getY() + 0.1D,
                        player.getZ() + (world.random.nextDouble() - 0.5D) * (double)player.getDimensions(Pose.STANDING).width,
                        0.0D, 1.5D, 0.0D);
            }
        }
    }

    public void witherAbility(PlayerEntity player)
    {
        if(player.getEffect(Effects.WITHER) != null)
        {
            player.removeEffect(Effects.WITHER);
        }
    }

    public void batAbility(PlayerEntity player)
    {
        addTimedEffect(player, Effects.NIGHT_VISION, 210, 240, 0, false, false, true);
    }

    public static void beeAbility(AttackEntityEvent event)
    {
        if(ABILITIES.checkBackpack(event.getPlayer(), ModItems.BEE_TRAVELERS_BACKPACK.get()))
        {
            boolean flag = event.getTarget().hurt(DamageSource.sting(event.getPlayer()), 1.0F);

            if(flag)
            {
                event.getEntity().doEnchantDamageEffects(event.getPlayer(), event.getTarget());

                if(event.getTarget() instanceof LivingEntity)
                {
                    LivingEntity living = ((LivingEntity)event.getTarget());
                    living.setStingerCount(living.getStingerCount() + 1);
                    living.addEffect(new EffectInstance(Effects.POISON, 4 * 20, 0));
                }
            }
        }
    }

    private static final EntityPredicate OCELOT_ABILITY_PREDICATE = (new EntityPredicate()).range(6.0D);

    public void ocelotAbility(PlayerEntity player)
    {
        if(player.level.getNearestEntity(MobEntity.class, OCELOT_ABILITY_PREDICATE, player, player.getX(), player.getY(), player.getZ(), player.getBoundingBox().inflate(6.0D, 2.0D, 6.0D)) != null)
        {
            addTimedEffect(player, Effects.MOVEMENT_SPEED, 20, 30, 0, false, false, true);
        }
    }

    public void cowAbility(PlayerEntity player)
    {
        if(!player.getActiveEffects().isEmpty() && CapabilityUtils.getBackpackInv(player).getLastTime() <= 0)
        {
            player.curePotionEffects(new ItemStack(Items.MILK_BUCKET));

            if(!player.level.isClientSide)
            {
                player.level.levelEvent(2007, player.blockPosition(), 16777215);
            }
            player.level.playSound(null, player.blockPosition(), SoundEvents.HONEY_BLOCK_PLACE, SoundCategory.PLAYERS, 1.0F, player.getRandom().nextFloat() * 0.1F + 0.9F);

            CapabilityUtils.getBackpackInv(player).setLastTime(TimeUtils.randomTime(player.level.random, 450, 600));
            CapabilityUtils.getBackpackInv(player).setDataChanged(ITravelersBackpackInventory.LAST_TIME_DATA);
        }
    }

    public void squidAbility(PlayerEntity player)
    {
        if(player.isInWater())
        {
            addTimedEffect(player, Effects.WATER_BREATHING, 210, 240, 0, false, false, true);
            batAbility(player);
        }
    }

    //Utility methods

    private boolean isUnderRain(BlockPos pos, World world)
    {
        return world.canSeeSky(pos) && world.isRaining();
    }

    public boolean checkBackpack(PlayerEntity player, Item item)
    {
        return CapabilityUtils.isWearingBackpack(player) && CapabilityUtils.getBackpackInv(player).getItemStack().getItem() == item && CapabilityUtils.getBackpackInv(player).getAbilityValue();
    }

    public void addTimedEffect(PlayerEntity player, Effect effect, int minDuration, int maxDuration, int amplifier, boolean ambient, boolean showParticle, boolean showIcon)
    {
        if(!player.hasEffect(effect) || player.getEffect(effect).getDuration() <= minDuration)
        {
            player.addEffect(new EffectInstance(effect, maxDuration, amplifier, ambient, showParticle, showIcon));
        }
    }

    public static void sendParticlesPacket(IParticleData data, PlayerEntity player, int count)
    {
        for(int i = 0; i < count; i++)
        {
            double d0 = player.level.random.nextGaussian() * 0.02D;
            double d1 = player.level.random.nextGaussian() * 0.02D;
            double d2 = player.level.random.nextGaussian() * 0.02D;

            if(player.level instanceof ServerWorld)
            {
                ((ServerWorld)player.level).sendParticles(data, player.getRandomX(1.0D), player.getRandomY() + 0.5D, player.getRandomZ(1.0D), 1, d0, d1, d2, 0.0F);
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
            //ModItems.LAPIS_TRAVELERS_BACKPACK.get(),
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
            //ModItems.LAPIS_TRAVELERS_BACKPACK.get(),

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