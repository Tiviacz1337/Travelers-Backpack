package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.components.Fluids;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import com.tiviacz.travelersbackpack.util.FluidTypeHelper;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class TanksUpgradeItem extends UpgradeItem {
    public TanksUpgradeItem(Properties pProperties) {
        super(pProperties, "tanks_upgrade"); //.component(ModDataComponents.FLUIDS.get(), Fluids.empty()), "tanks_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.getConfig().backpackUpgrades.enableTanksUpgrade && super.isEnabled(enabledFeatures);
    }

    public static boolean canBePutInBackpack(long backpackFluidStorageSize, ItemStack tanksUpgrade) {
        long[] fluidTanks = new long[]{0, 0};
        if(NbtHelper.has(tanksUpgrade, ModDataHelper.FLUIDS)) { //tanksUpgrade.has(ModDataComponents.FLUIDS.get())) {
            Fluids fluidTanks2 = NbtHelper.get(tanksUpgrade, ModDataHelper.FLUIDS); //tanksUpgrade.get(ModDataComponents.FLUIDS.get());
            fluidTanks[0] = fluidTanks2.leftFluidStack().getAmount();
            fluidTanks[1] = fluidTanks2.rightFluidStack().getAmount();
        }
        return backpackFluidStorageSize >= fluidTanks[0] && backpackFluidStorageSize >= fluidTanks[1];
    }

    public static FluidVariantWrapper getLeftFluidStack(ItemStack tanksUpgrade) {
        if(NbtHelper.has(tanksUpgrade, ModDataHelper.FLUIDS)) { //tanksUpgrade.has(ModDataComponents.FLUIDS.get())) {
            Fluids fluidTanks2 = NbtHelper.get(tanksUpgrade, ModDataHelper.FLUIDS); //tanksUpgrade.get(ModDataComponents.FLUIDS.get());
            return fluidTanks2.leftFluidStack();
        }
        return FluidVariantWrapper.blank();
    }

    public static FluidVariantWrapper getRightFluidStack(ItemStack tanksUpgrade) {
        if(NbtHelper.has(tanksUpgrade, ModDataHelper.FLUIDS)) {
            Fluids fluidTanks2 = NbtHelper.get(tanksUpgrade, ModDataHelper.FLUIDS);
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
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);

        if(NbtHelper.has(stack, ModDataHelper.FLUIDS)) { //stack.has(ModDataComponents.FLUIDS.get())) {
            Fluids fluidTanks = NbtHelper.get(stack, ModDataHelper.FLUIDS); //stack.get(ModDataComponents.FLUIDS.get());
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
}
