package com.tiviacz.travelersbackpack.client.screens.widgets.settings;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.BackpackSettingsScreen;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.network.ServerboundSpecialActionPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class VisibilityWidget extends SettingsWidgetBase {
    private final Point iconUv = new Point(78, 36);
    private final Point iconEnabledUv = new Point(60, 36);

    public VisibilityWidget(BackpackSettingsScreen screen, Point pos) {
        super(screen, pos, new Point(24, 24));
    }

    public void sendDataToServer() {
        screen.visibility = !screen.visibility;
        PacketDistributor.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.TOGGLE_VISIBILITY, 0));
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        Point uv = screen.visibility ? iconEnabledUv : iconUv;
        guiGraphics.blit(BackpackScreen.ICONS, pos.x(), pos.y(), emptyTabUv.x(), emptyTabUv.y(), width, height); //Empty Tab
        guiGraphics.blit(BackpackScreen.ICONS, pos.x() + 3, pos.y() + 3, uv.x(), uv.y(), iconSize.x(), iconSize.y()); //Icon
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(isMouseOverIcon(mouseX, mouseY)) {
            guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.toggle_visibility"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(isMouseOverIcon(pMouseX, pMouseY)) {
            //Send data to server if changed
            sendDataToServer();
            this.screen.playUIClickSound();
            return true;
        }
        return false;
    }
}
