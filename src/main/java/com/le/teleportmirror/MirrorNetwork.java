package com.le.teleportmirror;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 魔镜的网络通信层
 * <p>
 * 负责定义和注册自定义网络负载（CustomPacketPayload），
 * 处理客户端与服务端之间的魔镜相关通信：
 * <ul>
 *   <li>OpenPlayerSelectionPayload - 服务端→客户端，打开玩家选择界面</li>
 *   <li>RequestTeleportPayload - 客户端→服务端，请求传送到目标玩家</li>
 * </ul>
 */
public class MirrorNetwork {

    /**
     * 注册所有的网络负载处理器
     * 在 RegisterPayloadHandlersEvent 事件中调用
     */
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(TeleportMirrorMod.MODID).versioned("1.0.0");

        registrar.playToClient(
                OpenPlayerSelectionPayload.TYPE,
                OpenPlayerSelectionPayload.STREAM_CODEC,
                MirrorNetwork::handleOpenSelection
        );

        registrar.playToServer(
                RequestTeleportPayload.TYPE,
                RequestTeleportPayload.STREAM_CODEC,
                MirrorNetwork::handleRequestTeleport
        );
    }

    /**
     * 向指定客户端发送打开玩家选择界面的请求
     *
     * @param player 目标玩家
     * @param tier 魔镜等级
     */
    public static void sendOpenSelectionToClient(ServerPlayer player, MirrorTier tier) {
        List<String> names = new ArrayList<>();
        List<UUID> uuids = new ArrayList<>();
        boolean teamOnly = Config.TELEPORT_TEAM_ONLY.get();

        for (ServerPlayer onlinePlayer : player.server.getPlayerList().getPlayers()) {
            // 排除使用者自身
            if (onlinePlayer.getUUID().equals(player.getUUID())) {
                continue;
            }
            // 如果开启了同队伍限制，仅保留同队伍玩家
            if (teamOnly) {
                var playerTeam = player.getTeam();
                var targetTeam = onlinePlayer.getTeam();
                if (playerTeam == null || targetTeam == null) {
                    continue;
                }
                if (!playerTeam.getName().equals(targetTeam.getName())) {
                    continue;
                }
            }
            names.add(onlinePlayer.getName().getString());
            uuids.add(onlinePlayer.getUUID());
        }

        PacketDistributor.sendToPlayer(player, new OpenPlayerSelectionPayload(names, uuids, tier.getName()));
    }

    /**
     * 处理服务端发来的打开选择界面负载
     * 通过反射调用客户端专用类以避免在服务端加载客户端代码
     */
    private static void handleOpenSelection(OpenPlayerSelectionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                Class<?> clientHandlerClass = Class.forName("com.le.teleportmirror.MirrorNetworkClient");
                clientHandlerClass.getMethod("openPlayerSelection", OpenPlayerSelectionPayload.class)
                        .invoke(null, payload);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to open player selection screen", e);
            }
        });
    }

    /**
     * 处理客户端发来的传送请求
     * 验证玩家手中的魔镜类型和等级后执行传送
     */
    private static void handleRequestTeleport(RequestTeleportPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer sender = (ServerPlayer) context.player();
            ServerPlayer target = sender.server.getPlayerList().getPlayer(payload.targetUuid);

            if (target == null) {
                return;
            }

            MirrorTier tier = MirrorTier.valueOf(payload.tierName.toUpperCase());
            ItemStack mainHand = sender.getMainHandItem();
            ItemStack offHand = sender.getOffhandItem();

            // 检查主手和副手中是否有正确的传送魔镜
            ItemStack mirrorStack = null;
            if (mainHand.getItem() instanceof MirrorItem mirrorItem && mirrorItem.getMirrorType() == MirrorType.TELEPORT) {
                mirrorStack = mainHand;
            } else if (offHand.getItem() instanceof MirrorItem mirrorItem && mirrorItem.getMirrorType() == MirrorType.TELEPORT) {
                mirrorStack = offHand;
            }

            if (mirrorStack == null || mirrorStack.isEmpty()) {
                return;
            }

            MirrorItem mirrorItem = (MirrorItem) mirrorStack.getItem();
            if (mirrorItem.getTier() != tier) {
                return;
            }

            mirrorItem.performTeleportToPlayer(sender, target, mirrorStack);
        });
    }

    /**
     * 打开玩家选择界面的网络负载（服务端 → 客户端）
     */
    public record OpenPlayerSelectionPayload(List<String> playerNames, List<UUID> playerUuids,
            String tierName) implements CustomPacketPayload {
        public static final Type<OpenPlayerSelectionPayload> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(TeleportMirrorMod.MODID, "open_player_selection"));

        public static final StreamCodec<FriendlyByteBuf, OpenPlayerSelectionPayload> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public OpenPlayerSelectionPayload decode(FriendlyByteBuf buf) {
                int size = buf.readVarInt();
                List<String> names = new ArrayList<>();
                List<UUID> uuids = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    names.add(buf.readUtf());
                    uuids.add(buf.readUUID());
                }
                String tierName = buf.readUtf();
                return new OpenPlayerSelectionPayload(names, uuids, tierName);
            }

            @Override
            public void encode(FriendlyByteBuf buf, OpenPlayerSelectionPayload payload) {
                buf.writeVarInt(payload.playerNames.size());
                for (int i = 0; i < payload.playerNames.size(); i++) {
                    buf.writeUtf(payload.playerNames.get(i));
                    buf.writeUUID(payload.playerUuids.get(i));
                }
                buf.writeUtf(payload.tierName);
            }
        };

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /**
     * 请求传送到目标玩家的网络负载（客户端 → 服务端）
     */
    public record RequestTeleportPayload(UUID targetUuid, String tierName) implements CustomPacketPayload {
        public static final Type<RequestTeleportPayload> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(TeleportMirrorMod.MODID, "request_teleport"));

        public static final StreamCodec<FriendlyByteBuf, RequestTeleportPayload> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public RequestTeleportPayload decode(FriendlyByteBuf buf) {
                UUID targetUuid = buf.readUUID();
                String tierName = buf.readUtf();
                return new RequestTeleportPayload(targetUuid, tierName);
            }

            @Override
            public void encode(FriendlyByteBuf buf, RequestTeleportPayload payload) {
                buf.writeUUID(payload.targetUuid);
                buf.writeUtf(payload.tierName);
            }
        };

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
