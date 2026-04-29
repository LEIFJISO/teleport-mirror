package com.le.teleportmirror;

import com.le.teleportmirror.screen.PlayerSelectionScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 客户端网络处理器
 * <p>
 * 仅编译到客户端（通过 @OnlyIn(Dist.CLIENT) 注解），
 * 负责接收服务端发来的网络负载并打开对应的客户端界面。
 * 在专用服务端环境中此类不会被加载。
 */
@OnlyIn(Dist.CLIENT)
public class MirrorNetworkClient {

    /**
     * 接收服务端发送的玩家选择请求，在客户端打开玩家选择界面
     *
     * @param payload 包含可选玩家列表、UUID 和魔镜等级的网络负载
     */
    public static void openPlayerSelection(MirrorNetwork.OpenPlayerSelectionPayload payload) {
        Minecraft client = Minecraft.getInstance();
        MirrorTier tier = MirrorTier.valueOf(payload.tierName().toUpperCase());
        client.setScreen(new PlayerSelectionScreen(payload.playerNames(), payload.playerUuids(), tier));
    }
}
