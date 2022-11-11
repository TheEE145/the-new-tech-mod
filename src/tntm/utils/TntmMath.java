package tntm.utils;

import mindustry.world.Tile;

import static mindustry.Vars.world;

public class TntmMath {
    public static final float theta = (float) (Math.PI * 2);

    public static float thx(float angle, float rad) {
        return (float) (Math.cos((angle / 360) * theta) * rad);
    }

    public static float thy(float angle, float rad) {
        return (float) (Math.sin((angle / 360) * theta) * rad);
    }

    public static float len(float x1, float x2, float y1, float y2) {
        return (float) Math.sqrt(pow(x2 - x1) + pow(y2 - y1));
    }

    public static Tile getTileByDraw(float x, float y) {
        return world.tile((int) Math.floor(x / 8), (int) Math.floor(y / 8));
    }

    public static boolean collision(float x1, float y1, float x2, float y2, float radius) {
        return TntmMath.len(x1, x2, y1, y2) < radius;
    }

    public static float pow(float n) {
        return n*n;
    }
}