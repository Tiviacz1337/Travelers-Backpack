package com.tiviacz.travelersbackpack.client.screens.widgets.settings;

import com.tiviacz.travelersbackpack.client.screens.BackpackSettingsScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;

public class SettingsWidgetBase extends WidgetBase<BackpackSettingsScreen> {
    protected Point openTabSize = new Point(49, 47);
    public boolean tabOpened = false;

    public SettingsWidgetBase(BackpackSettingsScreen screen, Point pos, Point openTabSize) {
        super(screen, pos, 24, 24);
        this.openTabSize = openTabSize;
    }

    public void updatePos(int y, int size) {
        this.pos = new Point(this.pos.x(), y + size);
    }

    @Override
    public int[] getWidgetSizeAndPos() {
        int[] size = new int[4];
        size[0] = pos.x();
        size[1] = pos.y();
        size[2] = this.tabOpened ? openTabSize.x() : width;
        size[3] = this.tabOpened ? openTabSize.y() : height;
        return size;
    }
}