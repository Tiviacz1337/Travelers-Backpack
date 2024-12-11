package com.tiviacz.travelersbackpack.inventory.upgrades.tanks;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.inventory.FluidTank;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import com.tiviacz.travelersbackpack.inventory.SlotPositioner;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.network.ServerboundFillTankPacket;
import com.tiviacz.travelersbackpack.util.FluidTypeHelper;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import com.tiviacz.travelersbackpack.util.RenderHelper;
import dev.architectury.fluid.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
        this.tankHeight = upgrade.tankHeight;
        this.leftTankPos = upgrade.leftTankPos;
        this.rightTankPos = upgrade.rightTankPos;
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if (inTank(this.leftTankPos, mouseX, mouseY)) {
            guiGraphics.renderComponentTooltip(screen.getFont(), getTankTooltip(this.upgrade.leftTank), mouseX, mouseY);
        }

        if (inTank(this.rightTankPos, mouseX, mouseY)) {
            guiGraphics.renderComponentTooltip(screen.getFont(), getTankTooltip(this.upgrade.rightTank), mouseX, mouseY);
        }
    }

    @Override
    public void renderAboveBg(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
        SlotPositioner pos = this.upgrade.getUpgradeManager().getWrapper().getSlotPositioner();
        int extendedOffset = 0;
        RenderHelper.renderScreenTank(guiGraphics, this.upgrade.leftTank, x + 8, y + 8, 0, pos.getRows() * 18 - 2, 16);
        renderTank(guiGraphics, pos, x + 7, y);
        if (pos.isExtended()) extendedOffset = 36;
        RenderHelper.renderScreenTank(guiGraphics, this.upgrade.rightTank, x + 196 + extendedOffset, y + 8, 0, pos.getRows() * 18 - 2, 16);
        renderTank(guiGraphics, pos, x + 195 + extendedOffset, y);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (inTank(this.leftTankPos, (int) pMouseX, (int) pMouseY)) {
            if (isValid(screen.getMenu().getCarried())) {
                PacketDistributor.sendToServer(new ServerboundFillTankPacket(true));
                return true;
            }
        }
        if (inTank(this.rightTankPos, (int) pMouseX, (int) pMouseY)) {
            if (isValid(screen.getMenu().getCarried())) {
                PacketDistributor.sendToServer(new ServerboundFillTankPacket(false));
                return true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public boolean isValid(ItemStack stack) {
        return true;
        //return FluidUtil.getFluidHandler(stack).isPresent() || stack.getItem() instanceof PotionItem || stack.getItem() == Items.GLASS_BOTTLE; //#TODO
    }

    public void renderTank(GuiGraphics guiGraphics, SlotPositioner pos, int x, int y) {
        //Top segment
        guiGraphics.blit(BackpackScreen.ICONS, x, y + 7, 0, 95, 18, 18);

        //Middle segment
        for (int i = 1; i <= pos.getRows() - 2; i++) {
            guiGraphics.blit(BackpackScreen.ICONS, x, y + 7 + (18 * i), 0, 113, 18, 18);
        }

        //Bottom segment
        guiGraphics.blit(BackpackScreen.ICONS, x, y + 7 + (18 * (pos.getRows() - 1)), 0, 131, 18, 18);
    }

    @Environment(EnvType.CLIENT)
    public List<Component> getTankTooltip(FluidTank tank) {
        FluidVariantWrapper fluidStack = tank.getFluid();
        List<Component> tankTips = new ArrayList<>();
        String fluidName = !fluidStack.isEmpty() ? FluidTypeHelper.getFluidVariantName(fluidStack.fluidVariant()).getString() : I18n.get("screen.travelersbackpack.none");
        String fluidAmount = !fluidStack.isEmpty() ? fluidStack.getAmount() + "/" + tank.getCapacity() : I18n.get("screen.travelersbackpack.empty");

        if (!fluidStack.isEmpty()) {
            if (fluidStack.fluidVariant().getComponents().get(DataComponents.POTION_CONTENTS) != null && fluidStack.fluidVariant().getComponents().get(DataComponents.POTION_CONTENTS).isPresent()) {
                fluidName = null;
                PotionContents contents = fluidStack.fluidVariant().getComponents().get(DataComponents.POTION_CONTENTS).get();
                if (Minecraft.getInstance().level != null) {
                    contents.addPotionTooltip(tankTips::add, 1.0F, Minecraft.getInstance().level.tickRateManager().tickrate());
                }
                //contents.addPotionTooltip(tankTips::add, 1.0F, level.tickRateManager().tickrate());
            }
        }

        if (fluidName != null) tankTips.add(Component.literal(fluidName));
        tankTips.add(Component.literal(fluidAmount));

        return tankTips;
    }

    public boolean inTank(Point tankPos, int mouseX, int mouseY) {
        return screen.getGuiLeft() + tankPos.x() <= mouseX && mouseX <= tankPos.x() + this.tankWidth + screen.getGuiLeft() && tankPos.y() + screen.getGuiTop() <= mouseY && mouseY <= tankPos.y() + this.tankHeight + screen.getGuiTop();
    }
}