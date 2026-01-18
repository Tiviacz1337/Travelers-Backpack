package com.tiviacz.travelersbackpack.compat.trashslot;

import com.google.common.collect.Lists;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.trashslot.api.SlotRenderStyle;
import net.blay09.mods.trashslot.api.Snap;
import net.blay09.mods.trashslot.client.gui.layout.SimpleGuiContainerLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;

import java.util.ArrayList;
import java.util.List;

public class BackpackLayout extends SimpleGuiContainerLayout {
    public static final int PLAYER_INVENTORY_WIDTH = 14 + 18 * 9;

    public BackpackLayout() {
        setEnabledByDefault();
    }

    @Override
    public int getDefaultSlotX(AbstractContainerScreen<?> screen) {
        AbstractContainerScreenAccessor screenAccessor = (AbstractContainerScreenAccessor)screen;
        if(screen.height - screenAccessor.getImageHeight() > 2 * SlotRenderStyle.LONE.getHeight()) {
            return PLAYER_INVENTORY_WIDTH / 2 - SlotRenderStyle.LONE.getWidth();
        } else {
            return PLAYER_INVENTORY_WIDTH / 2;
        }
    }

    @Override
    public int getDefaultSlotY(AbstractContainerScreen<?> screen) {
        AbstractContainerScreenAccessor screenAccessor = (AbstractContainerScreenAccessor)screen;
        if(screen.height - screenAccessor.getImageHeight() > 2 * SlotRenderStyle.LONE.getHeight()) {
            return screenAccessor.getImageHeight() / 2;
        } else {
            return screenAccessor.getImageHeight() / 2 - SlotRenderStyle.LONE.getHeight();
        }
    }

    @Override
    public List<Rect2i> getCollisionAreas(AbstractContainerScreen<?> screen) {
        if(screen instanceof BackpackScreen backpackScreen) {
            List<Rect2i> collisionAreas = new ArrayList<>();
            AbstractContainerScreenAccessor screenAccessor = (AbstractContainerScreenAccessor)screen;
            collisionAreas.add(new Rect2i(screenAccessor.getLeftPos(), screenAccessor.getTopPos(), screenAccessor.getImageWidth(), screenAccessor.getImageHeight() - 88));
            collisionAreas.add(new Rect2i(screenAccessor.getLeftPos() + backpackScreen.getWidthAdditions(), screenAccessor.getTopPos() + 88, screenAccessor.getImageWidth() - backpackScreen.getWidthAdditions() * 2, screenAccessor.getImageHeight() - 88));
            backpackScreen.children().forEach((child) -> {
                if(child instanceof WidgetBase widget) {
                    collisionAreas.add(new Rect2i(widget.getWidgetSizeAndPos()[0], widget.getWidgetSizeAndPos()[1], widget.getWidgetSizeAndPos()[2], widget.getWidgetSizeAndPos()[3]));
                }
            });
            backpackScreen.upgradeSlots.forEach(upgradeSlot -> collisionAreas.add(new Rect2i(upgradeSlot.getUpgradeSlotSizeAndPos()[0], upgradeSlot.getUpgradeSlotSizeAndPos()[1], upgradeSlot.getUpgradeSlotSizeAndPos()[2], upgradeSlot.getUpgradeSlotSizeAndPos()[3])));
            if(backpackScreen.getWrapper().showToolSlots()) {
                collisionAreas.add(new Rect2i(backpackScreen.toolSlotsWidget.getAdditionSizeAndPos()[0], backpackScreen.toolSlotsWidget.getAdditionSizeAndPos()[1], backpackScreen.toolSlotsWidget.getAdditionSizeAndPos()[2], backpackScreen.toolSlotsWidget.getAdditionSizeAndPos()[3]));
            }
            return collisionAreas;
        }
        enableDefaultCollision();
        return super.getCollisionAreas(screen);
    }

    @Override
    public List<Snap> getSnaps(AbstractContainerScreen<?> screen, SlotRenderStyle renderStyle) {
        List<Snap> list = Lists.newArrayList();
        AbstractContainerScreenAccessor screenAccessor = (AbstractContainerScreenAccessor)screen;
        if(screen instanceof BackpackScreen backpackScreen) {
            if(backpackScreen.getWidthAdditions() == 0) {
                list.add(new Snap(Snap.Type.HORIZONTAL, 0, screenAccessor.getTopPos()));
                list.add(new Snap(Snap.Type.HORIZONTAL, 0, screenAccessor.getTopPos() + screenAccessor.getImageHeight() - renderStyle.getHeight()));
                list.add(new Snap(Snap.Type.VERTICAL, screenAccessor.getLeftPos(), 0));
                list.add(new Snap(Snap.Type.VERTICAL, screenAccessor.getLeftPos() + screenAccessor.getImageWidth() - renderStyle.getWidth(), 0));
                return list;
            } else {
                list.add(new Snap(Snap.Type.HORIZONTAL, 0, screenAccessor.getTopPos()));
                list.add(new Snap(Snap.Type.HORIZONTAL, 0, screenAccessor.getTopPos() + screenAccessor.getImageHeight() - renderStyle.getHeight()));
            }
        }
        return list;
    }

