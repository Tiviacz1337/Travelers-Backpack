package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.FilterSlotItemHandler;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;

import java.util.ArrayList;
import java.util.List;

public class JeiGhostIngredientHandler implements IGhostIngredientHandler<BackpackScreen> {
    @Override
    public <I> List<Target<I>> getTargetsTyped(BackpackScreen screen, ITypedIngredient<I> ingredient, boolean doStart) {
        List<Target<I>> targets = new ArrayList<>();
        if(ingredient.getType() == VanillaTypes.ITEM_STACK) {
            BackpackBaseMenu menu = screen.getMenu();
            ingredient.getItemStack().ifPresent(ghostStack -> {
                menu.mappedSlots.values().forEach(list -> list.forEach(slot -> {
                    if(slot >= menu.slots.size()) {
                        return;
                    }
                    if(menu.getSlot(slot) instanceof FilterSlotItemHandler filterSlot && filterSlot.mayPlace(ghostStack)) {
                        targets.add(new Target<>() {
                            @Override
                            public Rect2i getArea() {
                                return new Rect2i(screen.getGuiLeft() + filterSlot.x, screen.getGuiTop() + filterSlot.y, 16, 16);
                            }

                            @Override
                            public void accept(I i) {
                                ServerboundActionTagPacket.create(ServerboundActionTagPacket.SET_STACK, ServerActions.SLOT, ghostStack, menu.getSlot(slot).index);
                            }
                        });
                    }
                }));
            });
        }
        return targets;
    }

    @Override
    public void onComplete() {

    }
}