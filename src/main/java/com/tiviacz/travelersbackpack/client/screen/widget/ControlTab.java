package com.tiviacz.travelersbackpack.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.sorter.InventorySorter;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.List;

public class ControlTab extends WidgetBase
{
    public ControlTab(TravelersBackpackHandledScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        this.isVisible = true;
    }

    @Override
    void drawBackground(MatrixStack matrixStack, MinecraftClient minecraft, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TravelersBackpackHandledScreen.EXTRAS_TRAVELERS_BACKPACK);

        if(isVisible())
        {
            drawTexture(matrixStack, x, y, 133, 0, width, height);

            if(isButtonHovered(mouseX, mouseY, Buttons.SORT))
            {
                drawTexture(matrixStack, x + 4, y + 4, 137, 18, 9, 9);
            }

            if(isButtonHovered(mouseX, mouseY, Buttons.QUICK_STACK))
            {
                drawTexture(matrixStack, x + 15, y + 4, 148, 18, 9, 9);
            }

            if(isButtonHovered(mouseX, mouseY, Buttons.TRANSFER_TO_BACKPACK))
            {
                drawTexture(matrixStack, x + 26, y + 4, 159, 18, 9, 9);
            }

            if(isButtonHovered(mouseX, mouseY, Buttons.TRANSFER_TO_PLAYER))
            {
                drawTexture(matrixStack, x + 37, y + 4, 170, 18, 9, 9);
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    void drawMouseoverTooltip(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if(BackpackUtils.isShiftPressed())
        {
            if(isButtonHovered(mouseX, mouseY, Buttons.SORT))
            {
                List<Text> list = new ArrayList<>();
                list.add(new TranslatableText("screen.travelersbackpack.sort"));

                screen.renderTooltip(matrixStack, list, mouseX, mouseY);
            }

            if(isButtonHovered(mouseX, mouseY, Buttons.QUICK_STACK))
            {
                List<Text> list = new ArrayList<>();
                list.add(new TranslatableText("screen.travelersbackpack.quick_stack"));
                list.add(new TranslatableText("screen.travelersbackpack.quick_stack_shift"));

                screen.renderTooltip(matrixStack, list, mouseX, mouseY);
            }

            if(isButtonHovered(mouseX, mouseY, Buttons.TRANSFER_TO_BACKPACK))
            {
                List<Text> list = new ArrayList<>();
                list.add(new TranslatableText("screen.travelersbackpack.transfer_to_backpack"));
                list.add(new TranslatableText("screen.travelersbackpack.transfer_to_backpack_shift"));

                screen.renderTooltip(matrixStack, list, mouseX, mouseY);
            }

            if(isButtonHovered(mouseX, mouseY, Buttons.TRANSFER_TO_PLAYER))
            {
                screen.renderTooltip(matrixStack, new TranslatableText("screen.travelersbackpack.transfer_to_player"), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean isSettingsChild()
    {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(screen.inventory.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE) || screen.inventory.getSlotManager().isSelectorActive(SlotManager.MEMORY))
        {
            return false;
        }

        if(isButtonHovered((int)mouseX, (int)mouseY, Buttons.SORT))
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeByte(screen.inventory.getScreenID()).writeByte(InventorySorter.SORT_BACKPACK).writeBoolean(BackpackUtils.isShiftPressed());
            ClientPlayNetworking.send(ModNetwork.SORTER_ID, buf);

            screen.playUIClickSound();
            return true;
        }

        if(isButtonHovered((int)mouseX, (int)mouseY, Buttons.QUICK_STACK))
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeByte(screen.inventory.getScreenID()).writeByte(InventorySorter.QUICK_STACK).writeBoolean(BackpackUtils.isShiftPressed());

            ClientPlayNetworking.send(ModNetwork.SORTER_ID, buf);
            screen.playUIClickSound();
            return true;
        }

        if(isButtonHovered((int)mouseX, (int)mouseY, Buttons.TRANSFER_TO_BACKPACK))
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeByte(screen.inventory.getScreenID()).writeByte(InventorySorter.TRANSFER_TO_BACKPACK).writeBoolean(BackpackUtils.isShiftPressed());

            ClientPlayNetworking.send(ModNetwork.SORTER_ID, buf);
            screen.playUIClickSound();
            return true;
        }

        if(isButtonHovered((int)mouseX, (int)mouseY, Buttons.TRANSFER_TO_PLAYER))
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeByte(screen.inventory.getScreenID()).writeByte(InventorySorter.TRANSFER_TO_PLAYER).writeBoolean(BackpackUtils.isShiftPressed());

            ClientPlayNetworking.send(ModNetwork.SORTER_ID, buf);
            screen.playUIClickSound();
            return true;
        }
        return false;
    }

    public boolean isButtonHovered(int mouseX, int mouseY, Buttons button)
    {
        return (65 + button.ordinal() * 11) + screen.getX() <= mouseX && mouseX <= (65 + button.ordinal() * 11) + 8 + screen.getX() && -6 + screen.getY() <= mouseY && mouseY <= -6 + 8 + screen.getY();
    }

    public enum Buttons
    {
        SORT,
        QUICK_STACK,
        TRANSFER_TO_BACKPACK,
        TRANSFER_TO_PLAYER
    }
}