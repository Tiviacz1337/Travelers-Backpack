package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TierUpgradeItem extends Item
{
    private final Tiers.Tier tier;

    public TierUpgradeItem(Settings settings, Tiers.Tier tier)
    {
        super(settings);

        this.tier = tier;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        if(TravelersBackpackConfig.enableTierUpgrades)
        {
            if(this != ModItems.BLANK_UPGRADE)
            {
                tooltip.add(Text.translatable("item.travelersbackpack.tier_upgrade_tooltip", this.tier.getName()).formatted(Formatting.BLUE));
            }
            else
            {
                tooltip.add(Text.translatable("item.travelersbackpack.blank_upgrade_tooltip").formatted(Formatting.BLUE));
            }
        }
        else
        {
            tooltip.add(Text.translatable("item.travelersbackpack.tier_upgrade_disabled"));
        }
    }

    public static DefaultedList<ItemStack> getUpgradesForTier(Tiers.Tier tier)
    {
        DefaultedList<ItemStack> list = DefaultedList.of();

        if(tier.getOrdinal() >= 1)
        {
            list.add(ModItems.IRON_TIER_UPGRADE.getDefaultStack());

            if(tier.getOrdinal() >= 2)
            {
                list.add(ModItems.GOLD_TIER_UPGRADE.getDefaultStack());

                if(tier.getOrdinal() >= 3)
                {
                    list.add(ModItems.DIAMOND_TIER_UPGRADE.getDefaultStack());

                    if(tier.getOrdinal() >= 4)
                    {
                        list.add(ModItems.NETHERITE_TIER_UPGRADE.getDefaultStack());
                    }
                }
            }
        }
        return list;
    }
}