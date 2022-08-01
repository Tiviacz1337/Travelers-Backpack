package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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
     * {@link ServerActions#switchAbilitySliderBlockEntity(Player, BlockPos)}
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
                armorAbility(player, null, false, NETHERITE_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.DIAMOND_TRAVELERS_BACKPACK.get())
            {
                armorAbility(player, null, false, DIAMOND_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.GOLD_TRAVELERS_BACKPACK.get())
            {
                armorAbility(player, null, false, GOLD_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.EMERALD_TRAVELERS_BACKPACK.get())
            {
                emeraldAbility(player, null);
            }

            if(stack.getItem() == ModItems.IRON_TRAVELERS_BACKPACK.get())
            {
                armorAbility(player, null, false, IRON_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.CACTUS_TRAVELERS_BACKPACK.get())
            {
                cactusAbility(player, null);
            }

            if(stack.getItem() == ModItems.DRAGON_TRAVELERS_BACKPACK.get())
            {
                dragonAbility(player);
            }

            if(stack.getItem() == ModItems.BLAZE_TRAVELERS_BACKPACK.get())
            {
                blazeAbility(player);
            }

            if(stack.getItem() == ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK.get())
            {
                magmaCubeAbility(player);
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

    public void abilityRemoval(@Nullable ItemStack stack, @Nullable Player player, @Nullable TravelersBackpackBlockEntity blockEntity)
    {
        if(blockEntity == null) //WEARABLE ABILITIES
        {
            if(stack.getItem() == ModItems.NETHERITE_TRAVELERS_BACKPACK.get())
            {
                armorAbility(player, null, true, NETHERITE_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.DIAMOND_TRAVELERS_BACKPACK.get())
            {
                armorAbility(player, null, true, DIAMOND_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.IRON_TRAVELERS_BACKPACK.get())
            {
                armorAbility(player, null, true, IRON_ARMOR_MODIFIER);
            }

            if(stack.getItem() == ModItems.GOLD_TRAVELERS_BACKPACK.get())
            {
                armorAbility(player, null, true, GOLD_ARMOR_MODIFIER);
            }
        }
    }

    /**
     * Called in TravelersBackpackBlock#animateTick method to enable visual only abilities for TravelersBackpackTileEntity
     */

    public void animateTick(@Nullable TravelersBackpackBlockEntity blockEntity, BlockState stateIn, Level level, BlockPos pos, RandomSource rand)
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
                spongeAbility(null, blockEntity);
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

    public void armorAbility(@Nullable Player player, @Nullable TravelersBackpackBlockEntity blockEntity, boolean isRemoval, AttributeModifier modifier)
    {
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);

        if(isRemoval && armor != null && armor.hasModifier(modifier))
        {
            armor.removePermanentModifier(modifier.getId());
        }

        if(!isRemoval && armor != null && !armor.hasModifier(modifier))
        {
            armor.addPermanentModifier(modifier);
        }
    }

    public void armorAbilityRemovals(@Nullable Player player, @Nullable TravelersBackpackBlockEntity blockEntity)
    {
        armorAbility(player, blockEntity, true, NETHERITE_ARMOR_MODIFIER);
        armorAbility(player, blockEntity, true, DIAMOND_ARMOR_MODIFIER);
        armorAbility(player, blockEntity, true, IRON_ARMOR_MODIFIER);
        armorAbility(player, blockEntity, true, GOLD_ARMOR_MODIFIER);
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

    public void spongeAbility(@Nullable Player player, @Nullable TravelersBackpackBlockEntity blockEntity)
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

    public void chickenAbility(@Nullable Player player, boolean firstSwitch)
    {
        TravelersBackpackContainer container = CapabilityUtils.getBackpackInv(player);

        if(firstSwitch)
        {
            if(container.getLastTime() <= 0)
            {
                if(!player.level.isClientSide)
                {
                    container.setLastTime((200 + 10 * player.level.random.nextInt(10)) * 20);
                    container.setDataChanged(ITravelersBackpackContainer.LAST_TIME_DATA);
                }
            }
        }

        if(container.getLastTime() <= 0)
        {
            player.level.playSound(player, player.blockPosition(), SoundEvents.CHICKEN_EGG, SoundSource.AMBIENT, 1.0F, (player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.3F + 1.0F);
            player.spawnAtLocation(Items.EGG);

            if(!player.level.isClientSide)
            {
                container.setLastTime((200 + 10 * player.level.random.nextInt(10)) * 20);
                container.setDataChanged(ITravelersBackpackContainer.LAST_TIME_DATA);
            }
        }
    }

    public void cactusAbility(@Nullable Player player, @Nullable TravelersBackpackBlockEntity blockEntity)
    {
        ITravelersBackpackContainer container = player == null ? blockEntity : CapabilityUtils.getBackpackInv(player);
        FluidTank leftTank = container.getLeftTank();
        FluidTank rightTank = container.getRightTank();

        int drops = 0;

        if(player != null && player.isInWater())
        {
            drops += 2;
        }

        if(isUnderRain(blockEntity == null ? player.blockPosition() : blockEntity.getBlockPos(), blockEntity == null ? player.level : blockEntity.getLevel()))
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

    public static boolean creeperAbility(LivingDeathEvent event)
    {
        if(event.getEntity() instanceof Player)
        {
            Player player = (Player)event.getEntity();

            TravelersBackpackContainer container = CapabilityUtils.getBackpackInv(player);

            if(player.isDeadOrDying() && container != null && container.getItemStack().getItem() == ModItems.CREEPER_TRAVELERS_BACKPACK.get() && container.getAbilityValue() && container.getLastTime() <= 0)
            {
                player.setHealth(1.0F);
                player.removeAllEffects();
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 450, 1));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 400, 0));
                player.level.explode(player, DamageSource.playerAttack(player), null, player.getRandomX(0.5F), player.getY(), player.getRandomZ(0.5F), 3.0F, false, Explosion.BlockInteraction.NONE);
                player.level.playSound(null, player.blockPosition(), SoundEvents.CREEPER_PRIMED, SoundSource.AMBIENT, 1.2F, 0.5F);

                if(!player.level.isClientSide)
                {
                    container.setLastTime((200 + 10 * player.level.random.nextInt(10)) * 50);
                    container.setDataChanged(ITravelersBackpackContainer.LAST_TIME_DATA);
                }
                event.setCanceled(true);
                return true;
            }
        }
        return false;
    }

    public void dragonAbility(@Nullable Player player)
    {
        magmaCubeAbility(player);
        squidAbility(player);

        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 210, 0, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 210, 0, false, false, true));
    }

    public void blazeAbility(@Nullable Player player)
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

    public void magmaCubeAbility(@Nullable Player player)
    {
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 210, 0, false, false, true));
    }

    public void witherAbility(@Nullable Player player)
    {
        if(player.getEffect(MobEffects.WITHER) != null)
        {
            player.removeEffect(MobEffects.WITHER);
        }
    }

    public void batAbility(@Nullable Player player)
    {
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 210, 0, false, false, true));
    }

    private final TargetingConditions ocelotAbilityTargeting = TargetingConditions.forCombat().range(64.0D); //#TODO check

    public void ocelotAbility(@Nullable Player player)
    {
        if(player.level.getNearestEntity(Mob.class, ocelotAbilityTargeting, player, player.getX(), player.getY(), player.getZ(), player.getBoundingBox().inflate(6.0D, 2.0D, 6.0D)) != null)
        {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 0, false, false, true));
        }
    }

    public void squidAbility(@Nullable Player player)
    {
        if(player.isInWater())
        {
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 210, 0, false, false, true));
            batAbility(player);
        }
    }

    private boolean isUnderRain(BlockPos pos, Level level)
    {
        return level.canSeeSky(pos) && level.isRaining();
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
            ModItems.REDSTONE_TRAVELERS_BACKPACK.get(),

            ModItems.BOOKSHELF_TRAVELERS_BACKPACK.get(),
            //ModItems.END_TRAVELERS_BACKPACK.get(),
            //ModItems.NETHER_TRAVELERS_BACKPACK.get(),
            //ModItems.SANDSTONE_TRAVELERS_BACKPACK.get(),
            //ModItems.SNOW_TRAVELERS_BACKPACK.get(),
            ModItems.SPONGE_TRAVELERS_BACKPACK.get(),

            //ModItems.CAKE_TRAVELERS_BACKPACK.get(),

            ModItems.CACTUS_TRAVELERS_BACKPACK.get(),
           // ModItems.HAY_TRAVELERS_BACKPACK.get(),
           // ModItems.MELON_TRAVELERS_BACKPACK.get(),
           // ModItems.PUMPKIN_TRAVELERS_BACKPACK.get(),

            ModItems.CREEPER_TRAVELERS_BACKPACK.get(),
            ModItems.DRAGON_TRAVELERS_BACKPACK.get(),
           // ModItems.ENDERMAN_TRAVELERS_BACKPACK.get(),
            ModItems.BLAZE_TRAVELERS_BACKPACK.get(),
           // ModItems.GHAST_TRAVELERS_BACKPACK.get(),
            ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK.get(),
          //  ModItems.SKELETON_TRAVELERS_BACKPACK.get(),
          //  ModItems.SPIDER_TRAVELERS_BACKPACK.get(),
            ModItems.WITHER_TRAVELERS_BACKPACK.get(),

            ModItems.BAT_TRAVELERS_BACKPACK.get(),
           // ModItems.BEE_TRAVELERS_BACKPACK.get(),
           // ModItems.WOLF_TRAVELERS_BACKPACK.get(),
           // ModItems.FOX_TRAVELERS_BACKPACK.get(),
            ModItems.OCELOT_TRAVELERS_BACKPACK.get(),
           // ModItems.HORSE_TRAVELERS_BACKPACK.get(),
           // ModItems.COW_TRAVELERS_BACKPACK.get(),
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

            //ModItems.END_TRAVELERS_BACKPACK.get(),
            //ModItems.NETHER_TRAVELERS_BACKPACK.get(),
            //ModItems.SANDSTONE_TRAVELERS_BACKPACK.get(),
            //ModItems.SNOW_TRAVELERS_BACKPACK.get(),

            //ModItems.CAKE_TRAVELERS_BACKPACK.get(),

            ModItems.CACTUS_TRAVELERS_BACKPACK.get(),
            //ModItems.HAY_TRAVELERS_BACKPACK.get(),
            //ModItems.MELON_TRAVELERS_BACKPACK.get(),
            //ModItems.PUMPKIN_TRAVELERS_BACKPACK.get(),

            ModItems.CREEPER_TRAVELERS_BACKPACK.get(),
            ModItems.DRAGON_TRAVELERS_BACKPACK.get(),
            //ModItems.ENDERMAN_TRAVELERS_BACKPACK.get(),
            ModItems.BLAZE_TRAVELERS_BACKPACK.get(),
            //ModItems.GHAST_TRAVELERS_BACKPACK.get(),
            ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK.get(),
            //ModItems.SKELETON_TRAVELERS_BACKPACK.get(),
            //ModItems.SPIDER_TRAVELERS_BACKPACK.get(),
            ModItems.WITHER_TRAVELERS_BACKPACK.get(),

            ModItems.BAT_TRAVELERS_BACKPACK.get(),
           // ModItems.BEE_TRAVELERS_BACKPACK.get(),
           // ModItems.WOLF_TRAVELERS_BACKPACK.get(),
            //ModItems.FOX_TRAVELERS_BACKPACK.get(),
            ModItems.OCELOT_TRAVELERS_BACKPACK.get(),
            //ModItems.HORSE_TRAVELERS_BACKPACK.get(),
            //ModItems.COW_TRAVELERS_BACKPACK.get(),
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
            ModItems.CREEPER_TRAVELERS_BACKPACK.get(),

            ModItems.CHICKEN_TRAVELERS_BACKPACK.get()
    };

    public static final Item[] BLOCK_ABILITIES_LIST = {

            ModItems.EMERALD_TRAVELERS_BACKPACK.get(),
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
            //ModItems.MELON_TRAVELERS_BACKPACK.get(),
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