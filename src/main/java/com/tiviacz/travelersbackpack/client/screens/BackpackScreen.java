package com.tiviacz.travelersbackpack.client.screens;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.buttons.*;
import com.tiviacz.travelersbackpack.client.screens.widgets.*;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.sorter.ContainerSorter;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.item.upgrades.TanksUpgradeItem;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.util.FluidTypeHelper;
import com.tiviacz.travelersbackpack.util.KeyHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;

@Environment(EnvType.CLIENT)
public class BackpackScreen extends AbstractBackpackScreen<BackpackBaseMenu> implements MenuAccess<BackpackBaseMenu> {
    public boolean tanksVisible;
    public Map<Class<?>, WidgetBase<?>> mappedWidgets = new HashMap<>();
    public List<UpgradeSlot> upgradeSlots = new ArrayList<>();
    public List<IButton> buttons = new ArrayList<>();
    public SortingButtons sortingButtons;
    public ToolSlotsWidget toolSlotsWidget;
    public SettingsWidget settingsWidget;
    public int warningTicks = 0;

    public BackpackScreen(BackpackBaseMenu backpackMenu, Inventory inventory, Component component) {
        super(backpackMenu, inventory, backpackMenu.getWrapper().getBackpackScreenTitle());
        //Init getting called internally
        recalculate();

        //Update position
        updateBackpackSlotsPosition();
        updatePlayerSlotsPosition();

        this.titleLabelX = 8 + (tanksVisible ? 22 : 0);
    }

    @Override
    protected void init() {
        super.init();
        initButtons();
        initWidgets();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        //Reload screen
        updateScreen(true);
        getMenu().rebuildSlots();

        //Update position
        updateBackpackSlotsPosition();
        updatePlayerSlotsPosition();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if(this.warningTicks > 0) {
            this.warningTicks--;
        }
    }

    public void recalculate() {
        this.clearWidgets();
        upgradeSlots.clear();

        this.slotCount = getWrapper().getStorage().getSlots();
        this.visibleSlots = this.slotCount;
        this.slotsHeight = calculateSlotHeight(slotCount > 81);
        this.tanksVisible = getWrapper().tanksVisible();

        this.leftPos = 0;
        this.topPos = 0;

        boolean wideTexture = slotCount > 81;
        this.wider = wideTexture;
        this.visibleRows = (int)Math.ceil((double)this.slotCount / getSlotsInRow());
        int playerInventoryHeight = 96;
        this.imageWidth = wideTexture ? (tanksVisible ? 256 : 212) : (tanksVisible ? 220 : 176);
        this.imageHeight = TOP_BAR_OFFSET + this.slotsHeight + playerInventoryHeight + 1;

        updateDimensions();

        this.inventoryLabelY = 3 + TOP_BAR_OFFSET + (this.visibleRows * 18);
        this.inventoryLabelX = 8;
        this.titleLabelX = 8 + (tanksVisible ? 22 : 0);
        this.titleLabelY = 6;

        if(tanksVisible) {
            this.inventoryLabelX += 22;
        }

        if(wideTexture) {
            this.inventoryLabelX += 18;
        }

        //Cache first slot Y pos, ignore if not visible
        if(menu.getSlot(0).y >= 0) {
            this.slotYPos = menu.getSlot(0).y;
        }
    }

    public int getMiddleBar() {
        return TOP_BAR_OFFSET + calculateSlotHeight(this.visibleRows) + 1;
    }

    public int getWidthAdditions() {
        int addition = 0;
        if(tanksVisible) addition += 22;
        if(wider) addition += 18;
        return addition;
    }

    public void updateScreen(boolean duplicatedCall) {
        this.isScrollable = false;
        recalculate();
        init();

        //Update position
        if(!duplicatedCall) {
            updateBackpackSlotsPosition();
            updatePlayerSlotsPosition();
        }
    }

    @Override
    public void renderScreen(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
        //Render widgets below inventory
        renderUpgradeSlots(guiGraphics, x, y);

        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderBg(guiGraphics, x, y, mouseX, mouseY));

