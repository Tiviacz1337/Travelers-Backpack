package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.TravelersBackpackCapability;
import com.tiviacz.travelersbackpack.capability.TravelersBackpackWearable;
import com.tiviacz.travelersbackpack.common.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.SyncBackpackCapabilityClient;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.loot.LootPool;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Random;

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

            if(!world.isClientSide && block instanceof SleepingBagBlock && !TravelersBackpackConfig.COMMON.enableSleepingBagSpawnPoint.get())
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void playerWashBackpack(PlayerInteractEvent.RightClickBlock event)
    {
        ItemStack stack = event.getItemStack();

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
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemEntityJoin(EntityJoinWorldEvent event)
    {
        if(!(event.getEntity() instanceof ItemEntity) || !TravelersBackpackConfig.SERVER.invulnerableBackpack.get()) return;

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
    }

    @SubscribeEvent
    public static void playerDeath(LivingDeathEvent event)
    {
        if(event.getEntity() instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity)event.getEntity();

            if(CapabilityUtils.isWearingBackpack(player))
            {
                if(!player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY))
                {
                    BackpackUtils.onPlayerDeath(player.level, player, CapabilityUtils.getWearingBackpack(player));
                }
                CapabilityUtils.synchronise((PlayerEntity)event.getEntity());
            }
        }
    }

    @SubscribeEvent
    public static void playerClone(final PlayerEvent.Clone event)
    {
        CapabilityUtils.getCapability(event.getOriginal()).ifPresent(oldTravelersBackpack ->
                CapabilityUtils.getCapability(event.getPlayer()).ifPresent(newTravelersBackpack ->
                        newTravelersBackpack.setWearable(oldTravelersBackpack.getWearable())));
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
                    new SyncBackpackCapabilityClient(CapabilityUtils.getWearingBackpack(target).save(new CompoundNBT()), target.getId())));
        }
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
        if(TravelersBackpackConfig.SERVER.enableLoot.get())
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