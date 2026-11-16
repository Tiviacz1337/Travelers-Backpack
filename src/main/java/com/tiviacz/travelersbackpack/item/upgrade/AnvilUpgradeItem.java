package com.tiviacz.travelersbackpack.item.upgrade;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.anvil.AnvilUpgrade;
import com.tiviacz.travelersbackpack.util.ContainerContentsHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Optional;
import java.util.function.Consumer;

public class AnvilUpgradeItem extends UpgradeItem {
    /**
     * Same as the vanilla anvil: intact -> chipped -> damaged -> destroyed
     */
    public static final int DURABILITY = 3;
    private final boolean breakable;

    public AnvilUpgradeItem(Properties properties) {
        this(properties, true);
    }

    public AnvilUpgradeItem(Properties properties, boolean breakable) {
        //Tooltip is built in appendHoverText
        super(properties, null);
        this.breakable = breakable;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag) {
        //Single tooltip line made of two translations: description + break info (breakable only)
        MutableComponent tooltip = Component.translatable("item.travelersbackpack.anvil_upgrade_tooltip");
        if(this.breakable) {
            tooltip.append(CommonComponents.SPACE).append(Component.translatable("item.travelersbackpack.anvil_upgrade_destroy_tooltip"));
        }
        componentConsumer.accept(tooltip.withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, context, tooltipDisplay, componentConsumer, tooltipFlag);
    }

    /**
     * Damage is stored in a custom component instead of vanilla DAMAGE,
     * so the upgrade can't be repaired by combining (crafting grid, grindstone, anvil) - just like the vanilla anvil
     */
    public boolean isBreakable() {
        return this.breakable;
    }

    public static int getAnvilDamage(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.ANVIL_DAMAGE, 0);
    }

    //Durability bar, same look as vanilla Item#isBarVisible/getBarWidth/getBarColor
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return this.breakable && getAnvilDamage(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Mth.clamp(Math.round(13.0F - getAnvilDamage(stack) * 13.0F / DURABILITY), 0, 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float health = Math.max(0.0F, (float)(DURABILITY - getAnvilDamage(stack)) / DURABILITY);
        return Mth.hsvToRgb(health / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return (!TravelersBackpackConfig.serverSpec.isLoaded() || TravelersBackpackConfig.SERVER.backpackUpgrades.enableAnvilUpgrade.get())
                && super.isEnabled(enabledFeatures);
    }

    @Override
    public boolean requiresEquippedBackpack() {
        return false;
    }

    @Override
    public Class<? extends UpgradeBase<?>> getUpgradeClass() {
        return AnvilUpgrade.class;
    }

    @Override
    public TriFunction<UpgradeManager, Integer, ItemStack, Optional<? extends UpgradeBase<?>>> getUpgrade() {
        return (manager, slot, provider) -> {
            ItemContainerContents contents = provider.getOrDefault(ModDataComponents.BACKPACK_CONTAINER, ItemContainerContents.EMPTY);
            NonNullList<ItemStack> items = ContainerContentsHelper.getItems(contents, 2);
            return Optional.of(new AnvilUpgrade(manager, slot, items));
        };
    }
}
