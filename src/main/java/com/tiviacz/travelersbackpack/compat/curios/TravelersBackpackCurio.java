package com.tiviacz.travelersbackpack.compat.curios;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;

public record TravelersBackpackCurio(ItemStack stack) implements ICurio
{
    @Override
    public ItemStack getStack()
    {
        return this.stack;
    }

    @Override
    public boolean canEquip(SlotContext context)
    {
        return TravelersBackpackConfig.SERVER.backpackSettings.curiosIntegration.get();
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack)
    {
        if(!TravelersBackpackConfig.SERVER.backpackSettings.curiosIntegration.get()) return;

        if(slotContext.entity() instanceof Player player)
        {
            AttachmentUtils.getAttachment(player).ifPresent(data ->
            {
                data.setWearable(stack);
                data.setContents(stack);
            });
        }
    }

    @Override
    public void onEquipFromUse(SlotContext slotContext)
    {
        if(!TravelersBackpackConfig.SERVER.backpackSettings.curiosIntegration.get()) return;

        if(slotContext.entity() instanceof Player player)
        {
            AttachmentUtils.getAttachment(player).ifPresent(data ->
            {
                data.setWearable(stack);
                data.setContents(stack);
            });
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack)
    {
        if(!TravelersBackpackConfig.SERVER.backpackSettings.curiosIntegration.get()) return;

        if(slotContext.entity() instanceof Player player)
        {
            AttachmentUtils.getAttachment(player).ifPresent(ITravelersBackpack::removeWearable);
        }
    }

    @Nonnull
    @Override
    public ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit)
    {
        return slotContext.entity().level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) ? DropRule.ALWAYS_KEEP : TravelersBackpack.isAnyGraveModInstalled() ? DropRule.DEFAULT : DropRule.DESTROY;
    }
}