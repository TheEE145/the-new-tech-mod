package tntm.world.statuses;

import arc.func.Cons;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import tntm.TheTech;

public class TntmStatusEffect extends StatusEffect {
    public Cons<Unit> onTimeEnd;

    public void damage(float damage) {
        this.damage = damage/60;
    }

    public TntmStatusEffect(String name) {
        super(name);
        localizedName = TheTech.prefix(localizedName);
    }

    @Override
    public void update(Unit unit, float time) {
        super.update(unit, time);

        if((time <= 2f) && (onTimeEnd != null)) {
            onTimeEnd.get(unit);
        }
    }
}