package com.tiviacz.travelersbackpack.client.screens.widgets.settings;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.BackpackSettingsScreen;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.network.SupporterBadgePacket;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SupporterBadgeWidget extends SettingsWidgetBase {
    private final Point iconUv = new Point(132, 72);
    private final Point iconEnabledUv = new Point(132, 54);

    public SupporterBadgeWidget(BackpackSettingsScreen screen, Point pos) {
        super(screen, pos, new Point(24, 24));
    }

    public void sendDataToServer() {
        boolean showSupporterStar = TravelersBackpackConfig.CLIENT.showSupporterBadge.get();
        TravelersBackpackConfig.CLIENT.showSupporterBadge.set(!showSupporterStar);
        TravelersBackpackConfig.CLIENT.showSupporterBadge.save();
        ClientPacketDistributor.sendToServer(new SupporterBadgePacket.Serverbound(!showSupporterStar));
    }

    @Override
    public void renderBg(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY) {
        Point uv = TravelersBackpackConfig.CLIENT.showSupporterBadge.get() ? iconEnabledUv : iconUv;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.ICONS, pos.x(), pos.y(), emptyTabUv.x(), emptyTabUv.y(), width, height, 256, 256); //Empty Tab
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.ICONS, pos.x() + 3, pos.y() + 3, uv.x(), uv.y(), iconSize.x(), iconSize.y(), 256, 256); //Icon
    }

    @Override
    public void renderTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if(isMouseOverIcon(mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("screen.travelersbackpack.toggle_supporter_badge_visibility"));
            tooltip.add(Component.literal("Thanks for your support! :) - Tiviacz1337"));
            guiGraphics.setTooltipForNextFrame(screen.getFont(), tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean b1) {
        if(isMouseOverIcon(event)) {
            //Send data to server if changed
            sendDataToServer();
            this.screen.playUIClickSound();
            return true;
        }
        return false;
    }
}