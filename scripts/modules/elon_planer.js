let elon, elonGenerator;

Events.on(ClientLoadEvent, () => {
    elon = new Planet("@planets.elon.name", Planets.sun, 10);
    elonGenerator = extend(PlanetGenerator, () => {
    });

    elonGenerator.rid = new Packages.arc.util.noise.RidgedPerlin(1, 2);
    elonGenerator.basegen = new BaseGenerator();
    elonGenerator.scl = 5;
    elonGenerator.waterOffset = 0.07;
    elonGenerator.water = 2 / elonGenerator.arr[0].length;

    elon.defaultCore = Vars.content.getByName(ContentType.block, "the-new-tech-mod-exiopolis");

    //Planet
    elon.allowWaves = true;
    elon.accessible = true;
    elon.allowLaunchLoadout = true;
    elon.allowLaunchSchematics = true;
    elon.allowSectorInvasion = true;
    elon.atmosphereColor = Colors.cyan;
    elon.meshLoader = () => new HexMesh(elon, 6);
    elon.startSector = 10;

    //Planet Generator
    elonGenerator.floor = Vars.content.getByName(ContentType.block, 'the-new-tech-mod-cyan-show');
    elonGenerator.block = Vars.content.getByName(ContentType.block, 'the-new-tech-mod-cyan-show-wall');

    elon.generator = elonGenerator;
});