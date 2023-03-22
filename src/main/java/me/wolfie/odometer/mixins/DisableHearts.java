package me.wolfie.odometer.mixins;

import me.wolfie.odometer.Odometer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
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
    private void renderStatusBars(MatrixStack matrices, CallbackInfo ci) {
        if(Odometer.config.customgui){
            ci.cancel();
        }

    }

    @Shadow
    protected abstract int getHeartCount(LivingEntity livingEntity);

    @Shadow
    protected abstract LivingEntity getRiddenEntity();

    @Shadow
    protected abstract void renderHealthBar(MatrixStack matrices, PlayerEntity playerEntity, int m, int o, int r, int v, float f, int i, int j, int p, boolean bl);

    @Shadow
    protected abstract int getHeartRows(int x);



    @Shadow
    protected abstract PlayerEntity getCameraPlayer();


}
