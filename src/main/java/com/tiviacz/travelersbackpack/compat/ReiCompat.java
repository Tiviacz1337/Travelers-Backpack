package com.tiviacz.travelersbackpack.compat;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.inventory.CraftingInventoryImproved;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBaseScreenHandler;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.*;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.plugin.crafting.DefaultCraftingDisplay;
import me.shedaniel.rei.server.ContainerContext;
import me.shedaniel.rei.server.ContainerInfo;
import me.shedaniel.rei.server.SlotStackAccessor;
import me.shedaniel.rei.server.StackAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReiCompat implements REIPluginV0
{
    public static final Identifier REI_PLUGIN = new Identifier(TravelersBackpack.MODID, "rei_plugin");

    @Override
    public Identifier getPluginIdentifier()
    {
        return REI_PLUGIN;
    }

    @Override
    public void registerOthers(RecipeHelper recipeHelper)
    {
        recipeHelper.registerAutoCraftingHandler(new BackpackAutoTransfer());
    }

    @Override
    public void registerBounds(DisplayHelper displayHelper)
    {
        BaseBoundsHandler.getInstance().registerExclusionZones(TravelersBackpackHandledScreen.class, () ->
        {
            TravelersBackpackHandledScreen screen = (TravelersBackpackHandledScreen) MinecraftClient.getInstance().currentScreen;

            List<Rectangle> ret = new ArrayList<>();
            int[] s = screen.settingsWidget.getWidgetSizeAndPos();
            ret.add(new Rectangle(s[0], s[1], s[2], s[3]));
            int[] sort = screen.sortWidget.getWidgetSizeAndPos();
            if (screen.sortWidget.isVisible()) ret.add(new Rectangle(sort[0], sort[1], sort[2], sort[3]));
            int[] memory = screen.memoryWidget.getWidgetSizeAndPos();
            if (screen.memoryWidget.isVisible()) ret.add(new Rectangle(memory[0], memory[1], memory[2], memory[3]));
            int[] crafting = screen.craftingWidget.getWidgetSizeAndPos();
            if(screen.craftingWidget.isVisible()) ret.add(new Rectangle(crafting[0], crafting[1], crafting[2], crafting[3]));
            return ret;
        });
    }

    public static class BackpackAutoTransfer implements AutoTransferHandler
    {
        @NotNull
        @Override
        public Result handle(@NotNull Context context)
        {
            if (context.getRecipe() instanceof TransferRecipeDisplay && ClientHelper.getInstance().canUseMovePackets())
                return Result.createNotApplicable();
            RecipeDisplay display = context.getRecipe();
            if (!(context.getContainer() instanceof TravelersBackpackBaseScreenHandler))
                return Result.createNotApplicable();
            TravelersBackpackBaseScreenHandler container = (TravelersBackpackBaseScreenHandler)context.getContainer();
            if (container == null)
                return Result.createNotApplicable();
            if (display instanceof DefaultCraftingDisplay) {
                DefaultCraftingDisplay craftingDisplay = (DefaultCraftingDisplay) display;
                if (craftingDisplay.getOptionalRecipe().isPresent()) {
                    int h = 3, w = 3;

                    Recipe<?> recipe = (craftingDisplay).getOptionalRecipe().get();
                    if (craftingDisplay.getHeight() > h || craftingDisplay.getWidth() > w)
                        return Result.createFailed(I18n.translate("error.rei.transfer.too_small", h, w));
                    if (!context.getMinecraft().player.getRecipeBook().contains(recipe))
                        return Result.createFailed(I18n.translate("error.rei.recipe.not.unlocked"));
                    if (!context.isActuallyCrafting())
                        return Result.createSuccessful();
                    context.getMinecraft().openScreen(context.getContainerScreen());
                    context.getMinecraft().interactionManager.clickRecipe(container.syncId, recipe, Screen.hasShiftDown());
                    return Result.createSuccessful();
                }
            }
            return Result.createNotApplicable();
        }
    }

    public static class BackpackContainerInfo implements ContainerInfo<TravelersBackpackBaseScreenHandler>
    {
        public static BackpackContainerInfo create()
        {
            return new BackpackContainerInfo();
        }

        @Override
        public Class<? extends ScreenHandler> getContainerClass()
        {
            return TravelersBackpackBaseScreenHandler.class;
        }

        @Override
        public int getCraftingResultSlotIndex(TravelersBackpackBaseScreenHandler container)
        {
            return 0;
        }

        @Override
        public int getCraftingWidth(TravelersBackpackBaseScreenHandler container)
        {
            return 3;
        }

        @Override
        public int getCraftingHeight(TravelersBackpackBaseScreenHandler container) {
            return 3;
        }

        @Override
        public void clearCraftingSlots(TravelersBackpackBaseScreenHandler container)
        {
            container.craftMatrix.clear();
        }

        @Override
        public List<StackAccessor> getGridStacks(ContainerContext<TravelersBackpackBaseScreenHandler> context)
        {
            List<StackAccessor> list = new ArrayList<>();
            Tiers.Tier tier = context.getContainer().inventory.getTier();

            for(int i = 1; i < tier.getStorageSlotsWithCrafting() + 1; i++)
            {
                if(context.getContainer().getSlot(i).inventory instanceof CraftingInventoryImproved)
                {
                    list.add(new SlotStackAccessor(context.getContainer().getSlot(i)));
                }
            }
            return list;
        }

        @Override
        public List<StackAccessor> getInventoryStacks(ContainerContext<TravelersBackpackBaseScreenHandler> context)
        {
            List<StackAccessor> list = new ArrayList<>();
            Tiers.Tier tier = context.getContainer().inventory.getTier();

            //Backpack Inv
            for(int i = 1; i < tier.getStorageSlotsWithCrafting() + 1; i++)
            {
                if(context.getContainer().getSlot(i).inventory instanceof CraftingInventoryImproved)
                {
                    continue;
                }
                list.add(new SlotStackAccessor(context.getContainer().getSlot(i)));
            }

            //Player Inv
            for(int i = (tier.getAllSlots() + 14); i < (tier.getAllSlots() + 14) + 36; i++)
            {
                list.add(new SlotStackAccessor(context.getContainer().getSlot(i)));
            }
            return list;
        }
    }
}