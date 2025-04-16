package com.tiviacz.travelersbackpack.compat.emi;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.BackpackSettingsScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingUpgrade;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.widget.Bounds;
import net.minecraft.world.inventory.Slot;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@EmiEntrypoint
public class EmiCompat implements EmiPlugin {
    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.addExclusionArea(BackpackSettingsScreen.class, ((screen, consumer) -> {
            screen.children().stream().filter(w -> w instanceof WidgetBase).forEach(widget -> {
                int[] size = ((WidgetBase)widget).getWidgetSizeAndPos();
                consumer.accept(new Bounds(size[0], size[1], size[2], size[3]));
            });
        }));

        emiRegistry.addExclusionArea(BackpackScreen.class, ((screen, consumer) -> {
            int[] s = screen.settingsWidget.getWidgetSizeAndPos();
            consumer.accept(new Bounds(s[0], s[1], s[2], s[3]));

            screen.children().stream().filter(w -> w instanceof UpgradeWidgetBase).forEach(widget -> {
                int[] size = ((UpgradeWidgetBase)widget).getWidgetSizeAndPos();
                consumer.accept(new Bounds(size[0], size[1], size[2], size[3]));
            });
            screen.upgradeSlots.forEach(slot -> {
                if(!slot.isHidden()) {
                    int[] size = slot.getUpgradeSlotSizeAndPos();
                    consumer.accept(new Bounds(size[0], size[1], size[2], size[3]));
                }
            });
        }));

        emiRegistry.addRecipeHandler(ModMenuTypes.BACKPACK_BLOCK_MENU.get(), new GridMenuInfo<>());
        emiRegistry.addRecipeHandler(ModMenuTypes.BACKPACK_MENU.get(), new GridMenuInfo<>());
    }

    private static class GridMenuInfo<T extends BackpackBaseMenu> implements StandardRecipeHandler<T> {
        @Override
        public @Nullable Slot getOutputSlot(T handler) {
            return handler.getSlot(handler.CRAFTING_RESULT);
        }

        @Override
        public List<Slot> getInputSources(T handler) {
            List<Slot> list = new ArrayList<>();
            //Backpack Inv
            for(int i = 0; i < handler.BACKPACK_INV_END; i++) {
                list.add(handler.getSlot(i));
            }
            //Player Inv
            for(int i = handler.PLAYER_INV_START; i < handler.PLAYER_HOT_END; i++) {
                if(handler.getWrapper().getScreenID() == Reference.ITEM_SCREEN_ID && handler.getSlot(i) instanceof DisabledSlot)
                    continue;

                list.add(handler.getSlot(i));
            }
            return list;
        }

        @Override
        public List<Slot> getCraftingSlots(T handler) {
            List<Slot> list = new ArrayList<>();
            CraftingUpgrade upgrade = handler.getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).get();
            if(upgrade.isTabOpened()) {
                int firstCraftSlot = handler.CRAFTING_GRID_START;
                for(int i = 0; i < 9; i++) {
                    list.add(handler.getSlot(firstCraftSlot + i));
                }
            }
            return list;
        }

        @Override
        public boolean craft(EmiRecipe recipe, EmiCraftContext<T> context) {
            CraftingUpgrade upgrade = context.getScreenHandler().getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).get();
            if(!upgrade.isTabOpened()) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.UPGRADE_TAB, upgrade.getDataHolderSlot(), true, ServerActions.TAB_OPEN);
                return false;
            }
            return StandardRecipeHandler.super.craft(recipe, context);
        }

        @Override
        public boolean canCraft(EmiRecipe recipe, EmiCraftContext<T> context) {
            return StandardRecipeHandler.super.canCraft(recipe, context) && context.getScreenHandler().getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).isPresent();
        }

        @Override
        public boolean supportsRecipe(EmiRecipe recipe) {
            return recipe.getCategory() == VanillaEmiRecipeCategories.CRAFTING && recipe.supportsRecipeTree();
        }
    }
}