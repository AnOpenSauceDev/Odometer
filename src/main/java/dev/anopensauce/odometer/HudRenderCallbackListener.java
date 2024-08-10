package dev.anopensauce.odometer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2i;

public class HudRenderCallbackListener implements HudRenderCallback {

    public static Float burnscalar = 0.0f;

    float ms = 0.0f;
    float thump = 0f;


    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

        //textures

        var windcords = new Vector2i(MinecraftClient.getInstance().getWindow().getScaledWidth(),MinecraftClient.getInstance().getWindow().getScaledHeight());

        var h = Math.ceil(MinecraftClient.getInstance().player.getHealth());



        int s = 1;
        int t = -5;

        windcords.y = 0;

        int tw = 16;
        int th = 32;

        float scale = 13;

        drawContext.drawTexture(Identifier.of("odometer","gui/digits" + (int) h % 10 +".png"), (int) ((windcords.x/2) - (1 * scale) - t),(windcords.y) + s,0,0,tw,th,tw,th);

        var a = ((int) ((h % 100) - (h % 10)) / 10);
        if(a > 0) {
            drawContext.drawTexture(Identifier.of("odometer", "gui/digits" + a +".png"), (int) ((windcords.x / 2) - (2 * scale) - t), (windcords.y) + s, 0, 0, tw, th, tw, th);
        }else if(a == 0){
            drawContext.drawTexture(Identifier.of("odometer", "gui/digits_blank.png"), (int) ((windcords.x / 2) - (2 * scale) - t), (windcords.y) + s, 0, 0, tw, th, tw, th);
        }

        var b = ((int) ((h % 1000) - (h % 100) - (h % 10)) /100);

        if(b > 0) {
            drawContext.drawTexture(Identifier.of("odometer", "gui/digits" + b +".png"), (int) ((windcords.x / 2) - (3 * scale) - t), (windcords.y) + s, 0, 0, tw, th, tw, th);
        }else if(b == 0) {
            //drawContext.drawTexture(Identifier.of("odometer", "gui/digits_blank.png"), (int) ((windcords.x / 2) - (3 * scale) - t), (windcords.y / 2) + s, 0, 0, tw, th, tw, th);
        }

        //pulse, "pain", etc

        ms += tickCounter.getTickDelta(true) * 20;

        var tmp = ((ms/100) - 9);

        if(ms >= 300){thump = (float) MathHelper.lerp(tmp,0.5f,0.80f);}

        if(ms > 550){thump = (float) MathHelper.lerp(tmp,0.8f,0.5f);}

        if(ms > 1000){ms = 0f;}


        if(burnscalar > 19.0f){ burnscalar = 19f;} // otherwise clipping occurs.

        if(burnscalar > 0) {
            var amt = 0.5f * (((burnscalar + thump) / 10) - 0.3f);
            renderDamage(drawContext, Math.min(amt,16.0f));
        }

        // any other order causes patterns that are going to be seizure-inducing
        /*
        if(burnscalar > MinecraftClient.getInstance().player.getHealth()) {
            //ARGB?
            drawContext.fillGradient(0, 0, MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight(), 0xc05e1717, 0x2b2b2b00);
        }

         */

    }

    private static final Identifier NAUSEA_OVERLAY = Identifier.ofVanilla("textures/misc/nausea.png");

    private void renderDamage(DrawContext context, float distortionStrength) {
        int i = context.getScaledWindowWidth();
        int j = context.getScaledWindowHeight();
        context.getMatrices().push();
        float f = MathHelper.lerp(distortionStrength, 2.5F, 1.1F);
        context.getMatrices().translate((float)i / 2.0F, (float)j / 2.0F, 0.0F);
        context.getMatrices().scale(f, f, f);
        context.getMatrices().translate((float)(-i) / 2.0F, (float)(-j) / 2.0F, 0.0F);
        distortionStrength += 2;
        float r = 1.0F * distortionStrength;
        float g = 0.5F * (distortionStrength * 0.25f);
        float b = 0.5F * (distortionStrength * 0.25f);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
        context.setShaderColor(r, g, b, 1.0F);
        context.drawTexture(NAUSEA_OVERLAY, 0, 0, -90, 0.0F, 0.0F, i, j, i, j);
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        context.getMatrices().pop();
    }

}
