package com.tiviacz.travelersbackpack.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WarningScreen extends Screen
{
    private final MainMenuScreen guiMainMenu;

    public WarningScreen(MainMenuScreen guiMainMenu)
    {
        super(new StringTextComponent("Warning"));
        this.guiMainMenu = guiMainMenu;
    }

    @Override
    public void init()
    {
        super.init();
        this.buttons.clear();
        this.addButton(new Button(this.width / 2 - 100, Math.min(this.height / 2 + 50 / 2 + 9, this.height - 30), 200, 20, new TranslationTextComponent("gui.done"), s -> this.minecraft.setScreen(this.guiMainMenu)));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);

        int y = this.height / 2 - 35;

        for(int i = 0; i < 5; i++)
        {
            String s = I18n.get("information." + TravelersBackpack.MODID + ".warning." + (i + 1));
            drawCenteredString(matrixStack, this.font, s, this.width / 2, y, 0xFFFFFF);
            y += 12;
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}