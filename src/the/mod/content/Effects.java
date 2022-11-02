package the.mod.content;

import arc.graphics.Color;
import mindustry.entities.Effect;
import mindustry.entities.effect.*;
import the.mod.utils.ThePal;

public class Effects {
    public static MultiEffect craftMeteria, laserEndEffect, longLaserEndEffect;
    public static ExplosionEffect meteriaExplode;
    public static ParticleEffect laserStartEffect;
    public static WaveEffect virusMEffect;

    public static void load() {
        meteriaExplode = new ExplosionEffect() {{
           smokeColor = waveColor = sparkColor = ThePal.meteria;
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

                        colorFrom = colorTo = ThePal.meteria;
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
                        colorFrom = colorTo = ThePal.meteria;
                    }}
            };
        }};

        virusMEffect = new WaveEffect() {{
           strokeFrom = 1;
           strokeTo = 1;

           sizeFrom = 0;
           sizeTo = 3;

           colorFrom = colorTo = ThePal.virusM;
           lifetime = 20;
        }};
    }
}