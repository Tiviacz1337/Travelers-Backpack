package com.tiviacz.travelersbackpack.client.screens;

import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.items.upgrades.UpgradeItem;
import net.minecraft.client.gui.GuiGraphics;

public class UpgradeSlot {
    public static final int SLOT_SIZE = 24;
    public static final int ICON_SIZE = 18;
    private final BackpackWrapper wrapper;
    private final Point pos;
    private final boolean isHidden;
    private final int index;
    private final int x;
    private final int y;

    public UpgradeSlot(BackpackWrapper wrapper, Point pos, int index, int x, int y, boolean isHidden) {
        this.wrapper = wrapper;
        this.pos = pos;
        this.index = index;
        this.x = x;
        this.y = y;
        this.isHidden = isHidden;
    }

    public void render(GuiGraphics guiGraphics, int xPos, int yPos) {
        if(isHidden) {
            return;
        }
        if(wrapper.getUpgradeManager().hasUpgradeInSlot(index) && wrapper.getUpgrades().getStackInSlot(this.index).getOrDefault(ModDataComponents.TAB_OPEN, false)) {
            return;
        }

        guiGraphics.blit(BackpackScreen.ICONS, xPos + x, yPos + y, 0, 0, SLOT_SIZE, SLOT_SIZE);
        if(!(wrapper.getUpgrades().getStackInSlot(this.index).getItem() instanceof UpgradeItem) || !wrapper.getUpgradeManager().hasUpgradeInSlot(index)) {
            guiGraphics.blit(BackpackScreen.ICONS, xPos + x + 3, yPos + y + 3, 24, 0, ICON_SIZE, ICON_SIZE);
        } else {
            guiGraphics.renderItem(wrapper.getUpgrades().getStackInSlot(this.index), xPos + x + 4, yPos + y + 4);
        }
    }

    public int getIndex() {
        return this.index;
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    public int[] getUpgradeSlotSizeAndPos() {
        int[] size = new int[4];
        size[0] = pos.x();
        size[1] = pos.y();
        size[2] = SLOT_SIZE;
        size[3] = SLOT_SIZE;
        return size;
    }
}