package com.le.teleportmirror;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * 魔镜物品类
 * <p>
 * 魔镜是一个可蓄力使用的物品，长按右键蓄力完成后触发传送效果。
 * 支持两种功能类型（回城/传送）和四个等级（初级/中级/高级/永久）。
 * <p>
 * 使用流程：
 * <ol>
 *   <li>玩家右键开始蓄力（显示弓的动画）</li>
 *   <li>达到蓄力时间后触发效果</li>
 *   <li>回城魔镜直接传送；传送魔镜打开玩家选择界面</li>
 *   <li>消耗耐久、附加副作用、设置冷却</li>
 * </ol>
 */
public class MirrorItem extends Item {
    /** 魔镜等级 */
    private final MirrorTier tier;
    /** 魔镜功能类型 */
    private final MirrorType type;

    /**
     * @param tier 魔镜等级
     * @param type 魔镜类型（回城或传送）
     * @param properties 物品属性（耐久度、堆叠数等）
     */
    public MirrorItem(MirrorTier tier, MirrorType type, Properties properties) {
        super(properties);
        this.tier = tier;
        this.type = type;
    }

    /** 获取魔镜等级 */
    public MirrorTier getTier() {
        return tier;
    }

    /** 获取魔镜功能类型 */
    public MirrorType getMirrorType() {
        return type;
    }

