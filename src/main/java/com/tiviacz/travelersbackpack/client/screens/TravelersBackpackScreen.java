package com.tiviacz.travelersbackpack.client.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.buttons.*;
import com.tiviacz.travelersbackpack.client.screens.widgets.*;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.ModClientEventsHandler;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.network.ServerboundSpecialActionPacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackScreen extends AbstractContainerScreen<TravelersBackpackBaseMenu> implements MenuAccess<TravelersBackpackBaseMenu>
{
    public static final ResourceLocation BACKGROUND_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack_background.png");
    public static final ResourceLocation SLOTS_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack_slots.png");
    public static final ResourceLocation SETTINGS_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack_settings.png");
    public static final ResourceLocation EXTRAS_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack_extras.png");
    public List<IButton> buttons = new ArrayList<>();
    public ControlTab controlTab;
    public ToolSlotsWidget toolSlotsWidget;
    public SettingsWidget settingsWidget;
    public SortWidget sortWidget;
    public MemoryWidget memoryWidget;
    public TankSlotWidget leftTankSlotWidget;
    public TankSlotWidget rightTankSlotWidget;
    public CraftingWidget craftingWidget;

    public final ITravelersBackpackContainer container;
    private final TankScreen tankLeft;
    private final TankScreen tankRight;

    private boolean fluidSlotsAsWidget;
    //private List<Integer> rows = new ArrayList<>();
    private int rows;
    //public int pageCount;
    //public int page;

    public TravelersBackpackScreen(TravelersBackpackBaseMenu screenContainer, Inventory inventory, Component component)
    {
        super(screenContainer, inventory, component);
        this.container = screenContainer.container;

        this.leftPos = 0;
        this.topPos = 0;

        this.imageWidth = 248;

        this.tankLeft = new TankScreen(container.getLeftTank(), 25, 7, 52 + container.getYOffset(), 16);
        this.tankRight = new TankScreen(container.getRightTank(), 207, 7, 52 + container.getYOffset(), 16);

        initScreen();
    }

    public Font getFont()
    {
        return this.font;
    }

    @Override
    protected void init()
    {
        super.init();
        initControlTab();
        initToolSlotsWidget();
        initSettingsTab();
        initTankSlotWidgets();
        initCraftingWidget();
        initButtons();
    }

    public void initTankSlotWidgets()
    {
        if(this.fluidSlotsAsWidget)
        {
            this.leftTankSlotWidget = new TankSlotWidget(this, leftPos, topPos, 28, 60);
            addWidget(leftTankSlotWidget);

            this.rightTankSlotWidget = new TankSlotWidget(this, leftPos + 220, topPos, 28, 60);
            addWidget(rightTankSlotWidget);
        }
    }

    public void initControlTab()
    {
        this.controlTab = new ControlTab(this, leftPos + 61, topPos - 10, 50, 13);
        addWidget(controlTab);
    }

    public void initToolSlotsWidget()
    {
        this.toolSlotsWidget = new ToolSlotsWidget(this, leftPos + 5, topPos - 15, 18, 15);
        addWidget(toolSlotsWidget);
    }

    public void initSettingsTab()
    {
        this.settingsWidget = new SettingsWidget(this, leftPos + imageWidth, topPos + 10, 15, 18);
        addWidget(settingsWidget);
        this.sortWidget = new SortWidget(this, leftPos + imageWidth, topPos + 29, 15, 18);
        addWidget(sortWidget);
        this.memoryWidget = new MemoryWidget(this, leftPos + imageWidth, topPos + 48, 15, 18);
        addWidget(memoryWidget);
    }

    public void initCraftingWidget()
    {
        this.craftingWidget = new CraftingWidget(this, leftPos + imageWidth, topPos + 29, 15, 18);
        addWidget(craftingWidget);
    }

    public void initButtons()
    {
        buttons.clear();
        buttons.add(new SleepingBagButton(this));
        buttons.add(new EquipButton(this));
        buttons.add(new UnequipButton(this));
        buttons.add(new AbilitySliderButton(this));
    }

    public void initScreen()
    {
        this.rows = Math.max(3, Math.min(container.getRows(), 7));
        this.fluidSlotsAsWidget = true;
        this.imageHeight = 153; //Minimal screen size (3 Rows)

        if(rows > 3)
        {
            this.imageHeight = 153 + ((rows - 3) * 18);

            if(rows > 4)
            {
                this.fluidSlotsAsWidget = false;
            }
        }
    }

    public void drawBackground(GuiGraphics guiGraphics, int x, int y)
    {
        //Top bar
        guiGraphics.blit(BACKGROUND_TRAVELERS_BACKPACK, x, y, 0, 0, this.imageWidth, 5);

        //Tool slots addition
        if(this.rows < container.getToolSlotsHandler().getSlots() && container.getSettingsManager().showToolSlots())
        {
            int sub = container.getToolSlotsHandler().getSlots() - this.rows;

            //Elements
            for(int i = 0; i < sub; i++)
            {
                guiGraphics.blit(EXTRAS_TRAVELERS_BACKPACK, x + 2, y + container.getYOffset() + 61 + (18 * i), 186, 61, 24, 18);
            }

            //Bottom bar
            guiGraphics.blit(EXTRAS_TRAVELERS_BACKPACK, x + 2, y + container.getYOffset() + 61 + (18 * sub), 186, 80, 24, 2);
        }

        //Rest of the background
        int offset = 5 + (Math.abs(this.rows - 7) * 18); //7 = Max rows
        guiGraphics.blit(BACKGROUND_TRAVELERS_BACKPACK, x, y + 5, 0, offset, this.imageWidth, this.imageHeight - 5);

        //Slots
        if(TravelersBackpackConfig.enableLegacyGui)
        {
            drawSlotsLegacy(guiGraphics, x + 43, y + 6);
        }
        else
        {
            drawSlots(guiGraphics, x + 43, y + 6);
        }

        //Tanks
        drawTank(guiGraphics, x + 24, y);
        drawTank(guiGraphics, x + 206, y);

        //Fluid Slots
        drawFluidSlot(guiGraphics, x + 5, y + 6);
        drawFluidSlot(guiGraphics, x + 225, y + 6);
    }

    public void drawSlots(GuiGraphics guiGraphics, int x, int y)
    {
        int rows = this.container.getRows();
        int additionalSlots = this.container.getHandler().getSlots() % 9;

        //Draw full rows
        int gridX = 9 * 18;
        int gridY = (rows - 1) * 18;
        guiGraphics.blit(SLOTS_TRAVELERS_BACKPACK, x, y, 43, 6, gridX, gridY);

        //Draw last row
        if(additionalSlots == 0) additionalSlots = 9;

        gridX = additionalSlots * 18;
        gridY = 18;
        guiGraphics.blit(SLOTS_TRAVELERS_BACKPACK, x, y + 18 * (rows - 1), 43, 6, gridX, gridY);
    }

    public void drawTank(GuiGraphics guiGraphics, int x, int y)
    {
        //Top segment
        guiGraphics.blit(EXTRAS_TRAVELERS_BACKPACK, x, y + 6, 232, 38, 18, 18);

        //Middle segment
        for(int i = 1; i <= this.rows - 2; i++)
        {
            guiGraphics.blit(EXTRAS_TRAVELERS_BACKPACK, x, y + 6 + (18 * i), 232, 57, 18, 18);
        }

        //Bottom segment
        guiGraphics.blit(EXTRAS_TRAVELERS_BACKPACK, x, y + 6 + (18 * (this.rows - 1)), 232, 76, 18, 18);
    }

    public void drawFluidSlot(GuiGraphics guiGraphics, int x, int y)
    {
        guiGraphics.blit(EXTRAS_TRAVELERS_BACKPACK, x, y, 213, 38, 18, this.fluidSlotsAsWidget ? 18 : 48);
    }

    public void drawSlotsLegacy(GuiGraphics guiGraphics, int x, int y)
    {
        int rows = this.container.getRows() - 1;
        int additionalSlots = this.container.getHandler().getSlots() % 9;

        //Draw full rows
        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                drawSlotLegacy(guiGraphics, x + (j * 18), y + (i * 18), 213, 0);
            }
        }

        //Draw last row
        if(additionalSlots == 0) additionalSlots = 9;

        for(int j = 0; j < additionalSlots; j++)
        {
            drawSlotLegacy(guiGraphics, x + (j * 18), y + (rows * 18), 213, 0);
        }
    }

    public void drawSlotLegacy(GuiGraphics guiGraphics, int x, int y, int vOffset, int uWidth)
    {
        guiGraphics.blit(EXTRAS_TRAVELERS_BACKPACK, x, y, vOffset, uWidth, 18, 18);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        //Fluid Bars
        if(!this.container.getLeftTank().isEmpty())
        {
            this.tankLeft.drawScreenFluidBar(this, guiGraphics);
        }
        if(!this.container.getRightTank().isEmpty())
        {
            this.tankRight.drawScreenFluidBar(this, guiGraphics);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        this.buttons.forEach(button -> button.render(guiGraphics, mouseX, mouseY, partialTicks));

        this.controlTab.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.toolSlotsWidget.render(guiGraphics, mouseX, mouseY, partialTicks);

        if(this.fluidSlotsAsWidget)
        {
            this.leftTankSlotWidget.render(guiGraphics, mouseX, mouseY, partialTicks);
            this.rightTankSlotWidget.render(guiGraphics, mouseX, mouseY, partialTicks);
        }

        this.settingsWidget.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.children().stream().filter(w -> w instanceof WidgetBase).filter(w -> ((WidgetBase) w).isSettingsChild() && ((WidgetBase) w).isVisible()).forEach(w -> ((WidgetBase) w).render(guiGraphics, mouseX, mouseY, partialTicks));

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY)
    {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if(this.tankLeft.inTank(this, mouseX, mouseY))
        {
            guiGraphics.renderComponentTooltip(font, tankLeft.getTankTooltip(container.getLevel()), mouseX, mouseY);
        }

        if(this.tankRight.inTank(this, mouseX, mouseY))
        {
            guiGraphics.renderComponentTooltip(font, tankRight.getTankTooltip(container.getLevel()), mouseX, mouseY);
        }

        this.buttons.forEach(button -> button.renderTooltip(guiGraphics, mouseX, mouseY));

        this.craftingWidget.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public boolean isWidgetVisible(int rowsCount, TankSlotWidget widget)
    {
        return this.container.getRows() == rowsCount && widget.isVisible();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY)
    {
        this.craftingWidget.render(guiGraphics, mouseX, mouseY, partialTicks);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        drawBackground(guiGraphics, x, y);

        drawToolSlots(guiGraphics);
        drawUnsortableSlots(guiGraphics);
        drawMemorySlots(guiGraphics);
    }

    public void drawToolSlots(GuiGraphics guiGraphics)
    {
        if(container.getSettingsManager().showToolSlots())
        {
            boolean enableLegacy = TravelersBackpackConfig.enableLegacyGui;

            for(int i = 0; i < container.getToolSlotsHandler().getSlots(); i++)
            {
                guiGraphics.blit(EXTRAS_TRAVELERS_BACKPACK, this.getGuiLeft() + 5, this.getGuiTop() + 6 + (18 * i), 232, enableLegacy ? 0 : 19, 18, 18);

                if(!enableLegacy)
                {
                    guiGraphics.blit(EXTRAS_TRAVELERS_BACKPACK, this.getGuiLeft() + 5, this.getGuiTop() + 6 + (18 * i), 76, 0, 18, 18);
                }
            }
        }
    }

    public void drawUnsortableSlots(GuiGraphics guiGraphics)
    {
        if(!container.getSlotManager().getUnsortableSlots().isEmpty() && !container.getSlotManager().isSelectorActive(SlotManager.MEMORY))
        {
            container.getSlotManager().getUnsortableSlots()
                    .forEach(i -> guiGraphics.blit(EXTRAS_TRAVELERS_BACKPACK, this.getGuiLeft() + getMenu().getSlot(i + 1).x, this.getGuiTop() + getMenu().getSlot(i + 1).y, 77, 20, 16, 16));
        }
    }

    public void drawMemorySlots(GuiGraphics guiGraphics)
    {
        if(!container.getSlotManager().getMemorySlots().isEmpty())
        {
            container.getSlotManager().getMemorySlots()
                    .forEach(pair -> {

                        if(container.getSlotManager().isSelectorActive(SlotManager.MEMORY))
                        {
                            guiGraphics.blit(EXTRAS_TRAVELERS_BACKPACK, this.getGuiLeft() + getMenu().getSlot(pair.getFirst() + 1).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst() + 1).y, 115, 24, 16, 16);
                        }

                        if(!menu.getSlot(pair.getFirst() + 1).getItem().isEmpty()) return;

                        ItemStack itemstack = pair.getSecond();
                        guiGraphics.renderFakeItem(itemstack, this.getGuiLeft() + getMenu().getSlot(pair.getFirst() + 1).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst() + 1).y);
                        guiGraphics.fill(RenderType.guiGhostRecipeOverlay(), this.getGuiLeft() + getMenu().getSlot(pair.getFirst() + 1).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst() + 1).y, this.getGuiLeft() + getMenu().getSlot(pair.getFirst() + 1).x + 16, this.getGuiTop() + getMenu().getSlot(pair.getFirst() + 1).y + 16, 822083583);
                    });
        }
    }

    @Override
    protected boolean hasClickedOutside(double pMouseX, double pMouseY, int pGuiLeft, int pGuiTop, int pMouseButton)
    {
        if(!this.menu.getCarried().isEmpty())
        {
            for(GuiEventListener widget : children())
            {
                if(widget instanceof WidgetBase base)
                {
                    if(base.isMouseOver(pMouseX, pMouseY)) return false;
                }
            }
        }
        return pMouseX < (double)pGuiLeft || pMouseY < (double)pGuiTop || pMouseX >= (double)(pGuiLeft + this.imageWidth) || pMouseY >= (double)(pGuiTop + this.imageHeight);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int button, ClickType type)
    {
        super.slotClicked(slot, slotId, button, type);

        //Selecting or unselecting unsortable slots by clicking the single slot
        selectSlots(slot, button);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY)
    {
        Slot slot = this.getSlotUnderMouse();

        //Selecting or unselecting unsortable and memory slots by dragging mouse cursor
        selectSlots(slot, pButton);

        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    public void selectSlots(Slot slot, int button)
    {
        if(slot != null && slot.index >= 1 && slot.index <= container.getHandler().getSlots())
        {
            if(container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE))
            {
                if(button == 0 && !container.getSlotManager().isSlot(SlotManager.UNSORTABLE, slot.index - 1))
                {
                    container.getSlotManager().setUnsortableSlot(slot.index - 1);
                }

                if(button == 1 && container.getSlotManager().isSlot(SlotManager.UNSORTABLE, slot.index - 1))
                {
                    container.getSlotManager().setUnsortableSlot(slot.index - 1);
                }
            }

            else if(container.getSlotManager().isSelectorActive(SlotManager.MEMORY))
            {
                if(button == 0 && !container.getSlotManager().isSlot(SlotManager.MEMORY, slot.index - 1) && !slot.getItem().isEmpty())
                {
                    container.getSlotManager().setMemorySlot(slot.index - 1, slot.getItem());
                }

                if(button == 1 && container.getSlotManager().isSlot(SlotManager.MEMORY, slot.index - 1))
                {
                    container.getSlotManager().setMemorySlot(slot.index - 1, slot.getItem());
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if((container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE) && !this.sortWidget.isMouseOver(mouseX, mouseY)) || (container.getSlotManager().isSelectorActive(SlotManager.MEMORY) && !this.memoryWidget.isMouseOver(mouseX, mouseY)))
        {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        //Emptying tank
        if(!container.getLeftTank().isEmpty())
        {
            if(this.tankLeft.inTank(this, (int)mouseX, (int)mouseY) && BackpackUtils.isShiftPressed())
            {
                //TravelersBackpack.NETWORK.send(new ServerboundSpecialActionPacket(container.getScreenID(), Reference.EMPTY_TANK, 1), PacketDistributor.SERVER.noArg());
                PacketDistributor.SERVER.noArg().send(new ServerboundSpecialActionPacket(container.getScreenID(), Reference.EMPTY_TANK, 1));
                if(container.getScreenID() == Reference.ITEM_SCREEN_ID) ServerActions.emptyTank(1, menu.inventory.player, container.getLevel(), container.getScreenID());
            }
        }

        //Emptying tank
        if(!container.getRightTank().isEmpty())
        {
            if(this.tankRight.inTank(this, (int)mouseX, (int)mouseY) && BackpackUtils.isShiftPressed())
            {
                //TravelersBackpack.NETWORK.send(new ServerboundSpecialActionPacket(container.getScreenID(), Reference.EMPTY_TANK, 2), PacketDistributor.SERVER.noArg());
                PacketDistributor.SERVER.noArg().send(new ServerboundSpecialActionPacket(container.getScreenID(), Reference.EMPTY_TANK, 2));
                if(container.getScreenID() == Reference.ITEM_SCREEN_ID) ServerActions.emptyTank(2, menu.inventory.player, container.getLevel(), container.getScreenID());
            }
        }

        this.buttons.forEach(b -> b.mouseClicked(mouseX, mouseY, button));

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void playUIClickSound()
    {
        menu.inventory.player.level().playSound(menu.inventory.player, menu.inventory.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.MASTER, 0.25F, 1.0F);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers)
    {
        if(ModClientEventsHandler.OPEN_BACKPACK.isActiveAndMatches(InputConstants.getKey(pKeyCode, pScanCode)))
        {
            LocalPlayer playerEntity = this.getMinecraft().player;

            if(playerEntity != null)
            {
                this.onClose();
            }
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
}