package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.TravelersBackpackCapability;
import com.tiviacz.travelersbackpack.capability.TravelersBackpackWearable;
import com.tiviacz.travelersbackpack.capability.entity.IEntityTravelersBackpack;
import com.tiviacz.travelersbackpack.capability.entity.TravelersBackpackEntityCapability;
import com.tiviacz.travelersbackpack.capability.entity.TravelersBackpackEntityWearable;
import com.tiviacz.travelersbackpack.commands.AccessBackpackCommand;
import com.tiviacz.travelersbackpack.commands.RestoreBackpackCommand;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.common.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.items.SleepingBagItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.CSyncCapabilityPacket;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackTileEntity;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import com.tiviacz.travelersbackpack.util.TimeUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.TableLootEntry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.server.command.ConfigCommand;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler
{
    @SubscribeEvent
    public static void playerSetSpawn(PlayerSetSpawnEvent event)
    {
        World world = event.getPlayer().level;

        if(event.getNewSpawn() != null)
        {
            Block block = world.getBlockState(event.getNewSpawn()).getBlock();

            if(!world.isClientSide && block instanceof SleepingBagBlock && !TravelersBackpackConfig.enableSleepingBagSpawnPoint)
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void playerRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        ItemStack stack = event.getItemStack();
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        PlayerEntity player = event.getPlayer();

        if(player.isShiftKeyDown() && event.getHand() == Hand.MAIN_HAND && player.getItemInHand(Hand.MAIN_HAND).getItem() instanceof SleepingBagItem)
        {
            TravelersBackpackTileEntity tileEntity = (TravelersBackpackTileEntity)world.getBlockEntity(pos);
            if(tileEntity == null) return;

            ItemStack oldSleepingBag = tileEntity.getProperSleepingBag(tileEntity.getSleepingBagColor()).getBlock().asItem().getDefaultInstance();
            tileEntity.setSleepingBagColor(ShapedBackpackRecipe.getProperColor((SleepingBagItem)player.getItemInHand(Hand.MAIN_HAND).getItem()));
            if(!world.isClientSide) InventoryHelper.dropItemStack(world, pos.getX(), pos.above().getY(), pos.getZ(), oldSleepingBag);
            player.level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.2F) * 0.7F);
            player.swing(Hand.MAIN_HAND, true);
            return;
        }

        // Equip Backpack on right click with any item in hand //#TODO CHECK
        else if(TravelersBackpackConfig.enableBackpackBlockWearable && event.getWorld().getBlockState(event.getPos()).getBlock() instanceof TravelersBackpackBlock)
        {
            if(player.isShiftKeyDown() && !CapabilityUtils.isWearingBackpack(player))
            {
                TravelersBackpackTileEntity tile = (TravelersBackpackTileEntity)world.getBlockEntity(pos);
                ItemStack backpack = new ItemStack(event.getWorld().getBlockState(event.getPos()).getBlock(), 1);

                if(!TravelersBackpack.enableCurios())
                {
                    Direction bagDirection = world.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                    if(world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState()))
                    {
                        tile.transferToItemStack(backpack);
                        CapabilityUtils.equipBackpack(event.getPlayer(), backpack);
                        player.swing(Hand.MAIN_HAND, true);

                        if(tile.isSleepingBagDeployed())
                        {
                            world.setBlockAndUpdate(pos.relative(bagDirection), Blocks.AIR.defaultBlockState());
                            world.setBlockAndUpdate(pos.relative(bagDirection).relative(bagDirection), Blocks.AIR.defaultBlockState());
                        }
                        return;
                    }
                }
                else
                {
                    tile.transferToItemStack(backpack);

                    CuriosApi.getCuriosHelper().getCurio(backpack).ifPresent(curio -> CuriosApi.getCuriosHelper().getCuriosHandler(player).ifPresent(handler ->
                    {
                        Map<String, ICurioStacksHandler> curios = handler.getCurios();
                        for(Map.Entry<String, ICurioStacksHandler> entry : curios.entrySet())
                        {
                            IDynamicStackHandler stackHandler = entry.getValue().getStacks();
                            for(int i = 0; i < stackHandler.getSlots(); i++)
                            {
                                ItemStack present = stackHandler.getStackInSlot(i);
                                Set<String> tags = CuriosApi.getCuriosHelper().getCurioTags(backpack.getItem());
                                String id = entry.getKey();

                                if(present.isEmpty() && ((tags.contains(id) || tags.contains(SlotTypePreset.CURIO.getIdentifier()))
                                        || (!tags.isEmpty() && id.equals(SlotTypePreset.CURIO.getIdentifier()))) && curio.canEquip(id, player))
                                {
                                    Direction bagDirection = world.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                                    if(world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState()))
                                    {
                                        stackHandler.setStackInSlot(i, backpack.copy());
                                        player.level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.2F) * 0.7F);
                                        player.swing(Hand.MAIN_HAND, true);

                                        if(tile.isSleepingBagDeployed())
                                        {
                                            world.setBlockAndUpdate(pos.relative(bagDirection), Blocks.AIR.defaultBlockState());
                                            world.setBlockAndUpdate(pos.relative(bagDirection).relative(bagDirection), Blocks.AIR.defaultBlockState());
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                    }));
                }
            }
        }

        //Wash colored backpack in cauldron
        if(event.getWorld().isClientSide || event.getPlayer().isCrouching()) return;

        if(stack.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get())
        {
            BlockState blockState = event.getWorld().getBlockState(event.getPos());

            if(BackpackDyeRecipe.hasColor(stack) && blockState.getBlock() instanceof CauldronBlock)
            {
                if(blockState.getValue(CauldronBlock.LEVEL) > 0)
                {
                    stack.getTag().remove("Color");
                    ((CauldronBlock)blockState.getBlock()).setWaterLevel(event.getWorld(), event.getPos(), blockState, blockState.getValue(CauldronBlock.LEVEL) - 1);
                    event.getWorld().playSound(null, event.getPos().getX(), event.getPos().getY(), event.getPos().getY(), SoundEvents.BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            }
        }
    }

    @SubscribeEvent
    public static void blockBlazeProjectile(ProjectileImpactEvent.Fireball event)
    {
        if(TravelersBackpackConfig.enableBackpackAbilities)
        {
            BackpackAbilities.blazeAbility(event);
        }
    }

    @SubscribeEvent
    public static void livingChangeTarget(LivingSetAttackTargetEvent event)
    {
        if(TravelersBackpackConfig.enableBackpackAbilities)
        {
            BackpackAbilities.ghastAbility(event);
            BackpackAbilities.pumpkinAbility(event);
        }
    }

    @SubscribeEvent
    public static void onHit(AttackEntityEvent event)
    {
        if(TravelersBackpackConfig.enableBackpackAbilities)
        {
            BackpackAbilities.beeAbility(event);
        }
    }

    @SubscribeEvent
    public static void onExpPickup(PlayerXpEvent.PickupXp event)
    {
        if(TravelersBackpackConfig.enableBackpackAbilities)
        {
            BackpackAbilities.ABILITIES.lapisAbility(event.getPlayer());
        }
    }

    @SubscribeEvent
    public static void onItemEntityJoin(EntityJoinWorldEvent event)
    {
        if(event.getEntity() instanceof LivingEntity && TravelersBackpackConfig.spawnEntitiesWithBackpack)
        {
            LazyOptional<IEntityTravelersBackpack> cap = CapabilityUtils.getEntityCapability((LivingEntity)event.getEntity());

            if(cap.isPresent())
            {
                IEntityTravelersBackpack travelersBackpack = cap.resolve().get();

                if(!travelersBackpack.hasWearable() && TimeUtils.randomInBetweenInclusive(event.getWorld().getRandom(), 0, TravelersBackpackConfig.spawnChance) == 0)
                {
                    boolean isNether = event.getEntity().getType() == EntityType.PIGLIN || event.getEntity().getType() == EntityType.WITHER_SKELETON;
                    Random rand = event.getWorld().getRandom();
                    ItemStack backpack = isNether ?
                            ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES.get(TimeUtils.randomInBetweenInclusive(rand, 0, ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES.size() - 1)).getDefaultInstance() :
                            ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.get(TimeUtils.randomInBetweenInclusive(rand, 0, ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.size() - 1)).getDefaultInstance();

                    backpack.getOrCreateTag().putInt("SleepingBagColor", DyeColor.values()[TimeUtils.randomInBetweenInclusive(rand, 0, DyeColor.values().length - 1)].getId());

                    travelersBackpack.setWearable(backpack);
                    travelersBackpack.synchronise();
                }
            }
        }

        if(!(event.getEntity() instanceof ItemEntity) || !TravelersBackpackConfig.invulnerableBackpack) return;

        if(((ItemEntity)event.getEntity()).getItem().getItem() instanceof TravelersBackpackItem)
        {
            ((ItemEntity)event.getEntity()).setExtendedLifetime();
            event.getEntity().setInvulnerable(true);
        }
    }

    @SubscribeEvent
    public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof PlayerEntity)
        {
            final TravelersBackpackWearable travelersBackpack = new TravelersBackpackWearable((PlayerEntity)event.getObject());
            event.addCapability(TravelersBackpackCapability.ID, TravelersBackpackCapability.createProvider(travelersBackpack));
        }

        if(event.getObject() instanceof LivingEntity)
        {
            if(Reference.COMPATIBLE_TYPE_ENTRIES.contains(((LivingEntity)event.getObject()).getType()))
            {
                final TravelersBackpackEntityWearable travelersBackpack = new TravelersBackpackEntityWearable((LivingEntity)event.getObject());
                event.addCapability(TravelersBackpackEntityCapability.ID, TravelersBackpackEntityCapability.createProvider(travelersBackpack));
            }
        }
    }

    @SubscribeEvent
    public static void playerDeath(LivingDeathEvent event)
    {
        if(event.getEntity() instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity)event.getEntity();

            if(TravelersBackpackConfig.enableBackpackAbilities && BackpackAbilities.creeperAbility(event))
            {
                return;
            }

            if(CapabilityUtils.isWearingBackpack(player))
            {
                if(!player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY))
                {
                    BackpackUtils.onPlayerDeath(player.level, player, CapabilityUtils.getWearingBackpack(player));
                }
                CapabilityUtils.synchronise((PlayerEntity)event.getEntity());
            }
        }

        if(Reference.COMPATIBLE_TYPE_ENTRIES.contains(event.getEntityLiving().getType()))
        {
            if(CapabilityUtils.isWearingBackpack(event.getEntityLiving()))
            {
                event.getEntity().spawnAtLocation(CapabilityUtils.getWearingBackpack(event.getEntityLiving()));
            }
        }
    }

    @SubscribeEvent
    public static void playerClone(final PlayerEvent.Clone event)
    {
        CapabilityUtils.getCapability(event.getOriginal()).ifPresent(oldTravelersBackpack ->
                CapabilityUtils.getCapability(event.getPlayer()).ifPresent(newTravelersBackpack ->
                {
                    newTravelersBackpack.setWearable(oldTravelersBackpack.getWearable());
                    newTravelersBackpack.setContents(oldTravelersBackpack.getWearable());
                }));
    }

    @SubscribeEvent
    public static void playerChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event)
    {
        CapabilityUtils.synchronise(event.getPlayer());
    }

    @SubscribeEvent
    public static void playerJoin(final PlayerEvent.PlayerLoggedInEvent event)
    {
        CapabilityUtils.synchronise(event.getPlayer());
    }

    @SubscribeEvent
    public static void entityJoin(EntityJoinWorldEvent event)
    {
        if(event.getEntity() instanceof PlayerEntity)
        {
            CapabilityUtils.synchronise((PlayerEntity)event.getEntity());
        }
    }

    @SubscribeEvent
    public static void playerTracking(final PlayerEvent.StartTracking event)
    {
        if(event.getTarget() instanceof PlayerEntity && !event.getTarget().level.isClientSide)
        {
            ServerPlayerEntity target = (ServerPlayerEntity)event.getTarget();

            CapabilityUtils.getCapability(target).ifPresent(c -> TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)event.getPlayer()),
                    new CSyncCapabilityPacket(CapabilityUtils.getWearingBackpack(target).save(new CompoundNBT()), target.getId(), true)));
        }

        if(Reference.COMPATIBLE_TYPE_ENTRIES.contains(event.getTarget().getType()) && !event.getTarget().level.isClientSide)
        {
            LivingEntity target = (LivingEntity)event.getTarget();

            CapabilityUtils.getEntityCapability(target).ifPresent(c -> TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)event.getEntity()),
                    new CSyncCapabilityPacket(CapabilityUtils.getWearingBackpack(target).save(new CompoundNBT()), target.getId(), false)));
        }
    }

    /**
     * Ability removal for attribute modifiers
     */
    private static boolean checkAbilitiesForRemoval = true;

    @SubscribeEvent
    public static void playerTick(final TickEvent.PlayerTickEvent event)
    {
        if(TravelersBackpackConfig.enableBackpackAbilities && event.phase == TickEvent.Phase.END && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, CapabilityUtils.getWearingBackpack(event.player)))
        {
            TravelersBackpackInventory.abilityTick(event.player);
            if(!checkAbilitiesForRemoval && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_REMOVAL_LIST, CapabilityUtils.getWearingBackpack(event.player))) checkAbilitiesForRemoval = true;
        }

        if(checkAbilitiesForRemoval && event.phase == TickEvent.Phase.END && !event.player.level.isClientSide && (!CapabilityUtils.isWearingBackpack(event.player) || !TravelersBackpackConfig.enableBackpackAbilities))
        {
            BackpackAbilities.ABILITIES.armorAbilityRemovals(event.player);
            checkAbilitiesForRemoval = false;
        }
    }

    private static long nextBackpackCountCheck = 0;
    private static final int BACKPACK_COUNT_CHECK_COOLDOWN = 100;

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END || !TravelersBackpackConfig.tooManyBackpacksSlowness || nextBackpackCountCheck > event.world.getGameTime())
        {
            return;
        }
        nextBackpackCountCheck = event.world.getGameTime() + BACKPACK_COUNT_CHECK_COOLDOWN;

        event.world.players().forEach(player ->
        {
            AtomicInteger numberOfBackpacks = checkBackpacksForSlowness(player);
            if(numberOfBackpacks.get() == 0) return;

            int maxNumberOfBackpacks = TravelersBackpackConfig.maxNumberOfBackpacks;
            if(numberOfBackpacks.get() > maxNumberOfBackpacks)
            {
                int numberOfSlownessLevels = Math.min(10, (int) Math.ceil((numberOfBackpacks.get() - maxNumberOfBackpacks) * TravelersBackpackConfig.slownessPerExcessedBackpack));
                player.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, BACKPACK_COUNT_CHECK_COOLDOWN * 2, numberOfSlownessLevels - 1, false, false));
            }
        });
    }

    public static AtomicInteger checkBackpacksForSlowness(PlayerEntity player)
    {
        AtomicInteger atomic = new AtomicInteger(0);

        for(int i = 0; i < player.inventory.items.size() + 1; i++)
        {
            if(i != 36)
            {
                if(player.inventory.items.get(i).getItem() instanceof TravelersBackpackItem)
                {
                    atomic.incrementAndGet();
                }
            }
            else
            {
                if(player.inventory.offhand.get(0).getItem() instanceof TravelersBackpackItem)
                {
                    atomic.incrementAndGet();
                }
            }
        }
        return atomic;
    }

    @SubscribeEvent
    public static void registerCommands(final RegisterCommandsEvent event)
    {
        new AccessBackpackCommand(event.getDispatcher());
        new RestoreBackpackCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void explosionDetonate(final ExplosionEvent.Detonate event)
    {
        for(int i = 0; i < event.getAffectedEntities().size(); i++)
        {
            Entity entity = event.getAffectedEntities().get(i);

            if(entity instanceof ItemEntity && ((ItemEntity)entity).getItem().getItem() instanceof TravelersBackpackItem)
            {
                event.getAffectedEntities().remove(i);
            }
        }
    }

    @SubscribeEvent
    public static void onLootLoad(final LootTableLoadEvent event)
    {
        if(TravelersBackpackConfig.enableLoot)
        {
            if(event.getName().equals(new ResourceLocation("chests/abandoned_mineshaft")))
            {
                ResourceLocation bat = new ResourceLocation(TravelersBackpack.MODID, "chests/bat");
                event.getTable().addPool(LootPool.lootPool().name("abandoned_mineshaft_bat").add(TableLootEntry.lootTableReference(bat)).build());
            }

            if(event.getName().equals(new ResourceLocation("chests/village/village_armorer")))
            {
                ResourceLocation iron_golem = new ResourceLocation(TravelersBackpack.MODID, "chests/iron_golem");
                event.getTable().addPool(LootPool.lootPool().name("village_armorer_iron_golem").add(TableLootEntry.lootTableReference(iron_golem)).build());
            }
        }
    }

    @SubscribeEvent
    public static void addVillagerTrade(final VillagerTradesEvent event)
    {
        if(event.getType() == VillagerProfession.LIBRARIAN)
        {
            event.getTrades().get(5).add(new BackpackVillagerTrade());
        }
    }

    private static class BackpackVillagerTrade implements VillagerTrades.ITrade
    {
        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random)
        {
            return new MerchantOffer(new ItemStack(Items.EMERALD, random.nextInt(64) + 48), new ItemStack(ModItems.VILLAGER_TRAVELERS_BACKPACK.get().getItem(), 1), 1, 5, 0.5F);
        }
    }
}