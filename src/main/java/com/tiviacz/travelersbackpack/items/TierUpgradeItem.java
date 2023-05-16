package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class TierUpgradeItem extends Item
{
    private final Tiers.Tier tier;

    public TierUpgradeItem(Properties pProperties, Tiers.Tier tier)
    {
        super(pProperties);

        this.tier = tier;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        if(TravelersBackpackConfig.enableTierUpgrades)
        {
            if(this != ModItems.BLANK_UPGRADE.get())
            {
                tooltip.add(new TranslatableComponent("item.travelersbackpack.tier_upgrade_tooltip", this.tier.getName()).withStyle(ChatFormatting.BLUE));
            }
            else
            {
                tooltip.add(new TranslatableComponent("item.travelersbackpack.blank_upgrade_tooltip").withStyle(ChatFormatting.BLUE));
            }
        }
        else
        {
            tooltip.add(new TranslatableComponent("item.travelersbackpack.tier_upgrade_disabled"));
        }
    }

    public static NonNullList<ItemStack> getUpgradesForTier(Tiers.Tier tier)
    {
        NonNullList<ItemStack> list = NonNullList.create();

        if(tier.getOrdinal() >= 1)
        {
            list.add(ModItems.IRON_TIER_UPGRADE.get().getDefaultInstance());

            if(tier.getOrdinal() >= 2)
            {
                list.add(ModItems.GOLD_TIER_UPGRADE.get().getDefaultInstance());

                if(tier.getOrdinal() >= 3)
                {
                    list.add(ModItems.DIAMOND_TIER_UPGRADE.get().getDefaultInstance());

                    if(tier.getOrdinal() >= 4)
                    {
                        list.add(ModItems.NETHERITE_TIER_UPGRADE.get().getDefaultInstance());
                    }
                }
            }
        }
        return list;
    }
}