package com.tiviacz.travelersbackpack.compat.pneumonogravestones;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pneumono.gravestones.api.GravestoneDataType;
import net.pneumono.gravestones.multiversion.VersionUtil;

import java.util.Optional;

public class BackpackDataType extends GravestoneDataType {
    private static final String KEY = "travelersbackpack";

    @Override
    public void writeData(CompoundTag nbt, DynamicOps<Tag> ops, Player player) {
        if(TravelersBackpack.enableIntegration()) return;
        ComponentUtils.getComponent(player).ifPresent(component -> {
            if(component.hasBackpack()) {
                DataResult<Tag> result = ItemStack.OPTIONAL_CODEC.encodeStart(ops, component.getBackpack());
                nbt.put(KEY, (Tag)result.result().orElseThrow());
                component.remove();
            }
        });
    }

    @Override
    public void onBreak(CompoundTag nbt, DynamicOps<Tag> ops, Level world, BlockPos pos, int decay) {
        Optional<ItemStack> optional = VersionUtil.get(nbt, KEY, ItemStack.OPTIONAL_CODEC);
        optional.ifPresent(backpack -> this.dropStack(world, pos, backpack));
    }

    public void onCollect(CompoundTag nbt, DynamicOps<Tag> ops, Level world, BlockPos pos, Player player, int decay) {
        if(TravelersBackpack.enableIntegration()) return;
        Optional<ItemStack> optional = VersionUtil.get(nbt, KEY, ItemStack.OPTIONAL_CODEC);

        optional.ifPresent(backpack -> {
            if(!ComponentUtils.isWearingBackpack(player)) {
                ComponentUtils.equipBackpack(player, backpack);
            } else {
                this.dropStack(player, backpack);
            }
        });
    }
}