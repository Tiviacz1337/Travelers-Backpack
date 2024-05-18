package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBlockEntityScreenHandler;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.library.transfer.BasicRecipeTransferHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import org.jetbrains.annotations.Nullable;

public class BlockEntityTransferHandler extends BasicRecipeTransferHandler<TravelersBackpackBlockEntityScreenHandler, RecipeEntry<CraftingRecipe>>
{
    public BlockEntityTransferHandler(IConnectionToServer serverConnection, IStackHelper stackHelper, IRecipeTransferHandlerHelper handlerHelper, IRecipeTransferInfo<TravelersBackpackBlockEntityScreenHandler, RecipeEntry<CraftingRecipe>> transferInfo)
    {
        super(serverConnection, stackHelper, handlerHelper, transferInfo);
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(TravelersBackpackBlockEntityScreenHandler container, RecipeEntry<CraftingRecipe> recipe, IRecipeSlotsView recipeSlotsView, PlayerEntity player, boolean maxTransfer, boolean doTransfer)
    {
        if(doTransfer)
        {
            container.inventory.getSettingsManager().set(SettingsManager.CRAFTING, SettingsManager.SHOW_CRAFTING_GRID, (byte)1);

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeByte(container.inventory.getScreenID()).writeByte(SettingsManager.CRAFTING).writeInt(SettingsManager.SHOW_CRAFTING_GRID).writeByte((byte)1);

            ClientPlayNetworking.send(ModNetwork.SETTINGS_ID, buf);
        }
        return super.transferRecipe(container, recipe, recipeSlotsView, player, maxTransfer, doTransfer);
    }
}
