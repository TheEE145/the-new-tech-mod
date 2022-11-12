package tntm.graphics;

import tntm.utils.ticker.BasicTicker;

public class TntmDrawData {
    public static BasicTicker alphaTicker = new BasicTicker() {{
        min = 0;
        max = 1;
        cur = 0.05f;
    }};

    public static BasicTicker beamTicker = new BasicTicker() {{
        min = 0;
        max = 2;
        cur = 0.05f;
    }};

    public static void renderer() {
        beamTicker.tick();
        alphaTicker.tick();
    }
}