//Fixed by cjy
package data.weapons;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicAnim;
import data.scripts.util.MagicRender;
import data.utils.sgb.I18nUtil;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import static com.fs.starfarer.api.util.Misc.ZERO;

public class SGB_ArcFurnace_EveryFrameEffect implements EveryFrameWeaponEffectPluginWithAdvanceAfter, OnFireEffectPlugin {
    float maxFiringAfterOverload = 30, armorWeaponAddMax = 30;
    float reloadRate = -1;
    float ammoRecorder = 0;
    float lowAmmo = -1;
    float lowAmmoTimer = 0;
    boolean noAmmo = false, fluxing = false;

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (weapon.getCooldownRemaining() > CoolDown) {
            weapon.setRemainingCooldownTo(CoolDown);
        }
        if (!noAmmo) {
            if (weapon.getAmmo() == 0) {
                noAmmo = true;
            }
        }else{
            if(weapon.getAmmo()==weapon.getMaxAmmo()){
                noAmmo = false;
            }
        }
        if (lowAmmo == -1) {
            lowAmmo = weapon.getMaxAmmo() * 0.25f; //假设弹药低于25%会触发自动装填
        }
        if (weapon.getShip().getShipAI() != null ){ //AI状态
            fluxing = weapon.getShip().getFluxTracker().isOverloaded() || weapon.getShip().getFluxTracker().isVenting();
        }
        if (weapon.getAmmo() < lowAmmo) {
            if (weapon.getShip().getShipAI() == null) { //玩家手操状态
                if (Keyboard.isKeyDown(Keyboard.KEY_R)) { //先用R，想换什么直接换就行，按下R后弹药清空
                    weapon.setAmmo(0);
                }
            } else {
                if (ammoRecorder == weapon.getAmmo()) {
                    lowAmmoTimer += amount;
                    if (lowAmmoTimer > 10) { //弹药量10秒没有变化就清空弹药
                        lowAmmoTimer = 0;
                        weapon.setAmmo(0);
                    }
                } else {
                    lowAmmoTimer = 0;
                }
            }
        }
        if (state == State.CLOSED) {
            if (reloadRate == -1 || weapon.getAmmo() > ammoRecorder) {
                reloadRate = MathUtils.getRandomNumberInRange(4f, 10f);
            }
            weapon.getAmmoTracker().setAmmoPerSecond(reloadRate);
        } else {
            weapon.getAmmoTracker().setAmmoPerSecond(0f);
            //weapon.setGlowAmount(0, null);
        }
        ammoRecorder = weapon.getAmmo();
    }

    private enum State {
        CLOSED,
        OPENING,
        OPENED,
        CLOSING
    }

    private static final float CLOSED_ANGLE_LEFT = 170; //假设左侧的武器关闭状态的朝向为左侧120度
    private static final float MOVE_DIST = 15; //假设回收的时候武器侧方移动对应Px
    private static final float MOVE_DIST_Y = 8; //假设回收的时候武器向下移动对应Px
    private static final float MOVE_DIST_Y_HARDPOINT = 12; //假设挂点武器回收的时候向下移动对应Px

    private boolean init = false;
    float facing = 0;
    float turnRate = 20; //转速 10
    int isLeft = 1; Boolean isMid = false;   //用来识别左右
    //    float spriteW = 0;
