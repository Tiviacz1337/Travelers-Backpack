package com.tiviacz.travelersbackpack.client.screens.tooltip;

import com.tiviacz.travelersbackpack.inventory.CommonFluid;
import com.tiviacz.travelersbackpack.util.KeyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class ClientBackpackTooltipComponent implements ClientTooltipComponent {
    private final BackpackTooltipComponent component;

    public ClientBackpackTooltipComponent(BackpackTooltipComponent component) {
        this.component = component;
    }

    public boolean show() {
        return KeyHelper.isCtrlPressed() || component.isHoveredWithItem();
    }

    @Override
    public int getHeight(Font font) {
        int height = 0;

        if(show()) {
            if(!component.leftFluidStack.isEmpty()) {
                height += 10;
            }

            if(!component.rightFluidStack.isEmpty()) {
                height += 10;
            }

            if(!component.upgrades.isEmpty()) {
                height += 10; //Text
                height += 18;
            }

            if(!component.storage.isEmpty()) {
                height += 10; //Text
                height += (int)(Math.ceil((float)component.storage.size() / 9) * 18);
            }

            if(!component.tools.isEmpty()) {
                height += 10; //Text
                height += 18;
            }
        }
        return height;
    }

    @Override
    public int getWidth(Font font) {
        int width = 0;
        int textWidth = 0;

        if(show()) {
            if(!component.storage.isEmpty()) {
                width += Math.min(component.storage.size(), 9) * 18 + Math.min(component.storage.size(), 9) * 2;
            }
            if(!component.leftFluidStack.isEmpty()) {
                textWidth = font.width(getFluidTankTooltip(component.leftFluidStack));
            }
            if(!component.rightFluidStack.isEmpty()) {
                textWidth = Math.max(font.width(getFluidTankTooltip(component.rightFluidStack)), textWidth);
            }
            if(textWidth > width) {
                width = textWidth;
            }
        }
        return width;
    }

    @Override
    public void extractText(GuiGraphicsExtractor guiGraphics, Font pFont, int pMouseX, int pMouseY) {
        if(show()) {
            int yOffset = 0;

            if(!component.leftFluidStack.isEmpty()) {
                renderFluidTankTooltip(component.leftFluidStack, guiGraphics, pFont, pMouseX, pMouseY);
                yOffset += 10;
            }

            if(!component.rightFluidStack.isEmpty()) {
                renderFluidTankTooltip(component.rightFluidStack, guiGraphics, pFont, pMouseX, pMouseY + yOffset);
                yOffset += 10;
            }

            if(!component.upgrades.isEmpty()) {
                guiGraphics.text(pFont, Component.translatable("screen.travelersbackpack.upgrades").withStyle(ChatFormatting.YELLOW), pMouseX, pMouseY + yOffset, -1);
                yOffset += 10;
                yOffset += 18;
            }

            if(!component.storage.isEmpty()) {
                guiGraphics.text(pFont, Component.translatable("screen.travelersbackpack.inventory").withStyle(ChatFormatting.YELLOW), pMouseX, pMouseY + yOffset, -1);
                yOffset += 10;
                yOffset += (int)(Math.ceil((float)component.storage.size() / 9) * 18);
            }

            if(!component.tools.isEmpty()) {
                guiGraphics.text(pFont, Component.translatable("screen.travelersbackpack.tools").withStyle(ChatFormatting.YELLOW), pMouseX, pMouseY + yOffset, -1);
                yOffset += 10;
                yOffset += 18;
            }
        }
    }

    @Override
    public void extractImage(Font pFont, int pX, int pY, int k, int k1, GuiGraphicsExtractor pGuiGraphicsExtractor) {
        int yOffset = 0;

        if(show()) {
            if(!component.leftFluidStack.isEmpty()) {
                yOffset += 10;
            }

            if(!component.rightFluidStack.isEmpty()) {
                yOffset += 10;
            }

            boolean flag = false;

            if(!component.upgrades.isEmpty()) {
                yOffset += 10; //text
                flag = true;

                for(int i = 0; i < component.upgrades.size(); i++) {
                    renderItem(component.upgrades.get(i), pX + (i * 18), pY + yOffset, pFont, pGuiGraphicsExtractor);
                }
            }

            if(!component.storage.isEmpty()) {
                yOffset += 10; //Text
                int j = 0;
                if(flag) yOffset += 18;
                flag = true;
                boolean nextRow = false;

                for(int i = 0; i < component.storage.size(); i++) {
                    if(nextRow) {
                        yOffset += 18;
                        nextRow = false;
                    }
                    renderItem(component.storage.get(i), pX + j * 2 + j * 18, pY + yOffset, pFont, pGuiGraphicsExtractor);

                    if(j < 8) {
                        j++;
                    } else {
                        j = 0;
                        nextRow = true;
                    }
                }
            }

            if(!component.tools.isEmpty()) {
                yOffset += 10; //Text
                if(flag) yOffset += 18;

                for(int i = 0; i < component.tools.size(); i++) {
                    renderItem(component.tools.get(i), pX + (i * 18), pY + yOffset, pFont, pGuiGraphicsExtractor);
                }
            }
        }
    }

    private void renderItem(ItemStack stack, int pX, int pY, Font pFont, GuiGraphicsExtractor guiGraphics) {
        guiGraphics.fakeItem(stack, pX, pY);
        guiGraphics.itemDecorations(pFont, stack, pX, pY);
    }

    //Forge

    public Component getFluidTankTooltip(FluidStack fluidStack) {
        Component c = CommonFluid.getFluidName(fluidStack);
        Component c1 = Component.literal(": ");
        Component c2 = Component.literal(fluidStack.getAmount() + "mB").withStyle(ChatFormatting.BLUE);
        return MutableComponent.create(c.getContents()).append(c1).append(c2);
    }

    public void renderFluidTankTooltip(FluidStack fluidStack, GuiGraphicsExtractor guiGraphics, Font font, int mouseX, int mouseY) {
        guiGraphics.text(font, getFluidTankTooltip(fluidStack), mouseX, mouseY, -1);
    }
}