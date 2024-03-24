package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
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

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class UpgradeItem extends Item
{
    private final Upgrade type;

    public UpgradeItem(Settings settings, Upgrade type)
    {
        super(settings);

        this.type = type;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        if(TravelersBackpackConfig.getConfig().backpackSettings.enableTierUpgrades)
        {
            switch(type)
            {
                case BLANK_UPGRADE:
                    tooltip.add(Text.translatable("item.travelersbackpack.blank_upgrade_tooltip").formatted(Formatting.BLUE));
                    break;
                case IRON_TIER_UPGRADE:
                    tooltip.add(Text.translatable("item.travelersbackpack.tier_upgrade_tooltip", Tiers.LEATHER.getName()).formatted(Formatting.BLUE));
                    break;
                case GOLD_TIER_UPGRADE:
                    tooltip.add(Text.translatable("item.travelersbackpack.tier_upgrade_tooltip", Tiers.IRON.getName()).formatted(Formatting.BLUE));
                    break;
                case DIAMOND_TIER_UPGRADE:
                    tooltip.add(Text.translatable("item.travelersbackpack.tier_upgrade_tooltip", Tiers.GOLD.getName()).formatted(Formatting.BLUE));
                    break;
                case NETHERITE_TIER_UPGRADE:
                    tooltip.add(Text.translatable("item.travelersbackpack.tier_upgrade_tooltip", Tiers.DIAMOND.getName()).formatted(Formatting.BLUE));
                    break;
            }
        }
        else
        {
            if(type != Upgrade.CRAFTING_UPGRADE)
            {
                tooltip.add(Text.translatable("item.travelersbackpack.upgrade_disabled"));
            }
        }

        if(type == Upgrade.CRAFTING_UPGRADE)
        {
            if(TravelersBackpackConfig.getConfig().backpackSettings.enableCraftingUpgrade)
            {
                tooltip.add(Text.translatable("item.travelersbackpack.crafting_upgrade_tooltip").formatted(Formatting.BLUE));
            }
            else
            {
                tooltip.add(Text.translatable("item.travelersbackpack.upgrade_disabled").formatted(Formatting.RED));
            }
        }
    }

    public static List<Supplier<Item>> upgrades = Arrays.asList(
            () -> ModItems.IRON_TIER_UPGRADE,
            () -> ModItems.GOLD_TIER_UPGRADE,
            () -> ModItems.DIAMOND_TIER_UPGRADE,
            () -> ModItems.NETHERITE_TIER_UPGRADE);

    public static DefaultedList<ItemStack> getUpgrades(ITravelersBackpackInventory inventory)
    {
        DefaultedList<ItemStack> list = DefaultedList.of();

        for(int i = 1; i <= inventory.getTier().getOrdinal(); i++)
        {
            list.add(upgrades.get(i - 1).get().getDefaultStack());
        }

        if(inventory.getSettingsManager().hasCraftingGrid())
        {
            list.add(ModItems.CRAFTING_UPGRADE.getDefaultStack());
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