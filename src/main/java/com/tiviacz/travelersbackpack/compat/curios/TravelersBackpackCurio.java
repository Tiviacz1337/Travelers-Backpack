package com.tiviacz.travelersbackpack.compat.curios;

public class TravelersBackpackCurio { /*} implements ICurio {
    public static void registerCurio(RegisterCapabilitiesEvent event) {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> event.registerItem(CuriosCapability.ITEM, (stack, context) -> new TravelersBackpackCurio(stack), holder::get));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerCurioRenderer() {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> CuriosRendererRegistry.register(holder.get(), Renderer::new));
    }

    public final ItemStack stack;

    public TravelersBackpackCurio(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ItemStack getStack() {
        return this.stack;
    }

    @Override
    public boolean canEquip(SlotContext context) {
        return TravelersBackpackConfig.SERVER.backpackSettings.backSlotIntegration.get();
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext) {
        return false;
    }

    @Override
    public void curioTick(SlotContext slotContext) {
        if(!TravelersBackpackConfig.SERVER.backpackSettings.backSlotIntegration.get()) return;
        if(slotContext.entity() instanceof Player player) {
            BackpackWrapper.tick(this.stack, player, true);
        }
    }

    @Nonnull
    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit) {
        return DropRule.DEFAULT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Renderer implements ICurioRenderer {
        @Override
        public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if(slotContext.entity() instanceof Player player && renderLayerParent.getModel() instanceof PlayerModel<?> playerModel) {
                ItemStack backpackStack = AttachmentUtils.getWearingBackpack(player);
                BackpackLayer.renderBackpackLayer(BackpackLayerModel.LAYER_MODEL, playerModel, matrixStack, renderTypeBuffer, light, player, backpackStack);
            }
        }
    } */
}