    /** 使用弓的拉动动画来表现蓄力过程 */
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    /** 允许无限蓄力（实际在 onUseTick 中检测是否达到蓄力阈值） */
    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    /** 右键开始使用魔镜，若在冷却中则无法使用 */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    /**
     * 每 tick 调用，检测蓄力是否达到设定阈值。
     * 到达阈值后停止蓄力，并根据魔镜类型执行回城传送或打开玩家选择界面。
     */
    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide) {
            return;
        }

        int chargeTicks = Config.CHARGE_TICKS.get();
        int usedTicks = getUseDuration(stack, livingEntity) - remainingUseDuration;

        if (usedTicks >= chargeTicks) {
            if (!(livingEntity instanceof ServerPlayer serverPlayer)) {
                return;
            }

            livingEntity.stopUsingItem();

            if (type == MirrorType.RETURN) {
                performReturnTeleport(serverPlayer, stack);
            } else {
                MirrorNetwork.sendOpenSelectionToClient(serverPlayer, tier);
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
    }

    /**
     * 检查当前等级的魔镜是否允许跨维度传送
     */
    private boolean canCrossDimension() {
        return switch (tier) {
            case BASIC -> Config.ALLOW_CROSS_DIMENSION_BASIC.get();
            case INTERMEDIATE -> Config.ALLOW_CROSS_DIMENSION_INTERMEDIATE.get();
            case ADVANCED -> Config.ALLOW_CROSS_DIMENSION_ADVANCED.get();
            case PERMANENT -> Config.ALLOW_CROSS_DIMENSION_PERMANENT.get();
        };
    }

    /**
     * 执行回城传送：将玩家传送到其重生点或世界出生点
     *
     * @param player 使用魔镜的玩家
     * @param stack 魔镜物品堆
     */
    private void performReturnTeleport(ServerPlayer player, ItemStack stack) {
        ServerLevel serverLevel = player.serverLevel();
        BlockPos targetPos;
        ServerLevel targetLevel;
        ResourceKey<Level> respawnDimension = player.getRespawnDimension();
        BlockPos respawnPos = player.getRespawnPosition();

        // 优先使用玩家的重生点（床/重生锚）
        if (respawnPos != null) {
            targetLevel = player.server.getLevel(respawnDimension);
            if (targetLevel == null) {
                targetLevel = serverLevel;
            }
            targetPos = respawnPos;
        } else {
            // 否则使用主世界的全局出生点
            targetLevel = player.server.getLevel(Level.OVERWORLD);
            if (targetLevel == null) {
                targetLevel = serverLevel;
            }
            targetPos = targetLevel.getSharedSpawnPos();
        }

        if (!canCrossDimension() && targetLevel.dimension() != serverLevel.dimension()) {
            return;
        }

        consumeMirrorUse(player, stack);

        player.teleportTo(targetLevel, targetPos.getCenter().x, targetPos.getCenter().y, targetPos.getCenter().z,
                player.getYRot(), player.getXRot());

        targetLevel.playSound(null, targetPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    /**
     * 执行传送到目标玩家身边
     *
     * @param player 使用魔镜的玩家
     * @param target 目标玩家
     * @param stack 魔镜物品堆
     */
    public void performTeleportToPlayer(ServerPlayer player, ServerPlayer target, ItemStack stack) {
        ServerLevel targetLevel = target.serverLevel();
        BlockPos targetPos = target.blockPosition();

        if (!canCrossDimension() && targetLevel.dimension() != player.serverLevel().dimension()) {
            return;
        }

        consumeMirrorUse(player, stack);

        player.teleportTo(targetLevel, targetPos.getCenter().x, targetPos.getCenter().y, targetPos.getCenter().z,
                player.getYRot(), player.getXRot());

        targetLevel.playSound(null, targetPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    /**
     * 获取配置中的最大耐久度
     * 永久等级返回 {@link Integer#MAX_VALUE}
     */
    private int getConfigMaxDamage() {
        return switch (tier) {
            case BASIC -> Config.BASIC_DURABILITY.get();
            case INTERMEDIATE -> Config.INTERMEDIATE_DURABILITY.get();
            case ADVANCED -> Config.ADVANCED_DURABILITY.get();
            case PERMANENT -> Integer.MAX_VALUE;
        };
    }

    /**
     * 消耗一次魔镜使用：附加副作用、设置冷却、扣除耐久
     */
    private void consumeMirrorUse(ServerPlayer player, ItemStack stack) {
        applySideEffects(player);
        applyCooldown(player);

        if (!tier.isPermanent()) {
            int maxDamage = getConfigMaxDamage();
            if (stack.getDamageValue() + 1 >= maxDamage) {
                // 耐久耗尽，物品销毁
                stack.shrink(1);
                player.level().playSound(null, player.blockPosition(),
                        SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0f, 1.0f);
            } else {
                stack.setDamageValue(stack.getDamageValue() + 1);
            }
        }
    }

    /** 设置冷却时间（以 tick 为单位，1秒=20tick） */
    private void applyCooldown(Player player) {
        int cooldownTicks = Config.COOLDOWN_SECONDS.get() * 20;
        player.getCooldowns().addCooldown(this, cooldownTicks);
    }

    /** 根据魔镜类型和等级获取副作用配置字符串 */
    private String getEffectsConfig() {
        return switch (type) {
            case RETURN -> switch (tier) {
                case BASIC -> Config.EFFECTS_RETURN_BASIC.get();
                case INTERMEDIATE -> Config.EFFECTS_RETURN_INTERMEDIATE.get();
                case ADVANCED -> Config.EFFECTS_RETURN_ADVANCED.get();
                case PERMANENT -> Config.EFFECTS_RETURN_PERMANENT.get();
            };
            case TELEPORT -> switch (tier) {
                case BASIC -> Config.EFFECTS_TELEPORT_BASIC.get();
                case INTERMEDIATE -> Config.EFFECTS_TELEPORT_INTERMEDIATE.get();
                case ADVANCED -> Config.EFFECTS_TELEPORT_ADVANCED.get();
                case PERMANENT -> Config.EFFECTS_TELEPORT_PERMANENT.get();
            };
        };
    }

    /** 根据魔镜等级获取饱食度消耗配置字符串 */
    private String getFoodLevelConfig() {
        return switch (tier) {
            case BASIC -> Config.FOOD_LEVEL_BASIC.get();
            case INTERMEDIATE -> Config.FOOD_LEVEL_INTERMEDIATE.get();
            case ADVANCED -> Config.FOOD_LEVEL_ADVANCED.get();
            case PERMANENT -> Config.FOOD_LEVEL_PERMANENT.get();
        };
    }

    /** 根据魔镜等级获取饱和度消耗配置字符串 */
    private String getSaturationConfig() {
        return switch (tier) {
            case BASIC -> Config.SATURATION_COST_BASIC.get();
            case INTERMEDIATE -> Config.SATURATION_COST_INTERMEDIATE.get();
            case ADVANCED -> Config.SATURATION_COST_ADVANCED.get();
            case PERMANENT -> Config.SATURATION_COST_PERMANENT.get();
        };
    }

    /**
     * 给玩家附加副作用效果和饱食度消耗
     * 副作用配置格式："effect_id,dur_seconds,amplifier;..."
     */
    private void applySideEffects(Player player) {
        String effectsConfig = getEffectsConfig();
        if (effectsConfig == null || effectsConfig.isBlank()) {
            return;
        }

        String[] effectEntries = effectsConfig.split(";");
        for (String entry : effectEntries) {
            entry = entry.trim();
            if (entry.isEmpty()) continue;

            String[] parts = entry.split(",");
            if (parts.length < 3) continue;

            String effectId = parts[0].trim();
            int durationSeconds;
            int amplifier;
            try {
                durationSeconds = Integer.parseInt(parts[1].trim());
                amplifier = Integer.parseInt(parts[2].trim());
            } catch (NumberFormatException e) {
                continue;
            }

            if (durationSeconds <= 0) continue;

            var effectKey = ResourceKey.create(Registries.MOB_EFFECT,
                    ResourceLocation.parse(effectId));
            var effectHolder = BuiltInRegistries.MOB_EFFECT.getHolder(effectKey);
            effectHolder.ifPresent(holder ->
                    player.addEffect(new MobEffectInstance(holder, durationSeconds * 20, amplifier)));
        }

        applyFoodCost(player);
    }

    /** 扣减饱食度和饱和度 */
    private void applyFoodCost(Player player) {
        FoodData foodData = player.getFoodData();
        applyCostValue(getFoodLevelConfig(), (float) foodData.getFoodLevel(), newVal -> foodData.setFoodLevel(newVal.intValue()));
        applyCostValue(getSaturationConfig(), foodData.getSaturationLevel(), newVal -> foodData.setSaturation(newVal));
    }

    /**
     * 通用数值扣减方法
     * 支持百分比模式（以%结尾）和固定值模式
     *
     * @param config 配置字符串，如 "50%" 或 "4.0"
     * @param currentValue 当前数值
     * @param setter 数值设置回调
     */
    private void applyCostValue(String config, float currentValue, java.util.function.Consumer<Float> setter) {
        if (config == null || config.isBlank()) {
            return;
        }
        String trimmed = config.trim();
        if (trimmed.endsWith("%")) {
            String pctStr = trimmed.substring(0, trimmed.length() - 1);
            try {
                float percentage = Float.parseFloat(pctStr) / 100f;
                setter.accept(Math.max(0, currentValue * (1f - percentage)));
            } catch (NumberFormatException ignored) {
            }
        } else {
            try {
                float cost = Float.parseFloat(trimmed);
                setter.accept(Math.max(0, currentValue - cost));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    /** 魔镜不可附魔 */
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    /** 永久魔镜显示附魔光效 */
    @Override
    public boolean isFoil(ItemStack stack) {
        return tier == MirrorTier.PERMANENT;
    }
}
