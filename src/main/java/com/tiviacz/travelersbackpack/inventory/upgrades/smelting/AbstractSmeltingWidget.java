package com.tiviacz.travelersbackpack.inventory.upgrades.smelting;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetElement;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.ResultArrowElement;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;

public class AbstractSmeltingWidget<T> extends UpgradeWidgetBase<AbstractSmeltingUpgrade<T>> {
    public final ResultArrowElement resultArrowElement;
    public final WidgetElement arrowElement = new WidgetElement(new Point(45, 61), new Point(12, 12));

    public AbstractSmeltingWidget(BackpackScreen screen, AbstractSmeltingUpgrade<T> upgrade, Point pos, String name) {
        super(screen, upgrade, pos, new Point(0, 118), name);
        this.resultArrowElement = new ResultArrowElement(screen, this, this.arrowElement);
    }

    @Override
    public void renderBg(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, x, y, mouseX, mouseY);
        this.resultArrowElement.renderBg(guiGraphics, x, y, mouseX, mouseY);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);

        if(isTabOpened() && screen.getWrapper().getScreenID() != Reference.ITEM_SCREEN_ID) {
            long gameTime = upgrade.getUpgradeManager().getWrapper().getLevel().getGameTime();

            if(upgrade.isBurning()) {
                long burnTimeFinish = upgrade.getBurnFinishTime();
                long progress = burnTimeFinish - gameTime;
                float p = (float)progress / upgrade.getBurnTotalTime();
                int k = Math.clamp((int)Math.ceil(13 * p), 0, 13);
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.TABS, pos.x() + 7, pos.y() + 55 - k, 0, 213 - k, 14, k + 1, 256, 256);
            }

            if(upgrade.isCooking()) {
                long cookTimeFinish = upgrade.getCookingFinishTime();
                long cookProgress = cookTimeFinish - gameTime;
                float cp = 1.0f - (float)cookProgress / upgrade.getCookingTotalTime();
                int l = (int)Math.ceil(10 * cp);
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.TABS, pos.x() + 28, pos.y() + 42, 14, 200, l, 13, 256, 256);
            }
        }
    }

    @Override
    public void renderTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        this.resultArrowElement.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if(this.resultArrowElement.mouseClicked(event)) {
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }
}