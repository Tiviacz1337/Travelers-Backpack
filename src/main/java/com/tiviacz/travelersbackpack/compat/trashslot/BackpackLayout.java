package com.tiviacz.travelersbackpack.compat.trashslot;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import net.blay09.mods.trashslot.api.TrashSlotAPI;
import net.blay09.mods.trashslot.api.layout.*;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;

import java.util.*;

public class BackpackLayout implements TrashContainerLayout {
    public static final int PLAYER_INVENTORY_WIDTH = 14 + 18 * 9;

    /*public BackpackLayout() {
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
    }*/

    public static final String MOD_ID = TravelersBackpack.MODID;

    private static final Identifier DEFAULT_SNAP_ID = Identifier.fromNamespaceAndPath(MOD_ID, "bottom_right");

    private static final Map<Identifier, Snap> EXTENDED_SNAPS = buildExtendedSnaps();

    private static final Map<Identifier, Snap> SNAPS = new HashMap<>();

    static {
        registerSnap(SNAPS, "top", new SnapCoordinateProvider.Range(left(12), right(-28)), top(-15), SlotVisual.ATTACH_TOP);
        registerSnap(SNAPS, "top_right", right(-24), top(-15), SlotVisual.ATTACH_TOP_RIGHT);
        registerSnap(SNAPS, "top_left", left(8), top(-15), SlotVisual.ATTACH_TOP_LEFT);
        registerSnap(SNAPS, "bottom", new SnapCoordinateProvider.Range(left(12), right(-28)), bottom(-1), SlotVisual.ATTACH_BOTTOM);
        registerSnap(SNAPS, "bottom_right", right(-24), bottom(-1), SlotVisual.ATTACH_BOTTOM_RIGHT);
        registerSnap(SNAPS, "bottom_left", left(8), bottom(-1), SlotVisual.ATTACH_BOTTOM_LEFT);
        registerSnap(SNAPS, "left", left(-15), new SnapCoordinateProvider.Range(top(11), bottom(-24)), SlotVisual.ATTACH_LEFT);
        registerSnap(SNAPS, "left_top", left(-15), top(7), SlotVisual.ATTACH_LEFT_TOP);
        registerSnap(SNAPS, "left_bottom", left(-15), bottom(-24), SlotVisual.ATTACH_LEFT_BOTTOM);
        registerSnap(SNAPS, "right", right(-1), new SnapCoordinateProvider.Range(top(10), bottom(-28)), SlotVisual.ATTACH_RIGHT);
        registerSnap(SNAPS, "right_top", right(-1), top(7), SlotVisual.ATTACH_RIGHT_TOP);
        registerSnap(SNAPS, "right_bottom", right(-1), bottom(-24), SlotVisual.ATTACH_RIGHT_BOTTOM);
    }

    //private final Map<Identifier, ScreenBoundsProvider> bounds;
    private final Map<Identifier, Snap> snaps;

    public BackpackLayout() {
        this.snaps = SNAPS;
    }

    private static Map<Identifier, Snap> buildExtendedSnaps() {
        Map<Identifier, Snap> snaps = new HashMap<>();
        registerSnap(snaps, "top", new SnapCoordinateProvider.Range(extLeft(12), extRight(-28)), extTop(-15), SlotVisual.ATTACH_TOP);
        registerSnap(snaps, "top_right", extRight(-24), extTop(-15), SlotVisual.ATTACH_TOP_RIGHT);
        registerSnap(snaps, "top_left", extLeft(8), extTop(-15), SlotVisual.ATTACH_TOP_LEFT);
        registerSnap(snaps, "bottom", new SnapCoordinateProvider.Range(extLeft(12), extRight(-28)), extBottom(-1), SlotVisual.ATTACH_BOTTOM);
        registerSnap(snaps, "bottom_right", extRight(-24), extBottom(-1), SlotVisual.ATTACH_BOTTOM_RIGHT);
        registerSnap(snaps, "bottom_left", extLeft(8), extBottom(-1), SlotVisual.ATTACH_BOTTOM_LEFT);
        registerSnap(snaps, "left", extLeft(-15), new SnapCoordinateProvider.Range(extTop(11), extBottom(-24)), SlotVisual.ATTACH_LEFT);
        registerSnap(snaps, "left_top", extLeft(-15), extTop(7), SlotVisual.ATTACH_LEFT_TOP);
        registerSnap(snaps, "left_bottom", extLeft(-15), extBottom(-24), SlotVisual.ATTACH_LEFT_BOTTOM);
        registerSnap(snaps, "right", extRight(-1), new SnapCoordinateProvider.Range(extTop(10), extBottom(-28)), SlotVisual.ATTACH_RIGHT);
        registerSnap(snaps, "right_top", extRight(-1), extTop(7), SlotVisual.ATTACH_RIGHT_TOP);
        registerSnap(snaps, "right_bottom", extRight(-1), extBottom(-24), SlotVisual.ATTACH_RIGHT_BOTTOM);
        return snaps;
    }

