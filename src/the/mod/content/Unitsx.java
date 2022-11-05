package the.mod.content;

import arc.struct.Seq;
import mindustry.gen.EntityMapping;
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
        }});
    }
}