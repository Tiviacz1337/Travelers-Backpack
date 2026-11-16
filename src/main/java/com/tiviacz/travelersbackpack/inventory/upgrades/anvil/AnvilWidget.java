package com.tiviacz.travelersbackpack.inventory.upgrades.anvil;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
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
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class AnvilWidget extends UpgradeWidgetBase<AnvilUpgrade> {
    private static final Component TOO_EXPENSIVE_TEXT = Component.translatable("container.repair.expensive");
    private static final int COST_COLOR = 0xFF80FF20;
    private static final int ERROR_COLOR = 0xFFFF6060;

    private static final int TEXT_FIELD_X = 6, TEXT_FIELD_Y = 22, TEXT_FIELD_U = 93, TEXT_FIELD_V = 92, TEXT_FIELD_DISABLED_V = 108, TEXT_FIELD_WIDTH = 96, TEXT_FIELD_HEIGHT = 16;
    private static final int ERROR_X = 63, ERROR_Y = 42, ERROR_U = 189, ERROR_V = 92, ERROR_WIDTH = 21, ERROR_HEIGHT = 17;
    private static final int COST_BOX_X = 6, COST_BOX_Y = 62, COST_BOX_WIDTH = 96, COST_BOX_HEIGHT = 24;
    private static final int COST_BACKGROUND_COLOR = 0x4F000000;

    private final EditBox nameBox;
    private ItemStack lastInput;
    private boolean focused;

    public AnvilWidget(BackpackScreen screen, AnvilUpgrade upgrade, Point pos) {
        super(screen, upgrade, pos, new Point(93, 0), "screen.travelersbackpack.anvil_upgrade");
        this.nameBox = new EditBox(screen.getScreenFont(), pos.x() + TEXT_FIELD_X + 3, pos.y() + TEXT_FIELD_Y + 4, TEXT_FIELD_WIDTH - 7, 12, Component.translatable("container.repair"));
        this.nameBox.setCanLoseFocus(true);
        this.nameBox.setTextColor(-1);
        this.nameBox.setTextColorUneditable(-1);
        this.nameBox.setInvertHighlightedTextColor(false);
        this.nameBox.setBordered(false);
        this.nameBox.setMaxLength(AnvilUpgrade.MAX_NAME_LENGTH);

        this.lastInput = upgrade.getInputStack();
        if(!upgrade.getItemName().isEmpty()) {
            this.nameBox.setValue(upgrade.getItemName());
        } else {
            this.nameBox.setValue(this.lastInput.isEmpty() ? "" : this.lastInput.getHoverName().getString());
        }
        this.nameBox.setEditable(!this.lastInput.isEmpty());
        this.nameBox.setResponder(this::onNameChanged);
    }

    private void onNameChanged(String name) {
        ItemStack input = this.upgrade.getInputStack();
        if(input.isEmpty()) {
            return;
        }
        String newName = name;
        if(!input.has(DataComponents.CUSTOM_NAME) && newName.equals(input.getHoverName().getString())) {
            newName = "";
        }
        if(this.upgrade.setItemName(newName)) {
            ServerboundActionTagPacket.create(ServerboundActionTagPacket.ANVIL_NAME, this.upgrade.getDataHolderSlot(), newName);
        }
    }

    private void updateInput() {
        ItemStack input = this.upgrade.getInputStack();
        if(!ItemStack.matches(input, this.lastInput)) {
            this.lastInput = input;
            this.nameBox.setValue(input.isEmpty() ? "" : input.getHoverName().getString());
            this.nameBox.setEditable(!input.isEmpty());
            if(input.isEmpty()) {
                setFocused(false);
            }
        }
    }

    @Override
    public Identifier getBackgroundTexture() {
        return BackpackScreen.TABS_2;
    }

    @Override
    public void renderBg(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY) {
        updateInput();
        super.renderBg(guiGraphics, x, y, mouseX, mouseY);
        if(!isTabOpened()) {
            return;
        }

        boolean hasInput = !this.upgrade.getInputStack().isEmpty();

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.TABS_2, pos.x() + TEXT_FIELD_X, pos.y() + TEXT_FIELD_Y, TEXT_FIELD_U, hasInput ? TEXT_FIELD_V : TEXT_FIELD_DISABLED_V, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT, 256, 256);

        if((hasInput || !this.upgrade.getAdditionStack().isEmpty()) && this.upgrade.getResult().isEmpty()) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.TABS_2, pos.x() + ERROR_X, pos.y() + ERROR_Y, ERROR_U, ERROR_V, ERROR_WIDTH, ERROR_HEIGHT, 256, 256);
        }

        this.nameBox.setX(pos.x() + TEXT_FIELD_X + 3);
        this.nameBox.setY(pos.y() + TEXT_FIELD_Y + 4);
        this.nameBox.extractRenderState(guiGraphics, mouseX, mouseY, 0.0F);

        renderCost(guiGraphics);
    }

    private void renderCost(GuiGraphicsExtractor guiGraphics) {
        int cost = this.upgrade.getCost();
        if(cost <= 0) {
            return;
        }
        Player player = this.screen.getScreenPlayer();
        int color = COST_COLOR;
        Component line;
        if(cost >= AnvilUpgrade.TOO_EXPENSIVE_COST && !player.hasInfiniteMaterials()) {
            line = TOO_EXPENSIVE_TEXT;
            color = ERROR_COLOR;
        } else if(this.upgrade.getResult().isEmpty()) {
            line = null;
        } else {
            line = Component.translatable("container.repair.cost", cost);
            if(!this.upgrade.mayPickup(player)) {
                color = ERROR_COLOR;
            }
        }

        if(line != null) {
            Font font = this.screen.getScreenFont();
            List<FormattedCharSequence> lines = font.split(line, COST_BOX_WIDTH - 4);
            int lineHeight = font.lineHeight + 1;
            int visibleHeight = (lines.size() - 1) * lineHeight + 8;
            int textY = pos.y() + COST_BOX_Y + (COST_BOX_HEIGHT - visibleHeight) / 2;

            int textWidth = 0;
            for(FormattedCharSequence text : lines) {
                textWidth = Math.max(textWidth, font.width(text));
            }
            int bgLeft = pos.x() + COST_BOX_X + (COST_BOX_WIDTH - textWidth) / 2 - 3;
            guiGraphics.fill(bgLeft, textY - 3, bgLeft + textWidth + 6, textY + visibleHeight + 3, COST_BACKGROUND_COLOR);

            for(FormattedCharSequence text : lines) {
                int textX = pos.x() + COST_BOX_X + (COST_BOX_WIDTH - font.width(text)) / 2;
                guiGraphics.text(font, text, textX, textY, color);
                textY += lineHeight;
            }
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
        if(!isTabOpened() || !this.focused) {
            return false;
        }
        return this.nameBox.keyPressed(event) || this.nameBox.canConsumeInput();
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        return isTabOpened() && this.focused && this.nameBox.charTyped(event);
    }

    @Override
    public boolean preeditUpdated(PreeditEvent event) {
        return isTabOpened() && this.focused && this.nameBox.preeditUpdated(event);
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