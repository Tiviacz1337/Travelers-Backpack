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
import com.tiviacz.travelersbackpack.common.recipes.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.compat.curios.TravelersBackpackCurios;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.items.UpgradeItem;
import com.tiviacz.travelersbackpack.network.ClientboundSyncAttachmentPacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
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
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.EnderManAngerEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.command.ConfigCommand;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NeoForgeEventHandler
{
    @SubscribeEvent
    public static void playerSetSpawn(PlayerSetSpawnEvent event)
    {
        Level level = event.getEntity().level();

        if(event.getNewSpawn() != null)
        {
            Block block = level.getBlockState(event.getNewSpawn()).getBlock();

            if(!level.isClientSide && block instanceof SleepingBagBlock && !TravelersBackpackConfig.SERVER.backpackSettings.enableSleepingBagSpawnPoint.get())
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

        if(TravelersBackpackConfig.SERVER.backpackSettings.rightClickUnequip.get())
        {
            if(AttachmentUtils.isWearingBackpack(player) && !level.isClientSide)
            {
                if(player.isShiftKeyDown() && event.getHand() == InteractionHand.MAIN_HAND && player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())
                {
                    ItemStack backpackStack = AttachmentUtils.getWearingBackpack(player);
                    UseOnContext context = new UseOnContext(level, player, InteractionHand.MAIN_HAND, backpackStack, event.getHitVec());

                    if(backpackStack.getItem() instanceof TravelersBackpackItem item)
                    {
                        if(item.place(new BlockPlaceContext(context)) == InteractionResult.sidedSuccess(level.isClientSide))
                        {
                            player.swing(InteractionHand.MAIN_HAND, true);
                            level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.05F, (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);

                            AttachmentUtils.getAttachment(player).ifPresent(ITravelersBackpack::removeWearable);

                            if(TravelersBackpack.enableCurios())
                            {
                                TravelersBackpackCurios.rightClickUnequip(player, backpackStack);
                            }

                            AttachmentUtils.synchronise(player);
                            AttachmentUtils.synchroniseToOthers(player);

                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        }

        if(player.isShiftKeyDown() && event.getHand() == InteractionHand.MAIN_HAND && player.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.SLEEPING_BAGS) && level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity)
        {
            ItemStack oldSleepingBag = blockEntity.getProperSleepingBag(blockEntity.getSleepingBagColor()).getBlock().asItem().getDefaultInstance();
            blockEntity.setSleepingBagColor(ShapedBackpackRecipe.getProperColor(player.getItemInHand(InteractionHand.MAIN_HAND).getItem()));
            if(!level.isClientSide)
            {
                Containers.dropItemStack(level, pos.getX(), pos.above().getY(), pos.getZ(), oldSleepingBag);
                stack.shrink(1);
            }
            player.level().playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, (1.0F + (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.2F) * 0.7F);
            player.swing(InteractionHand.MAIN_HAND, true);

            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if(player.isShiftKeyDown() && player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == ModItems.BLANK_UPGRADE.get() && level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity)
        {
            NonNullList<ItemStack> list = NonNullList.create();

            for(int i = 0; i < blockEntity.getCombinedHandler().getSlots(); i++)
            {
                ItemStack stackInSlot = blockEntity.getCombinedHandler().getStackInSlot(i);

                if(!stackInSlot.isEmpty())
                {
                    list.add(stackInSlot);
                    blockEntity.getCombinedHandler().setStackInSlot(i, ItemStack.EMPTY);
                }
            }

            list.addAll(UpgradeItem.getUpgrades(blockEntity));

            //Remove unsortable slots
            if(!blockEntity.getSlotManager().getUnsortableSlots().isEmpty())
            {
                blockEntity.getSlotManager().getUnsortableSlots().clear();
            }

            //Remove memory slots
            if(!blockEntity.getSlotManager().getMemorySlots().isEmpty())
            {
                blockEntity.getSlotManager().getMemorySlots().clear();
            }

            //Drain excessive fluid
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

            player.swing(InteractionHand.MAIN_HAND, true);

            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if(event.getLevel().isClientSide) return;

        // Equip Backpack on right click with any item in hand //#TODO CHECK
        if(TravelersBackpackConfig.SERVER.backpackSettings.rightClickEquip.get() && event.getLevel().getBlockState(event.getPos()).getBlock() instanceof TravelersBackpackBlock block)
        {
            if(player.isShiftKeyDown() && !AttachmentUtils.isWearingBackpack(player))
            {
                TravelersBackpackBlockEntity blockEntity = (TravelersBackpackBlockEntity)level.getBlockEntity(pos);
                ItemStack backpack = new ItemStack(block, 1);

                Direction bagDirection = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                boolean canEquipCurio = false;
                if(TravelersBackpack.enableCurios()) canEquipCurio = TravelersBackpackCurios.rightClickEquip(player, backpack, true);

                if(level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState()))
                {
                    blockEntity.transferToItemStack(backpack);

                    if(TravelersBackpack.enableCurios() && canEquipCurio) TravelersBackpackCurios.rightClickEquip(player, backpack, false);
                    else AttachmentUtils.equipBackpack(event.getEntity(), backpack);

                    player.swing(InteractionHand.MAIN_HAND, true);

                    if(blockEntity.isSleepingBagDeployed())
                    {
                        level.setBlockAndUpdate(pos.relative(bagDirection), Blocks.AIR.defaultBlockState());
                        level.setBlockAndUpdate(pos.relative(bagDirection).relative(bagDirection), Blocks.AIR.defaultBlockState());
                    }
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }
                //}
                //else
                //{
               //     //blockEntity.transferToItemStack(backpack);

                    //#TODO needs reload bug
                    /*CuriosApi.getCurio(backpack).ifPresent(curio -> CuriosApi.getCuriosInventory(player).ifPresent(handler ->
                    {
                        Map<String, ICurioStacksHandler> curios = handler.getCurios();

                        for(Map.Entry<String, ICurioStacksHandler> entry : curios.entrySet())
                        {
                            IDynamicStackHandler stackHandler = entry.getValue().getStacks();

                            for(int i = 0; i < stackHandler.getSlots(); i++)
                            {
                                String id = entry.getKey();
                                NonNullList<Boolean> renderStates = entry.getValue().getRenders();
                                SlotContext slotContext = new SlotContext(id, player, i, false,
                                        renderStates.size() > i && renderStates.get(i));

                                if(CuriosApi.isStackValid(slotContext, backpack) && curio.canEquip(slotContext))
                                {
                                    ItemStack present = stackHandler.getStackInSlot(i);

                                    if(present.isEmpty())
                                    {
                                        stackHandler.setStackInSlot(i, backpack.copy());
                                        curio.onEquipFromUse(slotContext);

                                        Direction bagDirection = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                                        if(level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState()))
                                        {
                                            player.level().playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, (1.0F + (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.2F) * 0.7F);
                                            player.swing(InteractionHand.MAIN_HAND, true);

                                            if(blockEntity.isSleepingBagDeployed())
                                            {
                                                level.setBlockAndUpdate(pos.relative(bagDirection), Blocks.AIR.defaultBlockState());
                                                level.setBlockAndUpdate(pos.relative(bagDirection).relative(bagDirection), Blocks.AIR.defaultBlockState());
                                            }
                                        }

                                        event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide()));
                                        event.setCanceled(true);
                                        return;
                                    }
                                }
                            }
                        }
                    })); */
                //}
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
                    stack.getTag().remove(ITravelersBackpackContainer.COLOR);
                    LayeredCauldronBlock.lowerFillLevel(blockState, event.getLevel(), event.getPos());
                    event.getLevel().playSound(null, event.getPos().getX(), event.getPos().getY(), event.getPos().getY(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
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
    public static void onEntityJoinLevel(EntityJoinLevelEvent event)
    {
        if(event.getEntity() instanceof LivingEntity living && !event.loadedFromDisk() && TravelersBackpackConfig.SERVER.world.spawnEntitiesWithBackpack.get())
        {
            Optional<IEntityTravelersBackpack> data = AttachmentUtils.getEntityAttachment(living);

            if(data.isPresent() && Reference.ALLOWED_TYPE_ENTRIES.contains(event.getEntity().getType()))
            {
                IEntityTravelersBackpack travelersBackpack = data.get();

                if(!travelersBackpack.hasWearable() && event.getLevel().getRandom().nextInt(0, TravelersBackpackConfig.SERVER.world.spawnChance.get()) == 0)
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

        if(!(event.getEntity() instanceof ItemEntity itemEntity) || !TravelersBackpackConfig.SERVER.backpackSettings.invulnerableBackpack.get()) return;

        if(itemEntity.getItem().getItem() instanceof TravelersBackpackItem)
        {
            itemEntity.setUnlimitedLifetime();
            itemEntity.setInvulnerable(true);
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

                if(TravelersBackpack.isAnyGraveModInstalled()) return;

                if(!player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY))
                {
                    BackpackUtils.onPlayerDeath(player.level(), player, AttachmentUtils.getWearingBackpack(player));
                }
                AttachmentUtils.synchronise((Player)event.getEntity());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerDrops(LivingDropsEvent event)
    {
        if(TravelersBackpack.isAnyGraveModInstalled())
        {
            if(event.getEntity() instanceof Player player)
            {
                if(player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;

                if(AttachmentUtils.isWearingBackpack(player) && !player.level().isClientSide)
                {
                    ItemStack stack = AttachmentUtils.getWearingBackpack(player);
                    ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack);
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
                ItemEntity itemEntity = new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), AttachmentUtils.getWearingBackpack(event.getEntity()));
                event.getDrops().add(itemEntity);
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
    }

    @SubscribeEvent
    public static void playerTracking(final PlayerEvent.StartTracking event)
    {
        if(event.getTarget() instanceof Player && !event.getTarget().level().isClientSide)
        {
            ServerPlayer target = (ServerPlayer)event.getTarget();
            AttachmentUtils.getAttachment(target).ifPresent(data ->
                    PacketDistributor.PLAYER.with((ServerPlayer)event.getEntity()).send(new ClientboundSyncAttachmentPacket(target.getId(), true, AttachmentUtils.getWearingBackpack(target).save(new CompoundTag()))));
        }

        if(Reference.ALLOWED_TYPE_ENTRIES.contains(event.getTarget().getType()) && !event.getTarget().level().isClientSide)
        {
            LivingEntity target = (LivingEntity)event.getTarget();
            AttachmentUtils.getEntityAttachment(target).ifPresent(data ->
                    PacketDistributor.PLAYER.with((ServerPlayer)event.getEntity()).send(new ClientboundSyncAttachmentPacket(target.getId(), false, data.getWearable().save(new CompoundTag()))));
            //PacketDistributor.PLAYER.with((ServerPlayer)event.getEntity()).send(new ClientboundSyncCapabilityPacket(target.getId(), false, CapabilityUtils.getWearingBackpack(target).save(new CompoundTag())));
        }
    }

    private static boolean checkAbilitiesForRemoval = true;

    @SubscribeEvent
    public static void playerTick(final TickEvent.PlayerTickEvent event)
    {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get() && event.phase == TickEvent.Phase.END && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, AttachmentUtils.getWearingBackpack(event.player)))
        {
            TravelersBackpackContainer.abilityTick(event.player);
            if(!checkAbilitiesForRemoval && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_REMOVAL_LIST, AttachmentUtils.getWearingBackpack(event.player))) checkAbilitiesForRemoval = true;
        }

        if(checkAbilitiesForRemoval && event.phase == TickEvent.Phase.END && !event.player.level().isClientSide && (!AttachmentUtils.isWearingBackpack(event.player) || !TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get()))
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
        if(event.phase != TickEvent.Phase.END || !TravelersBackpackConfig.SERVER.slownessDebuff.tooManyBackpacksSlowness.get() || nextBackpackCountCheck > event.level.getGameTime())
        {
            return;
        }
        nextBackpackCountCheck = event.level.getGameTime() + BACKPACK_COUNT_CHECK_COOLDOWN;

        event.level.players().forEach(player ->
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
    {   //#TODO check
        if(TravelersBackpackConfig.SERVER.world.enableVillagerTrade.get() && event.getType() == VillagerProfession.LIBRARIAN)
        {
            event.getTrades().get(3).add((trader, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, random.nextInt(64) + 48),
                    new ItemStack(ModItems.VILLAGER_TRAVELERS_BACKPACK.get().asItem(), 1), 1, 50, 0.5F));
        }
    }
}