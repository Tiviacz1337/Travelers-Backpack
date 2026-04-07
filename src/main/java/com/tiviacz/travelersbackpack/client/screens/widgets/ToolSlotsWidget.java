package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

public class ToolSlotsWidget extends WidgetBase<BackpackScreen> {
    private final int xPos;

    public ToolSlotsWidget(BackpackScreen screen, Point pos, int xPos) {
        super(screen, pos, 10, 10);
        this.xPos = xPos;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(isMouseOver(mouseX, mouseY)) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.ICONS, pos.x() - 1, pos.y() - 1, 78, 82, width + 2, height + 2, 256, 256);
        }
        if(!screen.getWrapper().showToolSlots()) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.ICONS, pos.x(), pos.y(), 4, 24, width, height, 256, 256);
        } else {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.ICONS, pos.x(), pos.y(), 4, 34, width, height, 256, 256);
            renderToolsAddition(guiGraphics, screen.getWrapper().getTools().getSlots(), pos.x() - 130 - xPos, pos.y() + 16 + 10 - 19);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if(isMouseOver(event.x(), event.y())) {
            ServerboundActionTagPacket.create(ServerboundActionTagPacket.SHOW_TOOL_SLOTS, !screen.getWrapper().showToolSlots());
            this.screen.playUIClickSound();
            return true;
        }
        return false;
    }

    @Override
    public void renderTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if(isMouseOver(mouseX, mouseY)) {
            if(screen.getWrapper().showToolSlots()) {
                guiGraphics.setTooltipForNextFrame(screen.getFont(), Component.translatable("screen.travelersbackpack.hide_tool_slots"), mouseX, mouseY);
            } else {
                guiGraphics.setTooltipForNextFrame(screen.getFont(), Component.translatable("screen.travelersbackpack.show_tool_slots"), mouseX, mouseY);
            }
        }
    }

    public void renderToolsAddition(GuiGraphicsExtractor guiGraphics, int size, int x, int y) {
        //Top bar
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.ICONS, x, y, 0, 67, 23, 5, 256, 256);

        //Middle
        for(int i = 0; i < size; i++) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.ICONS, x, y + 5 + (i * 18), 0, 72, 23, 18, 256, 256);
        }

        //Bottom bar
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.ICONS, x, y + 5 + (size * 18), 0, 90, 23, 5, 256, 256);
    }

    public int[] getAdditionSizeAndPos() {
        int[] size = new int[4];
        size[0] = pos.x() - 130 - xPos;
        size[1] = pos.y() + 16 + 10 - 19;
        size[2] = 23;
        size[3] = 5 + (screen.getWrapper().getTools().getSlots() * 18) + 5;
        return size;
    }
}