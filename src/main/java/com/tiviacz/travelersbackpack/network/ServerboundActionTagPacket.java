package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TankActions;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundActionTagPacket(CompoundTag actionTag) implements CustomPacketPayload {
    public static final Type<ServerboundActionTagPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "action_tag"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundActionTagPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, ServerboundActionTagPacket::actionTag,
            ServerboundActionTagPacket::new
    );

    public static final int UPGRADE_TAB = 0;
    public static final int OPEN_SCREEN = 1;
    public static final int OPEN_BACKPACK = 2;
    public static final int SORTER = 3;
    public static final int SLEEPING_BAG = 4;
    public static final int FILL_TANK = 5;
    public static final int SWAP_TOOL = 6;
    public static final int TOGGLE_BUTTONS_VISIBILITY = 7;
    public static final int SHOW_TOOL_SLOTS = 8;
    public static final int REMOVE_UPGRADE = 9;
    public static final int OPEN_SETTINGS = 10;
    public static final int SWITCH_HOSE_MODE = 11;
    public static final int SWITCH_HOSE_TANK = 12;
    public static final int TOGGLE_VISIBILITY = 13;
    public static final int ABILITY_SLIDER = 14;
    public static final int EQUIP_BACKPACK = 15;

    public static void handle(ServerboundActionTagPacket message, IPayloadContext ctx) {
        ctx.player().getServer().execute(() -> {
            if(!(ctx.player() instanceof ServerPlayer player)) {
                return;
            }
            CompoundTag actionTag = message.actionTag();
            int actionType = actionTag.getInt("ActionType");
            switch(actionType) {
                case UPGRADE_TAB -> {
                    int slot = actionTag.getInt("Arg0");
                    boolean open = actionTag.getBoolean("Arg1");
                    int packetType = actionTag.getInt("Arg2");
                    ServerActions.modifyUpgradeTab(player, slot, open, packetType);
                }
                case OPEN_SCREEN -> {
                    if(AttachmentUtils.isWearingBackpack(player)) {
                        BackpackContainer.openBackpack(player, AttachmentUtils.getWearingBackpack(player), Reference.WEARABLE_SCREEN_ID);
                    }
                }
                case OPEN_BACKPACK -> {
                    int index = actionTag.getInt("Arg0");
                    boolean fromSlot = actionTag.getBoolean("Arg1");
                    ServerActions.openBackpackFromSlot(player, index, fromSlot);
                }
                case SORTER -> {
                    int button = actionTag.getInt("Arg0");
                    boolean shiftPressed = actionTag.getBoolean("Arg1");
                    ServerActions.sortBackpack(player, button, shiftPressed);
                }
                case SLEEPING_BAG -> {
                    BlockPos pos = BlockPos.CODEC.parse(NbtOps.INSTANCE, actionTag.get("Arg0")).getOrThrow();
                    boolean isEquipped = actionTag.getBoolean("Arg1");
                    ServerActions.toggleSleepingBag(player, pos, isEquipped);
                }
                case FILL_TANK -> {
                    boolean leftTank = actionTag.getBoolean("Arg0");
                    TankActions.fillTank(player, leftTank);
                }
                case SWAP_TOOL -> {
                    double scrollDelta = actionTag.getDouble("Arg0");
                    ServerActions.swapTool(player, scrollDelta);
                }
                case TOGGLE_BUTTONS_VISIBILITY -> ServerActions.toggleButtonsVisibility(player);
                case SHOW_TOOL_SLOTS -> {
                    boolean show = actionTag.getBoolean("Arg0");
                    ServerActions.showToolSlots(player, show);
                }
                case REMOVE_UPGRADE -> {
                    int slot = actionTag.getInt("Arg0");
                    ServerActions.removeBackpackUpgrade(player, slot);
                }
                case OPEN_SETTINGS -> {
                    int entityId = actionTag.getInt("Arg0");
                    boolean open = actionTag.getBoolean("Arg1");
                    ServerActions.openBackpackSettings(player, entityId, open);
                }
                case SWITCH_HOSE_MODE -> {
                    double scrollDelta = actionTag.getDouble("Arg0");
                    ServerActions.switchHoseMode(player, scrollDelta);
                }
                case SWITCH_HOSE_TANK -> ServerActions.toggleHoseTank(player);
                case TOGGLE_VISIBILITY -> ServerActions.toggleVisibility(player);
                case ABILITY_SLIDER -> {
                    boolean sliderValue = actionTag.getBoolean("Arg0");
                    ServerActions.switchAbilitySlider(player, sliderValue);
                }
                case EQUIP_BACKPACK -> {
                    boolean equip = actionTag.getBoolean("Arg0");
                    ServerActions.equipBackpack(player, equip);
                }
            }
        });
    }

    public static void create(int type, Object... args) {
        PacketDistributor.sendToServer(new ServerboundActionTagPacket(createPacketTag(type, args)));
    }

    public static CompoundTag createPacketTag(int type, Object... args) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("ActionType", type);
        for(int i = 0; i < args.length; i++) {
            String argName = "Arg" + i;
            if(args[i] instanceof BlockPos) {
                tag.put(argName, BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, (BlockPos)args[i]).getOrThrow());
            } else if(args[i] instanceof Boolean) {
                tag.putBoolean(argName, (boolean)args[i]);
            } else if(args[i] instanceof Byte) {
                tag.putByte(argName, (byte)args[i]);
            } else if(args[i] instanceof Integer) {
                tag.putInt(argName, (int)args[i]);
            } else if(args[i] instanceof Double) {
                tag.putDouble(argName, (double)args[i]);
            }
        }
        return tag;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}