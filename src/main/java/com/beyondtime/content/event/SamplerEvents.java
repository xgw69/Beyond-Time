package com.beyondtime.content.event;

import java.util.Objects;

import com.beyondtime.BeyondTime;
import com.beyondtime.content.item.SamplerItem;
import com.beyondtime.content.item.TissueSampleItem;
import com.beyondtime.content.tissue.TissueSample;
import com.beyondtime.registry.BTEntityTags;
import com.beyondtime.registry.BTItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

/**
 * Turns a killing blow from a sampler into a sealed tissue sample.
 *
 * <p>Handled on the death event rather than in {@code hurtEnemy}: only the death event knows that the
 * creature actually died, and only it can add to the drop list without risking a second sample from
 * the same creature. The samplable list is a tag ({@link BTEntityTags#SAMPLABLE}), so adding a
 * creature is a JSON edit.
 */
@EventBusSubscriber(modid = BeyondTime.MODID)
public final class SamplerEvents {
    private SamplerEvents() {}

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity killed = event.getEntity();
        if (killed.level().isClientSide()) {
            return;
        }

        // Only a real melee blow with the sampler counts. A projectile fired while holding one does
        // not, which keeps "kill it with the tool" literal.
        Entity attacker = event.getSource().getDirectEntity();
        if (!(attacker instanceof ServerPlayer player)) {
            return;
        }

        ItemStack weapon = player.getMainHandItem();
        if (!weapon.is(BTItems.SAMPLER.get())) {
            return;
        }

        if (killed.getType().getTags().noneMatch(BTEntityTags.SAMPLABLE::equals)) {
            return;
        }

        if (!(killed.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Identifier entityId = Objects.requireNonNull(
                BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()), "unregistered entity type");
        TissueSample sample = TissueSample.fresh(
                entityId,
                killed.level().dimension().identifier(),
                killed.level().getRandom().nextLong());

        // The sixteen-point killing cost is paid before the sample is released. A sampler with 16 or
        // fewer points left therefore breaks on the fourth kill and yields nothing, which is the
        // "64 durability = exactly three samples" arithmetic in docs/MACHINES.md 2.1.
        weapon.hurtAndBreak(
                SamplerItem.KILL_COST,
                serverLevel,
                player,
                broken -> player.onEquippedItemBroken(broken, EquipmentSlot.MAINHAND));
        if (weapon.isEmpty()) {
            return;
        }

        event.getDrops().add(new ItemEntity(
                killed.level(), killed.getX(), killed.getY() + 0.25, killed.getZ(), TissueSampleItem.of(sample)));
    }
}
