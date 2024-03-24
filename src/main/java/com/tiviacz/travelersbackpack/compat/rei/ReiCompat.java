package com.tiviacz.travelersbackpack.compat.rei;

import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.network.ServerboundSettingsPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoContext;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoRegistry;
import me.shedaniel.rei.api.common.transfer.info.MenuTransferException;
import me.shedaniel.rei.api.common.transfer.info.simple.SimpleGridMenuInfo;
import me.shedaniel.rei.api.common.transfer.info.simple.SimpleMenuInfoProvider;
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;
import me.shedaniel.rei.forge.REIPluginCommon;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@REIPluginCommon
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
        registry.register(BuiltinPlugin.CRAFTING.cast(), TravelersBackpackBaseMenu.class, SimpleMenuInfoProvider.of(GridMenuInfo::new));
    }

    public static class GridMenuInfo implements SimpleGridMenuInfo<TravelersBackpackBaseMenu, SimpleGridMenuDisplay>
    {
        private final SimpleGridMenuDisplay display;

        public GridMenuInfo(SimpleGridMenuDisplay display) {
            this.display = display;
        }

        @Override
        public SimpleGridMenuDisplay getDisplay() {
            return display;
        }

        @Override
        public int getCraftingResultSlotIndex(TravelersBackpackBaseMenu menu) {
            return 0;
        }

        @Override
        public int getCraftingWidth(TravelersBackpackBaseMenu menu) {
            return 3;
        }

        @Override
        public int getCraftingHeight(TravelersBackpackBaseMenu menu) {
            return 3;
        }

        @Override
        public void clearInputSlots(TravelersBackpackBaseMenu menu) {
            menu.craftSlots.clearContent();
        }

        @Override
        public IntStream getInputStackSlotIds(MenuInfoContext<TravelersBackpackBaseMenu, ?, SimpleGridMenuDisplay> context)
        {
            int firstCraftSlot = context.getMenu().container.getCombinedHandler().getSlots() - 8;
            return IntStream.range(firstCraftSlot, firstCraftSlot + 9);
        }

        @Override
        public Iterable<SlotAccessor> getInventorySlots(MenuInfoContext<TravelersBackpackBaseMenu, ?, SimpleGridMenuDisplay> context)
        {
            List<SlotAccessor> list = new ArrayList<>();

            //Backpack Inv
            for(int i = 1; i <= context.getMenu().container.getHandler().getSlots(); i++)
            {
                list.add(SlotAccessor.fromSlot(context.getMenu().getSlot(i)));
            }

            //Player Inv
            for(int i = context.getMenu().container.getCombinedHandler().getSlots() + 1; i < context.getMenu().container.getCombinedHandler().getSlots() + 1 + Inventory.INVENTORY_SIZE; i++)
            {
                if(context.getMenu().container.getScreenID() == Reference.ITEM_SCREEN_ID && context.getMenu().getSlot(i) instanceof DisabledSlot) continue;

                list.add(SlotAccessor.fromSlot(context.getMenu().getSlot(i)));
            }
            return list;
        }

        @Override
        public void validate(MenuInfoContext<TravelersBackpackBaseMenu, ?, SimpleGridMenuDisplay> context) throws MenuTransferException
        {
            if(!context.getMenu().container.getSettingsManager().hasCraftingGrid())
            {
                throw new MenuTransferException(Component.translatable("error.rei.no.handlers.applicable"));
            }
            else
            {
                //Open Tab
                context.getMenu().container.getSettingsManager().set(SettingsManager.CRAFTING, SettingsManager.SHOW_CRAFTING_GRID, (byte)1);
                PacketDistributor.SERVER.noArg().send(new ServerboundSettingsPacket(context.getMenu().container.getScreenID(), SettingsManager.CRAFTING, SettingsManager.SHOW_CRAFTING_GRID, (byte)1));
                //TravelersBackpack.NETWORK.send(new ServerboundSettingsPacket(context.getMenu().container.getScreenID(), SettingsManager.CRAFTING, SettingsManager.SHOW_CRAFTING_GRID, (byte)1), PacketDistributor.SERVER.noArg());
            }
            SimpleGridMenuInfo.super.validate(context);
        }
    }
}