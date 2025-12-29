package com.tiviacz.travelersbackpack.compat.pneumonogravestones;

import com.mojang.serialization.DynamicOps;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pneumono.gravestones.api.GravestoneDataType;

public class BackpackDataType extends GravestoneDataType {
    private static final String KEY = "travelersbackpack";

    @Override
    public void writeData(CompoundTag compoundTag, DynamicOps<Tag> dynamicOps, Player player) throws Exception {
        if(TravelersBackpack.enableIntegration()) return;
        ComponentUtils.getComponentOptional(player).ifPresent(component -> {
            if(component.hasBackpack()) {
                compoundTag.put(KEY, component.getBackpack().save(new CompoundTag()));
                component.remove();
            }
        });
    }

    @Override
    public void onBreak(CompoundTag compoundTag, DynamicOps<Tag> dynamicOps, Level level, BlockPos blockPos, int i) throws Exception {
        ItemStack backpack = ItemStack.of(compoundTag.getCompound(KEY));

        if(backpack.getItem() instanceof TravelersBackpackItem) {
            Containers.dropItemStack(level, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), backpack);
        }
    }

    @Override
    public void onCollect(CompoundTag compoundTag, DynamicOps<Tag> dynamicOps, Level level, BlockPos blockPos, Player player, int i) throws Exception {
        if(TravelersBackpack.enableIntegration()) return;
        ItemStack backpack = ItemStack.of(compoundTag.getCompound(KEY));

        if(backpack.getItem() instanceof TravelersBackpackItem) {
            if(!ComponentUtils.isWearingBackpack(player)) {
                ComponentUtils.equipBackpack(player, backpack);
            } else {
                Containers.dropItemStack(level, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), backpack);
            }
        }
    }
}
