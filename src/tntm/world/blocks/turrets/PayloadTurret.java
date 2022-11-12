package tntm.world.blocks.turrets;

import arc.Core;
import arc.Events;
import arc.audio.Sound;
import arc.func.Boolf;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.EnumSet;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.core.World;
import mindustry.entities.*;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.pattern.ShootPattern;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.ControlBlock;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.payloads.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;
import tntm.utils.TntmMath;
import tntm.world.bullets.BlockBulletType;

import static mindustry.Vars.tilesize;

//from mindustry by Anuken (why we can`t extends PayloadBlock, Turret, ReloadTurret, BaseTurret in the same time)
public class PayloadTurret extends PayloadBlock {
    public boolean dynamicBullet;
    public float reload = 10f;
    //after being logic-controlled and this amount of time passes, the turret will resume normal AI
    public final static float logicControlCooldown = 60 * 2;

    public final int timerTarget = timers++;
    /** Ticks between attempt at finding a target. */
    public float targetInterval = 20;

    /** Minimum input heat required to fire. */
    public float heatRequirement = -1f;
    /** Maximum efficiency possible, if this turret uses heat. */
    public float maxHeatEfficiency = 3f;

    /** Bullet angle randomness in degrees. */
    public float inaccuracy = 0f;
    /** Turret shoot point. */
    public float shootX = 0f, shootY = Float.NEGATIVE_INFINITY;
    /** Random spread on the X axis. */
    public float xRand = 0f;
    /** Minimum warmup needed to fire. */
    public float minWarmup = 0f;
    /** If true, this turret will accurately target moving targets with respect to charge time. */
    public boolean accurateDelay = true;
    /** pattern used for bullets */
    public ShootPattern shoot = new ShootPattern();

    public float range = 80f;
    public float placeOverlapMargin = 8 * 7f;

    /** Effect displayed when coolant is used. */
    public Effect coolEffect = Fx.fuelburn;
    /** How much reload is lowered by for each unit of liquid of heat capacity. */
    public float coolantMultiplier = 5f;
    /** If not null, this consumer will be used for coolant. */
    public @Nullable ConsumeLiquidBase coolant;

    /** If true, this block targets air units. */
    public boolean targetAir = true;
    /** If true, this block targets ground units and structures. */
    public boolean targetGround = true;
    /** If true, this block targets friend blocks, to heal them. */
    public boolean targetHealing = false;
    /** Function for choosing which unit to target. */
    public Units.Sortf unitSort = UnitSorts.closest;
    /** Filter for types of units to attack. */
    public Boolf<Unit> unitFilter = u -> true;
    /** Filter for types of buildings to attack. */
    public Boolf<Building> buildingFilter = b -> !b.block.underBullets;

    /** Optional override for all shoot effects. */
    public @Nullable Effect shootEffect;
    /** Optional override for all smoke effects. */
    public @Nullable Effect smokeEffect;
    /** Effect created when ammo is used. Not optional. */
    public Effect ammoUseEffect = Fx.none;
    /** Sound emitted when a single bullet is shot. */
    public Sound shootSound = Sounds.shoot;
    /** Sound emitted when shoot.firstShotDelay is >0 and shooting begins. */
    public Sound chargeSound = Sounds.none;
    /** Range for pitch of shoot sound. */
    public float soundPitchMin = 0.9f, soundPitchMax = 1.1f;
    /** Backwards Y offset of ammo eject effect. */
    public float ammoEjectBack = 1f;
    /** Lerp speed of turret warmup. */
    public float shootWarmupSpeed = 0.1f;
    /** If true, turret warmup is linear instead of a curve. */
    public boolean linearWarmup = false;
    /** Visual amount by which the turret recoils back per shot. */
    public float recoil = 1f;
    /** ticks taken for turret to return to starting position in ticks. uses reload time by default  */
    public float recoilTime = -1f;
    /** power curve applied to visual recoil */
    public float recoilPow = 1.8f;
    /** ticks to cool down the heat region */
    public float cooldownTime = 20f;
    /** Visual elevation of turret shadow, -1 to use defaults. */
    public float elevation = -1f;
    /** How much the screen shakes per shot. */
    public float shake = 0f;

    public ObjectMap<Block, BulletType> ammoTypes = new ObjectMap<>();

