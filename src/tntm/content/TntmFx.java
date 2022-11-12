package tntm.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import mindustry.entities.Effect;
import mindustry.entities.effect.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import tntm.graphics.TntmPal;
import tntm.world.effects.SparkEffect;

import static arc.graphics.g2d.Draw.alpha;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;
import static arc.math.Mathf.rand;

public class TntmFx {
    public static MultiEffect craftMeteria, laserEndEffect, longLaserEndEffect, bread;
    public static ExplosionEffect meteriaExplode;
    public static ParticleEffect laserStartEffect;
    public static WaveEffect virusMEffect;
    public static SparkEffect bulletCollision;

    public static Effect explosion;

    public static void load() {
        meteriaExplode = new ExplosionEffect() {{
           smokeColor = waveColor = sparkColor = TntmPal.meteria;
           lifetime = 120;
           smokes = 20;
           smokeSize = 10;
           waveRad = 30;
           waveStroke = 5;
        }};

        laserStartEffect = new ParticleEffect() {{
            lenFrom = 1;
            lenTo = 1;

            strokeFrom = 1;
            strokeTo = 1;

            sizeFrom = 1f;
            sizeTo = 1.2f;

            particles = 2;
            baseLength = 2;
            length = 4;

            lifetime = 10;
            colorFrom = colorTo = Color.lightGray;
        }};

        MultiEffect tmp1 = new MultiEffect() {{
            effects = new Effect[]{
                    new ParticleEffect() {{
                        lenFrom = 1;
                        lenTo = 1;

                        strokeFrom = 1;
                        strokeTo = 1;

                        sizeFrom = 0.5f;
                        sizeTo = 0.7f;

                        particles = 3;
                        baseLength = 2;
                        length = 4;

                        lifetime = 20;
                        colorFrom = colorTo = Color.orange;
                    }},

                    new ParticleEffect() {{
                        lenFrom = 1;
                        lenTo = 1;

                        strokeFrom = 1;
                        strokeTo = 1;

                        sizeFrom = 1f;
                        sizeTo = 2f;

                        particles = 3;
                        baseLength = 2;
                        length = 4;

                        lifetime = 20;
                        colorFrom = colorTo = Color.lightGray;
                    }}
            };
        }};

        longLaserEndEffect = new MultiEffect() {{
            effects = new Effect[] {
                    new WaveEffect() {{
                        strokeFrom = 1;
                        strokeTo = 1;

                        sizeFrom = 0;
                        sizeTo = 4;

                        colorFrom = colorTo = Color.purple;
                        lifetime = 25;
                    }},

                    tmp1
            };
        }};

        laserEndEffect = new MultiEffect() {{
            effects = new Effect[] {
                    new WaveEffect() {{
                        strokeFrom = 1;
                        strokeTo = 1;

                        sizeFrom = 0;
                        sizeTo = 4;

                        colorFrom = colorTo = Color.red;
                        lifetime = 15;
                    }},

                    tmp1
            };
        }};

        craftMeteria = new MultiEffect() {{
            effects = new Effect[] {
                    new WaveEffect() {{
                        strokeFrom = 1;
                        strokeTo = 1;

                        sizeFrom = 0;
                        sizeTo = 4;

                        colorFrom = colorTo = TntmPal.meteria;
                        lifetime = 30;
                    }},

                    new ParticleEffect() {{
                        lenFrom = 1;
                        lenTo = 1;

                        strokeFrom = 1;
                        strokeTo = 1;

                        sizeFrom = 1;
                        sizeTo = 3;

                        particles = 3;
                        baseLength = 2;
                        length = 4;

                        lifetime = 10;
                        colorFrom = colorTo = TntmPal.meteria;
                    }}
            };
        }};

        virusMEffect = new WaveEffect() {{
           strokeFrom = 1;
           strokeTo = 1;

           sizeFrom = 0;
           sizeTo = 3;

           colorFrom = colorTo = TntmPal.virusM;
           lifetime = 20;
        }};

        bread = new MultiEffect() {{
            effects = new Effect[] {
                    new SparkEffect(100f, 7, 18f, 7f, false, 3),
                    new WaveEffect() {{
                        strokeFrom = 1;
                        strokeTo = 1;

                        sizeFrom = 0;
                        sizeTo = 3;

                        lifetime = 20;
                    }}
            };
        }};

        bulletCollision = new SparkEffect(75f, 3, 10f, 7f, false, 5);

        explosion = new Effect(30, 80f, b -> {
            color(Color.gray);
            //TODO awful borders with linear filtering here
            alpha(0.9f);
            float intensity = 1;
            for(int i = 0; i < 4; i++){
                rand.setSeed(b.id * 2L + i);
                float lenScl = rand.random(0.4f, 1f);
                int fi = i;
                b.scaled(160, e -> {
                    randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int) (3f * intensity), 14f * intensity, (x, y, in, out) -> {
                        float fout = e.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
                        Fill.circle(e.x + x, e.y + y, fout * ((2f + intensity) * 1.8f));
                    });
                });
            }

            b.scaled(80, e -> {
                e.scaled(5 + intensity * 2.5f, i -> {
                    stroke((3.1f + intensity/5f) * i.fout());
                    Lines.circle(e.x, e.y, (3f + i.fin() * 14f) * intensity);
                    Drawf.light(e.x, e.y, i.fin() * 14f * 2f * intensity, Color.white, 0.9f * e.fout());
                });

                color(Pal.lighterOrange, Pal.lightOrange, Color.gray, e.fin());
                stroke((1.7f * e.fout()) * (1f + (intensity - 1f) / 2f));

                Draw.z(Layer.effect + 0.001f);
                randLenVectors(e.id + 1, e.finpow() + 0.001f, (int)(9 * intensity), 40f * intensity, (x, y, in, out) -> {
                    lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + out * 4 * (3f + intensity));
                    Drawf.light(e.x + x, e.y + y, (out * 4 * (3f + intensity)) * 3.5f, Draw.getColor(), 0.8f);
                });
            });
        });
    }
}