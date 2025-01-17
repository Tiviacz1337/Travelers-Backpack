package com.tiviacz.travelersbackpack.compat.polymorph;

import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.common.crafting.RecipeSelection;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ResultSlotExt;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingContainerImprovedNew;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class PolymorphCompat {
    public static boolean shouldResetRecipe(Recipe<CraftingContainer> current, BackpackBaseMenu menu, CraftingContainerImprovedNew craftSlots, Level level, Player player) {
        Optional<CraftingRecipe> optional = RecipeSelection.getPlayerRecipe(menu, RecipeType.CRAFTING, craftSlots, level, player);
        return optional.filter(craftingRecipe -> craftingRecipe != current).isPresent();
    }

    public static Recipe<CraftingContainer> getPolymorphedRecipe(BackpackBaseMenu menu, CraftingContainerImprovedNew craftSlots, Level level, Player player) {
        Optional<CraftingRecipe> optional = RecipeSelection.getPlayerRecipe(menu, RecipeType.CRAFTING, craftSlots, level, player);
        return optional.orElse(null);
    }

    @Environment(EnvType.CLIENT)
    public static void registerWidget() {
        PolymorphApi.client().registerWidget(screen -> {
            if(screen instanceof BackpackScreen backpackScreen) {
                for(var slot : backpackScreen.getMenu().slots) {
                    if(slot instanceof ResultSlotExt) {
                        return new PolymorphWidget(backpackScreen, slot);
                    }
                }
            }
            return null;
        });
    }
}
