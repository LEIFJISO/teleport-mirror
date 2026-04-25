package com.le.teleportmirror;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(TeleportMirrorMod.MODID)
public class TeleportMirrorMod {
    public static final String MODID = "teleportmirror";

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredItem<MirrorItem> BASIC_RETURN_MIRROR = ITEMS.register("basic_return_mirror",
            () -> new MirrorItem(MirrorTier.BASIC, MirrorType.RETURN,
                    new Item.Properties().stacksTo(1).durability(MirrorTier.BASIC.getDefaultDurability())));

    public static final DeferredItem<MirrorItem> INTERMEDIATE_RETURN_MIRROR = ITEMS.register("intermediate_return_mirror",
            () -> new MirrorItem(MirrorTier.INTERMEDIATE, MirrorType.RETURN,
                    new Item.Properties().stacksTo(1).durability(MirrorTier.INTERMEDIATE.getDefaultDurability())));

    public static final DeferredItem<MirrorItem> ADVANCED_RETURN_MIRROR = ITEMS.register("advanced_return_mirror",
            () -> new MirrorItem(MirrorTier.ADVANCED, MirrorType.RETURN,
                    new Item.Properties().stacksTo(1).durability(MirrorTier.ADVANCED.getDefaultDurability())));

    public static final DeferredItem<MirrorItem> PERMANENT_RETURN_MIRROR = ITEMS.register("permanent_return_mirror",
            () -> new MirrorItem(MirrorTier.PERMANENT, MirrorType.RETURN,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredItem<MirrorItem> BASIC_TELEPORT_MIRROR = ITEMS.register("basic_teleport_mirror",
            () -> new MirrorItem(MirrorTier.BASIC, MirrorType.TELEPORT,
                    new Item.Properties().stacksTo(1).durability(MirrorTier.BASIC.getDefaultDurability())));

    public static final DeferredItem<MirrorItem> INTERMEDIATE_TELEPORT_MIRROR = ITEMS.register("intermediate_teleport_mirror",
            () -> new MirrorItem(MirrorTier.INTERMEDIATE, MirrorType.TELEPORT,
                    new Item.Properties().stacksTo(1).durability(MirrorTier.INTERMEDIATE.getDefaultDurability())));

    public static final DeferredItem<MirrorItem> ADVANCED_TELEPORT_MIRROR = ITEMS.register("advanced_teleport_mirror",
            () -> new MirrorItem(MirrorTier.ADVANCED, MirrorType.TELEPORT,
                    new Item.Properties().stacksTo(1).durability(MirrorTier.ADVANCED.getDefaultDurability())));

    public static final DeferredItem<MirrorItem> PERMANENT_TELEPORT_MIRROR = ITEMS.register("permanent_teleport_mirror",
            () -> new MirrorItem(MirrorTier.PERMANENT, MirrorType.TELEPORT,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MIRROR_TAB =
            CREATIVE_MODE_TABS.register("mirror_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.teleportmirror"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> BASIC_RETURN_MIRROR.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(BASIC_RETURN_MIRROR.get());
                        output.accept(INTERMEDIATE_RETURN_MIRROR.get());
                        output.accept(ADVANCED_RETURN_MIRROR.get());
                        output.accept(PERMANENT_RETURN_MIRROR.get());
                        output.accept(BASIC_TELEPORT_MIRROR.get());
                        output.accept(INTERMEDIATE_TELEPORT_MIRROR.get());
                        output.accept(ADVANCED_TELEPORT_MIRROR.get());
                        output.accept(PERMANENT_TELEPORT_MIRROR.get());
                    }).build());

    public TeleportMirrorMod(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.addListener(this::onRegisterPayloads);
    }

    private void onRegisterPayloads(final RegisterPayloadHandlersEvent event) {
        MirrorNetwork.registerPayloads(event);
    }
}
