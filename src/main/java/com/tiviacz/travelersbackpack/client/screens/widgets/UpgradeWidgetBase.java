package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.IFilter;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.inventory.upgrades.IEnable;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.items.upgrades.UpgradeItem;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UpgradeWidgetBase<U extends UpgradeBase> extends WidgetBase<BackpackScreen> {
    private final WidgetElement removeElement;
    private final WidgetElement enableElement;
    protected U upgrade;
    protected int dataHolderSlot;
    private final Point tabUv;
    private final String upgradeIconTooltip;

    public UpgradeWidgetBase(BackpackScreen screen, U upgrade, Point pos, Point tabUv, String upgradeIconTooltip) {
        super(screen, pos, 24, 24);
        this.upgrade = upgrade;
        this.dataHolderSlot = upgrade.getDataHolderSlot();
        this.tabUv = tabUv;
        this.upgradeIconTooltip = upgradeIconTooltip;

        this.width = upgrade.getTabSize().x();
        this.height = upgrade.getTabSize().y();

        this.removeElement = new WidgetElement(new Point(this.upgrade.getTabSize().x() - 3 - 18, 3), new Point(18, 18));
        this.enableElement = new WidgetElement(new Point(this.upgrade.getTabSize().x(), 6), new Point(4, 13));
    }

    public U getUpgrade() {
        return this.upgrade;
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if(isTabOpened()) {
            if(upgrade instanceof IFilter filter) {
                int slotCount = filter.getFilterSlotCount();
                int rowCount = (int)Math.ceil((double)slotCount / 3);
                //Upper
                guiGraphics.blit(BackpackScreen.TABS, pos.x(), pos.y(), tabUv.x(), tabUv.y(), width, 43);
                //Lower
                guiGraphics.blit(BackpackScreen.TABS, pos.x(), pos.y() + 43, tabUv.x(), tabUv.y() + 43 + (3 - rowCount) * 18, width, height - 43);
                for(int i = 0; i < 3; i++) {
                    for(int j = 0; j < 3; j++) {
                        if(j + i * 3 < slotCount) {
                            guiGraphics.blit(BackpackScreen.TABS, pos.x() + 6 + j * 18, pos.y() + 43 + i * 18, 233, 0, 18, 18);
                        }
                    }
                }
            } else {
                guiGraphics.blit(BackpackScreen.TABS, pos.x(), pos.y(), tabUv.x(), tabUv.y(), width, height);
            }
            guiGraphics.renderItem(screen.getWrapper().getUpgrades().getStackInSlot(this.dataHolderSlot), pos.x() + 4, pos.y() + 4);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderEnableButton(guiGraphics, mouseX, mouseY, partialTicks);

        if(isBackpackOwner()) {
            renderRemoveButton(guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(isMouseOverIcon(mouseX, mouseY)) {
            List<Component> tooltips = new ArrayList<>();
            tooltips.add(Component.translatable(this.upgradeIconTooltip));
            if(getUpgrade().getUpgradeManager().getWrapper().getScreenID() != Reference.WEARABLE_SCREEN_ID && this.upgrade.getDataHolderStack().getItem() instanceof UpgradeItem upgradeItem && upgradeItem.requiresEquippedBackpack()) {
                tooltips.add(Component.translatable("screen.travelersbackpack.equip_to_use"));
            }
            guiGraphics.renderTooltip(screen.getFont(), tooltips, Optional.empty(), mouseX, mouseY);
        }

        renderEnableButtonTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(enableButtonMouseClicked(pMouseX, pMouseY, pButton)) {
            return true;
        }
        if(removeButtonMouseClicked(pMouseX, pMouseY, pButton)) {
            return true;
        }
        if(isMouseOverIcon(pMouseX, pMouseY)) {
            if(this.upgrade.isTabOpened()) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.UPGRADE_TAB, this.dataHolderSlot, false, ServerActions.TAB_OPEN);
            } else {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.UPGRADE_TAB, this.dataHolderSlot, true, ServerActions.TAB_OPEN);
            }
            this.screen.playUIClickSound();
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return pMouseX > pos.x() + 3 && pMouseY > pos.y() && pMouseX < pos.x() + upgrade.getTabSize().x() && pMouseY < pos.y() + upgrade.getTabSize().y();
    }

    public boolean isMouseOverRemoveButton(double mouseX, double mouseY) {
        return isWithinBounds(mouseX, mouseY, this.removeElement);
    }

    public boolean isMouseOverEnableButton(double mouseX, double mouseY) {
        return isWithinBounds(mouseX, mouseY, this.enableElement);
    }

    public void renderMatchContentsSlotOverlay(GuiGraphics guiGraphics, List<Integer> filter, int settingType, int settingValue, int activeSlots) {
        if(isTabOpened()) {
            if(filter.get(settingType) == settingValue) {
                for(int i = 0; i < 3; i++) {
                    for(int j = 0; j < 3; j++) {
                        if(j + i * 3 < activeSlots) {
                            guiGraphics.blit(BackpackScreen.ICONS, pos.x() + 6 + 18 * j, pos.y() + 43 + 18 * i, 24, 36, 18, 18);
                        }
                    }
                }
            }
        }
    }

    public void renderRemoveButton(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if(isTabOpened()) {
            guiGraphics.blit(BackpackScreen.ICONS, pos.x() + this.removeElement.pos().x(), pos.y() + this.removeElement.pos().y(), 42, 36, this.removeElement.size().x(), this.removeElement.size().y());
        }
    }

    public boolean removeButtonMouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(this.upgrade.isTabOpened()) {
            if(isMouseOverRemoveButton(pMouseX, pMouseY)) {
                if(!isBackpackOwner()) {
                    return false;
                }
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.REMOVE_UPGRADE, this.dataHolderSlot);
                this.screen.playUIClickSound();
                return true;
            }
        }
        return false;
    }

    public void renderEnableButton(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(this.upgrade instanceof IEnable e && !this.upgrade.isTabOpened()) {
            if(e.isEnabled(this.upgrade)) {
                guiGraphics.blit(BackpackScreen.ICONS, pos.x() + this.enableElement.pos().x(), pos.y() + this.enableElement.pos().y(), 18, 24, this.enableElement.size().x(), this.enableElement.size().y());
                if(isMouseOverEnableButton(mouseX, mouseY)) {
                    guiGraphics.fillGradient(RenderType.guiOverlay(), pos.x() + this.enableElement.pos().x(), pos.y() + this.enableElement.pos().y() + 7, pos.x() + this.enableElement.pos().x() + 3, pos.y() + this.enableElement.pos().y() + 12, -2130706433, -2130706433, 0);
                }
            } else {
                guiGraphics.blit(BackpackScreen.ICONS, pos.x() + this.enableElement.pos().x(), pos.y() + this.enableElement.pos().y(), 18, 37, this.enableElement.size().x(), this.enableElement.size().y());
                if(isMouseOverEnableButton(mouseX, mouseY)) {
                    guiGraphics.fillGradient(RenderType.guiOverlay(), pos.x() + this.enableElement.pos().x(), pos.y() + this.enableElement.pos().y() + 1, pos.x() + this.enableElement.pos().x() + 3, pos.y() + this.enableElement.pos().y() + 6, -2130706433, -2130706433, 0);
                }
            }
        }
    }

    public void renderEnableButtonTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(this.upgrade instanceof IEnable e && !this.upgrade.isTabOpened()) {
            if(isMouseOverEnableButton(mouseX, mouseY)) {
                if(e.isEnabled(this.upgrade)) {
                    guiGraphics.renderTooltip(screen.getFont(), Component.literal("Disable Upgrade"), mouseX, mouseY);
                } else {
                    guiGraphics.renderTooltip(screen.getFont(), Component.literal("Enable Upgrade"), mouseX, mouseY);
                }
            }
        }
    }

    public boolean enableButtonMouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(this.upgrade instanceof IEnable e && !this.upgrade.isTabOpened()) {
            if(isMouseOverEnableButton(pMouseX, pMouseY)) {
                if(!isBackpackOwner()) {
                    return false;
                }
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.UPGRADE_TAB, this.dataHolderSlot, !e.isEnabled(this.upgrade), ServerActions.UPGRADE_ENABLED);
                this.screen.playUIClickSound();
                return true;
            }
        }
        return false;
    }

    public boolean isBackpackOwner() {
        return screen.getWrapper().isOwner(screen.getMenu().player);
    }

    public boolean isTabOpened() {
        return this.upgrade.isTabOpened();
    }

    @Override
    public int[] getWidgetSizeAndPos() {
        int[] size = new int[4];
        size[0] = pos.x();
        size[1] = pos.y();
        size[2] = width + (this.upgrade instanceof IEnable && !isTabOpened() ? 4 : 0);
        size[3] = height;
        return size;
    }
}