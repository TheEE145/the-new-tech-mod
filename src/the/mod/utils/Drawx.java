package the.mod.utils;

import arc.func.Cons3;
import arc.func.Floatc2;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.util.Align;
import arc.util.pooling.Pools;
import mindustry.graphics.*;
import mindustry.ui.Fonts;
import the.mod.types.Other;

import java.util.concurrent.atomic.AtomicBoolean;

import static mindustry.Vars.*;

public class Drawx {
    public static final GlyphLayout layout = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
    public static final float SCREEN_BOUNDS = 4592.235f;
    public static TextureRegion circle, sun, pix, arrowx, air;

    public static class Math {
        private static boolean beamBigger = true;
        public static float beamStroke = 0f;

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

    public enum FontType {
        UNDER_LINE,
        UPPER_LINE,
        BOTH_LINE,
        NO_LINE
    }

    public static final Font font = Fonts.outline;
    public static void text(float x, float y, Color color, CharSequence text, FontType type) {
        boolean i = font.usesIntegerPositions();
        font.setUseIntegerPositions(false);
        font.getData().setScale(1f / 3f);
        layout.setText(font, text);

        boolean both = type == FontType.BOTH_LINE;

        font.setColor(color);
        float offset = layout.height / 2;

        if(type == FontType.UNDER_LINE || both) {
            offset = layout.height + 1;
        }

        if(type == FontType.UPPER_LINE) {
            offset = layout.height - 1;
        }

        font.draw(text, x, y + offset, Align.center);
        if(type != FontType.NO_LINE) {
            float start = x - layout.width / 2f - 2f;
            float end = x + layout.width / 2f + 1.5f;

            if(type == FontType.UNDER_LINE || both) {
                Drawx.line(start, y - 1, end, y - 1, color);
            }

            float offset2 = both ? 6 : 4;
            if(type == FontType.UPPER_LINE || both) {
                Drawx.line(start, y + offset2, end, y + offset2, color);
            }
        }

        font.setUseIntegerPositions(i);
        font.setColor(Color.white);
        font.getData().setScale(1f);
        Draw.reset();
        Pools.free(layout);
    }

    public static void line(float x, float y, float x2, float y2, Color color) {
        line(x, y, x2, y2, color, 1);
    }

    public static void line(float x, float y, float x2, float y2, Color color, float stroke) {
        Lines.stroke(stroke + 1);
        Draw.color(Pal.darkOutline);
        Lines.line(x, y, x2, y2);

        Lines.stroke(stroke);
        Draw.color(color);
        Lines.line(x, y, x2, y2);
    }
}