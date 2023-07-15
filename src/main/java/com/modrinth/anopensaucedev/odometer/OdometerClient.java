package com.modrinth.anopensaucedev.odometer;

import com.modrinth.anopensaucedev.odometer.listener.HudRenderCallbackListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class OdometerClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {



        Odometer.HealthMap.put(MinecraftClient.getInstance().getSession().getUuid(),0.0); // default value so the gui won't crash

        ClientPlayNetworking.registerGlobalReceiver(Odometer.GetHealthValues,((client, handler, buf, responseSender) -> {
            Odometer.HealthMap.put(MinecraftClient.getInstance().getSession().getUuid(),buf.getDouble(0));
        }));

        HudRenderCallback.EVENT.register(new HudRenderCallbackListener());


    }
}
