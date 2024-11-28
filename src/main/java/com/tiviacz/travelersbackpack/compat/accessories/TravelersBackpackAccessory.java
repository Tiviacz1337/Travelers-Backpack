package com.tiviacz.travelersbackpack.compat.accessories;

public class TravelersBackpackAccessory { /* implements Accessory {
    public static void init() {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> AccessoriesAPI.registerAccessory(holder.get(), new TravelersBackpackAccessory()));
    }

    @OnlyIn(Dist.CLIENT)
    public static void initClient() {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> AccessoriesRendererRegistry.registerRenderer(holder.get(), Renderer::new));
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference reference) {
        return TravelersBackpackConfig.SERVER.backpackSettings.backSlotIntegration.get();
    }

    @Override
    public boolean canEquipFromUse(ItemStack stack) {
        return false;
    }

    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        if(!TravelersBackpackConfig.SERVER.backpackSettings.backSlotIntegration.get()) return;
        if(reference.entity() instanceof Player player) {
            BackpackWrapper.tick(stack, player, true);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Renderer implements SimpleAccessoryRenderer {
        @Override
        public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<M> entityModel, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if(reference.entity() instanceof Player player && entityModel instanceof PlayerModel<?> playerModel) {
                ItemStack backpackStack = AttachmentUtils.getWearingBackpack(player);
                BackpackLayer.renderBackpackLayer(BackpackLayerModel.LAYER_MODEL, playerModel, matrices, multiBufferSource, light, player, backpackStack);
            }
        }

        @Override
        public <M extends LivingEntity> void align(ItemStack stack, SlotReference reference, EntityModel<M> model, PoseStack matrices) {
        }
    } */
}