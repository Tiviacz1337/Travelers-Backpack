package com.tiviacz.travelersbackpack.inventory.upgrades.stonecutter;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetElement;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.ResultArrowElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

public class StonecutterWidget extends UpgradeWidgetBase<StonecutterUpgrade> {
    private final ResultArrowElement resultArrowElement;
    public final WidgetElement arrowElement = new WidgetElement(new Point(74, 25), new Point(12, 12));
    public final WidgetElement craftArrowElement = new WidgetElement(new Point(35, 25), new Point(8, 12));
    private int startIndex = 0;
    private boolean displayRecipes;
    private float scrollOffs;
    private boolean scrolling;

    public StonecutterWidget(BackpackScreen screen, StonecutterUpgrade upgrade, Point pos) {
        super(screen, upgrade, pos, new Point(0, 0), "screen.travelersbackpack.stonecutter_upgrade");
        this.resultArrowElement = new ResultArrowElement(screen, this, this.arrowElement);
        upgrade.registerUpdateListener(this::containerChanged);
        if(upgrade.hasInputItem()) {
            containerChanged();
        }
    }

    @Override
    public void renderBg(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, x, y, mouseX, mouseY);
        if(isTabOpened()) {
            this.resultArrowElement.renderBg(guiGraphics, x, y, mouseX, mouseY);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.TABS_2, pos.x() + craftArrowElement.pos().x(), pos.y() + craftArrowElement.pos().y(), 72, 105, craftArrowElement.size().x(), craftArrowElement.size().y(), 256, 256);

