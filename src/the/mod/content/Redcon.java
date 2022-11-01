package the.mod.content;

import mindustry.content.*;
import mindustry.game.Team;
import mindustry.graphics.g3d.*;
import mindustry.type.Planet;
import mindustry.world.meta.Attribute;
import the.mod.utils.RedconPlanetGenerator;

public class Redcon {
    public static Planet redcon;

    public static void load() {
        redcon = new Planet("redcon", Planets.sun, 1f, 4) {{
            generator = new RedconPlanetGenerator();

            radius = 24f;
            orbitRadius = 2.1f;
            hasAtmosphere = true;

            allowSectorInvasion = false;
            techTree = ModTechTree.tech;
            alwaysUnlocked = true;
            defaultCore = Blocksx.terra;
            generateIcons = true;

            hiddenItems
                    .addAll(Items.serpuloItems)
                    .addAll(Items.erekirItems)
                    .removeAll(Itemsx.all);

            ruleSetter = r -> {
                r.waveTeam = Team.blue;
                r.placeRangeCheck = false;
                r.attributes.set(Attribute.heat, 1f);
                r.showSpawns = true;
                r.fog = false;
                r.staticFog = true;
                r.lighting = false;
                r.coreDestroyClear = true;
                r.onlyDepositCore = true;
            };

            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.3f;
            unlockedOnLand.add(Blocksx.terra);
        }};

        redcon.unlock();
        Planets.serpulo.launchCandidates.add(redcon);
        redcon.launchCandidates.addAll(Planets.erekir, Planets.serpulo);
    }
}