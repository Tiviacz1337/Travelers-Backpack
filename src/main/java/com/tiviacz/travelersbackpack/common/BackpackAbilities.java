package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Random;
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
                armorAbility(player, null, false, NETHERITE_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.DIAMOND_TRAVELERS_BACKPACK)
            {
                armorAbility(player, null, false, DIAMOND_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.GOLD_TRAVELERS_BACKPACK)
            {
                armorAbility(player, null, false, GOLD_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.EMERALD_TRAVELERS_BACKPACK)
            {
                emeraldAbility(player, null);
            }

            if(stack.getItem() == ModItems.IRON_TRAVELERS_BACKPACK)
            {
                armorAbility(player, null, false, IRON_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.CACTUS_TRAVELERS_BACKPACK)
            {
                cactusAbility(player, null);
            }

            if(stack.getItem() == ModItems.DRAGON_TRAVELERS_BACKPACK)
            {
                dragonAbility(player);
            }

            if(stack.getItem() == ModItems.BLAZE_TRAVELERS_BACKPACK)
            {
                blazeAbility(player);
            }

            if(stack.getItem() == ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK)
            {
                magmaCubeAbility(player);
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

    public void abilityRemoval(@Nullable ItemStack stack, @Nullable PlayerEntity player, @Nullable TravelersBackpackBlockEntity blockEntity)
    {
        if(blockEntity == null) //WEARABLE ABILITIES
        {
            if(stack.getItem() == ModItems.NETHERITE_TRAVELERS_BACKPACK)
            {
                armorAbility(player, null, true, NETHERITE_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.DIAMOND_TRAVELERS_BACKPACK)
            {
                armorAbility(player, null, true, DIAMOND_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.IRON_TRAVELERS_BACKPACK)
            {
                armorAbility(player, null, true, IRON_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.GOLD_TRAVELERS_BACKPACK)
            {
                armorAbility(player, null, true, GOLD_ARMOR_MODIFIER);
            }
        }
    }

    /**
     * Called in TravelersBackpackBlock#animateTick method to enable visual only abilities for TravelersBackpackTileEntity
     */

    public void animateTick(@Nullable TravelersBackpackBlockEntity blockEntity, BlockState stateIn, World worldIn, BlockPos pos, Random rand)
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
                spongeAbility(null, blockEntity);
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

    public void armorAbility(@Nullable PlayerEntity player, @Nullable TravelersBackpackBlockEntity blockEntity, boolean isRemoval, EntityAttributeModifier modifier)
    {
        EntityAttributeInstance armor = player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);

        if(isRemoval && armor != null && armor.hasModifier(modifier))
        {
            armor.tryRemoveModifier(modifier.getId());
        }

        if(!isRemoval && armor != null && !armor.hasModifier(modifier))
        {
            armor.addPersistentModifier(modifier);
        }
    }

    public void armorAbilityRemovals(@Nullable PlayerEntity player, @Nullable TravelersBackpackBlockEntity blockEntity)
    {
        armorAbility(player, blockEntity, true, NETHERITE_ARMOR_MODIFIER);
        armorAbility(player, blockEntity, true, DIAMOND_ARMOR_MODIFIER);
        armorAbility(player, blockEntity, true, IRON_ARMOR_MODIFIER);
        armorAbility(player, blockEntity, true, GOLD_ARMOR_MODIFIER);
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

    public void spongeAbility(@Nullable PlayerEntity player, @Nullable TravelersBackpackBlockEntity blockEntity)
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

    public void chickenAbility(@Nullable PlayerEntity player, boolean firstSwitch)
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

    public static boolean creeperAbility(@Nullable PlayerEntity player)
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

    public void dragonAbility(@Nullable PlayerEntity player)
    {
        magmaCubeAbility(player);
        squidAbility(player);

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 210, 0, false, false, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 210, 0, false, false, true));
    }

    public void blazeAbility(@Nullable PlayerEntity player)
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

    public void magmaCubeAbility(@Nullable PlayerEntity player)
    {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 210, 0, false, false, true));
    }

    public void witherAbility(@Nullable PlayerEntity player)
    {
        if(player.getStatusEffect(StatusEffects.WITHER) != null)
        {
            player.removeStatusEffect(StatusEffects.WITHER);
        }
    }

    public void batAbility(@Nullable PlayerEntity player)
    {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 210, 0, false, false, true));
    }

    private static final TargetPredicate OCELOT_ABILITY_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(6.0D);

    public void ocelotAbility(@Nullable PlayerEntity player)
    {
        if(player.world.getClosestEntity(MobEntity.class, OCELOT_ABILITY_PREDICATE, player, player.getX(), player.getY(), player.getZ(), player.getBoundingBox().expand(6.0D, 2.0D, 6.0D)) != null)
        {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20, 0, false, false, true));
        }
    }

    public void squidAbility(@Nullable PlayerEntity player)
    {
        if(player.isSubmergedInWater())
        {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 210, 0, false, false, true));
            batAbility(player);
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
            ModItems.REDSTONE_TRAVELERS_BACKPACK,

            ModItems.BOOKSHELF_TRAVELERS_BACKPACK,
            //ModItems.END_TRAVELERS_BACKPACK,
            //ModItems.NETHER_TRAVELERS_BACKPACK,
            //ModItems.SANDSTONE_TRAVELERS_BACKPACK,
            //ModItems.SNOW_TRAVELERS_BACKPACK,
            ModItems.SPONGE_TRAVELERS_BACKPACK,

            //ModItems.CAKE_TRAVELERS_BACKPACK,

            ModItems.CACTUS_TRAVELERS_BACKPACK,
           // ModItems.HAY_TRAVELERS_BACKPACK,
           // ModItems.MELON_TRAVELERS_BACKPACK,
           // ModItems.PUMPKIN_TRAVELERS_BACKPACK,

            ModItems.CREEPER_TRAVELERS_BACKPACK,
            ModItems.DRAGON_TRAVELERS_BACKPACK,
           // ModItems.ENDERMAN_TRAVELERS_BACKPACK,
            ModItems.BLAZE_TRAVELERS_BACKPACK,
           // ModItems.GHAST_TRAVELERS_BACKPACK,
            ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK,
          //  ModItems.SKELETON_TRAVELERS_BACKPACK,
          //  ModItems.SPIDER_TRAVELERS_BACKPACK,
            ModItems.WITHER_TRAVELERS_BACKPACK,

            ModItems.BAT_TRAVELERS_BACKPACK,
           // ModItems.BEE_TRAVELERS_BACKPACK,
           // ModItems.WOLF_TRAVELERS_BACKPACK,
           // ModItems.FOX_TRAVELERS_BACKPACK,
            ModItems.OCELOT_TRAVELERS_BACKPACK,
           // ModItems.HORSE_TRAVELERS_BACKPACK,
           // ModItems.COW_TRAVELERS_BACKPACK,
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

            //ModItems.END_TRAVELERS_BACKPACK,
            //ModItems.NETHER_TRAVELERS_BACKPACK,
            //ModItems.SANDSTONE_TRAVELERS_BACKPACK,
            //ModItems.SNOW_TRAVELERS_BACKPACK,

            //ModItems.CAKE_TRAVELERS_BACKPACK,

            ModItems.CACTUS_TRAVELERS_BACKPACK,
            //ModItems.HAY_TRAVELERS_BACKPACK,
            //ModItems.MELON_TRAVELERS_BACKPACK,
            //ModItems.PUMPKIN_TRAVELERS_BACKPACK,

            ModItems.CREEPER_TRAVELERS_BACKPACK,
            ModItems.DRAGON_TRAVELERS_BACKPACK,
            //ModItems.ENDERMAN_TRAVELERS_BACKPACK,
            ModItems.BLAZE_TRAVELERS_BACKPACK,
            //ModItems.GHAST_TRAVELERS_BACKPACK,
            ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK,
            //ModItems.SKELETON_TRAVELERS_BACKPACK,
            //ModItems.SPIDER_TRAVELERS_BACKPACK,
            ModItems.WITHER_TRAVELERS_BACKPACK,

            ModItems.BAT_TRAVELERS_BACKPACK,
           // ModItems.BEE_TRAVELERS_BACKPACK,
           // ModItems.WOLF_TRAVELERS_BACKPACK,
            //ModItems.FOX_TRAVELERS_BACKPACK,
            ModItems.OCELOT_TRAVELERS_BACKPACK,
            //ModItems.HORSE_TRAVELERS_BACKPACK,
            //ModItems.COW_TRAVELERS_BACKPACK,
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
            //ModItems.EMERALD_TRAVELERS_BACKPACK.get(),
            ModItems.IRON_TRAVELERS_BACKPACK,

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
            //ModItems.ENDERMAN_TRAVELERS_BACKPACK.get(),
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
            ModItems.CREEPER_TRAVELERS_BACKPACK,

            ModItems.CHICKEN_TRAVELERS_BACKPACK
    };

    public static final Item[] BLOCK_ABILITIES_LIST = {

            ModItems.EMERALD_TRAVELERS_BACKPACK,
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
            //ModItems.MELON_TRAVELERS_BACKPACK,
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