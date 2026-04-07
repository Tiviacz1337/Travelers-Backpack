package com.tiviacz.travelersbackpack.compat.polymorph;

/*public class PolymorphWidget extends PlayerRecipesWidget {
    private final BackpackScreen screen;

    public PolymorphWidget(BackpackScreen screen, Slot outputSlot) {
        super(screen, outputSlot);
        this.screen = screen;
    }

    @Override
    public int getXPos() {
        return super.getXPos() + 20;
    }

    @Override
    public int getYPos() {
        return super.getYPos() + 72;
    }

    @Override
    public void initChildWidgets() {
        super.initChildWidgets();

        int openButtonYOffset = -50;
        this.openButton.setOffsets(this.getXPos(), this.getYPos() + openButtonYOffset);
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float renderPartialTicks) {
        screen.getMenu().getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).ifPresent(crafting -> {
            if(crafting.isTabOpened()) {
                super.render(guiGraphics, mouseX, mouseY, renderPartialTicks);
            }
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(screen.getMenu().getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).isPresent()) {
            if(screen.getMenu().getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class).get().isTabOpened()) {
                return super.mouseClicked(mouseX, mouseY, button);
            }
        }
        return false;
    }
}
*/