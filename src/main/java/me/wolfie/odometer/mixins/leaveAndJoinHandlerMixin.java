package me.wolfie.odometer.mixins;

//import me.wolfie.dataentry.util.ThreadedDataReader;
//import me.wolfie.dataentry.util.ThreadedDataWriter;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.FileNotFoundException;

import static me.wolfie.odometer.Odometer.HealthMap;

@Mixin(PlayerManager.class)
public class leaveAndJoinHandlerMixin {



    @Inject(method = "remove", at = @At("HEAD"))
    public void quit(ServerPlayerEntity player, CallbackInfo ci){ // set their health to what it would tick down to, before handling quitting
        player.setHealth(player.getHealth() - HealthMap.get(player.getUuidAsString()).floatValue());
        System.out.println("v = " + (player.getHealth() - HealthMap.get(player.getUuidAsString()).floatValue()));
        HealthMap.put(player.getUuidAsString(),0.0);

    }

}
