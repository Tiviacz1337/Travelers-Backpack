package com.tiviacz.travelersbackpack.inventory.upgrades.voiding;

import com.mojang.math.Axis;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.BackpackSettingsScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.FilterUpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetElement;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.menu.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.ButtonStates;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.FilterButton;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TankWidget;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class VoidWidget extends FilterUpgradeWidgetBase<VoidWidget, VoidUpgrade> {
    private static final Identifier TRASH_UPGRADE = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/item/void_upgrade.png");
    private static final int RED_HIGHLIGHT_COLOR = 2143884822;
    private static final int RED_ARROW_COLOR = 0xBFAA0000;
    private static final int HIGHLIGHT_TANK_COLOR = -2130706433;
    private final WidgetElement trashBinSlot = new WidgetElement(new Point(25, 4), new Point(16, 16));
    public final List<Integer> selectedSlots = new ArrayList<>();
    public final List<Integer> selectedTanks = new ArrayList<>();

    private boolean tickAnimation = false;
    private float progress = 0.0F;
    private boolean hoveringTrashBin = false;

    public VoidWidget(BackpackScreen screen, VoidUpgrade upgrade, Point pos) {
        super(screen, upgrade, pos, new Point(137, 0), "screen.travelersbackpack.void_upgrade");

        FilterButton<VoidWidget> whitelistButton = new FilterButton<>(this, upgrade.getFilter().get(VoidFilterSettings.ALLOW_MODE), ButtonStates.ALLOW, new Point(pos.x() + 6, pos.y() + 22));
        FilterButton<VoidWidget> objectButton = new FilterButton<>(this, upgrade.getFilter().get(VoidFilterSettings.OBJECT_CATEGORY), ButtonStates.OBJECT_TYPE, new Point(pos.x() + 6 + 18, pos.y() + 22));
        FilterButton<VoidWidget> ignoreModeButton = new FilterButton<>(this, upgrade.getFilter().get(VoidFilterSettings.IGNORE_MODE), ButtonStates.IGNORE_MODE, new Point(pos.x() + 6 + 36, pos.y() + 22));

        this.addFilterButton(whitelistButton);
        this.addFilterButton(objectButton);
        this.addFilterButton(ignoreModeButton);
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, x, y, mouseX, mouseY);
        this.renderMatchContentsSlotOverlay(guiGraphics, upgrade.getFilter(), VoidFilterSettings.ALLOW_MODE, VoidFilterSettings.MATCH_CONTENTS, TravelersBackpackConfig.SERVER.backpackUpgrades.voidUpgradeSettings.filterSlotCount.get());

        if(isTabOpened()) {
            if(isHoveringWithTrashBin()) {
                renderTrashBin(guiGraphics, pos.x() + 31, pos.y() + 9, 0x80FFFFFF, (this.selectedSlots.isEmpty() && this.selectedTanks.isEmpty()) ? 0.0F : 3.0F);
                if(!this.selectedSlots.isEmpty() || !this.selectedTanks.isEmpty()) {
                    //guiGraphics.setColor(1.0F, 1.0F, 1.0F, 0.75F);
                    renderRedDownArrow(guiGraphics, pos.x() + 31 + 1, pos.y() + 5);
                }
            } else {
                renderTrashBinAnimation(guiGraphics, pos.x() + 31, pos.y() + 9, 0xFFFFFFFF);
            }
        }
    }

    @Override
    public void renderUnderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.renderUnderTooltip(guiGraphics, mouseX, mouseY, partialTicks);

        //Highlight slot under mouse
        Slot slot = screen.getSlotUnderMouse();
        if(isHoveringWithTrashBin() && slot != null && canSelectSlot(slot) && !this.selectedSlots.contains(slot.index)) {
            drawRedHighlight(guiGraphics, slot);
        }

        //Highlight selected slots
        this.selectedSlots.forEach(index -> drawRedHighlight(guiGraphics, screen.getMenu().getSlot(index)));
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if(isTabOpened()) {
            if(isWithinTrashBinSlot(mouseX, mouseY)) {
                if(isHoveringWithTrashBin() && (!this.selectedSlots.isEmpty() || !this.selectedTanks.isEmpty())) {
                    guiGraphics.setTooltipForNextFrame(screen.getFont(), Component.translatable("screen.travelersbackpack.void_upgrade_trash_bin_drop").withStyle(ChatFormatting.RED), mouseX, mouseY);
                }
                if(this.selectedSlots.isEmpty() && this.selectedTanks.isEmpty()) {
                    List<Component> components = new ArrayList<>(TextUtils.getTranslatedSplittedText("screen.travelersbackpack.void_upgrade_trash_bin", null));
                    if(screen.getWrapper().getUpgradeManager().getUpgrade(TanksUpgrade.class).isEmpty()) {
                        components.remove(2);
                    }
                    guiGraphics.setComponentTooltipForNextFrame(screen.getFont(), components, mouseX, mouseY);
                }
            }
            if(getFilterButton(ButtonStates.ALLOW).isMouseOver(mouseX, mouseY)) {
                guiGraphics.setTooltipForNextFrame(screen.getFont(), WHITELIST_TOOLTIPS.get(getFilterButton(ButtonStates.ALLOW).getCurrentState()), mouseX, mouseY);
            }
            if(getFilterButton(ButtonStates.OBJECT_TYPE).isMouseOver(mouseX, mouseY)) {
                guiGraphics.setTooltipForNextFrame(screen.getFont(), OBJECT_TOOLTIPS.get(getFilterButton(ButtonStates.OBJECT_TYPE).getCurrentState()), mouseX, mouseY);
            }
            if(getFilterButton(ButtonStates.IGNORE_MODE).isMouseOver(mouseX, mouseY)) {
                guiGraphics.setTooltipForNextFrame(screen.getFont(), IGNORE_MODE_TOOLTIPS.get(getFilterButton(ButtonStates.IGNORE_MODE).getCurrentState()), mouseX, mouseY);
            }
        }
    }

    @Override
    public void renderOnTop(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.renderOnTop(guiGraphics, mouseX, mouseY, partialTicks);
        if(isTabOpened()) {
            this.tickAnimation = isHoveringWithTrashBin() || isWithinTrashBinSlot(mouseX, mouseY);
            if(isHoveringWithTrashBin()) {
                renderTrashBinAnimation(guiGraphics, mouseX - 1, mouseY - 1, 0xFFFFFFFF);
            }
        }
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        if(isHoveringWithTrashBin()) {
            return true;
        }
        return super.isMouseOver(pMouseX, pMouseY);
    }

    public boolean isWithinTrashBinSlot(double mouseX, double mouseY) {
        return isWithinBounds(mouseX, mouseY, this.trashBinSlot);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        //Selecting slots to void
        if(select(event)) {
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if(isTabOpened()) {
            if(isWithinTrashBinSlot(event.x(), event.y()) && screen.getWrapper().isOwner(screen.getScreenPlayer()) && screen.getMenu().getCarried().isEmpty()) {
                this.hoveringTrashBin = !this.hoveringTrashBin;
                if(!isHoveringWithTrashBin()) {
                    this.selectedSlots.forEach(index -> ServerboundActionTagPacket.create(ServerboundActionTagPacket.SET_STACK, ServerActions.SLOT, ItemStack.EMPTY, index));
                    this.selectedSlots.clear();
                    this.selectedTanks.forEach(tank -> ServerboundActionTagPacket.create(ServerboundActionTagPacket.SET_STACK, ServerActions.TANK, ItemStack.EMPTY, tank));
                    this.selectedTanks.clear();
                }
            } else {
                if(select(event)) {
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    public boolean isHoveringWithTrashBin() {
        return this.hoveringTrashBin;
    }

    public boolean select(MouseButtonEvent event) {
        if(isHoveringWithTrashBin()) {
            if(selectSlots(screen.getSlotUnderMouse(), event.button())) {
                return true;
            }
            if(screen.mappedWidgets.get(TanksUpgrade.class) instanceof TankWidget tankWidget) {
                if(selectTank(tankWidget, tankWidget.getUpgrade().getLeftTank().isEmpty(), tankWidget.leftTankElement, event, 0)) {
                    return true;
                }
                if(selectTank(tankWidget, tankWidget.getUpgrade().getRightTank().isEmpty(), tankWidget.rightTankElement, event, 1)) {
                    return true;
                }
            }
            if(!isWithinTrashBinSlot(event.x(), event.y())) {
                return true;
            }
        }
        return false;
    }

    public boolean selectTank(TankWidget tankWidget, boolean isEmpty, WidgetElement tankElement, MouseButtonEvent event, int tank) {
        if(tankWidget.inTank(tankElement, event.x(), event.y()) && !isEmpty) {
            if(event.button() == 0 && !this.selectedTanks.contains(tank)) {
                this.selectedTanks.add(tank);
                return true;
            }

            if(event.button() == 1 && this.selectedTanks.contains(tank)) {
                this.selectedTanks.remove((Object)tank);
                return true;
            }
        }
        return false;
    }

    public boolean canSelectSlot(Slot slot) {
        return (slot.index >= 0 && slot.index < screen.getWrapper().getStorage().getSlots()) || slot.container instanceof Inventory;
    }

    public boolean selectSlots(Slot slot, int button) {
        if(slot != null) {
            if(slot instanceof DisabledSlot || !slot.hasItem()) {
                return false;
            }
            if(canSelectSlot(slot)) {
                if(BackpackSettingsScreen.selectSlotsIndex(this.selectedSlots, this.hoveringTrashBin, slot, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void drawRedTankHighlight(GuiGraphics guiGraphics, int x, int y, boolean inTank, int height, int tankIndex) {
        //Render red highlight if hovering with trash bin
        if(isHoveringWithTrashBin()) {
            boolean flag = inTank;
            if(flag || selectedTanks.contains(tankIndex)) {
                if(flag) {
                    guiGraphics.fillGradient(x, y, x + 16, y + height, HIGHLIGHT_TANK_COLOR, HIGHLIGHT_TANK_COLOR);
                }
                guiGraphics.fill(x, y, x + 16, y + height, RED_HIGHLIGHT_COLOR);
            }
        }
    }

    public void drawRedHighlight(GuiGraphics guiGraphics, Slot slot) {
        guiGraphics.fill(screen.getGuiLeft() + slot.x, screen.getGuiTop() + slot.y, screen.getGuiLeft() + slot.x + 16, screen.getGuiTop() + slot.y + 16, RED_HIGHLIGHT_COLOR);
    }

    public void renderRedDownArrow(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x, y + 2, x + 2, y + 5, RED_ARROW_COLOR);
        guiGraphics.fill(x - 2, y + 5, x + 4, y + 6, RED_ARROW_COLOR);
        guiGraphics.fill(x - 1, y + 6, x + 3, y + 7, RED_ARROW_COLOR);
        guiGraphics.fill(x, y + 7, x + 2, y + 8, RED_ARROW_COLOR);
    }

    public void renderTrashBinAnimation(GuiGraphics guiGraphics, int x, int y, int color) {
        this.tickAnimation();
        float time = (float)(System.currentTimeMillis() % 2000) / 1000.0F;
        float f = (float) (Math.sin(time * Math.PI) * 1.0F + 1.0F);
        guiGraphics.pose().pushMatrix();
        if(isHoveringWithTrashBin()) {
            guiGraphics.pose().rotateAbout(Axis.ZP.rotationDegrees(-12.5F + (f * 12.5F)).angle(), x, y);
        }
        renderTrashBin(guiGraphics, x, y, color, progress);
        guiGraphics.pose().popMatrix();
    }

    public void renderTrashBin(GuiGraphics guiGraphics, int x, int y, int color, float progress) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().rotateAbout(Axis.ZP.rotationDegrees(progress * 7.5F).angle(), x, y);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TRASH_UPGRADE, x, y - (int)(1 * progress), 6, 4, 4, 1, 16, 16, color); //Top
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TRASH_UPGRADE, x - 2, y + 1 - (int)(1 * progress), 4, 5, 8, 1, 16, 16, color); //Middle
        guiGraphics.pose().popMatrix();
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TRASH_UPGRADE, x - 2, y + 2, 4, 6, 8, 1, 16, 16, color); //Middle
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TRASH_UPGRADE, x - 1, y + 3, 5, 7, 6, 5, 16, 16, color); //Bottom
    }

    public void tickAnimation() {
        if(tickAnimation) {
            if(progress < 3.0F) {
                progress += 0.2F;
            } else {
                progress = 3.0F;
            }
        } else {
            if(progress > 0.0F) {
                progress -= 0.2F;
            } else {
                progress = 0.0F;
            }
        }
    }

    private static final List<Component> WHITELIST_TOOLTIPS = List.of(
            Component.translatable("screen.travelersbackpack.filter_allow_voiding"),
            Component.translatable("screen.travelersbackpack.filter_block_voiding"),
            Component.translatable("screen.travelersbackpack.filter_match_contents_voiding"));

    private static final List<Component> OBJECT_TOOLTIPS = List.of(
            Component.translatable("screen.travelersbackpack.filter_item"),
            Component.translatable("screen.travelersbackpack.filter_modid"),
            Component.translatable("screen.travelersbackpack.filter_tag"));

    private static final List<Component> IGNORE_MODE_TOOLTIPS = List.of(
            Component.translatable("screen.travelersbackpack.filter_match_components"),
            Component.translatable("screen.travelersbackpack.filter_ignore_components"));
}