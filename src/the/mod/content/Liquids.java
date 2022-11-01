package the.mod.content;

import arc.graphics.Color;
import arc.struct.Seq;
import the.mod.TheTech;
import the.mod.utils.Types.*;

public class Liquids {
    public static ModLiquid emethen;

    public static final Seq<ModLiquid> all = new Seq<>();

    public static ModLiquid add(ModLiquid e) {
        all.add(e);
        TheTech.all.add(e);
        return e;
    }

    public static void load() {
        emethen = add(new ModLiquid("emethen", Color.orange));
    }
}