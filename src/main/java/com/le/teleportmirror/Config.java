package com.le.teleportmirror;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // ==================== General ====================

    public static final ModConfigSpec.IntValue COOLDOWN_SECONDS = BUILDER
            .comment("Cooldown in seconds between mirror uses")
            .defineInRange("cooldownSeconds", 60, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue CHARGE_TICKS = BUILDER
            .comment("Ticks required to charge the mirror (20 ticks = 1 second)")
            .defineInRange("chargeTicks", 40, 1, Integer.MAX_VALUE);

    // ==================== Cross-Dimension ====================

    public static final ModConfigSpec.BooleanValue ALLOW_CROSS_DIMENSION_BASIC = BUILDER
            .comment("Allow Basic mirrors to teleport across dimensions")
            .define("allowCrossDimension.basic", false);

    public static final ModConfigSpec.BooleanValue ALLOW_CROSS_DIMENSION_INTERMEDIATE = BUILDER
            .comment("Allow Intermediate mirrors to teleport across dimensions")
            .define("allowCrossDimension.intermediate", false);

    public static final ModConfigSpec.BooleanValue ALLOW_CROSS_DIMENSION_ADVANCED = BUILDER
            .comment("Allow Advanced mirrors to teleport across dimensions")
            .define("allowCrossDimension.advanced", true);

    public static final ModConfigSpec.BooleanValue ALLOW_CROSS_DIMENSION_PERMANENT = BUILDER
            .comment("Allow Permanent mirrors to teleport across dimensions")
            .define("allowCrossDimension.permanent", true);

    // ==================== Teleport Restrictions ====================

    public static final ModConfigSpec.BooleanValue TELEPORT_TEAM_ONLY = BUILDER
            .comment("Teleport Mirror can only target players with the same team name (tag)")
            .define("teleportTeamOnly", false);

    // ==================== Durability ====================

    public static final ModConfigSpec.IntValue BASIC_DURABILITY = BUILDER
            .comment("Durability of Basic tier mirrors")
            .defineInRange("durability.basic", 10, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue INTERMEDIATE_DURABILITY = BUILDER
            .comment("Durability of Intermediate tier mirrors")
            .defineInRange("durability.intermediate", 50, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue ADVANCED_DURABILITY = BUILDER
            .comment("Durability of Advanced tier mirrors")
            .defineInRange("durability.advanced", 100, 0, Integer.MAX_VALUE);

    // Permanent tier has no configurable durability (unlimited)

    // ==================== Food Cost ====================

    // -- Basic --
    public static final ModConfigSpec.ConfigValue<String> FOOD_LEVEL_BASIC = BUILDER
            .comment("Food level cost for Basic mirrors. \"50%\"=percentage, \"10\"=fixed, \"\"=none")
            .define("foodCost.basic.foodLevel", "50%");

    public static final ModConfigSpec.ConfigValue<String> SATURATION_COST_BASIC = BUILDER
            .comment("Saturation cost for Basic mirrors. \"50%\"=percentage, \"4.0\"=fixed, \"\"=none")
            .define("foodCost.basic.saturation", "50%");

    // -- Intermediate --
    public static final ModConfigSpec.ConfigValue<String> FOOD_LEVEL_INTERMEDIATE = BUILDER
            .comment("Food level cost for Intermediate mirrors. \"50%\"=percentage, \"10\"=fixed, \"\"=none")
            .define("foodCost.intermediate.foodLevel", "50%");

    public static final ModConfigSpec.ConfigValue<String> SATURATION_COST_INTERMEDIATE = BUILDER
            .comment("Saturation cost for Intermediate mirrors. \"50%\"=percentage, \"4.0\"=fixed, \"\"=none")
            .define("foodCost.intermediate.saturation", "50%");

    // -- Advanced --
    public static final ModConfigSpec.ConfigValue<String> FOOD_LEVEL_ADVANCED = BUILDER
            .comment("Food level cost for Advanced mirrors. \"50%\"=percentage, \"10\"=fixed, \"\"=none")
            .define("foodCost.advanced.foodLevel", "50%");

    public static final ModConfigSpec.ConfigValue<String> SATURATION_COST_ADVANCED = BUILDER
            .comment("Saturation cost for Advanced mirrors. \"50%\"=percentage, \"4.0\"=fixed, \"\"=none")
            .define("foodCost.advanced.saturation", "50%");

    // -- Permanent --
    public static final ModConfigSpec.ConfigValue<String> FOOD_LEVEL_PERMANENT = BUILDER
            .comment("Food level cost for Permanent mirrors. \"50%\"=percentage, \"10\"=fixed, \"\"=none")
            .define("foodCost.permanent.foodLevel", "");

    public static final ModConfigSpec.ConfigValue<String> SATURATION_COST_PERMANENT = BUILDER
            .comment("Saturation cost for Permanent mirrors. \"50%\"=percentage, \"4.0\"=fixed, \"\"=none")
            .define("foodCost.permanent.saturation", "");

    // ==================== Side Effects ====================
    // Format per entry: "effect_id,dur_seconds,amplifier;..."

    // -- Return Mirrors --
    public static final ModConfigSpec.ConfigValue<String> EFFECTS_RETURN_BASIC = BUILDER
            .comment("Side effects for Basic Return Mirror")
            .define("effects.return.basic", "minecraft:nausea,10,0;minecraft:wither,5,0");

    public static final ModConfigSpec.ConfigValue<String> EFFECTS_RETURN_INTERMEDIATE = BUILDER
            .comment("Side effects for Intermediate Return Mirror")
            .define("effects.return.intermediate", "minecraft:nausea,5,0;minecraft:wither,3,0");

    public static final ModConfigSpec.ConfigValue<String> EFFECTS_RETURN_ADVANCED = BUILDER
            .comment("Side effects for Advanced Return Mirror")
            .define("effects.return.advanced", "minecraft:nausea,3,0");

    public static final ModConfigSpec.ConfigValue<String> EFFECTS_RETURN_PERMANENT = BUILDER
            .comment("Side effects for Permanent Return Mirror")
            .define("effects.return.permanent", "");

    // -- Teleport Mirrors --
    public static final ModConfigSpec.ConfigValue<String> EFFECTS_TELEPORT_BASIC = BUILDER
            .comment("Side effects for Basic Teleport Mirror")
            .define("effects.teleport.basic", "minecraft:nausea,10,0;minecraft:wither,5,0");

    public static final ModConfigSpec.ConfigValue<String> EFFECTS_TELEPORT_INTERMEDIATE = BUILDER
            .comment("Side effects for Intermediate Teleport Mirror")
            .define("effects.teleport.intermediate", "minecraft:nausea,5,0;minecraft:wither,3,0");

    public static final ModConfigSpec.ConfigValue<String> EFFECTS_TELEPORT_ADVANCED = BUILDER
            .comment("Side effects for Advanced Teleport Mirror")
            .define("effects.teleport.advanced", "minecraft:nausea,3,0");

    public static final ModConfigSpec.ConfigValue<String> EFFECTS_TELEPORT_PERMANENT = BUILDER
            .comment("Side effects for Permanent Teleport Mirror")
            .define("effects.teleport.permanent", "");

    // ==================== Recipe Toggles ====================

    public static final ModConfigSpec.BooleanValue ENABLE_BASIC_RECIPE = BUILDER
            .comment("Enable crafting recipe for Basic tier mirrors")
            .define("enableRecipe.basic", true);

    public static final ModConfigSpec.BooleanValue ENABLE_INTERMEDIATE_RECIPE = BUILDER
            .comment("Enable crafting recipe for Intermediate tier mirrors")
            .define("enableRecipe.intermediate", true);

    public static final ModConfigSpec.BooleanValue ENABLE_ADVANCED_RECIPE = BUILDER
            .comment("Enable crafting recipe for Advanced tier mirrors")
            .define("enableRecipe.advanced", true);

    public static final ModConfigSpec.BooleanValue ENABLE_PERMANENT_RECIPE = BUILDER
            .comment("Enable crafting recipe for Permanent tier mirrors")
            .define("enableRecipe.permanent", true);

    // ==================== Recipe Overrides ====================
    // Format: "row1;row2;row3|key=item_id;..."
    // Example: " C ;CGC; C |C=minecraft:copper_ingot;G=minecraft:glass_pane"
    // Empty "" means use the default JSON recipe.

    // -- Return Mirrors --
    public static final ModConfigSpec.ConfigValue<String> RECIPE_OVERRIDE_RETURN_BASIC = BUILDER
            .comment("Override recipe for Basic Return Mirror. Empty = use default.")
            .define("recipeOverride.return.basic", "");

    public static final ModConfigSpec.ConfigValue<String> RECIPE_OVERRIDE_RETURN_INTERMEDIATE = BUILDER
            .comment("Override recipe for Intermediate Return Mirror. Empty = use default.")
            .define("recipeOverride.return.intermediate", "");

    public static final ModConfigSpec.ConfigValue<String> RECIPE_OVERRIDE_RETURN_ADVANCED = BUILDER
            .comment("Override recipe for Advanced Return Mirror. Empty = use default.")
            .define("recipeOverride.return.advanced", "");

    public static final ModConfigSpec.ConfigValue<String> RECIPE_OVERRIDE_RETURN_PERMANENT = BUILDER
            .comment("Override recipe for Permanent Return Mirror. Empty = use default.")
            .define("recipeOverride.return.permanent", "");

    // -- Teleport Mirrors --
    public static final ModConfigSpec.ConfigValue<String> RECIPE_OVERRIDE_TELEPORT_BASIC = BUILDER
            .comment("Override recipe for Basic Teleport Mirror. Empty = use default.")
            .define("recipeOverride.teleport.basic", "");

    public static final ModConfigSpec.ConfigValue<String> RECIPE_OVERRIDE_TELEPORT_INTERMEDIATE = BUILDER
            .comment("Override recipe for Intermediate Teleport Mirror. Empty = use default.")
            .define("recipeOverride.teleport.intermediate", "");

    public static final ModConfigSpec.ConfigValue<String> RECIPE_OVERRIDE_TELEPORT_ADVANCED = BUILDER
            .comment("Override recipe for Advanced Teleport Mirror. Empty = use default.")
            .define("recipeOverride.teleport.advanced", "");

    public static final ModConfigSpec.ConfigValue<String> RECIPE_OVERRIDE_TELEPORT_PERMANENT = BUILDER
            .comment("Override recipe for Permanent Teleport Mirror. Empty = use default.")
            .define("recipeOverride.teleport.permanent", "");

    static final ModConfigSpec SPEC = BUILDER.build();
}
