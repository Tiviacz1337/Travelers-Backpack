package com.tiviacz.travelersbackpack.compat.pneumonogravestones;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pneumono.gravestones.api.GravestoneDataType;

public class BackpackDataType extends GravestoneDataType {
    private static final String KEY = "travelersbackpack";

    @Override
    public void writeData(CompoundTag compoundTag, DynamicOps<Tag> dynamicOps, Player player) throws Exception {
        if(TravelersBackpack.enableIntegration()) return;
        ComponentUtils.getComponent(player).ifPresent(component -> {
            if(component.hasBackpack()) {
                DataResult<Tag> result = ItemStack.OPTIONAL_CODEC.encodeStart(dynamicOps, component.getBackpack());
                compoundTag.put(KEY, result.result().orElseThrow());
                component.remove();
            }
        });
    }

    @Override
    public void onBreak(CompoundTag compoundTag, DynamicOps<Tag> dynamicOps, Level level, BlockPos blockPos, int i) throws Exception {
        ItemStack backpack = ItemStack.OPTIONAL_CODEC.decode(dynamicOps, compoundTag.getCompound(KEY)).result().orElseThrow().getFirst();

        if(backpack.getItem() instanceof TravelersBackpackItem) {
            dropStack(level, blockPos, backpack);
        }
    }

    @Override
    public void onCollect(CompoundTag compoundTag, DynamicOps<Tag> dynamicOps, Level level, BlockPos blockPos, Player player, int i) throws Exception {
        if(TravelersBackpack.enableIntegration()) return;
        ItemStack backpack = ItemStack.OPTIONAL_CODEC.decode(dynamicOps, compoundTag.getCompound(KEY)).result().orElseThrow().getFirst();

        if(backpack.getItem() instanceof TravelersBackpackItem) {
            if(!ComponentUtils.isWearingBackpack(player)) {
                ComponentUtils.equipBackpack(player, backpack);
            } else {
                dropStack(level, blockPos, backpack);
            }
        }
    }
}
