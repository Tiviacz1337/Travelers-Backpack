package com.tiviacz.travelersbackpack.component;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.compat.trinkets.TrinketsCompat;
import com.tiviacz.travelersbackpack.component.entity.EntityTravelersBackpackComponent;
import com.tiviacz.travelersbackpack.component.entity.IEntityTravelersBackpackComponent;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import dev.emi.trinkets.api.TrinketsApi;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ComponentUtils implements EntityComponentInitializer
{
    public static final ComponentKey<ITravelersBackpackComponent> WEARABLE = ComponentRegistry.getOrCreate(new Identifier(TravelersBackpack.MODID, "travelersbackpack"), ITravelersBackpackComponent.class);
    public static final ComponentKey<IEntityTravelersBackpackComponent> ENTITY_WEARABLE = ComponentRegistry.getOrCreate(new Identifier(TravelersBackpack.MODID, "travelersbackpack_entity"), IEntityTravelersBackpackComponent.class);

    public static ITravelersBackpackComponent getComponent(PlayerEntity player)
    {
        return player.getComponent(WEARABLE);
    }

    public static IEntityTravelersBackpackComponent getComponent(LivingEntity livingEntity)
    {
        return livingEntity.getComponent(ENTITY_WEARABLE);
    }

    public static void sync(PlayerEntity player)
    {
        if(player instanceof ServerPlayerEntity)
        {
            getComponent(player).sync();
        }
    }

    public static void syncToTracking(PlayerEntity player)
    {
        if(player instanceof ServerPlayerEntity serverPlayer)
        {
            getComponent(player).syncToTracking(serverPlayer);
        }
    }

    public static void synchroniseEntity(LivingEntity livingEntity)
    {
        getComponent(livingEntity).sync();
    }

    public static boolean isWearingBackpack(PlayerEntity player)
    {
        if(TravelersBackpack.enableTrinkets())
        {
            return TrinketsApi.getTrinketComponent(player).map(t -> t.isEquipped(item -> item.getItem() instanceof TravelersBackpackItem)).orElse(false);
        }

        return player.getComponent(WEARABLE).hasWearable() && player.getComponent(WEARABLE).getWearable().getItem() instanceof TravelersBackpackItem;
    }

    public static boolean isWearingBackpack(LivingEntity livingEntity)
    {
        return livingEntity.getComponent(ENTITY_WEARABLE).hasWearable() && livingEntity.getComponent(ENTITY_WEARABLE).getWearable().getItem() instanceof TravelersBackpackItem;
    }

    public static ItemStack getWearingBackpack(PlayerEntity player)
    {
        if(TravelersBackpack.enableTrinkets())
        {
            return TrinketsCompat.getTravelersBackpackTrinket(player);
        }

        return isWearingBackpack(player) ? player.getComponent(WEARABLE).getWearable() : ItemStack.EMPTY;
    }

    public static ItemStack getWearingBackpack(LivingEntity livingEntity)
    {
        return isWearingBackpack(livingEntity) ? livingEntity.getComponent(ENTITY_WEARABLE).getWearable() : ItemStack.EMPTY;
    }

    public static void equipBackpack(PlayerEntity player, ItemStack stack)
    {
        if(player.getWorld().isClient) return;

        if(!player.getComponent(WEARABLE).hasWearable())
        {
            player.getComponent(WEARABLE).setWearable(stack);
            player.getComponent(WEARABLE).setContents(stack);
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (player.getWorld().random.nextFloat() - player.getWorld().random.nextFloat()) * 0.2F) * 0.7F);
        }

        sync(player);
        syncToTracking(player);
    }

    @Nullable
    public static TravelersBackpackInventory getBackpackInv(PlayerEntity player)
    {
        ItemStack wearable = getWearingBackpack(player);

        if(TravelersBackpack.enableTrinkets())
        {
            return TrinketsCompat.getTrinketsTravelersBackpackInventory(player);
        }

        if(wearable.getItem() instanceof TravelersBackpackItem)
        {
            return player.getComponent(WEARABLE).getInventory();
        }
        return null;
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry)
    {
        registry.registerForPlayers(WEARABLE, TravelersBackpackComponent::new, RespawnCopyStrategy.INVENTORY);

        registry.registerFor(ZombieEntity.class, ENTITY_WEARABLE, EntityTravelersBackpackComponent::new);
        registry.registerFor(EndermanEntity.class, ENTITY_WEARABLE, EntityTravelersBackpackComponent::new);
        registry.registerFor(PiglinEntity.class, ENTITY_WEARABLE, EntityTravelersBackpackComponent::new);
        registry.registerFor(SkeletonEntity.class, ENTITY_WEARABLE, EntityTravelersBackpackComponent::new);
        registry.registerFor(WitherSkeletonEntity.class, ENTITY_WEARABLE, EntityTravelersBackpackComponent::new);
    }
}