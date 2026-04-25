package com.le.teleportmirror;

public enum MirrorType {
    RETURN("return"),
    TELEPORT("teleport");

    private final String name;

    MirrorType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
