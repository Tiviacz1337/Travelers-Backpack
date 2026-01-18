package com.tiviacz.travelersbackpack.inventory.upgrades.tanks;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetElement;
import com.tiviacz.travelersbackpack.inventory.FluidTank;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.voiding.VoidUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.voiding.VoidWidget;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.util.FluidTypeHelper;
import com.tiviacz.travelersbackpack.util.FluidUtil;
import com.tiviacz.travelersbackpack.util.RenderHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.ArrayList;
import java.util.List;

public class TankWidget extends UpgradeWidgetBase<TanksUpgrade> {
    public final WidgetElement leftTankElement;
    public final WidgetElement rightTankElement;
    public final int tankWidth = 18;
    public final int tankHeight;
    public final Point leftTankPos;
    public final Point rightTankPos;

    public TankWidget(BackpackScreen screen, TanksUpgrade upgrade, Point pos) {
        super(screen, upgrade, pos, new Point(0, 0), "screen.travelersbackpack.tanks_upgrade");
        this.tankHeight = 18 * screen.visibleRows;
        this.leftTankPos = new Point(screen.getGuiLeft() + 7, screen.getGuiTop() + 17);
        this.rightTankPos = new Point(screen.getGuiLeft() + 195 + (screen.getWrapper().isExtended() ? 36 : 0), screen.getGuiTop() + 17);
        this.leftTankElement = new WidgetElement(this.leftTankPos, new Point(this.tankWidth, this.tankHeight));
        this.rightTankElement = new WidgetElement(this.rightTankPos, new Point(this.tankWidth, this.tankHeight));
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if(inTank(this.leftTankElement, mouseX, mouseY)) {
            guiGraphics.setComponentTooltipForNextFrame(screen.getFont(), getTankTooltip(this.upgrade.leftTank), mouseX, mouseY);
        }

        if(inTank(this.rightTankElement, mouseX, mouseY)) {
            guiGraphics.setComponentTooltipForNextFrame(screen.getFont(), getTankTooltip(this.upgrade.rightTank), mouseX, mouseY);
        }
    }

    @Override
    public void renderAboveBg(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
        int rows = upgrade.getUpgradeManager().getWrapper().getRows();
        RenderHelper.renderScreenTank(guiGraphics, this.upgrade.leftTank, this.leftTankPos.x() + 1, this.leftTankPos.y() + 1, 0, getTankHeight(rows), 16);
        renderTank(guiGraphics, this.leftTankElement, 0, mouseX, mouseY, rows, this.leftTankPos.x(), this.leftTankPos.y());
        RenderHelper.renderScreenTank(guiGraphics, this.upgrade.rightTank, this.rightTankPos.x() + 1, this.rightTankPos.y() + 1, 0, getTankHeight(rows), 16);
        renderTank(guiGraphics, this.rightTankElement, 1, mouseX, mouseY, rows, this.rightTankPos.x(), this.rightTankPos.y());
    }

    public int getTankHeight(int rows) {
        return (screen.isScrollable ? screen.visibleRows : rows) * 18 - 2;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if(inTank(this.leftTankElement, event.x(), event.y())) {
            if(isValid(screen.getMenu().getCarried())) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.FILL_TANK, true);
                return true;
            }
        }
        if(inTank(this.rightTankElement, event.x(), event.y())) {
            if(isValid(screen.getMenu().getCarried())) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.FILL_TANK, false);
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        if(inTank(this.leftTankElement, pMouseX, pMouseY) || inTank(this.rightTankElement, pMouseX, pMouseY)) {
            if(!screen.getMenu().getCarried().isEmpty()) {
                return true;
            }
        }
        return super.isMouseOver(pMouseX, pMouseY);
    }

    public boolean isValid(ItemStack stack) {
        return FluidUtil.hasFluidStorageConstant(stack) || stack.getItem() instanceof PotionItem;
        //return true;
        //return FluidUtil.getFluidHandler(stack).isPresent() || stack.getItem() instanceof PotionItem || stack.getItem() == Items.GLASS_BOTTLE;
    }

    public void renderTank(GuiGraphics guiGraphics, WidgetElement tankElement, int tankIndex, int mouseX, int mouseY, int rows, int x, int y) {
        //Render red highlight if hovering with trash bin
        if(screen.mappedWidgets.get(VoidUpgrade.class) instanceof VoidWidget voidWidget) {
            voidWidget.drawRedTankHighlight(guiGraphics, x + 1, y + 1, inTank(tankElement, mouseX, mouseY), getTankHeight(rows), tankIndex);
        }

        //Top segment
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.ICONS, x, y, 0, 95, 18, 18, 256, 256);

        //Middle segment
        for(int i = 1; i <= (screen.isScrollable ? screen.visibleRows : rows) - 2; i++) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.ICONS, x, y + (18 * i), 0, 113, 18, 18, 256, 256);
        }

        //Bottom segment
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.ICONS, x, y + (18 * ((screen.isScrollable ? screen.visibleRows : rows) - 1)), 0, 131, 18, 18, 256, 256);
    }

    @Environment(EnvType.CLIENT)
    public List<Component> getTankTooltip(FluidTank tank) {
        FluidVariantWrapper fluidStack = tank.getFluid();
        List<Component> tankTips = new ArrayList<>();
        String fluidName = !fluidStack.isEmpty() ? FluidTypeHelper.getFluidVariantName(fluidStack.fluidVariant()).getString() : I18n.get("screen.travelersbackpack.none");
        String fluidAmount = !fluidStack.isEmpty() ? fluidStack.getAmount() + "/" + tank.getCapacity() : I18n.get("screen.travelersbackpack.empty");

        if(!fluidStack.isEmpty()) {
            if(fluidStack.fluidVariant().getComponents().get(DataComponents.POTION_CONTENTS) != null && fluidStack.fluidVariant().getComponents().get(DataComponents.POTION_CONTENTS).isPresent()) {
                float durationFactor = 1.0F;
                if(fluidStack.fluidVariant().getComponentMap().has(DataComponents.CUSTOM_DATA)) {
                    if(fluidStack.fluidVariant().getComponents().get(DataComponents.CUSTOM_DATA).get().copyTag().contains("PotionType")) {
                        int potionType = fluidStack.fluidVariant().getComponents().get(DataComponents.CUSTOM_DATA).get().copyTag().getIntOr("PotionType", 0);
                        if(potionType == 1) {
                            tankTips.add(Component.translatable("item.minecraft.splash_potion"));
                        }
                        if(potionType == 2) {
                            tankTips.add(Component.translatable("item.minecraft.lingering_potion"));
                            durationFactor = 0.25F;
                        }

                    }
                }
                fluidName = null;
                PotionContents contents = fluidStack.fluidVariant().getComponents().get(DataComponents.POTION_CONTENTS).get();
                if(Minecraft.getInstance().level != null) {
                    PotionContents.addPotionTooltip(contents.getAllEffects(), tankTips::add, durationFactor, Minecraft.getInstance().level.tickRateManager().tickrate());
                }
            }
        }

        if(fluidName != null) tankTips.add(Component.literal(fluidName));
        tankTips.add(Component.literal(fluidAmount));

        return tankTips;
    }

    public boolean inTank(WidgetElement tankElement, double mouseX, double mouseY) {
        return mouseX >= tankElement.pos().x() && mouseX < tankElement.pos().x() + tankElement.size().x() && mouseY >= tankElement.pos().y() && mouseY < tankElement.pos().y() + tankElement.size().y();
    }
}