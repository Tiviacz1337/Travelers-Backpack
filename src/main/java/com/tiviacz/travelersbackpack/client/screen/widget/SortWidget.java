package com.tiviacz.travelersbackpack.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.inventory.sorter.InventorySorter;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.network.SSlotPacket;
import com.tiviacz.travelersbackpack.network.SSorterPacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

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
    protected void renderBg(MatrixStack matrixStack, Minecraft minecraft, int mouseX, int mouseY)
    {
        minecraft.getTextureManager().bind(TravelersBackpackScreen.SETTTINGS_TRAVELERS_BACKPACK);

        if(isVisible())
        {
            if(isWidgetActive())
            {
                blit(matrixStack, x, y, 0, 41, width, height);
            }
            else
            {
                blit(matrixStack, x, y, 32, isWidgetActive ? 19 : 0, width, height);
            }
        }
    }

    @Override
    public void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if(isHovered && showTooltip)
        {
            //String[] s = I18n.get("screen.travelersbackpack.unsortable").split("\n");
            //List<ITextComponent> component = new ArrayList<>();
            //component.add(new StringTextComponent(s[0]));
           // component.add(new StringTextComponent(s[1]));

            List<ITextComponent> components = new ArrayList<>(TextUtils.getTranslatedSplittedText("screen.travelersbackpack.unsortable", null));

            if(isWidgetActive)
            {
                if(mouseX >= x + 1 && mouseY >= y + 15 && mouseX < x + 11 && mouseY < y + 25)
                {
                    components.add(new TranslationTextComponent("screen.travelersbackpack.select_all"));
                }
                if(mouseX >= x + 13 && mouseY >= y + 15 && mouseX < x + 23 && mouseY < y + 25)
                {
                    components.add(new TranslationTextComponent("screen.travelersbackpack.remove_all"));
                }
            }
            screen.renderComponentTooltip(matrixStack, components, mouseX, mouseY);
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
        if(screen.inv.getSlotManager().isSelectorActive(SlotManager.MEMORY))
        {
            return false;
        }

        if(isHovered && this.isWidgetActive)
        {
            if(mouseX >= x + 1 && mouseY >= y + 15 && mouseX < x + 11 && mouseY < y + 25)
            {
                for(int i = 10; i <= screen.inv.getTier().getStorageSlots() + 3; i++)
                {
                    if(screen.inv.getSlotManager().isSlot(SlotManager.UNSORTABLE, i - 10)) continue;

                    screen.inv.getSlotManager().setUnsortableSlot(i - 10);
                }
                screen.playUIClickSound();
                return true;
            }

            if(mouseX >= x + 13 && mouseY >= y + 15 && mouseX < x + 23 && mouseY < y + 25)
            {
                screen.inv.getSlotManager().clearUnsortables();
                screen.playUIClickSound();
                return true;
            }
        }

        if(isHovered)
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
            TravelersBackpack.NETWORK.sendToServer(new SSorterPacket(screen.inv.getScreenID(), InventorySorter.SORT, BackpackUtils.isShiftPressed()));

            //Turns slot checking on client
            TravelersBackpack.NETWORK.sendToServer(new SSlotPacket(screen.inv.getScreenID(), screen.inv.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE), screen.inv.getSlotManager().getUnsortableSlots().stream().mapToInt(i -> i).toArray()));
            screen.inv.getSlotManager().setSelectorActive(SlotManager.UNSORTABLE, !screen.inv.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE));

            this.screen.playUIClickSound();
            return true;
        }
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