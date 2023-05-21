package me.wolfie.odometer.mixins;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static me.wolfie.odometer.Odometer.HealthMap;
import static me.wolfie.odometer.listener.ServerListener.minecraftServer;

@Mixin(PlayerEntity.class)
public abstract class DamageHandlerPlayerEntity extends LivingEntity {
    protected DamageHandlerPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }






    @Shadow public abstract boolean isInvulnerableTo(DamageSource damageSource);

    @Shadow public abstract PlayerInventory getInventory();

    @Shadow public abstract void increaseStat(Identifier stat, int amount);

    @Shadow public abstract void addExhaustion(float exhaustion);

    @Shadow public abstract void setAbsorptionAmount(float amount);

    @Shadow public abstract float getAbsorptionAmount();


    @Shadow public abstract Text getName();

    /**
     * @author no
     * @reason because
     */
    @Overwrite // jeez this is a mess
    public void applyDamage(DamageSource source, float amount){
        if(this != null) { // permanently soil a vanilla function forever thanks to one edge-case.

            if(this.isInvulnerableTo(source)){
                return;
            }
            DamageHandlerPlayerEntity Player = this;


            amount = oh_my_god(source, this, amount); // ever heard of "shadowing?"
            float f = amount = modifyAppliedDamage(source, amount, this);
            amount = Math.max(amount - Player.getAbsorptionAmount(), 0.0f);
            Player.setAbsorptionAmount(Player.getAbsorptionAmount() - (f - amount));
            float g = f - amount;
            if (g > 0.0f && g < 3.4028235E37f) {
                Player.increaseStat(Stats.DAMAGE_ABSORBED, Math.round(g * 10.0f));
            }
            if (amount == 0.0f) {
                return;
            }
            Player.addExhaustion(source.getExhaustion());
            float h = Player.getHealth();




            HealthMap.put(Player.getUuidAsString(), HealthMap.get(Player.getUuidAsString()) + amount); // amount of _damage_ taken. Keep in mind.

            Player.getDamageTracker().onDamage(source, h, amount);
            if (amount < 3.4028235E37f) {
                Player.increaseStat(Stats.DAMAGE_TAKEN, Math.round(amount * 10.0f));
            }
        }
    }


    // applyarmortodamage
    public float oh_my_god(DamageSource source, DamageHandlerPlayerEntity playerEntity, float amount){
        if (!source.bypassesArmor()) {
             damageArmor(source, amount,playerEntity);
            amount = DamageUtil.getDamageLeft(amount, playerEntity.getArmor(), (float)playerEntity.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
        }
        return amount;
    }

    protected void damageArmor(DamageSource source, float amount, DamageHandlerPlayerEntity player) {
        player.getInventory().damageArmor(source,amount,PlayerInventory.ARMOR_SLOTS);
    }

    protected float modifyAppliedDamage(DamageSource source, float amount, DamageHandlerPlayerEntity PE) {
        int i;
        int j;
        float f;
        float g;
        float h;
        if (source.isUnblockable()) {
            return amount;
        }
        if (PE.hasStatusEffect(StatusEffects.RESISTANCE) && !PE.getRecentDamageSource().isOutOfWorld() && (h = (g = amount) - (amount = Math.max((f = amount * (float)(j = 25 - (i = (PE.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5))) / 25.0f, 0.0f))) > 0.0f && h < 3.4028235E37f) {
            if(minecraftServer != null) {
                if (minecraftServer.getPlayerManager().getPlayer(PE.getName().getString()) != null) {
                    (minecraftServer.getPlayerManager().getPlayer(PE.getName().getString())).increaseStat(Stats.DAMAGE_RESISTED, Math.round(h * 10.0f));
                } else if (source.getAttacker() instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity) source.getAttacker()).increaseStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(h * 10.0f));
                }
            }


        }
        if (amount <= 0.0f) {
            return 0.0f;
        }
        if (source.bypassesArmor()) {
            return amount;
        }
        i = EnchantmentHelper.getProtectionAmount(PE.getArmorItems(), source);
        if (i > 0) {
            amount = DamageUtil.getInflictedDamage(amount, i);
        }
        return amount;
    }


}
