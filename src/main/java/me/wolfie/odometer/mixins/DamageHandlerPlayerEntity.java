package me.wolfie.odometer.mixins;

import me.wolfie.odometer.listener.ServerListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static me.wolfie.odometer.Odometer.HealthMap;
import static me.wolfie.odometer.Odometer.LOGGER;

@Mixin(PlayerEntity.class)
public abstract class DamageHandlerPlayerEntity {

    /*
    @Inject(method = "applyDamage", at = @At("TAIL")) // we actually don't have to run the hash check here- its redundant due to it being set, so dw.
    public void handleHealthChange(DamageSource source, float amount, CallbackInfo ci) {

        if (MinecraftClient.getInstance().player != null) { // prevent mc utterly and totally nuking itself... again




        }

    }
    */


    //side note, MOJANG'S SPAGHETTI CODE IS DRIVING ME INSANE. I SPENT 2 DAYS DEBUGGING THIS STUPID MESS, AND NOW I HAVE RE-WRITTEN THE WHOLE METHOD.
    /**
     * @author no
     * @reason because
     */
    @Overwrite // player will be the replacement, k?
    public void applyDamage(DamageSource source, float amount){
        PlayerEntity Player = MinecraftClient.getInstance().player;
        if(Player != null) { // permanently soil a vanilla function forever thanks to one edge-case.

            if (Player.isInvulnerableTo(source)) {
                return;
            }

            if(source.isOutOfWorld()){


                ServerListener.KillPlayer(Player);



            }

            LivingEntity playerLivingEntity = (LivingEntity) Player;  // player = extended LivingEntity

            amount = oh_my_god(source, Player, amount);
            float f = amount = modifyAppliedDamage(source, amount, Player);
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
            //Player.setHealth(Player.getHealth() - amount); screw this line of code


            MinecraftClient player = MinecraftClient.getInstance();
            HealthMap.put(player.getSession().getUuid(), HealthMap.get(player.getSession().getUuid()) + Double.parseDouble(String.valueOf(amount))); // amount of _damage_ taken. Keep in mind.

            Player.getDamageTracker().onDamage(source, h, amount);
            if (amount < 3.4028235E37f) {
                Player.increaseStat(Stats.DAMAGE_TAKEN, Math.round(amount * 10.0f));
            }
        }
    }

    // applyarmortodamage
    public float oh_my_god(DamageSource source, PlayerEntity playerEntity, float amount){
        if (!source.bypassesArmor()) {
             damageArmor(source, amount,playerEntity);
            amount = DamageUtil.getDamageLeft(amount, playerEntity.getArmor(), (float)playerEntity.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
        }
        return amount;
    }

    protected void damageArmor(DamageSource source, float amount, PlayerEntity player) {
        player.getInventory().damageArmor(source, amount, PlayerInventory.ARMOR_SLOTS);
    }

    protected float modifyAppliedDamage(DamageSource source, float amount, PlayerEntity PE) {
        int i;
        int j;
        float f;
        float g;
        float h;
        if (source.isUnblockable()) {
            return amount;
        }
        if (PE.hasStatusEffect(StatusEffects.RESISTANCE) && source != DamageSource.OUT_OF_WORLD && (h = (g = amount) - (amount = Math.max((f = amount * (float)(j = 25 - (i = (PE.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5))) / 25.0f, 0.0f))) > 0.0f && h < 3.4028235E37f) {
            if (PE instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)PE).increaseStat(Stats.DAMAGE_RESISTED, Math.round(h * 10.0f));
            } else if (source.getAttacker() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)source.getAttacker()).increaseStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(h * 10.0f));
            }
        }
        if (amount <= 0.0f) {
            return 0.0f;
        }
        if (source.bypassesProtection()) {
            return amount;
        }
        i = EnchantmentHelper.getProtectionAmount(PE.getArmorItems(), source);
        if (i > 0) {
            amount = DamageUtil.getInflictedDamage(amount, i);
        }
        return amount;
    }


}
