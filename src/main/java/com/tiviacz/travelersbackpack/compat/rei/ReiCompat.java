package com.tiviacz.travelersbackpack.compat.rei;

import com.tiviacz.travelersbackpack.inventory.CraftingInventoryImproved;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBaseScreenHandler;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;
import me.shedaniel.rei.api.common.transfer.RecipeFinder;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoContext;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoRegistry;
import me.shedaniel.rei.api.common.transfer.info.simple.SimpleGridMenuInfo;
import me.shedaniel.rei.api.common.transfer.info.simple.SimpleMenuInfoProvider;
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ReiCompat implements REIServerPlugin
{
    @Override
    public double getPriority()
    {
        return 0D;
    }

    @Override
    public void registerMenuInfo(MenuInfoRegistry registry) {
        registry.register(BuiltinPlugin.CRAFTING, TravelersBackpackBaseScreenHandler.class, SimpleMenuInfoProvider.of(GridMenuInfo::new));
    }

    private static class GridMenuInfo<T extends TravelersBackpackBaseScreenHandler, D extends SimpleGridMenuDisplay> extends RecipeBookGridMenuInfo<T, D> {

        public GridMenuInfo(D display) {
            super(display);
        }

        @Override
        public IntStream getInputStackSlotIds(MenuInfoContext<T, ?, D> context) {
            return IntStream.range(1, 10);
        }
    }

    public static class RecipeBookGridMenuInfo<T extends TravelersBackpackBaseScreenHandler, D extends SimpleGridMenuDisplay> implements SimpleGridMenuInfo<T, D> {
        private final D display;

        public RecipeBookGridMenuInfo(D display) {
            this.display = display;
        }

        @Override
        public D getDisplay() {
            return display;
        }

        @Override
        public int getCraftingResultSlotIndex(T menu) {
            return 0;
        }

        @Override
        public int getCraftingWidth(T menu) {
            return 3;
        }

        @Override
        public int getCraftingHeight(T menu) {
            return 3;
        }

        @Override
        public void clearInputSlots(T menu) {
            menu.craftMatrix.clear();
        }

        @Override
        public void populateRecipeFinder(MenuInfoContext<T, ?, D> context, RecipeFinder finder) {
            context.getMenu().craftMatrix.provideRecipeInputs(new net.minecraft.recipe.RecipeMatcher() {
                @Override
                public void addUnenchantedInput(ItemStack stack) {
                    finder.addNormalItem(stack);
                }
            });
        }

        @Override
        public Iterable<SlotAccessor> getInventorySlots(MenuInfoContext<T, ?, D> context)
        {
            List<SlotAccessor> list = new ArrayList<>();
            Tiers.Tier tier = context.getMenu().inventory.getTier();

            //Backpack Inv
            for(int i = 1; i < tier.getStorageSlotsWithCrafting() + 1; i++)
            {
                if(context.getMenu().getSlot(i).inventory instanceof CraftingInventoryImproved)
                {
                    continue;
                }
                list.add(SlotAccessor.fromSlot(context.getMenu().getSlot(i)));
            }

            //Player Inv
            for(int i = (tier.getAllSlots() + 10); i < (tier.getAllSlots() + 10) + PlayerInventory.MAIN_SIZE; i++)
            {
                list.add(SlotAccessor.fromSlot(context.getMenu().getSlot(i)));
            }
            return list;
        }
    }
}