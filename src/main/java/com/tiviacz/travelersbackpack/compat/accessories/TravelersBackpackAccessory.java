package com.tiviacz.travelersbackpack.compat.accessories;

/*public class TravelersBackpackAccessory implements Accessory {
    public static void init() {
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof TravelersBackpackItem)
                .forEach(item -> AccessoriesAPI.registerAccessory(item, new TravelersBackpackAccessory()));
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        BuiltInRegistries.ITEM.stream()
                .filter(holder -> holder instanceof TravelersBackpackItem)
                .forEach(holder -> AccessoriesRendererRegistry.registerRenderer(holder, Renderer::new));
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference reference) {
        return TravelersBackpackConfig.SERVER.backpackSettings.backSlotIntegration.get();
    }

    @Override
    public boolean canEquipFromUse(ItemStack stack, SlotReference reference) {
        return false;
    }

    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        if(!TravelersBackpackConfig.SERVER.backpackSettings.backSlotIntegration.get()) return;
        if(reference.entity() instanceof Player player) {
            BackpackWrapper.tick(stack, player, true);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Renderer implements SimpleAccessoryRenderer {
        @Override
        public <S extends LivingEntityRenderState> void render(AccessoryRenderState accessoryState, S entityState, EntityModel<S> model, PoseStack matrices, SubmitNodeCollector collector) {
            var stack = accessoryState.getStateData(AccessoriesRenderStateKeys.ITEM_STACK);
            var light = accessoryState.getStateData(AccessoriesRenderStateKeys.LIGHT);
            ItemStackRenderState backpackRenderState = new ItemStackRenderState();
            StackModelPart tools = new StackModelPart();
            if(stack.getItem() instanceof TravelersBackpackItem && model instanceof PlayerModel playerModel && entityState instanceof AvatarRenderState playerRenderState) {
                BackpackLayer.renderBackpackLayer(playerModel, matrices, collector, light, playerRenderState, backpackRenderState, tools, stack);
            }
        }

        @Override
        public <S extends LivingEntityRenderState> void align(AccessoryRenderState accessoryRenderState, S s, EntityModel<S> entityModel, PoseStack poseStack) {

        }
    }
}*/