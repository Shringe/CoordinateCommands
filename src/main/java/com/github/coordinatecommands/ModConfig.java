package com.github.coordinatecommands;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = CoordinateCommands.MOD_ID)
public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.TransitiveObject
    public Main main = new Main();

    public class Main {
        public boolean simpleMode = true;
        public int decimalPrecision = 2;
    }
}
