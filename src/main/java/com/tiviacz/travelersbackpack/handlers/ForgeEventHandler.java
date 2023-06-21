package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.capability.TravelersBackpackCapability;
import com.tiviacz.travelersbackpack.capability.TravelersBackpackWearable;
import com.tiviacz.travelersbackpack.capability.entity.IEntityTravelersBackpack;
import com.tiviacz.travelersbackpack.capability.entity.TravelersBackpackEntityCapability;
import com.tiviacz.travelersbackpack.capability.entity.TravelersBackpackEntityWearable;
import com.tiviacz.travelersbackpack.commands.AccessBackpackCommand;
import com.tiviacz.travelersbackpack.commands.ClearBackpackCommand;
import com.tiviacz.travelersbackpack.commands.RestoreBackpackCommand;
import com.tiviacz.travelersbackpack.commands.UnpackBackpackCommand;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.recipes.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.SleepingBagItem;
import com.tiviacz.travelersbackpack.items.TierUpgradeItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ClientboundSyncCapabilityPacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.EnderManAngerEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.command.ConfigCommand;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler
{
    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event)
    {
        event.register(ITravelersBackpack.class);
        event.register(IEntityTravelersBackpack.class);
    }

    @SubscribeEvent
    public static void playerSetSpawn(PlayerSetSpawnEvent event)
    {
        Level level = event.getEntity().level;

        if(event.getNewSpawn() != null)
        {
            Block block = level.getBlockState(event.getNewSpawn()).getBlock();

            if(!level.isClientSide && block instanceof SleepingBagBlock && !TravelersBackpackConfig.enableSleepingBagSpawnPoint)
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void playerRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        ItemStack stack = event.getItemStack();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();

        if(player.isShiftKeyDown() && event.getHand() == InteractionHand.MAIN_HAND && player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SleepingBagItem item && level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity)
        {
            ItemStack oldSleepingBag = blockEntity.getProperSleepingBag(blockEntity.getSleepingBagColor()).getBlock().asItem().getDefaultInstance();
            blockEntity.setSleepingBagColor(ShapedBackpackRecipe.getProperColor(item));
            if(!level.isClientSide)
            {
                Containers.dropItemStack(level, pos.getX(), pos.above().getY(), pos.getZ(), oldSleepingBag);
                stack.shrink(1);
            }
            player.level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, (1.0F + (player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.2F) * 0.7F);
            player.swing(InteractionHand.MAIN_HAND, true);
            return;
        }

        if(player.isShiftKeyDown() && player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == ModItems.BLANK_UPGRADE.get() && level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity)
        {
            if(blockEntity.getTier() != Tiers.LEATHER)
            {
                int storageSlots = blockEntity.getTier().getStorageSlots();
                NonNullList<ItemStack> list = NonNullList.create();

                for(int i = 0; i < 9; i++)
                {
                    ItemStack stackInSlot = blockEntity.getCraftingGridHandler().getStackInSlot(i);

                    if(!stackInSlot.isEmpty())
                    {
                        list.add(stackInSlot);
                        blockEntity.getCraftingGridHandler().setStackInSlot(i, ItemStack.EMPTY);
                    }
                }

                for(int i = storageSlots - 1; i > Tiers.LEATHER.getStorageSlots() - 7; i--)
                {
                    ItemStack stackInSlot = blockEntity.getHandler().getStackInSlot(i);

                    if(!stackInSlot.isEmpty())
                    {
                        list.add(stackInSlot);
                        blockEntity.getHandler().setStackInSlot(i, ItemStack.EMPTY);
                    }
                }

                list.addAll(TierUpgradeItem.getUpgradesForTier(blockEntity.getTier()));

                if(!blockEntity.getSlotManager().getUnsortableSlots().isEmpty())
                {
                    blockEntity.getSlotManager().getUnsortableSlots().removeIf(i -> i > Tiers.LEATHER.getStorageSlots() - 7);
                }

                if(!blockEntity.getSlotManager().getMemorySlots().isEmpty())
                {
                    blockEntity.getSlotManager().getMemorySlots().removeIf(p -> p.getFirst() > Tiers.LEATHER.getStorageSlots() - 7);
                }

                int fluidAmountLeft = blockEntity.getLeftTank().isEmpty() ? 0 : blockEntity.getLeftTank().getFluidAmount();

                if(fluidAmountLeft > Tiers.LEATHER.getTankCapacity())
                {
                    blockEntity.getLeftTank().drain(fluidAmountLeft - Tiers.LEATHER.getTankCapacity(), IFluidHandler.FluidAction.EXECUTE);
                }

                int fluidAmountRight = blockEntity.getRightTank().isEmpty() ? 0 : blockEntity.getRightTank().getFluidAmount();

                if(fluidAmountRight > Tiers.LEATHER.getTankCapacity())
                {
                    blockEntity.getRightTank().drain(fluidAmountRight - Tiers.LEATHER.getTankCapacity(), IFluidHandler.FluidAction.EXECUTE);
                }

                if(!level.isClientSide)
                {
                    Containers.dropContents(level, pos.above(), list);
                }

                blockEntity.resetTier();
                player.swing(InteractionHand.MAIN_HAND, true);
                return;
            }
        }

        if(event.getLevel().isClientSide) return;

        // Equip Backpack on right click with any item in hand //#TODO CHECK
        if(TravelersBackpackConfig.enableBackpackBlockWearable && event.getLevel().getBlockState(event.getPos()).getBlock() instanceof TravelersBackpackBlock block)
        {
            if(player.isShiftKeyDown() && !CapabilityUtils.isWearingBackpack(player))
            {
                TravelersBackpackBlockEntity blockEntity = (TravelersBackpackBlockEntity)level.getBlockEntity(pos);
                ItemStack backpack = new ItemStack(block, 1);

                if(!TravelersBackpack.enableCurios())
                {
                    Direction bagDirection = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                    if(level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState()))
                    {
                        blockEntity.transferToItemStack(backpack);
                        CapabilityUtils.equipBackpack(event.getEntity(), backpack);
                        player.swing(InteractionHand.MAIN_HAND, true);

                        if(blockEntity.isSleepingBagDeployed())
                        {
                            level.setBlockAndUpdate(pos.relative(bagDirection), Blocks.AIR.defaultBlockState());
                            level.setBlockAndUpdate(pos.relative(bagDirection).relative(bagDirection), Blocks.AIR.defaultBlockState());
                        }
                        return;
                    }
                }
                else
                {
                    blockEntity.transferToItemStack(backpack);

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
                                    Direction bagDirection = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                                    if(level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState()))
                                    {
                                        stackHandler.setStackInSlot(i, backpack.copy());
                                        player.level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, (1.0F + (player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.2F) * 0.7F);
                                        player.swing(InteractionHand.MAIN_HAND, true);

                                        if(blockEntity.isSleepingBagDeployed())
                                        {
                                            level.setBlockAndUpdate(pos.relative(bagDirection), Blocks.AIR.defaultBlockState());
                                            level.setBlockAndUpdate(pos.relative(bagDirection).relative(bagDirection), Blocks.AIR.defaultBlockState());
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

        if(event.getLevel().isClientSide || event.getEntity().isShiftKeyDown()) return;

        if(stack.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get())
        {
            BlockState blockState = event.getLevel().getBlockState(event.getPos());

            if(BackpackDyeRecipe.hasColor(stack) && blockState.getBlock() instanceof LayeredCauldronBlock)
            {
                if(blockState.getValue(LayeredCauldronBlock.LEVEL) > 0)
                {
                    stack.getTag().remove("Color");
                    LayeredCauldronBlock.lowerFillLevel(blockState, event.getLevel(), event.getPos());
                    event.getLevel().playSound(null, event.getPos().getX(), event.getPos().getY(), event.getPos().getY(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEnderManAngerEvent(EnderManAngerEvent event)
    {
        if(TravelersBackpackConfig.enableBackpackAbilities)
        {
            BackpackAbilities.pumpkinAbility(event);
        }
    }

    @SubscribeEvent
    public static void blockBlazeProjectile(ProjectileImpactEvent event)
    {
        if(TravelersBackpackConfig.enableBackpackAbilities)
        {
            BackpackAbilities.blazeAbility(event);
        }
    }

    @SubscribeEvent
    public static void livingChangeTarget(LivingChangeTargetEvent event)
    {
        if(TravelersBackpackConfig.enableBackpackAbilities)
        {
            BackpackAbilities.ghastAbility(event);
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
            BackpackAbilities.ABILITIES.lapisAbility(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event)
    {
        if(event.getEntity() instanceof LivingEntity living && !event.loadedFromDisk() && TravelersBackpackConfig.spawnEntitiesWithBackpack)
        {
            LazyOptional<IEntityTravelersBackpack> cap = CapabilityUtils.getEntityCapability(living);

            if(cap.isPresent() && Reference.ALLOWED_TYPE_ENTRIES.contains(event.getEntity().getType()))
            {
                IEntityTravelersBackpack travelersBackpack = cap.resolve().get();

                if(!travelersBackpack.hasWearable() && event.getLevel().getRandom().nextInt(0, TravelersBackpackConfig.spawnChance) == 0)
                {
                    boolean isNether = living.getType() == EntityType.PIGLIN || living.getType() == EntityType.WITHER_SKELETON;
                    RandomSource rand = event.getLevel().random;
                    ItemStack backpack = isNether ?
                            ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES.get(rand.nextIntBetweenInclusive(0, ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES.size() - 1)).getDefaultInstance() :
                            ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.get(rand.nextIntBetweenInclusive(0, ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.size() - 1)).getDefaultInstance();

                    backpack.getOrCreateTag().putInt("SleepingBagColor", DyeColor.values()[rand.nextIntBetweenInclusive(0, DyeColor.values().length - 1)].getId());

                    travelersBackpack.setWearable(backpack);
                    travelersBackpack.synchronise();
                }
            }
        }

        if(!(event.getEntity() instanceof ItemEntity itemEntity) || !TravelersBackpackConfig.invulnerableBackpack) return;

        if(itemEntity.getItem().getItem() instanceof TravelersBackpackItem)
        {
            itemEntity.setUnlimitedLifetime();
            itemEntity.setInvulnerable(true);
        }
    }

    @SubscribeEvent
    public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof Player player)
        {
            final TravelersBackpackWearable travelersBackpack = new TravelersBackpackWearable(player);
            event.addCapability(TravelersBackpackCapability.ID, TravelersBackpackCapability.createProvider(travelersBackpack));
        }

        if(event.getObject() instanceof LivingEntity livingEntity)
        {
            if(Reference.ALLOWED_TYPE_ENTRIES.contains(livingEntity.getType()))
            {
                final TravelersBackpackEntityWearable travelersBackpack = new TravelersBackpackEntityWearable(livingEntity);
                event.addCapability(TravelersBackpackEntityCapability.ID, TravelersBackpackEntityCapability.createProvider(travelersBackpack));
            }
        }
    }

    @SubscribeEvent
    public static void playerDeath(LivingDeathEvent event)
    {
        if(event.getEntity() instanceof Player player)
        {
            if(CapabilityUtils.isWearingBackpack(player))
            {
                if(TravelersBackpackConfig.enableBackpackAbilities && BackpackAbilities.creeperAbility(event))
                {
                    return;
                }

                if(!player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY))
                {
                    BackpackUtils.onPlayerDeath(player.level, player, CapabilityUtils.getWearingBackpack(player));
                }
                CapabilityUtils.synchronise((Player)event.getEntity());
            }
        }

        if(Reference.ALLOWED_TYPE_ENTRIES.contains(event.getEntity().getType()))
        {
            if(CapabilityUtils.isWearingBackpack(event.getEntity()))
            {
                event.getEntity().spawnAtLocation(CapabilityUtils.getWearingBackpack(event.getEntity()));
            }
        }
    }

    @SubscribeEvent
    public static void playerClone(final PlayerEvent.Clone event)
    {
        Player oldPlayer = event.getOriginal();
        oldPlayer.revive();

        CapabilityUtils.getCapability(oldPlayer)
                .ifPresent(oldTravelersBackpack -> CapabilityUtils.getCapability(event.getEntity())
                        .ifPresent(newTravelersBackpack ->
                        {
                            newTravelersBackpack.setWearable(oldTravelersBackpack.getWearable());
                            newTravelersBackpack.setContents(oldTravelersBackpack.getWearable());
                        }));
    }

    @SubscribeEvent
    public static void playerChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event)
    {
        CapabilityUtils.synchronise(event.getEntity());
    }

    @SubscribeEvent
    public static void playerJoin(final PlayerEvent.PlayerLoggedInEvent event)
    {
        CapabilityUtils.synchronise(event.getEntity());
    }

    @SubscribeEvent
    public static void entityJoin(EntityJoinLevelEvent event)
    {
        if(event.getEntity() instanceof Player player)
        {
            CapabilityUtils.synchronise(player);
        }
    }

    @SubscribeEvent
    public static void playerTracking(final PlayerEvent.StartTracking event)
    {
        if(event.getTarget() instanceof Player && !event.getTarget().level.isClientSide)
        {
            ServerPlayer target = (ServerPlayer)event.getTarget();

            CapabilityUtils.getCapability(target).ifPresent(c -> TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()),
                    new ClientboundSyncCapabilityPacket(CapabilityUtils.getWearingBackpack(target).save(new CompoundTag()), target.getId(), true)));
        }

        if(Reference.ALLOWED_TYPE_ENTRIES.contains(event.getTarget().getType()) && !event.getTarget().level.isClientSide)
        {
            LivingEntity target = (LivingEntity)event.getTarget();

            CapabilityUtils.getEntityCapability(target).ifPresent(c -> TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)event.getEntity()),
                    new ClientboundSyncCapabilityPacket(CapabilityUtils.getWearingBackpack(target).save(new CompoundTag()), target.getId(), false)));
        }
    }

    private static boolean checkAbilitiesForRemoval = true;

    @SubscribeEvent
    public static void playerTick(final TickEvent.PlayerTickEvent event)
    {
        if(TravelersBackpackConfig.enableBackpackAbilities && event.phase == TickEvent.Phase.END && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, CapabilityUtils.getWearingBackpack(event.player)))
        {
            TravelersBackpackContainer.abilityTick(event.player);
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
    public static void onWorldTick(TickEvent.LevelTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END || !TravelersBackpackConfig.tooManyBackpacksSlowness || nextBackpackCountCheck > event.level.getGameTime())
        {
            return;
        }
        nextBackpackCountCheck = event.level.getGameTime() + BACKPACK_COUNT_CHECK_COOLDOWN;

        event.level.players().forEach(player ->
        {
            AtomicInteger numberOfBackpacks = checkBackpacksForSlowness(player);
            if(numberOfBackpacks.get() == 0) return;

            int maxNumberOfBackpacks = TravelersBackpackConfig.maxNumberOfBackpacks;
            if(numberOfBackpacks.get() > maxNumberOfBackpacks)
            {
                int numberOfSlownessLevels = Math.min(10, (int) Math.ceil((numberOfBackpacks.get() - maxNumberOfBackpacks) * TravelersBackpackConfig.slownessPerExcessedBackpack));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, BACKPACK_COUNT_CHECK_COOLDOWN * 2, numberOfSlownessLevels - 1, false, false));
            }
        });
    }

    public static AtomicInteger checkBackpacksForSlowness(Player player)
    {
        AtomicInteger atomic = new AtomicInteger(0);

        for(int i = 0; i < player.getInventory().items.size() + 1; i++)
        {
            if(i != 36)
            {
                if(player.getInventory().items.get(i).getItem() instanceof TravelersBackpackItem)
                {
                    atomic.incrementAndGet();
                }
            }
            else
            {
                if(player.getInventory().offhand.get(0).getItem() instanceof TravelersBackpackItem)
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
        new ClearBackpackCommand(event.getDispatcher());
        new UnpackBackpackCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void explosionDetonate(final ExplosionEvent.Detonate event)
    {
        for(int i = 0; i < event.getAffectedEntities().size(); i++)
        {
            Entity entity = event.getAffectedEntities().get(i);

            if(entity instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() instanceof TravelersBackpackItem)
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
                event.getTable().addPool(LootPool.lootPool().name("abandoned_mineshaft_bat").add(LootTableReference.lootTableReference(bat)).build());
            }

            if(event.getName().equals(new ResourceLocation("chests/village/village_armorer")))
            {
                ResourceLocation iron_golem = new ResourceLocation(TravelersBackpack.MODID, "chests/iron_golem");
                event.getTable().addPool(LootPool.lootPool().name("village_armorer_iron_golem").add(LootTableReference.lootTableReference(iron_golem)).build());
            }
        }
    }

    @SubscribeEvent
    public static void addVillagerTrade(final VillagerTradesEvent event)
    {
        if(TravelersBackpackConfig.enableVillagerTrade && event.getType() == VillagerProfession.LIBRARIAN)
        {
            event.getTrades().get(5).add(new BackpackVillagerTrade());
        }
    }

    private static class BackpackVillagerTrade implements VillagerTrades.ItemListing
    {
        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, RandomSource random)
        {
            return new MerchantOffer(new ItemStack(Items.EMERALD, random.nextInt(64) + 48), new ItemStack(ModItems.VILLAGER_TRAVELERS_BACKPACK.get().asItem(), 1), 1, 5, 0.5F);
        }
    }
}