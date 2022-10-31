package the.mod.content;

import arc.graphics.Color;
import the.mod.utils.Types.ModItem;

public class Itemsx {
    public static ModItem silica, silicaSand, virusM, virusMSand, coalSand;

    public static void load() {
        silica = new ModItem("silica-crystal", Color.lightGray) {{
            alwaysUnlocked = true;
        }};

        silicaSand = new ModItem("silica-crystal-sand", Color.lightGray);
        virusM = new ModItem("m-virus", Color.cyan);
        virusMSand = new ModItem("m-virus-sand", Color.cyan);
        coalSand = new ModItem("coal-sand", Color.black);
    }
}