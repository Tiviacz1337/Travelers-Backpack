package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class UpgradeItem extends Item
{
    private final UpgradeItem.Upgrade type;

    public UpgradeItem(Properties pProperties, UpgradeItem.Upgrade type)
    {
        super(pProperties);

        this.type = type;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        if(TravelersBackpackConfig.enableTierUpgrades)
        {
            switch(type)
            {
                case BLANK_UPGRADE:
                    tooltip.add(Component.translatable("item.travelersbackpack.blank_upgrade_tooltip").withStyle(ChatFormatting.BLUE));
                    break;
                case IRON_TIER_UPGRADE:
                    tooltip.add(Component.translatable("item.travelersbackpack.tier_upgrade_tooltip", Tiers.LEATHER.getName()).withStyle(ChatFormatting.BLUE));
                    break;
                case GOLD_TIER_UPGRADE:
                    tooltip.add(Component.translatable("item.travelersbackpack.tier_upgrade_tooltip", Tiers.IRON.getName()).withStyle(ChatFormatting.BLUE));
                    break;
                case DIAMOND_TIER_UPGRADE:
                    tooltip.add(Component.translatable("item.travelersbackpack.tier_upgrade_tooltip", Tiers.GOLD.getName()).withStyle(ChatFormatting.BLUE));
                    break;
                case NETHERITE_TIER_UPGRADE:
                    tooltip.add(Component.translatable("item.travelersbackpack.tier_upgrade_tooltip", Tiers.DIAMOND.getName()).withStyle(ChatFormatting.BLUE));
                    break;
            }
        }
        else
        {
            addTooltipForUpgrades(tooltip, Component.translatable("item.travelersbackpack.upgrade_disabled"),
                    Upgrade.BLANK_UPGRADE, Upgrade.IRON_TIER_UPGRADE, Upgrade.GOLD_TIER_UPGRADE, Upgrade.DIAMOND_TIER_UPGRADE, Upgrade.NETHERITE_TIER_UPGRADE);
        }

        if(type == Upgrade.CRAFTING_UPGRADE)
        {
            if(TravelersBackpackConfig.enableCraftingUpgrade)
            {
                tooltip.add(Component.translatable("item.travelersbackpack.crafting_upgrade_tooltip").withStyle(ChatFormatting.BLUE));
            }
            else
            {
                tooltip.add(Component.translatable("item.travelersbackpack.upgrade_disabled").withStyle(ChatFormatting.RED));
            }
        }
    }

    public void addTooltipForUpgrades(List<Component> tooltip, Component component, Upgrade... upgrades)
    {
        Arrays.stream(upgrades).forEach(upgrade -> tooltip.add(component));
    }

    public static List<Supplier<Item>> upgrades = Arrays.asList(
            () -> ModItems.IRON_TIER_UPGRADE.get(),
            () -> ModItems.GOLD_TIER_UPGRADE.get(),
            () -> ModItems.DIAMOND_TIER_UPGRADE.get(),
            () -> ModItems.NETHERITE_TIER_UPGRADE.get());

    public static NonNullList<ItemStack> getUpgrades(ITravelersBackpackContainer container)
    {
        NonNullList<ItemStack> list = NonNullList.create();

        for(int i = 1; i <= container.getTier().getOrdinal(); i++)
        {
            list.add(upgrades.get(i - 1).get().getDefaultInstance());
        }

        if(container.getSettingsManager().hasCraftingGrid())
        {
            list.add(ModItems.CRAFTING_UPGRADE.get().getDefaultInstance());
        }

        return list;
    }

    public enum Upgrade
    {
        BLANK_UPGRADE,
        IRON_TIER_UPGRADE,
        GOLD_TIER_UPGRADE,
        DIAMOND_TIER_UPGRADE,
        NETHERITE_TIER_UPGRADE,
        CRAFTING_UPGRADE;
    }
}