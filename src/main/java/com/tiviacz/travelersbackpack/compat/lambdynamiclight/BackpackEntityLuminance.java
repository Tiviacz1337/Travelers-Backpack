package com.tiviacz.travelersbackpack.compat.lambdynamiclight;

import com.tiviacz.travelersbackpack.attachment.AttachmentUtils;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Range;

public class BackpackEntityLuminance implements EntityLuminance {
    public static final BackpackEntityLuminance INSTANCE = new BackpackEntityLuminance();

    private BackpackEntityLuminance() {}

    @Override
    public Type type() {
        return BackpackDynamicLight.TYPE;
    }

    @Override
    public @Range(from = 0, to = 15) int getLuminance(ItemLightSourceManager itemLightSourceManager, Entity entity) {
        if(entity instanceof Player player) {
            ItemStack backpack = AttachmentUtils.getWearingBackpack(player);
            if(!backpack.isEmpty()) {
                RenderInfo info = backpack.getOrDefault(ModDataComponents.RENDER_INFO, RenderInfo.EMPTY);
                return info.hasLantern() ? 15 : 0;
            }
        }
        return 0;
    }
}