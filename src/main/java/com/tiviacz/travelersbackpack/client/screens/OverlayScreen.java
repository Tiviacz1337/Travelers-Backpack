            drawItemStack(mc.getItemRenderer(), inv.getHandler().getStackInSlot(Reference.TOOL_LOWER), scaledWidth - 30, scaledHeight + 11);
        itemRenderer.renderGuiItem(stack, x, y);
        itemRenderer.renderGuiItemDecorations(Minecraft.getInstance().font, stack, x, y);
