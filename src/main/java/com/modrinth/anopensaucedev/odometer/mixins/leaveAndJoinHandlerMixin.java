package com.modrinth.anopensaucedev.odometer.mixins;

//import me.wolfie.dataentry.util.ThreadedDataReader;
//import me.wolfie.dataentry.util.ThreadedDataWriter;
import com.modrinth.anopensaucedev.odometer.Odometer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class leaveAndJoinHandlerMixin {



    @Inject(method = "remove", at = @At("HEAD"))
    public void quit(ServerPlayerEntity player, CallbackInfo ci){ // set their health to what it would tick down to, before handling quitting
        player.setHealth(player.getHealth() - Odometer.HealthMap.get(player.getUuidAsString()).floatValue());
        System.out.println("v = " + (player.getHealth() - Odometer.HealthMap.get(player.getUuidAsString()).floatValue()));
        Odometer.HealthMap.put(player.getUuidAsString(),0.0);

    }

}
