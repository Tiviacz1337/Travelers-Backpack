package com.tiviacz.travelersbackpack.compat.emi;

import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.init.ModScreenHandlerTypes;
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBaseScreenHandler;
import com.tiviacz.travelersbackpack.inventory.screen.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.util.Reference;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.widget.Bounds;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EmiCompat implements EmiPlugin
{
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
        public @Nullable Slot getOutputSlot(T handler) {
            return handler.getSlot(0);
        }

        @Override
        public List<Slot> getInputSources(T handler)
        {
            List<Slot> list = new ArrayList<>();

            //Backpack Inv
            for(int i = 1; i <= handler.inventory.getInventory().size(); i++)
            {
                list.add(handler.getSlot(i));
            }

            //Player Inv
            for(int i = handler.inventory.getCombinedInventory().size(); i < handler.inventory.getCombinedInventory().size() + PlayerInventory.MAIN_SIZE; i++)
            {
                if(handler.inventory.getScreenID() == Reference.ITEM_SCREEN_ID && handler.getSlot(i) instanceof DisabledSlot) continue;

                list.add(handler.getSlot(i));
            }
            return list;
        }

        @Override
        public List<Slot> getCraftingSlots(T handler)
        {
            List<Slot> list = new ArrayList<>();
            int firstCraftSlot = handler.inventory.getCombinedInventory().size() - 8;

            for(int i = 0; i < 9; i++)
            {
                list.add(handler.getSlot(firstCraftSlot + i));
            }
            return list;
        }

        @Override
        public boolean craft(EmiRecipe recipe, EmiCraftContext<T> context)
        {
            context.getScreenHandler().inventory.getSettingsManager().set(SettingsManager.CRAFTING, SettingsManager.SHOW_CRAFTING_GRID, (byte)1);

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeByte(context.getScreenHandler().inventory.getScreenID()).writeByte(SettingsManager.CRAFTING).writeInt(SettingsManager.SHOW_CRAFTING_GRID).writeByte((byte)1);

            ClientPlayNetworking.send(ModNetwork.SETTINGS_ID, buf);

            return StandardRecipeHandler.super.craft(recipe, context);
        }

        @Override
        public boolean canCraft(EmiRecipe recipe, EmiCraftContext<T> context)
        {
            return StandardRecipeHandler.super.canCraft(recipe, context) && context.getScreenHandler().inventory.getSettingsManager().hasCraftingGrid();
        }

        @Override
        public boolean supportsRecipe(EmiRecipe recipe)
        {
            return VanillaEmiRecipeCategories.CRAFTING.equals(recipe.getCategory()) && recipe.supportsRecipeTree();
        }
    }
}