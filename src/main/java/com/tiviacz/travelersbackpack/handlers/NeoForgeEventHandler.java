package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.capability.entity.IEntityTravelersBackpack;
import com.tiviacz.travelersbackpack.commands.AccessBackpackCommand;
import com.tiviacz.travelersbackpack.commands.ClearBackpackCommand;
import com.tiviacz.travelersbackpack.commands.RestoreBackpackCommand;
import com.tiviacz.travelersbackpack.commands.UnpackBackpackCommand;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.items.UpgradeItem;
import com.tiviacz.travelersbackpack.network.ClientboundSendMessagePacket;
import com.tiviacz.travelersbackpack.network.ClientboundSyncAttachmentPacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.LogHelper;
import com.tiviacz.travelersbackpack.util.Reference;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.EnderManAngerEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.command.ConfigCommand;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@EventBusSubscriber(modid = TravelersBackpack.MODID, bus = EventBusSubscriber.Bus.GAME)
public class NeoForgeEventHandler
{
    @SubscribeEvent
    public static void playerSetSpawn(PlayerSetSpawnEvent event)
    {
        Level level = event.getEntity().level();

        if(event.getNewSpawn() != null)
        {
            Block block = level.getBlockState(event.getNewSpawn()).getBlock();

            if(!level.isClientSide && block instanceof SleepingBagBlock && !event.isForced()) // && !TravelersBackpackConfig.SERVER.backpackSettings.enableSleepingBagSpawnPoint.get())
            {
                event.setCanceled(true);
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
        if (TravelersBackpackConfig.SERVER.backpackSettings.rightClickUnequip.get() && !TravelersBackpack.enableIntegration()) {
            if (AttachmentUtils.isWearingBackpack(player) && !level.isClientSide) {
                if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
                    ItemStack backpackStack = AttachmentUtils.getWearingBackpack(player);
                    UseOnContext context = new UseOnContext(level, player, hand, backpackStack, event.getHitVec());
                    boolean quickPickupFlag = level.getBlockState(pos).getBlock() instanceof TravelersBackpackBlock;

                    if (!quickPickupFlag && backpackStack.getItem() instanceof TravelersBackpackItem item) {
                        if (item.place(new BlockPlaceContext(context)) == InteractionResult.sidedSuccess(level.isClientSide)) {
                            player.swing(hand, true);
                            level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.05F, (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);
                            AttachmentUtils.getAttachment(player).ifPresent(ITravelersBackpack::removeWearable);

                            AttachmentUtils.synchronise(player);
                            AttachmentUtils.synchroniseToOthers(player);

                            event.setCanceled(true);
                            event.setCancellationResult(InteractionResult.SUCCESS);
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
                player.getMainHandItem().shrink(1);
            }
            level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);
            //player.swing(InteractionHand.MAIN_HAND, true);

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

            //player.swing(InteractionHand.MAIN_HAND, true);

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        //Quick Equip
        if (TravelersBackpackConfig.SERVER.backpackSettings.rightClickEquip.get() && level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity) {
            if (player.isShiftKeyDown() && !AttachmentUtils.isWearingBackpack(player) && !TravelersBackpack.enableIntegration()) {
                ItemStack backpack = new ItemStack(level.getBlockState(pos).getBlock(), 1).copy();
                blockEntity.transferToItemStack(backpack);
                Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                if (!level.isClientSide && level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())) {
                    AttachmentUtils.equipBackpack(player, backpack);
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
                ItemStack backpack = new ItemStack(level.getBlockState(pos).getBlock(), 1).copy();
                blockEntity.transferToItemStack(backpack);
                Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                if (!level.isClientSide && level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, backpack);
                    blockEntity.removeSleepingBag(level, direction);
                    level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);

                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEnderManAngerEvent(EnderManAngerEvent event)
    {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get())
        {
            BackpackAbilities.pumpkinAbility(event);
        }
    }

    @SubscribeEvent
    public static void blockBlazeProjectile(ProjectileImpactEvent event)
    {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get())
        {
            BackpackAbilities.blazeAbility(event);
        }
    }

    @SubscribeEvent
    public static void livingChangeTarget(LivingChangeTargetEvent event)
    {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get())
        {
            BackpackAbilities.ghastAbility(event);
        }
    }

    @SubscribeEvent
    public static void onHit(AttackEntityEvent event)
    {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get())
        {
            BackpackAbilities.beeAbility(event);
        }
    }

    @SubscribeEvent
    public static void onExpPickup(PlayerXpEvent.PickupXp event)
    {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get())
        {
            BackpackAbilities.ABILITIES.lapisAbility(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void playerDeath(LivingDeathEvent event)
    {
        if(event.getEntity() instanceof Player player)
        {
            if(AttachmentUtils.isWearingBackpack(player))
            {
                if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get() && BackpackAbilities.creeperAbility(event))
                {
                    return;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDrops(LivingDropsEvent event)
    {
        if(event.getEntity() instanceof Player player)
        {
            //Use different placing logic if no integration is loaded
            if(AttachmentUtils.isWearingBackpack(player))
            {
                //If integration loaded - just remove backpack from component, rest is handled by integration
                if(TravelersBackpack.enableIntegration())
                {
                    AttachmentUtils.getAttachment(player).ifPresent(attachment ->
                    {
                        attachment.removeWearable();
                        attachment.synchronise();
                        attachment.synchroniseToOthers(player);
                    });
                    return;
                }

                //Continue if no integration detected
                //Keep backpack on with Keep Inventory game rule
                if(player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;

                ItemStack stack = AttachmentUtils.getWearingBackpack(player);

                if(BackpackUtils.onPlayerDrops(player.level(), player, stack))
                {
                    if(player.level().isClientSide) return;

                    ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack);
                    itemEntity.setDefaultPickUpDelay();

                    PacketDistributor.sendToPlayer((ServerPlayer)player, new ClientboundSendMessagePacket(true, player.blockPosition()));
                    LogHelper.info("There's no space for backpack. Dropping backpack item at" + " X: " + player.blockPosition().getX() + " Y: " + player.getY() + " Z: " + player.blockPosition().getZ());

                    event.getDrops().add(itemEntity);

                    AttachmentUtils.getAttachment(player).ifPresent(ITravelersBackpack::removeWearable);
                    AttachmentUtils.synchronise(player);
                }
            }
        }

        if(Reference.ALLOWED_TYPE_ENTRIES.contains(event.getEntity().getType()))
        {
            if(AttachmentUtils.isWearingBackpack(event.getEntity()))
            {
                if(!(event.getSource().getEntity() instanceof Player)) return;

                ItemEntity itemEntity = new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), AttachmentUtils.getWearingBackpack(event.getEntity()));
                event.getDrops().add(itemEntity);
            }
        }
    }

    @SubscribeEvent
    public static void entityLeave(EntityLeaveLevelEvent event)
    {
        if(!(event.getEntity() instanceof ItemEntity itemEntity) || !TravelersBackpackConfig.SERVER.backpackSettings.voidProtection.get()) return;

        //Void protection
        if(itemEntity.getItem().getItem() instanceof TravelersBackpackItem)
        {
            if(event.getLevel().isClientSide) return;

            BlockPos entityPos = itemEntity.blockPosition();
            Vec3 entityPosCentered = entityPos.getCenter();
            double y = entityPosCentered.y();

            if(y < event.getLevel().getMinBuildHeight())
            {
                ItemEntity protectedItemEntity = new ItemEntity(event.getLevel(), entityPosCentered.x(), y, entityPosCentered.z(), itemEntity.getItem());

                protectedItemEntity.setNoGravity(true);
                protectedItemEntity.setDefaultPickUpDelay();

                y = event.getLevel().getMinBuildHeight();

                for(double i = y; i < event.getLevel().getHeight(); i++)
                {
                    if(event.getLevel().getBlockState(BlockPos.containing(new Vec3(entityPosCentered.x(), i, entityPosCentered.z()))).canBeReplaced())
                    {
                        y = i;
                        break;
                    }
                }

                protectedItemEntity.setPos(entityPosCentered.x(), y, entityPosCentered.z());
                protectedItemEntity.setDeltaMovement(0, 0, 0);

                event.getLevel().addFreshEntity(protectedItemEntity);
            }
        }
    }

    @SubscribeEvent
    public static void playerClone(final PlayerEvent.Clone event)
    {
        Player oldPlayer = event.getOriginal();
        oldPlayer.revive();

        AttachmentUtils.getAttachment(oldPlayer)
                .ifPresent(oldTravelersBackpack -> AttachmentUtils.getAttachment(event.getEntity())
                        .ifPresent(newTravelersBackpack ->
                        {
                            newTravelersBackpack.setWearable(oldTravelersBackpack.getWearable());
                            newTravelersBackpack.setContents(oldTravelersBackpack.getWearable());
                        }));
    }

    @SubscribeEvent
    public static void playerChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event)
    {
        AttachmentUtils.synchronise(event.getEntity());
    }

    @SubscribeEvent
    public static void playerJoin(final PlayerEvent.PlayerLoggedInEvent event)
    {
        AttachmentUtils.synchronise(event.getEntity());
    }

    @SubscribeEvent
    public static void entityJoin(EntityJoinLevelEvent event)
    {
        if(event.getEntity() instanceof Player player)
        {
            AttachmentUtils.synchronise(player);
        }

        if(event.getEntity() instanceof LivingEntity living && !event.loadedFromDisk() && TravelersBackpackConfig.SERVER.world.spawnEntitiesWithBackpack.get())
        {
            Optional<IEntityTravelersBackpack> data = AttachmentUtils.getEntityAttachment(living);

            if(data.isPresent() && Reference.ALLOWED_TYPE_ENTRIES.contains(event.getEntity().getType()))
            {
                IEntityTravelersBackpack travelersBackpack = data.get();

                if(!travelersBackpack.hasWearable() && event.getLevel().getRandom().nextFloat() < TravelersBackpackConfig.SERVER.world.chance.get())
                {
                    boolean isNether = living.getType() == EntityType.PIGLIN || living.getType() == EntityType.WITHER_SKELETON;
                    RandomSource rand = event.getLevel().random;
                    ItemStack backpack = isNether ?
                            ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES.get(rand.nextIntBetweenInclusive(0, ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES.size() - 1)).getDefaultInstance() :
                            ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.get(rand.nextIntBetweenInclusive(0, ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.size() - 1)).getDefaultInstance();

                    backpack.set(ModDataComponents.SLEEPING_BAG_COLOR, DyeColor.values()[rand.nextIntBetweenInclusive(0, DyeColor.values().length - 1)].getId());

                    travelersBackpack.setWearable(backpack);
                    travelersBackpack.synchronise();
                }
            }
        }

        if(!(event.getEntity() instanceof ItemEntity itemEntity) || !TravelersBackpackConfig.SERVER.backpackSettings.invulnerableBackpack.get()) return;

        if(itemEntity.getItem().getItem() instanceof TravelersBackpackItem)
        {
            itemEntity.setUnlimitedLifetime();
            itemEntity.setInvulnerable(true);
        }
    }

    @SubscribeEvent
    public static void playerTracking(final PlayerEvent.StartTracking event)
    {
        if(event.getTarget() instanceof Player && !event.getTarget().level().isClientSide)
        {
            ServerPlayer target = (ServerPlayer)event.getTarget();
            AttachmentUtils.getAttachment(target).ifPresent(data ->
                    PacketDistributor.sendToPlayer((ServerPlayer)event.getEntity(), new ClientboundSyncAttachmentPacket(target.getId(), true, AttachmentUtils.getWearingBackpack(target))));
        }

        if(Reference.ALLOWED_TYPE_ENTRIES.contains(event.getTarget().getType()) && !event.getTarget().level().isClientSide)
        {
            LivingEntity target = (LivingEntity)event.getTarget();
            AttachmentUtils.getEntityAttachment(target).ifPresent(data ->
                    PacketDistributor.sendToPlayer((ServerPlayer)event.getEntity(), new ClientboundSyncAttachmentPacket(target.getId(), false, data.getWearable())));
        }
    }

    private static boolean checkAbilitiesForRemoval = true;

    @SubscribeEvent
    public static void playerTick(final PlayerTickEvent.Post event)
    {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get() /*&& event.phase == TickEvent.Phase.END */ && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, AttachmentUtils.getWearingBackpack(event.getEntity())))
        {
            TravelersBackpackContainer.abilityTick(event.getEntity());
            if(!checkAbilitiesForRemoval && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_REMOVAL_LIST, AttachmentUtils.getWearingBackpack(event.getEntity()))) checkAbilitiesForRemoval = true;
        }

        if(checkAbilitiesForRemoval && /*event.phase == TickEvent.Phase.END && */ !event.getEntity().level().isClientSide && (!AttachmentUtils.isWearingBackpack(event.getEntity()) || !TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get()))
        {
            BackpackAbilities.ABILITIES.armorAbilityRemovals(event.getEntity());
            checkAbilitiesForRemoval = false;
        }
    }

    private static long nextBackpackCountCheck = 0;
    private static final int BACKPACK_COUNT_CHECK_COOLDOWN = 100;

    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Post event)
    {
        if(/*event.phase != TickEvent.Phase.END || */ !TravelersBackpackConfig.SERVER.slownessDebuff.tooManyBackpacksSlowness.get() || nextBackpackCountCheck > event.getLevel().getGameTime())
        {
            return;
        }
        nextBackpackCountCheck = event.getLevel().getGameTime() + BACKPACK_COUNT_CHECK_COOLDOWN;

        event.getLevel().players().forEach(player ->
        {
            if(player.isCreative() || player.isSpectator()) return;

            AtomicInteger numberOfBackpacks = checkBackpacksForSlowness(player);
            if(numberOfBackpacks.get() == 0) return;

            int maxNumberOfBackpacks = TravelersBackpackConfig.SERVER.slownessDebuff.maxNumberOfBackpacks.get();
            if(numberOfBackpacks.get() > maxNumberOfBackpacks)
            {
                int numberOfSlownessLevels = Math.min(10, (int) Math.ceil((numberOfBackpacks.get() - maxNumberOfBackpacks) * TravelersBackpackConfig.SERVER.slownessDebuff.slownessPerExcessedBackpack.get()));
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
    public static void addVillagerTrade(final VillagerTradesEvent event)
    {
        if(TravelersBackpackConfig.COMMON.enableVillagerTrade.get() && event.getType() == VillagerProfession.LIBRARIAN)
        {
            event.getTrades().get(3).add((trader, random) -> new MerchantOffer(new ItemCost(Items.EMERALD, random.nextInt(64) + 48),
                    new ItemStack(ModItems.VILLAGER_TRAVELERS_BACKPACK.get().asItem(), 1), 1, 50, 0.5F));
        }
    }
}