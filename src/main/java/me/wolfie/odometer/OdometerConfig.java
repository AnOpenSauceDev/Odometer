package me.wolfie.odometer;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "odometer")
public class OdometerConfig implements ConfigData {

    @Comment("the rate at which health decays")
    public float decayRate = 1;

    @Comment("disable the title screen warning?")
    public boolean disablewarning = false;
    @Comment("render earthbound GUI?")
    public boolean customgui = true;

}
