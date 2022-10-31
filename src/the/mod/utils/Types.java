package the.mod.utils;

import mindustry.type.UnitType;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import the.mod.TheTech;

public class Types {
    public static class ModItemTurret extends ItemTurret {
        public ModItemTurret(String name) {
            super(name);

            localizedName = TheTech.prefix(localizedName);
        }

        public class ModItemTurretBuild extends ItemTurretBuild {
        }
    }

    public static class ModUnit extends UnitType {
        public ModUnit(String name) {
            super(name);

            localizedName = TheTech.prefix(localizedName);
        }
    }
}