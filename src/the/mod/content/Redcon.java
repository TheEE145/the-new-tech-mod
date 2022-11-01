package the.mod.content;

import arc.graphics.Color;
import arc.math.geom.Vec3;
import mindustry.content.*;
import mindustry.game.Team;
import mindustry.graphics.g3d.*;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Planet;
import mindustry.world.meta.Attribute;

public class Redcon {
    public static Planet redcon;

    public static void load() {
        redcon = new Planet("redcon", Planets.sun, 5f) {{
            radius = 0.15f;
            orbitRadius = 446;
            hasAtmosphere = true;
            meshLoader = () -> new MultiMesh(
                    new HexMesh(this, 6)
            );

            allowSectorInvasion = false;
            techTree = ModTechTree.tech;
            alwaysUnlocked = true;
            defaultCore = Blocks.terra;
            generateIcons = true;


            hiddenItems
                    .addAll(Items.serpuloItems)
                    .addAll(Items.erekirItems)
                    .removeAll(Itemsx.all);

            ruleSetter = r -> {
                r.waveTeam = Team.blue;
                r.placeRangeCheck = false; //TODO true or false?
                r.attributes.set(Attribute.heat, 1f);
                r.showSpawns = true;
                r.fog = false;
                r.staticFog = true;
                r.lighting = false;
                r.coreDestroyClear = true;
                r.onlyDepositCore = true; //TODO not sure
            };

            unlockedOnLand.add(Blocks.terra);
        }};

        Planets.serpulo.launchCandidates.add(redcon);
        redcon.launchCandidates.addAll(Planets.erekir, Planets.serpulo);
    }

    //TODO beta
    public static class RedconPlanetGenerator extends PlanetGenerator {
        @Override
        public float getHeight(Vec3 position) {
            return 0;
        }

        @Override
        public Color getColor(Vec3 position) {
            return null;
        }
    }
}