package com.tiviacz.travelersbackpack.inventory.upgrades.tanks;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.inventory.FluidTank;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
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
    public final int tankWidth = 18;
    public final int tankHeight;
    public final Point leftTankPos;
    public final Point rightTankPos;

    public TankWidget(BackpackScreen screen, TanksUpgrade upgrade, Point pos) {
        super(screen, upgrade, pos, new Point(0, 0), "screen.travelersbackpack.tanks_upgrade");
        this.tankHeight = 18 * screen.visibleRows;
        this.leftTankPos = upgrade.leftTankPos;
        this.rightTankPos = upgrade.rightTankPos;
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if(inTank(this.leftTankPos, mouseX, mouseY)) {
            guiGraphics.setComponentTooltipForNextFrame(screen.getFont(), getTankTooltip(this.upgrade.leftTank), mouseX, mouseY);
        }

        if(inTank(this.rightTankPos, mouseX, mouseY)) {
            guiGraphics.setComponentTooltipForNextFrame(screen.getFont(), getTankTooltip(this.upgrade.rightTank), mouseX, mouseY);
        }
    }

    @Override
    public void renderAboveBg(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
        int extendedOffset = 0;
        int rows = upgrade.getUpgradeManager().getWrapper().getRows();
        y += 10;
        renderTank(guiGraphics, rows, x + 7, y);
        RenderHelper.renderScreenTank(guiGraphics, this.upgrade.leftTank, x + 8, y + 8, 0, (screen.isScrollable ? screen.visibleRows : rows) * 18 - 2, 16);
        if(upgrade.getUpgradeManager().getWrapper().isExtended()) extendedOffset = 36;
        renderTank(guiGraphics, rows, x + 195 + extendedOffset, y);
        RenderHelper.renderScreenTank(guiGraphics, this.upgrade.rightTank, x + 196 + extendedOffset, y + 8, 0, (screen.isScrollable ? screen.visibleRows : rows) * 18 - 2, 16);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if(inTank(this.leftTankPos, (int)event.x(), (int)event.y())) {
            if(isValid(screen.getMenu().getCarried())) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.FILL_TANK, true);
                return true;
            }
        }
        if(inTank(this.rightTankPos, (int)event.x(), (int)event.y())) {
            if(isValid(screen.getMenu().getCarried())) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.FILL_TANK, false);
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        if(inTank(this.leftTankPos, (int)pMouseX, (int)pMouseY) || inTank(this.rightTankPos, (int)pMouseX, (int)pMouseY)) {
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

    public void renderTank(GuiGraphics guiGraphics, int rows, int x, int y) {
        //Top segment
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.ICONS, x, y + 7, 0, 95, 18, 18, 256, 256);

        //Middle segment
        for(int i = 1; i <= (screen.isScrollable ? screen.visibleRows : rows) - 2; i++) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.ICONS, x, y + 7 + (18 * i), 0, 113, 18, 18, 256, 256);
        }

        //Bottom segment
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.ICONS, x, y + 7 + (18 * ((screen.isScrollable ? screen.visibleRows : rows) - 1)), 0, 131, 18, 18, 256, 256);
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

    public boolean inTank(Point tankPos, int mouseX, int mouseY) {
        return screen.getGuiLeft() + tankPos.x() <= mouseX && mouseX <= tankPos.x() + this.tankWidth + screen.getGuiLeft() && tankPos.y() + screen.getGuiTop() <= mouseY && mouseY <= tankPos.y() + this.tankHeight + screen.getGuiTop();
    }
}