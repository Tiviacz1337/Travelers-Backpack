package com.tiviacz.travelersbackpack.compat.trashslot;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import net.blay09.mods.trashslot.api.TrashSlotAPI;
import net.blay09.mods.trashslot.api.layout.*;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class BackpackLayout implements TrashContainerLayout {
    private static final Identifier DEFAULT_SNAP_ID = Identifier.withDefaultNamespace("bottom_right");
    private static final Identifier DEFAULT_RECT_ID = ScreenBoundsProvider.SCREEN_ID;
    private static final Identifier EXTENDED_TOP_RECT_ID = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "extended_top");
    private static final Identifier EXTENDED_BOTTOM_RECT_ID = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "extended_bottom");

    private static final Function<BackpackScreen, Rect2i> EXTENDED_TOP_RECT = (screen) -> new Rect2i(
            screen.getLeftPos(),
            screen.getTopPos(),
            screen.getImageWidth(),
            screen.getImageHeight() - 89
    );

    private static final Function<BackpackScreen, Rect2i> EXTENDED_BOTTOM_RECT = (screen) -> new Rect2i(
            screen.getLeftPos() + screen.getWidthAdditions(),
            screen.getTopPos() + screen.getImageHeight() - 89,
            screen.getImageWidth() - (screen.getWidthAdditions() * 2),
            89
    );

    private static TrashContainerLayout getBackpackExtendedLayout() {
        return TrashSlotAPI.getLayout(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_extended"));
    }

    private static TrashContainerLayout getDefaultLayout() {
        // TODO Will be available as TrashSlotAPI.getDefaultLayout() in next TrashSlot version
        return TrashSlotAPI.getLayout(Identifier.fromNamespaceAndPath("trashslot", "default"));
    }

    @Override
    public TrashSlotAvailability getAvailability() {
        return TrashSlotAvailability.DEFAULT;
    }

    @Override
    public Map<Identifier, Snap> getSnaps(TrashSlotContainerContext context) {
        if(context.screen() instanceof BackpackScreen backpackScreen) {
            if(backpackScreen.getWidthAdditions() > 0) {
                return getBackpackExtendedLayout().getSnaps(context);
            }
        }
        return getDefaultLayout().getSnaps(context);
    }

    @Override
    public Optional<Snap> getSnap(TrashSlotContainerContext context, Identifier identifier) {
        if(context.screen() instanceof BackpackScreen backpackScreen) {
            if(backpackScreen.getWidthAdditions() > 0) {
                return getBackpackExtendedLayout().getSnap(context, identifier);
            }
        }
        return getDefaultLayout().getSnap(context, identifier);
    }

    @Override
    public Optional<Snap> getDefaultSnap(TrashSlotContainerContext context) {
        return getSnap(context, DEFAULT_SNAP_ID);
    }

    @Override
    public Optional<Rect2i> getBounds(TrashSlotContainerContext context, Identifier identifier) {
        if(context.screen() instanceof BackpackScreen backpackScreen) {
            if(backpackScreen.getWidthAdditions() > 0) {
                if(EXTENDED_TOP_RECT_ID.equals(identifier)) {
                    return Optional.of(EXTENDED_TOP_RECT.apply(backpackScreen));
                } else if(EXTENDED_BOTTOM_RECT_ID.equals(identifier)) {
                    return Optional.of(EXTENDED_BOTTOM_RECT.apply(backpackScreen));
                }
            }
        }

        // TODO Have to manually restrict getDefaultLayout().getBounds() call to only this ID for now due to TrashSlot bug
        if(DEFAULT_RECT_ID.equals(identifier)) {
            return getDefaultLayout().getBounds(context, identifier);
        }
        return Optional.empty();
    }

    @Override
    public List<Rect2i> getAllBounds(TrashSlotContainerContext context) {
        if(context.screen() instanceof BackpackScreen backpackScreen) {
            List<Rect2i> collisionAreas = new ArrayList<>();
            if(backpackScreen.getWidthAdditions() > 0) {
                collisionAreas.add(EXTENDED_TOP_RECT.apply(backpackScreen));
                collisionAreas.add(getBounds(context, DEFAULT_RECT_ID)
                        .map(defaultRect -> {
                            // TrashSlot tries to push itself between the two touching collision areas,
                            // so we extend the bottom half's size to cover the full height of the screen.
                            final var bottomRect = EXTENDED_BOTTOM_RECT.apply(backpackScreen);
                            return new Rect2i(bottomRect.getX(), defaultRect.getY(), bottomRect.getWidth(), defaultRect.getHeight());
                        })
                        .orElseGet(() -> EXTENDED_BOTTOM_RECT.apply(backpackScreen)));
            } else {
                collisionAreas.addAll(getDefaultLayout().getAllBounds(context));
            }
            backpackScreen.children().forEach((child) -> {
                if(child instanceof WidgetBase<?> widget) {
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
}