package com.tiviacz.travelersbackpack.client.screen.tooltip;

import com.tiviacz.travelersbackpack.inventory.FluidTank;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

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
            if(!component.leftTank.isResourceBlank())
            {
                height += 10;
            }

            if(!component.rightTank.isResourceBlank())
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
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix4f, VertexConsumerProvider.Immediate vertexConsumers)
    {
        if(BackpackUtils.isCtrlPressed())
        {
            int yOffset = 0;

            if(!component.leftTank.isResourceBlank())
            {
                renderFluidTankTooltip(component.leftTank, textRenderer, x, y, matrix4f, vertexConsumers);
                yOffset += 10;
            }

            if(!component.rightTank.isResourceBlank())
            {
                renderFluidTankTooltip(component.rightTank, textRenderer, x, y + yOffset, matrix4f, vertexConsumers);
            }
        }
    }

    public void renderFluidTankTooltip(FluidTank fluidTank, TextRenderer textRenderer, int x, int y, Matrix4f matrix4f, VertexConsumerProvider.Immediate immediate)
    {
        float amount = (float)fluidTank.getAmount() / 81;

        Text c = new LiteralText(FluidVariantAttributes.getName(fluidTank.getResource()).getString());
        Text c1 = new LiteralText(": ");
        Text c2 = new LiteralText((int)amount + "mB");

        textRenderer.draw(c, (float)x, (float)y, -1, true, matrix4f, immediate, false, 0, 15728880);
        textRenderer.draw(c1, (float)x + textRenderer.getWidth(c), (float)y, -1, true, matrix4f, immediate, false, 0, 15728880);
        textRenderer.draw(c2, (float)x + textRenderer.getWidth(c) + textRenderer.getWidth(c1), (float)y, 5592575, true, matrix4f, immediate, false, 0, 15728880);
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int pY, MatrixStack matrices, ItemRenderer itemRenderer, int z)
    {
        int yOffset = 0;

        if(BackpackUtils.isCtrlPressed())
        {
            if(!component.leftTank.isResourceBlank())
            {
                yOffset += 10;
            }

            if(!component.rightTank.isResourceBlank())
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
                    yOffset += (i / 9) * 18;
                    drawItem(component.storage.get(i), x + j*2 + j*18, pY + yOffset, textRenderer, itemRenderer);

                    if(j < 8) j++;
                    else j = 0;
                }
            }

            if(!component.tools.isEmpty())
            {
                if(flag) yOffset += 18;

                for(int i = 0; i < component.tools.size(); i++)
                {
                    drawItem(component.tools.get(i), x + (i*18), pY + yOffset, textRenderer, itemRenderer);
                }
            }
        }
    }

    private void drawItem(ItemStack stack, int x, int y, TextRenderer textRenderer, ItemRenderer itemRenderer)
    {
        itemRenderer.renderGuiItemIcon(stack, x, y);
        itemRenderer.renderGuiItemOverlay(textRenderer, stack, x, y);
    }
}