package com.tiviacz.travelersbackpack.compat.rei;

//@REIPluginClient
public class ReiClientCompat { /*implements REIClientPlugin {
    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(BackpackSettingsScreen.class, screen -> {
            List<Rectangle> ret = new ArrayList<>();
            screen.children().stream().filter(w -> w instanceof WidgetBase).forEach(widget -> {
                int[] size = ((WidgetBase)widget).getWidgetSizeAndPos();
                ret.add(new Rectangle(size[0], size[1], size[2], size[3]));
            });
            return ret;
        });

        zones.register(BackpackScreen.class, screen -> {
            List<Rectangle> ret = new ArrayList<>();
            int[] s = screen.settingsWidget.getWidgetSizeAndPos();
            ret.add(new Rectangle(s[0], s[1], s[2], s[3]));

            screen.children().stream().filter(w -> w instanceof UpgradeWidgetBase).forEach(widget -> {
                int[] size = ((UpgradeWidgetBase)widget).getWidgetSizeAndPos();
                ret.add(new Rectangle(size[0], size[1], size[2], size[3]));
            });
            screen.upgradeSlots.forEach(slot -> {
                if(!slot.isHidden()) {
                    int[] size = slot.getUpgradeSlotSizeAndPos();
                    ret.add(new Rectangle(size[0], size[1], size[2], size[3]));
                }
            });
            return ret;
        });
    } */
}