    @Override
    public SlotRenderStyle getSlotRenderStyle(AbstractContainerScreen<?> screen, int slotX, int slotY) {
        if(screen instanceof BackpackScreen storageScreen) {
            AbstractContainerScreenAccessor screenAccessor = (AbstractContainerScreenAccessor)screen;
            int leftSnap = storageScreen.getGuiLeft() + storageScreen.getWidthAdditions();
            int rightSnap = leftSnap + 7 + 18 * 9 + 7;
            int extensionY = (storageScreen.visibleRows * 18 + 25);

            if(slotX + SlotRenderStyle.LONE.getWidth() == screenAccessor.getLeftPos()) {
                int slotBottom = slotY + SlotRenderStyle.LONE.getHeight();
                if(slotY == screenAccessor.getTopPos()) {
                    return SlotRenderStyle.ATTACH_LEFT_TOP;
                }

                if(slotY >= screenAccessor.getTopPos() && slotBottom < screenAccessor.getTopPos() + extensionY) {
                    return SlotRenderStyle.ATTACH_LEFT_CENTER;
                }

                if(slotBottom == screenAccessor.getTopPos() + extensionY) {
                    return SlotRenderStyle.ATTACH_LEFT_BOTTOM;
                }
            }

            if(slotX + SlotRenderStyle.LONE.getWidth() == leftSnap) {
                int slotBottom = slotY + SlotRenderStyle.LONE.getHeight();
                if(slotY == screenAccessor.getTopPos()) {
                    return SlotRenderStyle.ATTACH_LEFT_TOP;
                }

                if(slotBottom == screenAccessor.getTopPos() + screenAccessor.getImageHeight() || slotBottom == extensionY) {
                    return SlotRenderStyle.ATTACH_LEFT_BOTTOM;
                }

                if(slotY >= screenAccessor.getTopPos() && slotBottom < screenAccessor.getTopPos() + screenAccessor.getImageHeight()) {
                    return SlotRenderStyle.ATTACH_LEFT_CENTER;
                }
            }

            if(slotX == rightSnap) {
                int slotBottom = slotY + SlotRenderStyle.LONE.getHeight();
                if(slotY == screenAccessor.getTopPos()) {
                    return SlotRenderStyle.ATTACH_RIGHT_TOP;
                }

                if(slotBottom == screenAccessor.getTopPos() + screenAccessor.getImageHeight()) {
                    return SlotRenderStyle.ATTACH_RIGHT_BOTTOM;
                }

                if(slotY >= screenAccessor.getTopPos() && slotBottom < screenAccessor.getTopPos() + screenAccessor.getImageHeight()) {
                    return SlotRenderStyle.ATTACH_RIGHT_CENTER;
                }
            }

            if(slotY + SlotRenderStyle.LONE.getHeight() == screenAccessor.getTopPos()) {
                int slotRight = slotX + SlotRenderStyle.LONE.getWidth();
                if(slotX == screenAccessor.getLeftPos()) {
                    return SlotRenderStyle.ATTACH_TOP_LEFT;
                }

                if(slotRight == screenAccessor.getLeftPos() + screenAccessor.getImageWidth()) {
                    return SlotRenderStyle.ATTACH_TOP_RIGHT;
                }

                if(slotX >= screenAccessor.getLeftPos() && slotRight < screenAccessor.getLeftPos() + screenAccessor.getImageWidth()) {
                    return SlotRenderStyle.ATTACH_TOP_CENTER;
                }
            }

            if(slotY == screenAccessor.getTopPos() + screenAccessor.getImageHeight()) {
                int slotRight = slotX + SlotRenderStyle.LONE.getWidth();
                if(slotX == screenAccessor.getLeftPos() + storageScreen.getWidthAdditions()) {
                    return SlotRenderStyle.ATTACH_BOTTOM_LEFT;
                }

                if(slotRight == screenAccessor.getLeftPos() + screenAccessor.getImageWidth() - storageScreen.getWidthAdditions()) {
                    return SlotRenderStyle.ATTACH_BOTTOM_RIGHT;
                }

                if(slotX >= screenAccessor.getLeftPos() && slotRight < screenAccessor.getLeftPos() + screenAccessor.getImageWidth() - storageScreen.getWidthAdditions()) {
                    return SlotRenderStyle.ATTACH_BOTTOM_CENTER;
                }
            }
        }
        return SlotRenderStyle.LONE;
    }

    @Override
    public String getContainerId(AbstractContainerScreen<?> screen) {
        if(screen instanceof BackpackScreen backpackScreen) {
            return "travelersbackpack_" + backpackScreen.getWrapper().getStorageSize();
        }
        return super.getContainerId(screen);
    }
}