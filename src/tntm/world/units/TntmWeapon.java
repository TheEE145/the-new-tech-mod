package tntm.world.units;

import mindustry.type.Weapon;
import tntm.TheTech;

public class TntmWeapon extends Weapon {
    public TntmWeapon(String name) {
        super(TheTech.modId + "-" + name);
    }
}