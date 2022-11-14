package tntm.utils;

import arc.util.Timer;
import tntm.brutality.Brutality;
import tntm.content.TntmUnits;
import tntm.graphics.TntmDrawData;
import tntm.world.units.TntmUnitType;

public class TntmTimer {
    public static final float
            second = 60f,
            second8 = second * 8;

    public static float convert(float seconds) {
        return seconds * 60;
    }

    public static void load() {
        Timer.schedule(() -> {
            TntmDrawData.renderer();
            TntmUnits.all.each(TntmUnitType::tact);
        }, 1, 0.02f);
    }
}