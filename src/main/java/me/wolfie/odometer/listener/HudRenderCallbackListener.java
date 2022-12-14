package me.wolfie.odometer.listener;

import com.mojang.blaze3d.systems.RenderSystem;
import me.wolfie.odometer.Odometer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static me.wolfie.odometer.Odometer.HealthMap;

@Environment(EnvType.CLIENT)
public class HudRenderCallbackListener implements HudRenderCallback {

    //texture for the health container
    Identifier contid = new Identifier("odometer","textures/gui/container.png");
    Identifier mortallywounded = new Identifier("odometer", "textures/gui/container_fatal.png");

    @Override
    public void onHudRender(MatrixStack matrixStack, float tickDelta) {


        if (HealthMap.get(MinecraftClient.getInstance().getSession().getUuid()) != null) {
            int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
            int height = MinecraftClient.getInstance().getWindow().getScaledHeight();


            MinecraftClient client = MinecraftClient.getInstance();

            if(MinecraftClient.getInstance().player.getHealth() - HealthMap.get(MinecraftClient.getInstance().getSession().getUuid()) > 0) {
                RenderTexture(matrixStack, tickDelta, contid, width, height, width - 275, height - 86, 82, 64); // box
            }else {
                RenderTexture(matrixStack, tickDelta, mortallywounded, width, height, width - 275, height - 86, 82, 64); // box
            }

            // various status indicators
            RenderTexture(matrixStack,tickDelta,new Identifier("textures/particle/heart.png"),width,height,width - 260, height - 68, 8, 8); // health
            RenderTexture(matrixStack,tickDelta,new Identifier("textures/particle/bubble.png"),width,height,width - 260, height - 48, 8, 8); // air
            RenderTexture(matrixStack,tickDelta,new Identifier("textures/item/bread.png"),width,height,width - 260, height - 58, 8, 8); // food
            RenderTexture(matrixStack,tickDelta,new Identifier("textures/item/experience_bottle.png"),width,height,width - 260, height - 38, 8, 8); // level

            // WARNING: ORDER MATTERS... A LOT!
            // bad order will create many overlaps, so be careful

            //text block
            client.textRenderer.draw(matrixStack, Text.of(MinecraftClient.getInstance().getSession().getUsername()), Math.toIntExact((long) (width - 259)), height - 77, 0xFFFFFF);
            client.textRenderer.draw(matrixStack, Text.of((Math.round(MinecraftClient.getInstance().player.getHealth())) + " (" + (Math.round(MinecraftClient.getInstance().player.getHealth() + client.player.getAbsorptionAmount() - HealthMap.get(client.getSession().getUuid()))) + ")"), Math.toIntExact((long) (width - 250)), height - 68, 0xFFFFFF);
            client.textRenderer.draw(matrixStack, Text.of(String.valueOf(Math.round(MinecraftClient.getInstance().player.getHungerManager().getFoodLevel()))), Math.toIntExact((long) (width - 250)), height - 58, 0xFFFFFF);
            client.textRenderer.draw(matrixStack, Text.of(String.valueOf(Math.round(MinecraftClient.getInstance().player.getAir() / 10))), Math.toIntExact((long) (width - 250 )), height - 48, 0xFFFFFF);
            client.textRenderer.draw(matrixStack, Text.of(String.valueOf(client.player.experienceLevel + " (" + MinecraftClient.getInstance().player.totalExperience + ")")), Math.toIntExact((long) (width - 250 )), height - 38, 0xFFFFFF);
        }
    }

    public  void RenderTexture(MatrixStack matrixStack,float tickDelta,Identifier id, int width, int height, int x, int y, int scaleX, int scaleY){
        matrixStack.push();
        RenderSystem.setShaderTexture(0, id);
        MinecraftClient.getInstance().getTextureManager().bindTexture(id);
        //doing texturewidth to a multiple what it actually is like [16 -> 32] and bumping up the width lets you stretch things!
        InGameHud.drawTexture(matrixStack, x, y, 0.0F, 0.0F, scaleX, scaleY, scaleX, scaleY);
        matrixStack.pop();
    }

}
