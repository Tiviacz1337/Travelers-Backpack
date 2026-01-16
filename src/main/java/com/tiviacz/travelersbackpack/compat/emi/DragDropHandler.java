package com.tiviacz.travelersbackpack.compat.emi;

import com.google.common.collect.Maps;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.FilterSlotItemHandler;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class DragDropHandler implements EmiDragDropHandler<BackpackScreen> {
    private final BiFunction<BackpackScreen, EmiIngredient, Map<Bounds, Consumer<EmiIngredient>>> bounds;

    public DragDropHandler() {
        this.bounds = (screen, ingredient) -> {
            Map<Bounds, Consumer<EmiIngredient>> map = Maps.newHashMap();
            if(ingredient.getEmiStacks().isEmpty()) {
                return map;
            }

            EmiStack emiGhostStack = ingredient.getEmiStacks().get(0);
            if(!emiGhostStack.isEmpty()) {
                ItemStack ghostStack = emiGhostStack.getItemStack();
                BackpackBaseMenu menu = screen.getMenu();
                menu.mappedSlots.values().forEach(list -> list.forEach(slot -> {
                    if(slot >= menu.slots.size()) {
                        return;
                    }
                    if(menu.getSlot(slot) instanceof FilterSlotItemHandler filterSlot && filterSlot.mayPlace(ghostStack)) {
                        map.put(new Bounds(screen.getGuiLeft() + filterSlot.x, screen.getGuiTop() + filterSlot.y, 16, 16), (i) -> ServerboundActionTagPacket.create(ServerboundActionTagPacket.SET_STACK, ServerActions.SLOT, ghostStack, menu.getSlot(slot).index));
                    }
                }));
            }
            return map;
        };
    }

    @Override
    public boolean dropStack(BackpackScreen backpackScreen, EmiIngredient emiIngredient, int x, int y) {
        Map<Bounds, Consumer<EmiIngredient>> bounds = this.bounds.apply(backpackScreen, emiIngredient);
        for(Bounds b : bounds.keySet()) {
            if(b.contains(x, y)) {
                bounds.get(b).accept(emiIngredient);
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(BackpackScreen screen, EmiIngredient dragged, GuiGraphics draw, int mouseX, int mouseY, float delta) {
        for(Bounds b : this.bounds.apply(screen, dragged).keySet()) {
            draw.fill(b.x(), b.y(), b.x() + b.width(), b.y() + b.height(), 0x8822BB33);
        }
    }
}