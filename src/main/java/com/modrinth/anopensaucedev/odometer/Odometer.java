package com.modrinth.anopensaucedev.odometer;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import com.modrinth.anopensaucedev.odometer.listener.ServerListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class Odometer implements ModInitializer {

    public static Identifier GetHealthValues = new Identifier("odometer","health_values");
    public static final Logger LOGGER = LoggerFactory.getLogger("odometer");
    public static OdometerConfig config;
    String VERSION = "1.7.1";
    public static HashMap<String,Double> HealthMap = new HashMap<>(); // String = UUID, Double = final health (what we go down to)

    @Override
    public void onInitialize() {
        LOGGER.info("Odometer Version: " + VERSION + " Got loaded..."); // yes im aware of the naming differences, i decided against this name shortly before publishing, and im too lazy to rename all of this
        ServerTickEvents.END_SERVER_TICK.register(new ServerListener());
        AutoConfig.register(OdometerConfig.class,GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(OdometerConfig.class).getConfig();
    }
}
