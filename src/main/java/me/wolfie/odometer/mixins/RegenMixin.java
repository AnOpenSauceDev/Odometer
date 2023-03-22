package me.wolfie.odometer.mixins;

import me.wolfie.odometer.Odometer;
import net.fabricmc.fabric.mixin.screen.ScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.gui.DrawableHelper.drawCenteredText;

@Mixin(TitleScreen.class)
public abstract class RegenMixin {

    @Inject(method = "render",at = @At("TAIL"))
    public void WarnPlayer(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci){
        if (!Odometer.config.disablewarning) { return; }
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
        TitleScreen.drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, Text.of("Notice: Rolling Health is best with NaturalRegeneration OFF."),250,height - 250, 0xFFFFFF);
    }


}
