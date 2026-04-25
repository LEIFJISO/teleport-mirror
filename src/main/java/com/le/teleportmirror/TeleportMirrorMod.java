package com.le.teleportmirror;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        NeoForge.EVENT_BUS.addListener(this::onServerStarted);
    }

    private void onRegisterPayloads(final RegisterPayloadHandlersEvent event) {
        MirrorNetwork.registerPayloads(event);
    }

    private void onServerStarted(final ServerStartedEvent event) {
        registerConfigRecipes(event.getServer().getRecipeManager());
    }

    private void registerConfigRecipes(net.minecraft.world.item.crafting.RecipeManager manager) {
        tryRegisterRecipe(manager, Config.ENABLE_BASIC_RECIPE.get(),
                Config.RECIPE_OVERRIDE_RETURN_BASIC.get(), BASIC_RETURN_MIRROR.get(), "basic_return_mirror");
        tryRegisterRecipe(manager, Config.ENABLE_INTERMEDIATE_RECIPE.get(),
                Config.RECIPE_OVERRIDE_RETURN_INTERMEDIATE.get(), INTERMEDIATE_RETURN_MIRROR.get(), "intermediate_return_mirror");
        tryRegisterRecipe(manager, Config.ENABLE_ADVANCED_RECIPE.get(),
                Config.RECIPE_OVERRIDE_RETURN_ADVANCED.get(), ADVANCED_RETURN_MIRROR.get(), "advanced_return_mirror");
        tryRegisterRecipe(manager, Config.ENABLE_PERMANENT_RECIPE.get(),
                Config.RECIPE_OVERRIDE_RETURN_PERMANENT.get(), PERMANENT_RETURN_MIRROR.get(), "permanent_return_mirror");
        tryRegisterRecipe(manager, Config.ENABLE_BASIC_RECIPE.get(),
                Config.RECIPE_OVERRIDE_TELEPORT_BASIC.get(), BASIC_TELEPORT_MIRROR.get(), "basic_teleport_mirror");
        tryRegisterRecipe(manager, Config.ENABLE_INTERMEDIATE_RECIPE.get(),
                Config.RECIPE_OVERRIDE_TELEPORT_INTERMEDIATE.get(), INTERMEDIATE_TELEPORT_MIRROR.get(), "intermediate_teleport_mirror");
        tryRegisterRecipe(manager, Config.ENABLE_ADVANCED_RECIPE.get(),
                Config.RECIPE_OVERRIDE_TELEPORT_ADVANCED.get(), ADVANCED_TELEPORT_MIRROR.get(), "advanced_teleport_mirror");
        tryRegisterRecipe(manager, Config.ENABLE_PERMANENT_RECIPE.get(),
                Config.RECIPE_OVERRIDE_TELEPORT_PERMANENT.get(), PERMANENT_TELEPORT_MIRROR.get(), "permanent_teleport_mirror");
    }

    @SuppressWarnings("unchecked")
    private void tryRegisterRecipe(net.minecraft.world.item.crafting.RecipeManager manager,
            boolean enabled, String override, Item result, String name) {
        if (!enabled) return;
        if (override.isBlank()) return;

        RecipeHolder<?> recipe = parseRecipeOverride(override, result, name);
        if (recipe == null) return;

        try {
            Field recipesField = manager.getClass().getDeclaredField("recipes");
            recipesField.setAccessible(true);
            Object recipeMap = recipesField.get(manager);

            Field byTypeField = recipeMap.getClass().getDeclaredField("byType");
            byTypeField.setAccessible(true);
            Map<RecipeType<?>, Map<ResourceLocation, RecipeHolder<?>>> byType =
                    (Map<RecipeType<?>, Map<ResourceLocation, RecipeHolder<?>>>) byTypeField.get(recipeMap);

            Map<ResourceLocation, RecipeHolder<?>> crafting = byType
                    .computeIfAbsent(RecipeType.CRAFTING, k -> new HashMap<>());
            crafting.put(recipe.id(), recipe);
        } catch (Exception e) {
            // Silently ignore - default JSON recipe will be used
        }
    }

    private RecipeHolder<?> parseRecipeOverride(String override, Item result, String name) {
        String[] parts = override.split("\\|");
        if (parts.length < 2) return null;

        String[] rows = parts[0].split(";");
        if (rows.length != 3) return null;

        Map<Character, Ingredient> key = new HashMap<>();
        String[] keyEntries = parts[1].split(";");
        for (String entry : keyEntries) {
            String[] kv = entry.split("=");
            if (kv.length != 2) continue;
            char k = kv[0].trim().charAt(0);
            String itemId = kv[1].trim();
            var item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
            key.put(k, Ingredient.of(item));
        }

        List<String> pattern = List.of(rows[0].trim(), rows[1].trim(), rows[2].trim());

        ShapedRecipePattern shapedPattern;
        try {
            shapedPattern = ShapedRecipePattern.of(key, pattern);
        } catch (Exception e) {
            return null;
        }

        ShapedRecipe recipe = new ShapedRecipe("", CraftingBookCategory.MISC, shapedPattern, new ItemStack(result));

        return new RecipeHolder<>(
                ResourceLocation.fromNamespaceAndPath(MODID, name + "_config"),
                recipe);
    }
}
