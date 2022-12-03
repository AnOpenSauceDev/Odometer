package me.wolfie.odometer.mixins;

import me.wolfie.odometer.listener.ServerListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.Logger;

import static me.wolfie.odometer.Odometer.*;



// tends to be very fragile... handle with care.
@Mixin(PlayerEntity.class)
public class TickHandle {
    // Remember, 1 tick = 1/20th of a second
    @Inject(method = "tick", at = @At("HEAD"))
    public void Tick(CallbackInfo cInfo){
        if(MinecraftClient.getInstance().player != null) { // prevent mc utterly and totally nuking itself

            if (HealthMap.get(MinecraftClient.getInstance().getSession().getUuid()) != null) {

                //MinecraftClient.getInstance().player.setHealth(MinecraftClient.getInstance().player.getHealth());
                if(MinecraftClient.getInstance().player.getHealth() >= 1) {
                    // THIS CODE IS DISGUSTING, FIX IT LATER OR ELSE 😡😡😡😡😡😡😡😡😡 🤬🤬🤬🤬🤬
                    if (HealthMap.get(MinecraftClient.getInstance().getSession().getUuid()) != 0) {
                        if (HealthMap.get(MinecraftClient.getInstance().getSession().getUuid()) > 0) {
                            MinecraftClient.getInstance().player.setHealth(MinecraftClient.getInstance().player.getHealth() - (1 / 20F) /* / DecaySpeed */); // set to value. 1/20 because of ticks!!!
                            HealthMap.put(MinecraftClient.getInstance().getSession().getUuid(), HealthMap.get(MinecraftClient.getInstance().getSession().getUuid()) - (1.0 / 20.0) /* / DecaySpeed*/); // subtract the damage

                        }
                        // MinecraftClient.getInstance().player.sendMessage(Text.literal(String.valueOf("DMG = " + HealthMap.get(MinecraftClient.getInstance().getSession().getUuid()))));
                    }
                }else{
                    // handle deaths
                    if(HealthMap.get(MinecraftClient.getInstance().getSession().getUuid()) >= 1){



                        // yay redundancy!
                        PlayerEntity Player = MinecraftClient.getInstance().player;
                        LivingEntity livingEntity = (LivingEntity) MinecraftClient.getInstance().player;
                        ServerListener.KillPlayer(Player);
                        

                    }
                }
            }else {

                HealthMap.put(MinecraftClient.getInstance().getSession().getUuid(), Double.valueOf(0));
            }
        }



    }
}
