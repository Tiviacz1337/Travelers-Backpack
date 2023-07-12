package com.tiviacz.travelersbackpack.client.screen.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

@Environment(value= EnvType.CLIENT)
public class BackpackTooltipComponent implements TooltipComponent
{
    public static final Identifier LEATHER_TOOLTIP_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/tooltip/leather_travelers_backpack_tooltip.png");
    public static final Identifier IRON_TOOLTIP_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/tooltip/iron_travelers_backpack_tooltip.png");
    public static final Identifier GOLD_TOOLTIP_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/tooltip/gold_travelers_backpack_tooltip.png");
    public static final Identifier DIAMOND_TOOLTIP_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/tooltip/diamond_travelers_backpack_tooltip.png");
    public static final Identifier NETHERITE_TOOLTIP_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/tooltip/netherite_travelers_backpack_tooltip.png");
    private final BackpackTooltipData component;

    public BackpackTooltipComponent(BackpackTooltipData component)
    {
        this.component = component;
    }

    @Override
    public int getHeight()
    {
        if(BackpackUtils.isCtrlPressed() && component.stack.hasNbt())
        {
            return getTextureHeight();
        }
        return 0;
    }

    @Override
    public int getWidth(TextRenderer textRenderer)
    {
        if(BackpackUtils.isCtrlPressed() && component.stack.hasNbt())
        {
            return 211;
        }
        return 0;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix4f, VertexConsumerProvider.Immediate immediate)
    {

    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z)
    {
        if(!component.stack.hasNbt()) return;

        if(BackpackUtils.isCtrlPressed())
        {
            blit(matrices, x, y, z);
            int slot = 0;

            if(!component.inventory.isEmpty())
            {
                for(int j = 0; j < 7; j++)
                {
                    for(int i = 0; i < 9; i++)
                    {
                        if(applyGridConditions(i, j)) continue;

                        int i1 = x + (component.tier != Tiers.NETHERITE ? i - 1 : i) * 18 + (component.tier != Tiers.NETHERITE ? 42 : 42 - 18);
                        int j1 = y + j * 18 + 6;
                        this.renderItemInSlot(i1, j1, slot, textRenderer, matrices, itemRenderer, z, false);
                        slot++;
                    }
                }
            }

            int craftingSlot = 0;

            if(!component.craftingInventory.isEmpty())
            {
                for(int j = 0; j < 3; j++)
                {
                    for(int i = 0; i < 3; i++)
                    {
                        int i1 = x + i * 18 + 132;
                        int j1 = y + j * 18 + (component.tier.getOrdinal() * 18) + 6;
                        this.renderItemInSlot(i1, j1, craftingSlot, textRenderer, matrices, itemRenderer, z, true);
                        craftingSlot++;
                    }
                }
            }

            if(component.hasToolInSlot(Tiers.SlotType.TOOL_UPPER))
            {
                this.renderItemInSlot(x + 42 - 18, y + (component.tier.getOrdinal() * 18) + 18 + 6, slot, textRenderer, matrices, itemRenderer, z, false);
                slot++;
            }

            if(component.hasToolInSlot(Tiers.SlotType.TOOL_UPPER))
            {
                this.renderItemInSlot(x + 42 - 18, y + (component.tier.getOrdinal() * 18) + 36 + 6, slot, textRenderer, matrices, itemRenderer, z, false);
                slot++;
            }

            if(!component.leftTank.isResourceBlank())
            {
                RenderUtils.renderScreenTank(matrices, component.leftTank, x + 6, y + 7, 1000, component.tier.getTankRenderPos(), 16);
            }

            if(!component.rightTank.isResourceBlank())
            {
                RenderUtils.renderScreenTank(matrices, component.rightTank, x + 188, y + 7, 1000, component.tier.getTankRenderPos(), 16);
            }
        }
    }

    private void renderItemInSlot(int x, int y, int slot, TextRenderer textRenderer, MatrixStack matrices, ItemRenderer itemRenderer, int z, boolean isCrafting)
    {
        ItemStack stack = ItemStack.EMPTY;

        if(!isCrafting)
        {
            if(slot > component.tier.getStorageSlots() + 2) return;

            stack = component.inventory.getStack(slot);
        }
        else
        {
            stack = component.craftingInventory.getStack(slot);
        }

        if(stack.isEmpty()) return;

        itemRenderer.renderGuiItemIcon(stack, x + 1, y + 1);
        itemRenderer.renderGuiItemOverlay(textRenderer, stack, x + 1, y + 1);
    }

    private void blit(MatrixStack matrices, int x, int y, int z)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, getTooltipTexture());
        Screen.drawTexture(matrices, x, y, z, 0, 0, 211, getTextureHeight(), 256, 256);
    }

    private boolean applyGridConditions(int i, int j)
    {
        if(component.tier != Tiers.NETHERITE)
        {
            if(i == 0) return true;
        }
        if(component.tier == Tiers.LEATHER)
        {
            if(i > 5) return true;
            if(j > 2) return true;
        }

        if(component.tier == Tiers.IRON)
        {
            if(j > 0 && i > 5) return true;
            if(j > 3) return true;
        }

        if(component.tier == Tiers.GOLD)
        {
            if(j > 1 && i > 5) return true;
            if(j > 4) return true;
        }

        if(component.tier == Tiers.DIAMOND)
        {
            if(j > 2 && i > 5) return true;
            if(j > 5) return true;
        }

        if(component.tier == Tiers.NETHERITE)
        {
            if(j > 4 && i == 0) return true;
            if(j > 3 && i > 5) return true;
        }
        return false;
    }

    public Identifier getTooltipTexture()
    {
        if(component.tier == Tiers.LEATHER) return LEATHER_TOOLTIP_TRAVELERS_BACKPACK;
        if(component.tier == Tiers.IRON) return IRON_TOOLTIP_TRAVELERS_BACKPACK;
        if(component.tier == Tiers.GOLD) return GOLD_TOOLTIP_TRAVELERS_BACKPACK;
        if(component.tier == Tiers.DIAMOND) return DIAMOND_TOOLTIP_TRAVELERS_BACKPACK;
        if(component.tier == Tiers.NETHERITE) return NETHERITE_TOOLTIP_TRAVELERS_BACKPACK;
        return LEATHER_TOOLTIP_TRAVELERS_BACKPACK;
    }

    public int getTextureHeight()
    {
        if(component.tier == Tiers.LEATHER) return 67;
        if(component.tier == Tiers.IRON) return 85;
        if(component.tier == Tiers.GOLD) return 103;
        if(component.tier == Tiers.DIAMOND) return 121;
        if(component.tier == Tiers.NETHERITE) return 139;
        return 67;
    }
}