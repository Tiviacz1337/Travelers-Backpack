package com.tiviacz.travelersbackpack.compat.polymorph;

/*public class PolymorphCompat {
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

    @Environment(EnvType.CLIENT)
    public static void registerWidget() {
        PolymorphWidgets.getInstance().registerWidget(screen -> {
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
*/