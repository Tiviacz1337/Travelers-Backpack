package com.tiviacz.travelersbackpack.compat.rei;

import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBaseScreenHandler;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;
import me.shedaniel.rei.api.common.transfer.RecipeFinder;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoContext;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoRegistry;
import me.shedaniel.rei.api.common.transfer.info.simple.SimpleGridMenuInfo;
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import net.minecraft.item.ItemStack;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReiCompat implements REIServerPlugin
{
    @Override
    public double getPriority()
    {
        return 0D;
    }

    @Override
    public void registerMenuInfo(MenuInfoRegistry registry)
    {
        registry.register(BuiltinPlugin.CRAFTING, TravelersBackpackBaseScreenHandler.class, new GridMenuInfo<>());
    }

    private static class GridMenuInfo<T extends TravelersBackpackBaseScreenHandler, D extends SimpleGridMenuDisplay> extends RecipeBookGridMenuInfo<T, D>
    {
        @Override
        public IntStream getInputStackSlotIds(MenuInfoContext<T, ?, D> context)
        {
            return IntStream.range(1, 10);
        }
    }

    public static class RecipeBookGridMenuInfo<T extends TravelersBackpackBaseScreenHandler, D extends SimpleGridMenuDisplay> implements SimpleGridMenuInfo<T, D>
    {
        @Override
        public int getCraftingResultSlotIndex(T menu)
        {
            return 0;
        }

        @Override
        public int getCraftingWidth(T menu)
        {
            return 3;
        }

        @Override
        public int getCraftingHeight(T menu)
        {
            return 3;
        }

        @Override
        public void clearInputSlots(T menu)
        {
            menu.craftMatrix.clear();
        }

        @Override
        public void populateRecipeFinder(T menu, RecipeFinder finder)
        {
            menu.craftMatrix.provideRecipeInputs(new net.minecraft.recipe.RecipeMatcher()
            {
                @Override
                public void addUnenchantedInput(ItemStack stack)
                {
                    finder.addNormalItem(stack);
                }
            });
        }

        @Override
        public Iterable<SlotAccessor> getInventorySlots(MenuInfoContext<T, ?, D> context)
        {
            Tiers.Tier tier = context.getMenu().inventory.getTier();

            return IntStream.range(10, tier.getStorageSlots() + 46).filter(i -> i <= 10 + tier.getStorageSlots() - 7 || i >= 10 + tier.getStorageSlots()).mapToObj(index -> SlotAccessor.fromSlot(context.getMenu().getSlot(index))).collect(Collectors.toList());
        }
    }
}