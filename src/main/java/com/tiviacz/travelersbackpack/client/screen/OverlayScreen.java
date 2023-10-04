package com.tiviacz.travelersbackpack.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OverlayScreen extends Screen
{
    public Minecraft mc;
    public ItemRenderer itemRenderer;
    public MainWindow mainWindow;

    public OverlayScreen()
    {
        super(new StringTextComponent(""));

        this.mc = Minecraft.getInstance();
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
        this.mainWindow = Minecraft.getInstance().getWindow();
    }

    static float animationProgress = 0.0F;

    public void renderOverlay(MatrixStack matrixStack)
    {
        PlayerEntity player = mc.player;

        int offsetX = TravelersBackpackConfig.offsetX;
        int offsetY = TravelersBackpackConfig.offsetY;
        int scaledWidth = mainWindow.getGuiScaledWidth() - offsetX;
        int scaledHeight = mainWindow.getGuiScaledHeight() - offsetY;

        int textureX = 10;
        int textureY = 0;

        ITravelersBackpackInventory inv = CapabilityUtils.getBackpackInv(player);
        FluidTank rightTank = inv.getRightTank();
        FluidTank leftTank = inv.getLeftTank();

        if(!rightTank.getFluid().isEmpty())
        {
            this.drawGuiTank(matrixStack, rightTank, scaledWidth + 1, scaledHeight, 21, 8);
        }

        if(!leftTank.getFluid().isEmpty())
        {
            this.drawGuiTank(matrixStack, leftTank, scaledWidth - 11, scaledHeight, 21, 8);
        }

        KeyBinding key = ModClientEventHandler.CYCLE_TOOL;
        List<ItemStack> tools = getTools(inv.getTier(), inv.getInventory());

        if(key.isDown() && tools.size() > 2)
        {
            if(animationProgress < 1.0F)
            {
                animationProgress += 0.05F;
            }
            for(int i = 0; i < getTools(inv.getTier(), inv.getInventory()).size(); i++)
            {
                drawItemStack(getTools(inv.getTier(), inv.getInventory()).get(i), scaledWidth - 30, (int)(scaledHeight + 11 - (animationProgress * (i * 15))));
            }
        }
        else
        {
            if(animationProgress > 0.0F)
            {
                for(int i = 0; i < getTools(inv.getTier(), inv.getInventory()).size(); i++)
                {
                    drawItemStack(getTools(inv.getTier(), inv.getInventory()).get(i), scaledWidth - 30, (int)(scaledHeight + 11 - (animationProgress * (i * 15))));
                }
                animationProgress -= 0.05F;
            }
            else
            {
                if(!inv.getInventory().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_FIRST)).isEmpty())
                {
                    drawItemStack(inv.getInventory().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_FIRST)), scaledWidth - 30, scaledHeight - 4);
                }

                if(tools.size() > 1)
                {
                    if(!inv.getInventory().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_SECOND) + tools.size() - 2).isEmpty())
                    {
                        drawItemStack(inv.getInventory().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_SECOND) + tools.size() - 2), scaledWidth - 30, scaledHeight + 11);
                    }
                }
            }
        }

        ResourceLocation texture = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack_overlay.png");
        mc.getTextureManager().bind(texture);

        if(player.getMainHandItem().getItem() instanceof HoseItem)
        {
            int tank = HoseItem.getHoseTank(player.getMainHandItem());

            int selectedTextureX = 0;
            int selectedTextureY = 0;

            if(tank == 1)
            {
                blit(matrixStack, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                blit(matrixStack, scaledWidth - 12, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
            }

            if(tank == 2)
            {
                blit(matrixStack, scaledWidth, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
                blit(matrixStack, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }

            if(tank == 0)
            {
                blit(matrixStack, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                blit(matrixStack, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }
        }
        else
        {
            blit(matrixStack, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
            blit(matrixStack, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
        }
    }

    public void drawGuiTank(MatrixStack matrixStackIn, FluidTank tank, int startX, int startY, int height, int width)
    {
        RenderUtils.renderScreenTank(matrixStackIn, tank, startX, startY, height, width);
    }

    private void drawItemStack(ItemStack stack, int x, int y)
    {
        //RenderHelper.turnBackOn();
        itemRenderer.renderGuiItem(stack, x, y);
        itemRenderer.renderGuiItemDecorations(Minecraft.getInstance().font, stack, x, y);
        //RenderHelper.turnOff();
    }

    public static List<ItemStack> getTools(Tiers.Tier tier, ItemStackHandler inventory)
    {
        List<ItemStack> tools = new ArrayList<>();
        for(int i = tier.getSlotIndex(Tiers.SlotType.TOOL_FIRST); i <= tier.getSlotIndex(Tiers.SlotType.TOOL_FIRST) + (tier.getToolSlots() - 1); i++)
        {
            if(!inventory.getStackInSlot(i).isEmpty())
            {
                tools.add(inventory.getStackInSlot(i));
            }
        }
        Collections.reverse(tools);
        return tools;
    }
}