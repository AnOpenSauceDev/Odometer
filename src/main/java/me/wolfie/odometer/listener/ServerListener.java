package me.wolfie.odometer.listener;

import me.wolfie.odometer.Odometer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

import static me.wolfie.odometer.Odometer.*;


public class ServerListener implements ServerTickEvents.EndTick {
    public static PlayerEntity[] playerEntitiesArray;
    boolean dead = false;
    public static MinecraftServer minecraftServer;

    public static void KillPlayer(PlayerEntity Player) {
        ServerPlayerEntity serverPlayerEntity = minecraftServer.getPlayerManager().getPlayer(Player.getUuid());

        if (!serverPlayerEntity.isDead()) {
            serverPlayerEntity.setHealth(0);
            HealthMap.put(serverPlayerEntity.getUuidAsString(), 0.0);
            serverPlayerEntity.onDeath(serverPlayerEntity.getRecentDamageSource());
        }

    }

    public static float HEALTH_DECAY_CONSTANT;

    @Override
    public void onEndTick(MinecraftServer server) {
        HEALTH_DECAY_CONSTANT = config.decayRate;
        minecraftServer = server; // declare server

        List<ServerPlayerEntity> playersList = server.getPlayerManager().getPlayerList();
        ServerPlayerEntity[] players = new ServerPlayerEntity[playersList.size()];
        playerEntitiesArray = players;
        for (int x = 0; x < playersList.size(); x++) {
            players[x] = playersList.get(x);
            if (HealthMap.get(players[x].getUuidAsString()) != null) {
                if (HealthMap.get(players[x].getUuidAsString()) > 0) {
                    HealthMap.put(players[x].getUuidAsString(), HealthMap.get(players[x].getUuidAsString()) - 0.2);
                    players[x].setHealth(players[x].getHealth() - HEALTH_DECAY_CONSTANT / 5);
                }

                if (players[x].getHealth() <= 0) {
                    HealthMap.put(players[x].getUuidAsString(), 0.0);
                    KillPlayer(players[x]);
                }
                
                PacketByteBuf byteBuf = PacketByteBufs.create();
                byteBuf.writeDouble(HealthMap.get(players[x].getUuidAsString()));
                ServerPlayNetworking.send(players[x].networkHandler.player, GetHealthValues, byteBuf);
            } else {
       HealthMap.put(players[x].getUuidAsString(), 0.0);

            }


        }


    }
}
