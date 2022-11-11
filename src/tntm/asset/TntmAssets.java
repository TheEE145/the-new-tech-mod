package tntm.asset;

import arc.graphics.g2d.TextureRegion;
import tntm.TheTech;
import tntm.graphics.TntmDraw;

import static arc.Core.atlas;

public class TntmAssets {
    public static void load() {
        TntmDraw.circle = mod("circlex");
        TntmDraw.sun    = mod("sunx");
        TntmDraw.pix    = mod("pix");
        TntmDraw.arrowx = mod("arrowx");
        TntmDraw.air    = mod("air");
    }

    public static TextureRegion get(String id) {
        return atlas.find(id);
    }

    public static TextureRegion mod(String id) {
        return get(TheTech.modId + "-" + id);
    }
}