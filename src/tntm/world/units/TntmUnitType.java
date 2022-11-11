package tntm.world.units;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.ArcRuntimeException;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import tntm.TheTech;
import tntm.asset.TntmAssets;
import tntm.utils.*;
import tntm.utils.ticker.*;

public class TntmUnitType extends UnitType {
    public boolean helicopter, ground, legs, tank;
    public boolean helicopterEnginesEnabled = false;

    public TextureRegion topRegion, rotorRegion;
    public float rotorSpeed = 15;
    public Rotor[] rotors;

    public TntmUnitType(String name) {
        super(name);

        localizedName = TheTech.prefix(localizedName);
        outlineColor = Pal.darkOutline;
    }

    public void color(Color color) {
        trailColor = engineColor = color;
        trailLength = 24;
    }

    @Override
    public void setStats() {
        super.setStats();
        TheTech.setup(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load() {
        super.load();

        if(helicopter) {
            topRegion = TntmAssets.get(name + "-top");
            rotorRegion = TntmAssets.get(name + "-rotor");

            if(!helicopterEnginesEnabled) {
                engines = Seq.with();
                engineSize = 0;
            }
        }

        if(ground) {
            constructor = MechUnit::create;
        }

        if(legs) {
            constructor = LegsUnit::create;
        }

        if(tank) {
            constructor = TankUnit::create;
        }

        if(!ground && !legs && !tank) {
            constructor = EntityMapping.map(3);
        }
    }

    public float layer(Unit unit) {
        if(unit == null) {
            return Layer.flyingUnit;
        }

        return unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);
    }

    ItemStack[] requirements = ItemStack.empty;
    public void researchRequirements(ItemStack[] items) {
        requirements = items;
    }

    @Override
    public ItemStack[] researchRequirements() {
        return requirements;
    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);

        if(helicopter && rotors != null) {
            float rot = unit.rotation;
            Draw.draw(layer(unit), () -> {
                for(Rotor r : rotors) {
                    float xx = TntmMath.thx(rot, r.x);
                    float xy = TntmMath.thy(rot, r.y);

                    float yx = TntmMath.thx(rot, r.y);
                    float yy = TntmMath.thy(rot, r.y);

                    Draw.rect(
                            rotorRegion,

                            unit.x + xx + yx,
                            unit.y + xy + yy,

                            rotorRegion.width/(4f + r.small),
                            rotorRegion.height/(4f + r.small),

                            r.rotation
                    );

                    Draw.rect(topRegion, unit.x + xx + yx, unit.y + xy + yy);
                }
            });
        }
    }

    @Override
    public void createIcons(MultiPacker packer) {
        try {
            super.createIcons(packer);
        } catch(ArcRuntimeException ignored) {
        }
    }

    public void tact() {
        if(helicopter && rotors != null) {
            for(Rotor r : rotors) {
                r.tack(rotorSpeed);
            }
        }
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);
    }

    public static class Rotor extends RotationTicker {
        public Rotor() {
            super(0);
        }

        public float small = 0f;
        public float x, y = x = 0;
    }
}