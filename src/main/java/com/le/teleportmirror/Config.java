package com.le.teleportmirror;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue COOLDOWN_SECONDS = BUILDER
            .comment("Cooldown in seconds between mirror uses")
            .defineInRange("cooldownSeconds", 60, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue CHARGE_TICKS = BUILDER
            .comment("Number of ticks required to charge the mirror (20 ticks = 1 second)")
            .defineInRange("chargeTicks", 40, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue ALLOW_CROSS_DIMENSION_BASIC = BUILDER
            .comment("Allow Basic tier mirrors to teleport across dimensions")
            .define("allowCrossDimension.basic", false);

    public static final ModConfigSpec.BooleanValue ALLOW_CROSS_DIMENSION_INTERMEDIATE = BUILDER
            .comment("Allow Intermediate tier mirrors to teleport across dimensions")
            .define("allowCrossDimension.intermediate", false);

    public static final ModConfigSpec.BooleanValue ALLOW_CROSS_DIMENSION_ADVANCED = BUILDER
            .comment("Allow Advanced tier mirrors to teleport across dimensions")
            .define("allowCrossDimension.advanced", true);

    public static final ModConfigSpec.BooleanValue ALLOW_CROSS_DIMENSION_PERMANENT = BUILDER
            .comment("Allow Permanent tier mirrors to teleport across dimensions")
            .define("allowCrossDimension.permanent", true);

    public static final ModConfigSpec.BooleanValue TELEPORT_TEAM_ONLY = BUILDER
            .comment("Teleport Mirror can only target players with the same team name (team tag).")
            .define("teleportTeamOnly", false);

    public static final ModConfigSpec.IntValue BASIC_DURABILITY = BUILDER
            .comment("Durability for Basic tier mirrors")
            .defineInRange("basicDurability", 10, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue INTERMEDIATE_DURABILITY = BUILDER
            .comment("Durability for Intermediate tier mirrors")
            .defineInRange("intermediateDurability", 50, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue ADVANCED_DURABILITY = BUILDER
            .comment("Durability for Advanced tier mirrors")
            .defineInRange("advancedDurability", 100, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<String> FOOD_LEVEL_BASIC = BUILDER
            .comment("Food level cost for Basic tier mirrors (half-shanks). \"50%\"=percentage, \"10\"=fixed, \"\"=none.")
            .define("foodCost.basic.foodLevel", "50%");

    public static final ModConfigSpec.ConfigValue<String> SATURATION_COST_BASIC = BUILDER
            .comment("Saturation cost for Basic tier mirrors. \"50%\"=percentage, \"4.0\"=fixed, \"\"=none.")
            .define("foodCost.basic.saturation", "50%");

    public static final ModConfigSpec.ConfigValue<String> FOOD_LEVEL_INTERMEDIATE = BUILDER
            .comment("Food level cost for Intermediate tier mirrors (half-shanks). \"50%\"=percentage, \"10\"=fixed, \"\"=none.")
            .define("foodCost.intermediate.foodLevel", "50%");

    public static final ModConfigSpec.ConfigValue<String> SATURATION_COST_INTERMEDIATE = BUILDER
            .comment("Saturation cost for Intermediate tier mirrors. \"50%\"=percentage, \"4.0\"=fixed, \"\"=none.")
            .define("foodCost.intermediate.saturation", "50%");

    public static final ModConfigSpec.ConfigValue<String> FOOD_LEVEL_ADVANCED = BUILDER
            .comment("Food level cost for Advanced tier mirrors (half-shanks). \"50%\"=percentage, \"10\"=fixed, \"\"=none.")
            .define("foodCost.advanced.foodLevel", "50%");

    public static final ModConfigSpec.ConfigValue<String> SATURATION_COST_ADVANCED = BUILDER
            .comment("Saturation cost for Advanced tier mirrors. \"50%\"=percentage, \"4.0\"=fixed, \"\"=none.")
            .define("foodCost.advanced.saturation", "50%");

    public static final ModConfigSpec.ConfigValue<String> FOOD_LEVEL_PERMANENT = BUILDER
            .comment("Food level cost for Permanent tier mirrors (half-shanks). \"50%\"=percentage, \"10\"=fixed, \"\"=none.")
            .define("foodCost.permanent.foodLevel", "");

    public static final ModConfigSpec.ConfigValue<String> SATURATION_COST_PERMANENT = BUILDER
            .comment("Saturation cost for Permanent tier mirrors. \"50%\"=percentage, \"4.0\"=fixed, \"\"=none.")
            .define("foodCost.permanent.saturation", "");

    public static final ModConfigSpec.ConfigValue<String> EFFECTS_RETURN_BASIC = BUILDER
            .comment("Effects for Basic Return Mirror. Format: \"effect_id,dur_sec,amplifier;...\"")
            .define("effects.return.basic", "minecraft:nausea,10,0;minecraft:wither,5,0");

    public static final ModConfigSpec.ConfigValue<String> EFFECTS_RETURN_INTERMEDIATE = BUILDER
            .comment("Effects for Intermediate Return Mirror. Format: \"effect_id,dur_sec,amplifier;...\"")
            .define("effects.return.intermediate", "minecraft:nausea,5,0;minecraft:wither,3,0");

    public static final ModConfigSpec.ConfigValue<String> EFFECTS_RETURN_ADVANCED = BUILDER
            .comment("Effects for Advanced Return Mirror. Format: \"effect_id,dur_sec,amplifier;...\"")
            .define("effects.return.advanced", "minecraft:nausea,3,0");

    public static final ModConfigSpec.ConfigValue<String> EFFECTS_RETURN_PERMANENT = BUILDER
            .comment("Effects for Permanent Return Mirror. Format: \"effect_id,dur_sec,amplifier;...\"")
            .define("effects.return.permanent", "");

    public static final ModConfigSpec.ConfigValue<String> EFFECTS_TELEPORT_BASIC = BUILDER
            .comment("Effects for Basic Teleport Mirror. Format: \"effect_id,dur_sec,amplifier;...\"")
            .define("effects.teleport.basic", "minecraft:nausea,10,0;minecraft:wither,5,0");

    public static final ModConfigSpec.ConfigValue<String> EFFECTS_TELEPORT_INTERMEDIATE = BUILDER
            .comment("Effects for Intermediate Teleport Mirror. Format: \"effect_id,dur_sec,amplifier;...\"")
            .define("effects.teleport.intermediate", "minecraft:nausea,5,0;minecraft:wither,3,0");

    public static final ModConfigSpec.ConfigValue<String> EFFECTS_TELEPORT_ADVANCED = BUILDER
            .comment("Effects for Advanced Teleport Mirror. Format: \"effect_id,dur_sec,amplifier;...\"")
            .define("effects.teleport.advanced", "minecraft:nausea,3,0");

    public static final ModConfigSpec.ConfigValue<String> EFFECTS_TELEPORT_PERMANENT = BUILDER
            .comment("Effects for Permanent Teleport Mirror. Format: \"effect_id,dur_sec,amplifier;...\"")
            .define("effects.teleport.permanent", "");

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

    public static final ModConfigSpec.ConfigValue<String> RECIPE_OVERRIDE_RETURN_BASIC = BUILDER
            .comment("Override recipe for Basic Return Mirror. Format: \"row1;row2;row3|key=item_id;...\". Empty=use default.")
            .define("recipeOverride.return.basic", "");

    public static final ModConfigSpec.ConfigValue<String> RECIPE_OVERRIDE_RETURN_INTERMEDIATE = BUILDER
            .comment("Override recipe for Intermediate Return Mirror.")
            .define("recipeOverride.return.intermediate", "");

    public static final ModConfigSpec.ConfigValue<String> RECIPE_OVERRIDE_RETURN_ADVANCED = BUILDER
            .comment("Override recipe for Advanced Return Mirror.")
            .define("recipeOverride.return.advanced", "");

    public static final ModConfigSpec.ConfigValue<String> RECIPE_OVERRIDE_RETURN_PERMANENT = BUILDER
            .comment("Override recipe for Permanent Return Mirror.")
            .define("recipeOverride.return.permanent", "");

    public static final ModConfigSpec.ConfigValue<String> RECIPE_OVERRIDE_TELEPORT_BASIC = BUILDER
            .comment("Override recipe for Basic Teleport Mirror.")
            .define("recipeOverride.teleport.basic", "");

    public static final ModConfigSpec.ConfigValue<String> RECIPE_OVERRIDE_TELEPORT_INTERMEDIATE = BUILDER
            .comment("Override recipe for Intermediate Teleport Mirror.")
            .define("recipeOverride.teleport.intermediate", "");

    public static final ModConfigSpec.ConfigValue<String> RECIPE_OVERRIDE_TELEPORT_ADVANCED = BUILDER
            .comment("Override recipe for Advanced Teleport Mirror.")
            .define("recipeOverride.teleport.advanced", "");

    public static final ModConfigSpec.ConfigValue<String> RECIPE_OVERRIDE_TELEPORT_PERMANENT = BUILDER
            .comment("Override recipe for Permanent Teleport Mirror.")
            .define("recipeOverride.teleport.permanent", "");

    static final ModConfigSpec SPEC = BUILDER.build();
}
