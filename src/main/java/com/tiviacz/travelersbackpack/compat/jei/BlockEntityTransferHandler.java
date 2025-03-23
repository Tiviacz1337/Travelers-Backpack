package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBlockEntityMenu;
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
import org.jetbrains.annotations.Nullable;

public class BlockEntityTransferHandler extends BasicRecipeTransferHandler<BackpackBlockEntityMenu, CraftingRecipe> {
    public BlockEntityTransferHandler(IConnectionToServer serverConnection, IStackHelper stackHelper, IRecipeTransferHandlerHelper handlerHelper, IRecipeTransferInfo<BackpackBlockEntityMenu, CraftingRecipe> transferInfo) {
        super(serverConnection, stackHelper, handlerHelper, transferInfo);
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(BackpackBlockEntityMenu menu, CraftingRecipe recipe, IRecipeSlotsView recipeSlotsView, Player player, boolean maxTransfer, boolean doTransfer) {
        if(doTransfer) {
            CraftingUpgrade upgrade = menu.getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).get();
            if(!upgrade.isTabOpened()) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.UPGRADE_TAB, upgrade.getDataHolderSlot(), true, ServerActions.TAB_OPEN);
            }
        }
        return super.transferRecipe(menu, recipe, recipeSlotsView, player, maxTransfer, doTransfer);
    }
}
