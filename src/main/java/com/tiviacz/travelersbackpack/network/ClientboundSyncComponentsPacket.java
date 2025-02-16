package com.tiviacz.travelersbackpack.network;

/*public record ClientboundSyncComponentsPacket(int entityID, DataComponentMap map) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sync_components");
    public static final Type<ClientboundSyncComponentsPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncComponentsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundSyncComponentsPacket::entityID,
            ByteBufCodecs.fromCodecWithRegistries(DataComponentMap.CODEC), ClientboundSyncComponentsPacket::map,
            ClientboundSyncComponentsPacket::new
    );

    public static void handle(final ClientboundSyncComponentsPacket message, ClientPlayNetworking.Context ctx) {
        ctx.client().execute(() -> {
            final Player playerEntity = (Player)Minecraft.getInstance().player.level().getEntity(message.entityID);
            ITravelersBackpack data = ComponentUtils.getComponent(playerEntity).orElseThrow(() -> new RuntimeException("No player attachment data found!"));
            if(data != null) {
                data.applyComponents(message.map());
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
} */
