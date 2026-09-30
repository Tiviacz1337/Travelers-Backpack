package com.tiviacz.travelersbackpack.compat.polymorph;

import com.illusivesoulworks.polymorph.common.PolymorphRecipeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingContainerImproved;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class PolymorphCompat {
    public static boolean shouldResetRecipe(RecipeHolder<CraftingRecipe> current, BackpackBaseMenu menu, CraftingContainerImproved craftSlots, Level level, Player player) {
        PolymorphRecipeManager manager = new PolymorphRecipeManager();
        Optional<RecipeHolder<CraftingRecipe>> optional = manager.getPlayerRecipe(menu, RecipeType.CRAFTING, craftSlots.asCraftInput(), level, player);
        return optional.filter(craftingRecipe -> craftingRecipe != current).isPresent();
    }

    public static RecipeHolder<CraftingRecipe> getPolymorphedRecipe(BackpackBaseMenu menu, CraftingContainerImproved craftSlots, Level level, Player player) {
        PolymorphRecipeManager manager = new PolymorphRecipeManager();
        Optional<RecipeHolder<CraftingRecipe>> optional = manager.getPlayerRecipe(menu, RecipeType.CRAFTING, craftSlots.asCraftInput(), level, player);
        return optional.orElse(null);
    }
}