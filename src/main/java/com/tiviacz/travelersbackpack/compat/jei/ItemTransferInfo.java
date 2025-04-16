package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingUpgrade;
import com.tiviacz.travelersbackpack.util.Reference;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemTransferInfo implements IRecipeTransferInfo<BackpackItemMenu, RecipeHolder<CraftingRecipe>> {
    @Override
    public Class<? extends BackpackItemMenu> getContainerClass() {
        return BackpackItemMenu.class;
    }

    @Override
    public Optional<MenuType<BackpackItemMenu>> getMenuType() {
        return Optional.of(ModMenuTypes.BACKPACK_MENU.get());
    }

    @Override
    public RecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
        return RecipeTypes.CRAFTING;
    }

    @Override
    public boolean canHandle(BackpackItemMenu menu, RecipeHolder<CraftingRecipe> recipe) {
        return menu.getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).isPresent() && menu.getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).get().isTabOpened();
    }

    @Override
    public List<Slot> getRecipeSlots(BackpackItemMenu menu, RecipeHolder<CraftingRecipe> recipe) {
        List<Slot> list = new ArrayList<>();
        int firstCraftSlot = menu.CRAFTING_GRID_START;
        for(int i = 0; i < 9; i++) {
            list.add(menu.getSlot(firstCraftSlot + i));
        }
        return list;
    }

    @Override
    public List<Slot> getInventorySlots(BackpackItemMenu menu, RecipeHolder<CraftingRecipe> recipe) {
        List<Slot> list = new ArrayList<>();
        //Backpack Inv
        for(int i = 0; i < menu.BACKPACK_INV_END; i++) {
            list.add(menu.getSlot(i));
        }
        //Player Inv
        for(int i = menu.PLAYER_INV_START; i < menu.PLAYER_HOT_END; i++) {
            if(menu.getWrapper().getScreenID() == Reference.ITEM_SCREEN_ID && menu.getSlot(i) instanceof DisabledSlot)
                continue;

            list.add(menu.getSlot(i));
        }
        return list;
    }
}