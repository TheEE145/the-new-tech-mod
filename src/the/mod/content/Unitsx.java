package the.mod.content;

import arc.struct.Seq;
import the.mod.TheTech;
import the.mod.utils.Types;

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
            health = 1000;
            flying = true;

            rotateSpeed = 35f;
            rotors = new Rotor[] {
                    new Rotor() {{
                        y = 8;
                    }}//,

                    //new Rotor() {{
                    //    y = 8;
                    //
                    //    reverse = true;
                    //}}
            };

            speed = 2;
        }});
    }
}