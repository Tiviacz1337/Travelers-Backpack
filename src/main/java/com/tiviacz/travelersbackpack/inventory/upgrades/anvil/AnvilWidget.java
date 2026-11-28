package com.tiviacz.travelersbackpack.inventory.upgrades.anvil;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetElement;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class AnvilWidget extends UpgradeWidgetBase<AnvilUpgrade> {
    private static final Component TOO_EXPENSIVE_TEXT = Component.translatable("container.repair.expensive");
    private static final int GREEN_TEXT_COLOR = 0xFF80FF20;
    private static final int RED_TEXT_COLOR = 0xFFFF6060;
    private static final int COST_BACKGROUND_COLOR = 0x4F000000;
    private final WidgetElement nameField = new WidgetElement(new Point(6, 22), new Point(96, 16));
    private final WidgetElement errorIcon = new WidgetElement(new Point(63, 42), new Point(21, 17));
    private final WidgetElement costBox = new WidgetElement(new Point(6, 62), new Point(96, 24));
    private final EditBox nameBox;
    private ItemStack lastInput;
    private boolean focused = false;

    public AnvilWidget(BackpackScreen screen, AnvilUpgrade upgrade, Point pos) {
        super(screen, upgrade, pos, new Point(93, 0), "screen.travelersbackpack.anvil_upgrade");
        this.nameBox = new EditBox(screen.getScreenFont(), pos.x() + nameField.pos().x() + 3, pos.y() + nameField.pos().y() + 4, nameField.size().x() - 7, 12, Component.translatable("container.repair"));
        this.nameBox.setCanLoseFocus(true);
        this.nameBox.setTextColor(-1);
        this.nameBox.setTextColorUneditable(-1);
        this.nameBox.setInvertHighlightedTextColor(false);
        this.nameBox.setBordered(false);
        this.nameBox.setMaxLength(AnvilMenu.MAX_NAME_LENGTH);

        this.lastInput = upgrade.getInputStack();
        if(upgrade.getItemName().isEmpty()) {
            this.nameBox.setValue(this.lastInput.isEmpty() ? "" : this.lastInput.getHoverName().getString());
        } else {
            this.nameBox.setValue(upgrade.getItemName());
        }
        this.nameBox.setEditable(!this.lastInput.isEmpty());
        this.nameBox.setResponder(this::onNameChanged);
    }

    @Override
    public Identifier getBackgroundTexture() {
        return BackpackScreen.TABS_2;
    }

    @Override
    public void renderBg(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY) {
        updateInput();
        super.renderBg(guiGraphics, x, y, mouseX, mouseY);

        if(isTabOpened()) {
            boolean hasInput = !this.upgrade.getInputStack().isEmpty();
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.TABS_2, pos.x() + nameField.pos().x(), pos.y() + nameField.pos().y(), 93, hasInput ? 92 : 108, nameField.size().x(), nameField.size().y(), 256, 256);

            if((hasInput || !this.upgrade.getAdditionStack().isEmpty()) && this.upgrade.getResult().isEmpty()) {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.TABS_2, pos.x() + errorIcon.pos().x(), pos.y() + errorIcon.pos().y(), 189, 92, errorIcon.size().x(), errorIcon.size().y(), 256, 256);
            }

            this.nameBox.setX(pos.x() + nameField.pos().x() + 3);
            this.nameBox.setY(pos.y() + nameField.pos().y() + 4);
            this.nameBox.extractRenderState(guiGraphics, mouseX, mouseY, 0.0F);

            renderCost(guiGraphics);
        }
    }

    public void renderCost(GuiGraphicsExtractor guiGraphics) {
        int cost = this.upgrade.getCost();
        if(cost <= 0) {
            return;
        }

        Player player = screen.getScreenPlayer();
        Component text = null;
        int color = GREEN_TEXT_COLOR;

        if(cost >= AnvilUpgrade.TOO_EXPENSIVE_COST && !player.hasInfiniteMaterials()) {
            text = TOO_EXPENSIVE_TEXT;
            color = RED_TEXT_COLOR;
        } else if(!this.upgrade.getResult().isEmpty()) {
            text = Component.translatable("container.repair.cost", cost);
            if(!this.upgrade.mayPickup(player)) {
                color = RED_TEXT_COLOR;
            }
        }

        if(text == null) {
            return;
        }

        Font font = screen.getScreenFont();
        List<FormattedCharSequence> lines = font.split(text, costBox.size().x() - 4);
        int lineHeight = font.lineHeight + 1;
        int textHeight = (lines.size() - 1) * lineHeight + 8;
        int textWidth = 0;
        for(FormattedCharSequence line : lines) {
            textWidth = Math.max(textWidth, font.width(line));
        }

        int textY = pos.y() + costBox.pos().y() + (costBox.size().y() - textHeight) / 2;
        int backgroundX = pos.x() + costBox.pos().x() + (costBox.size().x() - textWidth) / 2 - 3;
        guiGraphics.fill(backgroundX, textY - 3, backgroundX + textWidth + 6, textY + textHeight + 3, COST_BACKGROUND_COLOR);

        for(FormattedCharSequence line : lines) {
            int textX = pos.x() + costBox.pos().x() + (costBox.size().x() - font.width(line)) / 2;
            guiGraphics.text(font, line, textX, textY, color);
            textY += lineHeight;
        }
    }

    public void onNameChanged(String name) {
        ItemStack input = this.upgrade.getInputStack();
        if(input.isEmpty()) {
            return;
        }

        if(!input.has(DataComponents.CUSTOM_NAME) && name.equals(input.getHoverName().getString())) {
            name = "";
        }

        if(this.upgrade.setItemName(name)) {
            ServerboundActionTagPacket.create(ServerboundActionTagPacket.ANVIL_NAME, this.upgrade.getDataHolderSlot(), name);
        }
    }

    public void updateInput() {
        ItemStack input = this.upgrade.getInputStack();
        if(ItemStack.matches(input, this.lastInput)) {
            return;
        }

        this.lastInput = input;
        this.nameBox.setValue(input.isEmpty() ? "" : input.getHoverName().getString());
        this.nameBox.setEditable(!input.isEmpty());
        if(input.isEmpty()) {
            setFocused(false);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if(isTabOpened() && this.nameBox.isMouseOver(event.x(), event.y())) {
            setFocused(true);
            this.screen.setFocused(this);
            this.nameBox.mouseClicked(event, doubleClick);
            return true;
        }
        setFocused(false);
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if(isTabOpened() && this.focused && this.nameBox.mouseDragged(event, dx, dy)) {
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if(isTabOpened() && this.focused) {
            return this.nameBox.keyPressed(event) || this.nameBox.canConsumeInput();
        }
        return false;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if(isTabOpened() && this.focused) {
            return this.nameBox.charTyped(event);
        }
        return false;
    }

    @Override
    public boolean preeditUpdated(PreeditEvent event) {
        if(isTabOpened() && this.focused) {
            return this.nameBox.preeditUpdated(event);
        }
        return false;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
        this.nameBox.setFocused(focused);
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }
}