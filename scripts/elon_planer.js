//for console
//Vars.content.getByName(ContentType.planet, "elon");

let elon;

let _elonGenerator$r;
let _elonGenerator$vec;
let _elonGenerator$temp;
let _elonGenerator$rooms;
let _elonGenerator$nscl2;
let _elonGenerator$center;
let _elonGenerator$height;
let _elonGenerator$stroke2;
let _elonGenerator$roomseq;
let _elonGenerator$magnetic;
let _elonGenerator$perlinNoise;

let _elonGenerator$basesConstant = [
    57, 12, 31, 45, 9, 64, 60, 54
];

const elonGenerator = extend(PlanetGenerator, {
    seed: 98765,
    arr: [[]],

    arrmesh: [
    ],

    scl: 15,

    chump(val, isfirst) {
        return Mathf.clamp(Math.floor(val * (isfirst ? this.arr[0].length : this.arr.length)), 0, this.arr[0].length - 1);
    },

    oceansOffset: 0.05,
    basegen: new BaseGenerator(),

    rawHeight(position) {
        position = Tmp.v31.set(position).scl(this.scl);

        return (
            Mathf.pow(
                this.noise.octaveNoise3D(
                    9, 0.5, 1/3, 

                    position.x, 
                    position.y, 
                    position.z
                ), 
                
                2.3
            )

            + this.waterOffset
        ) / (1 + this.waterOffset);
    },

    getHeight(position) {
        return Math.max(this.rawHeight(position), this.water);
    },

    generateSector(sector) {
        for(let base of _elonGenerator$basesConstant) {
            if(sector.id != base) {
                continue;
            };

            sector.generateEnemyBase = true;
            return;
        };

        _elonGenerator$magnetic = Math.abs(tile.v.y) - sector.id/Number.MAX_SAFE_INTEGER;
        _elonGenerator$perlinNoise =  Noise.snoise3(tile.v.x, tile.v.y, tile.v.z, 0.002, 0.50);
        
        if(_elonGenerator$perlinNoise + (_elonGenerator$magnetic / 8) > 0.20 && (_elonGenerator$magnetic > 0.20)) {
            sector.generateEnemyBase = true;
            return;
        };

        sector.generateEnemyBase = false;
    },

    getColor(pos) {
        pos = this.getBlock(pos);
        
        return Tmp.c4.set(pos.mapColor).a(-pos.albedo + 1);
    },

    genTile(pos, tile) {
        tile.floor = this.getBlock(pos);
        tile.block = tile.floor.asFloor().wall;

        if(this.rid.getValue(pos.x, pos.y, pos.z, 17) > 0.42){
            tile.block = Blocks.air;
        };
    },

    getBlock(pos) {
        _elonGenerator$height = Mathf.clamp(this.rawHeight(pos) * 1.2);
        Tmp.v31.set(pos);

        pos = Tmp.v33.set(pos).scl(this.scl);

        _elonGenerator$temp = Mathf.clamp(Math.abs(pos.y * 1.9) / this.scl);
        _elonGenerator$perlinNoise = this.noise.octaveNoise3D(
            8, 0.60, 1 / 3, pos.x, pos.y + 1000, pos.z
        );

        _elonGenerator$temp = Mathf.lerp(_elonGenerator$temp, _elonGenerator$perlinNoise, 0.6);

        return this.arr[this.chump(_elonGenerator$temp, false)][this.chump(_elonGenerator$height, true)];
    },

    noiseOct(x, y, octaves, falloff, scl){
        _elonGenerator$vec = this.sector.rect.project(x, y).scl(4.5);

        return this.noise.octaveNoise3D(
            octaves, falloff, 0.9 / scl, 

            _elonGenerator$vec.x,
            _elonGenerator$vec.y, 
            _elonGenerator$vec.z
        );
    },

    room$init: {
        x: 0, y: 0, r: 0,
        connected: new ObjectSet(),

        connect(self) {
            if(this.connected.contains(self)) {
                return;
            };

            this.connected.add(self);

            _elonGenerator$nscl2 = rand.random(25, 55);
            _elonGenerator$stroke2 = rand.random(2, 8);

            elonGenerator.brush(
                elonGenerator.pathfind(
                    this.x, this.y, self.x, self.y,

                    (tile) => (
                        (tile.solid() ? 5 : 0) +

                        elonGenerator.noiseOct(tile.x, tile.y, 1, 1, 1 / _elonGenerator$nscl2) * 50, 
                        Astar.manhattan
                    )
                ), 
                
                _elonGenerator$stroke2
            );
        },

        setPos(vec2) {
            this.x = vec2.x;
            this.y = vec2.y;

            return this;
        },

        setRadius(r) {
            this.r = r;

            return this;
        }
    },

    room$set(x, y, r) {
        return Object.create(this.Room).setPos(Vec2(x, y)).setRadius(r);
    },

    short$1(val) {
        return Math.floor(val/2 + Angles.trnsx(
            _elonGenerator$center.stack.angle, 
            _elonGenerator$center.vars.len
        ));
    },

    generate(tiles, sec){
        this.tiles = tiles;
        this.sector = sec;

        this.rand.setSeed(sec.id);

        this.cells(5);
        this.distort(8, 10);

        this.width = this.tiles.width;
        this.height = this.tiles.height;

        _elonGenerator$rooms = rand.random(1, 7);
        _elonGenerator$r = this.width / 2 / Mathf.sqrt3;
        _elonGenerator$roomseq = new Seq();

        for(let i = 0; i < _elonGenerator$rooms; i++) {
            Tmp.v1.trns(rand.random(360), rand.random(_elonGenerator$r / 1.4));

            _elonGenerator$roomseq.add(
                this.room$set(
                    Math.floor(this.width/2 + Tmp.v1.x),
                    Math.floor(this.height/4 + Tmp.v1.y),
                    
                    Math.floor(
                        Math.min(
                            rand.random(
                                6,
                                
                                (
                                    _elonGenerator$r - Tmp.v1.len()
                                ) / 2,
                            ),

                            30
                        )
                    )
                )
            );
        };

        _elonGenerator$center = { player: null, enemies: new Seq(), 
            enemy: rand.random(1, Math.max(Mathf.floor(this.sector.threat * 2), 1)),

            vars: {
                offset: rand.nextInt(360),
                len: this.width / 2.55 - (rand.random(3, 13) + 10)
            },

            stack: {
                roomsdouble: -1,
                angle: null,
                tile: null,
                oct: 0,

                x1: 0,
                y1: 0
            }
        };

        for(let i = 0; i < 360; i += 6) {
            _elonGenerator$center.stack.angle = _elonGenerator$center.vars.offset + i;
            
            _elonGenerator$center.stack.x1 = this.short$1(this.width);
            _elonGenerator$center.stack.y1 = this.short$1(this.height);

            _elonGenerator$center.stack.oct = 0;

            for(let tx = -6; tx < 6; tx++) {
                for(let ty = -6; ty < 6; ty++) {
                    _elonGenerator$center.stack.tile = tiles.get(
                        _elonGenerator$center.stack.x1 + tx,
                        _elonGenerator$center.stack.y1 + ty
                    );

                    if(_elonGenerator$center.stack.tile == null) {
                        ++_elonGenerator$center.stack.oct;
                    };

                    if(_elonGenerator$spawn.stack.tile.floor().liquidDrop != null) {
                        ++_elonGenerator$spawn.stack.oct;
                    };
                };
            };

            if(_elonGenerator$center.stack.oct <= 5 || (i + 6 >= 360)) {
                _elonGenerator$roomseq.add(
                    _elonGenerator$center.player = this.room$set(
                        _elonGenerator$center.stack.x1,
                        _elonGenerator$center.stack.y1,

                        rand.random(5, 16)
                    )
                );

                for(let j = 0; j < _elonGenerator$center.enemy; j++) {
                    Tmp.v1.set(
                        (_elonGenerator$center.stack.x1 - (this.width / 2)), 
                        (_elonGenerator$center.stack.y1 - (this.height / 2))

                    ).rotate(180 + rand.range(60)).add(this.width / 2, this.height / 2);

                    _elonGenerator$center.stack.roomsdouble = this.room$set(
                        Math.floor(Tmp.v1.x), Math.floor(Tmp.v1.y), rand.random(10, 16)
                    );

                    _elonGenerator$roomseq.add(_elonGenerator$center.stack.roomsdouble);
                    _elonGenerator$center.enemies.add(_elonGenerator$center.stack.roomsdouble);
                };

                break;
            };

            _elonGenerator$roomseq.each(
                room => this.erase(room.x, room.y, room.r)
            );

            _elonGenerator$center.cn = rand.random(
                Math.max(_elonGenerator$rooms - 1, 1), 
                _elonGenerator$rooms + 3
            );

            for(let i = 0; i < _elonGenerator$center.cn; i++) {
                _elonGenerator$roomseq.random(rand).connect(_elonGenerator$roomseq.random(rand));
            };

            _elonGenerator$roomseq.each(room => {
                spawn.connect(room)
            });

            this.cells(1);
            this.distort(10, 6);

            this.trimDark();
            this.median(2);
            this.tech();

            this.ints.clear();
            this.ints.ensureCapacity(this.width * this.height / 4);

            _elonGenerator$center.enemies.each(e => this.tiles.getn(e.x, e.y).setOverlay(Blocks.spawn));

            Vars.state.rules.waves = this.sector.info.waves = true;
            Vars.state.rules.enemyCoreBuildRadius = 440;
        };
    },

    postGenerate() {
        if(this.sector.hasEnemyBase()){
            this.basegen.postGenerate();
        };
    }
});

Events.on(ClientLoadEvent, () => {
    elon = new Planet("elon", Planets.sun, 1, 2);
    
    elon.generator = new SerpuloPlanetGenerator();

    for(let i in elon) {
        print(i);
    };

    print('---------------- xd -------------');

    for(let i in elon.generator) {
        print(i);
    };

    for(let i of TechTree.roots.toArray()) {
        if(i.name === "elon") {
            elon.techTree = i;
        };
    };

    elon.mesh = new HexMesh(elon, 4.1);
    elon.orbitRadius = 50;
    elon.orbitTime = 1.5 * 600;
    elon.rotateTime = 3200;
    elon.bloom = true;
    elon.accessible = true;
    elon.startSector = 15;
    elon.hasAtmosphere = true;
    elon.atmosphereColor = Liquids.cryofluid.color;
    elon.atmosphereRadIn = 0.04;
    elon.atmosphereRadOut = 0.3;
    elon.alwaysUnlocked = true;
    elon.localizedName = "@planets.elon.name";

    elon.defaultCore = Vars.content.getByName(ContentType.block, "the-new-tech-mod-exiopolis");
    
    elon.hiddenItems.addAll(Items.serpuloItems);
    elon.hiddenItems.addAll(Items.erekirItems);
});


//it was hard