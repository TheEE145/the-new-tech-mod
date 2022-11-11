package tntm.world.blocks.lasers.meta;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import mindustry.world.Tile;
import tntm.TheTech;
import tntm.utils.TntmMath;

public class LaserModule {
    public float sx, sy, ex, ey, th, a;
    public Color color;

    public LaserModule(float sx, float sy, float ex, float ey) {
        this.sx = sx;
        this.sy = sy;
        this.ex = ex;
        this.ey = ey;
    }

    public void draw() {
        Draw.color(color);
        Draw.alpha(a);
        Lines.stroke(th);
        Lines.line(sx, sy, ex, ey);
    }

    public float len() {
        return (float) Math.sqrt(TntmMath.pow(ex - sx) + TntmMath.pow(ey - sy));
    }

    public Tile tileOn() {
        return TntmMath.getTileByDraw(ex, ey);
    }

    public LaserModule bounce(float nx, float ny) {
        LaserModule laserModule = new LaserModule(ex, ey, nx, ny);

        laserModule.a = a;
        laserModule.color = color;
        return laserModule;
    }

    @Override
    public String toString() {
        return "(" + sx + ", " + sy + ", " + ex + ", " + ey + ")";
    }
}