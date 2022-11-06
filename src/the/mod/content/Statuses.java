package the.mod.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.struct.Seq;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import the.mod.TheTech;
import the.mod.utils.*;

import static mindustry.Vars.*;

public class Statuses {
    public static final Seq<Types.ModStatusEffect> all = new Seq<>();

    public static BacteriaStatus virus1stage, virus2stage, virus3stage;
    public static Types.ModStatusEffect sonicPulse, neurotoxin;
    public static FlashStatus stunned;

    public static void load() {
        sonicPulse = new Types.ModStatusEffect("sonic-pulse") {{
            buildSpeedMultiplier = speedMultiplier = 3.0f;
        }};

        neurotoxin = new Types.ModStatusEffect("neurotoxin") {{
            damageMultiplier = speedMultiplier = buildSpeedMultiplier = dragMultiplier = healthMultiplier = reloadMultiplier = 0.25f;
            damage(275);

            onTimeEnd = u -> {
                Groups.unit.each(u2 -> {
                    if(TheTech.len(u.x, u2.x, u.y, u2.y) < 80 && u != u2) {
                        u2.apply(neurotoxin);
                        u2.apply(neurotoxin, Timer.second8);
                    }
                });
            };
        }};

        stunned = new FlashStatus("stunned") {{
            damageMultiplier = speedMultiplier = buildSpeedMultiplier = dragMultiplier = healthMultiplier = reloadMultiplier = 0;
            flashColor = Color.white;
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
                unit.apply(virus3stage, Timer.second8);
            };
        }};

        virus1stage = new BacteriaStatus("virus-1stage") {{
            aColor = ThePal.virusM;
            alpha = 0.2f;
            damage(1);

            damageMultiplier = speedMultiplier = buildSpeedMultiplier = 0.8f;
            onTimeEnd = (unit) -> {
                unit.apply(virus2stage, Timer.second8);
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

    public static class FlashStatus extends Types.ModStatusEffect {
        public Color flashColor = Color.white;

        public FlashStatus(String name) {
            super(name);
        }

        @Override
        public void draw(Unit unit) {
            super.draw(unit);

            if(player.unit() == unit) {
                Draw.draw(Layer.max, () -> {
                    Draw.color(flashColor);
                    Fill.rect(0, 0, Drawx.SCREEN_BOUNDS, Drawx.SCREEN_BOUNDS);
                });
            }
        }
    }
}