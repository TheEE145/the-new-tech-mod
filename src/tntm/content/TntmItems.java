package tntm.content;

import tntm.TheTech;
import arc.graphics.Color;
import arc.struct.Seq;
import tntm.world.items.TntmItem;

public class TntmItems {
    public static final Seq<TntmItem> all = new Seq<>();

    public static TntmItem
            silica, silicaSand,
            virusM, virusMSand,
            coalSand, magmaAlloy;

    public static <T extends TntmItem> T add(T type) {
        all.add(type);
        TheTech.all.add(type);
        return type;
    }

    public static void load() {
        silica = add(new TntmItem("silica-crystal", Color.lightGray) {{
            alwaysUnlocked = true;
        }});

        silicaSand = add(new TntmItem("silica-crystal-sand", Color.lightGray  ));
        virusM     = add(new TntmItem("m-virus",             Color.cyan       ));
        virusMSand = add(new TntmItem("m-virus-sand",        Color.cyan       ));
        coalSand   = add(new TntmItem("coal-sand",           Color.black      ));
        magmaAlloy = add(new TntmItem("magma-alloy",         Color.orange     ));
    }
}