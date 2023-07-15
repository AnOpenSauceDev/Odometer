package com.modrinth.anopensaucedev.odometer;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "odometer")
public class OdometerConfig implements ConfigData {

    @Comment("the rate at which health decays (default = 1)")
    public float decayRate = 1;

    @Comment("disable the title screen warning? (default = no)")
    public boolean disablewarning = false;
    @Comment("render custom GUI? (default = yes)")
    public boolean customgui = true;

}
