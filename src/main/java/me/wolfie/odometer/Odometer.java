package me.wolfie.odometer;

import me.wolfie.odometer.listener.HudRenderCallbackListener;
import me.wolfie.odometer.listener.ServerListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.Side;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class Odometer implements ModInitializer {

    public static Identifier GetHealthValues = new Identifier("odometer","health_values");
    public static final Logger LOGGER = LoggerFactory.getLogger("odometer");

    public static float DecaySpeed; // the rate at which we lose health
    String VERSION = "1.6";
    public static HashMap<String,Double> HealthMap = new HashMap<>(); // String = UUID, Double = final health (what we go down to)

    @Override
    public void onInitialize() {

        /*
        ConfigBuilder builder = ConfigBuilder.create().setTitle(Text.of("yes"));
        builder.setSavingRunnable(()->{

        });



        AtomicReference<Float> savedValue = new AtomicReference<>(0.0F);
        DecaySpeed = savedValue.get();
        ConfigCategory options = builder.getOrCreateCategory(Text.of("Options"));
        options.addEntry(builder.entryBuilder().startFloatField(Text.of("decay speed"), savedValue.get()).setDefaultValue(1f).setTooltip(Text.literal("Sets the rate at which health rolls down")).setSaveConsumer(newValue -> savedValue.set(newValue)));
        

         */
        LOGGER.info("Odometer Version: " + VERSION + " Got loaded..."); // yes im aware of the naming differences, i decided against this name shortly before publishing, and im too lazy to rename all of this
        ServerTickEvents.END_SERVER_TICK.register(new ServerListener());



    }
}
