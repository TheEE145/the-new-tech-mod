package tntm.graphics.drawer;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import mindustry.graphics.Pal;

public class HeatDrawer extends Drawer {
    public float heat = 0;
    public Color color = Pal.turretHeat;

    public HeatDrawer(String prefix) {
        super(prefix);
    }

    @Override
    public void draw(float x, float y) {
        if(heat > 0) {
            Draw.blend(Blending.additive);
            Draw.color(color, heat);
            Draw.rect(region, x, y);
            Draw.blend();
            Draw.color();
        }
    }
}