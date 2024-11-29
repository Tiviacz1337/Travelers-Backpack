package com.tiviacz.travelersbackpackold.client.screen.tooltip;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpackold.util.BackpackUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

@Environment(value= EnvType.CLIENT)
public class BackpackTooltipComponent implements TooltipComponent
{
    private final BackpackTooltipData component;

    public BackpackTooltipComponent(BackpackTooltipData component)
    {
        this.component = component;
    }

    @Override
    public int getHeight()
    {
        int height = 0;

        if(BackpackUtils.isCtrlPressed())
        {
            if(!component.leftTank.getFirst().isBlank())
            {
                height += 10;
            }

            if(!component.rightTank.getFirst().isBlank())
            {
                height += 10;
            }

            if(!component.storage.isEmpty())
            {
                height += (int)(Math.ceil((float)component.storage.size() / 9) * 18);
            }

            if(!component.tools.isEmpty())
            {
                height += 18;
            }
        }
        return height;
    }

    @Override
    public int getWidth(TextRenderer textRenderer)
    {
        int width = 0;

        if(BackpackUtils.isCtrlPressed())
        {
            if(!component.storage.isEmpty())
            {
                width += Math.min(component.storage.size(), 9) * 18 + Math.min(component.storage.size(), 9) * 2;
            }
        }
        return width;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix4f, VertexConsumerProvider.Immediate immediate)
    {
        if(BackpackUtils.isCtrlPressed())
        {
            int yOffset = 0;

            if(!component.leftTank.getFirst().isBlank())
            {
                renderFluidTankTooltip(component.leftTank, textRenderer, x, y, matrix4f, immediate);
                yOffset += 10;
            }

            if(!component.rightTank.getFirst().isBlank())
            {
                renderFluidTankTooltip(component.rightTank, textRenderer, x, y + yOffset, matrix4f, immediate);
            }
        }
    }

    public void renderFluidTankTooltip(Pair<FluidVariant, Long> fluidTank, TextRenderer textRenderer, int x, int y, Matrix4f matrix4f, VertexConsumerProvider.Immediate immediate)
    {
        float amount = (float)fluidTank.getSecond() / 81;

        Text c = Text.literal(FluidVariantAttributes.getName(fluidTank.getFirst()).getString());
        Text c1 = Text.literal(": ");
        Text c2 = Text.literal((int)amount + "mB");

        textRenderer.draw(c, (float)x, (float)y, -1, true, matrix4f, immediate, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        textRenderer.draw(c1, (float)x + textRenderer.getWidth(c), (float)y, -1, true, matrix4f, immediate, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        textRenderer.draw(c2, (float)x + textRenderer.getWidth(c) + textRenderer.getWidth(c1), (float)y, 5592575, true, matrix4f, immediate, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int pY, DrawContext context)
    {
        int yOffset = 0;

        if(BackpackUtils.isCtrlPressed())
        {
            if(!component.leftTank.getFirst().isBlank())
            {
                yOffset += 10;
            }

            if(!component.rightTank.getFirst().isBlank()) //tank...getResource().isBlank()
            {
                yOffset += 10;
            }

            boolean flag = false;

            if(!component.storage.isEmpty())
            {
                int j = 0;
                flag = true;

                for(int i = 0; i < component.storage.size(); i++)
                {
                    drawItem(component.storage.get(i), x + j*2 + j*18, pY + yOffset, textRenderer, context);

                    if(j < 8)
                    {
                        j++;
                    }
                    else
                    {
                        j = 0;
                        yOffset += 18;
                    }
                }
            }

            if(!component.tools.isEmpty())
            {
                if(flag) yOffset += 18;

                for(int i = 0; i < component.tools.size(); i++)
                {
                    drawItem(component.tools.get(i), x + (i*18), pY + yOffset, textRenderer, context);
                }
            }
        }
    }

    private void drawItem(ItemStack stack, int x, int y, TextRenderer textRenderer, DrawContext context)
    {
        context.drawItemWithoutEntity(stack, x, y);
        context.drawItemInSlot(textRenderer, stack, x, y);
    }
}