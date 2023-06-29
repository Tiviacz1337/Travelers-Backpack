package com.tiviacz.travelersbackpack.compat.universalgraves;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import eu.pb4.graves.GravesApi;
import eu.pb4.graves.grave.GraveInventoryMask;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class UniversalGravesCompat implements GraveInventoryMask
{
    public static final GraveInventoryMask INSTANCE = new UniversalGravesCompat();

    public static void register()
    {
        GravesApi.registerInventoryMask(new Identifier("universal_graves", "travelers_backpack"), INSTANCE);
    }

    @Override
    public void addToGrave(ServerPlayerEntity serverPlayerEntity, ItemConsumer itemConsumer)
    {
        if(ComponentUtils.isWearingBackpack(serverPlayerEntity))
        {
            ItemStack stack = ComponentUtils.getWearingBackpack(serverPlayerEntity);

            if(GravesApi.canAddItem(serverPlayerEntity, stack))
            {
                itemConsumer.addItem(stack, 0);

                ComponentUtils.getComponent(serverPlayerEntity).removeWearable();

                //Sync
                ComponentUtils.sync(serverPlayerEntity);
                ComponentUtils.syncToTracking(serverPlayerEntity);
            }
        }
    }

    @Override
    public boolean moveToPlayerExactly(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, int i, @Nullable NbtElement nbtElement)
    {
        if(!ComponentUtils.isWearingBackpack(serverPlayerEntity))
        {
            ItemStack stack = itemStack.copy();
            ComponentUtils.getComponent(serverPlayerEntity).setWearable(stack);
            ComponentUtils.getComponent(serverPlayerEntity).setContents(stack);

            //Sync
            ComponentUtils.sync(serverPlayerEntity);
            ComponentUtils.syncToTracking(serverPlayerEntity);

            serverPlayerEntity.getWorld().playSound(null, serverPlayerEntity.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (serverPlayerEntity.getWorld().random.nextFloat() - serverPlayerEntity.getWorld().random.nextFloat()) * 0.2F) * 0.7F);

            itemStack.setCount(0);
            return true;
        }
        return false;
    }

    @Override
    public boolean moveToPlayerClosest(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, int i, @Nullable NbtElement nbtElement)
    {
        return false;
    }
}