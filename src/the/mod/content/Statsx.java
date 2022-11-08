package the.mod.content;

import mindustry.world.meta.*;

public class Statsx {
    public static Stat requirements, meteriaConsume, meteriaProduce, meteriaCapacity, meteriaBoost;
    public static StatCat meteria;

    public static void load() {
        requirements = new Stat("requirements", StatCat.crafting);

        meteria = new StatCat("meteria");
        meteriaConsume = new Stat("meteria-consume", meteria);
        meteriaProduce = new Stat("meteria-produce", meteria);
        meteriaCapacity = new Stat("meteria-capacity", meteria);
        meteriaBoost = new Stat("meteria-boost", meteria);
    }
}