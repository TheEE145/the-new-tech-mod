package the.mod.content;

import mindustry.entities.Effect;
import mindustry.entities.effect.*;
import the.mod.utils.ThePal;

public class Effects {
    public static ExplosionEffect meteriaExplode;
    public static MultiEffect craftMeteria;
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