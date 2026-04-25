package com.le.teleportmirror.screen;

import com.le.teleportmirror.MirrorNetwork;
import com.le.teleportmirror.MirrorTier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.UUID;

public class PlayerSelectionScreen extends Screen {
    private final List<String> playerNames;
    private final List<UUID> playerUuids;
    private final MirrorTier tier;
    private int scrollOffset = 0;
    private static final int BUTTONS_PER_PAGE = 8;

    public PlayerSelectionScreen(List<String> playerNames, List<UUID> playerUuids, MirrorTier tier) {
        super(Component.translatable("screen.teleportmirror.select_player"));
        this.playerNames = playerNames;
        this.playerUuids = playerUuids;
        this.tier = tier;
    }

    @Override
    protected void init() {
        super.init();
        rebuildButtons();
    }

    private void rebuildButtons() {
        this.clearWidgets();

        int centerX = this.width / 2;
        int startY = 40;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int spacing = 4;

        int maxOffset = Math.max(0, playerNames.size() - BUTTONS_PER_PAGE);
        if (scrollOffset > maxOffset) scrollOffset = maxOffset;

        int endIndex = Math.min(scrollOffset + BUTTONS_PER_PAGE, playerNames.size());
        for (int i = scrollOffset; i < endIndex; i++) {
            final int idx = i;
            int y = startY + (i - scrollOffset) * (buttonHeight + spacing);
            this.addRenderableWidget(Button.builder(
                    Component.literal(playerNames.get(i)),
                    btn -> selectPlayer(idx))
                    .pos(centerX - buttonWidth / 2, y)
                    .size(buttonWidth, buttonHeight)
                    .build());
        }

        if (playerNames.size() > BUTTONS_PER_PAGE) {
            this.addRenderableWidget(Button.builder(
                    Component.literal("\u25B2"),
                    btn -> { scrollOffset = Math.max(0, scrollOffset - 1); rebuildButtons(); })
                    .pos(centerX + buttonWidth / 2 + 5, startY)
                    .size(20, 20)
                    .build());
            this.addRenderableWidget(Button.builder(
                    Component.literal("\u25BC"),
                    btn -> { scrollOffset = Math.min(maxOffset, scrollOffset + 1); rebuildButtons(); })
                    .pos(centerX + buttonWidth / 2 + 5, startY + BUTTONS_PER_PAGE * (buttonHeight + spacing) - spacing)
                    .size(20, 20)
                    .build());
        }

        this.addRenderableWidget(Button.builder(
                Component.translatable("gui.cancel"),
                btn -> this.onClose())
                .pos(centerX - 50, this.height - 30)
                .size(100, 20)
                .build());
    }

    private void selectPlayer(int index) {
        if (index >= 0 && index < playerUuids.size()) {
            UUID targetUuid = playerUuids.get(index);
            PacketDistributor.sendToServer(new MirrorNetwork.RequestTeleportPayload(targetUuid, tier.getName()));
        }
        this.onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.fill(0, 0, this.width, 32, 0x88000000);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
