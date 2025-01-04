package com.tiviacz.travelersbackpack.client.renderer;

import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

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

    // public boolean renderDefault() {
    //     return !this.backpack.has(ModDataComponents.STORAGE_SLOTS.get());
    // }

    public FluidTank getLeftTank() {
        return this.leftTank;
    }

    public FluidTank getRightTank() {
        return this.rightTank;
    }

   /* public Optional<Pair<FluidStack, FluidStack>> getTanksContents() {
        if(this.backpack.has(ModDataComponents.UPGRADES.get())) {
            Optional<ItemStack> tanksUpgrade = this.backpack.get(ModDataComponents.UPGRADES.get()).getItems().stream().filter(stack -> stack.getItem() == ModItems.TANKS_UPGRADE.get()).findFirst();
            if(tanksUpgrade.isPresent()) {
                Fluids tanksInfo = tanksUpgrade.get().get(ModDataComponents.FLUIDS.get());

                return Optional.of(Pair.of(tanksInfo.leftFluidStack(), tanksInfo.rightFluidStack()));
            }
        }
        return Optional.empty();
    } */

    public boolean isDyed() {
        return NbtHelper.has(this.backpack, ModDataHelper.COLOR) && this.backpack.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get();
        //return this.backpack.has(DataComponents.DYED_COLOR) && this.backpack.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get();
    }

    public int getSleepingBagColor() {
        return NbtHelper.getOrDefault(this.backpack, ModDataHelper.SLEEPING_BAG_COLOR, DyeColor.RED.getId());
        // return this.backpack.getOrDefault(ModDataComponents.SLEEPING_BAG_COLOR.get(), DyeColor.RED.getId());
    }

    public boolean hasSleepingBag() {
        return NbtHelper.has(this.backpack, ModDataHelper.SLEEPING_BAG_COLOR);
        // return this.backpack.has(ModDataComponents.SLEEPING_BAG_COLOR.get());
    }
}
