package com.le.teleportmirror;

public enum MirrorTier {
    BASIC(10, "basic"),
    INTERMEDIATE(50, "intermediate"),
    ADVANCED(100, "advanced"),
    PERMANENT(-1, "permanent");

    private final int defaultDurability;
    private final String name;

    MirrorTier(int defaultDurability, String name) {
        this.defaultDurability = defaultDurability;
        this.name = name;
    }

    public int getDefaultDurability() {
        return defaultDurability;
    }

    public String getName() {
        return name;
    }

    public boolean isPermanent() {
        return this == PERMANENT;
    }
}
