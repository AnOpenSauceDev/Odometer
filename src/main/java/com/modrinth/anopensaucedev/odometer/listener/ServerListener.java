package com.modrinth.anopensaucedev.odometer.listener;

import com.modrinth.anopensaucedev.odometer.Odometer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;


public class ServerListener implements ServerTickEvents.EndTick {
    public static PlayerEntity[] playerEntitiesArray;
    public static MinecraftServer minecraftServer;

    public static void KillPlayer(PlayerEntity Player) {
        ServerPlayerEntity serverPlayerEntity = minecraftServer.getPlayerManager().getPlayer(Player.getUuid());

        if (!serverPlayerEntity.isDead()) {
            serverPlayerEntity.setHealth(0);
            Odometer.HealthMap.put(serverPlayerEntity.getUuidAsString(), 0.0);
            serverPlayerEntity.onDeath(serverPlayerEntity.getRecentDamageSource());
        }

    }

    public static float HEALTH_DECAY_CONSTANT;

    @Override
    public void onEndTick(MinecraftServer server) {
        HEALTH_DECAY_CONSTANT = Odometer.config.decayRate;
        minecraftServer = server; // declare server

        List<ServerPlayerEntity> playersList = server.getPlayerManager().getPlayerList();
        ServerPlayerEntity[] players = new ServerPlayerEntity[playersList.size()];
        playerEntitiesArray = players; // get players, even though this is literally redundant
        for (int x = 0; x < playersList.size(); x++) {
            players[x] = playersList.get(x);
            if (Odometer.HealthMap.get(players[x].getUuidAsString()) != null) {
                if (Odometer.HealthMap.get(players[x].getUuidAsString()) > 0) {
                    Odometer.HealthMap.put(players[x].getUuidAsString(), Odometer.HealthMap.get(players[x].getUuidAsString()) - 0.2);
                    players[x].setHealth(players[x].getHealth() - HEALTH_DECAY_CONSTANT / 5);
                }

                if (players[x].getHealth() <= 0) {
                    Odometer.HealthMap.put(players[x].getUuidAsString(), 0.0);
                    KillPlayer(players[x]);
                }
                
                PacketByteBuf byteBuf = PacketByteBufs.create();
                byteBuf.writeDouble(Odometer.HealthMap.get(players[x].getUuidAsString()));
                ServerPlayNetworking.send(players[x].networkHandler.player, Odometer.GetHealthValues, byteBuf);
            } else {
       Odometer.HealthMap.put(players[x].getUuidAsString(), 0.0);

            }


        }


    }
}
