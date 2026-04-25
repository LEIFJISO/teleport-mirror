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

public class MirrorItem extends Item {
    private final MirrorTier tier;
    private final MirrorType type;

    public MirrorItem(MirrorTier tier, MirrorType type, Properties properties) {
        super(properties);
        this.tier = tier;
        this.type = type;
    }

    public MirrorTier getTier() {
        return tier;
    }

    public MirrorType getMirrorType() {
        return type;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

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

    private boolean canCrossDimension() {
        return switch (tier) {
            case BASIC -> Config.ALLOW_CROSS_DIMENSION_BASIC.get();
            case INTERMEDIATE -> Config.ALLOW_CROSS_DIMENSION_INTERMEDIATE.get();
            case ADVANCED -> Config.ALLOW_CROSS_DIMENSION_ADVANCED.get();
            case PERMANENT -> Config.ALLOW_CROSS_DIMENSION_PERMANENT.get();
        };
    }

    private void performReturnTeleport(ServerPlayer player, ItemStack stack) {
        ServerLevel serverLevel = player.serverLevel();
        BlockPos targetPos;
        ServerLevel targetLevel;
        ResourceKey<Level> respawnDimension = player.getRespawnDimension();
        BlockPos respawnPos = player.getRespawnPosition();

        if (respawnPos != null) {
            targetLevel = player.server.getLevel(respawnDimension);
            if (targetLevel == null) {
                targetLevel = serverLevel;
            }
            targetPos = respawnPos;
        } else {
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

    private int getConfigMaxDamage() {
        return switch (tier) {
            case BASIC -> Config.BASIC_DURABILITY.get();
            case INTERMEDIATE -> Config.INTERMEDIATE_DURABILITY.get();
            case ADVANCED -> Config.ADVANCED_DURABILITY.get();
            case PERMANENT -> Integer.MAX_VALUE;
        };
    }

    private void consumeMirrorUse(ServerPlayer player, ItemStack stack) {
        applySideEffects(player);
        applyCooldown(player);

        if (!tier.isPermanent()) {
            int maxDamage = getConfigMaxDamage();
            if (stack.getDamageValue() + 1 >= maxDamage) {
                stack.shrink(1);
                player.level().playSound(null, player.blockPosition(),
                        SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0f, 1.0f);
            } else {
                stack.setDamageValue(stack.getDamageValue() + 1);
            }
        }
    }

    private void applyCooldown(Player player) {
        int cooldownTicks = Config.COOLDOWN_SECONDS.get() * 20;
        player.getCooldowns().addCooldown(this, cooldownTicks);
    }

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

    private boolean shouldHalveFood() {
        return switch (tier) {
            case BASIC -> Config.HALVE_FOOD_BASIC.get();
            case INTERMEDIATE -> Config.HALVE_FOOD_INTERMEDIATE.get();
            case ADVANCED -> Config.HALVE_FOOD_ADVANCED.get();
            case PERMANENT -> Config.HALVE_FOOD_PERMANENT.get();
        };
    }

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

        if (shouldHalveFood()) {
            halveFood(player);
        }
    }

    private void halveFood(Player player) {
        FoodData foodData = player.getFoodData();
        foodData.setFoodLevel(Math.max(1, foodData.getFoodLevel() / 2));
        foodData.setSaturation(0);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return tier == MirrorTier.PERMANENT;
    }
}
