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

/**
 * 玩家选择界面
 * <p>
 * 当玩家使用传送魔镜长按右键蓄力完成后，客户端会打开此界面。
 * 界面列出当前在线的可选玩家，玩家点击按钮选择目标后，
 * 通过网络包向服务端发送传送请求，服务端执行实际传送逻辑。
 * <p>
 * 支持分页：每页最多显示 {@value #BUTTONS_PER_PAGE} 名玩家，
 * 通过滚动按钮切换页码。
 * <p>
 * 该界面不暂停游戏（isPauseScreen 返回 false）。
 */
public class PlayerSelectionScreen extends Screen {
    /** 可选玩家名称列表 */
    private final List<String> playerNames;
    /** 可选玩家 UUID 列表（与 playerNames 一一对应） */
    private final List<UUID> playerUuids;
    /** 当前使用的魔镜等级 */
    private final MirrorTier tier;
    /** 当前滚动偏移量 */
    private int scrollOffset = 0;
    /** 每页最多显示的玩家按钮数量 */
    private static final int BUTTONS_PER_PAGE = 8;

    /**
     * @param playerNames 可选玩家的显示名称
     * @param playerUuids 可选玩家的 UUID
     * @param tier 魔镜等级
     */
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

    /**
     * 重建按钮布局
     * 根据 scrollOffset 显示当前页的玩家按钮，以及滚动按钮和取消按钮
     */
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

        // 如果玩家数量超过单页容量，显示滚动按钮
        if (playerNames.size() > BUTTONS_PER_PAGE) {
            this.addRenderableWidget(Button.builder(
                    Component.literal("\u25B2"), // 上箭头 ▲
                    btn -> { scrollOffset = Math.max(0, scrollOffset - 1); rebuildButtons(); })
                    .pos(centerX + buttonWidth / 2 + 5, startY)
                    .size(20, 20)
                    .build());
            this.addRenderableWidget(Button.builder(
                    Component.literal("\u25BC"), // 下箭头 ▼
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

    /**
     * 玩家点击某个目标后，向服务端发送传送请求并关闭界面
     *
     * @param index 所选玩家在列表中的索引
     */
    private void selectPlayer(int index) {
        if (index >= 0 && index < playerUuids.size()) {
            UUID targetUuid = playerUuids.get(index);
            PacketDistributor.sendToServer(new MirrorNetwork.RequestTeleportPayload(targetUuid, tier.getName()));
        }
        this.onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0xC0101010);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFF);

        for (var renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
