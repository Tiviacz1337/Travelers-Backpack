package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.network.ServerboundShowToolSlotsPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.network.PacketDistributor;

public class ToolSlotsWidget extends WidgetBase<BackpackScreen> {
    private final int xPos;

    public ToolSlotsWidget(BackpackScreen screen, Point pos, int xPos) {
        super(screen, pos, 10, 10);
        this.xPos = xPos;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(!screen.getWrapper().showToolSlots()) {
            guiGraphics.blit(BackpackScreen.ICONS, pos.x(), pos.y(), 4, 28, width, height); //0. 24
        } else {
            guiGraphics.blit(BackpackScreen.ICONS, pos.x(), pos.y(), 4, 43, width, height);
            renderToolsAddition(guiGraphics, screen.getWrapper().getTools().getSlots(), pos.x() - 131 - xPos, pos.y() + 16 + 10 - 20);
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