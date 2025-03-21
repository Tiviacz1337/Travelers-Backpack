package com.tiviacz.travelersbackpack.inventory.upgrades.tanks;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.util.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

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
            guiGraphics.renderComponentTooltip(screen.getFont(), getTankTooltip(this.upgrade.leftTank), mouseX, mouseY);
        }

        if(inTank(this.rightTankPos, mouseX, mouseY)) {
            guiGraphics.renderComponentTooltip(screen.getFont(), getTankTooltip(this.upgrade.rightTank), mouseX, mouseY);
        }
    }

    @Override
    public void renderAboveBg(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
        int extendedOffset = 0;
        int rows = upgrade.getUpgradeManager().getWrapper().getRows();
        y += 10;
        RenderHelper.renderScreenTank(guiGraphics, this.upgrade.leftTank, x + 8, y + 8, 0, (screen.isScrollable ? screen.visibleRows : rows) * 18 - 2, 16);
        renderTank(guiGraphics, rows, x + 7, y);
        if(upgrade.getUpgradeManager().getWrapper().isExtended()) extendedOffset = 36;
        RenderHelper.renderScreenTank(guiGraphics, this.upgrade.rightTank, x + 196 + extendedOffset, y + 8, 0, (screen.isScrollable ? screen.visibleRows : rows) * 18 - 2, 16);
        renderTank(guiGraphics, rows, x + 195 + extendedOffset, y);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(inTank(this.leftTankPos, (int)pMouseX, (int)pMouseY)) {
            if(isValid(screen.getMenu().getCarried())) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.FILL_TANK, true);
                return true;
            }
        }
        if(inTank(this.rightTankPos, (int)pMouseX, (int)pMouseY)) {
            if(isValid(screen.getMenu().getCarried())) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.FILL_TANK, false);
                return true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public boolean isValid(ItemStack stack) {
        return FluidUtil.getFluidHandler(stack).isPresent() || stack.getItem() instanceof PotionItem || stack.getItem() == Items.GLASS_BOTTLE;
    }

    public void renderTank(GuiGraphics guiGraphics, int rows, int x, int y) {
        //Top segment
        guiGraphics.blit(BackpackScreen.ICONS, x, y + 7, 0, 95, 18, 18);

        //Middle segment
        for(int i = 1; i <= (screen.isScrollable ? screen.visibleRows : rows) - 2; i++) {
            guiGraphics.blit(BackpackScreen.ICONS, x, y + 7 + (18 * i), 0, 113, 18, 18);
        }

        //Bottom segment
        guiGraphics.blit(BackpackScreen.ICONS, x, y + 7 + (18 * ((screen.isScrollable ? screen.visibleRows : rows) - 1)), 0, 131, 18, 18);
    }

    @OnlyIn(Dist.CLIENT)
    public List<Component> getTankTooltip(FluidTank tank) {
        FluidStack fluidStack = tank.getFluid();
        List<Component> tankTips = new ArrayList<>();
        String fluidName = !fluidStack.isEmpty() ? fluidStack.getHoverName().getString() : I18n.get("screen.travelersbackpack.none");
        String fluidAmount = !fluidStack.isEmpty() ? fluidStack.getAmount() + "/" + tank.getCapacity() : I18n.get("screen.travelersbackpack.empty");

        if(!fluidStack.isEmpty()) {
            if(fluidStack.has(DataComponents.POTION_CONTENTS)) {
                float durationFactor = 1.0F;
                if(fluidStack.has(DataComponents.CUSTOM_DATA)) {
                    if(fluidStack.get(DataComponents.CUSTOM_DATA).copyTag().contains("PotionType")) {
                        int potionType = fluidStack.get(DataComponents.CUSTOM_DATA).copyTag().getInt("PotionType");
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
                PotionContents contents = fluidStack.get(DataComponents.POTION_CONTENTS);
                if(Minecraft.getInstance().level != null) {
                    contents.addPotionTooltip(tankTips::add, durationFactor, Minecraft.getInstance().level.tickRateManager().tickrate());
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