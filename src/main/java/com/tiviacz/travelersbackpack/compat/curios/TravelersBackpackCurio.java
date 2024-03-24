package com.tiviacz.travelersbackpack.compat.curios;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;

public class TravelersBackpackCurio implements ICurio
{
    public final ItemStack stack;

    public TravelersBackpackCurio(ItemStack stack)
    {
        this.stack = stack;
    }

    @Override
    public ItemStack getStack()
    {
        return this.stack;
    }

    @Override
    public boolean canEquip(String identifier, LivingEntity livingEntity)
    {
        return TravelersBackpackConfig.curiosIntegration;
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack)
    {
        if(slotContext.entity() instanceof Player player)
        {
            AttachmentUtils.getAttachment(player).ifPresent(data ->
            {
                data.setWearable(stack);
                data.setContents(stack);
                //cap.synchronise();
            });
        }
    }

    @Override
    public void onEquipFromUse(SlotContext slotContext)
    {
        if(slotContext.entity() instanceof Player player)
        {
            AttachmentUtils.getAttachment(player).ifPresent(data ->
            {
                data.setWearable(stack);
                data.setContents(stack);
                //cap.synchronise();
            });
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack)
    {
        if(slotContext.entity() instanceof Player player)
        {
            AttachmentUtils.getAttachment(player).ifPresent(ITravelersBackpack::removeWearable);
        }
    }

    @Nonnull
    @Override
    public ICurio.DropRule getDropRule(LivingEntity livingEntity)
    {
        return livingEntity.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) ? DropRule.ALWAYS_KEEP : TravelersBackpack.isAnyGraveModInstalled() ? DropRule.DEFAULT : DropRule.DESTROY;
    }
}