    private static SnapCoordinateProvider extLeft(int offset) { return new SnapCoordinateProvider.Left(BACKPACK_RECT_ID, offset); }
    private static SnapCoordinateProvider extRight(int offset) { return new SnapCoordinateProvider.Right(BACKPACK_RECT_ID, offset); }
    private static SnapCoordinateProvider extTop(int offset) { return new SnapCoordinateProvider.Top(BACKPACK_RECT_ID, offset); }
    private static SnapCoordinateProvider extBottom(int offset) { return new SnapCoordinateProvider.Bottom(BACKPACK_RECT_ID, offset); }

    private static void registerSnap(Map<Identifier, Snap> map, String path, SnapCoordinateProvider x, SnapCoordinateProvider y, SlotVisual visual) {
        map.put(Identifier.fromNamespaceAndPath(MOD_ID, path), new Snap(Optional.of(x), Optional.of(y), visual));
    }

    @Override
    public TrashSlotAvailability getAvailability() {
        return TrashSlotAvailability.DEFAULT;
    }

    @Override
    public Map<Identifier, Snap> getSnaps(TrashSlotContainerContext context) {
        var snaps = TrashSlotAPI.getLayout(Identifier.fromNamespaceAndPath("trashslot", "default")).getSnaps(context);
        var snaps2 = this.snaps;
        return snaps;
        //return this.snaps;
    }

    @Override
    public Optional<Snap> getSnap(TrashSlotContainerContext context, Identifier identifier) {
        return TrashSlotAPI.getLayout(Identifier.fromNamespaceAndPath("trashslot", "default")).getSnap(context, identifier);
    }

    @Override
    public Optional<Snap> getDefaultSnap(TrashSlotContainerContext context) {
        return getSnap(context, DEFAULT_SNAP_ID);
    }

    public static final Identifier INVENTORY_RECT_ID = Identifier.fromNamespaceAndPath(MOD_ID, "inventory_rect");
    public static final Identifier BACKPACK_RECT_ID = Identifier.fromNamespaceAndPath(MOD_ID, "backpack_rect");

    @Override
    public Optional<Rect2i> getBounds(TrashSlotContainerContext context, Identifier identifier) {
        if(context.screen() instanceof BackpackScreen backpackScreen) {
            if(backpackScreen.getWidthAdditions() > 0) {
                return Optional.of(new Rect2i(
                        backpackScreen.getGuiLeft(),
                        backpackScreen.getGuiTop(),
                        backpackScreen.getImageWidth(),
                        backpackScreen.getImageHeight() - 88
                ));
            }
            return Optional.of(new Rect2i(
                    backpackScreen.getGuiLeft(),
                    backpackScreen.getGuiTop(),
                    backpackScreen.getImageWidth(),
                    backpackScreen.getImageHeight()
            ));
        }
        if (identifier == null) {
            return Optional.empty();
        }
        if (identifier != null && context.screen() instanceof BackpackScreen backpackScreen) {

            /*if(identifier.equals(INVENTORY_RECT_ID)) {
                int coreX = backpackScreen.getGuiLeft() + backpackScreen.getWidthAdditions();
                int coreWidth = backpackScreen.getImageWidth() - (backpackScreen.getWidthAdditions() * 2);
                return Optional.of(new Rect2i(coreX, backpackScreen.getGuiTop(), coreWidth, backpackScreen.getImageHeight()));
            }*/

                return Optional.of(new Rect2i(
                        backpackScreen.getGuiLeft(),
                        backpackScreen.getGuiTop(),
                        backpackScreen.getImageWidth(),
                        backpackScreen.getImageHeight()
                ));
        }

        return TrashSlotAPI.getLayout(Identifier.fromNamespaceAndPath("trashslot", "default")).getBounds(context, identifier);
    }

    @Override
    public List<Rect2i> getAllBounds(TrashSlotContainerContext context) {
        if(context.screen() instanceof BackpackScreen backpackScreen) {
            List<Rect2i> collisionAreas = new ArrayList<>();
            collisionAreas.add(new Rect2i(backpackScreen.getGuiLeft() + backpackScreen.getWidthAdditions(), backpackScreen.getGuiTop(), backpackScreen.getImageWidth() - (backpackScreen.getWidthAdditions() * 2), backpackScreen.getImageHeight()));
            collisionAreas.add(new Rect2i(backpackScreen.getGuiLeft(), backpackScreen.getGuiTop(), backpackScreen.getImageWidth(), backpackScreen.getImageHeight() - 88));
            collisionAreas.add(new Rect2i(backpackScreen.getGuiLeft() + backpackScreen.getWidthAdditions(), backpackScreen.getGuiTop() + 88, backpackScreen.getImageWidth() - backpackScreen.getWidthAdditions() * 2, backpackScreen.getImageHeight() - 88));
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
        return List.of();
    }

    private static SnapCoordinateProvider left(int offset) {
        return new SnapCoordinateProvider.Left(INVENTORY_RECT_ID, offset);
    }

    private static SnapCoordinateProvider right(int offset) {
        return new SnapCoordinateProvider.Right(INVENTORY_RECT_ID, offset);
    }

    private static SnapCoordinateProvider top(int offset) {
        return new SnapCoordinateProvider.Top(INVENTORY_RECT_ID, offset);
    }

    private static SnapCoordinateProvider bottom(int offset) {
        return new SnapCoordinateProvider.Bottom(INVENTORY_RECT_ID, offset);
    }
}