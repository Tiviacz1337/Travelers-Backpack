package com.tiviacz.travelersbackpack.compat.universalgraves;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.component.ITravelersBackpack;
import eu.pb4.graves.GravesApi;
import eu.pb4.graves.grave.GraveInventoryMask;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class UniversalGravesCompat implements GraveInventoryMask {
    public static final GraveInventoryMask INSTANCE = new UniversalGravesCompat();

    public static void register() {
        GravesApi.registerInventoryMask(Identifier.fromNamespaceAndPath("universal_graves", "travelers_backpack"), INSTANCE);
    }

    @Override
    public void addToGrave(ServerPlayer serverPlayerEntity, ItemConsumer itemConsumer) {
        if(TravelersBackpack.enableIntegration()) return;

        if(ComponentUtils.isWearingBackpack(serverPlayerEntity)) {
            ItemStack stack = ComponentUtils.getWearingBackpack(serverPlayerEntity);

            if(GravesApi.canAddItem(serverPlayerEntity, stack)) {
                itemConsumer.addItem(stack, 0);

                ComponentUtils.getComponent(serverPlayerEntity).ifPresent(ITravelersBackpack::remove);

                //Sync
                //ComponentUtils.synchronise(serverPlayerEntity);
            }
        }
    }

    @Override
    public boolean moveToPlayerExactly(ServerPlayer serverPlayerEntity, ItemStack itemStack, int i, @Nullable Tag nbtElement) {
        if(TravelersBackpack.enableIntegration()) return false;

        if(!ComponentUtils.isWearingBackpack(serverPlayerEntity)) {
            ItemStack stack = itemStack.copy();
            ComponentUtils.getComponent(serverPlayerEntity).ifPresent(comp -> {
                comp.equipBackpack(stack);
            });

            //Sync
            //ComponentUtils.synchronise(serverPlayerEntity);

            serverPlayerEntity.level().playSound(null, serverPlayerEntity.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (serverPlayerEntity.level().random.nextFloat() - serverPlayerEntity.level().random.nextFloat()) * 0.2F) * 0.7F);

            itemStack.setCount(0);
            return true;
        }
        return false;
    }

    @Override
    public boolean moveToPlayerClosest(ServerPlayer serverPlayerEntity, ItemStack itemStack, int i, @Nullable Tag nbtElement) {
        return false;
    }
}