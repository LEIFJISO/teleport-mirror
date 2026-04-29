package com.le.teleportmirror;

/**
 * 魔镜的功能类型
 * <p>
 * RETURN - 回城魔镜：将使用者传送回出生点
 * TELEPORT - 传送魔镜：将使用者传送到另一个玩家身边
 */
public enum MirrorType {
    RETURN("return"),
    TELEPORT("teleport");

    /** 类型名称，用于注册和序列化 */
    private final String name;

    MirrorType(String name) {
        this.name = name;
    }

    /** 获取类型名称 */
    public String getName() {
        return name;
    }
}
