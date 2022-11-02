package the.mod.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.struct.Seq;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import the.mod.utils.*;

public class Statuses {
    public static final Seq<Types.ModStatusEffect> all = new Seq<>();

    public static BacteriaStatus virus1stage, virus2stage, virus3stage;
    public static Types.ModStatusEffect sonicPulse;

    public static void load() {
        sonicPulse = new Types.ModStatusEffect("sonic-pulse") {{
            buildSpeedMultiplier = speedMultiplier = 3.0f;
        }};

        virus3stage = new BacteriaStatus("virus-3stage") {{
            aColor = Color.pink;
            alpha = 0.8f;
            damage(50);

            damageMultiplier = speedMultiplier = buildSpeedMultiplier = 0.2f;
        }};

        virus2stage = new BacteriaStatus("virus-2stage") {{
            aColor = Color.red;
            alpha = 0.5f;
            damage(10);

            damageMultiplier = speedMultiplier = buildSpeedMultiplier = 0.5f;
            onTimeEnd = (unit) -> {
                unit.apply(virus3stage, 480);
            };
        }};

        virus1stage = new BacteriaStatus("virus-1stage") {{
            aColor = ThePal.virusM;
            alpha = 0.2f;
            damage(1);

            damageMultiplier = speedMultiplier = buildSpeedMultiplier = 0.8f;
            onTimeEnd = (unit) -> {
                unit.apply(virus2stage, 480);
            };
        }};
    }

    public static class BacteriaStatus extends Types.ModStatusEffect {
        public Color aColor;
        public float alpha;

        public BacteriaStatus(String name) {
            super(name);
        }

        @Override
        public void draw(Unit unit) {
            super.draw(unit);

            Draw.draw(Layer.shields - 1, () -> {
                Draw.color(aColor);
                Draw.alpha(alpha);
                Draw.rect(unit.type.shadowRegion, unit.x, unit.y, unit.rotation - 90);
                Draw.flush();
            });
        }
    }
}