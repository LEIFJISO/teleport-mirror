package com.le.teleportmirror;

import com.le.teleportmirror.screen.PlayerSelectionScreen;
import net.minecraft.client.Minecraft;
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

public class MirrorNetwork {

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

    public static void sendOpenSelectionToClient(ServerPlayer player, MirrorTier tier) {
        List<String> names = new ArrayList<>();
        List<UUID> uuids = new ArrayList<>();
        boolean teamOnly = Config.TELEPORT_TEAM_ONLY.get();

        for (ServerPlayer onlinePlayer : player.server.getPlayerList().getPlayers()) {
            if (onlinePlayer.getUUID().equals(player.getUUID())) {
                continue;
            }
            if (teamOnly && player.getTeam() != null) {
                if (onlinePlayer.getTeam() != player.getTeam()) {
                    continue;
                }
            }
            names.add(onlinePlayer.getName().getString());
            uuids.add(onlinePlayer.getUUID());
        }

        PacketDistributor.sendToPlayer(player, new OpenPlayerSelectionPayload(names, uuids, tier.getName()));
    }

    private static void handleOpenSelection(OpenPlayerSelectionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            MirrorTier tier = MirrorTier.valueOf(payload.tierName.toUpperCase());
            client.setScreen(new PlayerSelectionScreen(payload.playerNames, payload.playerUuids, tier));
        });
    }

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
