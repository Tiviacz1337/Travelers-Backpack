package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.inventory.sorter.ContainerSorter;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.network.ServerboundSlotPacket;
import com.tiviacz.travelersbackpack.network.ServerboundSorterPacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class SortWidget extends WidgetBase
{
    public SortWidget(TravelersBackpackScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        this.isVisible = false;
        this.showTooltip = true;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, Minecraft minecraft, int mouseX, int mouseY)
    {
        if(isVisible())
        {
            if(isWidgetActive())
            {
                guiGraphics.blit(TravelersBackpackScreen.SETTINGS_TRAVELERS_BACKPACK, x, y, 0, 41, width, height);
            }
            else
            {
                guiGraphics.blit(TravelersBackpackScreen.SETTINGS_TRAVELERS_BACKPACK, x, y, 32, isWidgetActive ? 19 : 0, width, height);
            }
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY)
    {
        if(isMouseOver(mouseX, mouseY) && showTooltip)
        {
            //String[] s =  I18n.get("screen.travelersbackpack.unsortable").split("\n");
            //List<Component> component = new ArrayList<>();
            //component.add(Component.literal(s[0]));
            //component.add(Component.literal(s[1]));

            List<Component> components = new ArrayList<>(TextUtils.getTranslatedSplittedText("screen.travelersbackpack.unsortable", null));

            if(mouseX >= x + 1 && mouseY >= y + 15 && mouseX < x + 11 && mouseY < y + 25)
            {
                components.add(Component.translatable("screen.travelersbackpack.select_all"));
            }
            if(mouseX >= x + 13 && mouseY >= y + 15 && mouseX < x + 23 && mouseY < y + 25)
            {
                components.add(Component.translatable("screen.travelersbackpack.remove_all"));
            }
            guiGraphics.renderComponentTooltip(screen.getFont(), components, mouseX, mouseY);
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

        if(screen.container.getSlotManager().isSelectorActive(SlotManager.MEMORY))
        {
            return false;
        }

        if(isMouseOver(mouseX, mouseY) && this.isWidgetActive)
        {
            if(mouseX >= x + 1 && mouseY >= y + 15 && mouseX < x + 11 && mouseY < y + 25)
            {
                for(int i = 1; i <= screen.container.getTier().getStorageSlotsWithCrafting(); i++)
                {
                    if(screen.container.getSlotManager().isSlot(SlotManager.UNSORTABLE, i - 1)) continue;

                    screen.container.getSlotManager().setUnsortableSlot(i - 1);
                }
                screen.playUIClickSound();
                return true;
            }

            if(mouseX >= x + 13 && mouseY >= y + 15 && mouseX < x + 23 && mouseY < y + 25)
            {
                screen.container.getSlotManager().clearUnsortables();
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

            //Turns slot checking on server
            TravelersBackpack.NETWORK.sendToServer(new ServerboundSorterPacket(screen.container.getScreenID(), ContainerSorter.SORT, BackpackUtils.isShiftPressed()));

            //Turns slot checking on client
            TravelersBackpack.NETWORK.sendToServer(new ServerboundSlotPacket(screen.container.getScreenID(), screen.container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE), screen.container.getSlotManager().getUnsortableSlots().stream().mapToInt(i -> i).toArray()));
            screen.container.getSlotManager().setSelectorActive(SlotManager.UNSORTABLE, !screen.container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE));

            this.screen.playUIClickSound();
            return true;
        }
        return false;
    }

    @Override
    public void setFocused(boolean p_265728_) {

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