        boolean wideTexture = slotCount > 81;
        int inventoryXOffset = tanksVisible ? 22 : 0;
        renderInventoryBackground(guiGraphics, x + inventoryXOffset, y, wideTexture ? BACKGROUND_11 : BACKGROUND_9, imageWidth, this.slotsHeight);

        int slotsXOffset = 7;

        if(tanksVisible) {
            slotsXOffset = 29;

            int halfTankHeight = this.slotsHeight / 2;
            int tanksHeight = 90;
            int uOffset = 56;
            int posOffset = 193;
            if(wideTexture) {
                uOffset = 0;
                posOffset = 229;
            }

            //Left Tank
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TANKS, x, y, 0, 0, 27, TOP_BAR_OFFSET - 9 + halfTankHeight, 256, 256);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TANKS, x, y + TOP_BAR_OFFSET - 9 + halfTankHeight, uOffset, 256 - (tanksHeight + halfTankHeight + TOP_BAR_OFFSET), 27, tanksHeight + halfTankHeight + TOP_BAR_OFFSET - 9, 256, 256);

            //Right Tank
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TANKS, x + posOffset, y, uOffset + 28, 0, 27, TOP_BAR_OFFSET - 9 + halfTankHeight, 256, 256);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TANKS, x + posOffset, y + TOP_BAR_OFFSET - 9 + halfTankHeight, uOffset + 28, 256 - (tanksHeight + halfTankHeight + TOP_BAR_OFFSET), 27, tanksHeight + halfTankHeight + TOP_BAR_OFFSET - 9, 256, 256);
        }

        //Render Upgrades
        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderAboveBg(guiGraphics, x, y, mouseX, mouseY, partialTicks));

        renderSlots(guiGraphics, x + slotsXOffset, y + TOP_BAR_OFFSET, this.slotCount);
        renderLockedBackpackSlot(guiGraphics);

        //Render Buttons
        this.buttons.forEach(button -> {
            if(getWrapper().showMoreButtons() || button instanceof MoreButton || button instanceof EquipButton) {
                button.render(guiGraphics, mouseX, mouseY, partialTicks);
            }
        });
    }

    public void renderUpgradeSlots(GuiGraphicsExtractor guiGraphics, int x, int y) {
        for(UpgradeSlot slot : upgradeSlots) {
            slot.render(guiGraphics, x, y);
        }
    }

    public void initializeUpgradeSlots() {
        for(int i = 0; i < getWrapper().getUpgrades().getSlots(); i++) {
            int x = menu.upgradeSlot.get(i).x - 4;
            int y = menu.upgradeSlot.get(i).y - 4;
            upgradeSlots.add(new UpgradeSlot(getWrapper(), new Point(getGuiLeft() + x, getGuiTop() + y), i, x, y, menu.upgradeSlot.get(i).isHidden));
        }
    }

    @Override
    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget) {
        if(widget instanceof UpgradeWidgetBase<?> upgradeWidgetBase) {
            this.mappedWidgets.put(upgradeWidgetBase.getUpgrade().getClass(), upgradeWidgetBase);
        }
        return super.addRenderableWidget(widget);
    }

    public void initWidgets() {
        if(this.isScrollable) {
            int scrollXPos = leftPos + 7 + (tanksVisible ? 22 : 0);
            this.scroll = new InventoryScroll(this, Minecraft.getInstance(), 4, this.visibleRows * 18, topPos + TOP_BAR_OFFSET, scrollXPos + getSlotsInRow() * 18);
            if(this.scrollAmount != 0) {
                this.scroll.setScrollDistance(this.scrollAmount);
            }
            addRenderableWidget(this.scroll);
        }

        this.settingsWidget = new SettingsWidget(this, new Point(this.leftPos + this.imageWidth - 3, this.topPos + 4), false);
        addRenderableWidget(this.settingsWidget);

        int xPos = leftPos + (wider ? 36 : 0) + (tanksVisible ? 22 : 0) + 120;
        this.sortingButtons = new SortingButtons(this, new Point(xPos, topPos - 10 + 12), 50, 13);
        addRenderableWidget(this.sortingButtons);

        int xPosTools = (wider ? 36 : 0) + (tanksVisible ? 22 : 0);
        this.toolSlotsWidget = new ToolSlotsWidget(this, new Point(this.leftPos + xPosTools + 110, topPos - 10 + 15), xPosTools);
        addRenderableWidget(this.toolSlotsWidget);

        UpgradeManager manager = getWrapper().getUpgradeManager();

        for(int i : manager.mappedUpgrades.keySet()) {
            Optional<UpgradeBase<?>> upgrade = manager.mappedUpgrades.get(i);
            upgrade.ifPresent(loadedUpgrade -> {
                int x = menu.upgradeSlot.get(i).x - 4;
                int y = menu.upgradeSlot.get(i).y - 4;

                addRenderableWidget(loadedUpgrade.createWidget(this, x, y));
            });
        }

        initializeUpgradeSlots();
    }

    public void initButtons() {
        buttons.clear();
        int xOffset = 0;
        if(getWrapper().getScreenID() == Reference.ITEM_SCREEN_ID) {
            if(!TravelersBackpack.enableIntegration()) {
                buttons.add(new EquipButton(this, getWrapper().getBackpackSlotIndex() == getScreenPlayer().getInventory().getSelectedSlot()));
                xOffset += 12;
            }
        }
        if(getWrapper().getScreenID() == Reference.WEARABLE_SCREEN_ID && getWrapper().isOwner(getMenu().player)) {
            buttons.add(new MoreButton(this));

            if(!TravelersBackpack.enableIntegration()) {
                buttons.add(new UnequipButton(this));
                xOffset += 12;
            }

            if(TravelersBackpackConfig.SERVER.backpackSettings.quickSleepingBag.get()) {
                buttons.add(new SleepingBagButton(this, true, xOffset));
                xOffset += 12;
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, getWrapper().getBackpackStack())) {
                buttons.add(new AbilitySliderButton(this, false, xOffset));
            }
        }
        if(getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
            buttons.add(new MoreButton(this));
            buttons.add(new SleepingBagButton(this, false, 0));

            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, getWrapper().getBackpackStack())) {
                buttons.add(new AbilitySliderButton(this, true, 12));
            }
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.extractContents(guiGraphics, mouseX, mouseY, partialTicks);
        this.extractCarriedItem(guiGraphics, mouseX, mouseY);
        this.extractSnapbackItem(guiGraphics);

        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderUnderTooltip(guiGraphics, mouseX, mouseY, partialTicks));
        this.extractTooltip(guiGraphics, mouseX, mouseY);
        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderOnTop(guiGraphics, mouseX, mouseY, partialTicks));
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractTooltip(guiGraphics, mouseX, mouseY);
        this.buttons.forEach(button -> {
            if(getWrapper().showMoreButtons() || button instanceof MoreButton || button instanceof EquipButton) {
                button.renderTooltip(guiGraphics, mouseX, mouseY);
            }
        });
        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderTooltip(guiGraphics, mouseX, mouseY));

        renderFluidWarningTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void drawUnsortableSlots(GuiGraphicsExtractor guiGraphics) {
        if(!getWrapper().getUnsortableSlots().isEmpty()) {
            getWrapper().getUnsortableSlots().forEach(i -> guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ICONS, this.getGuiLeft() + getMenu().getSlot(i).x, this.getGuiTop() + getMenu().getSlot(i).y, 25, 55, 16, 16, 256, 256));
        }
    }

    @Override
    public void drawMemorySlots(GuiGraphicsExtractor guiGraphics) {
        if(!getWrapper().getMemorySlots().isEmpty()) {
            getWrapper().getMemorySlots().forEach(pair -> {
                if(getMenu().getSlot(pair.getFirst()).getItem().isEmpty()) {
                    ItemStack itemstack = pair.getSecond().getFirst();
                    guiGraphics.fakeItem(itemstack, this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y);
                    guiGraphics.fill(this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y, this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x + 16, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y + 16, 822083583);
                }
            });
        }
    }

    @Override
    protected boolean hasClickedOutside(double pMouseX, double pMouseY, int pGuiLeft, int pGuiTop) {
        if(!this.menu.getCarried().isEmpty()) {
            for(GuiEventListener widget : children()) {
                if(widget instanceof WidgetBase base) {
                    if(base.isMouseOver(pMouseX, pMouseY)) return false;
                }
            }
        }
        if(getHoveredSlot() != null) {
            return false;
        }
        return pMouseX < (double)pGuiLeft || pMouseY < (double)pGuiTop || pMouseX >= (double)(pGuiLeft + this.imageWidth) || pMouseY >= (double)(pGuiTop + this.imageHeight);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        this.buttons.forEach(b -> {
            if(getWrapper().showMoreButtons() || b instanceof MoreButton || b instanceof EquipButton) {
                b.mouseClicked(event, bl);
            }
        });
        if(KeybindHandler.SORT_BACKPACK.matchesMouse(event)) {
            ServerboundActionTagPacket.create(ServerboundActionTagPacket.SORTER, ContainerSorter.SORT_BACKPACK, KeyHelper.isShiftPressed());
            playUIClickSound();
            return true;
        }
        return super.mouseClicked(event, bl);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(getChildAt(mouseX, mouseY).isPresent()) {
            GuiEventListener child = getChildAt(mouseX, mouseY).get();
            if(child instanceof FilterUpgradeWidgetBase<?, ?> widget) {
                if(widget.getUpgrade().isTagSelector()) {
                    if(widget.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                        return true;
                    }
                }
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if(KeybindHandler.SORT_BACKPACK.matches(event)) {
            ServerboundActionTagPacket.create(ServerboundActionTagPacket.SORTER, ContainerSorter.SORT_BACKPACK, KeyHelper.isShiftPressed());
            playUIClickSound();
            return true;
        }
        if(KeybindHandler.OPEN_BACKPACK.matches(event)) {
            LocalPlayer playerEntity = this.minecraft.player;
            if(playerEntity != null && (hoveredSlot == null || !(hoveredSlot.getItem().getItem() instanceof TravelersBackpackItem))) {
                this.onClose();
            }
            return true;
        }
        return super.keyPressed(event);
    }

    public void addCompatWidget(AbstractWidget widget) {
        this.addRenderableWidget(widget);
    }

    public void removeCompatWidget(AbstractWidget widget) {
        this.removeWidget(widget);
    }

    public static void displayTanksUpgradeWarning(Player player) {
        if(player.level().isClientSide()) {
            if(Minecraft.getInstance().screen instanceof BackpackScreen screen) {
                screen.warningTicks = 60;
            }
        }
    }

    //Fabric

    public void renderFluidWarningTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if(warningTicks > 0) {
            if(!(menu.getCarried().getItem() instanceof TanksUpgradeItem)) {
                warningTicks = 0;
            }

            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("screen.travelersbackpack.cant_apply_upgrade"));
            tooltip.add(Component.translatable("screen.travelersbackpack.too_much_fluid"));
            FluidVariantWrapper leftFluidStack = TanksUpgradeItem.getLeftFluidStack(menu.getCarried());
            FluidVariantWrapper rightFluidStack = TanksUpgradeItem.getRightFluidStack(menu.getCarried());

            if(!leftFluidStack.isEmpty() && leftFluidStack.getAmount() > getWrapper().getBackpackTankCapacity()) {
                tooltip.add(createFluidWarning(leftFluidStack, getWrapper().getBackpackTankCapacity()));
            }

            if(!rightFluidStack.isEmpty() && rightFluidStack.getAmount() > getWrapper().getBackpackTankCapacity()) {
                tooltip.add(createFluidWarning(rightFluidStack, getWrapper().getBackpackTankCapacity()));
            }
            guiGraphics.setTooltipForNextFrame(getFont(), tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    public Component createFluidWarning(FluidVariantWrapper fluidVariantWrapper, long backpackCapacity) {
        return Component.literal(FluidTypeHelper.getFluidVariantName(fluidVariantWrapper.fluidVariant()).getString() + " " + fluidVariantWrapper.getViewAmount() + "/" + (backpackCapacity / 81) + "mB").withStyle(ChatFormatting.RED);
    }
}