package the.mod.content;

import mindustry.entities.effect.*;
import the.mod.utils.ThePal;

public class Effects {
    public static ExplosionEffect meteriaExplode;
    public static ParticleEffect craftMeteria;
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

        craftMeteria = new ParticleEffect() {{
            colorFrom = colorTo = lightColor = ThePal.meteria;
            strokeFrom = 1;
            strokeTo = 0;
            sizeFrom = 8;
            sizeTo = 16;

            lenFrom = 0.5f;
            lenTo = 0.5f;

            particles = 1;
            lifetime = 10;
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