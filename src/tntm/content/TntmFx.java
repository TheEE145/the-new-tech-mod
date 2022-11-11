package tntm.content;

import arc.graphics.Color;
import arc.math.Mathf;
import mindustry.entities.Effect;
import mindustry.entities.effect.*;
import tntm.graphics.TntmDraw;
import tntm.graphics.TntmPal;

import static arc.graphics.g2d.Draw.*;

public class TntmFx {
    public static MultiEffect craftMeteria, laserEndEffect, longLaserEndEffect, bread;
    public static ExplosionEffect meteriaExplode;
    public static ParticleEffect laserStartEffect;
    public static WaveEffect virusMEffect;
    public static SparkEffect bulletCollision;

    public static class SparkEffect extends Effect {
        public SparkEffect(float lifetime, int count, float length, float max, boolean f, float sides) {
            super(lifetime, e -> {
                e.scaled(25f, s -> {
                    color(Color.white, e.color, f ? s.finpow() : s.fin());
                    TntmDraw.randomVectors(e.x, e.y, e.id, count, length, (x, y, i) -> {
                        TntmDraw.sideShard(x, y, 5f * s.fout() + Mathf.randomSeed(e.id + i, max), 4f * s.fout(), sides, Mathf.angle(x - e.x, y - e.y));
                    });
                });
            });
        }
    }

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
    }
}