package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.capability.TravelersBackpackCapability;
import com.tiviacz.travelersbackpack.capability.TravelersBackpackWearable;
import com.tiviacz.travelersbackpack.commands.AccessBackpackCommand;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ClientboundSyncCapabilityPacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
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
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.EnderManAngerEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
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
    public static void registerCapabilities(final RegisterCapabilitiesEvent event)
    {
        event.register(ITravelersBackpack.class);
    }

    @SubscribeEvent
    public static void playerSetSpawn(PlayerSetSpawnEvent event)
    {
        Level level = event.getPlayer().level;

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

        // Equip Backpack on right click with any item in hand //#TODO CHECK
        if(TravelersBackpackConfig.enableBackpackBlockWearable && event.getWorld().getBlockState(event.getPos()).getBlock() instanceof TravelersBackpackBlock block)
        {
            Level level = event.getWorld();
            BlockPos pos = event.getPos();
            Player player = event.getPlayer();

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
                        CapabilityUtils.equipBackpack(event.getPlayer(), backpack);
                        player.swing(InteractionHand.MAIN_HAND, true);

                        if(blockEntity.isSleepingBagDeployed())
                        {
                            level.setBlockAndUpdate(pos.relative(bagDirection), Blocks.AIR.defaultBlockState());
                            level.setBlockAndUpdate(pos.relative(bagDirection).relative(bagDirection), Blocks.AIR.defaultBlockState());
                        }
                        event.setCanceled(true);
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
                                        event.setCanceled(true);
                                    }
                                }
                            }
                        }
                    }));
                }
            }
        }

        //Wash colored backpack in cauldron
        if(event.getWorld().isClientSide || event.getPlayer().isShiftKeyDown()) return;

        if(stack.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get())
        {
            BlockState blockState = event.getWorld().getBlockState(event.getPos());

            if(BackpackDyeRecipe.hasColor(stack) && blockState.getBlock() instanceof LayeredCauldronBlock)
            {
                if(blockState.getValue(LayeredCauldronBlock.LEVEL) > 0)
                {
                    stack.getTag().remove("Color");
                    LayeredCauldronBlock.lowerFillLevel(blockState, event.getWorld(), event.getPos());
                    event.getWorld().playSound(null, event.getPos().getX(), event.getPos().getY(), event.getPos().getY(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    event.setCanceled(true);
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
    public static void onItemEntityJoin(EntityJoinWorldEvent event)
    {
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
    }

    @SubscribeEvent
    public static void playerClone(final PlayerEvent.Clone event)
    {
        Player oldPlayer = event.getOriginal();
        oldPlayer.revive();

        CapabilityUtils.getCapability(oldPlayer)
                .ifPresent(oldTravelersBackpack -> CapabilityUtils.getCapability(event.getPlayer())
                        .ifPresent(newTravelersBackpack ->
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

            CapabilityUtils.getCapability(target).ifPresent(c -> TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()),
                    new ClientboundSyncCapabilityPacket(CapabilityUtils.getWearingBackpack(target).save(new CompoundTag()), target.getId())));
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
                event.getTable().addPool(new LootPool.Builder().name(new ResourceLocation(TravelersBackpack.MODID, "chests/bat").toString()).build());
            }

            if(event.getName().equals(new ResourceLocation("chests/village/village_armorer")))
            {
                event.getTable().addPool(new LootPool.Builder().name(new ResourceLocation(TravelersBackpack.MODID, "chests/iron_golem").toString()).build());
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

    private static class BackpackVillagerTrade implements VillagerTrades.ItemListing
    {
        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random)
        {
            return new MerchantOffer(new ItemStack(Items.EMERALD, random.nextInt(64) + 48), new ItemStack(ModItems.VILLAGER_TRAVELERS_BACKPACK.get().asItem(), 1), 1, 5, 0.5F);
        }
    }
}