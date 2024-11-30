package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpackneo.network.ServerboundShowToolSlotsPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.network.PacketDistributor;

public class ToolSlotsWidget extends WidgetBase<BackpackScreen> {
    public ToolSlotsWidget(BackpackScreen screen, Point pos) {
        super(screen, pos, 18, 15);
        //this.isVisible = screen.getWrapper().getTools().getSlots() > 0;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(!screen.getWrapper().showToolSlots()) {
            guiGraphics.blit(BackpackScreen.ICONS, pos.x(), pos.y(), 0, 24, width, height); //0. 24
        } else {
            guiGraphics.blit(BackpackScreen.ICONS, pos.x(), pos.y(), 0, 39, width, height);
            renderToolsAddition(guiGraphics, screen.getWrapper().getTools().getSlots(), pos.x() - 27, pos.y() + 16 + 10);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isMouseOver(mouseX, mouseY)) {
            PacketDistributor.sendToServer(new ServerboundShowToolSlotsPacket(!screen.getWrapper().showToolSlots()));
            this.screen.playUIClickSound();
            return true;
        }
        return false;
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {

    }

    public void renderToolsAddition(GuiGraphics guiGraphics, int size, int x, int y) {
        //Top bar
        guiGraphics.blit(BackpackScreen.ICONS, x, y, 0, 67, 23, 5);

        //Middle
        for(int i = 0; i < size; i++) {
            guiGraphics.blit(BackpackScreen.ICONS, x, y + 5 + (i * 18), 0, 72, 23, 18);
        }

        //Bottom bar
        guiGraphics.blit(BackpackScreen.ICONS, x, y + 5 + (size * 18), 0, 90, 23, 5);
    }
}