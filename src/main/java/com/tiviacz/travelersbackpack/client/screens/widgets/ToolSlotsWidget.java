package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.network.ServerboundShowToolSlotsPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

public class ToolSlotsWidget extends WidgetBase<BackpackScreen> {
    private final int xPos;

    public ToolSlotsWidget(BackpackScreen screen, Point pos, int xPos) {
        super(screen, pos, 10, 10);
        this.xPos = xPos;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(isMouseOver(mouseX, mouseY)) {
            guiGraphics.blit(RenderType::guiTextured, BackpackScreen.ICONS, pos.x() - 1, pos.y() - 1, 78, 82, width + 2, height + 2, 256, 256);
        }
        if(!screen.getWrapper().showToolSlots()) {
            guiGraphics.blit(RenderType::guiTextured, BackpackScreen.ICONS, pos.x(), pos.y(), 4, 24, width, height, 256, 256);
        } else {
            guiGraphics.blit(RenderType::guiTextured, BackpackScreen.ICONS, pos.x(), pos.y(), 4, 34, width, height, 256, 256);
            renderToolsAddition(guiGraphics, screen.getWrapper().getTools().getSlots(), pos.x() - 130 - xPos, pos.y() + 16 + 10 - 19);
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
        if(isMouseOver(mouseX, mouseY)) {
            if(screen.getWrapper().showToolSlots()) {
                guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.hide_tool_slots"), mouseX, mouseY);
            } else {
                guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.show_tool_slots"), mouseX, mouseY);
            }
        }
    }

    public void renderToolsAddition(GuiGraphics guiGraphics, int size, int x, int y) {
        //Top bar
        guiGraphics.blit(RenderType::guiTextured, BackpackScreen.ICONS, x, y, 0, 67, 23, 5, 256, 256);

        //Middle
        for(int i = 0; i < size; i++) {
            guiGraphics.blit(RenderType::guiTextured, BackpackScreen.ICONS, x, y + 5 + (i * 18), 0, 72, 23, 18, 256, 256);
        }

        //Bottom bar
        guiGraphics.blit(RenderType::guiTextured, BackpackScreen.ICONS, x, y + 5 + (size * 18), 0, 90, 23, 5, 256, 256);
    }
}