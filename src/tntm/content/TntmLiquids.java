package tntm.content;

import arc.graphics.Color;
import arc.struct.Seq;
import tntm.TheTech;
import tntm.world.liquids.TntmLiquid;

public class TntmLiquids {
    public static TntmLiquid emethen;

    public static final Seq<TntmLiquid> all = new Seq<>();

    public static TntmLiquid add(TntmLiquid e) {
        all.add(e);
        TheTech.all.add(e);
        return e;
    }

    public static void load() {
        emethen = add(new TntmLiquid("emethen", Color.orange));
    }
}