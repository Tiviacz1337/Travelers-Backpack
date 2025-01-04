package com.tiviacz.travelersbackpack.client.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.buttons.*;
import com.tiviacz.travelersbackpack.client.screens.widgets.SettingsWidget;
import com.tiviacz.travelersbackpack.client.screens.widgets.SortingButtons;
import com.tiviacz.travelersbackpack.client.screens.widgets.ToolSlotsWidget;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.sorter.ContainerSorter;
import com.tiviacz.travelersbackpack.inventory.upgrades.IUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.items.upgrades.TanksUpgradeItem;
import com.tiviacz.travelersbackpack.network.ServerboundSorterPacket;
import com.tiviacz.travelersbackpack.util.BackpackDeathHelper;
import com.tiviacz.travelersbackpack.util.PacketDistributorHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class BackpackScreen extends AbstractContainerScreen<BackpackBaseMenu> implements MenuAccess<BackpackBaseMenu>, IBackpackScreen {
    public static final ResourceLocation BACKGROUND_11 = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/background_11.png");
    public static final ResourceLocation BACKGROUND_9 = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/background_9.png");
    public static final ResourceLocation SLOTS = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/slots.png");
    public static final ResourceLocation TANKS = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/tanks.png");
    public static final ResourceLocation ICONS = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/icons.png");
    public static final ResourceLocation TABS = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/tabs.png");
    public static final int TOP_BAR_OFFSET = 7;
    public int slotCount;
    public boolean tanksVisible;
    public int upgradeSlotCount;
    public List<UpgradeSlot> upgradeSlots = new ArrayList<>();
    boolean upgradesInitialized = false;
    boolean wider = false;
    public List<IButton> buttons = new ArrayList<>();
    public SortingButtons sortingButtons;
    public ToolSlotsWidget toolSlotsWidget;
    public SettingsWidget settingsWidget;
    private final BackpackWrapper wrapper;
    public int warningTicks = 0;

    public BackpackScreen(BackpackBaseMenu backpackMenu, Inventory inventory, Component component) {
        super(backpackMenu, inventory, component);
        this.wrapper = backpackMenu.getWrapper();
        recalculate();
    }

    @Override
    public BackpackWrapper getWrapper() {
        return this.wrapper;
    }

    @Override
    public Player getScreenPlayer() {
        return getMenu().player;
    }

    @Override
    protected void init() {
        super.init();
        initButtons();
        initWidgets();
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
        this.tanksVisible = getWrapper().tanksVisible();
        this.upgradeSlotCount = getWrapper().getUpgrades().getSlots();

        this.leftPos = 0;
        this.topPos = 0;

        boolean wideTexture = slotCount > 81;
        wider = wideTexture;
        int playerInventoryHeight = 96;
        this.imageWidth = wideTexture ? (tanksVisible ? 256 : 212) : (tanksVisible ? 220 : 176);
        this.imageHeight = TOP_BAR_OFFSET + calculateSlotHeight(wideTexture) + playerInventoryHeight;

        this.inventoryLabelY = this.imageHeight - 93;
        this.inventoryLabelX = 8;

        if(tanksVisible) {
            this.inventoryLabelX += 22;
        }

        if(wideTexture) {
            this.inventoryLabelX += 18;
        }
    }

    public int getImageHeight() {
        return this.imageHeight;
    }

    public int getWidthAdditions() {
        int addition = 0;
        if(tanksVisible) addition += 22;
        if(wider) addition += 18;
        return addition;
    }

    public void updateScreen() {
        recalculate();
        init();
    }

    public void renderInventoryBackground(GuiGraphics guiGraphics, int x, int y, ResourceLocation texture, int xSize, int slotsHeight) {
        int halfSlotHeight = slotsHeight / 2;
        guiGraphics.blit(texture, x, y, 0, 0, xSize, TOP_BAR_OFFSET + halfSlotHeight);
        int playerInventoryHeight = 97;
        guiGraphics.blit(texture, x, y + TOP_BAR_OFFSET + halfSlotHeight, 0, 256 - (playerInventoryHeight + halfSlotHeight), xSize, playerInventoryHeight + halfSlotHeight);
    }

    public void renderSlots(GuiGraphics guiGraphics, int x, int y, int slotCount, int slotsInRow) {
        int lastSlotRow = slotCount % slotsInRow;
        int fullRows = slotCount / slotsInRow;

        //Full Rows
        guiGraphics.blit(SLOTS, x, y, 0, 0, slotsInRow * 18, fullRows * 18);

        //Last Row
        if(lastSlotRow > 0) {
            guiGraphics.blit(SLOTS, x, y + fullRows * 18, 0, fullRows * 18, lastSlotRow * 18, 18);
        }
    }

    public void renderScreen(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks) {

        //Render widgets below inventory
        renderUpgradeSlots(guiGraphics, x, y);

        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderBg(guiGraphics, x, y, mouseX, mouseY));

        boolean wideTexture = slotCount > 81;
        int inventoryXOffset = tanksVisible ? 22 : 0;
        renderInventoryBackground(guiGraphics, x + inventoryXOffset, y, wideTexture ? BACKGROUND_11 : BACKGROUND_9, imageWidth, calculateSlotHeight(wideTexture));

        int slotsXOffset = 7;

        if(tanksVisible) {
            slotsXOffset = 29;

            int halfTankHeight = calculateSlotHeight(wideTexture) / 2;
            int tanksHeight = 90;
            int uOffset = 56;
            int posOffset = 193;
            if(wideTexture) {
                uOffset = 0;
                posOffset = 229;
            }

            //Left Tank
            guiGraphics.blit(TANKS, x, y, 0, 0, 27, TOP_BAR_OFFSET + halfTankHeight);
            guiGraphics.blit(TANKS, x, y + TOP_BAR_OFFSET + halfTankHeight, uOffset, 256 - (tanksHeight + halfTankHeight + TOP_BAR_OFFSET), 27, tanksHeight + halfTankHeight + TOP_BAR_OFFSET);

            //Right Tank
            guiGraphics.blit(TANKS, x + posOffset, y, uOffset + 28, 0, 27, TOP_BAR_OFFSET + halfTankHeight);
            guiGraphics.blit(TANKS, x + posOffset, y + TOP_BAR_OFFSET + halfTankHeight, uOffset + 28, 256 - (tanksHeight + halfTankHeight + TOP_BAR_OFFSET), 27, tanksHeight + halfTankHeight + TOP_BAR_OFFSET);
        }

        //Render Upgrades
        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderAboveBg(guiGraphics, x, y, mouseX, mouseY, partialTicks));

        renderSlots(guiGraphics, x + slotsXOffset, y + TOP_BAR_OFFSET, slotCount, wideTexture ? 11 : 9);
    }

    public int calculateSlotHeight(boolean wider) {
        int rowSlots = wider ? 11 : 9;
        int rows = (int)Math.ceil((double)slotCount / rowSlots);
        return rows * 18;
    }

    public void renderUpgradeSlots(GuiGraphics guiGraphics, int x, int y) {
        for(UpgradeSlot slot : upgradeSlots) {
            slot.render(guiGraphics, x, y);
        }
    }

    public void initializeUpgradeSlots() {
        for(int i = 0; i < upgradeSlotCount; i++) {
            int x = menu.upgradeSlot.get(i).x - 4;
            int y = menu.upgradeSlot.get(i).y - 4;
            upgradeSlots.add(new UpgradeSlot(getWrapper().getUpgrades(), new Point(getGuiLeft() + x, getGuiTop() + y), i, x, y, menu.upgradeSlot.get(i).isHidden));
        }

        upgradesInitialized = true;
    }

    @Override
    public Font getFont() {
        return this.font;
    }

    public void initWidgets() {
        this.settingsWidget = new SettingsWidget(this, new Point(this.leftPos + this.imageWidth - 3, this.topPos + 4), false);
        addRenderableWidget(this.settingsWidget);

        int xPos = leftPos + (wider ? 27 : 9) + (tanksVisible ? 22 : (wider ? 0 : 18));
        this.sortingButtons = new SortingButtons(this, new Point(xPos, topPos - 10), 50, 13);
        addRenderableWidget(sortingButtons);

        this.toolSlotsWidget = new ToolSlotsWidget(this, new Point(this.leftPos + 7, topPos - 15));
        addRenderableWidget(this.toolSlotsWidget);

        UpgradeManager manager = getWrapper().getUpgradeManager();

        for(Optional<? extends IUpgrade> upgrade : manager.mappedUpgrades.values()) {
            upgrade.ifPresent(loadedUpgrade -> {
                int x;
                int y;

                x = menu.upgradeSlot.get(getWrapper().getUpgradeManager().slotMappedUpgrades.get(upgrade)).x - 4;
                y = menu.upgradeSlot.get(getWrapper().getUpgradeManager().slotMappedUpgrades.get(upgrade)).y - 4;

                addRenderableWidget(loadedUpgrade.createWidget(this, x, y));
            });
        }

        initializeUpgradeSlots();
    }

    public void initButtons() {
        buttons.clear();
        if(getWrapper().getScreenID() == Reference.ITEM_SCREEN_ID) {
            buttons.add(new EquipButton(this));
        }
        if(getWrapper().getScreenID() == Reference.WEARABLE_SCREEN_ID && getWrapper().isOwner(getMenu().player)) {
            buttons.add(new UnequipButton(this));

            if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, getWrapper().getBackpackStack())) {
                buttons.add(new AbilitySliderButton(this, false));
            }
        }
        if(getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
            buttons.add(new SleepingBagButton(this));

            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, getWrapper().getBackpackStack())) {
                buttons.add(new AbilitySliderButton(this, true));
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        this.buttons.forEach(button -> button.render(guiGraphics, mouseX, mouseY, partialTicks));

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        this.buttons.forEach(button -> button.renderTooltip(guiGraphics, mouseX, mouseY));
        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderTooltip(guiGraphics, mouseX, mouseY));

        if(warningTicks > 0) {
            if(!(menu.getCarried().getItem() instanceof TanksUpgradeItem)) {
                warningTicks = 0;
            }

            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("screen.travelersbackpack.cant_apply_upgrade"));
            tooltip.add(Component.translatable("screen.travelersbackpack.too_much_fluid"));
            FluidStack leftFluidStack = TanksUpgradeItem.getLeftFluidStack(menu.getCarried());
            FluidStack rightFluidStack = TanksUpgradeItem.getRightFluidStack(menu.getCarried());

            if(!leftFluidStack.isEmpty() && leftFluidStack.getAmount() > getWrapper().getBackpackTankCapacity()) {
                tooltip.add(crateFluidWarning(leftFluidStack, getWrapper().getBackpackTankCapacity()));
            }

            if(!rightFluidStack.isEmpty() && rightFluidStack.getAmount() > getWrapper().getBackpackTankCapacity()) {
                tooltip.add(crateFluidWarning(rightFluidStack, getWrapper().getBackpackTankCapacity()));
            }
            guiGraphics.renderTooltip(getFont(), tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    public Component crateFluidWarning(FluidStack fluidStack, int backpackCapacity) {
        return Component.literal(fluidStack.getFluid().getFluidType().getDescription().getString() + " " + fluidStack.getAmount() + "/" + backpackCapacity + "mB").withStyle(ChatFormatting.RED);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        renderScreen(guiGraphics, x, y, mouseX, mouseY, partialTicks);
        drawUnsortableSlots(guiGraphics);
        drawMemorySlots(guiGraphics);
    }

    public void drawUnsortableSlots(GuiGraphics guiGraphics) {
        if(!getWrapper().getUnsortableSlots().isEmpty()) {
            getWrapper().getUnsortableSlots().forEach(i -> guiGraphics.blit(ICONS, this.getGuiLeft() + getMenu().getSlot(i).x, this.getGuiTop() + getMenu().getSlot(i).y, 25, 55, 16, 16));
        }
    }

    public void drawMemorySlots(GuiGraphics guiGraphics) {
        if(!getWrapper().getMemorySlots().isEmpty()) {
            getWrapper().getMemorySlots().forEach(pair -> {
                if(getMenu().getSlot(pair.getFirst()).getItem().isEmpty()) {
                    ItemStack itemstack = pair.getSecond().getFirst();
                    guiGraphics.renderFakeItem(itemstack, this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y);
                    guiGraphics.fill(RenderType.guiGhostRecipeOverlay(), this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y, this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x + 16, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y + 16, 822083583);
                }
            });
        }
    }

    @Override
    protected boolean hasClickedOutside(double pMouseX, double pMouseY, int pGuiLeft, int pGuiTop, int pMouseButton) {
        if(!this.menu.getCarried().isEmpty()) {
            for(GuiEventListener widget : children()) {
                if(widget instanceof WidgetBase base) {
                    if(base.isMouseOver(pMouseX, pMouseY)) return false;
                }
            }
        }
        return pMouseX < (double)pGuiLeft || pMouseY < (double)pGuiTop || pMouseX >= (double)(pGuiLeft + this.imageWidth) || pMouseY >= (double)(pGuiTop + this.imageHeight);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.buttons.forEach(b -> b.mouseClicked(mouseX, mouseY, button));
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void playUIClickSound() {
        menu.getPlayerInventory().player.level().playSound(menu.getPlayerInventory().player, menu.getPlayerInventory().player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.MASTER, 0.25F, 1.0F);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if(ModClientEventHandler.SORT_BACKPACK.isActiveAndMatches(InputConstants.getKey(pKeyCode, pScanCode))) {
            PacketDistributorHelper.sendToServer(new ServerboundSorterPacket(getWrapper().getScreenID(), ContainerSorter.SORT_BACKPACK, BackpackDeathHelper.isShiftPressed()));
            playUIClickSound();
            return true;
        }
        if(ModClientEventHandler.OPEN_BACKPACK.isActiveAndMatches(InputConstants.getKey(pKeyCode, pScanCode))) {
            LocalPlayer playerEntity = this.getMinecraft().player;
            if(playerEntity != null) {
                this.onClose();
            }
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    public static void displayTanksUpgradeWarning(Player player) {
        if(player.level().isClientSide) {
            if(Minecraft.getInstance().screen instanceof BackpackScreen screen) {
                screen.warningTicks = 60;
            }
        }
    }
}
