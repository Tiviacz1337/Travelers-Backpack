package com.tiviacz.travelersbackpack.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screen.buttons.*;
import com.tiviacz.travelersbackpack.client.screen.widget.*;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBaseScreenHandler;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class TravelersBackpackHandledScreen extends HandledScreen<TravelersBackpackBaseScreenHandler>
{
    public static final Identifier BACKGROUND_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/travelers_backpack_background.png");
    public static final Identifier SLOTS_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/travelers_backpack_slots.png");
    public static final Identifier SETTINGS_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/travelers_backpack_settings.png");
    public static final Identifier EXTRAS_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/travelers_backpack_extras.png");
    public List<IButton> buttons = new ArrayList<>();
    public ControlTab controlTab;
    public ToolSlotsWidget toolSlotsWidget;
    public SettingsWidget settingsWidget;
    public SortWidget sortWidget;
    public MemoryWidget memoryWidget;
    public TankSlotWidget leftTankSlotWidget;
    public TankSlotWidget rightTankSlotWidget;
    public CraftingWidget craftingWidget;

    public final ITravelersBackpackInventory inventory;
    private final TankScreen tankLeft;
    private final TankScreen tankRight;
    private boolean fluidSlotsAsWidget;
    private int rows;

    public TravelersBackpackHandledScreen(TravelersBackpackBaseScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        this.inventory = handler.inventory;

        this.x = 0;
        this.y = 0;

        this.backgroundWidth = 248;

        this.tankLeft = new TankScreen(handler.inventory.getLeftTank(), 25, 7, 52 + handler.inventory.getYOffset(), 16);
        this.tankRight = new TankScreen(handler.inventory.getRightTank(), 207, 7, 52 + handler.inventory.getYOffset(), 16);

        initScreen();
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
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
            this.leftTankSlotWidget = new TankSlotWidget(this, x, y, 28, 60);
            addSelectableChild(leftTankSlotWidget);

            this.rightTankSlotWidget = new TankSlotWidget(this, x + 220, y, 28, 60);
            addSelectableChild(rightTankSlotWidget);
        }
    }

    public void initControlTab()
    {
        this.controlTab = new ControlTab(this, x + 61, y - 10, 50, 13);
        addSelectableChild(controlTab);
    }

    public void initToolSlotsWidget()
    {
        this.toolSlotsWidget = new ToolSlotsWidget(this, x + 5, y - 15, 18, 15);
        addSelectableChild(toolSlotsWidget);
    }

    public void initSettingsTab()
    {
        this.settingsWidget = new SettingsWidget(this, x + backgroundWidth, y + 10, 15, 18);
        addSelectableChild(settingsWidget);
        this.sortWidget = new SortWidget(this, x + backgroundWidth, y + 29, 15, 18);
        addSelectableChild(sortWidget);
        this.memoryWidget = new MemoryWidget(this, x + backgroundWidth, y + 48, 15, 18);
        addSelectableChild(memoryWidget);
    }

    public void initCraftingWidget()
    {
        this.craftingWidget = new CraftingWidget(this, x + backgroundWidth, y + 29, 15, 18);
        addSelectableChild(craftingWidget);
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
        //this.page = 1;
        //this.rows.clear();
        // this.rows.add(0, Math.max(3, Math.min(container.getRows(), 7)));

        //if(this.container.getRows() > 7)
        // {
        //     this.rows.add(1, this.container.getRows() - 7);
        // }

        this.rows = Math.max(3, Math.min(inventory.getRows(), 7));
        this.fluidSlotsAsWidget = true;
        this.backgroundHeight = 153; //Minimal screen size (3 Rows)
        // this.pageCount = this.rows.size();

        if(rows > 3)
        {
            this.backgroundHeight = 153 + ((rows - 3) * 18);

            if(rows > 4)
            {
                this.fluidSlotsAsWidget = false;
            }
        }
    }

    public void drawBackground(MatrixStack matrices, int x, int y)
    {
        //Top bar
        RenderSystem.setShaderTexture(0, BACKGROUND_TRAVELERS_BACKPACK);
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, 5);

        //Tool slots addition
        if(this.rows < inventory.getToolSlotsInventory().size() && inventory.getSettingsManager().showToolSlots())
        {
            int sub = inventory.getToolSlotsInventory().size() - this.rows;
            RenderSystem.setShaderTexture(0, EXTRAS_TRAVELERS_BACKPACK);

            //Elements
            for(int i = 0; i < sub; i++)
            {
                this.drawTexture(matrices, x + 2, y + inventory.getYOffset() + 61 + (18 * i), 186, 61, 24, 18);
            }

            //Bottom bar
            this.drawTexture(matrices, x + 2, y + inventory.getYOffset() + 61 + (18 * sub), 186, 80, 24, 2);
        }

        //Rest of the background
        RenderSystem.setShaderTexture(0, BACKGROUND_TRAVELERS_BACKPACK);
        int offset = 5 + (Math.abs(this.rows - 7) * 18); //7 = Max rows
        this.drawTexture(matrices, x, y + 5, 0, offset, this.backgroundWidth, this.backgroundHeight - 5);

        //Slots
        if(TravelersBackpackConfig.getConfig().client.enableLegacyGui)
        {
            drawSlotsLegacy(matrices, x + 43, y + 6);
        }
        else
        {
            drawSlots(matrices, x + 43, y + 6);
        }

        //Tanks
        drawTank(matrices, x + 24, y);
        drawTank(matrices, x + 206, y);

        //Fluid Slots
        drawFluidSlot(matrices, x + 5, y + 6);
        drawFluidSlot(matrices, x + 225, y + 6);
    }

    public void drawSlots(MatrixStack matrices, int x, int y)
    {
        RenderSystem.setShaderTexture(0, SLOTS_TRAVELERS_BACKPACK);

        int rows = this.inventory.getRows();
        int additionalSlots = this.inventory.getInventory().size() % 9;

        //Draw full rows
        int gridX = 9 * 18;
        int gridY = (rows - 1) * 18;
        this.drawTexture(matrices, x, y, 43, 6, gridX, gridY);

        //Draw last row
        if(additionalSlots == 0) additionalSlots = 9;

        gridX = additionalSlots * 18;
        gridY = 18;
        this.drawTexture(matrices, x, y + 18 * (rows - 1), 43, 6, gridX, gridY);
    }

    public void drawTank(MatrixStack matrices, int x, int y)
    {
        RenderSystem.setShaderTexture(0, EXTRAS_TRAVELERS_BACKPACK);

        //Top segment
        this.drawTexture(matrices, x, y + 6, 232, 38, 18, 18);

        //Middle segment
        for(int i = 1; i <= this.rows - 2; i++)
        {
            this.drawTexture(matrices, x, y + 6 + (18 * i), 232, 57, 18, 18);
        }

        //Bottom segment
        this.drawTexture(matrices, x, y + 6 + (18 * (this.rows - 1)), 232, 76, 18, 18);
    }

    public void drawFluidSlot(MatrixStack matrices, int x, int y)
    {
        RenderSystem.setShaderTexture(0, EXTRAS_TRAVELERS_BACKPACK);
        this.drawTexture(matrices, x, y, 213, 38, 18, this.fluidSlotsAsWidget ? 18 : 48);
    }

    public void drawSlotsLegacy(MatrixStack matrices, int x, int y)
    {
        RenderSystem.setShaderTexture(0, EXTRAS_TRAVELERS_BACKPACK);

        int rows = this.inventory.getRows() - 1;
        int additionalSlots = this.inventory.getInventory().size() % 9;

        //Draw full rows
        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                drawSlotLegacy(matrices, x + (j * 18), y + (i * 18), 213, 0);
            }
        }

        //Draw last row
        if(additionalSlots == 0) additionalSlots = 9;

        for(int j = 0; j < additionalSlots; j++)
        {
            drawSlotLegacy(matrices, x + (j * 18), y + (rows * 18), 213, 0);
        }
    }

    public void drawSlotLegacy(MatrixStack matrices, int x, int y, int vOffset, int uWidth)
    {
        this.drawTexture(matrices, x, y, vOffset, uWidth, 18, 18);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {}

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.renderBackground(matrices);

        //Crafting widget so it renders under screen
        RenderSystem.setShaderTexture(0, SETTINGS_TRAVELERS_BACKPACK);
        this.craftingWidget.render(matrices, mouseX, mouseY, delta);

        super.render(matrices, mouseX, mouseY, delta);

        //Fluid Bars
        if(!this.inventory.getLeftTank().isResourceBlank())
        {
            this.tankLeft.drawScreenFluidBar(this, matrices);
        }
        if(!this.inventory.getRightTank().isResourceBlank())
        {
            this.tankRight.drawScreenFluidBar(this, matrices);
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, EXTRAS_TRAVELERS_BACKPACK);

        this.buttons.forEach(button -> button.render(matrices, mouseX, mouseY, delta));

        this.controlTab.render(matrices, mouseX, mouseY, delta);
        this.toolSlotsWidget.render(matrices, mouseX, mouseY, delta);

        if(this.fluidSlotsAsWidget)
        {
            this.leftTankSlotWidget.render(matrices, mouseX, mouseY, delta);
            this.rightTankSlotWidget.render(matrices, mouseX, mouseY, delta);
        }

        this.settingsWidget.render(matrices, mouseX, mouseY, delta);
        this.children().stream().filter(w -> w instanceof WidgetBase).filter(w -> ((WidgetBase) w).isSettingsChild() && ((WidgetBase) w).isVisible()).forEach(w -> ((WidgetBase) w).render(matrices, mouseX, mouseY, delta));

        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack matrices, int x, int y)
    {
        super.drawMouseoverTooltip(matrices, x, y);

        if(this.tankLeft.inTank(this, x, y))
        {
            this.renderTooltip(matrices, tankLeft.getTankTooltip(), x, y);
        }

        if(this.tankRight.inTank(this, x, y))
        {
            this.renderTooltip(matrices, tankRight.getTankTooltip(), x, y);
        }

        this.buttons.forEach(button -> button.drawMouseoverTooltip(matrices, x, y));

        this.craftingWidget.drawMouseoverTooltip(matrices, x, y);
    }

    public boolean isWidgetVisible(int rowsCount, TankSlotWidget widget)
    {
        if(widget == null)
        {
            return false;
        }
        return this.inventory.getRows() == rowsCount && widget.isVisible();
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        drawBackground(matrices, x, y);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, EXTRAS_TRAVELERS_BACKPACK);

        drawToolSlots(matrices);
        drawUnsortableSlots(matrices);
        drawMemorySlots(matrices);
    }

    public void drawToolSlots(MatrixStack matrices)
    {
        if(inventory.getSettingsManager().showToolSlots())
        {
            boolean enableLegacy = TravelersBackpackConfig.getConfig().client.enableLegacyGui;

            for(int i = 0; i < inventory.getToolSlotsInventory().size(); i++)
            {
                this.drawTexture(matrices, this.getX() + 5, this.getY() + 6 + (18 * i), 232, enableLegacy ? 0 : 19, 18, 18);

                if(!enableLegacy)
                {
                    this.drawTexture(matrices, this.getX() + 5, this.getY() + 6 + (18 * i), 76, 0, 18, 18);
                }
            }
        }
    }

    public void drawUnsortableSlots(MatrixStack matrices)
    {
        if(!inventory.getSlotManager().getUnsortableSlots().isEmpty() && !inventory.getSlotManager().isSelectorActive(SlotManager.MEMORY))
        {
            inventory.getSlotManager().getUnsortableSlots()
                    .forEach(i -> this.drawTexture(matrices, this.getX() + getScreenHandler().getSlot(i + 1).x, this.getY() + getScreenHandler().getSlot(i + 1).y, 77, 20, 16, 16));
        }
    }

    public void drawMemorySlots(MatrixStack matrices)
    {
        if(!inventory.getSlotManager().getMemorySlots().isEmpty())
        {
            inventory.getSlotManager().getMemorySlots()
                    .forEach(pair -> {

                        if(inventory.getSlotManager().isSelectorActive(SlotManager.MEMORY))
                        {
                            RenderSystem.setShaderTexture(0, EXTRAS_TRAVELERS_BACKPACK);
                            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                            this.drawTexture(matrices, this.getX() + getScreenHandler().getSlot(pair.getFirst() + 1).x, this.getY() + getScreenHandler().getSlot(pair.getFirst() + 1).y, 115, 24, 16, 16);
                        }

                        if(!getScreenHandler().getSlot(pair.getFirst() + 1).getStack().isEmpty()) return;

                        ItemStack itemstack = pair.getSecond();
                        itemRenderer.renderGuiItemIcon(itemstack, this.getX() + getScreenHandler().getSlot(pair.getFirst() + 1).x, this.getY() + getScreenHandler().getSlot(pair.getFirst() + 1).y);
                        RenderSystem.depthFunc(516);
                        fill(matrices, this.getX() + getScreenHandler().getSlot(pair.getFirst() + 1).x, this.getY() + getScreenHandler().getSlot(pair.getFirst() + 1).y, this.getX() + getScreenHandler().getSlot(pair.getFirst() + 1).x + 16, this.getY() + getScreenHandler().getSlot(pair.getFirst() + 1).y + 16, 0x30FFFFFF);
                        RenderSystem.depthFunc(515);
                    });
        }
    }

    @Override
    protected boolean isClickOutsideBounds(double pMouseX, double pMouseY, int pGuiLeft, int pGuiTop, int pMouseButton)
    {
        if(this.getSlotAt(pMouseX, pMouseY) != null)
        {
            return false;
        }

        if(!this.getScreenHandler().getCursorStack().isEmpty())
        {
            for(Element element : children())
            {
                if(element instanceof WidgetBase base)
                {
                    if(base.isMouseOver(pMouseX, pMouseY))
                    {
                        return false;
                    }
                }
            }
        }
        return pMouseX < (double)pGuiLeft || pMouseY < (double)pGuiTop || pMouseX >= (double)(pGuiLeft + this.backgroundWidth) || pMouseY >= (double)(pGuiTop + this.backgroundHeight);
    }

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType)
    {
        super.onMouseClick(slot, slotId, button, actionType);

        //Selecting or unselecting unsortable slots by clicking the single slot
        selectSlots(slot, button);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY)
    {
        Slot slot = this.focusedSlot;

        //Selecting or unselecting unsortable and memory slots by dragging mouse cursor
        selectSlots(slot, pButton);

        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    public void selectSlots(Slot slot, int button)
    {
        if(slot != null && slot.id >= 1 && slot.id <= inventory.getInventory().size())
        {
            if(inventory.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE))
            {
                if(button == 0 && !inventory.getSlotManager().isSlot(SlotManager.UNSORTABLE, slot.id - 1))
                {
                    inventory.getSlotManager().setUnsortableSlot(slot.id - 1);
                }

                if(button == 1 && inventory.getSlotManager().isSlot(SlotManager.UNSORTABLE, slot.id - 1))
                {
                    inventory.getSlotManager().setUnsortableSlot(slot.id - 1);
                }
            }

            else if(inventory.getSlotManager().isSelectorActive(SlotManager.MEMORY))
            {
                if(button == 0 && !inventory.getSlotManager().isSlot(SlotManager.MEMORY, slot.id - 1) && !slot.getStack().isEmpty())
                {
                    inventory.getSlotManager().setMemorySlot(slot.id - 1, slot.getStack());
                }

                if(button == 1 && inventory.getSlotManager().isSlot(SlotManager.MEMORY, slot.id - 1))
                {
                    inventory.getSlotManager().setMemorySlot(slot.id - 1, slot.getStack());
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if((inventory.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE) && !this.sortWidget.isMouseOver(mouseX, mouseY)) || (inventory.getSlotManager().isSelectorActive(SlotManager.MEMORY) && !this.memoryWidget.isMouseOver(mouseX, mouseY)))
        {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        //Emptying tank
        if(!inventory.getLeftTank().isResourceBlank())
        {
            if(this.tankLeft.inTank(this, (int)mouseX, (int)mouseY) && BackpackUtils.isShiftPressed())
            {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeByte(this.inventory.getScreenID()).writeByte(Reference.EMPTY_TANK).writeDouble(1.0D);

                ClientPlayNetworking.send(ModNetwork.SPECIAL_ACTION_ID, buf);

                if(inventory.getScreenID() == Reference.ITEM_SCREEN_ID) ServerActions.emptyTank(1, this.handler.playerInventory.player, inventory.getWorld(), inventory.getScreenID());
            }
        }

        //Emptying tank
        if(!inventory.getRightTank().isResourceBlank())
        {
            if(this.tankRight.inTank(this, (int)mouseX, (int)mouseY) && BackpackUtils.isShiftPressed())
            {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeByte(this.inventory.getScreenID()).writeByte(Reference.EMPTY_TANK).writeDouble(2.0D);

                ClientPlayNetworking.send(ModNetwork.SPECIAL_ACTION_ID, buf);

                if(inventory.getScreenID() == Reference.ITEM_SCREEN_ID) ServerActions.emptyTank(2, this.handler.playerInventory.player, inventory.getWorld(), inventory.getScreenID());
            }
        }

        this.buttons.forEach(b -> b.mouseClicked(mouseX, mouseY, button));

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void playUIClickSound()
    {
        this.handler.playerInventory.player.world.playSound(this.handler.playerInventory.player, this.handler.playerInventory.player.getBlockPos(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 0.25F, 1.0F);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(KeybindHandler.OPEN_INVENTORY.matchesKey(keyCode, scanCode))
        {
            ClientPlayerEntity playerEntity = this.client.player;

            if(playerEntity != null)
            {
                this.close();
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}