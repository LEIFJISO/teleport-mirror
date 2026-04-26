package com.le.teleportmirror;

import com.le.teleportmirror.screen.PlayerSelectionScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MirrorNetworkClient {

    public static void openPlayerSelection(MirrorNetwork.OpenPlayerSelectionPayload payload) {
        Minecraft client = Minecraft.getInstance();
        MirrorTier tier = MirrorTier.valueOf(payload.tierName().toUpperCase());
        client.setScreen(new PlayerSelectionScreen(payload.playerNames(), payload.playerUuids(), tier));
    }
}
