package com.tiviacz.travelersbackpack.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.inventory.sorter.InventorySorter;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.network.SMemoryPacket;
import com.tiviacz.travelersbackpack.network.SSorterPacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class MemoryWidget extends WidgetBase
{
    public MemoryWidget(TravelersBackpackScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        this.isVisible = false;
        this.showTooltip = true;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, Minecraft minecraft, int mouseX, int mouseY)
    {
        minecraft.getTextureManager().bind(TravelersBackpackScreen.SETTINGS_TRAVELERS_BACKPACK);

        if(isVisible())
        {
            blit(matrixStack, x, y, 16, isWidgetActive ? 19 : 0, width, height);
        }
    }

    @Override
    public void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if(isMouseOver(mouseX, mouseY) && showTooltip)
        {
            //String[] s = I18n.get("screen.travelersbackpack.memory").split("\n");
            //screen.renderComponentTooltip(matrixStack, Arrays.asList(new StringTextComponent(s[0]), new StringTextComponent(s[1])), mouseX, mouseY);
            screen.renderComponentTooltip(matrixStack, TextUtils.getTranslatedSplittedText("screen.travelersbackpack.memory", null), mouseX, mouseY);
            //screen.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.memory"), mouseX, mouseY);
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

        if(screen.inv.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE))
        {
            return false;
        }

        if(isMouseOver(pMouseX, pMouseY))
        {
            setWidgetStatus(!this.isWidgetActive);

            //Turns slot checking on server
            TravelersBackpack.NETWORK.sendToServer(new SSorterPacket(screen.inv.getScreenID(), InventorySorter.MEMORY, BackpackUtils.isShiftPressed()));

            //Turns slot checking on client
            TravelersBackpack.NETWORK.sendToServer(new SMemoryPacket(screen.inv.getScreenID(), screen.inv.getSlotManager().isSelectorActive(SlotManager.MEMORY), screen.inv.getSlotManager().getMemorySlots().stream().mapToInt(Pair::getFirst).toArray(), screen.inv.getSlotManager().getMemorySlots().stream().map(Pair::getSecond).toArray(ItemStack[]::new)));
            screen.inv.getSlotManager().setSelectorActive(SlotManager.MEMORY, !screen.inv.getSlotManager().isSelectorActive(SlotManager.MEMORY));

            this.screen.playUIClickSound();
            return true;
        }
        return false;
    }
}