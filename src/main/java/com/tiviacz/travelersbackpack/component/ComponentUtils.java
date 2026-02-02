package com.tiviacz.travelersbackpack.component;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import dev.emi.trinkets.api.TrinketsApi;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ComponentUtils implements EntityComponentInitializer {
    public static final ComponentKey<ITravelersBackpack> WEARABLE = ComponentRegistry.getOrCreate(new ResourceLocation(TravelersBackpack.MODID, "travelersbackpack"), ITravelersBackpack.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(WEARABLE, TravelersBackpackComponent::new, RespawnCopyStrategy.ALWAYS_COPY); //#TODO check
    }

    public static ITravelersBackpackComponent getComponent(Player player) {
        return (ITravelersBackpackComponent)getComponentOptional(player).get();
    }

    public static Optional<ITravelersBackpack> getComponentOptional(Player player) {
        if(player == null) {
            return Optional.empty();
        }
        return Optional.of(player.getComponent(WEARABLE));
    }

    public static void synchronise(Player player) {
        if(player instanceof ServerPlayer) {
            getComponentOptional(player).ifPresent(ITravelersBackpack::synchronise);
        }
    }

    public static boolean isWearingBackpack(Player player) {
        if(TravelersBackpack.enableIntegration()) {
            if(TravelersBackpack.enableTrinkets()) {
                if(TrinketsApi.getTrinketComponent(player).isPresent()) {
                    return TrinketsApi.getTrinketComponent(player).get().isEquipped(t -> t.getItem() instanceof TravelersBackpackItem);
                }
            }
            return false;
        }
        if(getComponentOptional(player).isPresent()) {
            return getComponentOptional(player).get().hasBackpack() && getComponentOptional(player).get().getBackpack().getItem() instanceof TravelersBackpackItem;
        }
        return false;
    }

    public static ItemStack getWearingBackpack(Player player) {
        if(TravelersBackpack.enableIntegration()) {
            if(TravelersBackpack.enableTrinkets()) {
                return isWearingBackpack(player) ? TrinketsApi.getTrinketComponent(player).get().getEquipped(t -> t.getItem() instanceof TravelersBackpackItem).get(0).getB() : ItemStack.EMPTY;
            }
            return ItemStack.EMPTY;
        }
        return isWearingBackpack(player) ? getComponentOptional(player).get().getBackpack() : ItemStack.EMPTY;
    }

    public static void equipBackpack(Player player, ItemStack stack) {
        if(getComponentOptional(player).isPresent() && !isWearingBackpack(player)) {
            getComponentOptional(player).ifPresent(attachment -> attachment.equipBackpack(stack));
            player.level().playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, (1.0F + (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.2F) * 0.7F);

            //Sync
            synchronise(player);
        }
    }

    @Nullable
    public static BackpackWrapper getBackpackWrapper(Player player, ItemStack stack) {
        return getBackpackWrapper(player, stack, loadAll());
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
            return ComponentUtils.getComponentOptional(player).map(ITravelersBackpack::getWrapper).orElse(null);
        }
        return null;
    }

    //Artificial wrapper for actions that do not require loading items
    @Nullable
    public static BackpackWrapper getBackpackWrapperArtificial(Player player) {
        return getBackpackWrapper(player, noItems());
    }

    //Fully loaded wrapper
    @Nullable
    public static BackpackWrapper getBackpackWrapper(Player player) {
        return getBackpackWrapper(player, loadAll());
    }

    public static final int[] LOAD_ALL = new int[]{1, 1, 1};
    public static final int[] NO_ITEMS = new int[]{0, 0, 0};
    public static final int[] STORAGE_ONLY = new int[]{1, 0, 0};
    public static final int[] UPGRADES_ONLY = new int[]{0, 1, 0};
    public static final int[] TOOLS_ONLY = new int[]{0, 0, 1};

    @Nullable
    public static BackpackWrapper getBackpackWrapper(Player player, int[] dataLoad) {
        if(TravelersBackpack.enableIntegration()) {
            if(isWearingBackpack(player)) {
                return BackpackWrapper.getBackpackWrapper(player, getWearingBackpack(player), dataLoad);
            }
            return null;
        }
        if(isWearingBackpack(player)) {
            return ComponentUtils.getComponentOptional(player).map(ITravelersBackpack::getWrapper).orElse(null);
        }
        return null;
    }

    public static int[] loadAll() {
        return new int[] {1, 1, 1};
    }

    public static int[] noItems() {
        return new int[] {0, 0, 0};
    }

    public static int[] storageOnly() {
        return new int[] {1, 0, 0};
    }

    public static int[] upgradesOnly() {
        return new int[] {0, 1, 0};
    }

    public static int[] toolsOnly() {
        return new int[] {0, 0, 1};
    }
}