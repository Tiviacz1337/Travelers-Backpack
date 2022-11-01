package com.tiviacz.travelersbackpack.common;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.UUID;

public class BackpackAbilities
{
    public static final BackpackAbilities ABILITIES = new BackpackAbilities();

    /**
     * Called in TravelersBackpackTileEntity#Tick and ForgeEventHandler#playerTick method to enable abilities
     */
    public void abilityTick(@Nullable ItemStack stack, @Nullable PlayerEntity player, @Nullable TravelersBackpackBlockEntity blockEntity)
    {
        if(blockEntity == null) //WEARABLE ABILITIES
        {
            if(stack.getItem() == ModItems.NETHERITE_TRAVELERS_BACKPACK)
            {
                attributeAbility(player, false, EntityAttributes.GENERIC_ARMOR, NETHERITE_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.DIAMOND_TRAVELERS_BACKPACK)
            {
                attributeAbility(player, false, EntityAttributes.GENERIC_ARMOR, DIAMOND_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.GOLD_TRAVELERS_BACKPACK)
            {
                attributeAbility(player, false, EntityAttributes.GENERIC_ARMOR, GOLD_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.EMERALD_TRAVELERS_BACKPACK)
            {
                emeraldAbility(player, null);
            }

            if(stack.getItem() == ModItems.IRON_TRAVELERS_BACKPACK)
            {
                attributeAbility(player, false, EntityAttributes.GENERIC_ARMOR, IRON_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.CAKE_TRAVELERS_BACKPACK)
            {
                cakeAbility(player);
            }

            if(stack.getItem() == ModItems.CACTUS_TRAVELERS_BACKPACK)
            {
                cactusAbility(player, null);
            }

            if(stack.getItem() == ModItems.DRAGON_TRAVELERS_BACKPACK)
            {
                dragonAbility(player);
            }

            if(stack.getItem() == ModItems.ENDERMAN_TRAVELERS_BACKPACK)
            {
                attributeAbility(player, false, ReachEntityAttributes.REACH, ENDERMAN_REACH_DISTANCE_MODIFIER);
            }

            if(stack.getItem() == ModItems.BLAZE_TRAVELERS_BACKPACK)
            {
                blazeAbility(player);
            }

            if(stack.getItem() == ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK)
            {
                magmaCubeAbility(player);
            }

            if(stack.getItem() == ModItems.SPIDER_TRAVELERS_BACKPACK)
            {
                spiderAbility(player);
            }

            if(stack.getItem() == ModItems.WITHER_TRAVELERS_BACKPACK)
            {
                witherAbility(player);
            }

            if(stack.getItem() == ModItems.BAT_TRAVELERS_BACKPACK)
            {
                batAbility(player);
            }

            if(stack.getItem() == ModItems.OCELOT_TRAVELERS_BACKPACK)
            {
                ocelotAbility(player);
            }

            if(stack.getItem() == ModItems.COW_TRAVELERS_BACKPACK)
            {
                cowAbility(player);
            }

            if(stack.getItem() == ModItems.CHICKEN_TRAVELERS_BACKPACK)
            {
                chickenAbility(player, false);
            }

            if(stack.getItem() == ModItems.SQUID_TRAVELERS_BACKPACK)
            {
                squidAbility(player);
            }
        }
        else //TILE ABILITIES
        {
            Item item = blockEntity.getItemStack().getItem();

            if(item == ModItems.CACTUS_TRAVELERS_BACKPACK)
            {
                cactusAbility(null, blockEntity);
            }
        }
    }

    public void abilityRemoval(ItemStack stack, PlayerEntity player)
    {
        if(stack.getItem() == ModItems.NETHERITE_TRAVELERS_BACKPACK)
        {
            attributeAbility(player, true, EntityAttributes.GENERIC_ARMOR, NETHERITE_ARMOR_MODIFIER);
        }

        if(stack.getItem() == ModItems.DIAMOND_TRAVELERS_BACKPACK)
        {
            attributeAbility(player, true, EntityAttributes.GENERIC_ARMOR, DIAMOND_ARMOR_MODIFIER);
        }

        if(stack.getItem() == ModItems.IRON_TRAVELERS_BACKPACK)
        {
            attributeAbility(player, true, EntityAttributes.GENERIC_ARMOR, IRON_ARMOR_MODIFIER);
        }

        if(stack.getItem() == ModItems.GOLD_TRAVELERS_BACKPACK)
        {
            attributeAbility(player, true, EntityAttributes.GENERIC_ARMOR, GOLD_ARMOR_MODIFIER);
        }

        if(stack.getItem() == ModItems.ENDERMAN_TRAVELERS_BACKPACK)
        {
            attributeAbility(player, true, ReachEntityAttributes.REACH, ENDERMAN_REACH_DISTANCE_MODIFIER);
        }
    }

    /**
     * Called in TravelersBackpackBlock#animateTick method to enable visual only abilities for TravelersBackpackTileEntity
     */

    public void animateTick(TravelersBackpackBlockEntity blockEntity, BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if(blockEntity != null && blockEntity.getAbilityValue())
        {
            Block block = stateIn.getBlock();

            if(block == ModBlocks.EMERALD_TRAVELERS_BACKPACK)
            {
                emeraldAbility(null, blockEntity);
            }

            if(block == ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK)
            {
                bookshelfAbility(null, blockEntity);
            }

            if(block == ModBlocks.SPONGE_TRAVELERS_BACKPACK)
            {
                spongeAbility(blockEntity);
            }
        }
    }

    public void emeraldAbility(@Nullable PlayerEntity player, @Nullable TravelersBackpackBlockEntity blockEntity)
    {
        World world = player == null ? blockEntity.getWorld() : player.world;

        if(player == null || world.random.nextInt(10) == 1)
        {
            float f = world.random.nextFloat() * (float) Math.PI * 2.0F;
            float f1 = world.random.nextFloat() * 0.5F + 0.5F;
            float f2 = MathHelper.sin(f) * 0.5F * f1;
            float f3 = MathHelper.cos(f) * 0.5F * f1;
            world.addParticle(ParticleTypes.HAPPY_VILLAGER,
                    player == null ? blockEntity.getPos().getX() + f2 + 0.5F : player.getPos().x + f2,
                    player == null ? blockEntity.getPos().getY() + world.random.nextFloat() : player.getBoundingBox().minY + world.random.nextFloat() + 0.5F,
                    player == null ? blockEntity.getPos().getZ() + f3 + 0.5F : player.getPos().z + f3, (double)(float)Math.pow(2.0D, (world.random.nextInt(169) - 12) / 12.0D) / 24.0D, -1.0D, 0.0D);
        }
    }

    public final EntityAttributeModifier NETHERITE_ARMOR_MODIFIER = new EntityAttributeModifier(UUID.fromString("49d951a4-ca9c-48b5-b549-61ef67ee53aa"), "NetheriteBackpackBonusArmor", 4.0D, EntityAttributeModifier.Operation.ADDITION);
    public final EntityAttributeModifier DIAMOND_ARMOR_MODIFIER = new EntityAttributeModifier(UUID.fromString("294425c4-8dc6-4640-a336-d9fd72950e20"), "DiamondBackpackBonusArmor", 3.0D, EntityAttributeModifier.Operation.ADDITION);
    public final EntityAttributeModifier IRON_ARMOR_MODIFIER = new EntityAttributeModifier(UUID.fromString("fcf6706b-dfd9-40d6-aa25-62c4fb7a83fa"), "IronBackpackBonusArmor", 2.0D, EntityAttributeModifier.Operation.ADDITION);
    public final EntityAttributeModifier GOLD_ARMOR_MODIFIER = new EntityAttributeModifier(UUID.fromString("21060f97-da7a-4460-a4e4-c94fae72ab00"), "GoldBackpackBonusArmor", 2.0D, EntityAttributeModifier.Operation.ADDITION);
    public final EntityAttributeModifier ENDERMAN_REACH_DISTANCE_MODIFIER = new EntityAttributeModifier(UUID.fromString("a3d7a647-1ed9-4317-94c2-ca889cd33657"), "EndermanReachDistanceBonus", 1.0D, EntityAttributeModifier.Operation.ADDITION);

    public void attributeAbility(PlayerEntity player, boolean isRemoval, EntityAttribute attribute, EntityAttributeModifier modifier)
    {
        EntityAttributeInstance armor = player.getAttributeInstance(attribute);

        if(isRemoval && armor != null && armor.hasModifier(modifier))
        {
            armor.tryRemoveModifier(modifier.getId());
        }

        if(!isRemoval && armor != null && !armor.hasModifier(modifier))
        {
            armor.addPersistentModifier(modifier);
        }
    }

    public void armorAbilityRemovals(PlayerEntity player)
    {
        attributeAbility(player, true, EntityAttributes.GENERIC_ARMOR, NETHERITE_ARMOR_MODIFIER);
        attributeAbility(player, true, EntityAttributes.GENERIC_ARMOR, DIAMOND_ARMOR_MODIFIER);
        attributeAbility(player, true, EntityAttributes.GENERIC_ARMOR, IRON_ARMOR_MODIFIER);
        attributeAbility(player, true, EntityAttributes.GENERIC_ARMOR, GOLD_ARMOR_MODIFIER);

        attributeAbility(player, true, ReachEntityAttributes.REACH, ENDERMAN_REACH_DISTANCE_MODIFIER);
    }

    public void bookshelfAbility(@Nullable PlayerEntity player, @Nullable TravelersBackpackBlockEntity blockEntity)
    {
        BlockPos enchanting = BackpackUtils.findBlock3D(blockEntity.getWorld(), blockEntity.getPos().getX(), blockEntity.getPos().getY(), blockEntity.getPos().getZ(), Blocks.ENCHANTING_TABLE, 2, 2);

        if(enchanting != null)
        {
            if(!blockEntity.getWorld().isAir(new BlockPos((enchanting.getX() - blockEntity.getPos().getX()) / 2 + blockEntity.getPos().getX(), enchanting.getY(), (enchanting.getZ() - blockEntity.getPos().getZ()) / 2 + blockEntity.getPos().getZ())))
            {
                return;
            }

            for(int o = 0; o < 4; o++)
            {
                blockEntity.getWorld().addParticle(ParticleTypes.ENCHANT, enchanting.getX() + 0.5D, enchanting.getY() + 2.0D, enchanting.getZ() + 0.5D,
                        ((blockEntity.getPos().getX() - enchanting.getX()) + blockEntity.getWorld().random.nextFloat()) - 0.5D,
                        ((blockEntity.getPos().getY() - enchanting.getY()) - blockEntity.getWorld().random.nextFloat() - 1.0F),
                        ((blockEntity.getPos().getZ() - enchanting.getZ()) + blockEntity.getWorld().random.nextFloat()) - 0.5D);
            }
        }
    }

    public void spongeAbility(TravelersBackpackBlockEntity blockEntity)
    {
        if(!blockEntity.getLeftTank().isResourceBlank() && !blockEntity.getRightTank().isResourceBlank())
        {
            if(blockEntity.getLeftTank().getResource().getFluid().matchesType(Fluids.WATER) && blockEntity.getRightTank().getResource().getFluid().matchesType(Fluids.WATER))
            {
                if(blockEntity.getLeftTank().getAmount() == blockEntity.getLeftTank().getCapacity() && blockEntity.getRightTank().getAmount() == blockEntity.getRightTank().getCapacity())
                {
                    float f = blockEntity.getWorld().random.nextFloat() * (float) Math.PI * 2.0F;
                    float f1 = blockEntity.getWorld().random.nextFloat() * 0.5F + 0.5F;
                    float f2 = MathHelper.sin(f) * 0.5F * f1;
                    float f3 = MathHelper.cos(f) * 0.5F * f1;
                    blockEntity.getWorld().addParticle(ParticleTypes.SPLASH,
                            blockEntity.getPos().getX() + f2 + 0.5F,
                            blockEntity.getPos().getY() + blockEntity.getWorld().random.nextFloat(),
                            blockEntity.getPos().getZ() + f3 + 0.5F, (double)(float)Math.pow(2.0D, (blockEntity.getWorld().random.nextInt(169) - 12) / 12.0D) / 24.0D, -1.0D, 0.0D);
                }
            }
        }
    }

    public void cakeAbility(PlayerEntity player)
    {
        TravelersBackpackInventory container = ComponentUtils.getBackpackInv(player);

        if(container.getLastTime() <= 0)
        {
            player.getHungerManager().add(2, 0.1F);
            player.world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_CAKE_ADD_CANDLE, SoundCategory.AMBIENT, 1.0F, (player.world.random.nextFloat() - player.world.random.nextFloat()) * 0.3F + 1.0F);

            if(!player.world.isClient)
            {
                if(player.world instanceof ServerWorld server)
                {
                    for(int i = 0; i < 3; i++)
                    {
                        float f = server.random.nextFloat() * (float) Math.PI * 2.0F;
                        float f1 = server.random.nextFloat() * 0.5F + 0.5F;
                        float f2 = MathHelper.sin(f) * 0.5F * f1;
                        float f3 = MathHelper.cos(f) * 0.5F * f1;
                        server.spawnParticles(ParticleTypes.HEART,
                                player.getPos().x + f2,
                                player.getBoundingBox().minY + player.world.random.nextFloat() + 0.5F,
                                player.getPos().z + f3, 3, (double)(float)Math.pow(2.0D, (player.world.random.nextInt(169) - 12) / 12.0D) / 24.0D, -1.0D, 0.0D, 0);
                    }
                }

                container.setLastTime(3000 + player.getHungerManager().getFoodLevel() * 5);
                container.markDataDirty(ITravelersBackpackInventory.LAST_TIME_DATA);
            }
        }
    }

    public void chickenAbility(PlayerEntity player, boolean firstSwitch)
    {
        TravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);

        if(firstSwitch)
        {
            if(inv.getLastTime() <= 0)
            {
                if(!inv.getWorld().isClient)
                {
                    inv.setLastTime((200 + 10 * player.world.random.nextInt(10)) * 20);
                    inv.markDataDirty(ITravelersBackpackInventory.LAST_TIME_DATA);
                    return;
                }
            }
        }

        if(inv.getLastTime() <= 0)
        {
            player.world.playSound(player, player.getBlockPos(), SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.AMBIENT, 1.0F, (player.world.random.nextFloat() - player.world.random.nextFloat()) * 0.3F + 1.0F);
            player.dropItem(Items.EGG);

            if(!inv.getWorld().isClient)
            {
                inv.setLastTime((200 + 10 * player.world.random.nextInt(10)) * 20);
                inv.markDataDirty(ITravelersBackpackInventory.LAST_TIME_DATA);
            }
        }
    }

    public void cactusAbility(@Nullable PlayerEntity player, @Nullable TravelersBackpackBlockEntity blockEntity)
    {
        ITravelersBackpackInventory inv = player == null ? blockEntity : ComponentUtils.getBackpackInv(player);
        SingleVariantStorage<FluidVariant> leftTank = inv.getLeftTank();
        SingleVariantStorage<FluidVariant> rightTank = inv.getRightTank();

        int drops = 0;

        if(player != null && player.isSubmergedInWater())
        {
            drops += 2 * 81;
        }

        if(isUnderRain(blockEntity == null ? player.getBlockPos() : blockEntity.getPos(), blockEntity == null ? player.world : blockEntity.getWorld()))
        {
            drops += 1 * 81;
        }

        FluidVariant water = FluidVariant.of(Fluids.WATER);

        if(!inv.getWorld().isClient && inv.getLastTime() <= 0 && drops > 0)
        {
            inv.setLastTime(5);

            try(Transaction transaction = Transaction.openOuter())
            {
                if(leftTank.isResourceBlank() || leftTank.getResource().isOf(water.getFluid()))
                {
                    long amount = leftTank.insert(water, drops, transaction);
                    if(amount == drops)
                    {
                        transaction.commit();
                    }
                }
            }

            try(Transaction transaction = Transaction.openOuter())
            {
                if(rightTank.isResourceBlank() || rightTank.getResource().isOf(water.getFluid()))
                {
                    long amount = rightTank.insert(water, drops, transaction);
                    if(amount == drops)
                    {
                        transaction.commit();
                    }
                }
            }

            inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);
        }
    }

    public static void melonAbility(TravelersBackpackBlockEntity blockEntity)
    {
        if(blockEntity.getAbilityValue() && blockEntity.getLastTime() <= 0)
        {
            Block.dropStack(blockEntity.getWorld(), blockEntity.getPos(), new ItemStack(Items.MELON_SLICE, blockEntity.getWorld().random.nextBetweenExclusive(0, 3)));
            blockEntity.setLastTime(blockEntity.getWorld().random.nextBetweenExclusive(120 * 20, 480 * 20));
            blockEntity.markDirty();
        }
    }

    public static void pumpkinAbility(PlayerEntity player, CallbackInfoReturnable<Boolean> cir)
    {
        if(ABILITIES.checkBackpack(player, ModItems.PUMPKIN_TRAVELERS_BACKPACK))
        {
            cir.setReturnValue(false);
        }
    }

    public static boolean creeperAbility(PlayerEntity player)
    {
        TravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);

        if(player.isDead() && inv != null && inv.getItemStack().getItem() == ModItems.CREEPER_TRAVELERS_BACKPACK && inv.getAbilityValue() && inv.getLastTime() <= 0)
        {
            player.setHealth(1.0F);
            player.clearStatusEffects();
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 450, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 400, 0));
            player.world.createExplosion(player, DamageSource.player(player), null, player.getParticleX(0.5F), player.getY(), player.getParticleZ(0.5F), 3.0F, false, Explosion.DestructionType.NONE);
            player.world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.AMBIENT, 1.2F, 0.5F);

            if(!inv.getWorld().isClient)
            {
                inv.setLastTime((200 + 10 * player.world.random.nextInt(10)) * 50);
                inv.markDataDirty(ITravelersBackpackInventory.LAST_TIME_DATA);
            }
            return true;
        }
        return false;
    }

    public void dragonAbility(PlayerEntity player)
    {
        magmaCubeAbility(player);
        squidAbility(player);

        addTimedStatusEffect(player, StatusEffects.REGENERATION, 0, 210, 0, false, false, true);
        addTimedStatusEffect(player, StatusEffects.STRENGTH, 210, 240, 0, false, false, true);
    }

    public void blazeAbility(PlayerEntity player)
    {
        if(player.fallDistance >= 3.0F)
        {
            for(int i = 0; i < 4; ++i)
            {
                player.world.addParticle(ParticleTypes.LARGE_SMOKE, player.getParticleX(0.5D), player.getRandomBodyY(), player.getParticleZ(0.5D), 0.0D, 0.0D, 0.0D);
            }

            player.fallDistance = 0.0F;
        }
    }

    public static void blazeAbility(EntityHitResult result, SmallFireballEntity fireball, CallbackInfo ci)
    {
        if(result.getEntity() instanceof PlayerEntity player && ABILITIES.checkBackpack(player, ModItems.BLAZE_TRAVELERS_BACKPACK))
        {
            player.world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0F, 0.8F + player.world.random.nextFloat() * 0.4F);
            sendParticlesPacket(ParticleTypes.FLAME, player, 3);

            fireball.discard();
            ci.cancel();
        }
    }

    public static void ghastAbility(GhastEntity ghast, LivingEntity livingEntity, CallbackInfo ci)
    {
        if(livingEntity instanceof PlayerEntity player)
        {
            if(ABILITIES.checkBackpack(player, ModItems.GHAST_TRAVELERS_BACKPACK))
            {
                if(ghast.getAttacker() != player)
                {
                    ci.cancel();
                }
            }
        }
    }

    public void magmaCubeAbility(PlayerEntity player)
    {
        addTimedStatusEffect(player, StatusEffects.FIRE_RESISTANCE, 210, 240, 0, false, false, true);
    }

    public void spiderAbility(PlayerEntity player)
    {
        if(player.horizontalCollision && !(player.getBlockStateAtPos().getBlock() instanceof FluidBlock))
        {
            if(!player.isOnGround() && player.isSneaking())
            {
                player.setVelocity(player.getVelocity().x, 0.0D, player.getVelocity().z);
            }
            else
            {
                player.setVelocity(player.getVelocity().x, 0.20D, player.getVelocity().z);

                World level = player.world;
                BlockState state = level.getBlockState(player.getBlockPos().offset(player.getMovementDirection()));
                player.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
                        player.getX() + (level.random.nextDouble() - 0.5D) * (double)player.getDimensions(EntityPose.STANDING).width,
                        player.getY() + 0.1D,
                        player.getZ() + (level.random.nextDouble() - 0.5D) * (double)player.getDimensions(EntityPose.STANDING).width,
                        0.0D, 1.5D, 0.0D);
            }
        }
    }

    public void witherAbility(PlayerEntity player)
    {
        if(player.getStatusEffect(StatusEffects.WITHER) != null)
        {
            player.removeStatusEffect(StatusEffects.WITHER);
        }
    }

    public void batAbility(PlayerEntity player)
    {
        addTimedStatusEffect(player, StatusEffects.NIGHT_VISION, 210, 240, 0, false, false, true);
    }

    public static void beeAbility(PlayerEntity player, Entity target)
    {
        if(ABILITIES.checkBackpack(player, ModItems.BEE_TRAVELERS_BACKPACK))
        {
            boolean flag = target.damage(DamageSource.sting(player), 1.0F);

            if(flag)
            {
                player.applyDamageEffects(player, target);

                if(target instanceof LivingEntity living)
                {
                    living.setStingerCount(living.getStingerCount() + 1);
                    living.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 4 * 20, 0), player);
                }
            }
        }
    }

    private static final TargetPredicate OCELOT_ABILITY_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(6.0D);

    public void ocelotAbility(PlayerEntity player)
    {
        if(player.world.getClosestEntity(MobEntity.class, OCELOT_ABILITY_PREDICATE, player, player.getX(), player.getY(), player.getZ(), player.getBoundingBox().expand(6.0D, 2.0D, 6.0D)) != null)
        {
            addTimedStatusEffect(player, StatusEffects.SPEED, 20, 30, 0, false, false, true);
        }
    }

    public void cowAbility(PlayerEntity player)
    {
        if(!player.getActiveStatusEffects().isEmpty() && ComponentUtils.getBackpackInv(player).getLastTime() <= 0)
        {
            player.clearStatusEffects();

            if(!player.world.isClient)
            {
                player.world.syncWorldEvent(2007, player.getBlockPos(), 16777215);
            }

            player.world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.PLAYERS, 1.0F, player.getRandom().nextFloat() * 0.1F + 0.9F);

            ComponentUtils.getBackpackInv(player).setLastTime(player.world.random.nextBetweenExclusive(450 * 20, 600 * 20));
            ComponentUtils.getBackpackInv(player).markDataDirty(ITravelersBackpackInventory.LAST_TIME_DATA);
        }
    }

    public void squidAbility(PlayerEntity player)
    {
        if(player.isSubmergedInWater())
        {
            addTimedStatusEffect(player, StatusEffects.WATER_BREATHING, 210, 240, 0, false, false, true);
            batAbility(player);
        }
    }

    //Utility methods

    public static void sendParticlesPacket(ParticleEffect effect, PlayerEntity player, int count)
    {
        for(int i = 0; i < count; i++)
        {
            double d0 = player.world.random.nextGaussian() * 0.02D;
            double d1 = player.world.random.nextGaussian() * 0.02D;
            double d2 = player.world.random.nextGaussian() * 0.02D;

            if(player.world instanceof ServerWorld server)
            {
                server.spawnParticles(effect, player.getParticleX(1.0D), player.getRandomBodyY() + 0.5D, player.getParticleZ(1.0D), 1, d0, d1, d2, 0.0F);
            }
        }
    }

    public boolean checkBackpack(PlayerEntity player, Item item)
    {
        return ComponentUtils.isWearingBackpack(player) && ComponentUtils.getBackpackInv(player).getItemStack().getItem() == item && ComponentUtils.getBackpackInv(player).getAbilityValue();
    }

    public void addTimedStatusEffect(PlayerEntity player, StatusEffect effect, int minDuration, int maxDuration, int amplifier, boolean ambient, boolean showParticle, boolean showIcon)
    {
        if(!player.hasStatusEffect(effect) || player.getStatusEffect(effect).getDuration() <= minDuration)
        {
            player.addStatusEffect(new StatusEffectInstance(effect, maxDuration, amplifier, ambient, showParticle, showIcon));
        }
    }

    private boolean isUnderRain(BlockPos pos, World world)
    {
        return world.isSkyVisible(pos) && world.isRaining();
    }

    public static boolean isOnList(Item[] list, ItemStack stackToCheck)
    {
        return Arrays.stream(list).anyMatch(s -> s == stackToCheck.getItem());
    }

    public static final Item[] ALL_ABILITIES_LIST = {

            ModItems.NETHERITE_TRAVELERS_BACKPACK,
            ModItems.DIAMOND_TRAVELERS_BACKPACK,
            ModItems.GOLD_TRAVELERS_BACKPACK,
            ModItems.EMERALD_TRAVELERS_BACKPACK, //#TODO niy
            ModItems.IRON_TRAVELERS_BACKPACK,
            //ModItems.LAPIS_TRAVELERS_BACKPACK,
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
            ModItems.SQUID_TRAVELERS_BACKPACK,
            //  ModItems.IRON_GOLEM_TRAVELERS_BACKPACK
    };

    public static final Item[] ITEM_ABILITIES_LIST = {

            ModItems.NETHERITE_TRAVELERS_BACKPACK,
            ModItems.DIAMOND_TRAVELERS_BACKPACK,
            ModItems.GOLD_TRAVELERS_BACKPACK,
            ModItems.EMERALD_TRAVELERS_BACKPACK,
            ModItems.IRON_TRAVELERS_BACKPACK,
            //ModItems.LAPIS_TRAVELERS_BACKPACK,

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
            ModItems.SQUID_TRAVELERS_BACKPACK,
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
            ModItems.MELON_TRAVELERS_BACKPACK,
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