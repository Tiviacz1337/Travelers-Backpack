package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.components.Fluids;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.Optional;

public class BackpackRenderInfo {
    public ItemStack backpack;
    public RenderInfo info;

    public FluidTank leftTank;
    public FluidTank rightTank;

    public BackpackRenderInfo(ItemStack backpack, RenderInfo info) {
        this.backpack = backpack;
        this.info = info;

        if(info != null && !info.isEmpty()) {
            this.leftTank = new FluidTank(info.getCapacity());
            this.rightTank = new FluidTank(info.getCapacity());
            this.leftTank.setFluid(info.getLeftFluidStack());
            this.rightTank.setFluid(info.getRightFluidStack());
        }
    }

    public boolean isEmpty() {
        return this.info.isEmpty();
    }

    public ItemStack getBackpack() {
        return this.backpack;
    }

    public boolean renderDefault() {
        return !this.backpack.has(ModDataComponents.STORAGE_SLOTS);
    }

    public FluidTank getLeftTank() {
        return this.leftTank;
    }

    public FluidTank getRightTank() {
        return this.rightTank;
    }

    public Optional<Pair<FluidStack, FluidStack>> getTanksContents() {
        if(this.backpack.has(ModDataComponents.UPGRADES)) {
            Optional<ItemStack> tanksUpgrade = this.backpack.get(ModDataComponents.UPGRADES).getItems().stream().filter(stack -> stack.getItem() == ModItems.TANKS_UPGRADE.get()).findFirst();
            if(tanksUpgrade.isPresent()) {
                Fluids tanksInfo = tanksUpgrade.get().get(ModDataComponents.FLUIDS);

                return Optional.of(Pair.of(tanksInfo.leftFluidStack(), tanksInfo.rightFluidStack()));
            }
        }
        return Optional.empty();
    }

    public boolean isDyed() {
        return this.backpack.has(DataComponents.DYED_COLOR) && this.backpack.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get();
    }

    public int getSleepingBagColor() {
        return this.backpack.getOrDefault(ModDataComponents.SLEEPING_BAG_COLOR.get(), DyeColor.RED.getId());
    }

    public boolean hasSleepingBag() {
        return this.backpack.has(ModDataComponents.SLEEPING_BAG_COLOR);
    }
}
