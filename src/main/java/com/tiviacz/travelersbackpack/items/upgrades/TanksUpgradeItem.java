package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.components.Fluids;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class TanksUpgradeItem extends UpgradeItem {
    public TanksUpgradeItem(Properties pProperties) {
        super(pProperties.component(ModDataComponents.FLUIDS.get(), Fluids.empty()), "tanks_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.enableTanksUpgrade.get() && super.isEnabled(enabledFeatures);
    }

    public static boolean canBePutInBackpack(int backpackFluidStorageSize, ItemStack tanksUpgrade) {
        int[] fluidTanks = new int[]{0, 0};
        if(tanksUpgrade.has(ModDataComponents.FLUIDS.get())) {
            Fluids fluidTanks2 = tanksUpgrade.get(ModDataComponents.FLUIDS.get());
            fluidTanks[0] = fluidTanks2.leftFluidStack().getAmount();
            fluidTanks[1] = fluidTanks2.rightFluidStack().getAmount();
        }
        return backpackFluidStorageSize >= fluidTanks[0] && backpackFluidStorageSize >= fluidTanks[1];
    }

    public static FluidStack getLeftFluidStack(ItemStack tanksUpgrade) {
        if(tanksUpgrade.has(ModDataComponents.FLUIDS.get())) {
            Fluids fluidTanks2 = tanksUpgrade.get(ModDataComponents.FLUIDS.get());
            return fluidTanks2.leftFluidStack();
        }
        return FluidStack.EMPTY;
    }

    public static FluidStack getRightFluidStack(ItemStack tanksUpgrade) {
        if(tanksUpgrade.has(ModDataComponents.FLUIDS.get())) {
            Fluids fluidTanks2 = tanksUpgrade.get(ModDataComponents.FLUIDS.get());
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        if(stack.has(ModDataComponents.FLUIDS.get())) {
            Fluids fluidTanks = stack.get(ModDataComponents.FLUIDS.get());
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
}
