package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.components.Fluids;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
import java.util.Optional;

public class TanksUpgradeItem extends UpgradeItem {
    public TanksUpgradeItem(Properties pProperties) {
        super(pProperties, "tanks_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.enableTanksUpgrade.get() && super.isEnabled(enabledFeatures);
    }

    public static boolean canBePutInBackpack(int backpackFluidStorageSize, ItemStack tanksUpgrade) {
        int[] fluidTanks = new int[]{0, 0};
        if(NbtHelper.has(tanksUpgrade, ModDataHelper.FLUIDS)) {
            Fluids fluidTanks2 = NbtHelper.get(tanksUpgrade, ModDataHelper.FLUIDS);
            fluidTanks[0] = fluidTanks2.leftFluidStack().getAmount();
            fluidTanks[1] = fluidTanks2.rightFluidStack().getAmount();
        }
        return backpackFluidStorageSize >= fluidTanks[0] && backpackFluidStorageSize >= fluidTanks[1];
    }

    public static FluidStack getLeftFluidStack(ItemStack tanksUpgrade) {
        if(NbtHelper.has(tanksUpgrade, ModDataHelper.FLUIDS)) {
            Fluids fluidTanks2 = NbtHelper.get(tanksUpgrade, ModDataHelper.FLUIDS);
            return fluidTanks2.leftFluidStack();
        }
        return FluidStack.EMPTY;
    }

    public static FluidStack getRightFluidStack(ItemStack tanksUpgrade) {
        if(NbtHelper.has(tanksUpgrade, ModDataHelper.FLUIDS)) {
            Fluids fluidTanks2 = NbtHelper.get(tanksUpgrade, ModDataHelper.FLUIDS);
            return fluidTanks2.rightFluidStack();
        }
        return FluidStack.EMPTY;
    }

    public static RenderInfo writeToRenderData() {
        CompoundTag tag = new CompoundTag();
        tag.put("LeftTank", new CompoundTag());
        tag.put("RightTank", new CompoundTag());
        return new RenderInfo(tag);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);

        if(NbtHelper.has(stack, ModDataHelper.FLUIDS)) {
            Fluids fluidTanks = NbtHelper.get(stack, ModDataHelper.FLUIDS);
            FluidStack leftFluidStack = fluidTanks.leftFluidStack();
            FluidStack rightFluidStack = fluidTanks.rightFluidStack();

            if(!leftFluidStack.isEmpty()) {
                tooltipComponents.add(Component.literal(leftFluidStack.getFluid().getFluidType().getDescription().getString() + ": " + leftFluidStack.getAmount() + "mB").withStyle(ChatFormatting.BLUE));
            }
            if(!rightFluidStack.isEmpty()) {
                tooltipComponents.add(Component.literal(rightFluidStack.getFluid().getFluidType().getDescription().getString() + ": " + rightFluidStack.getAmount() + "mB").withStyle(ChatFormatting.BLUE));
            }
        }
    }

    @Override
    public boolean requiresEquippedBackpack() {
        return false;
    }

    @Override
    public Class<? extends UpgradeBase<?>> getUpgradeClass() {
        return TanksUpgrade.class;
    }

    @Override
    public TriFunction<UpgradeManager, Integer, ItemStack, Optional<? extends UpgradeBase<?>>> getUpgrade() {
        return (upgradeManager, dataHolderSlot, provider) -> {
            Fluids fluids = NbtHelper.getOrDefault(provider, ModDataHelper.FLUIDS, new Fluids(FluidStack.EMPTY, FluidStack.EMPTY));
            return Optional.of(new TanksUpgrade(upgradeManager, dataHolderSlot, fluids));
        };
    }
}