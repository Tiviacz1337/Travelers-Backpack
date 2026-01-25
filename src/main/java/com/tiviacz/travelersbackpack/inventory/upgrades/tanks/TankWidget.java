package com.tiviacz.travelersbackpack.inventory.upgrades.tanks;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetElement;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.voiding.VoidUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.voiding.VoidWidget;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.util.FluidStackHelper;
import com.tiviacz.travelersbackpack.util.RenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            guiGraphics.renderComponentTooltip(screen.getFont(), getTankTooltip(this.upgrade.leftTank), mouseX, mouseY);
        }

        if(inTank(this.rightTankElement, mouseX, mouseY)) {
            guiGraphics.renderComponentTooltip(screen.getFont(), getTankTooltip(this.upgrade.rightTank), mouseX, mouseY);
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
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(inTank(this.leftTankElement, pMouseX, pMouseY)) {
            if(isValid(screen.getMenu().getCarried())) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.FILL_TANK, true);
                return true;
            }
        }
        if(inTank(this.rightTankElement, pMouseX, pMouseY)) {
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

    public void renderTank(GuiGraphics guiGraphics, WidgetElement tankElement, int tankIndex, int mouseX, int mouseY, int rows, int x, int y) {
        //Render red highlight if hovering with trash bin
        if(screen.mappedWidgets.get(VoidUpgrade.class) instanceof VoidWidget voidWidget) {
            voidWidget.drawRedTankHighlight(guiGraphics, x + 1, y + 1, inTank(tankElement, mouseX, mouseY), getTankHeight(rows), tankIndex);
        }

        //Top segment
        guiGraphics.blit(BackpackScreen.ICONS, x, y, 0, 95, 18, 18);

        //Middle segment
        for(int i = 1; i <= (screen.isScrollable ? screen.visibleRows : rows) - 2; i++) {
            guiGraphics.blit(BackpackScreen.ICONS, x, y + (18 * i), 0, 113, 18, 18);
        }

        //Bottom segment
        guiGraphics.blit(BackpackScreen.ICONS, x, y + (18 * ((screen.isScrollable ? screen.visibleRows : rows) - 1)), 0, 131, 18, 18);
    }

    @OnlyIn(Dist.CLIENT)
    public static List<Component> getTankTooltip(FluidTank tank) {
        FluidStack fluidStack = tank.getFluid();
        List<Component> tankTips = new ArrayList<>();
        String fluidName = !fluidStack.isEmpty() ? fluidStack.getFluid().getFluidType().getDescription().getString() : I18n.get("screen.travelersbackpack.none");
        String fluidAmount = !fluidStack.isEmpty() ? fluidStack.getAmount() + "/" + tank.getCapacity() : I18n.get("screen.travelersbackpack.empty");

        if(!fluidStack.isEmpty()) {
            if(fluidStack.getTag() != null) {
                float durationFactor = 1.0F;
                if(fluidStack.getTag().contains("Splash")) {
                    tankTips.add(Component.translatable("item.minecraft.splash_potion"));
                }
                if(fluidStack.getTag().contains("Lingering")) {
                    tankTips.add(Component.translatable("item.minecraft.lingering_potion"));
                    durationFactor = 0.25F;
                }
                if(fluidStack.getTag().contains("Potion")) {
                    fluidName = null;
                    setPotionDescription(FluidStackHelper.getItemStackFromFluidStack(fluidStack), tankTips, durationFactor);
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

    public static void setPotionDescription(ItemStack stack, List<Component> componentList, float durationFactor) {
        List<MobEffectInstance> list = PotionUtils.getMobEffects(stack);
        List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();
        if(list.isEmpty()) {
            componentList.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
        } else {
            for(MobEffectInstance mobeffectinstance : list) {
                MutableComponent mutablecomponent = Component.translatable(mobeffectinstance.getDescriptionId());
                MobEffect mobeffect = mobeffectinstance.getEffect();
                Map<Attribute, AttributeModifier> map = mobeffect.getAttributeModifiers();
                if(!map.isEmpty()) {
                    for(Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                        AttributeModifier attributemodifier = entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), mobeffect.getAttributeModifierValue(mobeffectinstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                        list1.add(new Pair<>(entry.getKey(), attributemodifier1));
                    }
                }

                if(mobeffectinstance.getAmplifier() > 0) {
                    mutablecomponent = Component.translatable("potion.withAmplifier", mutablecomponent, Component.translatable("potion.potency." + mobeffectinstance.getAmplifier()));
                }

                if(mobeffectinstance.getDuration() > 20) {
                    mutablecomponent = Component.translatable("potion.withDuration", mutablecomponent, MobEffectUtil.formatDuration(mobeffectinstance, durationFactor));
                }

                componentList.add(mutablecomponent.withStyle(mobeffect.getCategory().getTooltipFormatting()));
            }
        }

        if(!list1.isEmpty()) {
            componentList.add(CommonComponents.EMPTY);
            componentList.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));

            for(Pair<Attribute, AttributeModifier> pair : list1) {
                AttributeModifier attributemodifier2 = pair.getSecond();
                double d0 = attributemodifier2.getAmount();
                double d1;
                if(attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    d1 = attributemodifier2.getAmount();
                } else {
                    d1 = attributemodifier2.getAmount() * 100.0D;
                }

                if(d0 > 0.0D) {
                    componentList.add(Component.translatable("attribute.modifier.plus." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(pair.getFirst().getDescriptionId())).withStyle(ChatFormatting.BLUE));
                } else if(d0 < 0.0D) {
                    d1 *= -1.0D;
                    componentList.add(Component.translatable("attribute.modifier.take." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(pair.getFirst().getDescriptionId())).withStyle(ChatFormatting.RED));
                }
            }
        }
    }
}