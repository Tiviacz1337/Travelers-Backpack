package com.tiviacz.travelersbackpack.client.screen.widget;

import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.sorter.InventorySorter;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.TextUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class SortWidget extends WidgetBase
{
    public SortWidget(TravelersBackpackHandledScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        this.isVisible = false;
        this.showTooltip = true;
    }

    @Override
    protected void drawBackground(DrawContext context, MinecraftClient minecraft, int mouseX, int mouseY)
    {
        if(isVisible())
        {
            if(isWidgetActive())
            {
                context.drawTexture(TravelersBackpackHandledScreen.SETTINGS_TRAVELERS_BACKPACK, x, y, 0, 41, width, height);
            }
            else
            {
                context.drawTexture(TravelersBackpackHandledScreen.SETTINGS_TRAVELERS_BACKPACK, x, y, 32, isWidgetActive ? 19 : 0, width, height);
            }
        }
    }

    @Override
    public void drawMouseoverTooltip(DrawContext context, int mouseX, int mouseY)
    {
        if(isMouseOver(mouseX, mouseY) && showTooltip)
        {
            List<Text> texts = new ArrayList<>(TextUtils.getTranslatedSplittedText("screen.travelersbackpack.unsortable", null));

            if(mouseX >= x + 1 && mouseY >= y + 15 && mouseX < x + 11 && mouseY < y + 25)
            {
                texts.add(Text.translatable("screen.travelersbackpack.select_all"));
            }
            if(mouseX >= x + 13 && mouseY >= y + 15 && mouseX < x + 23 && mouseY < y + 25)
            {
                texts.add(Text.translatable("screen.travelersbackpack.remove_all"));
            }
            context.drawTooltip(screen.getTextRenderer(), texts, mouseX, mouseY);
        }
    }

    @Override
    public void setWidgetStatus(boolean status)
    {
        super.setWidgetStatus(status);
        screen.memoryWidget.setTooltipVisible(!status);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int pButton)
    {
        if(!this.screen.settingsWidget.isWidgetActive()) return false;

        if(screen.inventory.getSlotManager().isSelectorActive(SlotManager.MEMORY))
        {
            return false;
        }

        if(isMouseOver(mouseX, mouseY) && this.isWidgetActive)
        {
            if(mouseX >= x + 1 && mouseY >= y + 15 && mouseX < x + 11 && mouseY < y + 25)
            {
                for(int i = 1; i <= screen.inventory.getInventory().size(); i++)
                {
                    if(screen.inventory.getSlotManager().isSlot(SlotManager.UNSORTABLE, i - 1)) continue;

                    screen.inventory.getSlotManager().setUnsortableSlot(i - 1);
                }
                screen.playUIClickSound();
                return true;
            }

            if(mouseX >= x + 13 && mouseY >= y + 15 && mouseX < x + 23 && mouseY < y + 25)
            {
                screen.inventory.getSlotManager().clearUnsortables();
                screen.playUIClickSound();
                return true;
            }
        }

        if(isMouseOver(mouseX, mouseY))
        {
            setWidgetStatus(!this.isWidgetActive);

            if(this.isWidgetActive)
            {
                this.height = 30;
                this.width = 28;
                this.zOffset = 1;
            }

            if(!this.isWidgetActive)
            {
                this.height = 18;
                this.width = 15;
                this.zOffset = 0;
            }

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeByte(screen.inventory.getScreenID()).writeByte(InventorySorter.SORT).writeBoolean(BackpackUtils.isShiftPressed());

            //Turns slot checking on server
            ClientPlayNetworking.send(ModNetwork.SORTER_ID, buf);

            //Turns slot checking on client
            ClientPlayNetworking.send(ModNetwork.SLOT_ID, PacketByteBufs.copy(PacketByteBufs.create().writeByte(screen.inventory.getScreenID()).writeBoolean(screen.inventory.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE))).writeIntArray(screen.inventory.getSlotManager().getUnsortableSlots().stream().mapToInt(i -> i).toArray()));
            screen.inventory.getSlotManager().setSelectorActive(SlotManager.UNSORTABLE, !screen.inventory.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE));

            screen.playUIClickSound();
            return true;
        }
        return false;
    }

    @Override
    public void setFocused(boolean focused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public int[] getWidgetSizeAndPos()
    {
        int[] size = new int[4];
        size[0] = x;
        size[1] = y;
        size[2] = width;
        size[3] = height;
        return size;
    }
}