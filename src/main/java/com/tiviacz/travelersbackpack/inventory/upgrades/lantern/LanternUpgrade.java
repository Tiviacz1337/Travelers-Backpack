package com.tiviacz.travelersbackpack.inventory.upgrades.lantern;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.component.RenderInfo;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.upgrades.IEnable;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class LanternUpgrade extends UpgradeBase<LanternUpgrade> implements IEnable {
    public static final String LANTERN = RenderInfo.LANTERN;

    public LanternUpgrade(UpgradeManager manager, int dataHolderSlot) {
        super(manager, dataHolderSlot, new Point(40, 28));

        //Update Render data
        getUpgradeManager().getWrapper().updateRenderInfo(compoundTag -> writeToRenderData(isEnabled(this), compoundTag));
    }

    @Override
    public WidgetBase<?> createWidget(BackpackScreen screen, int x, int y) {
        return new LanternWidget(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y), new Point(137, 0), "screen.travelersbackpack.lantern_upgrade");
    }

    @Override
    public void setEnabled(boolean enabled) {
        getUpgradeManager().getWrapper().updateRenderInfo(compoundTag -> writeToRenderData(enabled, compoundTag));
    }

    public void writeToRenderData(boolean check, CompoundTag compound) {
        if(check) {
            compound.putInt(LANTERN, 15);
        } else {
            compound.remove(LANTERN);
        }
    }

    @Override
    public void onUpgradeRemoved(ItemStack removedStack) {
        super.onUpgradeRemoved(removedStack);
        setEnabled(false);
    }

    @Override
    public boolean hasTab() {
        return false;
    }

    @Override
    public List<Pair<Integer, Integer>> getUpgradeSlotsPosition(int x, int y) {
        return List.of();
    }

    @Override
    public List<? extends Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        return List.of();
    }
}