package tntm.graphics.drawer;

import arc.graphics.g2d.Draw;
import tntm.utils.ticker.RotationTicker;

public class DrawRotor extends Drawer {
    public RotationTicker tacker;
    public float rotation;

    public DrawRotor(String prefix) {
        super(prefix);
    }

    @Override
    public void update() {
        tacker.tack(rotation);
    }

    @Override
    public void draw(float x, float y) {
        if(tacker == null) {
            return;
        }

        Draw.rect(region, x, y, tacker.rotation);
    }
}