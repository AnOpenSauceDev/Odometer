package me.wolfie.odometer.listener;

import me.wolfie.odometer.Odometer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import static me.wolfie.odometer.Odometer.HealthMap;


//imagine doing spigot-esque code...
public class ServerListener implements ServerTickEvents.EndTick{
    boolean dead = false;
    public static MinecraftServer minecraftServer;
    public static void KillPlayer(PlayerEntity Player){

        ServerPlayerEntity serverPlayerEntity = minecraftServer.getPlayerManager().getPlayer(Player.getUuid());

        if(!serverPlayerEntity.isDead()) {
            serverPlayerEntity.setHealth(0);
            HealthMap.put(serverPlayerEntity.getUuidAsString(),0.0);
            serverPlayerEntity.onDeath(DamageSource.OUT_OF_WORLD);
        }



    }



    @Override
    public void onEndTick(MinecraftServer server) {
        minecraftServer = server;

    }


}
