package tntm.world.blocks.lasers.meta;

import arc.struct.Seq;
import mindustry.world.Tile;
import tntm.TheTech;
import tntm.utils.TntmMath;

public class LaserLink {
    public Seq<LaserModule> lasers;
    public float startX, startY;

    public LaserLink(float startX, float startY) {
        this.startX = startX;
        this.startY = startY;

        lasers = new Seq<>();
    }

    public Tile start() {
        return TntmMath.getTileByDraw(startX, startY);
    }

    public LaserModule last() {
        return lasers.isEmpty() ? null : lasers.get(lasers.size - 1);
    }

    public Tile target() {
        return last() == null ? start() : last().tileOn();
    }

    public float len() {
        float len = 0;
        for(LaserModule m : lasers) {
            len += m.len();
        }

        return len;
    }

    public void draw() {
        for(LaserModule e : lasers) {
            e.draw();
        }
    }

    public LaserModule start(LaserModule m, float x, float y) {
        if(m == null) {
            return null;
        }

        m.sx = startX;
        m.sy = startY;
        m.ex = x;
        m.ey = y;

        lasers.add(m);
        return m;
    }

    public LaserModule bounce(float x, float y) {
        LaserModule laser;

        if(lasers.isEmpty()) {
            laser = new LaserModule(startX, startY, x, y);
        } else {
            laser = last().bounce(x, y);
        }

        lasers.add(laser);
        return laser;
    }

    @Override
    public String toString() {
        return lasers.toString();
    }
}