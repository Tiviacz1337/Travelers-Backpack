package com.tiviacz.travelersbackpack.inventory.upgrades.refill;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import net.minecraft.client.gui.GuiGraphics;

public class RefillWidget extends UpgradeWidgetBase<RefillUpgrade> {
    public RefillWidget(BackpackScreen screen, RefillUpgrade upgrade, Point pos) {
        super(screen, upgrade, pos, new Point(137, 0), "screen.travelersbackpack.refill_upgrade");
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if(isTabOpened()) {
            int slotCount = upgrade.getFilterSlotCount();
            int rowCount = (int)Math.ceil((double)slotCount / 3);
            //Upper
            guiGraphics.blit(BackpackScreen.TABS, pos.x(), pos.y(), tabUv.x(), tabUv.y(), width, 22); //22
            //Lower
            guiGraphics.blit(BackpackScreen.TABS, pos.x(), pos.y() + 22, tabUv.x(), tabUv.y() + 43 + (3 - rowCount) * 18, width, height - 22); //22
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    if(j + i * 3 < slotCount) {
                        guiGraphics.blit(BackpackScreen.TABS, pos.x() + 6 + j * 18, pos.y() + 22 + i * 18, 233, 0, 18, 18); //22
                    }
                }
            }
        }
        guiGraphics.renderItem(screen.getWrapper().getUpgrades().getStackInSlot(this.dataHolderSlot), pos.x() + 4, pos.y() + 4);
    }
}