package tntm.content;

import arc.graphics.Color;
import arc.util.Log;
import mindustry.content.*;
import mindustry.game.Team;
import mindustry.graphics.g3d.*;
import mindustry.type.Planet;
import tntm.world.planets.RedconPlanetGenerator;

import static mindustry.Vars.content;

public class TntmPlanets {
    public static Planet redcon;

    public static void load() {
        redcon = new Planet("redcon", Planets.sun, 1f, 3){{
            generator = new RedconPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 6);
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 11, 0.15f, 0.13f, 5, new Color().set(Color.orange).mul(0.9f).a(0.75f), 2, 0.45f, 0.9f, 0.38f),
                    new HexSkyMesh(this, 1, 0.6f, 0.16f, 5, Color.white.cpy().lerp(Color.yellow, 0.55f).a(0.75f), 2, 0.45f, 1f, 0.41f)
            );

            launchCapacityMultiplier = 0.5f;
            sectorSeed = 2;
            allowWaves = true;

            allowWaveSimulation = true;
            allowSectorInvasion = true;
            allowLaunchSchematics = true;
            enemyCoreSpawnReplace = true;
            allowLaunchLoadout = true;

            //doesn't play well with configs
            prebuildBase = false;
            ruleSetter = r -> {
                r.waveTeam = Team.blue;
                r.defaultTeam = Team.crux;
                r.placeRangeCheck = false;
                r.attributes.clear();
                r.showSpawns = false;
            };
            atmosphereColor = Color.orange;
            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.3f;
            startSector = 15;
            alwaysUnlocked = true;
            landCloudColor = Color.orange.cpy().a(0.5f);

            hiddenItems.addAll(content.items()).removeAll(TntmItems.all);
            Log.info(hiddenItems);

            defaultCore = TntmBlocks.terra;
        }};

        Planets.serpulo.hiddenItems.addAll(TntmItems.all);
        Planets.erekir.hiddenItems.addAll(TntmItems.all);

        redcon.unlock();
        Planets.serpulo.launchCandidates.add(redcon);
        redcon.launchCandidates.addAll(Planets.erekir, Planets.serpulo);
    }
}