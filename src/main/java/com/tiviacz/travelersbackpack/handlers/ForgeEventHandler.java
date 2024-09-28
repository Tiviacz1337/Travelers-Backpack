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
import com.tiviacz.travelersbackpack.common.BackpackManager;
import com.tiviacz.travelersbackpack.common.recipes.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.items.UpgradeItem;
import com.tiviacz.travelersbackpack.network.ClientboundSendMessagePacket;
import com.tiviacz.travelersbackpack.network.ClientboundSyncCapabilityPacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.LogHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.EnderManAngerEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.command.ConfigCommand;

import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler
{
    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event)
    {
        event.register(ITravelersBackpack.class);
        event.register(IEntityTravelersBackpack.class); //#TODO remove
    }

    @SubscribeEvent
    public static void playerSetSpawn(PlayerSetSpawnEvent event)
    {
        Level level = event.getEntity().level();

        if(event.getNewSpawn() != null)
        {
            Block block = level.getBlockState(event.getNewSpawn()).getBlock();

            if(!level.isClientSide && block instanceof SleepingBagBlock && !event.isForced())
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void playerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();

        //Quick Unequip
        if (TravelersBackpackConfig.COMMON.backpackSettings.enableBackpackRightClickUnequip.get() && !TravelersBackpack.enableCurios()) {
            if (CapabilityUtils.isWearingBackpack(player) && !level.isClientSide) {
                if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
                    ItemStack backpackStack = CapabilityUtils.getWearingBackpack(player);
                    UseOnContext context = new UseOnContext(level, player, hand, backpackStack, event.getHitVec());
                    boolean quickPickupFlag = level.getBlockState(pos).getBlock() instanceof TravelersBackpackBlock;

                    if (!quickPickupFlag && backpackStack.getItem() instanceof TravelersBackpackItem item) {
                        if (item.place(new BlockPlaceContext(context)) == InteractionResult.sidedSuccess(level.isClientSide)) {
                            player.swing(hand, true);
                            level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.05F, (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);
                            CapabilityUtils.getCapability(player).ifPresent(ITravelersBackpack::removeWearable);

                            CapabilityUtils.synchronise(player);
                            CapabilityUtils.synchroniseToOthers(player);

                            event.setCanceled(true);
                            event.setCancellationResult(InteractionResult.SUCCESS); //#TODO check with older forge
                            return;
                        }
                    }
                }
            }
        }

        if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().is(ModTags.SLEEPING_BAGS) && level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity) {
            ItemStack oldSleepingBag = blockEntity.getProperSleepingBag().getBlock().asItem().getDefaultInstance();
            blockEntity.setSleepingBagColor(ShapedBackpackRecipe.getProperColor(player.getMainHandItem().getItem()));
            if (!level.isClientSide) {
                Containers.dropItemStack(level, pos.getX(), pos.above().getY(), pos.getZ(), oldSleepingBag);
                stack.shrink(1);
            }
            level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);

            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (player.isShiftKeyDown() && player.getMainHandItem().getItem() == ModItems.BLANK_UPGRADE.get() && level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity) {
            NonNullList<ItemStack> list = NonNullList.create();

            for (int i = 0; i < blockEntity.getCombinedHandler().getSlots(); i++) {
                ItemStack stackInSlot = blockEntity.getCombinedHandler().getStackInSlot(i);

                if (!stackInSlot.isEmpty()) {
                    list.add(stackInSlot);
                    blockEntity.getCombinedHandler().setStackInSlot(i, ItemStack.EMPTY);
                }
            }

            list.addAll(UpgradeItem.getUpgrades(blockEntity));

            //Remove unsortable slots
            if (!blockEntity.getSlotManager().getUnsortableSlots().isEmpty()) {
                blockEntity.getSlotManager().getUnsortableSlots().clear();
            }

            //Remove memory slots
            if (!blockEntity.getSlotManager().getMemorySlots().isEmpty()) {
                blockEntity.getSlotManager().getMemorySlots().clear();
            }

            //Drain excessive fluid
            int fluidAmountLeft = blockEntity.getLeftTank().isEmpty() ? 0 : blockEntity.getLeftTank().getFluidAmount();

            if (fluidAmountLeft > Tiers.LEATHER.getTankCapacity()) {
                blockEntity.getLeftTank().drain(fluidAmountLeft - Tiers.LEATHER.getTankCapacity(), IFluidHandler.FluidAction.EXECUTE);
            }

            int fluidAmountRight = blockEntity.getRightTank().isEmpty() ? 0 : blockEntity.getRightTank().getFluidAmount();

            if (fluidAmountRight > Tiers.LEATHER.getTankCapacity()) {
                blockEntity.getRightTank().drain(fluidAmountRight - Tiers.LEATHER.getTankCapacity(), IFluidHandler.FluidAction.EXECUTE);
            }

            if (!level.isClientSide) {
                Containers.dropContents(level, pos.above(), list);
            }

            //Change size of Tool slots and Storage slots
            blockEntity.getHandler().setSize(Tiers.LEATHER.getStorageSlots());
            blockEntity.getToolSlotsHandler().setSize(Tiers.LEATHER.getToolSlots());

            //Reset tier
            blockEntity.resetTier();

            //Reset Tanks
            blockEntity.getLeftTank().setCapacity(Tiers.LEATHER.getTankCapacity());
            blockEntity.getRightTank().setCapacity(Tiers.LEATHER.getTankCapacity());

            //Reset Settings
            blockEntity.getSettingsManager().loadDefaults();

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        //Quick Equip
        if (TravelersBackpackConfig.COMMON.backpackSettings.enableBackpackBlockWearable.get() && level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity) {
            if (player.isShiftKeyDown() && !CapabilityUtils.isWearingBackpack(player) && !TravelersBackpack.enableCurios()) {
                ItemStack backpack = new ItemStack(level.getBlockState(pos).getBlock(), 1);
                blockEntity.transferToItemStack(backpack);
                Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                if (!level.isClientSide && level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())) {
                    CapabilityUtils.equipBackpack(player, backpack);
                    blockEntity.removeSleepingBag(level, direction);

                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    return;
                }
            }
        }

        //Quick Pick-Up
        if (level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity) {
            if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
                ItemStack backpack = new ItemStack(level.getBlockState(pos).getBlock(), 1);
                blockEntity.transferToItemStack(backpack);
                Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                if (!level.isClientSide && level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, backpack);
                    blockEntity.removeSleepingBag(level, direction);
                    level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);

                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    return;
                }
            }
        }

        //Wash colored backpack in cauldron

        if(event.getLevel().isClientSide || event.getEntity().isShiftKeyDown()) return;

        if(stack.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get()) {
            BlockState blockState = level.getBlockState(pos);
            if (BackpackDyeRecipe.hasColor(stack) && blockState.getBlock() instanceof LayeredCauldronBlock) {
                if (blockState.getValue(LayeredCauldronBlock.LEVEL) > 0) {
                    stack.getTag().remove(ITravelersBackpackContainer.COLOR);
                    LayeredCauldronBlock.lowerFillLevel(blockState, level, pos);
                    level.playSound(null, pos.getX(), pos.getY(), pos.getY(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    return;
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
        if(event.getEntity() instanceof Player player)
        {
            CapabilityUtils.synchronise(player);
        }

        if(event.getEntity() instanceof LivingEntity living && !event.loadedFromDisk() && TravelersBackpackConfig.spawnEntitiesWithBackpack)
        {
            LazyOptional<IEntityTravelersBackpack> cap = CapabilityUtils.getEntityCapability(living);

            if(cap.isPresent() && Reference.ALLOWED_TYPE_ENTRIES.contains(event.getEntity().getType()))
            {
                IEntityTravelersBackpack travelersBackpack = cap.resolve().get();

                if(!travelersBackpack.hasWearable() && event.getLevel().getRandom().nextFloat() < TravelersBackpackConfig.COMMON.world.chance.get())
                {
                    boolean isNether = living.getType() == EntityType.PIGLIN || living.getType() == EntityType.WITHER_SKELETON;
                    RandomSource rand = event.getLevel().random;
                    ItemStack backpack = isNether ?
                            ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES.get(rand.nextIntBetweenInclusive(0, ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES.size() - 1)).getDefaultInstance() :
                            ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.get(rand.nextIntBetweenInclusive(0, ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.size() - 1)).getDefaultInstance();

                    backpack.getOrCreateTag().putInt(ITravelersBackpackContainer.SLEEPING_BAG_COLOR, DyeColor.values()[rand.nextIntBetweenInclusive(0, DyeColor.values().length - 1)].getId());

                    travelersBackpack.setWearable(backpack);
                    travelersBackpack.synchronise();
                }
            }
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
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerDrops(LivingDropsEvent event)
    {
        if(event.getEntity() instanceof Player player)
        {
            //Use different placing logic if no integration is loaded
            if(CapabilityUtils.isWearingBackpack(player))
            {
                //If integration loaded - just remove backpack from capability, rest is handled by integration
                if(TravelersBackpack.enableCurios())
                {
                    //Create backup
                    if(!player.level().isClientSide) BackpackManager.addBackpack((ServerPlayer)player, CapabilityUtils.getWearingBackpack(player));

                    CapabilityUtils.getCapability(player).ifPresent(cap ->
                    {
                        cap.removeWearable();
                        cap.synchronise();
                        cap.synchroniseToOthers(player);
                    });
                    return;
                }

                //Continue if no integration detected
                //Keep backpack on with Keep Inventory game rule
                if(player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;

                ItemStack stack = CapabilityUtils.getWearingBackpack(player);

                if(BackpackUtils.onPlayerDrops(player.level(), player, stack))
                {
                    if(player.level().isClientSide) return;

                    ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack);
                    itemEntity.setDefaultPickUpDelay();

                    TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new ClientboundSendMessagePacket(true, player.blockPosition()));
                    LogHelper.info("There's no space for backpack. Dropping backpack item at" + " X: " + player.blockPosition().getX() + " Y: " + player.getY() + " Z: " + player.blockPosition().getZ());
                    event.getDrops().add(itemEntity);

                    CapabilityUtils.getCapability(player).ifPresent(ITravelersBackpack::removeWearable);
                    CapabilityUtils.synchronise(player);
                }
            }
        }

        if(Reference.ALLOWED_TYPE_ENTRIES.contains(event.getEntity().getType()))
        {
            if(CapabilityUtils.isWearingBackpack(event.getEntity()))
            {
                if(!(event.getSource().getEntity() instanceof Player)) return;

                ItemEntity itemEntity = new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), CapabilityUtils.getWearingBackpack(event.getEntity()));
                event.getDrops().add(itemEntity);
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
    public static void playerTracking(final PlayerEvent.StartTracking event)
    {
        if(event.getTarget() instanceof Player && !event.getTarget().level().isClientSide)
        {
            ServerPlayer target = (ServerPlayer)event.getTarget();

            CapabilityUtils.getCapability(target).ifPresent(c -> TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()),
                    new ClientboundSyncCapabilityPacket(target.getId(), true, CapabilityUtils.getWearingBackpack(target).save(new CompoundTag()))));
        }

        if(Reference.ALLOWED_TYPE_ENTRIES.contains(event.getTarget().getType()) && !event.getTarget().level().isClientSide)
        {
            LivingEntity target = (LivingEntity)event.getTarget();

            CapabilityUtils.getEntityCapability(target).ifPresent(c -> TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)event.getEntity()),
                    new ClientboundSyncCapabilityPacket(target.getId(), false, CapabilityUtils.getWearingBackpack(target).save(new CompoundTag()))));
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

        if(checkAbilitiesForRemoval && event.phase == TickEvent.Phase.END && !event.player.level().isClientSide && (!CapabilityUtils.isWearingBackpack(event.player) || !TravelersBackpackConfig.enableBackpackAbilities))
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
            if(player.isCreative() || player.isSpectator()) return;

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

        for(int i = 0; i < player.getInventory().items.size(); i++)
        {
            if(player.getInventory().items.get(i).getItem() instanceof TravelersBackpackItem)
            {
                atomic.incrementAndGet();
            }
        }

        if(player.getInventory().offhand.get(0).getItem() instanceof TravelersBackpackItem)
        {
            atomic.incrementAndGet();
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
        for(int i = 0; i < event.getAffectedBlocks().size(); i++)
        {
            if(event.getLevel().getBlockState(event.getAffectedBlocks().get(i)).getBlock() instanceof TravelersBackpackBlock) {
                event.getAffectedBlocks().remove(i);
            }
        }
    }

    @SubscribeEvent
    public static void addVillagerTrade(final VillagerTradesEvent event)
    {
        if(TravelersBackpackConfig.enableVillagerTrade && event.getType() == VillagerProfession.LIBRARIAN)
        {
            event.getTrades().get(3).add((trader, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, random.nextInt(64) + 48),
                    new ItemStack(ModItems.VILLAGER_TRAVELERS_BACKPACK.get().asItem(), 1), 1, 50, 0.5F));
        }
    }
}