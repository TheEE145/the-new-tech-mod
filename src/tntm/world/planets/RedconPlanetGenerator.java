package tntm.world.planets;

import arc.func.Intc;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.ai.*;
import mindustry.ai.BaseRegistry.*;
import mindustry.content.Blocks;
import mindustry.content.StatusEffects;
import mindustry.game.*;
import mindustry.graphics.g3d.PlanetGrid.*;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import tntm.content.TntmBlocks;
import tntm.content.TntmLiquids;

import static mindustry.Vars.*;
import static mindustry.content.Blocks.air;
import static mindustry.content.Blocks.oreCoal;
import static mindustry.content.Blocks.spawn;
import static mindustry.content.UnitTypes.*;
import static mindustry.content.UnitTypes.eclipse;

/**from Mindustry by Anuken (edited version)*/
public class RedconPlanetGenerator extends PlanetGenerator{
    //alternate, less direct generation (wip)
    public static final Block spawn2 = spawn;
    public static boolean alt = false;

    BaseGenerator basegen = new BaseGenerator();
    float scl = 5f;
    float waterOffset = 0.07f;
    boolean genLakes = false;

    public static final Block
            sand     = TntmBlocks.mantium,
            darkSand = TntmBlocks.virusMFloor,
            ice      = TntmBlocks.orangeIce,
            stone    = TntmBlocks.mantium,
            water2   = TntmBlocks.emethen,
            basalt   = TntmBlocks.crystals,
            salt     = TntmBlocks.crystals,
            snow     = TntmBlocks.crystals;

    public static final ObjectMap<Block, Block> crystals = ObjectMap.of(
            TntmBlocks.virusMFloor, TntmBlocks.virusMCrystal
    );

    Block[][] arr =
            {
                    {
                            water2,
                            water2,
                            darkSand,
                            darkSand,
                            darkSand,
                            darkSand,
                            sand,
                            sand,
                            sand,
                            sand,
                            water2,
                            stone,
                            stone
                    },

                    {
                            water2,
                            water2,
                            darkSand,
                            darkSand,
                            sand,
                            sand,
                            sand,
                            sand,
                            sand,
                            water2,
                            stone,
                            stone,
                            stone
                    },

                    {
                            water2,
                            water2,
                            water2,
                            sand,
                            salt,
                            sand,
                            sand,
                            sand,
                            sand,
                            water2,
                            stone,
                            stone,
                            stone
                    },

                    {
                            water2,
                            water2,
                            sand,
                            salt,
                            salt,
                            salt,
                            sand,
                            stone,
                            stone,
                            stone,
                            snow,
                            ice,
                            ice
                    },

                    {
                            water2,
                            water2,
                            water2,
                            sand,
                            salt,
                            sand,
                            sand,
                            basalt,
                            snow,
                            snow,
                            snow,
                            snow,
                            ice
                    },

                    {
                            water2,
                            water2,
                            water2,
                            sand,
                            sand,
                            sand,
                            stone,
                            ice,
                            snow,
                            snow,
                            ice,
                            snow,
                            ice
                    },

                    {
                            water2,
                            water2,
                            sand,
                            sand,
                            stone,
                            stone,
                            snow,
                            basalt,
                            basalt,
                            basalt,
                            ice,
                            snow,
                            ice
                    },

                    {
                            water2,
                            water2,
                            darkSand,
                            darkSand,
                            basalt,
                            stone,
                            basalt,
                            stone,
                            basalt,
                            ice,
                            snow,
                            ice,
                            ice
                    },

                    {
                            water2,
                            darkSand,
                            darkSand,
                            darkSand,
                            stone,
                            stone,
                            snow,
                            basalt,
                            basalt,
                            ice,
                            snow,
                            ice,
                            ice
                    },

                    {
                            water2,
                            darkSand,
                            darkSand,
                            stone,
                            ice,
                            ice,
                            snow,
                            snow,
                            snow,
                            snow,
                            ice,
                            ice,
                            ice
                    },

                    {
                            water2,
                            water2,
                            darkSand,
                            stone,
                            stone,
                            ice,
                            ice,
                            snow,
                            snow,
                            ice,
                            ice,
                            ice,
                            ice
                    },

                    {
                            water2,
                            water2,
                            darkSand,
                            stone,
                            stone,
                            stone,
                            ice,
                            snow,
                            ice,
                            ice,
                            ice,
                            ice,
                            ice
                    },

                    {
                            water2,
                            darkSand,
                            snow,
                            ice,
                            ice,
                            snow,
                            snow,
                            snow,
                            ice,
                            ice,
                            ice,
                            ice,
                            ice
                    }
            };

