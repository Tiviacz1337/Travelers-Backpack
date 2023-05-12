package com.tiviacz.travelersbackpack.compat.emi;

import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.init.ModScreenHandlerTypes;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBaseScreenHandler;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.widget.Bounds;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class EmiCompat implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        registry.addExclusionArea(TravelersBackpackHandledScreen.class, ((screen, consumer) -> {
            if (screen.settingsWidget != null) {
                int[] s = screen.settingsWidget.getWidgetSizeAndPos();
                consumer.accept(new Bounds(s[0], s[1], s[2], s[3]));
            }
            if (screen.sortWidget != null && screen.sortWidget.isVisible()) {
                int[] sort = screen.sortWidget.getWidgetSizeAndPos();
                consumer.accept(new Bounds(sort[0], sort[1], sort[2], sort[3]));
            }
            if (screen.memoryWidget != null && screen.memoryWidget.isVisible()) {
                int[] memory = screen.memoryWidget.getWidgetSizeAndPos();
                consumer.accept(new Bounds(memory[0], memory[1], memory[2], memory[3]));
            }
        }));

        registry.addRecipeHandler(ModScreenHandlerTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY, new GridMenuInfo<>());
        registry.addRecipeHandler(ModScreenHandlerTypes.TRAVELERS_BACKPACK_ITEM, new GridMenuInfo<>());
    }

    private static class GridMenuInfo<T extends TravelersBackpackBaseScreenHandler> implements StandardRecipeHandler<T> {
        @Override
        public @Nullable Slot getOutputSlot(T handler) {
            return handler.getSlot(0);
        }

        @Override
        public List<Slot> getInputSources(T handler) {
            // skip result slot
            // crafting slots first
            List<Slot> slots = new ArrayList<>(getCraftingSlots(handler));

            // backpack inventory
            int tierStorageSlots = handler.inventory.getTier().getStorageSlots();
            IntStream.range(10, tierStorageSlots + 46)
                    .filter(i -> i <= 10 + tierStorageSlots - 7 || i >= 10 + tierStorageSlots)
                    .mapToObj(handler::getSlot)
                    .forEach(slots::add);

            return slots;
        }

        @Override
        public List<Slot> getCraftingSlots(T handler) {
            return handler.slots.subList(1, 10);
        }

        @Override
        public boolean supportsRecipe(EmiRecipe recipe) {
            return VanillaEmiRecipeCategories.CRAFTING.equals(recipe.getCategory()) && recipe.supportsRecipeTree();
        }
    }
}
