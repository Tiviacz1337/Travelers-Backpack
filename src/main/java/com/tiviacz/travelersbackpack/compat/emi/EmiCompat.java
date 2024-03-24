package com.tiviacz.travelersbackpack.compat.emi;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.network.ServerboundSettingsPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.widget.Bounds;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@EmiEntrypoint
public class EmiCompat implements EmiPlugin
{
    @Override
    public void register(EmiRegistry emiRegistry)
    {
        emiRegistry.addExclusionArea(TravelersBackpackScreen.class, ((screen, consumer) -> {
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

            if (screen.craftingWidget != null && screen.craftingWidget.isVisible()) {
                int[] crafting = screen.craftingWidget.getWidgetSizeAndPos();
                consumer.accept(new Bounds(crafting[0], crafting[1], crafting[2], crafting[3]));
            }
        }));

        emiRegistry.addRecipeHandler(ModMenuTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY.get(), new GridMenuInfo<>());
        emiRegistry.addRecipeHandler(ModMenuTypes.TRAVELERS_BACKPACK_ITEM.get(), new GridMenuInfo<>());
    }

    private static class GridMenuInfo<T extends TravelersBackpackBaseMenu> implements StandardRecipeHandler<T>
    {
        @Override
        public @Nullable Slot getOutputSlot(T handler)
        {
            return handler.getSlot(0);
        }

        @Override
        public List<Slot> getInputSources(T handler)
        {
            List<Slot> list = new ArrayList<>();

            //Backpack Inv
            for(int i = 1; i <= handler.container.getHandler().getSlots(); i++)
            {
                list.add(handler.getSlot(i));
            }

            //Player Inv
            for(int i = handler.container.getCombinedHandler().getSlots(); i < handler.container.getCombinedHandler().getSlots() + Inventory.INVENTORY_SIZE; i++)
            {
                if(handler.container.getScreenID() == Reference.ITEM_SCREEN_ID && handler.getSlot(i) instanceof DisabledSlot) continue;

                list.add(handler.getSlot(i));
            }
            return list;
        }

        @Override
        public List<Slot> getCraftingSlots(T handler)
        {
            List<Slot> list = new ArrayList<>();
            int firstCraftSlot = handler.container.getCombinedHandler().getSlots() - 8;

            for(int i = 0; i < 9; i++)
            {
                list.add(handler.getSlot(firstCraftSlot + i));
            }
            return list;
        }

        @Override
        public boolean craft(EmiRecipe recipe, EmiCraftContext<T> context)
        {
            context.getScreenHandler().container.getSettingsManager().set(SettingsManager.CRAFTING, SettingsManager.SHOW_CRAFTING_GRID, (byte)1);
            TravelersBackpack.NETWORK.sendToServer(new ServerboundSettingsPacket(context.getScreenHandler().container.getScreenID(), SettingsManager.CRAFTING, SettingsManager.SHOW_CRAFTING_GRID, (byte)1));

            return StandardRecipeHandler.super.craft(recipe, context);
        }

        @Override
        public boolean canCraft(EmiRecipe recipe, EmiCraftContext<T> context)
        {
            return StandardRecipeHandler.super.canCraft(recipe, context) && context.getScreenHandler().container.getSettingsManager().hasCraftingGrid();
        }

        @Override
        public boolean supportsRecipe(EmiRecipe recipe)
        {
            return VanillaEmiRecipeCategories.CRAFTING.equals(recipe.getCategory()) && recipe.supportsRecipeTree();
        }
    }
}