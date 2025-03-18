package com.tiviacz.travelersbackpack.compat.rei;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.BackpackSettingsScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingUpgrade;
import com.tiviacz.travelersbackpack.network.ServerboundTabPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import com.tiviacz.travelersbackpack.util.Reference;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ReiClientCompat implements REIClientPlugin {
    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(new BackpackTransferHandler());
    }

    public static class BackpackTransferHandler implements SimpleTransferHandler {
        @Override
        public ApplicabilityResult checkApplicable(Context context) {
            if(!BackpackBaseMenu.class.isInstance(context.getMenu())
                    || !BuiltinPlugin.CRAFTING.equals(context.getDisplay().getCategoryIdentifier())
                    || context.getContainerScreen() == null) {
                return ApplicabilityResult.createNotApplicable();
            } else {
                if(context.getMenu() instanceof BackpackBaseMenu menu && menu.getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).isPresent()) {
                    return ApplicabilityResult.createApplicable();
                }
                return ApplicabilityResult.createNotApplicable();
            }
        }

        @Override
        public Iterable<SlotAccessor> getInputSlots(Context context) {
            if(context.getMenu() instanceof BackpackBaseMenu menu) {
                return IntStream.range(menu.CRAFTING_GRID_START, menu.CRAFTING_GRID_START + 9)
                        .mapToObj(id -> SlotAccessor.fromSlot(context.getMenu().getSlot(id)))
                        .toList();
            }
            return List.of();
        }

        @Override
        public Iterable<SlotAccessor> getInventorySlots(Context context) {
            if(context.getMenu() instanceof BackpackBaseMenu menu) {
                List<SlotAccessor> list = new ArrayList<>();
                //Backpack Inv
                for(int i = 0; i < menu.BACKPACK_INV_END; i++) {
                    list.add(SlotAccessor.fromSlot(menu.getSlot(i)));
                }
                //Player Inv
                for(int i = menu.PLAYER_INV_START; i < menu.PLAYER_HOT_END; i++) {
                    if(menu.getWrapper().getScreenID() == Reference.ITEM_SCREEN_ID && menu.getSlot(i) instanceof DisabledSlot)
                        continue;

                    list.add(SlotAccessor.fromSlot(menu.getSlot(i)));
                }
                return list;
            }
            return List.of();
        }

        @Override
        public Result handle(Context context) {
            if(context.getMenu() instanceof BackpackBaseMenu menu) {
                CraftingUpgrade upgrade = menu.getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).get();
                if(!upgrade.isTabOpened() && context.isActuallyCrafting()) {
                    PacketDistributor.sendToServer(new ServerboundTabPacket(upgrade.getDataHolderSlot(), true, ServerboundTabPacket.TAB_OPEN));
                }
            }
            return handleSimpleTransfer(context, getMissingInputRenderer(), getInputsIndexed(context), getInputSlots(context), getInventorySlots(context));
        }
    }

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(BackpackSettingsScreen.class, screen -> {
            List<Rectangle> ret = new ArrayList<>();
            screen.children().stream().filter(w -> w instanceof WidgetBase).forEach(widget -> {
                int[] size = ((WidgetBase)widget).getWidgetSizeAndPos();
                ret.add(new Rectangle(size[0], size[1], size[2], size[3]));
            });
            return ret;
        });

        zones.register(BackpackScreen.class, screen -> {
            List<Rectangle> ret = new ArrayList<>();
            int[] s = screen.settingsWidget.getWidgetSizeAndPos();
            ret.add(new Rectangle(s[0], s[1], s[2], s[3]));

            screen.children().stream().filter(w -> w instanceof UpgradeWidgetBase).forEach(widget -> {
                int[] size = ((UpgradeWidgetBase)widget).getWidgetSizeAndPos();
                ret.add(new Rectangle(size[0], size[1], size[2], size[3]));
            });
            screen.upgradeSlots.forEach(slot -> {
                if(!slot.isHidden()) {
                    int[] size = slot.getUpgradeSlotSizeAndPos();
                    ret.add(new Rectangle(size[0], size[1], size[2], size[3]));
                }
            });
            return ret;
        });
    }
}