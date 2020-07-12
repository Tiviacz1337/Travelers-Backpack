package com.tiviacz.travelersbackpack.gui;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiWarning extends GuiScreen
{
    private final GuiMainMenu guiMainMenu;

    public GuiWarning(GuiMainMenu guiMainMenu)
    {
        this.guiMainMenu = guiMainMenu;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height - 30, I18n.format("gui.done")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();

        int x = this.width / 2;
        int y = this.height / 4;

        for(int i = 0; i < 6; i++)
        {
            String s = I18n.format("information." + TravelersBackpack.MODID + ".warning." + (i + 1));
            this.drawCenteredString(this.fontRenderer, s, x, y, 0xFFFFFF);
            y += 12;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if(button.id == 0)
        {
            Minecraft.getMinecraft().displayGuiScreen(this.guiMainMenu);
        }
    }
}
