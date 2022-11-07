package the.mod.content;

import mindustry.world.meta.*;

public class Statsx {
    public static Stat requirements;

    public static void load() {
        requirements = new Stat("requirements", StatCat.crafting);
    }
}