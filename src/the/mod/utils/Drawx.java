package the.mod.utils;

import arc.func.Cons3;
import arc.func.Floatc2;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Angles;
import mindustry.graphics.*;
import the.mod.types.Other;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import static mindustry.Vars.*;

public class Drawx {
    public static final float SCREEN_BOUNDS = 4592.235f;
    public static TextureRegion circle, sun, pix;

    public static class Math {
        private static boolean beamBigger = true;
        public static float beamStroke = 0f;

        public static boolean sun = false;
        public static Color sunColor = Color.white;

        public static void renderer() {
            if(beamBigger) {
                beamStroke += 0.05f;

                if(beamStroke >= 2f) {
                    beamStroke = 2f;
                    beamBigger = false;
                }
            } else {
                beamStroke -= 0.05f;

                if(beamStroke <= 0f) {
                    beamStroke = 0f;
                    beamBigger = true;
                }
            }

            AtomicBoolean sun = new AtomicBoolean(false);
            world.tiles.eachTile(t -> {
                if(t.build instanceof Other.SunGenerator.SunGeneratorBuild) {
                    sun.set(true);
                }
            });

            Math.sun = sun.get();
        }
    }

    public static void radius(float x, float y, float radius, float stroke, Color outlineColor, Color color, float alpha) {
        /*
                float seg = segments - space;
                float len = 360/seg;
                Log.info(len);

                float od = seg/360;
                Log.info(od);

                float angle = 0;
                for(int i = 0; i < seg; i++) {
                    Lines.stroke(stroke + 2f);
                    Draw.color(outlineColor);
                    Lines.arc(x, y, radius, od, angle);
                    Lines.stroke(stroke);
                    Draw.color(radColor);
                    Lines.arc(x, y, radius, od, angle);
                    angle += len + space;
                    Draw.flush();
                }
         */

        float od = 0.05555555555555555555555555555556f;
        float angle = 0;
        for(int i = 0; i < 20; i++) {
            Lines.stroke(stroke + 2f);
            Draw.color(outlineColor);
            Draw.alpha(alpha);
            Lines.arc(x, y, radius, od, angle);

            Lines.stroke(stroke);
            Draw.color(color);
            Draw.alpha(alpha);
            Lines.arc(x, y, radius, od, angle);

            angle += 28;
        }
    }

    public static void radius(float x, float y, float radius, float stroke, Color color, float alpha) {
        radius(x, y, radius, stroke, Pal.darkOutline, color, alpha);
    }

    public static void radius(float x, float y, float radius, Color color, float alpha) {
        radius(x, y, radius, 2, color, alpha);
    }

    public static void beam(float x, float y, float x1, float y1, float stroke, Color color, float alpha) {
        Lines.stroke(stroke);
        Draw.color(color);
        Draw.alpha(alpha);
        Lines.line(x, y, x1, y1);

        Lines.stroke(stroke/2);
        Draw.color(Color.white);
        Draw.alpha(alpha);
        Lines.line(x, y, x1, y1);

        float scale = stroke*1.5f;

        Draw.color(color);
        Draw.alpha(alpha);

        Draw.rect(circle, x, y, scale, scale);
        Draw.rect(circle, x1, y1, scale, scale);

        scale /= 2;
        Draw.color(Color.white);
        Draw.alpha(alpha);

        Draw.rect(circle, x, y, scale, scale);
        Draw.rect(circle, x1, y1, scale, scale);
    }

    public static void beam(float x, float y, float x1, float y1, Color color, float alpha) {
        beam(x, y, x1, y1, 4 + Math.beamStroke, color, alpha);
    }

    public static void beam(float x, float y, float x1, float y1, Color color) {
        beam(x, y, x1, y1, color, 1f);
    }

    public static void sideShard(float x, float y, float size, float width, float sides, float r) {
        for(int i = 0; i < 360; i += 360/sides) {
            Drawf.tri(x, y, width, size, r + i);
        }
    }

    public static void shard(float x, float y, float size, float width, float r){
        sideShard(x, y, size, width, 2, r);
    }

    public static void randomVectors(float x, float y, long seed, int amount, float length, Cons3<Float, Float, Integer> cons) {
        int[] id = {0};
        Angles.randLenVectors(seed, amount, length, (x1, y1) -> {
            cons.get(x1 + x, y1 + y, id[0]++);
        });
    }

    public static void randomVectors(float x, float y, long seed, int amount, float length, Floatc2 cons) {
        randomVectors(x, y, seed, amount, length, (x2, y2, ignored) -> {
            cons.get(x2, y2);
        });
    }
}