package com.tiviacz.travelersbackpack.client.screens.tooltip;

import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.CommonFluid;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class BackpackTooltipComponent implements TooltipComponent {
    protected List<ItemStack> storage = new ArrayList<>();
    protected List<ItemStack> upgrades = new ArrayList<>();
    protected List<ItemStack> tools = new ArrayList<>();
    protected FluidStack leftFluidStack = CommonFluid.empty();
    protected FluidStack rightFluidStack = CommonFluid.empty();

    public BackpackTooltipComponent(ItemStack stack) {
        this.loadComponentData(stack);
    }

    public void loadComponentData(ItemStack stack) {
        this.loadFluidStacks(stack);
        this.storage = this.loadStorage(stack);
        this.upgrades = this.loadUpgrades(stack);
        this.storage = this.mergeStacks(this.storage);
        this.tools = this.loadTools(stack);
    }

    public void loadFluidStacks(ItemStack stack) {
        if(stack.has(ModDataComponents.RENDER_INFO.get())) {
            RenderInfo info = stack.get(ModDataComponents.RENDER_INFO.get());
            this.leftFluidStack = info.getLeftFluidStack();
            this.rightFluidStack = info.getRightFluidStack();
        }
    }

    public List<ItemStack> loadStorage(ItemStack stack) {
        if(stack.has(ModDataComponents.BACKPACK_CONTAINER.get())) {
            return new ArrayList<>(stack.get(ModDataComponents.BACKPACK_CONTAINER.get()).getItems().stream().filter(itemStack -> !itemStack.isEmpty()).toList());
        }
        return new ArrayList<>();
    }

    public List<ItemStack> loadUpgrades(ItemStack stack) {
        if(stack.has(ModDataComponents.UPGRADES.get())) {
            return new ArrayList<>(stack.get(ModDataComponents.UPGRADES.get()).getItems().stream().filter(itemStack -> !itemStack.isEmpty()).toList());
        }
        return new ArrayList<>();
    }

    public List<ItemStack> mergeStacks(List<ItemStack> stacks) {
        if(!stacks.isEmpty()) {
            List<ItemStack> uniqueList = new ArrayList<>();
            for(ItemStack stack : stacks) {
                if(uniqueList.isEmpty()) {
                    uniqueList.add(stack);
                    continue;
                }
                boolean flag = false;
                for(int i = 0; i < uniqueList.size(); i++) {
                    if(ItemStack.isSameItemSameComponents(stack, uniqueList.get(i))) {
                        int count = stack.getCount() + uniqueList.get(i).getCount();
                        uniqueList.set(i, stack.copyWithCount(count));
                        flag = true;
                        break;
                    }
                }
                if(!flag) {
                    uniqueList.add(stack);
                }
            }
            //Split >999 stacks
            List<ItemStack> splittedList = new ArrayList<>();

            for(ItemStack itemStack : uniqueList) {
                if(itemStack.getCount() > 999) {
                    int count = itemStack.getCount();
                    int c = count / 999;
                    int reminder = count % 999;

                    for(int j = 0; j < c; j++) {
                        splittedList.add(itemStack.copyWithCount(999));
                    }
                    splittedList.add(itemStack.copyWithCount(reminder));
                } else {
                    splittedList.add(itemStack);
                }
            }
            return splittedList;
        }
        return new ArrayList<>();
    }

    public List<ItemStack> loadTools(ItemStack stack) {
        if(stack.has(ModDataComponents.TOOLS_CONTAINER.get())) {
            return new ArrayList<>(stack.get(ModDataComponents.TOOLS_CONTAINER.get()).getItems().stream().filter(itemStack -> !itemStack.isEmpty()).toList());
        }
        return new ArrayList<>();
    }
}