            int xo = this.pos.x();
            int yo = this.pos.y();
            int sy = (int)(41.0F * this.scrollOffs);
            int spriteX = this.isScrollBarActive() ? 48 : 60;
            int spriteY = 105;
            int scrollerXStart = xo + 74;
            int scrollerYStart = yo + 44;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.TABS_2, scrollerXStart, scrollerYStart + sy, spriteX, spriteY, 12, 15, 256, 256);
            if(mouseX >= scrollerXStart && mouseY >= scrollerYStart && mouseX < scrollerXStart + 12 && mouseY < scrollerYStart + 54) {
                if(this.isScrollBarActive()) {
                    guiGraphics.requestCursor(this.scrolling ? CursorTypes.RESIZE_NS : CursorTypes.POINTING_HAND);
                } else {
                    guiGraphics.requestCursor(CursorTypes.NOT_ALLOWED);
                }
            }

            int endIndex = this.startIndex + 12;
            int x2 = pos.x() + 7;
            int y2 = pos.y() + 43;
            this.extractButtons(guiGraphics, mouseX, mouseY, x2, y2, endIndex);
            this.extractRecipes(guiGraphics, x2, y2, endIndex);
        }
    }

    @Override
    public Identifier getBackgroundTexture() {
        return BackpackScreen.TABS_2;
    }

    private void containerChanged() {
        this.displayRecipes = this.upgrade.hasInputItem();
        this.scrollOffs = 0.0F;
        this.startIndex = 0;
    }

    private boolean isScrollBarActive() {
        return this.displayRecipes && this.getUpgrade().getNumberOfVisibleRecipes() > 12;
    }

    private void extractButtons(GuiGraphicsExtractor graphics, int xm, int ym, int x, int y, int endIndex) {
        for(int index = this.startIndex; index < endIndex && index < this.upgrade.getNumberOfVisibleRecipes(); index++) {
            int posIndex = index - this.startIndex;
            int posX = x + posIndex % 4 * 16;
            int row = posIndex / 4;
            int posY = y + row * 18 + 2;
            int spriteX;
            if(index == this.upgrade.getSelectedRecipeIndex()) {
                spriteX = 16;
            } else if(xm >= posX && ym >= posY && xm < posX + 16 && ym < posY + 18) {
                spriteX = 32;
            } else {
                spriteX = 0;
            }

            int textureY = posY - 1;
            graphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.TABS_2, posX, textureY, spriteX, 105, 16, 18, 256, 256);
            if(xm >= posX && ym >= textureY && xm < posX + 16 && ym < textureY + 18) {
                graphics.requestCursor(CursorTypes.POINTING_HAND);
            }
        }
    }

    private void extractRecipes(GuiGraphicsExtractor graphics, int x, int y, int endIndex) {
        SelectableRecipe.SingleInputSet<StonecutterRecipe> visibleRecipes = this.upgrade.getVisibleRecipes();
        ContextMap context = SlotDisplayContext.fromLevel(this.screen.getScreenPlayer().level());

        for(int index = this.startIndex; index < endIndex && index < visibleRecipes.size(); index++) {
            int posIndex = index - this.startIndex;
            int posX = x + posIndex % 4 * 16;
            int row = posIndex / 4;
            int posY = y + row * 18 + 2;
            SlotDisplay buttonIcon = visibleRecipes.entries().get(index).recipe().optionDisplay();
            graphics.item(buttonIcon.resolveForFirstStack(context), posX, posY);
        }
    }

    @Override
    public void renderTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        if(isTabOpened()) {
            this.resultArrowElement.renderTooltip(guiGraphics, mouseX, mouseY);
            if(this.displayRecipes) {
                int edgeLeft = this.getWidgetSizeAndPos()[0] + 7;
                int edgeTop = this.getWidgetSizeAndPos()[1] + 42;
                int endIndex = this.startIndex + 12;
                SelectableRecipe.SingleInputSet<StonecutterRecipe> visibleRecipes = this.getUpgrade().getVisibleRecipes();

                for(int index = this.startIndex; index < endIndex && index < visibleRecipes.size(); index++) {
                    int posIndex = index - this.startIndex;
                    int itemLeft = edgeLeft + posIndex % 4 * 16;
                    int itemRight = edgeTop + posIndex / 4 * 18 + 2;
                    if(mouseX >= itemLeft && mouseX < itemLeft + 16 && mouseY >= itemRight && mouseY < itemRight + 18) {
                        ContextMap context = SlotDisplayContext.fromLevel(this.screen.getScreenPlayer().level());
                        SlotDisplay buttonIcon = visibleRecipes.entries().get(index).recipe().optionDisplay();
                        guiGraphics.setTooltipForNextFrame(this.screen.getScreenFont(), buttonIcon.resolveForFirstStack(context), mouseX, mouseY);
                    }
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if(isTabOpened()) {
            if(this.resultArrowElement.mouseClicked(event)) {
                return true;
            }
            if(this.displayRecipes) {
                int xo = this.getWidgetSizeAndPos()[0] + 7;
                int yo = this.getWidgetSizeAndPos()[1] + 42;
                int endIndex = this.startIndex + 12;

                for(int index = this.startIndex; index < endIndex; index++) {
                    int posIndex = index - this.startIndex;
                    double xx = event.x() - (xo + posIndex % 4 * 16);
                    double yy = event.y() - (yo + posIndex / 4 * 18);
                    if(xx >= 0.0 && yy >= 0.0 && xx < 16.0 && yy < 18.0 && this.upgrade.clickMenuButton(this.screen.getScreenPlayer(), index)) {
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                        Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.screen.getMenu().containerId, index);
                        return true;
                    }
                }

                xo = this.getWidgetSizeAndPos()[0] + 74;
                yo = this.getWidgetSizeAndPos()[1] + 46;
                if(event.x() >= xo && event.x() < xo + 12 && event.y() >= yo && event.y() < yo + 54) {
                    this.scrolling = true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if(isTabOpened() && this.scrolling && this.isScrollBarActive()) {
            int yscr = this.pos.y() + 46;
            int yscr2 = yscr + 54;
            this.scrollOffs = ((float)event.y() - yscr - 7.5F) / (yscr2 - yscr - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.startIndex = (int)(this.scrollOffs * this.getOffscreenRows() + 0.5) * 4;
            return true;
        } else {
            return super.mouseDragged(event, dx, dy);
        }
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        this.scrolling = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if(super.mouseScrolled(x, y, scrollX, scrollY)) {
            return true;
        }

        if(this.isScrollBarActive()) {
            int offscreenRows = this.getOffscreenRows();
            float scrolledDelta = (float)scrollY / offscreenRows;
            this.scrollOffs = Mth.clamp(this.scrollOffs - scrolledDelta, 0.0F, 1.0F);
            this.startIndex = (int)(this.scrollOffs * offscreenRows + 0.5) * 4;
        }

        return true;
    }

    protected int getOffscreenRows() {
        return (this.upgrade.getNumberOfVisibleRecipes() + 4 - 1) / 4 - 3;
    }
}