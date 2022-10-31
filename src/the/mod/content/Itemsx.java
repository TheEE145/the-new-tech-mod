package the.mod.content;

import arc.graphics.Color;
import the.mod.utils.Types.ModItem;

public class Itemsx {
    public static ModItem silica;

    public static void load() {
        silica = new ModItem("silica-crystal", Color.lightGray);
    }
}