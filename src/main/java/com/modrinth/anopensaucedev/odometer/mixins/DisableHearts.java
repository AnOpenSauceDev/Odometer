package com.modrinth.anopensaucedev.odometer.mixins;

import com.modrinth.anopensaucedev.odometer.Odometer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class) // disabled hearts AND hunger
public abstract class DisableHearts {
    @Shadow
    private long heartJumpEndTick;
    @Shadow
    private int ticks;
    @Shadow
    private int lastHealthValue;
    @Shadow
    private long lastHealthCheckTime;
    @Shadow
    private int renderHealthValue;

    private final net.minecraft.util.math.random.Random random = Random.create();
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;
    @Shadow
    private MinecraftClient client;


    /**
     * @author ME
     * @reason disable health the ~~quick way~~ nevermind it took forever
     */


    @Inject(method = "renderStatusBars",at = @At("HEAD"),cancellable = true)
    private void renderStatusBars(DrawContext context, CallbackInfo ci) {
        if(Odometer.config.customgui){
            ci.cancel();
        }

    }

    @Shadow
    protected abstract int getHeartCount(LivingEntity livingEntity);

    @Shadow
    protected abstract LivingEntity getRiddenEntity();


    @Shadow
    protected abstract int getHeartRows(int x);



    @Shadow
    protected abstract PlayerEntity getCameraPlayer();


}
