package com.tiviacz.travelersbackpack.compat.rrv;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.event.OverlayManagementEvents;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.builtin.crafting.CraftingClientRecipe;
import cc.cassian.rrv.common.builtin.smithing.SmithingClientRecipe;
import cc.cassian.rrv.common.overlay.BlockingGuiComponent;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.BackpackSettingsScreen;
import com.tiviacz.travelersbackpack.client.screens.UpgradeSlot;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.common.recipes.BackpackUpgradeRecipe;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.HashMap;

public class TravelersBackpackPlugin implements ReliableRecipeViewerClientPlugin {
    @Override
    public void onIntegrationInitialize() {
        ItemView.addClientRecipeProvider(recipeList -> {
            ClientRecipeManager.INSTANCE.getRecipesForType(RecipeType.CRAFTING).forEach(recipeHolder -> {
                if (!(recipeHolder.value() instanceof ShapedBackpackRecipe backpackRecipe)) return;

                final HashMap<Integer, SlotContent> ingredients = HashMap.newHashMap(backpackRecipe.pattern.ingredients().size());
                for (int i = 0; i < backpackRecipe.pattern.ingredients().size(); i++) {
                    ingredients.put(i, backpackRecipe.pattern.ingredients().get(i).map(SlotContent::of).orElseGet(SlotContent::of));
                }

                recipeList.add(new CraftingClientRecipe.Builder(
                        Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_crafting/" + backpackRecipe.result.item().getRegisteredName().replace(':', '/')),
                        ingredients
                ).setResult(backpackRecipe.result).build());
            });

            ClientRecipeManager.INSTANCE.getRecipesForType(RecipeType.SMITHING).forEach(recipeHolder -> {
                if (!(recipeHolder.value() instanceof BackpackUpgradeRecipe upgradeRecipe)) return;

                recipeList.add(SmithingClientRecipe.transformationRecipe(
                        Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_upgrading/" + upgradeRecipe.getResult().item().getRegisteredName().replace(':', '/')),
                        upgradeRecipe.additionIngredient().map(SlotContent::of).orElseGet(SlotContent::of),
                        SlotContent.of(upgradeRecipe.baseIngredient()),
                        upgradeRecipe.templateIngredient().map(SlotContent::of).orElseGet(SlotContent::of),
                        SlotContent.of(upgradeRecipe.getResult())
                ));
            });
        });

        OverlayManagementEvents.registerExclusionArea((screen, instance, _) -> {
            if (!(screen instanceof BackpackSettingsScreen)) return;

            for (int i = 0; i < screen.children().size(); i++) {
                if (!(screen.children().get(i) instanceof WidgetBase<?> widget)) continue;
                final int[] size = widget.getWidgetSizeAndPos();
                instance.setExclusionArea(new BlockingGuiComponent(
                        Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_settings_screen_widget_" + i),
                        size[0], size[1], size[2], size[3]
                ));
            }
        });

        OverlayManagementEvents.registerExclusionArea((screen, instance, _) -> {
            if (!(screen instanceof BackpackScreen backpackScreen)) return;

            final int[] size = backpackScreen.settingsWidget.getWidgetSizeAndPos();
            instance.setExclusionArea(new BlockingGuiComponent(
                    Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_screen_settings_widget"),
                    size[0], size[1], size[2], size[3]
            ));

            for (int i = 0; i < screen.children().size(); i++) {
                if (!(screen.children().get(i) instanceof UpgradeWidgetBase<?> widget)) continue;
                final int[] widgetSize = widget.getWidgetSizeAndPos();
                instance.setExclusionArea(new BlockingGuiComponent(
                        Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_screen_widget_" + i),
                        widgetSize[0], widgetSize[1], widgetSize[2], widgetSize[3]
                ));
            }

            for (int i = 0; i < backpackScreen.upgradeSlots.size(); i++) {
                final UpgradeSlot slot = backpackScreen.upgradeSlots.get(i);
                if (slot.isHidden()) continue;
                final int[] slotSize = slot.getUpgradeSlotSizeAndPos();
                instance.setExclusionArea(new BlockingGuiComponent(
                        Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_screen_upgrade_slot_" + i),
                        slotSize[0], slotSize[1], slotSize[2], slotSize[3]
                ));
            }
        });
    }
}
