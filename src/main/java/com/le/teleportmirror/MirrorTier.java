package com.le.teleportmirror;

/**
 * 魔镜的等级
 * <p>
 * 不同等级影响耐久度、冷却时间、副作用强度等属性
 * <ul>
 *   <li>BASIC (初级) - 默认耐久10</li>
 *   <li>INTERMEDIATE (中级) - 默认耐久50</li>
 *   <li>ADVANCED (高级) - 默认耐久100</li>
 *   <li>PERMANENT (永久) - 无限耐久，无副作用</li>
 * </ul>
 */
public enum MirrorTier {
    BASIC(10, "basic"),
    INTERMEDIATE(50, "intermediate"),
    ADVANCED(100, "advanced"),
    PERMANENT(-1, "permanent");

    /** 默认耐久度 */
    private final int defaultDurability;
    /** 等级名称，用于注册和序列化 */
    private final String name;

    MirrorTier(int defaultDurability, String name) {
        this.defaultDurability = defaultDurability;
        this.name = name;
    }

    /** 获取该等级的默认耐久度 */
    public int getDefaultDurability() {
        return defaultDurability;
    }

    /** 获取等级名称 */
    public String getName() {
        return name;
    }

    /** 是否为永久等级（无限耐久） */
    public boolean isPermanent() {
        return this == PERMANENT;
    }
}
