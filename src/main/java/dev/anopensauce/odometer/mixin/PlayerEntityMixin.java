package dev.anopensauce.odometer.mixin;

import dev.anopensauce.odometer.Odometer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Unique
    private static float rollingDamageAmount = 0;

    @Unique
    private static final float rate = 0.5f; // used to be DPS, scales with damage now.


    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow public abstract boolean isInvulnerableTo(DamageSource damageSource);

    @Shadow public abstract void increaseStat(Identifier stat, int amount);

    @Shadow public abstract void addExhaustion(float exhaustion);


    /**
     * @author AnOpenSauceDev
     * @reason not have to fight minecraft for now, TODO: make into an inject
     */
    @Overwrite
    public void applyDamage(DamageSource source, float amount) {
        if (!isInvulnerableTo(source)) {
            amount = this.applyArmorToDamage(source, amount);
            amount = this.modifyAppliedDamage(source, amount);
            float var7 = Math.max(amount - this.getAbsorptionAmount(), 0.0F);
            // instantly damage absorb, so it won't be unfair if it wears off post-damage.
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (amount - var7));
            float g = amount - var7;
            if (g > 0.0F && g < 3.4028235E37F) {
                increaseStat(Stats.DAMAGE_ABSORBED, Math.round(g * 10.0F));
            }


            if (var7 != 0.0F) {
                addExhaustion(source.getExhaustion());

                //this.getDamageTracker().onDamage(source, var7);

                // handle ourselves
                // this.setHealth(this.getHealth() - var7);

                rollingDamageAmount += var7;

                if (var7 < 3.4028235E37F) {
                    this.increaseStat(Stats.DAMAGE_TAKEN, Math.round(var7 * 10.0F));
                }

                this.emitGameEvent(GameEvent.ENTITY_DAMAGE);
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void odometer$handleDamageRoll(CallbackInfo ci){
        // convert to ticks, then to our rate

        if(!getWorld().isClient){
            if(rollingDamageAmount > 0.05f ) {

                float amount = (0.20f) * rate;

                setHealth(getHealth() - amount);

                if(getHealth() <= 0){
                    rollingDamageAmount = 0.0f;
                }

                rollingDamageAmount -= amount;

                ServerPlayNetworking.send((ServerPlayerEntity) (Entity) this,new Odometer.statusUpdate(rollingDamageAmount));


            }else{
                rollingDamageAmount = 0;
            }
        }

    }


}
