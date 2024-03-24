package com.tiviacz.travelersbackpack.compat.rei;

import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBaseScreenHandler;
import com.tiviacz.travelersbackpack.inventory.screen.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.util.Reference;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoContext;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoRegistry;
import me.shedaniel.rei.api.common.transfer.info.MenuTransferException;
import me.shedaniel.rei.api.common.transfer.info.simple.SimpleGridMenuInfo;
import me.shedaniel.rei.api.common.transfer.info.simple.SimpleMenuInfoProvider;
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;

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
    public void registerMenuInfo(MenuInfoRegistry registry)
    {
        registry.register(BuiltinPlugin.CRAFTING, TravelersBackpackBaseScreenHandler.class, SimpleMenuInfoProvider.of(GridMenuInfo::new));
    }

    public static class GridMenuInfo<T extends TravelersBackpackBaseScreenHandler, D extends SimpleGridMenuDisplay> implements SimpleGridMenuInfo<T, D>
    {
        private final D display;

        public GridMenuInfo(D display) {
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
        public IntStream getInputStackSlotIds(MenuInfoContext<T, ?, D> context)
        {
            int firstCraftSlot = context.getMenu().inventory.getCombinedInventory().size() - 8;
            return IntStream.range(firstCraftSlot, firstCraftSlot + 9);
        }

        @Override
        public Iterable<SlotAccessor> getInventorySlots(MenuInfoContext<T, ?, D> context)
        {
            List<SlotAccessor> list = new ArrayList<>();

            //Backpack Inv
            for(int i = 1; i <= context.getMenu().inventory.getInventory().size(); i++)
            {
                list.add(SlotAccessor.fromSlot(context.getMenu().getSlot(i)));
            }

            //Player Inv
            for(int i = context.getMenu().inventory.getCombinedInventory().size() + 1; i < context.getMenu().inventory.getCombinedInventory().size() + 1 + PlayerInventory.MAIN_SIZE; i++)
            {
                if(context.getMenu().inventory.getScreenID() == Reference.ITEM_SCREEN_ID && context.getMenu().getSlot(i) instanceof DisabledSlot) continue;

                list.add(SlotAccessor.fromSlot(context.getMenu().getSlot(i)));
            }
            return list;
        }

        @Override
        public void validate(MenuInfoContext<T, ?, D> context) throws MenuTransferException
        {
            if(!context.getMenu().inventory.getSettingsManager().hasCraftingGrid())
            {
                throw new MenuTransferException(new TranslatableText("error.rei.no.handlers.applicable"));
            }
            else
            {
                //Open Tab
                context.getMenu().inventory.getSettingsManager().set(SettingsManager.CRAFTING, SettingsManager.SHOW_CRAFTING_GRID, (byte)1);

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeByte(context.getMenu().inventory.getScreenID()).writeByte(SettingsManager.CRAFTING).writeInt(SettingsManager.SHOW_CRAFTING_GRID).writeByte((byte)1);

                ClientPlayNetworking.send(ModNetwork.SETTINGS_ID, buf);
            }
            SimpleGridMenuInfo.super.validate(context);
        }
    }
}