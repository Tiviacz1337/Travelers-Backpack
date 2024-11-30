package com.tiviacz.travelersbackpackneo.compat.rei;

import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingUpgrade;
import com.tiviacz.travelersbackpackneo.network.ServerboundTabPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;
import me.shedaniel.rei.forge.REIPluginCommon;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@REIPluginCommon
public class ReiCompat implements REIClientPlugin {
    @Override
    public double getPriority() {
        return 0D;
    }

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
                if(context.getMenu() instanceof BackpackBaseMenu menu && menu.getWrapper().getUpgradeManager().craftingUpgrade.isPresent()) {
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
                CraftingUpgrade upgrade = menu.getWrapper().getUpgradeManager().craftingUpgrade.get();
                if(!upgrade.isTabOpened()) {
                    PacketDistributor.sendToServer(new ServerboundTabPacket(upgrade.getDataHolderSlot(), true, ServerboundTabPacket.TAB_OPEN));
                }
            }
            return handleSimpleTransfer(context, getMissingInputRenderer(), getInputsIndexed(context), getInputSlots(context), getInventorySlots(context));
        }
    }
}