    ObjectMap<Block, Block> dec = ObjectMap.of();
    ObjectMap<Block, Block> tars = ObjectMap.of();

    float water = 2f / arr[0].length;

    float rawHeight(Vec3 position){
        position = Tmp.v33.set(position).scl(scl);
        return (Mathf.pow(Simplex.noise3d(seed, 7, 0.5f, 1f/3f, position.x, position.y, position.z), 2.3f) + waterOffset) / (1f + waterOffset);
    }

    @Override
    public void generateSector(Sector sector){

        //these always have bases
        if(sector.id == 154 || sector.id == 0){
            sector.generateEnemyBase = true;
            return;
        }

        Ptile tile = sector.tile;

        boolean any = false;
        float poles = Math.abs(tile.v.y);
        float noise = Noise.snoise3(tile.v.x, tile.v.y, tile.v.z, 0.001f, 0.58f);

        if(noise + poles/7.1 > 0.12 && poles > 0.23){
            any = true;
        }

        if(noise < 0.16){
            for(Ptile other : tile.tiles){
                var osec = sector.planet.getSector(other);

                //no sectors near start sector!
                if(
                        osec.id == sector.planet.startSector || //near starting sector
                                osec.generateEnemyBase && poles < 0.85 || //near other base
                                (sector.preset != null && noise < 0.11) //near preset
                ){
                    return;
                }
            }
        }

        sector.generateEnemyBase = any;
    }

    @Override
    public float getHeight(Vec3 position){
        float height = rawHeight(position);
        return Math.max(height, water);
    }

    @Override
    public Color getColor(Vec3 position){
        Block block = getBlock(position);
        //replace salt with sand color
        if(block == salt) return salt.mapColor;
        return Tmp.c1.set(block.mapColor).a(1f - block.albedo);
    }

    @Override
    public void genTile(Vec3 position, TileGen tile){
        tile.floor = getBlock(position);
        tile.block = tile.floor.asFloor().wall;

        if(Ridged.noise3d(seed + 1, position.x, position.y, position.z, 2, 22) > 0.31){
            tile.block = air;
        }
    }

    Block getBlock(Vec3 position){
        float height = rawHeight(position);
        Tmp.v31.set(position);
        position = Tmp.v33.set(position).scl(scl);
        float rad = scl;
        float temp = Mathf.clamp(Math.abs(position.y * 2f) / (rad));
        float tnoise = Simplex.noise3d(seed, 7, 0.56, 1f/3f, position.x, position.y + 999f, position.z);
        temp = Mathf.lerp(temp, tnoise, 0.5f);
        height *= 1.2f;
        height = Mathf.clamp(height);

        float tar = Simplex.noise3d(seed, 4, 0.55f, 1f/2f, position.x, position.y + 999f, position.z) * 0.3f + Tmp.v31.dst(0, 0, 1f) * 0.2f;

        Block res = arr[Mathf.clamp((int)(temp * arr.length), 0, arr[0].length - 1)][Mathf.clamp((int)(height * arr[0].length), 0, arr[0].length - 1)];
        if(tar > 0.5f){
            return tars.get(res, res);
        }else{
            return res;
        }
    }

