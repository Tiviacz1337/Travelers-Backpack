package com.tiviacz.travelersbackpack.compat.rei;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.FilterSlotItemHandler;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.drag.*;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ReiGhostIngredientHandler implements DraggableStackVisitor<BackpackScreen> {
    @Override
    public DraggedAcceptorResult acceptDraggedStack(DraggingContext<BackpackScreen> context, DraggableStack stack) {
        Point cursor = context.getCurrentPosition();
        if(cursor != null) {
            Optional<BackpackBoundsProvider> target = getDraggableAcceptingBounds(context, stack)
                    .map(BackpackBoundsProvider.class::cast)
                    .filter(b -> b.contains(cursor.getX(), cursor.getY()))
                    .findFirst();
            if(target.isPresent()) {
                target.get().accept();
                return DraggedAcceptorResult.CONSUMED;
            }
        }
        return DraggableStackVisitor.super.acceptDraggedStack(context, stack);
    }

    @Override
    public Stream<BoundsProvider> getDraggableAcceptingBounds(DraggingContext<BackpackScreen> context, DraggableStack stack) {
        List<BoundsProvider> targets = new ArrayList<>();
        BackpackScreen screen = context.getScreen();

        if(stack.getStack().getType() == VanillaEntryTypes.ITEM) {
            BackpackBaseMenu menu = context.getScreen().getMenu();
            if(stack.getStack().getValue() instanceof ItemStack ghostStack) {
                menu.mappedSlots.values().forEach(list -> list.forEach(slot -> {
                    if(slot >= menu.slots.size()) {
                        return;
                    }
                    if(menu.getSlot(slot) instanceof FilterSlotItemHandler filterSlot && filterSlot.mayPlace(ghostStack)) {
                        targets.add(new BackpackBoundsProvider() {
                            @Override
                            public VoxelShape bounds() {
                                return DraggableBoundsProvider.fromRectangle(new Rectangle(screen.getGuiLeft() + filterSlot.x, screen.getGuiTop() + filterSlot.y, 16, 16));
                            }

                            @Override
                            public void accept() {
                                ServerboundActionTagPacket.create(ServerboundActionTagPacket.SET_STACK, ServerActions.SLOT, ghostStack, menu.getSlot(slot).index);
                            }
                        });
                    }
                }));
            }
        }
        return targets.stream();
    }

    @Override
    public <R extends Screen> boolean isHandingScreen(R screen) {
        return screen instanceof BackpackScreen;
    }

    public abstract class BackpackBoundsProvider implements BoundsProvider {
        public BackpackBoundsProvider() {
        }

        public abstract void accept();

        public boolean contains(int x, int y) {
            AABB box = bounds().bounds();
            return box.contains(x, y, box.minZ);
        }
    }
}