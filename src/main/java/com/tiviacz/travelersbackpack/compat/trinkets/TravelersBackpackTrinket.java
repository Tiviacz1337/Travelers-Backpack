package com.tiviacz.travelersbackpack.compat.trinkets;

/*public class TravelersBackpackTrinket implements Trinket {
    public static void init() {
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof TravelersBackpackItem)
                .forEach(item -> TrinketsApi.registerTrinket(item, new TravelersBackpackTrinket()));
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof TravelersBackpackItem)
                .forEach(item -> TrinketRendererRegistry.registerRenderer(item, new Renderer()));
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return TravelersBackpackConfig.SERVER.backpackSettings.backSlotIntegration.get();
    }

    @Override
    public boolean canEquipFromUse(ItemStack stack, LivingEntity entity) {
        return false;
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if(!TravelersBackpackConfig.SERVER.backpackSettings.backSlotIntegration.get()) return;
        if(entity instanceof Player player) {
            BackpackWrapper.tick(stack, player, true);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Renderer implements TrinketRenderer {
        @Override
        public void render(ItemStack itemStack, SlotReference slotReference, EntityModel<? extends LivingEntityRenderState> entityModel, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, LivingEntityRenderState livingEntityRenderState, float v, float v1) {
            ItemStackRenderState backpackRenderState = new ItemStackRenderState();
            StackModelPart tools = new StackModelPart();
            if(itemStack.getItem() instanceof TravelersBackpackItem && entityModel instanceof PlayerModel playerModel && livingEntityRenderState instanceof HumanoidRenderState humanoidRenderState) {
                BackpackLayer.renderBackpackLayer(playerModel, poseStack, submitNodeCollector, i, humanoidRenderState, backpackRenderState, tools, itemStack);
            }
        }
    }
}*/