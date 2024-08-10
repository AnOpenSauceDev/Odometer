package dev.anopensauce.odometer.client;

import dev.anopensauce.odometer.HudRenderCallbackListener;
import dev.anopensauce.odometer.Odometer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class OdometerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        ClientPlayNetworking.registerGlobalReceiver(Odometer.statusUpdate.ID, (payload,context) ->{

            context.client().execute(() ->{
                HudRenderCallbackListener.burnscalar = payload.value();
            });

        });

    }
}
