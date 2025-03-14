package com.tiviacz.travelersbackpack.item.upgrades;

import com.tiviacz.travelersbackpack.components.Fluids;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.util.FluidTypeHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
import java.util.Optional;

public class TanksUpgradeItem extends UpgradeItem {
    public TanksUpgradeItem(Properties pProperties) {
        super(pProperties.component(ModDataComponents.FLUIDS, Fluids.empty()), "tanks_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.getConfig().backpackUpgrades.enableTanksUpgrade && super.isEnabled(enabledFeatures);
    }

    public static boolean canBePutInBackpack(long backpackFluidStorageSize, ItemStack tanksUpgrade) {
        long[] fluidTanks = new long[]{0, 0};
        if(tanksUpgrade.has(ModDataComponents.FLUIDS)) {
            Fluids fluidTanks2 = tanksUpgrade.get(ModDataComponents.FLUIDS);
            fluidTanks[0] = fluidTanks2.leftFluidStack().getAmount();
            fluidTanks[1] = fluidTanks2.rightFluidStack().getAmount();
        }
        return backpackFluidStorageSize >= fluidTanks[0] && backpackFluidStorageSize >= fluidTanks[1];
    }

    public static FluidVariantWrapper getLeftFluidStack(ItemStack tanksUpgrade) {
        if(tanksUpgrade.has(ModDataComponents.FLUIDS)) {
            Fluids fluidTanks2 = tanksUpgrade.get(ModDataComponents.FLUIDS);
            return fluidTanks2.leftFluidStack();
        }
        return FluidVariantWrapper.blank();
    }

    public static FluidVariantWrapper getRightFluidStack(ItemStack tanksUpgrade) {
        if(tanksUpgrade.has(ModDataComponents.FLUIDS)) {
            Fluids fluidTanks2 = tanksUpgrade.get(ModDataComponents.FLUIDS);
            return fluidTanks2.rightFluidStack();
        }
        return FluidVariantWrapper.blank();
    }

    public static RenderInfo writeToRenderData() {
        CompoundTag tag = new CompoundTag();
        tag.put("LeftTank", new CompoundTag());
        tag.put("RightTank", new CompoundTag());
        return new RenderInfo(tag);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        if(stack.has(ModDataComponents.FLUIDS)) {
            Fluids fluidTanks = stack.get(ModDataComponents.FLUIDS);
            FluidVariantWrapper leftFluidStack = fluidTanks.leftFluidStack();
            FluidVariantWrapper rightFluidStack = fluidTanks.rightFluidStack();

            if(!leftFluidStack.isEmpty()) {
                tooltipComponents.add(Component.literal(FluidTypeHelper.getFluidVariantName(leftFluidStack.fluidVariant()).getString() + ": " + leftFluidStack.getAmount() + "mB").withStyle(ChatFormatting.BLUE));
            }
            if(!rightFluidStack.isEmpty()) {
                tooltipComponents.add(Component.literal(FluidTypeHelper.getFluidVariantName(rightFluidStack.fluidVariant()).getString() + ": " + rightFluidStack.getAmount() + "mB").withStyle(ChatFormatting.BLUE));
            }
        }
    }

    @Override
    public TriFunction<UpgradeManager, Integer, ItemStack, Optional<? extends UpgradeBase<?>>> getUpgrade() {
        return (upgradeManager, dataHolderSlot, provider) -> {
            Fluids fluids = provider.getOrDefault(ModDataComponents.FLUIDS, new Fluids(FluidVariantWrapper.blank(), FluidVariantWrapper.blank()));
            return Optional.of(new TanksUpgrade(upgradeManager, dataHolderSlot, fluids));
        };
    }
}
