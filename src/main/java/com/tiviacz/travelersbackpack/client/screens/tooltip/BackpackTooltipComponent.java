package com.tiviacz.travelersbackpack.client.screens.tooltip;

import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.CommonFluid;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class BackpackTooltipComponent implements TooltipComponent {
    protected List<ItemStack> storage = new ArrayList<>();
    protected List<ItemStack> upgrades = new ArrayList<>();
    protected List<ItemStack> tools = new ArrayList<>();
    protected FluidStack leftFluidStack = CommonFluid.empty();
    protected FluidStack rightFluidStack = CommonFluid.empty();
    protected final boolean hoveredWithItem;

    public BackpackTooltipComponent(ItemStack stack) {
        this(stack, false);
    }

    public BackpackTooltipComponent(ItemStack stack, boolean hoveredWithItem) {
        this.loadComponentData(stack);
        this.hoveredWithItem = hoveredWithItem;
    }

    public boolean isHoveredWithItem() {
        return this.hoveredWithItem;
    }

    public void loadComponentData(ItemStack stack) {
        this.loadFluidStacks(stack);
        this.storage = this.loadStorage(stack);
        this.upgrades = this.loadUpgrades(stack);
        this.storage = this.mergeStacks(this.storage);
        this.tools = this.loadTools(stack);
    }

    public void loadFluidStacks(ItemStack stack) {
        if(NbtHelper.has(stack, ModDataHelper.RENDER_INFO)) {
            RenderInfo info = NbtHelper.get(stack, ModDataHelper.RENDER_INFO);
            this.leftFluidStack = info.getLeftFluidStack();
            this.rightFluidStack = info.getRightFluidStack();
        }
    }

    public List<ItemStack> loadStorage(ItemStack stack) {
        if(NbtHelper.has(stack, ModDataHelper.BACKPACK_CONTAINER)) {
            return new ArrayList<>(((NonNullList<ItemStack>)NbtHelper.get(stack, ModDataHelper.BACKPACK_CONTAINER)).stream().filter(itemStack -> !itemStack.isEmpty()).toList());
        }
        return new ArrayList<>();
    }

    public List<ItemStack> loadUpgrades(ItemStack stack) {
        if(NbtHelper.has(stack, ModDataHelper.UPGRADES)) {
            return new ArrayList<>(((NonNullList<ItemStack>)NbtHelper.get(stack, ModDataHelper.UPGRADES)).stream().filter(itemStack -> !itemStack.isEmpty()).toList());
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
                    if(ItemStack.isSameItemSameTags(stack, uniqueList.get(i))) {
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
        if(NbtHelper.has(stack, ModDataHelper.TOOLS_CONTAINER)) {
            return new ArrayList<>(((NonNullList<ItemStack>)NbtHelper.get(stack, ModDataHelper.TOOLS_CONTAINER)).stream().filter(itemStack -> !itemStack.isEmpty()).toList());
        }
        return new ArrayList<>();
    }
}