    /** Initializes accepted ammo map. Format: [item1, bullet1, item2, bullet2...] */
    public void ammo(Object... objects){
        ammoTypes = ObjectMap.of(objects);
    }

    public PayloadTurret(String name){
        super(name);

        acceptsPayload = true;
        outputsPayload = false;

        liquidCapacity = 20f;
        quickRotate = false;
        outlinedIcon = 1;

        update = true;
        solid = true;
        outlineIcon = true;
        attacks = true;
        priority = TargetPriority.turret;
        group = BlockGroup.turrets;
        flags = EnumSet.of(BlockFlag.turret);

        rotate = false;
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    @Override
    public void setStats(){
        super.setStats();

        if(coolant != null){
            stats.add(Stat.booster, StatValues.boosters(reload, coolant.amount, coolantMultiplier, true, l -> l.coolant && consumesLiquid(l)));
        }

        stats.add(Stat.inaccuracy, (int)inaccuracy, StatUnit.degrees);
        stats.add(Stat.reload, 60f / (reload) * shoot.shots, StatUnit.perSecond);
        stats.add(Stat.targetsAir, targetAir);
        stats.add(Stat.targetsGround, targetGround);
        if(heatRequirement > 0) stats.add(Stat.input, heatRequirement, StatUnit.heatUnits);

        stats.remove(Stat.itemCapacity);
        stats.add(Stat.ammo, StatValues.ammo(ammoTypes));
    }

    @Override
    public void setBars(){
        super.setBars();

        if(heatRequirement > 0){
            addBar("heat", (Turret.TurretBuild entity) ->
                    new Bar(() ->
                            Core.bundle.format("bar.heatpercent", (int)entity.heatReq, (int)(Math.min(entity.heatReq / heatRequirement, maxHeatEfficiency) * 100)),
                            () -> Pal.lightOrange,
                            () -> entity.heatReq / heatRequirement));
        }
    }

    @Override
    public void init(){
        if(shootY == Float.NEGATIVE_INFINITY) shootY = size * tilesize / 2f;
        if(elevation < 0) elevation = size / 2f;
        if(recoilTime < 0f) recoilTime = reload;
        if(cooldownTime < 0f) cooldownTime = reload;

        if(coolant == null){
            coolant = findConsumer(c -> c instanceof ConsumeCoolant);
        }

        //just makes things a little more convenient
        if(coolant != null){
            //TODO coolant fix
            coolant.update = false;
            coolant.booster = true;
            coolant.optional = true;
        }

        placeOverlapRange = Math.max(placeOverlapRange, range + placeOverlapMargin);
        fogRadius = Math.max(Mathf.round(range / tilesize), fogRadius);

        super.init();
    }

    @Override
    public void load(){
        super.load();
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[] {region, topRegion};
    }

    public class PayloadTurretBuild extends PayloadBlockBuild<BuildPayload> implements Ranged, ControlBlock {
        public Vec2 recoilOffset = new Vec2();

        public float curRecoil, heat, logicControlTime = -1;
        public float shootWarmup, charge;
        public int totalShots;
        public boolean logicShooting = false;
        public @Nullable Posc target;
        public Vec2 targetPos = new Vec2();
        public BlockUnitc unit = (BlockUnitc)UnitTypes.block.create(team);
        public boolean wasShooting;
        public int queuedBullets = 0;

        public float heatReq;
        public float[] sideHeat = new float[4];

        public float rotation = 90;

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return this.payload == null && payload instanceof BuildPayload b && ammoTypes.containsKey(b.block());
        }

        @Override
        public void drawSelect() {
            Drawf.dashCircle(x, y, range(), team.color);
        }

        @Override
        public float range(){
            return range;
        }

        @Override
        public float warmup(){
            return shootWarmup;
        }

        @Override
        public float drawrot(){
            return rotation - 90;
        }

        @Override
        public boolean shouldConsume(){
            return isShooting() || reloadCounter < reload;
        }

        @Override
        public void created(){
            unit = (BlockUnitc)UnitTypes.block.create(team);
            unit.tile(this);
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.shoot && !unit.isPlayer()){
                targetPos.set(World.unconv((float)p1), World.unconv((float)p2));
                logicControlTime = logicControlCooldown;
                logicShooting = !Mathf.zero(p3);
            }

            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public Unit unit(){
            if(unit == null){
                unit = (BlockUnitc)UnitTypes.block.create(team);
                unit.tile(this);
            }
            return (Unit)unit;
        }

        @Override
        public boolean isControlled() {
            return unit().isPlayer();
        }

        @Override
        public boolean canControl() {
            return true;
        }

        @Override
        public boolean shouldAutoTarget() {
            return true;
        }

        @Override
        public void control(LAccess type, Object p1, double p2, double p3, double p4){
            if(type == LAccess.shootp && (unit == null || !unit.isPlayer())){
                logicControlTime = logicControlCooldown;
                logicShooting = !Mathf.zero(p2);

                if(p1 instanceof Posc pos){
                    targetPosition(pos);
                }
            }

            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public float progress(){
            return Mathf.clamp(reloadCounter / reload);
        }

        public boolean isShooting(){
            return (isControlled() ? unit.isShooting() : logicControlled() ? logicShooting : target != null);
        }

        public boolean logicControlled(){
            return logicControlTime > 0;
        }

        public void targetPosition(Posc pos){
            if(!hasAmmo() || pos == null) return;
            BulletType bullet = peekAmmo();

            var offset = Tmp.v1.setZero();

            //when delay is accurate, assume unit has moved by chargeTime already
            if(accurateDelay && pos instanceof Hitboxc h){
                offset.set(h.deltaX(), h.deltaY()).scl(shoot.firstShotDelay / Time.delta);
            }

            targetPos.set(Predict.intercept(this, pos, offset.x, offset.y, bullet.speed <= 0.01f ? 99999999f : bullet.speed));

            if(targetPos.isZero()){
                targetPos.set(pos);
            }
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);

            //draw input
            for(int i = 0; i < 4; i++){
                if(blends(i)){
                    Draw.rect(inRegion, x, y, (i * 90f) - 180f);
                }
            }

            drawPayload();

            Draw.z(Layer.blockOver + 0.1f);
            Draw.rect(topRegion, x, y);
        }

        @Override
        public void updateTile(){
            if(!validateTarget()) target = null;

            float warmupTarget = isShooting() && canConsume() ? 1f : 0f;
            if(linearWarmup){
                shootWarmup = Mathf.approachDelta(shootWarmup, warmupTarget, shootWarmupSpeed * (warmupTarget > 0 ? efficiency : 1f));
            }else{
                shootWarmup = Mathf.lerpDelta(shootWarmup, warmupTarget, shootWarmupSpeed * (warmupTarget > 0 ? efficiency : 1f));
            }

            wasShooting = false;

            curRecoil = Mathf.approachDelta(curRecoil, 0, 1 / recoilTime);
            heat = Mathf.approachDelta(heat, 0, 1 / cooldownTime);
            charge = charging() ? Mathf.approachDelta(charge, 1, 1 / shoot.firstShotDelay) : 0;

            unit.tile(this);
            unit.rotation(rotation);
            unit.team(team);
            recoilOffset.trns(rotation, -Mathf.pow(curRecoil, recoilPow) * recoil);

            if(logicControlTime > 0){
                logicControlTime -= Time.delta;
            }

            if(heatRequirement > 0){
                heatReq = calculateHeat(sideHeat);
            }

            //turret always reloads regardless of whether it's targeting something
            updateReload();

            if(hasAmmo()){
                if(Float.isNaN(reloadCounter)) reloadCounter = 0;

                if(timer(timerTarget, targetInterval)){
                    findTarget();
                }

                if(validateTarget()){
                    boolean canShoot = true;

                    if(isControlled()){ //player behavior
                        targetPos.set(unit.aimX(), unit.aimY());
                        canShoot = unit.isShooting();
                    }else if(logicControlled()){ //logic behavior
                        canShoot = logicShooting;
                    }else{ //default AI behavior
                        targetPosition(target);
                    }

                    if(!isControlled()){
                        unit.aimX(targetPos.x);
                        unit.aimY(targetPos.y);
                    }

                    if(canShoot){
                        wasShooting = true;
                        updateShooting();
                    }
                }
            } else{
                moveInPayload(false);
            }

            if(coolant != null){
                updateCooling();
            }
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount){
            if(coolant != null && liquids.currentAmount() <= 0.001f){
                Events.fire(EventType.Trigger.turretCool);
            }

            super.handleLiquid(source, liquid, amount);
        }

        protected boolean validateTarget(){
            return !Units.invalidateTarget(target, canHeal() ? Team.derelict : team, x, y) || isControlled() || logicControlled();
        }

        protected boolean canHeal(){
            return targetHealing && hasAmmo() && peekAmmo().collidesTeam && peekAmmo().heals();
        }

        protected void findTarget(){
            float range = range();

            if(targetAir && !targetGround){
                target = Units.bestEnemy(team, x, y, range, e -> !e.dead() && !e.isGrounded() && unitFilter.get(e), unitSort);
            }else{
                target = Units.bestTarget(team, x, y, range, e -> !e.dead() && unitFilter.get(e) && (e.isGrounded() || targetAir) && (!e.isGrounded() || targetGround), b -> targetGround && buildingFilter.get(b), unitSort);

                if(target == null && canHeal()){
                    target = Units.findAllyTile(team, x, y, range, b -> b.damaged() && b != this);
                }
            }
        }

        @Override
        public void updateEfficiencyMultiplier(){
            if(heatRequirement > 0){
                efficiency *= Math.min(heatReq / heatRequirement, maxHeatEfficiency);
            }
        }

        /** @return the ammo type that will be returned if useAmmo is called. */
        public @Nullable BulletType peekAmmo() {
            return ammoTypes.get(payload.block());
        }

        /** @return whether the turret has ammo. */
        public boolean hasAmmo(){
            return payload != null && hasArrived();
        }

        public boolean charging(){
            return queuedBullets > 0 && shoot.firstShotDelay > 0;
        }

        protected void updateReload(){
            float multiplier = hasAmmo() ? peekAmmo().reloadMultiplier : 1f;
            reloadCounter += delta() * multiplier * baseReloadSpeed();

            //cap reload for visual reasons
            reloadCounter = Math.min(reloadCounter, reload);
        }

        protected void updateShooting(){

            if(reloadCounter >= reload && !charging() && shootWarmup >= minWarmup){
                BulletType type = peekAmmo();

                shoot(type);

                reloadCounter %= reload;
            }
        }

        protected void shoot(BulletType type){
            bullet(type);
            heat = 1f;

            if(!cheating()) payload = null;
        }

        public float reloadCounter;

        protected void updateCooling(){
            if(reloadCounter < reload && coolant != null && coolant.efficiency(this) > 0 && efficiency > 0){
                float capacity = coolant instanceof ConsumeLiquidFilter filter ? filter.getConsumed(this).heatCapacity : 1f;
                coolant.update(this);
                reloadCounter += coolant.amount * edelta() * capacity * coolantMultiplier;

                if(Mathf.chance(0.06 * coolant.amount)){
                    coolEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
                }
            }
        }

        protected float baseReloadSpeed(){
            return efficiency;
        }


        protected void bullet(BulletType type){
            float lifeScl = type.scaleLife ? Mathf.clamp(Mathf.dst(x, y, targetPos.x, targetPos.y) / type.range, type.range, range / type.range) : 1f;

            float angle = angleTo(targetPos) + Mathf.range(inaccuracy + type.inaccuracy);
            Bullet b = type.create(this, team, x, y, angle, 1f, lifeScl);

            if(type instanceof BlockBulletType t && !t.homing(b) && b.data instanceof BlockBulletType.BData data && dynamicBullet) {
                b.lifetime = Math.min(type.lifetime, TntmMath.len(targetPos.x, x, targetPos.y, y) / type.speed);
                data.targetPos = new Vec2(targetPos);
            }
        }

        @Override
        public double sense(LAccess sensor){
            return switch(sensor){
                case shootX -> World.conv(targetPos.x);
                case shootY -> World.conv(targetPos.y);
                case shooting -> isShooting() ? 1 : 0;
                case progress -> Mathf.clamp(reloadCounter / reload);
                default -> super.sense(sensor);
            };
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(reloadCounter);
            write.f(rotation);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(revision >= 1){
                reloadCounter = read.f();
                rotation = read.f();
            }
        }

        @Override
        public byte version(){
            return 1;
        }
    }
}
