package com.tiviacz.travelersbackpack.component;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.entity.EntityTravelersBackpackComponent;
import com.tiviacz.travelersbackpack.component.entity.IEntityTravelersBackpackComponent;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
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

    public static ITravelersBackpackComponent getComponent(PlayerEntity player)
    {
        return WEARABLE.get(player);
    }

    public static IEntityTravelersBackpackComponent getComponent(LivingEntity livingEntity)
    {
        return ENTITY_WEARABLE.get(livingEntity);
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
        if(player instanceof ServerPlayerEntity)
        {
            getComponent(player).syncToTracking((ServerPlayerEntity)player);
        }
    }

    public static void synchroniseEntity(LivingEntity livingEntity)
    {
        getComponent(livingEntity).sync();
    }

    public static boolean isWearingBackpack(PlayerEntity player)
    {
        return WEARABLE.get(player).hasWearable() && WEARABLE.get(player).getWearable().getItem() instanceof TravelersBackpackItem;
    }

    public static boolean isWearingBackpack(LivingEntity livingEntity)
    {
        return ENTITY_WEARABLE.get(livingEntity).hasWearable() && ENTITY_WEARABLE.get(livingEntity).getWearable().getItem() instanceof TravelersBackpackItem;
    }

    public static ItemStack getWearingBackpack(PlayerEntity player)
    {
        return isWearingBackpack(player) ? WEARABLE.get(player).getWearable() : ItemStack.EMPTY;
    }

    public static ItemStack getWearingBackpack(LivingEntity livingEntity)
    {
        return isWearingBackpack(livingEntity) ? ENTITY_WEARABLE.get(livingEntity).getWearable() : ItemStack.EMPTY;
    }

    public static void equipBackpack(PlayerEntity player, ItemStack stack)
    {
        if(player.world.isClient) return;

        if(!WEARABLE.get(player).hasWearable())
        {
            WEARABLE.get(player).setWearable(stack);
            WEARABLE.get(player).setContents(stack);
            player.world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (player.world.random.nextFloat() - player.world.random.nextFloat()) * 0.2F) * 0.7F);
        }

        sync(player);
        syncToTracking(player);
    }

    @Nullable
    public static TravelersBackpackInventory getBackpackInv(PlayerEntity player)
    {
        ItemStack wearable = getWearingBackpack(player);

        if(wearable.getItem() instanceof TravelersBackpackItem)
        {
            return WEARABLE.get(player).getInventory();
            //return new TravelersBackpackInventory(wearable, player, Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID);
        }
        return null;
    }
}