package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.advancements.ActionTypeTrigger;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.TravelersBackpackCapability;
import com.tiviacz.travelersbackpack.capability.TravelersBackpackWearable;
import com.tiviacz.travelersbackpack.commands.AccessCommand;
import com.tiviacz.travelersbackpack.commands.ClearCommand;
import com.tiviacz.travelersbackpack.commands.RestoreCommand;
import com.tiviacz.travelersbackpack.commands.UnpackCommand;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.BackpackManager;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.pickup.AutoPickupUpgrade;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.items.upgrades.TanksUpgradeItem;
import com.tiviacz.travelersbackpack.network.ClientboundSendMessagePacket;
import com.tiviacz.travelersbackpack.network.ClientboundSyncCapabilityPacket;
import com.tiviacz.travelersbackpack.network.SupporterBadgePacket;
import com.tiviacz.travelersbackpack.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.server.command.ConfigCommand;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NeoForgeEventHandler {
    @SubscribeEvent
    public static void playerSetSpawn(PlayerSetSpawnEvent event) {
        Level level = event.getEntity().level();
        if(event.getNewSpawn() != null) {
            Block block = level.getBlockState(event.getNewSpawn()).getBlock();
            if(!level.isClientSide && block instanceof SleepingBagBlock && !event.isForced()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void playerWakeUp(PlayerWakeUpEvent event) {
        if(!TravelersBackpackConfig.SERVER.backpackSettings.quickSleepingBag.get()) {
            return;
        }
        BlockPos pos = event.getEntity().blockPosition();
        Level level = event.getEntity().level();
        if(level.getBlockState(pos).getBlock() instanceof SleepingBagBlock) {
            BlockState headPart = level.getBlockState(pos);
            if(headPart.hasProperty(SleepingBagBlock.CAN_DROP) && headPart.getValue(SleepingBagBlock.CAN_DROP)) {
                return;
            }
            BlockPos backpackPos = pos.relative(headPart.getValue(SleepingBagBlock.FACING).getOpposite(), 2);
            if(!(level.getBlockState(backpackPos).getBlock() instanceof TravelersBackpackBlock)) {
                if(!level.isClientSide) {
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();

        //Quick Unequip
        if(TravelersBackpackConfig.SERVER.backpackSettings.rightClickUnequip.get() && !TravelersBackpack.enableIntegration()) {
            if(CapabilityUtils.isWearingBackpack(player)) {
                if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
                    ItemStack backpackStack = CapabilityUtils.getWearingBackpack(player).copy();
                    UseOnContext context = new UseOnContext(level, player, hand, backpackStack, event.getHitVec());
                    boolean quickPickupFlag = level.getBlockState(pos).getBlock() instanceof TravelersBackpackBlock;

                    if(!quickPickupFlag && backpackStack.getItem() instanceof TravelersBackpackItem item) {
                        if(item.place(new BlockPlaceContext(context)) == InteractionResult.sidedSuccess(level.isClientSide)) {
                            player.swing(hand, true);
                            //level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.05F, (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);
                            if(!level.isClientSide) {
                                CapabilityUtils.getCapability(player).ifPresent(data -> {
                                    data.remove();
                                    data.synchronise();
                                });
                            }
                            event.setCanceled(true);
                            event.setCancellationResult(InteractionResult.SUCCESS);
                            return;
                        }
                    }
                }
            }
        }

        //Change Sleeping Bag
        if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().is(ModTags.SLEEPING_BAGS) && level.getBlockEntity(pos) instanceof BackpackBlockEntity blockEntity) {
            ItemStack oldSleepingBag = BackpackBlockEntity.getProperSleepingBag(blockEntity.getWrapper().getSleepingBagColor()).getBlock().asItem().getDefaultInstance();
            blockEntity.getWrapper().setSleepingBagColor(ShapedBackpackRecipe.getProperColor(player.getMainHandItem().getItem()));

            if(!level.isClientSide) {
                if(player instanceof ServerPlayer serverPlayer) {
                    ActionTypeTrigger.INSTANCE.trigger(serverPlayer, ActionTypeTrigger.CHANGE_SLEEPING_BAG);
                }
                Containers.dropItemStack(level, pos.getX(), pos.above().getY(), pos.getZ(), oldSleepingBag);
                player.getMainHandItem().shrink(1);
            }
            level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);

            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        //Remove custom backpack design (go back to standard)
        if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().is(Items.SHEARS) && level.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
            if(!backpackBlockEntity.getWrapper().getBackpackStack().is(ModItems.STANDARD_TRAVELERS_BACKPACK.get().asItem())) {
                ItemStack standardBackpack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK.get(), 1);
                backpackBlockEntity.toItemStack(standardBackpack);
                Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);
                if(!level.isClientSide && level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())) {
                    if(player instanceof ServerPlayer serverPlayer) {
                        ActionTypeTrigger.INSTANCE.trigger(serverPlayer, ActionTypeTrigger.REVERT_CUSTOM_BACKPACK);
                    }
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), standardBackpack);
                    backpackBlockEntity.removeSleepingBag(level, direction);
                    level.playSound(null, backpackBlockEntity.getBlockPos(), SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.gameEvent(GameEvent.SHEAR, player);
                    player.getMainHandItem().hurtAndBreak(1, player, consumer -> consumer.broadcastBreakEvent(hand));
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }
        }

        if(player.isShiftKeyDown() && player.getMainHandItem().getItem() == ModItems.BLANK_UPGRADE.get() && level.getBlockEntity(pos) instanceof BackpackBlockEntity blockEntity) {
            NonNullList<ItemStack> list = NonNullList.create();
            for(int i = 0; i < blockEntity.getWrapper().getStorage().getSlots(); i++) {
                ItemStack stackInSlot = blockEntity.getWrapper().getStorage().getStackInSlot(i);
                if(!stackInSlot.isEmpty()) {
                    list.add(stackInSlot);
                }
            }
            for(int i = 0; i < blockEntity.getWrapper().getTools().getSlots(); i++) {
                ItemStack stackInSlot = blockEntity.getWrapper().getTools().getStackInSlot(i);
                if(!stackInSlot.isEmpty()) {
                    list.add(stackInSlot);
                }
            }
            for(int i = 0; i < blockEntity.getWrapper().getUpgrades().getSlots(); i++) {
                ItemStack stackInSlot = blockEntity.getWrapper().getUpgrades().getStackInSlot(i);
                if(!stackInSlot.isEmpty()) {
                    list.add(stackInSlot);
                }
            }
            int tier = NbtHelper.getOrDefault(blockEntity.getWrapper().getBackpackStack(), ModDataHelper.TIER, 0);
            if(tier != 0) {
                list.addAll(getUpgrades(tier));
            }

            //Add backpack
            Item backpackItem = blockEntity.getWrapper().getBackpackStack().getItem();
            list.add(backpackItem.getDefaultInstance());

            if(!level.isClientSide) {
                Containers.dropContents(level, pos.above(), list);
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        //Quick Equip
        if(TravelersBackpackConfig.SERVER.backpackSettings.rightClickEquip.get() && level.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
            if(player.isShiftKeyDown() && !CapabilityUtils.isWearingBackpack(player) && !TravelersBackpack.enableIntegration()) {
                //Prioritize placing block
                if(player.getItemInHand(hand).getItem() instanceof BlockItem) {
                    return;
                }
                ItemStack backpack = new ItemStack(level.getBlockState(pos).getBlock(), 1).copy();
                backpackBlockEntity.toItemStack(backpack);
                Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                if(!level.isClientSide && level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())) {
                    CapabilityUtils.equipBackpack(player, backpack);
                    backpackBlockEntity.removeSleepingBag(level, direction);

                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    return;
                }
            }
        }

        //Quick Pick-Up
        if(level.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
            if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
                ItemStack backpack = new ItemStack(level.getBlockState(pos).getBlock(), 1).copy();
                backpackBlockEntity.toItemStack(backpack);
                Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                if(!level.isClientSide && level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, backpack);
                    backpackBlockEntity.removeSleepingBag(level, direction);
                    level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);

                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }
            }
        }
    }

    public static final List<Supplier<Item>> UPGRADES = Arrays.asList(
            () -> ModItems.IRON_TIER_UPGRADE.get(),
            () -> ModItems.GOLD_TIER_UPGRADE.get(),
            () -> ModItems.DIAMOND_TIER_UPGRADE.get(),
            () -> ModItems.NETHERITE_TIER_UPGRADE.get());

    public static NonNullList<ItemStack> getUpgrades(int tier) {
        NonNullList<ItemStack> list = NonNullList.create();

        for(int i = 0; i < tier; i++) {
            list.add(UPGRADES.get(i).get().getDefaultInstance());
        }

        return list;
    }

    @SubscribeEvent
    public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player player) {
            final TravelersBackpackWearable travelersBackpack = new TravelersBackpackWearable(player);
            event.addCapability(TravelersBackpackCapability.ID, TravelersBackpackCapability.createProvider(travelersBackpack));
        }
    }

    @SubscribeEvent
    public static void onEnderManAngerEvent(EnderManAngerEvent event) {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get()) {
            BackpackAbilities.pumpkinAbility(event);
        }
    }

    @SubscribeEvent
    public static void blockBlazeProjectile(ProjectileImpactEvent event) {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get()) {
            BackpackAbilities.blazeAbility(event);
        }
    }

    @SubscribeEvent
    public static void livingChangeTarget(LivingChangeTargetEvent event) {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get()) {
            BackpackAbilities.ghastAbility(event);
        }
    }

    @SubscribeEvent
    public static void onHit(AttackEntityEvent event) {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get()) {
            BackpackAbilities.beeAbility(event);
            BackpackAbilities.witherAbility(event);
            BackpackAbilities.wardenAbility(event);
        }
    }

    @SubscribeEvent
    public static void onExpPickup(PlayerXpEvent.PickupXp event) {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get()) {
            BackpackAbilities.ABILITIES.lapisAbility(event.getEntity(), event.getOrb());
        }
    }

    @SubscribeEvent
    public static void playerDeath(LivingDeathEvent event) {
        if(event.getEntity() instanceof Player player) {
            if(BackpackAbilities.ABILITIES.checkBackpack(player, ModItems.CREEPER_TRAVELERS_BACKPACK.get())) {
                if(BackpackAbilities.creeperAbility(event)) {
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDrops(LivingDropsEvent event) {
        if(event.getEntity() instanceof Player player) {
            //Use different placing logic if no integration is loaded
            if(CapabilityUtils.isWearingBackpack(player)) {
                //If integration loaded - just remove backpack from component, rest is handled by integration
                if(TravelersBackpack.enableIntegration()) {
                    //Create backup
                    if(!player.level().isClientSide)
                        BackpackManager.addBackpack((ServerPlayer)player, CapabilityUtils.getWearingBackpack(player));
                    return;
                }

                //Continue if no integration detected
                //Keep backpack on with Keep Inventory game rule
                if(player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;

                ItemStack stack = CapabilityUtils.getWearingBackpack(player);

                if(BackpackDeathHelper.onPlayerDrops(player.level(), player, stack)) {
                    if(player.level().isClientSide) return;

                    ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack);
                    itemEntity.setDefaultPickUpDelay();

                    PacketDistributorHelper.sendToPlayer((ServerPlayer)player, new ClientboundSendMessagePacket(true, player.blockPosition()));
                    LogHelper.info("There's no space for backpack. Dropping backpack item at" + " X: " + player.blockPosition().getX() + " Y: " + player.getY() + " Z: " + player.blockPosition().getZ());

                    event.getDrops().add(itemEntity);

                    CapabilityUtils.getCapability(player).ifPresent(attachment -> {
                        attachment.remove();
                        attachment.synchronise();
                    });
                }
            }
        }

        if(Reference.ALLOWED_TYPE_ENTRIES.contains(event.getEntity().getType())) {
            if(event.getEntity().getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof TravelersBackpackItem) {
                if(!(event.getSource().getEntity() instanceof Player)) return;

                ItemEntity itemEntity = new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity().getItemBySlot(EquipmentSlot.CHEST));
                event.getDrops().add(itemEntity);
            }
        }
    }

    @SubscribeEvent
    public static void playerClone(final PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        oldPlayer.revive();
        CapabilityUtils.getCapability(oldPlayer)
                .ifPresent(oldTravelersBackpack -> CapabilityUtils.getCapability(event.getEntity())
                        .ifPresent(newTravelersBackpack -> newTravelersBackpack.equipBackpack(oldTravelersBackpack.getBackpack())));
    }

    @SubscribeEvent
    public static void playerChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
        CapabilityUtils.synchronise(event.getEntity());
    }

    @SubscribeEvent
    public static void playerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
        CapabilityUtils.synchronise(event.getEntity());
    }

    @SubscribeEvent
    public static void playerJoin(final PlayerEvent.PlayerRespawnEvent event) {
        CapabilityUtils.synchronise(event.getEntity());
    }

    @SubscribeEvent
    public static void entityJoin(EntityJoinLevelEvent event) {
        if(event.getEntity() instanceof Player player) {
            CapabilityUtils.synchronise(player);

            //Synchronise supporter badge visibility
            if(player.level().isClientSide) {
                boolean badgeVisibility = TravelersBackpackConfig.CLIENT.showSupporterBadge.get();
                PacketDistributorHelper.sendToServer(new SupporterBadgePacket.Serverbound(badgeVisibility));
            }
        }
    }

    @SubscribeEvent
    public static void finalizeSpawnEvent(MobSpawnEvent.FinalizeSpawn event) {
        if(TravelersBackpackConfig.SERVER.world.spawnEntitiesWithBackpack.get()) {
            if(event.getEntity().getItemBySlot(EquipmentSlot.CHEST).isEmpty() && Reference.ALLOWED_TYPE_ENTRIES.contains(event.getEntity().getType())) {
                if(event.getLevel().getRandom().nextFloat() < TravelersBackpackConfig.SERVER.world.chance.get()) {
                    boolean isNether = event.getEntity().getType() == EntityType.PIGLIN || event.getEntity().getType() == EntityType.WITHER_SKELETON;
                    RandomSource rand = event.getLevel().getRandom();
                    ItemStack backpack = isNether ?
                            ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES.get(rand.nextIntBetweenInclusive(0, ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES.size() - 1)).getDefaultInstance() :
                            ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.get(rand.nextIntBetweenInclusive(0, ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.size() - 1)).getDefaultInstance();

                    NbtHelper.set(backpack, ModDataHelper.SLEEPING_BAG_COLOR, DyeColor.values()[rand.nextIntBetweenInclusive(0, DyeColor.values().length - 1)].getId());
                    boolean flag = false;
                    if(rand.nextFloat() > 0.5F) {
                        NbtHelper.set(backpack, ModDataHelper.STARTER_UPGRADES, List.of(ModItems.TANKS_UPGRADE.get().getDefaultInstance()));
                        flag = true;
                    }
                    if(rand.nextFloat() > 0.25F) {
                        NbtHelper.set(backpack, ModDataHelper.COLOR, rand.nextInt());
                    }
                    if(flag) {
                        NbtHelper.set(backpack, ModDataHelper.RENDER_INFO, TanksUpgradeItem.writeToRenderData());
                    } else {
                        NbtHelper.set(backpack, ModDataHelper.RENDER_INFO, RenderInfo.EMPTY);
                    }
                    event.getEntity().setItemSlot(EquipmentSlot.CHEST, backpack);
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerTracking(final PlayerEvent.StartTracking event) {
        if(event.getTarget() instanceof ServerPlayer target && !target.level().isClientSide) {
            CapabilityUtils.getCapability(target).ifPresent(data ->
                    PacketDistributorHelper.sendToPlayer((ServerPlayer)event.getEntity(), new ClientboundSyncCapabilityPacket(target.getId(), data.getBackpack())));
        }
    }

    private static boolean checkAbilitiesForRemoval = true;

    @SubscribeEvent
    public static void playerTick(final TickEvent.PlayerTickEvent event) {
        if(event.phase != TickEvent.Phase.END) return;

        if(CapabilityUtils.isWearingBackpack(event.player)) {
            BackpackWrapper.tick(CapabilityUtils.getWearingBackpack(event.player), event.player, false);
        }
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get() && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, CapabilityUtils.getWearingBackpack(event.player))) {
            if(!checkAbilitiesForRemoval && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_REMOVAL_LIST, CapabilityUtils.getWearingBackpack(event.player)))
                checkAbilitiesForRemoval = true;
        }
        if(checkAbilitiesForRemoval && !event.player.level().isClientSide && (!CapabilityUtils.isWearingBackpack(event.player) || !TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get())) {
            runAbilitiesRemoval(event.player);
            checkAbilitiesForRemoval = false;
        }
    }

    public static void runAbilitiesRemoval(Player player) {
        BackpackAbilities.ABILITIES.armorAbilityRemovals(player);
    }

    private static int nextSupportersFetch = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if(event.phase != TickEvent.Phase.END) return;

        if(nextSupportersFetch > event.getServer().getTickCount()) {
            return;
        }
        nextSupportersFetch = event.getServer().getTickCount() + (20 * 60 * 60); //Fetch every hour
        Supporters.updateSupporters();
    }

    private static long nextBackpackCountCheck = 0;
    private static final int BACKPACK_COUNT_CHECK_COOLDOWN = 100;

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if(event.phase != TickEvent.Phase.END) return;

        if(!TravelersBackpackConfig.SERVER.slownessDebuff.tooManyBackpacksSlowness.get() || nextBackpackCountCheck > event.level.getGameTime()) {
            return;
        }
        nextBackpackCountCheck = event.level.getGameTime() + BACKPACK_COUNT_CHECK_COOLDOWN;

        event.level.players().forEach(player -> {
            if(player.isCreative() || player.isSpectator()) return;

            AtomicInteger numberOfBackpacks = checkBackpacksForSlowness(player);
            if(numberOfBackpacks.get() == 0) return;

            int maxNumberOfBackpacks = TravelersBackpackConfig.SERVER.slownessDebuff.maxNumberOfBackpacks.get();
            if(numberOfBackpacks.get() > maxNumberOfBackpacks) {
                int numberOfSlownessLevels = Math.min(10, (int)Math.ceil((numberOfBackpacks.get() - maxNumberOfBackpacks) * TravelersBackpackConfig.SERVER.slownessDebuff.slownessPerExcessedBackpack.get()));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, BACKPACK_COUNT_CHECK_COOLDOWN * 2, numberOfSlownessLevels - 1, false, false));
            }
        });
    }

    public static AtomicInteger checkBackpacksForSlowness(Player player) {
        AtomicInteger atomic = new AtomicInteger(0);
        for(int i = 0; i < player.getInventory().items.size(); i++) {
            if(player.getInventory().items.get(i).getItem() instanceof TravelersBackpackItem) {
                atomic.incrementAndGet();
            }
        }
        if(player.getInventory().offhand.get(0).getItem() instanceof TravelersBackpackItem) {
            atomic.incrementAndGet();
        }
        return atomic;
    }

    @SubscribeEvent
    public static void registerCommands(final RegisterCommandsEvent event) {
        new AccessCommand(event.getDispatcher());
        new RestoreCommand(event.getDispatcher());
        new ClearCommand(event.getDispatcher());
        new UnpackCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void addVillagerTrade(final VillagerTradesEvent event) {
        if(TravelersBackpackConfig.COMMON.enableVillagerTrade.get() && event.getType() == VillagerProfession.LIBRARIAN) {
            event.getTrades().get(3).add((trader, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, random.nextInt(64) + 48),
                    new ItemStack(ModItems.VILLAGER_TRAVELERS_BACKPACK.get().asItem(), 1), 1, 50, 0.5F));
        }
    }

    /**
     * UPGRADES
     */

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        ItemEntity itemEntity = event.getItem();
        if(itemEntity.getItem().isEmpty() || itemEntity.pickupDelay > 0) {
            return;
        }

        Player player = event.getEntity();
        Level level = player.level();

        if(CapabilityUtils.isWearingBackpack(player)) {
            BackpackWrapper wrapper = CapabilityUtils.getBackpackWrapper(player);
            wrapper.getUpgradeManager().getUpgrade(AutoPickupUpgrade.class).ifPresent(pickupUpgrade -> {
                if(pickupUpgrade.canPickup(itemEntity.getItem()) && pickupUpgrade.tryPickup(itemEntity, level, player.blockPosition())) {
                    event.setResult(Event.Result.ALLOW);
                }});
            /*if(wrapper.getUpgradeManager().getUpgrade(AutoPickupUpgrade.class).isPresent() && wrapper.getUpgradeManager().getUpgrade(AutoPickupUpgrade.class).get().canPickup(itemEntity.getItem())) {
                ItemStack remainingStack = ItemHandlerHelper.insertItemStacked(wrapper.getStorageForInputOutput(), itemEntity.getItem(), false);
                if(remainingStack != itemEntity.getItem()) {
                    level.playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, (level.random.nextFloat() - level.random.nextFloat()) * 1.4F + 2.0F);
                    itemEntity.setItem(remainingStack);
                    event.setResult(Event.Result.ALLOW);
                }
            }*/
        }
    }
}