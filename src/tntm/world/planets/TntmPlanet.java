package tntm.world.planets;

import mindustry.type.Planet;
import tntm.TheTech;

public class TntmPlanet extends Planet {
    public TntmPlanet(String name, Planet parent, float radius) {
        super(name, parent, radius);

        localizedName = TheTech.prefix(localizedName);
    }

    public TntmPlanet(String name, Planet parent, float radius, int sectorSize) {
        super(name, parent, radius, sectorSize);

        localizedName = TheTech.prefix(localizedName);
    }
}