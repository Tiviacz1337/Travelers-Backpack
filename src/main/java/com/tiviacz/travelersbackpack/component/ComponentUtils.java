package com.tiviacz.travelersbackpack.component;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import dev.emi.trinkets.api.TrinketsApi;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

import java.util.Optional;
import java.util.function.Supplier;

public class ComponentUtils implements EntityComponentInitializer {
    public static final ComponentKey<ITravelersBackpack> WEARABLE = ComponentRegistry.getOrCreate(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "travelersbackpack"), ITravelersBackpack.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(WEARABLE, TravelersBackpackComponent::new, RespawnCopyStrategy.ALWAYS_COPY); //#TODO check
    }

    public static Optional<ITravelersBackpack> getComponent(Player player) {
        if(player == null) {
            return Optional.empty();
        }
        return Optional.of(player.getComponent(WEARABLE));
    }

    public static void synchronise(Player player) {
        if(player instanceof ServerPlayer) {
            getComponent(player).ifPresent(ITravelersBackpack::synchronise);
        }
    }

    public static boolean isWearingBackpack(Player player) {
        if(TravelersBackpack.enableIntegration()) {
            if(TravelersBackpack.enableTrinkets()) {
                if(TrinketsApi.getTrinketComponent(player).isPresent()) {
                    if(TrinketsApi.getTrinketComponent(player).get().isEquipped(t -> t.getItem() instanceof TravelersBackpackItem)) {
                        return true;
                    }
                }
            }
            if(TravelersBackpack.enableAccessories()) {
                if(AccessoriesCapability.get(player) != null) {
                    if(AccessoriesCapability.get(player).isEquipped(t -> t.getItem() instanceof TravelersBackpackItem)) {
                        return true;
                    }
                }
            }
            return false;
        }
        if(getComponent(player).isPresent()) {
            return getComponent(player).get().hasBackpack() && getComponent(player).get().getBackpack().getItem() instanceof TravelersBackpackItem;
        }
        return false;
    }

    public static ItemStack getWearingBackpack(Player player) {
        if(TravelersBackpack.enableIntegration()) {
            if(TravelersBackpack.enableTrinkets()) {
                if(TrinketsApi.getTrinketComponent(player).isPresent()) {
                    if(TrinketsApi.getTrinketComponent(player).get().isEquipped(t -> t.getItem() instanceof TravelersBackpackItem)) {
                        return TrinketsApi.getTrinketComponent(player).get().getEquipped(t -> t.getItem() instanceof TravelersBackpackItem).getFirst().getB();
                    }
                }
            }
            if(TravelersBackpack.enableAccessories()) {
                if(isWearingBackpack(player) && AccessoriesCapability.getOptionally(player).isPresent()) {
                    if(AccessoriesCapability.get(player).getFirstEquipped(t -> t.getItem() instanceof TravelersBackpackItem) != null) {
                        return AccessoriesCapability.get(player).getFirstEquipped(t -> t.getItem() instanceof TravelersBackpackItem).stack();
                    }
                }
            }
            return ItemStack.EMPTY;
        }
        return isWearingBackpack(player) ? getComponent(player).get().getBackpack() : ItemStack.EMPTY;
    }

    public static void equipBackpack(Player player, ItemStack stack) {
        if(getComponent(player).isPresent() && !isWearingBackpack(player)) {
            getComponent(player).ifPresent(attachment -> attachment.equipBackpack(stack));
            player.level().playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.2F) * 0.7F);

            //Sync
            synchronise(player);
        }
    }

    @Nullable
    public static BackpackWrapper getBackpackWrapper(Player player, ItemStack stack) {
        return getBackpackWrapper(player, stack, LOAD_ALL.get());
    }

    @Nullable
    public static BackpackWrapper getBackpackWrapper(Player player, ItemStack stack, int[] dataLoad) {
        if(TravelersBackpack.enableIntegration()) {
            if(isWearingBackpack(player)) {
                return BackpackWrapper.getBackpackWrapper(player, stack, dataLoad);
            }
            return null;
        }
        if(isWearingBackpack(player)) {
            return ComponentUtils.getComponent(player).map(ITravelersBackpack::getWrapper).orElse(null);
        }
        return null;
    }

    //Artificial wrapper for actions that do not require loading items
    @Nullable
    public static BackpackWrapper getBackpackWrapperArtificial(Player player) {
        return getBackpackWrapper(player, NO_ITEMS.get());
    }

    //Fully loaded wrapper
    @Nullable
    public static BackpackWrapper getBackpackWrapper(Player player) {
        return getBackpackWrapper(player, LOAD_ALL.get());
    }

    public static final Supplier<int[]> LOAD_ALL = () -> new int[]{1, 1, 1};
    public static final Supplier<int[]> NO_ITEMS = () -> new int[]{0, 0, 0};
    public static final Supplier<int[]> STORAGE_ONLY = () -> new int[]{1, 0, 0};
    public static final Supplier<int[]> UPGRADES_ONLY = () -> new int[]{0, 1, 0};
    public static final Supplier<int[]> TOOLS_ONLY = () -> new int[]{0, 0, 1};

    //Situational wrapper
    @Nullable
    public static BackpackWrapper getBackpackWrapper(Player player, int[] dataLoad) {
        if(TravelersBackpack.enableIntegration()) {
            if(isWearingBackpack(player)) {
                return BackpackWrapper.getBackpackWrapper(player, getWearingBackpack(player), dataLoad);
            }
            return null;
        }
        if(isWearingBackpack(player)) {
            return ComponentUtils.getComponent(player).map(ITravelersBackpack::getWrapper).orElse(null);
        }
        return null;
    }
}