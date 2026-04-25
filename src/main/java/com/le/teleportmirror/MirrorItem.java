package com.le.teleportmirror;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodData;

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

        consumeMirrorUse(player, stack);

        player.teleportTo(targetLevel, targetPos.getCenter().x, targetPos.getCenter().y, targetPos.getCenter().z,
                player.getYRot(), player.getXRot());

        targetLevel.playSound(null, targetPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    public void performTeleportToPlayer(ServerPlayer player, ServerPlayer target, ItemStack stack) {
        ServerLevel targetLevel = target.serverLevel();
        BlockPos targetPos = target.blockPosition();

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

    private void applySideEffects(Player player) {
        int nauseaDuration;
        int witherDuration;

        switch (tier) {
            case BASIC:
                nauseaDuration = Config.BASIC_NAUSEA_SECONDS.get() * 20;
                witherDuration = Config.BASIC_WITHER_SECONDS.get() * 20;
                if (nauseaDuration > 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, nauseaDuration, 0));
                }
                if (witherDuration > 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.WITHER, witherDuration, 0));
                }
                halveFood(player);
                break;
            case INTERMEDIATE:
                nauseaDuration = Config.INTERMEDIATE_NAUSEA_SECONDS.get() * 20;
                witherDuration = Config.INTERMEDIATE_WITHER_SECONDS.get() * 20;
                if (nauseaDuration > 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, nauseaDuration, 0));
                }
                if (witherDuration > 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.WITHER, witherDuration, 0));
                }
                halveFood(player);
                break;
            case ADVANCED:
                nauseaDuration = Config.ADVANCED_NAUSEA_SECONDS.get() * 20;
                if (nauseaDuration > 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, nauseaDuration, 0));
                }
                halveFood(player);
                break;
            case PERMANENT:
                break;
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
