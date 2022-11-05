package the.mod.content;

import arc.struct.Seq;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.gen.EntityMapping;
import mindustry.type.Weapon;
import the.mod.TheTech;
import the.mod.utils.Types;

@SuppressWarnings("unchecked")
public class Unitsx {
    public static final Seq<Types.ModUnitType> all = new Seq<>();

    public static <T extends Types.ModUnitType> T add(T type) {
        all.add(type);
        TheTech.all.add(type);
        return type;
    }

    public static Types.ModUnitType hellx;

    public static void load() {
        hellx = add(new Types.ModUnitType("hellx", true) {{
            constructor = EntityMapping.map(3);

            health = 1000;
            flying = true;

            rotorScale = 2f;
            buildSpeed = 5;

            itemCapacity = 40;
            mineFloor = true;
            mineSpeed = 3f;
            mineTier = 1;

            rotors = new Rotor[] {
                    new Rotor() {{
                        y = -1;
                    }}
            };

            speed = 2;
            weapons.add(
                    new Types.ModWeapon("rocket1x") {{
                        x = 12;
                        y = 4;

                        mirror = true;
                        reload = 15f;

                        bullet = new MissileBulletType() {{
                            damage = speed = 7;
                        }};
                    }}
            );
        }});
    }
}