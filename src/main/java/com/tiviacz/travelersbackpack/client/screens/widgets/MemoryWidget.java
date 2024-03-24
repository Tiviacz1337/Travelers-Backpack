package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.inventory.sorter.ContainerSorter;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.network.ServerboundMemoryPacket;
import com.tiviacz.travelersbackpack.network.ServerboundSorterPacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class MemoryWidget extends WidgetBase
{
    public MemoryWidget(TravelersBackpackScreen screen, int x, int y, int width, int height)
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
            guiGraphics.blit(TravelersBackpackScreen.SETTINGS_TRAVELERS_BACKPACK, x, y, 16, isWidgetActive ? 19 : 0, width, height);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY)
    {
        if(isMouseOver(mouseX, mouseY) && showTooltip)
        {
            guiGraphics.renderComponentTooltip(screen.getFont(), TextUtils.getTranslatedSplittedText("screen.travelersbackpack.memory", null), mouseX, mouseY);
        }
    }

    @Override
    public void setWidgetStatus(boolean status)
    {
        super.setWidgetStatus(status);
        screen.sortWidget.setTooltipVisible(!status);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
        if(!this.screen.settingsWidget.isWidgetActive()) return false;

        if(screen.container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE))
        {
            return false;
        }

        if(isMouseOver(pMouseX, pMouseY))
        {
            setWidgetStatus(!this.isWidgetActive);

            //Turns slot checking on server
            //TravelersBackpack.NETWORK.send(new ServerboundSorterPacket(screen.container.getScreenID(), ContainerSorter.MEMORY, BackpackUtils.isShiftPressed()), PacketDistributor.SERVER.noArg());
            PacketDistributor.SERVER.noArg().send(new ServerboundSorterPacket(screen.container.getScreenID(), ContainerSorter.MEMORY, BackpackUtils.isShiftPressed()));

            //Turns slot checking on client
            //TravelersBackpack.NETWORK.send(new ServerboundMemoryPacket(screen.container.getScreenID(), screen.container.getSlotManager().isSelectorActive(SlotManager.MEMORY), screen.container.getSlotManager().getMemorySlots().stream().mapToInt(Pair::getFirst).toArray(), screen.container.getSlotManager().getMemorySlots().stream().map(Pair::getSecond).toArray(ItemStack[]::new)), PacketDistributor.SERVER.noArg());
            PacketDistributor.SERVER.noArg().send(new ServerboundMemoryPacket(screen.container.getScreenID(), screen.container.getSlotManager().isSelectorActive(SlotManager.MEMORY), screen.container.getSlotManager().getMemorySlots().stream().mapToInt(Pair::getFirst).toArray(), screen.container.getSlotManager().getMemorySlots().stream().map(Pair::getSecond).toArray(ItemStack[]::new)));
            screen.container.getSlotManager().setSelectorActive(SlotManager.MEMORY, !screen.container.getSlotManager().isSelectorActive(SlotManager.MEMORY));

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
}