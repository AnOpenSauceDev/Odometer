package dev.anopensauce.odometer.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.anopensauce.odometer.HudRenderCallbackListener.burnscalar;

@Mixin(InGameHud.class)
public class InGameHudMixin {


    @Shadow @Final private MinecraftClient client;

    @Inject(method = "renderHealthBar",at = @At("HEAD"),cancellable = true)
    public void odometer$override(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci){
        ci.cancel();
    }

    @Inject(method = "renderChat",at = @At("TAIL"))
    public void odometer$ordering(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci){
        // weird ordering issue that causes flashing
        if(burnscalar > MinecraftClient.getInstance().player.getHealth() - 1) {
            //ARGB?
            context.fillGradient(0, 0, MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight(), 0x2F2b2b00, 0x2F2b2b00);

            var renderer = (GameRendererAccessor) MinecraftClient.getInstance().gameRenderer;

            var lvl = 0;

            if(client.player.getHealth() < 10){
                lvl = 11;
            }else if (client.player.getHealth() == 1){
                lvl = 8;
            }

            renderer.getBlurPostProcessor().setUniforms("Radius", lvl);
            renderer.getBlurPostProcessor().render(tickCounter.getTickDelta(true));
            client.getFramebuffer().beginWrite(false);

        }
    }


    @Redirect(method = "renderFood",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
    public void odometer$foodbar(DrawContext instance, Identifier texture, int x, int y, int width, int height){
        instance.drawGuiTexture(texture,x - 45,y,width,height);
    }
}
