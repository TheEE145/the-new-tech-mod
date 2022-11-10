package the.mod.content;

import the.mod.TheTech;
import the.mod.utils.Types.ModItem;
import arc.graphics.Color;
import arc.struct.Seq;

public class Itemsx {
    public static final Seq<ModItem> all = new Seq<>();

    public static ModItem
            silica, silicaSand,
            virusM, virusMSand,
            coalSand, magmaAlloy;

    public static <T extends ModItem> T add(T type) {
        all.add(type);
        TheTech.all.add(type);
        return type;
    }

    public static void load() {
        silica = add(new ModItem("silica-crystal", Color.lightGray) {{
            alwaysUnlocked = true;
        }});

        silicaSand = add(new ModItem("silica-crystal-sand", Color.lightGray  ));
        virusM     = add(new ModItem("m-virus",             Color.cyan       ));
        virusMSand = add(new ModItem("m-virus-sand",        Color.cyan       ));
        coalSand   = add(new ModItem("coal-sand",           Color.black      ));
        magmaAlloy = add(new ModItem("magma-alloy",         Color.orange     ));
    }
}