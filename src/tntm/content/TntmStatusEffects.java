package tntm.content;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.gen.Groups;
import tntm.graphics.TntmPal;
import tntm.utils.*;
import tntm.world.statuses.*;

public class TntmStatusEffects {
    public static final Seq<TntmStatusEffect> all = new Seq<>();

    public static BacteriaStatus virus1stage, virus2stage, virus3stage;
    public static TntmStatusEffect sonicPulse, neurotoxin;
    public static FlashStatus stunned;

    public static void load() {
        sonicPulse = new TntmStatusEffect("sonic-pulse") {{
            buildSpeedMultiplier = speedMultiplier = 3.0f;
        }};

        neurotoxin = new TntmStatusEffect("neurotoxin") {{
            damageMultiplier = speedMultiplier = buildSpeedMultiplier = dragMultiplier = healthMultiplier = reloadMultiplier = 0.25f;
            damage(275);

            onTimeEnd = u -> {
                Groups.unit.each(u2 -> {
                    if(TntmMath.len(u.x, u2.x, u.y, u2.y) < 80 && u != u2) {
                        u2.apply(neurotoxin);
                        u2.apply(neurotoxin, TntmTimer.second8);
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
                unit.apply(virus3stage, TntmTimer.second8);
            };
        }};

        virus1stage = new BacteriaStatus("virus-1stage") {{
            aColor = TntmPal.virusM;
            alpha = 0.2f;
            damage(1);

            damageMultiplier = speedMultiplier = buildSpeedMultiplier = 0.8f;
            onTimeEnd = (unit) -> {
                unit.apply(virus2stage, TntmTimer.second8);
            };
        }};
    }
}