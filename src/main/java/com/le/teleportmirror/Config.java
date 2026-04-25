package com.le.teleportmirror;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue COOLDOWN_SECONDS = BUILDER
            .comment("Cooldown in seconds between mirror uses")
            .defineInRange("cooldownSeconds", 60, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue CHARGE_TICKS = BUILDER
            .comment("Number of ticks required to charge the mirror (20 ticks = 1 second)")
            .defineInRange("chargeTicks", 40, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue BASIC_DURABILITY = BUILDER
            .comment("Durability for Basic tier mirrors")
            .defineInRange("basicDurability", 10, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue INTERMEDIATE_DURABILITY = BUILDER
            .comment("Durability for Intermediate tier mirrors")
            .defineInRange("intermediateDurability", 50, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue ADVANCED_DURABILITY = BUILDER
            .comment("Durability for Advanced tier mirrors")
            .defineInRange("advancedDurability", 100, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue BASIC_NAUSEA_SECONDS = BUILDER
            .comment("Nausea effect duration in seconds for Basic tier")
            .defineInRange("basicNauseaSeconds", 10, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue BASIC_WITHER_SECONDS = BUILDER
            .comment("Wither effect duration in seconds for Basic tier")
            .defineInRange("basicWitherSeconds", 5, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue INTERMEDIATE_NAUSEA_SECONDS = BUILDER
            .comment("Nausea effect duration in seconds for Intermediate tier")
            .defineInRange("intermediateNauseaSeconds", 5, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue INTERMEDIATE_WITHER_SECONDS = BUILDER
            .comment("Wither effect duration in seconds for Intermediate tier")
            .defineInRange("intermediateWitherSeconds", 3, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue ADVANCED_NAUSEA_SECONDS = BUILDER
            .comment("Nausea effect duration in seconds for Advanced tier")
            .defineInRange("advancedNauseaSeconds", 3, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue ENABLE_BASIC_RECIPE = BUILDER
            .comment("Enable crafting recipe for Basic tier mirrors")
            .define("enableBasicRecipe", true);

    public static final ModConfigSpec.BooleanValue ENABLE_INTERMEDIATE_RECIPE = BUILDER
            .comment("Enable crafting recipe for Intermediate tier mirrors")
            .define("enableIntermediateRecipe", true);

    public static final ModConfigSpec.BooleanValue ENABLE_ADVANCED_RECIPE = BUILDER
            .comment("Enable crafting recipe for Advanced tier mirrors")
            .define("enableAdvancedRecipe", true);

    public static final ModConfigSpec.BooleanValue ENABLE_PERMANENT_RECIPE = BUILDER
            .comment("Enable crafting recipe for Permanent tier mirrors")
            .define("enablePermanentRecipe", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}