    @Override
    protected float noise(float x, float y, double octaves, double falloff, double scl, double mag){
        Vec3 v = sector.rect.project(x, y).scl(5f);
        return Simplex.noise3d(seed, octaves, falloff, 1f / scl, v.x, v.y, v.z) * (float)mag;
    }

    @Override
    protected void generate(){
        class Room{
            final int x;
            final int y;
            final int radius;
            final ObjectSet<Room> connected = new ObjectSet<>();

            Room(int x, int y, int radius){
                this.x = x;
                this.y = y;
                this.radius = radius;
                connected.add(this);
            }

            void join(int x1, int y1, int x2, int y2){
                float nscl = rand.random(100f, 140f) * 6f;
                int stroke = rand.random(3, 9);
                brush(pathfind(x1, y1, x2, y2, tile -> (tile.solid() ? 50f : 0f) + noise(tile.x, tile.y, 2, 0.4f, 1f / nscl) * 500, Astar.manhattan), stroke);
            }

            void connect(Room to){
                if(!connected.add(to) || to == this) return;

                Vec2 midpoint = Tmp.v1.set(to.x, to.y).add(x, y).scl(0.5f);
                rand.nextFloat();

                if(alt){
                    midpoint.add(Tmp.v2.set(1, 0f).setAngle(Angles.angle(to.x, to.y, x, y) + 90f * (rand.chance(0.5) ? 1f : -1f)).scl(Tmp.v1.dst(x, y) * 2f));
                }else{
                    //add randomized offset to avoid straight lines
                    midpoint.add(Tmp.v2.setToRandomDirection(rand).scl(Tmp.v1.dst(x, y)));
                }

                midpoint.sub(width/2f, height/2f).limit(width / 2f / Mathf.sqrt3).add(width/2f, height/2f);

                int mx = (int)midpoint.x, my = (int)midpoint.y;

                join(x, y, mx, my);
                join(mx, my, to.x, to.y);
            }

            void joinLiquid(int x1, int y1, int x2, int y2){
                float nscl = rand.random(100f, 140f) * 6f;
                int rad = rand.random(7, 11);
                int avoid = 2 + rad;
                var path = pathfind(x1, y1, x2, y2, tile -> (tile.solid() || !tile.floor().isLiquid ? 70f : 0f) + noise(tile.x, tile.y, 2, 0.4f, 1f / nscl) * 500, Astar.manhattan);
                path.each(t -> {
                    //don't place liquid paths near the core
                    if(Mathf.dst2(t.x, t.y, x2, y2) <= avoid * avoid){
                        return;
                    }

                    for(int x = -rad; x <= rad; x++){
                        for(int y = -rad; y <= rad; y++){
                            int wx = t.x + x, wy = t.y + y;
                            if(Structs.inBounds(wx, wy, width, height) && Mathf.within(x, y, rad)){
                                Tile other = tiles.getn(wx, wy);
                                other.setBlock(air);
                                if(Mathf.within(x, y, rad - 1) && !other.floor().isLiquid){
                                    Floor floor = other.floor();
                                    //TODO does not respect tainted floors
                                    other.setFloor((Floor)(water2));
                                }
                            }
                        }
                    }
                });
            }

            void connectLiquid(Room to){
                if(to == this) return;

                Vec2 midpoint = Tmp.v1.set(to.x, to.y).add(x, y).scl(0.5f);
                rand.nextFloat();

                //add randomized offset to avoid straight lines
                midpoint.add(Tmp.v2.setToRandomDirection(rand).scl(Tmp.v1.dst(x, y)));
                midpoint.sub(width/2f, height/2f).limit(width / 2f / Mathf.sqrt3).add(width/2f, height/2f);

                int mx = (int)midpoint.x, my = (int)midpoint.y;

                joinLiquid(x, y, mx, my);
                joinLiquid(mx, my, to.x, to.y);
            }
        }

        cells(4);
        distort(10f, 12f);

        float constraint = 1.3f;
        float radius = width / 2f / Mathf.sqrt3;
        int rooms = rand.random(2, 5);
        Seq<Room> roomseq = new Seq<>();

        for(int i = 0; i < rooms; i++){
            Tmp.v1.trns(rand.random(360f), rand.random(radius / constraint));
            float rx = (width/2f + Tmp.v1.x);
            float ry = (height/2f + Tmp.v1.y);
            float maxrad = radius - Tmp.v1.len();
            float rrad = Math.min(rand.random(9f, maxrad / 2f), 30f);
            roomseq.add(new Room((int)rx, (int)ry, (int)rrad));
        }

        //check positions on the map to place the player spawn. this needs to be in the corner of the map
        Room spawn = null;
        Seq<Room> enemies = new Seq<>();
        int enemySpawns = rand.random(1, Math.max((int)(sector.threat * 4), 1));
        int offset = rand.nextInt(360);
        float length = width/2.55f - rand.random(13, 23);
        int angleStep = 5;
        int waterCheckRad = 5;
        for(int i = 0; i < 360; i+= angleStep){
            int angle = offset + i;
            int cx = (int)(width/2 + Angles.trnsx(angle, length));
            int cy = (int)(height/2 + Angles.trnsy(angle, length));

            int waterTiles = 0;

            //check for water presence
            for(int rx = -waterCheckRad; rx <= waterCheckRad; rx++){
                for(int ry = -waterCheckRad; ry <= waterCheckRad; ry++){
                    Tile tile = tiles.get(cx + rx, cy + ry);
                    if(tile == null || tile.floor().liquidDrop != null){
                        waterTiles ++;
                    }
                }
            }

            if(waterTiles <= 4 || (i + angleStep >= 360)){
                roomseq.add(spawn = new Room(cx, cy, rand.random(8, 15)));

                for(int j = 0; j < enemySpawns; j++){
                    float enemyOffset = rand.range(60f);
                    Tmp.v1.set(cx - width/2f, cy - height/2f).rotate(180f + enemyOffset).add(width/2f, height/2f);
                    Room espawn = new Room((int)Tmp.v1.x, (int)Tmp.v1.y, rand.random(8, 16));
                    roomseq.add(espawn);
                    enemies.add(espawn);
                }

                break;
            }
        }

        //clear radius around each room
        for(Room room : roomseq){
            erase(room.x, room.y, room.radius);
        }

        //randomly connect rooms together
        int connections = rand.random(Math.max(rooms - 1, 1), rooms + 3);
        for(int i = 0; i < connections; i++){
            roomseq.random(rand).connect(roomseq.random(rand));
        }

        for(Room room : roomseq){
            assert spawn != null;
            spawn.connect(room);
        }

        Room fspawn = spawn;

        cells(1);

        int tlen = tiles.width * tiles.height;
        int total = 0, waters = 0;

        for(int i = 0; i < tlen; i++){
            Tile tile = tiles.geti(i);
            if(tile.block() == air){
                total ++;
                if(tile.floor().liquidDrop == TntmLiquids.emethen) {
                    waters ++;
                }
            }
        }

        boolean naval = (float)waters / total >= 0.19f;

        //create water pathway if the map is flooded
        if(naval){
            for(Room room : enemies){
                room.connectLiquid(spawn);
            }
        }

        distort(10f, 6f);

        //rivers
        pass((x, y) -> {
            if(block.solid) return;

            Vec3 v = sector.rect.project(x, y);

            float rr = Simplex.noise2d(sector.id, (float)2, 0.6f, 1f / 7f, x, y) * 0.1f;
            float value = Ridged.noise3d(2, v.x, v.y, v.z, 1, 1f / 55f) + rr - rawHeight(v) * 0f;
            float rrscl = rr * 44 - 2;

            if(value > 0.17f && !Mathf.within(x, y, fspawn.x, fspawn.y, 12 + rrscl)){
                boolean deep = value > 0.17f + 0.1f && !Mathf.within(x, y, fspawn.x, fspawn.y, 15 + rrscl);
                boolean spore = floor != sand && floor != salt;
                //do not place rivers on ice, they're frozen
                //ignore pre-existing liquids
                if(!(floor == ice || floor == snow || floor.asFloor().isLiquid)){
                    floor = water2;
                }
            }
        });

        //shoreline setup
        pass((x, y) -> {
            int deepRadius = 3;

            if(floor.asFloor().isLiquid && floor.asFloor().shallow){

                for(int cx = -deepRadius; cx <= deepRadius; cx++){
                    for(int cy = -deepRadius; cy <= deepRadius; cy++){
                        if((cx) * (cx) + (cy) * (cy) <= deepRadius * deepRadius){
                            int wx = cx + x, wy = cy + y;

                            Tile tile = tiles.get(wx, wy);
                            if(tile != null && (!tile.floor().isLiquid || tile.block() != air)){
                                //found something solid, skip replacing anything
                                return;
                            }
                        }
                    }
                }

                floor = water2;
            }
        });

        if(naval){
            int deepRadius = 2;

            //TODO code is very similar, but annoying to extract into a separate function
            pass((x, y) -> {
                if(floor.asFloor().isLiquid && !floor.asFloor().isDeep() && !floor.asFloor().shallow){

                    for(int cx = -deepRadius; cx <= deepRadius; cx++){
                        for(int cy = -deepRadius; cy <= deepRadius; cy++){
                            if((cx) * (cx) + (cy) * (cy) <= deepRadius * deepRadius){
                                int wx = cx + x, wy = cy + y;

                                Tile tile = tiles.get(wx, wy);
                                if(tile != null && (tile.floor().shallow || !tile.floor().isLiquid)){
                                    //found something shallow, skip replacing anything
                                    return;
                                }
                            }
                        }
                    }

                    floor = water2;
                }
            });
        }

        Seq<Block> ores = Seq.with(TntmBlocks.silicaOre /*Blocks.oreLead*/);
        float poles = Math.abs(sector.tile.v.y);
        float nmag = 0.5f;
        float scl = 1f;
        float addscl = 1.3f;

        if(Simplex.noise3d(seed, 2, 0.5, scl, sector.tile.v.x, sector.tile.v.y, sector.tile.v.z)*nmag + poles > 0.25f*addscl){
            ores.add(oreCoal);
        }

        if(Simplex.noise3d(seed, 2, 0.5, scl, sector.tile.v.x + 1, sector.tile.v.y, sector.tile.v.z)*nmag + poles > 0.5f*addscl){
            ores.add(TntmBlocks.silicaOre);
        }

        if(Simplex.noise3d(seed, 2, 0.5, scl, sector.tile.v.x + 2, sector.tile.v.y, sector.tile.v.z)*nmag + poles > 0.7f*addscl){
            //ores.add(Blocks.oreThorium);
        }

        if(rand.chance(0.25)){
            ores.add(TntmBlocks.silicaOre);
        }

        FloatSeq frequencies = new FloatSeq();
        for(int i = 0; i < ores.size; i++){
            frequencies.add(rand.random(-0.1f, 0.01f) - i * 0.01f + poles * 0.04f);
        }

        pass((x, y) -> {
            if(!floor.asFloor().hasSurface()) return;

            int offsetX = x - 4, offsetY = y + 23;
            for(int i = ores.size - 1; i >= 0; i--){
                Block entry = ores.get(i);
                float freq = frequencies.get(i);
                if(Math.abs(0.5f - noise(offsetX, offsetY + i*999, 2, 0.7, (40 + i * 2))) > 0.22f + i*0.01 &&
                        Math.abs(0.5f - noise(offsetX, offsetY - i*999, 1, 1, (30 + i * 4))) > 0.37f + freq){
                    ore = entry;
                    break;
                }
            }

            //if(ore == Blocks.oreScrap && rand.chance(0.33)){
            //    floor = Blocks.metalFloorDamaged;
            //}
        });

        trimDark();

        median(2);

        inverseFloodFill(tiles.getn(spawn.x, spawn.y));

        tech();

        pass((x, y) -> {
            //random stuff
            dec: {
                for(int i = 0; i < 4; i++){
                    Tile near = world.tile(x + Geometry.d4[i].x, y + Geometry.d4[i].y);
                    if(near != null && near.block() != air){
                        break dec;
                    }
                }

                if(rand.chance(0.01) && floor.asFloor().hasSurface() && block ==air){
                    block = dec.get(floor, floor.asFloor().decoration);
                }
            }
        });

        float difficulty = sector.threat;
        ints.clear();
        ints.ensureCapacity(width * height / 4);

        int ruinCount = rand.random(-2, 4);
        if(ruinCount > 0){
            int padding = 25;

            //create list of potential positions
            for(int x = padding; x < width - padding; x++){
                for(int y = padding; y < height - padding; y++){
                    Tile tile = tiles.getn(x, y);
                    if(!tile.solid() && (tile.drop() != null || tile.floor().liquidDrop != null)){
                        ints.add(tile.pos());
                    }
                }
            }

            ints.shuffle(rand);

            int placed = 0;
            float diffRange = 0.4f;
            //try each position
            for(int i = 0; i < ints.size && placed < ruinCount; i++){
                int val = ints.items[i];
                int x = Point2.x(val), y = Point2.y(val);

                //do not overwrite player spawn
                if(Mathf.within(x, y, spawn.x, spawn.y, 18f)){
                    continue;
                }

                float range = difficulty + rand.random(diffRange);

                Tile tile = tiles.getn(x, y);
                BasePart part = null;
                if(tile.overlay().itemDrop != null){
                    part = bases.forResource(tile.drop()).getFrac(range);
                }else if(tile.floor().liquidDrop != null && rand.chance(0.05)){
                    part = bases.forResource(tile.floor().liquidDrop).getFrac(range);
                }else if(rand.chance(0.05)){ //ore-less parts are less likely to occur.
                    part = bases.parts.getFrac(range);
                }

                //actually place the part
                if(part != null && BaseGenerator.tryPlace(part, x, y, Team.derelict, (cx, cy) -> {
                    Tile other = tiles.getn(cx, cy);
                    if(other.floor().hasSurface()){
                        other.setOverlay(oreCoal);
                        for(int j = 1; j <= 2; j++){
                            for(Point2 p : Geometry.d8){
                                Tile t = tiles.get(cx + p.x*j, cy + p.y*j);
                                if(t != null && t.floor().hasSurface() && rand.chance(j == 1 ? 0.4 : 0.2)){
                                    t.setOverlay(oreCoal);
                                }
                            }
                        }
                    }
                })){
                    placed ++;

                    int debrisRadius = Math.max(part.schematic.width, part.schematic.height)/2 + 3;
                    Geometry.circle(x, y, tiles.width, tiles.height, debrisRadius, (cx, cy) -> {
                        float dst = Mathf.dst(cx, cy, x, y);
                        float removeChance = Mathf.lerp(0.05f, 0.5f, dst / debrisRadius);

                        Tile other = tiles.getn(cx, cy);
                        if(other.build != null && other.isCenter()){
                            if(other.team() == Team.derelict && rand.chance(removeChance)){
                                other.remove();
                            }else if(rand.chance(0.5)){
                                other.build.health = other.build.health - rand.random(other.build.health * 0.9f);
                            }
                        }
                    });
                }
            }
        }

        //remove invalid ores
        for(Tile tile : tiles){
            if(tile.overlay().needsSurface && !tile.floor().hasSurface()){
                tile.setOverlay(air);
            }
        }

        Schematics.placeLaunchLoadout(spawn.x, spawn.y);

        for(Room espawn : enemies){
            tiles.getn(espawn.x, espawn.y).setOverlay(spawn2);
        }

        if(sector.hasEnemyBase()){
            basegen.generate(tiles, enemies.map(r -> tiles.getn(r.x, r.y)), tiles.get(spawn.x, spawn.y), state.rules.waveTeam, sector, difficulty);

            state.rules.attackMode = sector.info.attack = true;
        }else{
            state.rules.winWave = sector.info.winWave = 10 + 5 * (int)Math.max(difficulty * 10, 1);
        }

        float waveTimeDec = 0.4f;

        state.rules.waveSpacing = Mathf.lerp(60 * 65 * 2, 60f * 60f * 1f, Math.max(difficulty - waveTimeDec, 0f));
        state.rules.waves = sector.info.waves = true;
        state.rules.env = sector.planet.defaultEnv;
        state.rules.enemyCoreBuildRadius = 600f;

        //spawn air only when spawn is blocked
        state.rules.spawns = generate(difficulty, new Rand(sector.id), state.rules.attackMode,
                state.rules.attackMode && spawner.countGroundSpawns() == 0, naval);

        //ok
        for(Tile t : tiles) {
            if((t.block() == Blocks.coreShard || t.block() == Blocks.coreFoundation || t.block() == Blocks.coreNucleus) && t.build != null) {
                t.setNet(TntmBlocks.terra, t.build.team, t.build.rotation);
            } else {
                if(Mathf.chance(0.0025f) && t.block() == air && !t.floor().isLiquid) {
                    if(crystals.get(t.block()) != null) {
                        t.setNet(crystals.get(t.block()));
                    } else {
                        t.setNet(TntmBlocks.silicaCrystal);
                    }
                }
            }
        }
    }

