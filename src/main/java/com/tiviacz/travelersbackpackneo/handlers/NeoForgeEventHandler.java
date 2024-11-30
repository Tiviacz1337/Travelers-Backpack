package com.tiviacz.travelersbackpackneo.handlers;

import com.tiviacz.travelersbackpackneo.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpackneo.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.commands.AccessCommand;
import com.tiviacz.travelersbackpack.commands.ClearCommand;
import com.tiviacz.travelersbackpack.commands.RestoreCommand;
import com.tiviacz.travelersbackpack.commands.UnpackCommand;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.BackpackManager;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpackneo.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpackneo.initold.ModDataComponents;
import com.tiviacz.travelersbackpackneo.initold.ModItemsNeo;
import com.tiviacz.travelersbackpackneo.initold.ModTags;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpackneo.inventory.StorageAccessWrapper;
import com.tiviacz.travelersbackpackneo.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpackneo.items.upgrades.TanksUpgradeItem;
import com.tiviacz.travelersbackpackneo.network.ClientboundSendMessagePacket;
import com.tiviacz.travelersbackpackneo.network.ClientboundSyncAttachmentPacket;
import com.tiviacz.travelersbackpack.util.BackpackDeathHelper;
import com.tiviacz.travelersbackpack.util.LogHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.command.ConfigCommand;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@EventBusSubscriber(modid = TravelersBackpack.MODID, bus = EventBusSubscriber.Bus.GAME)
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
    public static void playerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();

        //Quick Unequip
        if(TravelersBackpackConfig.SERVER.backpackSettings.rightClickUnequip.get() && !TravelersBackpack.enableIntegration()) {
            if(AttachmentUtils.isWearingBackpack(player) && !level.isClientSide) {
                if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
                    ItemStack backpackStack = AttachmentUtils.getWearingBackpack(player).copy();
                    UseOnContext context = new UseOnContext(level, player, hand, backpackStack, event.getHitVec());
                    boolean quickPickupFlag = level.getBlockState(pos).getBlock() instanceof TravelersBackpackBlock;

                    if(!quickPickupFlag && backpackStack.getItem() instanceof TravelersBackpackItem item) {
                        if(item.place(new BlockPlaceContext(context)) == InteractionResult.sidedSuccess(level.isClientSide)) {
                            player.swing(hand, true);
                            level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.05F, (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);
                            AttachmentUtils.getAttachment(player).ifPresent(data -> {
                                data.remove();
                                data.synchronise();
                            });

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
            ItemStack oldSleepingBag = blockEntity.getProperSleepingBag().getBlock().asItem().getDefaultInstance();
            blockEntity.getWrapper().setSleepingBagColor(ShapedBackpackRecipe.getProperColor(player.getMainHandItem().getItem()));

            if(!level.isClientSide) {
                Containers.dropItemStack(level, pos.getX(), pos.above().getY(), pos.getZ(), oldSleepingBag);
                player.getMainHandItem().shrink(1);
            }
            level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);

            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        //Remove custom backpack design (go back to standard)
        if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().is(Items.SHEARS) && level.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
            if(!backpackBlockEntity.getWrapper().getBackpackStack().is(ModItemsNeo.STANDARD_TRAVELERS_BACKPACK)) {
                ItemStack standardBackpack = new ItemStack(ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get(), 1);
                backpackBlockEntity.toItemStack(standardBackpack);
                Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);
                if(!level.isClientSide && level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), standardBackpack);
                    backpackBlockEntity.removeSleepingBag(level, direction);
                    level.playSound(null, backpackBlockEntity.getBlockPos(), SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.gameEvent(GameEvent.SHEAR, player);
                    player.getMainHandItem().hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }
        }

        /*if (player.isShiftKeyDown() && player.getMainHandItem().getItem() == ModItems.BLANK_UPGRADE.get() && level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity) {
            NonNullList<ItemStack> list = NonNullList.create(); //#TODO decide what to do with it

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
        } */

        //Quick Equip
        if(TravelersBackpackConfig.SERVER.backpackSettings.rightClickEquip.get() && level.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
            if(player.isShiftKeyDown() && !AttachmentUtils.isWearingBackpack(player) && !TravelersBackpack.enableIntegration()) {
                //Prioritize placing block
                if(player.getItemInHand(hand).getItem() instanceof BlockItem) {
                    return;
                }
                ItemStack backpack = new ItemStack(level.getBlockState(pos).getBlock(), 1).copy();
                backpackBlockEntity.toItemStack(backpack);
                Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                if(!level.isClientSide && level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())) {
                    AttachmentUtils.equipBackpack(player, backpack);
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
                    level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);

                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }
            }
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
        }
    }

    @SubscribeEvent
    public static void onExpPickup(PlayerXpEvent.PickupXp event) {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get()) {
            BackpackAbilities.ABILITIES.lapisAbility(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void playerDeath(LivingDeathEvent event) {
        if(event.getEntity() instanceof Player player) {
            if(AttachmentUtils.isWearingBackpack(player)) {
                if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get() && BackpackAbilities.creeperAbility(event)) {
                    return;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDrops(LivingDropsEvent event) {
        if(event.getEntity() instanceof Player player) {
            //Use different placing logic if no integration is loaded
            if(AttachmentUtils.isWearingBackpack(player)) {
                //If integration loaded - just remove backpack from component, rest is handled by integration
                if(TravelersBackpack.enableIntegration()) {
                    //Create backup
                    if(!player.level().isClientSide)
                        BackpackManager.addBackpack((ServerPlayer)player, AttachmentUtils.getWearingBackpack(player));
                    return;
                }

                //Continue if no integration detected
                //Keep backpack on with Keep Inventory game rule
                if(player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;

                ItemStack stack = AttachmentUtils.getWearingBackpack(player);

                if(BackpackDeathHelper.onPlayerDrops(player.level(), player, stack)) {
                    if(player.level().isClientSide) return;

                    ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack);
                    itemEntity.setDefaultPickUpDelay();

                    PacketDistributor.sendToPlayer((ServerPlayer)player, new ClientboundSendMessagePacket(true, player.blockPosition()));
                    LogHelper.info("There's no space for backpack. Dropping backpack item at" + " X: " + player.blockPosition().getX() + " Y: " + player.getY() + " Z: " + player.blockPosition().getZ());

                    event.getDrops().add(itemEntity);

                    AttachmentUtils.getAttachment(player).ifPresent(attachment -> {
                        attachment.remove();
                        attachment.synchronise();
                    });
                }
            }
        }

        if(Reference.ALLOWED_TYPE_ENTRIES.contains(event.getEntity().getType())) {
            if(event.getEntity().getItemBySlot(EquipmentSlot.BODY).getItem() instanceof TravelersBackpackItem) {
                if(!(event.getSource().getEntity() instanceof Player)) return;

                ItemEntity itemEntity = new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity().getItemBySlot(EquipmentSlot.BODY));
                event.getDrops().add(itemEntity);
            }
        }
    }

    @SubscribeEvent
    public static void playerClone(final PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        oldPlayer.revive();
        AttachmentUtils.getAttachment(oldPlayer)
                .ifPresent(oldTravelersBackpack -> AttachmentUtils.getAttachment(event.getEntity())
                        .ifPresent(newTravelersBackpack -> newTravelersBackpack.equipBackpack(oldTravelersBackpack.getBackpack())));
    }

    @SubscribeEvent
    public static void playerChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
        AttachmentUtils.synchronise(event.getEntity());
    }

    @SubscribeEvent
    public static void playerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
        AttachmentUtils.synchronise(event.getEntity());
    }

    @SubscribeEvent
    public static void entityJoin(EntityJoinLevelEvent event) {
        if(event.getEntity() instanceof Player player) {
            AttachmentUtils.synchronise(player);
        }
    }

    @SubscribeEvent
    public static void finalizeSpawnEvent(FinalizeSpawnEvent event) {
        if(TravelersBackpackConfig.SERVER.world.spawnEntitiesWithBackpack.get()) {
            if(event.getEntity().getItemBySlot(EquipmentSlot.BODY).isEmpty() && Reference.ALLOWED_TYPE_ENTRIES.contains(event.getEntity().getType())) {
                if(event.getLevel().getRandom().nextFloat() < TravelersBackpackConfig.SERVER.world.chance.get()) {
                    boolean isNether = event.getEntity().getType() == EntityType.PIGLIN || event.getEntity().getType() == EntityType.WITHER_SKELETON;
                    RandomSource rand = event.getLevel().getRandom();
                    ItemStack backpack = isNether ?
                            ModItemsNeo.COMPATIBLE_NETHER_BACKPACK_ENTRIES.get(rand.nextIntBetweenInclusive(0, ModItemsNeo.COMPATIBLE_NETHER_BACKPACK_ENTRIES.size() - 1)).getDefaultInstance() :
                            ModItemsNeo.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.get(rand.nextIntBetweenInclusive(0, ModItemsNeo.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.size() - 1)).getDefaultInstance();

                    backpack.set(ModDataComponents.SLEEPING_BAG_COLOR, DyeColor.values()[rand.nextIntBetweenInclusive(0, DyeColor.values().length - 1)].getId());
                    boolean flag = false;
                    if(rand.nextFloat() > 0.5F) {
                        backpack.set(ModDataComponents.STARTER_UPGRADES, List.of(ModItemsNeo.TANKS_UPGRADE.toStack()));
                        flag = true;
                    }
                    if(rand.nextFloat() > 0.25F) {
                        backpack.set(DataComponents.DYED_COLOR, new DyedItemColor(rand.nextInt(), true));
                    }
                    if(flag) {
                        backpack.set(ModDataComponents.RENDER_INFO, TanksUpgradeItem.writeToRenderData());
                    } else {
                        backpack.set(ModDataComponents.RENDER_INFO, RenderInfo.EMPTY);
                    }
                    event.getEntity().setItemSlot(EquipmentSlot.BODY, backpack);
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerTracking(final PlayerEvent.StartTracking event) {
        if(event.getTarget() instanceof ServerPlayer target && !target.level().isClientSide) {
            AttachmentUtils.getAttachment(target).ifPresent(data ->
                    PacketDistributor.sendToPlayer((ServerPlayer)event.getEntity(), new ClientboundSyncAttachmentPacket(target.getId(), data.getBackpack())));
        }
    }

    private static boolean checkAbilitiesForRemoval = true;

    @SubscribeEvent
    public static void playerTick(final PlayerTickEvent.Post event) {
        if(AttachmentUtils.isWearingBackpack(event.getEntity())) {
            BackpackWrapper.tick(AttachmentUtils.getWearingBackpack(event.getEntity()), event.getEntity(), false);
        }
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get() && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, AttachmentUtils.getWearingBackpack(event.getEntity()))) {
            if(!checkAbilitiesForRemoval && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_REMOVAL_LIST, AttachmentUtils.getWearingBackpack(event.getEntity())))
                checkAbilitiesForRemoval = true;
        }
        if(checkAbilitiesForRemoval && !event.getEntity().level().isClientSide && (!AttachmentUtils.isWearingBackpack(event.getEntity()) || !TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get())) {
            BackpackAbilities.ABILITIES.armorAbilityRemovals(event.getEntity());
            checkAbilitiesForRemoval = false;
        }
    }

    private static long nextBackpackCountCheck = 0;
    private static final int BACKPACK_COUNT_CHECK_COOLDOWN = 100;

    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Post event) {
        if(!TravelersBackpackConfig.SERVER.slownessDebuff.tooManyBackpacksSlowness.get() || nextBackpackCountCheck > event.getLevel().getGameTime()) {
            return;
        }
        nextBackpackCountCheck = event.getLevel().getGameTime() + BACKPACK_COUNT_CHECK_COOLDOWN;

        event.getLevel().players().forEach(player -> {
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
            event.getTrades().get(3).add((trader, random) -> new MerchantOffer(new ItemCost(Items.EMERALD, random.nextInt(64) + 48),
                    new ItemStack(ModItemsNeo.VILLAGER_TRAVELERS_BACKPACK.get().asItem(), 1), 1, 50, 0.5F));
        }
    }

    /**
     * UPGRADES
     */

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        ItemEntity itemEntity = event.getItemEntity();
        if(itemEntity.getItem().isEmpty() || itemEntity.pickupDelay > 0) {
            return;
        }

        Player player = event.getPlayer();
        Level level = player.level();

        if(AttachmentUtils.isWearingBackpack(player)) {
            BackpackWrapper wrapper = AttachmentUtils.getBackpackWrapper(player);
            if(wrapper.getUpgradeManager().pickupUpgrade.isPresent() && wrapper.getUpgradeManager().pickupUpgrade.get().canPickup(itemEntity.getItem())) {
                ItemStack remainingStack = ItemHandlerHelper.insertItemStacked(new StorageAccessWrapper(wrapper, wrapper.getStorage()), itemEntity.getItem(), false);
                if(remainingStack != itemEntity.getItem()) {
                    level.playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, (level.random.nextFloat() - level.random.nextFloat()) * 1.4F + 2.0F);
                    itemEntity.setItem(remainingStack);
                    event.setCanPickup(TriState.FALSE);
                }
            }
        }
    }
}