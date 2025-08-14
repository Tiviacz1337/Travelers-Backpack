package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.upgrades.FilterUpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.ButtonStates;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.FilterButton;
import com.tiviacz.travelersbackpack.network.ServerboundFilterSettingsPacket;
import com.tiviacz.travelersbackpack.network.ServerboundFilterTagsPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FilterUpgradeWidgetBase<W extends FilterUpgradeWidgetBase<W, U>, U extends FilterUpgradeBase<U, ?>> extends UpgradeWidgetBase<U> {
    private final WidgetElement tagIconElement;
    private final List<FilterButton<W>> buttons = new ArrayList<>();
    private int selectedTagIndex = 0;
    private List<String> addableTags;
    private List<String> tags;

    public FilterUpgradeWidgetBase(BackpackScreen screen, U upgrade, Point pos, Point tabUv, String upgradeIconTooltip) {
        super(screen, upgrade, pos, tabUv, upgradeIconTooltip);

        this.tagIconElement = new WidgetElement(new Point(pos.x() + 6 + 36, pos.y() + 22), new Point(18, 18));
        this.tags = upgrade.getFilterSettings().getFilterTags();
        this.addableTags = new ArrayList<>();
        upgrade.addChangeListener(this::updateAddableTags);
    }

    public void addFilterButton(FilterButton<W> button) {
        if(upgrade.isTagSelector() && button.getButtonState() == ButtonStates.IGNORE_MODE) {
            button.setHidden(true);
        }
        if(!this.buttons.contains(button)) {
            this.buttons.add(button);
        }
    }

    public void hideButton(ButtonStates.ButtonState state, boolean hidden) {
        if(hasButton(state)) {
            getFilterButton(state).setHidden(hidden);
        }
    }

    public boolean hasButton(ButtonStates.ButtonState buttonState) {
        return this.buttons.stream().anyMatch(button -> button.getButtonState() == buttonState);
    }

    public FilterButton<W> getFilterButton(ButtonStates.ButtonState buttonState) {
        return this.buttons.stream()
                .filter(button -> button.getButtonState() == buttonState)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No filter button found for state: " + buttonState));
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if(getUpgrade().isTagSelector() && isTabOpened()) {
            guiGraphics.blit(BackpackScreen.TABS, pos.x(), pos.y(), 66, 149, 87, 103);
            guiGraphics.renderItem(screen.getWrapper().getUpgrades().getStackInSlot(this.dataHolderSlot), pos.x() + 4, pos.y() + 4);
            int j = 0;
            for(String tag : getTags()) {
                j++;
                if(j >= 5) return;
                guiGraphics.blit(BackpackScreen.TABS, pos.x() + 7, calculateTagBoxY(getTags().indexOf(tag)), 153, 149, 73, 13);
                guiGraphics.drawString(screen.getFont(), getTrimmedText(tag), pos.x() + 9, calculateTagBoxY(getTags().indexOf(tag)) + 3, 0xFFFFFF, false);
            }
        } else {
            super.renderBg(guiGraphics, x, y, mouseX, mouseY);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        guiGraphics.blit(BackpackScreen.ICONS, this.tagIconElement.pos().x(), this.tagIconElement.pos().y(), 0, 186, this.tagIconElement.size().x(), this.tagIconElement.size().y());

        if(isTabOpened()) {
            if(upgrade.isTagSelector()) {
                if(!upgrade.getFirstFilterStack().isEmpty()) {
                    guiGraphics.blit(BackpackScreen.ICONS, this.tagIconElement.pos().x(), this.tagIconElement.pos().y(), 186, 36, this.tagIconElement.size().x(), this.tagIconElement.size().y());
                } else {
                    guiGraphics.blit(BackpackScreen.ICONS, this.tagIconElement.pos().x(), this.tagIconElement.pos().y(), 186, 0, this.tagIconElement.size().x(), this.tagIconElement.size().y());
                }
            }
            this.buttons.forEach(button -> button.renderButton(guiGraphics, mouseX, mouseY));
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if(isTabOpened()) {
            if(getUpgrade().isTagSelector() && isMouseOverTags(mouseX, mouseY)) {
                guiGraphics.renderTooltip(screen.getFont(), getTooltip(), Optional.empty(), mouseX, mouseY);
            }
        }
    }

    public void updateAddableTags() {
        this.selectedTagIndex = 0;
        this.addableTags = upgrade.getFilterSettings().getAddableTags();
    }

    public boolean isAdding() {
        return !upgrade.getFirstFilterStack().isEmpty();
    }

    public int getSelectedTagIndex() {
        return this.selectedTagIndex;
    }

    public void setNextSelectedTagIndex(double mouse) {
        if(mouse < 0 && this.selectedTagIndex + 1 < getTags().size()) {
            this.selectedTagIndex++;
        } else if(mouse > 0 && this.selectedTagIndex - 1 >= 0) {
            this.selectedTagIndex--;
        }
    }

    public List<String> getTags() {
        if(isAdding()) {
            return this.addableTags;
        } else {
            return this.tags;
        }
    }

    public boolean addTag() {
        List<String> itemTags = new ArrayList<>(this.addableTags);

        /*if(isAdding()) {
            ItemStack stackInTagSlot = upgrade.getFilterSettings().getFilterItems().stream().findFirst().get();
            itemTags.addAll(stackInTagSlot.getTags().map(tag -> tag.location().toString()).toList());
            itemTags.removeAll(tags);
        }*/

        if(itemTags.isEmpty()) {
            return false; //No tags available - display info
        }

        //selected = Math.max(0, Math.min(selected, itemTags.size() - 1));

        int selected = getSelectedTagIndex();
        String tag = itemTags.get(selected);
        itemTags.remove(tag);

        this.selectedTagIndex = Math.min(selected, itemTags.size() - 1);
        this.addableTags = itemTags;
        this.tags.add(tag);
        return true;
    }

    public boolean removeTag() {
        List<String> storedTags = new ArrayList<>(this.tags);
        if(storedTags.isEmpty()) return false;

        //selected = Math.max(0, Math.min(selected, storedTags.size() - 1));
        int selected = getSelectedTagIndex();
        //String tag = storedTags.get(selected);
        storedTags.remove(selected);

       /* if(selected >= storedTags.size()) {
            selected = storedTags.size() - 1;
        }*/

        this.selectedTagIndex = Math.max(0, Math.min(selected, storedTags.size() - 1));
        this.tags = storedTags;
        return true;
    }

    public List<Component> getTooltip() {
        List<Component> tooltip = new ArrayList<>();
        boolean isAdding = isAdding();
        List<String> displayedTags = isAdding ? new ArrayList<>(this.addableTags) : new ArrayList<>(this.tags);

        displayedTags.forEach(t -> tooltip.add(Component.literal(t).withStyle(ChatFormatting.GRAY)));
        /*if(isAdding) {
            this.addableTags.forEach(t -> tooltip.add(Component.literal(t).withStyle(ChatFormatting.GRAY)));
        } else {
            this.tags.forEach(t -> tooltip.add(Component.literal(t).withStyle(ChatFormatting.GRAY)));
        }*/

        if(tooltip.isEmpty())
            return List.of(Component.translatable("screen.travelersbackpack.filter_tag_empty").withStyle(ChatFormatting.DARK_GRAY));
        ChatFormatting cursorColor = isAdding ? ChatFormatting.GREEN : ChatFormatting.RED;

        String selectedTagString = displayedTags.get(getSelectedTagIndex());

        Component selectedTag = Component.literal("> " + selectedTagString).withStyle(cursorColor);
        tooltip.set(getSelectedTagIndex(), selectedTag);

        tooltip.add(Component.translatable(isAdding ? "screen.travelersbackpack.filter_tag_add" : "screen.travelersbackpack.filter_tag_remove").withStyle(ChatFormatting.BLUE));
        return tooltip;
    }

    public int calculateTagBoxY(int index) {
        return pos.y() + 44 + 13 * index;
    }

    public String getTrimmedText(String tag) {
        Font font = screen.getFont();
        int maxWidth = 70;

        String displayText = tag;
        if(font.width(tag) > maxWidth) {
            String ellipsis = "...";
            int ellipsisWidth = font.width(ellipsis);
            int availableWidth = maxWidth - ellipsisWidth;
            int i = tag.length();
            while(i > 0 && font.width(tag.substring(0, i)) > availableWidth) {
                i--;
            }
            displayText = tag.substring(0, i) + ellipsis;
        }

        return displayText;
    }

    public boolean isMouseOverTags(double mouseX, double mouseY) {
        return isWithinBounds(mouseX, mouseY, new Point(9, calculateTagBoxY(0) - pos.y()), new Point(73, 52));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(isTabOpened() && upgrade.isTagSelector() && isMouseOverTags(mouseX, mouseY)) {
            setNextSelectedTagIndex(scrollY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(isTabOpened() && isBackpackOwner()) {
            for(FilterButton<W> button : this.buttons) {
                if(button.mouseClicked(pMouseX, pMouseY, pButton)) {
                    //Tag selector - disable match contents
                    if(hasButton(ButtonStates.OBJECT_TYPE) && getFilterButton(ButtonStates.OBJECT_TYPE).getCurrentState() == 2) {
                        if(hasButton(ButtonStates.ALLOW) && getFilterButton(ButtonStates.ALLOW).getCurrentState() == 2) {
                            getFilterButton(ButtonStates.ALLOW).nextState(); //Change to ALLOW
                        }
                        hideButton(ButtonStates.IGNORE_MODE, true);
                    } else {
                        hideButton(ButtonStates.IGNORE_MODE, false);
                    }
                    PacketDistributor.sendToServer(new ServerboundFilterSettingsPacket(this.dataHolderSlot, List.of(buttons.get(0).getCurrentState(), buttons.get(1).getCurrentState(), buttons.get(2).getCurrentState())));
                    this.screen.playUIClickSound();
                    return true;
                }
            }
            if(isMouseOverTags(pMouseX, pMouseY) && getUpgrade().isTagSelector()) {
                if(isAdding()) {
                    if(!addTag()) {
                        return false;
                    }
                } else {
                    if(!removeTag()) {
                        return false;
                    }
                }
                PacketDistributor.sendToServer(new ServerboundFilterTagsPacket(this.dataHolderSlot, this.tags));
                getUpgrade().getFilterSettings().updateFilterTags(this.tags); //Client update
                return true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
}