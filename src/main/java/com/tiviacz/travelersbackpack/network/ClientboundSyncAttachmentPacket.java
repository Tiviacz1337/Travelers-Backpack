package com.tiviacz.travelersbackpack.network;

/*public record ClientboundSyncAttachmentPacket(int entityID, ItemStack backpack,
                                              boolean removeData) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sync_attachment");
    public static final Type<ClientboundSyncAttachmentPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncAttachmentPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundSyncAttachmentPacket::entityID,
            ItemStack.OPTIONAL_STREAM_CODEC, ClientboundSyncAttachmentPacket::backpack,
            ByteBufCodecs.BOOL, ClientboundSyncAttachmentPacket::removeData,
            ClientboundSyncAttachmentPacket::new
    );

    public ClientboundSyncAttachmentPacket(int entityID, ItemStack serverBackpack) {
        this(entityID, serverBackpack, false);
    }

    public ClientboundSyncAttachmentPacket(int entityID, ItemStack backpack, boolean removeData) {
        this.entityID = entityID;
        //Remove heavy data that is not needed anyways
        ItemStack backpackCopy = backpack.copy();
        if(backpackCopy.has(ModDataComponents.BACKPACK_CONTAINER)) {
            backpackCopy.remove(ModDataComponents.BACKPACK_CONTAINER);
        }
        //if(backpackCopy.has(ModDataComponents.UPGRADES)) {
        //    backpackCopy.remove(ModDataComponents.UPGRADES);
        //}
        this.backpack = backpackCopy;
        this.removeData = removeData;
    }

    public static void handle(final ClientboundSyncAttachmentPacket message, ClientPlayNetworking.Context ctx) {
        ctx.client().execute(() -> {
            final Player playerEntity = (Player)Minecraft.getInstance().player.level().getEntity(message.entityID);
            ITravelersBackpack data = ComponentUtils.getComponent(playerEntity).orElseThrow(() -> new RuntimeException("No player attachment data found!"));

            if(data != null) {
                if(message.removeData()) {
                    data.remove();
                } else {
                    data.updateBackpack(message.backpack());
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
} */