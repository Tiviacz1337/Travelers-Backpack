package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingUpgrade;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.library.transfer.BasicRecipeTransferHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ItemTransferHandler extends BasicRecipeTransferHandler<BackpackItemMenu, RecipeHolder<CraftingRecipe>> {
    public ItemTransferHandler(IConnectionToServer serverConnection, IStackHelper stackHelper, IRecipeTransferHandlerHelper handlerHelper, IRecipeTransferInfo<BackpackItemMenu, RecipeHolder<CraftingRecipe>> transferInfo) {
        super(serverConnection, stackHelper, handlerHelper, transferInfo);
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(BackpackItemMenu menu, RecipeHolder<CraftingRecipe> recipe, IRecipeSlotsView recipeSlotsView, Player player, boolean maxTransfer, boolean doTransfer) {
        Optional<CraftingUpgrade> upgrade = menu.getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class);
        if(upgrade.isPresent()) {
            if(doTransfer) {
                if(!upgrade.get().isTabOpened()) {
                    ServerboundActionTagPacket.create(ServerboundActionTagPacket.UPGRADE_TAB, upgrade.get().getDataHolderSlot(), true, ServerActions.TAB_OPEN);
                }
            }
            if(!upgrade.get().isTabOpened()) {
                return null;
            }
        }
        return super.transferRecipe(menu, recipe, recipeSlotsView, player, maxTransfer, doTransfer);
    }
}