    @Override
    public void postGenerate(Tiles tiles){
        if(sector.hasEnemyBase()){
            basegen.postGenerate();
        }
    }

    public static Seq<SpawnGroup> generate(float difficulty, Rand rand, boolean attack, boolean airOnly, boolean naval) {
        UnitType[][] species = {
                {
                        dagger,
                        mace,
                        fortress,
                        scepter,
                        reign
                },
                {
                        nova,
                        pulsar,
                        quasar,
                        vela,
                        corvus
                },
                {
                        crawler,
                        atrax,
                        spiroct,
                        arkyid,
                        toxopid
                },
                {
                        risso,
                        minke,
                        bryde,
                        sei,
                        omura
                },
                {
                        risso,
                        oxynoe,
                        cyerce,
                        aegires,
                        navanax
                },
                {
                        flare,
                        horizon,
                        zenith,
                        rand.chance(0.5) ? quad : antumbra,
                        rand.chance(0.1) ? quad : eclipse
                }
        };

        if (airOnly) {
            species = Structs.filter(UnitType[].class, species, v -> v[0].flying);
        }

        if (naval) {
            species = Structs.filter(UnitType[].class, species, v -> v[0].flying || v[0].naval);
        } else {
            species = Structs.filter(UnitType[].class, species, v -> !v[0].naval);
        }

        UnitType[][] fspec = species;

        //required progression:
        //- extra periodic patterns

        Seq<SpawnGroup> out = new Seq<>();

        //max reasonable wave, after which everything gets boring
        int cap = 150;

        float shieldStart = 30, shieldsPerWave = 20 + difficulty * 30f;
        float[] scaling = {1, 2f, 3f, 4f, 5f};

        Intc createProgression = start -> {
            //main sequence
            UnitType[] curSpecies = Structs.random(fspec);
            int curTier = 0;

            for(int i = start; i < cap;){
                int f = i;
                int next = rand.random(8, 16) + (int)Mathf.lerp(5f, 0f, difficulty) + curTier * 4;

                float shieldAmount = Math.max((i - shieldStart) * shieldsPerWave, 0);
                int space = start == 0 ? 1 : rand.random(1, 2);
                int ctier = curTier;

                //main progression
                out.add(new SpawnGroup(curSpecies[Math.min(curTier, curSpecies.length - 1)]){{
                    unitAmount = f == start ? 1 : 6 / (int)scaling[ctier];
                    begin = f;
                    end = f + next >= cap ? never : f + next;
                    max = 13;
                    unitScaling = (difficulty < 0.4f ? rand.random(2.5f, 5f) : rand.random(1f, 4f)) * scaling[ctier];
                    shields = shieldAmount;
                    shieldScaling = shieldsPerWave;
                    spacing = space;
                }});

                //extra progression that tails out, blends in
                out.add(new SpawnGroup(curSpecies[Math.min(curTier, curSpecies.length - 1)]){{
                    unitAmount = 3 / (int)scaling[ctier];
                    begin = f + next - 1;
                    end = f + next + rand.random(6, 10);
                    max = 6;
                    unitScaling = rand.random(2f, 4f);
                    spacing = rand.random(2, 4);
                    shields = shieldAmount/2f;
                    shieldScaling = shieldsPerWave;
                }});

                i += next + 1;
                if(curTier < 3 || (rand.chance(0.05) && difficulty > 0.8)){
                    curTier ++;
                }

                //do not spawn bosses
                curTier = Math.min(curTier, 3);

                //small chance to switch species
                if(rand.chance(0.3)){
                    curSpecies = Structs.random(fspec);
                }
            }
        };

        createProgression.get(0);

        int step = 5 + rand.random(5);

        while(step <= cap){
            createProgression.get(step);
            step += (int)(rand.random(15, 30) * Mathf.lerp(1f, 0.5f, difficulty));
        }

        int bossWave = (int)(rand.random(50, 70) * Mathf.lerp(1f, 0.5f, difficulty));
        int bossSpacing = (int)(rand.random(25, 40) * Mathf.lerp(1f, 0.5f, difficulty));

        int bossTier = difficulty < 0.6 ? 3 : 4;

        //main boss progression
        out.add(new SpawnGroup(Structs.random(species)[bossTier]){{
            unitAmount = 1;
            begin = bossWave;
            spacing = bossSpacing;
            end = never;
            max = 16;
            unitScaling = bossSpacing;
            shieldScaling = shieldsPerWave;
            effect = StatusEffects.boss;
        }});

        //alt boss progression
        out.add(new SpawnGroup(Structs.random(species)[bossTier]){{
            unitAmount = 1;
            begin = bossWave + rand.random(3, 5) * bossSpacing;
            spacing = bossSpacing;
            end = never;
            max = 16;
            unitScaling = bossSpacing;
            shieldScaling = shieldsPerWave;
            effect = StatusEffects.boss;
        }});

        int finalBossStart = 120 + rand.random(30);

        //final boss waves
        out.add(new SpawnGroup(Structs.random(species)[bossTier]){{
            unitAmount = 1;
            begin = finalBossStart;
            spacing = bossSpacing/2;
            end = never;
            unitScaling = bossSpacing;
            shields = 500;
            shieldScaling = shieldsPerWave * 4;
            effect = StatusEffects.boss;
        }});

        //final boss waves (alt)
        out.add(new SpawnGroup(Structs.random(species)[bossTier]){{
            unitAmount = 1;
            begin = finalBossStart + 15;
            spacing = bossSpacing/2;
            end = never;
            unitScaling = bossSpacing;
            shields = 500;
            shieldScaling = shieldsPerWave * 4;
            effect = StatusEffects.boss;
        }});

        //add mega to heal the base.
        if(attack && difficulty >= 0.5){
            int amount = Mathf.random(1, 3 + (int)(difficulty*2));

            for(int i = 0; i < amount; i++){
                int wave = Mathf.random(3, 20);
                out.add(new SpawnGroup(mega){{
                    unitAmount = 1;
                    begin = wave;
                    end = wave;
                    max = 16;
                }});
            }
        }

        //shift back waves on higher difficulty for a harder start
        int shift = Math.max((int)(difficulty * 14 - 5), 0);

        for(SpawnGroup group : out){
            group.begin -= shift;
            group.end -= shift;
        }

        return out;
    }
}