//    float spriteH = 0;
//    float barrelW = 0;
//    float barrelH = 0;
    float spriteX, spriteY;
    float barrelX, barrelY;
    float moving = 0;
    float movingRate = 1; //1秒展开动画，填2则是0.5秒
    State state = State.OPENED;
    //  Logger log = Global.getLogger(this.getClass());

    @Override
    public void advanceAfter(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine == null || engine.isPaused()) {
            return;
        }
        ShipAPI ship = weapon.getShip();
        if (!ship.isAlive()) {
            return;
        }
        //    log.info("Running");
        if (!init) {
            //     log.info("init");
            init = true;
            if(weapon.getSlot().getLocation().y == 0){
                isMid = true;
            }else{
                isLeft = weapon.getSlot().getLocation().y > 0 ? 1 : -1;  //y大于0的武器槽在武器左边,正负乘子为1，y小于0意味着在右边，乘子为-1
            }
//            spriteW = weapon.getSprite().getWidth();
//            spriteH = weapon.getSprite().getHeight();
//            barrelW = weapon.getBarrelSpriteAPI().getWidth();
//            barrelH = weapon.getBarrelSpriteAPI().getHeight();
            spriteX = weapon.getSprite().getCenterX();
            spriteY = weapon.getSprite().getCenterY();
            barrelX = weapon.getBarrelSpriteAPI().getCenterX();
            barrelY = weapon.getBarrelSpriteAPI().getCenterY();
            updateFacing(weapon, ship);
            CoolDown = weapon.getCooldown();
            turnRate = weapon.getTurnRate();
        }
        //进行 是否需要打开 的判定
        boolean shouldOpen = false;
        if (!weapon.isDisabled() && !fluxing) {
            WeaponGroupAPI group = ship.getWeaponGroupFor(weapon);
            if (group != null){
                if ( group.isAutofiring() || ship.getSelectedGroupAPI() == group) {
                    if (!noAmmo) {
                        shouldOpen = true;
                    }
                }

            }
        }
        //      log.info("ShouldOpen " + shouldOpen);
        if (shouldOpen) { //需要武器展开时
            //武器状态为完全关闭时
            if (state == State.CLOSED) {
                //     log.info("CLOSED TO OPENING");
                state = State.OPENING;
                //将武器的角度锁定在关闭角，并禁止开火
                lockWeaponAngle(weapon, ship);
                //武器处于打开中/关闭中
            } else if (state == State.CLOSING || state == State.OPENING) {
                if (state == State.CLOSING) {
                    state = State.OPENING;
                }
                //        log.info("OPENING");
                lockWeaponAngle(weapon, ship);
                if (moving > 0) {
                    moving -= amount * movingRate;
                    if (moving <= 0) {
                        moving = 0;
                        state = State.OPENED;
                        //          log.info("OPENING TO OPENED");
                    }
                }
                //调整武器图片偏移
                moveSprite(weapon);
                //完全打开后
            } else if (state == State.OPENED) {
                weapon.setSuspendAutomaticTurning(false);
                //         log.info("OPENED");
            }
        } else {
            //如果处于完全打开的状态
            if (state == State.OPENED) {
                //        log.info("OPENED but try to close");
                if (turnWeaponAngle(weapon, ship, amount)) {
                    state = State.CLOSING;
                    //         log.info("OPENED TO CLOSING");
                }
                //武器处于打开中/关闭中
            } else if (state == State.CLOSING || state == State.OPENING) {
                if (state == State.OPENING) {
                    state = State.CLOSING;
                }
                //        log.info("CLOSING");
                lockWeaponAngle(weapon, ship);
                if (moving <= 1) {
                    moving += amount * movingRate;
                    if (moving >= 1) {
                        moving = 1;
                        state = State.CLOSED;
                        //            log.info("CLOSING TO CLOSED");
                    }
                }
                //调整武器图片偏移
                moveSprite(weapon);
            }
            if (state == State.CLOSED) {
                //      log.info("CLOSED");
                //重置冷却
                CoolDown = weapon.getCooldown();
                overloadCounter = 0;
                lockWeaponAngle(weapon, ship);
            }
        }
        updateFacing(weapon, ship);
    }

    void moveSprite(WeaponAPI weapon) {
        SpriteAPI sprite = weapon.getSprite();
        SpriteAPI barrel = weapon.getBarrelSpriteAPI();
        //我忘记了X和Y应该调哪个，如果移动方向不对的话换一下xy,wh或者正负值
        //     sprite.setCenterX(moving * 0.5f * isLeft * spriteH);
        //    barrel.setCenterX(moving * 0.5f * isLeft * barrelH);
        float SlideMovX = MagicAnim.smoothNormalizeRange(moving, 0f, 1f);
        float SlideMovY = MagicAnim.smoothNormalizeRange(moving, 0f, 0.67f);

        if(weapon.getSlot().isHardpoint() || isMid) {
            sprite.setCenterY(-SlideMovY * MOVE_DIST_Y_HARDPOINT + spriteY);
            barrel.setCenterY(-SlideMovY * MOVE_DIST_Y_HARDPOINT + barrelX);// 这个barrelY是正常的

        } else {
            sprite.setCenterX(isLeft * SlideMovX * MOVE_DIST + spriteX);
            barrel.setCenterX(isLeft * SlideMovX * MOVE_DIST + barrelX); //这个barrelX是故意的，是为了效果

            sprite.setCenterY(-SlideMovY * MOVE_DIST_Y + spriteY);
            barrel.setCenterY(-SlideMovY * MOVE_DIST_Y + barrelX);
        }
    }

    /**
     * 让武器槽转向关闭角的方法
     *
     * @param weapon
     * @param ship
     */
    boolean turnWeaponAngle(WeaponAPI weapon, ShipAPI ship, float amount) {
        weapon.setSuspendAutomaticTurning(true);
        float t = turnRate;
        float targetAngle = ship.getFacing();
        if(!weapon.getSlot().isHardpoint() && !isMid) {
            targetAngle = CLOSED_ANGLE_LEFT * isLeft + ship.getFacing();
        }
        float rc = 999;
        float slotCenter = weapon.getSlot().getAngle() + ship.getFacing();
//        {
//            float a = weapon.getCurrAngle() - slotCenter;
//            float b = targetAngle - slotCenter;
//            float c = weapon.getCurrAngle() - targetAngle;
//            a = normalizeAngle(a);
//            b = normalizeAngle(b);
//            if (a * b >= 0 && a * c < 0 || Math.abs(a) <= turnRate * 2 * amount) {
//                t *= 2;
//            }
//        }
        if (weapon.getSlot().getArc() == 360) { //360度的武器槽往哪边转都可以，武器与目标的夹角已经标准化过，绝对值小于180，可以直接用就近原则进行偏转
            float a = normalizeAngle(weapon.getCurrAngle() - targetAngle);
            if (a >= 0) {
                a = Math.max(0, a - t * amount);
            } else {
                a = Math.min(0, a + t * amount);
            }
            rc = Math.abs(a);
            a += targetAngle;
            weapon.setCurrAngle(a);
        } else {
            float a = normalizeAngle(slotCenter - weapon.getCurrAngle());
            float b = normalizeAngle(slotCenter - targetAngle);
            //a * b <0 情况下，意味着使用就近原则转向会使武器转向武器槽不可用的方向
            // 需要对着武器槽中心先进行就近原则转向，直到武器朝向/武器槽中心朝向，和目标角位于直线的同一边
            //武器槽中心与武器的夹角不可能到达180度，因为已经排除了武器槽360度转向的可能
            //同样的，武器转到武器槽中心的时候，目标角度不可能还有超过180度的剩余可用角度，因此，对着武器槽中心角进行一段时间的就近转向后
            //就能满足目标角/武器角，武器槽中心在180度内了，也就是a*b>0，可以对着目标角度直接使用就近夹角转向
            if (a * b < 0 && Math.abs(a) + Math.abs(b) > 180) {
                a = normalizeAngle(weapon.getCurrAngle() - slotCenter);
                if (a >= 0) {
                    a = a - t * amount;
                } else {
                    a = a + t * amount;
                }
                a += slotCenter;
            } else {
//                if (a * b > 0) {
//                    float c = normalizeAngle(targetAngle-weapon.getCurrAngle())  ;
//                    if (a * c < 0) {
//                        t *= 2;
//                    }
//                }
                a = normalizeAngle(weapon.getCurrAngle() - targetAngle);
                if (a >= 0) {
                    a = Math.max(0, a - t * amount);
                } else {
                    a = Math.min(0, a + t * amount);
                }
                rc = Math.abs(a);
                a += targetAngle;
            }
            weapon.setCurrAngle(a);
            //     weapon.setCurrAngle(facing + ship.getFacing()); //锁死武器角
        }
        weapon.setForceNoFireOneFrame(true);
        //       log.info("turning  + remaining " + rc);
        //       log.info("current/target" + weapon.getCurrAngle() + " " + targetAngle);
        return rc <= t * amount * 2;
    }

    void lockWeaponAngle(WeaponAPI weapon, ShipAPI ship) {
        if(weapon.getSlot().isHardpoint() || isMid) {
            weapon.setSuspendAutomaticTurning(true);
            facing = CLOSED_ANGLE_LEFT;
            weapon.setCurrAngle(ship.getFacing()); //锁死武器角
            weapon.setForceNoFireOneFrame(true);
        }else {
            weapon.setSuspendAutomaticTurning(true);
            facing = CLOSED_ANGLE_LEFT * isLeft;
            weapon.setCurrAngle(facing + ship.getFacing()); //锁死武器角
            weapon.setForceNoFireOneFrame(true);
        }
    }

    void updateFacing(WeaponAPI weapon, ShipAPI ship) {
        facing = normalizeAngle(weapon.getCurrAngle() - ship.getFacing());
    }

    private float normalizeAngle(float angle) {
        while (angle > 180f) {
            angle -= 360f;
        }
        while (angle < -180f) {
            angle += 360f;
        }
        return angle;
    }

    float CoolDown = 1f;
    float decreaseCoolDownPerShot = 0.175f; //每发射弹降低射击间隔
    float minCoolDown = 0.01f; //预设最小射击间隔
    int overloadCounter = 0;
    boolean fireLight = false;

    /**
     * 弹丸发射的时候增加射速，以及过载时损坏武器
     *
     * @param projectile
     * @param weapon
     * @param engine
     */
    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        fireLight = true;
        CoolDown = Math.max(minCoolDown, CoolDown - decreaseCoolDownPerShot);
        float smoothCoolDown = MagicAnim.smoothToRange(CoolDown, 0f, weapon.getCooldown(), 0f, 1f);
        Color finalLight = colorBlend(new Color(245, 210, 113),
                new Color(255, 0, 0), smoothCoolDown);
        if (CoolDown == minCoolDown) {
            overloadCounter++;
            float overloadMaxAmmo = maxFiringAfterOverload;
            if(weapon.getShip().getVariant().hasHullMod("armoredweapons")){
                overloadMaxAmmo = maxFiringAfterOverload + maxFiringAfterOverload;
            }
            if (overloadCounter > overloadMaxAmmo) {
                weapon.disable();
                float damage = 100;
                float emp = 100;
                engine.applyDamage(weapon.getShip(), weapon.getLocation(), damage, DamageType.ENERGY, emp, true, false, weapon.getShip());

                MagicRender.battlespace(
                        Global.getSettings().getSprite("fx", "SGB_smoke_01"),
                        weapon.getLocation(),
                        weapon.getShip().getVelocity(),
                        new Vector2f(128, 128),
                        new Vector2f(-120, -120),
                        //angle,
                        360 * (float) Math.random(),
                        0,
                        Color.white,
                        true,
                        0.1f,
                        1f,
                        0.25f
                );
                MagicRender.battlespace(
                        Global.getSettings().getSprite("fx", "SGB_smoke_2"),
                        weapon.getLocation(),
                        weapon.getShip().getVelocity(),
                        new Vector2f(128, 128),
                        new Vector2f(200, 200),
                        //angle,
                        360 * (float) Math.random(),
                        0,
                        Color.white,
                        true,
                        0.1f,
                        0.5f,
                        0.25f
                );
                MagicRender.battlespace(
                        Global.getSettings().getSprite("fx", "SGB_Va_Explosion"),
                        weapon.getLocation(),
                        weapon.getShip().getVelocity(),
                        new Vector2f(10f, 10f),
                        new Vector2f(800f, 800f),
                        //angle,
                        360 * (float) Math.random(),
                        0,
                        new Color(243, 162, 162, 255),
                        true,
                        0.03f,
                        0.05f,
                        0.02f
                );
            }
        }
        //__SmallDec__
        Vector2f muzzle = projectile.getLocation();
        I18nUtil.easyWaving(muzzle, projectile.getVelocity(), 50, 3, 10);
        for (DamagingProjectileAPI p : CombatUtils.getProjectilesWithinRange(projectile.getLocation(), 150)) {
            if (p.getWeapon() != weapon) continue;
            engine.addHitParticle(
                    muzzle,
                    projectile.getVelocity(),
                    100,
                    0.25f,
                    0.5f,
                    finalLight
            );
        }
        if (fireLight) {
            float smoothCoolDown2 = smoothCoolDown;
            smoothCoolDown2--;
            weapon.setGlowAmount(smoothCoolDown2, finalLight);
            if (smoothCoolDown2 <= 0) {
                fireLight = false;
            }
        }
        MagicRender.battlespace(
                Global.getSettings().getSprite("fx", "SGB_smoke_2"),
                projectile.getLocation(),
                projectile.getVelocity(),
                new Vector2f(128, 128),
                new Vector2f(50, 50),
                //angle,
                360 * (float) Math.random(),
                0,
                new Color(70, 70, 70, 70),
                true,
                0.1f,
                0.1f,
                1.3f
        );
        //__Ammo_Out
        float vectorAS = 1;
        if (!weapon.getSlot().isHardpoint() && !isMid) {
            vectorAS = isLeft;
        }
        Vector2f EmptyAmmoVector = new Vector2f(MathUtils.getRandomNumberInRange(-30,-10), vectorAS * 100f );
        if(weapon.getSlot().isHardpoint() || isMid){
            EmptyAmmoVector = new Vector2f(MathUtils.getRandomNumberInRange(-30,-10), MathUtils.getRandomNumberInRange(-120f,120f));
        }
        Vector2f ammoVector = MathUtils.getRandomPointInCircle(VectorUtils.rotate(EmptyAmmoVector,weapon.getCurrAngle()),10);

        Vector2f EmptySmokeVector = new Vector2f(MathUtils.getRandomNumberInRange(-7,7), vectorAS * 30f );
        Vector2f smokeVector = MathUtils.getRandomPointInCircle(VectorUtils.rotate(EmptySmokeVector,weapon.getCurrAngle()),4f);

        Vector2f weaponAddVector = new Vector2f(0,vectorAS * 8f);
        Vector2f USE_weaponVector = VectorUtils.rotate(weaponAddVector,weapon.getCurrAngle());
        Vector2f ammoPositionVector = new Vector2f(weapon.getLocation().x + USE_weaponVector.x,
                weapon.getLocation().y + USE_weaponVector.y);
        float jitterRange = 3;
        jitterRange--;
        MagicRender.battlespace(
                Global.getSettings().getSprite("fx", "SGB_Ammo"),
                ammoPositionVector,
                ammoVector,
                new Vector2f(7, 7),
                new Vector2f(-1, -1),
                //angle,
                360 * (float) Math.random(),
                0,
                new Color(255, 233, 190, 252),
                true,
                0.2f,
                0.7f,
                0.3f
        );
        Color finalSmoke = new Color(finalLight.getRed()/255f/1.5f,finalLight.getGreen()/255f/1.8f,finalLight.getBlue()/255f/2f,50f /255f);
        MagicRender.battlespace(
                Global.getSettings().getSprite("fx", "SGB_smoke_1"),
                ammoPositionVector,
                smokeVector,
                new Vector2f(55, 55),
                new Vector2f(60, 60),
                //angle,
                360 * (float) Math.random(),
                0,
                finalSmoke,
                false,
                0.1f,
                0.5f,
                1f
        );
        //Sound
        Global.getSoundPlayer().playSound(
                "SGB_Only_MG",
                (float) (0.7+CoolDown), 1f,
                weapon.getLocation(), ZERO);

    }

    //_____________
    static boolean runOnce = false, firing = false, fired = false, midStoped = false;
    public final String ArcL_ID = "WS0004", ArcR_ID = "WS0005";
    private WeaponAPI ArcL, ArcR;
    protected IntervalUtil AnmiBack_Interval = new IntervalUtil(1.25f, 1.25f);
    float Arc_Height_L, Arc_Width_L;
    float Arc_Height_R, Arc_Width_R;
    //_____________

    public void advanceOld(float amount, CombatEngineAPI engine, WeaponAPI weapon) { //	进行一个特效
        if (engine.isPaused()) return;
        if (weapon == null) return;

        ShipAPI ship = weapon.getShip();
        if (ship == null) return;
        // Null判定-上 —————— 实际特效-下
        if (!runOnce) {
            runOnce = true;
            if (weapon.getSlot().getId().equals(ArcL_ID)) {
                ArcL = weapon;
                Arc_Width_L = weapon.getSprite().getWidth();
                Arc_Height_L = weapon.getSprite().getHeight();
            }
            if (weapon.getSlot().getId().equals(ArcR_ID)) {
                ArcR = weapon;
                Arc_Width_R = weapon.getSprite().getWidth();
                Arc_Height_R = weapon.getSprite().getHeight();
            }
            return;
        }
        //_________IMPORTANT_Before_________
        Vector2f muzzle, muzzleSpeed;
        muzzle = MathUtils.getPoint(
                weapon.getLocation(),
                6, //	特效所需的相对武器核心位置
                weapon.getCurrAngle()
        );
        muzzleSpeed = MathUtils.getPoint(
                ZERO,
                10, //	特效所需的相对武器核心位置
                weapon.getCurrAngle()
        );
        //______Locations________
        //if(firing && weapon.getChargeLevel()<1){
        if (weapon.getChargeLevel() < 1) {
            //midStoped = true;
            //Add Fire Light & ammo out
            I18nUtil.easyWaving(muzzle, muzzleSpeed, 50, 3, 10);
            engine.addHitParticle(
                    muzzle,
                    weapon.getShip().getVelocity(),
                    10,
                    0.5f,
                    1,
                    new Color(239, 189, 139)
            );
            // 单独使用计时器的回收动画情况下[利用cargLv[CG]来实现补正效果]
            AnmiBack_Interval.advance(amount);
            float anmi_dura = AnmiBack_Interval.getIntervalDuration();
            float cargLv = weapon.getChargeLevel();
            float speedW = MagicAnim.smoothNormalizeRange(anmi_dura, 0f, 1f);
            float speedH = MagicAnim.smoothNormalizeRange(anmi_dura, 0f, 0.67f);

            float CurrLW = Arc_Width_L / 2 + speedW * 20f;
            float CurrRW = Arc_Width_R / 2 - speedW * 20f;
            float CurrLH = Arc_Height_L / 2 + speedH * 8f;
            float CurrRH = Arc_Height_R / 2 + speedH * 8f;
            ArcL.getSprite().setCenter(CurrLW, CurrLH);
            ArcR.getSprite().setCenter(CurrRW, CurrRH);
            //Anmi float
            if (!AnmiBack_Interval.intervalElapsed()) {    // Anmi had not been set in to ZERO
            }
        } else if (weapon.getChargeLevel() == 1) {
            fired = true;
            if (weapon.getId().equals(ArcL_ID)) {    //确认左右-左
                if ((weapon.getSprite().getWidth() != Arc_Width_L) ||    //确认武器动画到位/即不到位不许开火
                        (weapon.getSprite().getHeight() != Arc_Width_L)) {
                    SGB_differentSideWeapon_FireSpec(weapon, muzzle, muzzleSpeed);
                }
            }
            if (weapon.getId().equals(ArcR_ID)) {    //确认左右-右
                if ((weapon.getSprite().getWidth() != Arc_Width_R) ||    //确认武器动画到位/即不到位不许开火
                        (weapon.getSprite().getHeight() != Arc_Width_R)) {
                    SGB_differentSideWeapon_FireSpec(weapon, muzzle, muzzleSpeed);
                }
            }
        } else if (weapon.getChargeLevel() == 0) {
            firing = false;
            midStoped = false;
        }
        //	N.O.T.E
        //	开火后进入正常状态，当中止开火时，分为两个判定组，
        //	·一个组利用计时器模拟回收（不和ChargeDown等绑定）
        //	·另一个组使用新的ChargeUp进行判定
        //	从上述两组中取最大数值来进行特效操作
        //	综合最大值未归位时加快CG_UP的速度，武器不恢复弹药[归位时恢复好像更好做]
        //

        //	-=思维笔记=-
        //	目标：尽量符合观感[酷]，且具备一定误差规避效果
        //	已知中止开火时Charge会逐渐(后续简称CG)归零，即停止开火时会进行CG_DOWN的计时，并在继续开火时再跑一遍CG_UP
        //	所以应让CG_DOWN默认为0[我甚至想让他是负数]，CG_UP为正常动画时间数值，组二使用同正常动画时间数值的计时器

        //	Charge=0只代表武器间歇性停火，两组计时器的综合最大值才决定炮管实际动画位置
        //	所以当动画位置未彻底归零[收归舱内]时，应加快CG_UP的速度 - 或者说减小数值来保证武器有一定程度上的误差规避效果

        //	综合最大值未归零时武器不会恢复弹药，从而模拟必须收回才能装弹的效果
        //_____
        //	上述做完后再考虑烧烂枪管的事-

        if (fired) {

        }
    }

    public void SGB_differentSideWeapon_FireSpec(WeaponAPI weapon, Vector2f muzzle, Vector2f muzzleSpeed) {
        weapon.setForceNoFireOneFrame(true);
        firing = true;
        midStoped = false;
        I18nUtil.easyWaving(muzzle, muzzleSpeed, 28, 3, 8);
    }

    public static Color colorBlend(Color a, Color b, float amount) {
        float conjAmount = 1f - amount;
        return new Color((int) Math.max(0, Math.min(255, a.getRed() * conjAmount + b.getRed() * amount)), (int) Math.max(0, Math.min(255, a.getGreen() * conjAmount + b.getGreen() * amount)), (int) Math.max(0, Math.min(255, a.getBlue() * conjAmount + b.getBlue() * amount)), (int) Math.max(0, Math.min(255, a.getAlpha() * conjAmount + b.getAlpha() * amount)));
    }
}