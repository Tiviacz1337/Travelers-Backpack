package com.tiviacz.travelersbackpack.compat.emi;

import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.init.ModScreenHandlerTypes;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBaseScreenHandler;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.widget.Bounds;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EmiCompat implements EmiPlugin
{
    @Override
    public void register(EmiRegistry registry)
    {
        registry.addExclusionArea(TravelersBackpackHandledScreen.class, ((screen, consumer) ->
        {
            if(screen.settingsWidget != null) {
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
            if (screen.craftingWidget != null && screen.craftingWidget.isVisible()) {
                int[] crafting = screen.craftingWidget.getWidgetSizeAndPos();
                consumer.accept(new Bounds(crafting[0], crafting[1], crafting[2], crafting[3]));
            }
        }));

        registry.addRecipeHandler(ModScreenHandlerTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY, new GridMenuInfo<>());
        registry.addRecipeHandler(ModScreenHandlerTypes.TRAVELERS_BACKPACK_ITEM, new GridMenuInfo<>());
    }

    private static class GridMenuInfo<T extends TravelersBackpackBaseScreenHandler> implements StandardRecipeHandler<T>
    {
        @Override
        public @Nullable Slot getOutputSlot(T handler)
        {
            return handler.getSlot(0);
        }

        @Override
        public List<Slot> getInputSources(T handler)
        {
            List<Slot> slots = new ArrayList<>();
            Tiers.Tier tier = handler.inventory.getTier();

            //Backpack Inv
            for(int i = 1; i < tier.getStorageSlotsWithCrafting() + 1; i++)
            {
                slots.add((handler.getSlot(i)));
            }

            //Player Inv
            for(int i = (tier.getAllSlots() + 10); i < (tier.getAllSlots() + 10) + PlayerInventory.MAIN_SIZE; i++)
            {
                slots.add(handler.getSlot(i));
            }

            return slots;
        }

        @Override
        public List<Slot> getCraftingSlots(T handler)
        {
            List<Slot> slots = new ArrayList<>();

            int firstCraftSlot = (handler.inventory.getTier().getStorageSlotsWithCrafting() - Tiers.LEATHER.getStorageSlotsWithCrafting()) + 6;
            for(int i = 0; i < 3; i++)
            {
                for(int j = 0; j < 3; j++)
                {
                    slots.add(handler.getSlot(firstCraftSlot + j + (i * 8)));
                }
            }
            return slots;
        }

        @Override
        public boolean supportsRecipe(EmiRecipe recipe)
        {
            return VanillaEmiRecipeCategories.CRAFTING.equals(recipe.getCategory()) && recipe.supportsRecipeTree();
        }
    }
}