package com.tiviacz.travelersbackpack.compat.rrv;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.builtin.crafting.CraftingClientRecipe;
import cc.cassian.rrv.common.builtin.smithing.SmithingClientRecipe;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.recipes.BackpackUpgradeRecipe;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeType;

public class TravelersBackpackPlugin implements ReliableRecipeViewerClientPlugin {
    @Override
    public void onIntegrationInitialize() {
        ItemView.addClientRecipeProvider(recipeList -> {
            ClientRecipeManager.INSTANCE.getRecipesForType(RecipeType.CRAFTING).forEach(recipeHolder -> {
                if (!(recipeHolder.value() instanceof ShapedBackpackRecipe backpackRecipe)) return;

                recipeList.add(new CraftingClientRecipe.Builder(
                        Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_crafting/" + backpackRecipe.result.item().getRegisteredName().replace(':', '/')),
                        backpackRecipe.pattern.ingredients().stream().map(optional -> optional.map(SlotContent::of).orElseGet(SlotContent::of)).toList()
                ).setResult(backpackRecipe.result).build());
            });

            ClientRecipeManager.INSTANCE.getRecipesForType(RecipeType.SMITHING).forEach(recipeHolder -> {
                if (!(recipeHolder.value() instanceof BackpackUpgradeRecipe upgradeRecipe)) return;

                recipeList.add(SmithingClientRecipe.transformationRecipe(
                        Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_upgrading/" + upgradeRecipe.result.item().getRegisteredName().replace(':', '/')),
                        upgradeRecipe.additionIngredient().map(SlotContent::of).orElseGet(SlotContent::of),
                        SlotContent.of(upgradeRecipe.baseIngredient()),
                        upgradeRecipe.templateIngredient().map(SlotContent::of).orElseGet(SlotContent::of),
                        SlotContent.of(upgradeRecipe.result)
                ));
            });
        });
    }
}
