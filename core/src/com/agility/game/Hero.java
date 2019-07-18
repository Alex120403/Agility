package com.agility.game;

import com.agility.game.UI.ItemInfo;
import com.agility.game.UI.OnHitDamageView;
import com.agility.game.UI.UI;
import com.agility.game.Utils.AnimationWithOffset;
import com.agility.game.Utils.GameBalanceConstants;
import com.agility.game.Utils.LockedCamera;
import com.agility.game.Utils.SimpleDirectionGestureDetector;
import com.agility.game.Utils.SpritePack;
import com.agility.game.WorldObjects.Item;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.management.ObjectName;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class Hero extends Actor {

    public static Sprite blood;
    private Sprite currentFrame;
    private static Sprite expBar, expBarBackground;

    private Body body;
    public Body swordSwipe;
    private final World world;
    private static double level = 1;
    private static Vector2 position,velocity;
    private int direction = 1, wallTouchDirection;

    public int damaged;
    private String currentAnimation = "idle";
    private float stateTime = 0, maxHealth = GameBalanceConstants.DEFAULT_HERO_MAX_HEALTH,health = maxHealth, afterRollingSlowlinessTimer;
    private int touchings = 0, avaliableJumps = 2, attackOrder, rollingTimer, rollDirection;
    private boolean onGround = true, isAttacking, hasWeapon, isSwiped, isDied, isRolling;
    private Game game;
    private transient ItemInfo itemInfoInEdge;
    private Inventory inventory;
    private final static Color colorDamage = new Color(1,0.5f,0.5f,1);


    // Exp bar

    private static BitmapFont expText;
    private static float expBarOpacity;
    private static boolean drawExpBar;


    private static final HashMap<String,AnimationWithOffset> animations = new HashMap<String, AnimationWithOffset>();
    private boolean anotherOneAttack;
    private boolean stopped;
    private Item weapon, armor;
    private Sound[] swipes = new Sound[6];
    private Sound[] jumps = new Sound[2];
    private Sound onGroundStep,rollSound;
    ArrayList<Enemy> enemies;
    private static final Random random = new Random();
    private static int expDisplay,expTarget;

    //private Music runSound;

    public Hero(Vector2 position, World world, final Game game) {
        this.position = position;
        this.world = world;
        this.game = game;
        isDied = false;
        blood = new Sprite(new Texture(Gdx.files.internal("blood.png")));
        blood.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        swipes[0] = Gdx.audio.newSound(Gdx.files.internal("sounds/swipe0.ogg"));
        swipes[1] = Gdx.audio.newSound(Gdx.files.internal("sounds/swipe1.ogg"));
        swipes[2] = Gdx.audio.newSound(Gdx.files.internal("sounds/swipe2.ogg"));
        swipes[3] = Gdx.audio.newSound(Gdx.files.internal("sounds/swipe0.ogg"));
        swipes[4] = Gdx.audio.newSound(Gdx.files.internal("sounds/swipe1.ogg"));
        swipes[5] = Gdx.audio.newSound(Gdx.files.internal("sounds/swipe2.ogg"));

        jumps[0] = Gdx.audio.newSound(Gdx.files.internal("sounds/jump0.ogg"));
        jumps[1] = Gdx.audio.newSound(Gdx.files.internal("sounds/jump1.ogg"));

        onGroundStep = Gdx.audio.newSound(Gdx.files.internal("sounds/onGround0.ogg"));

        rollSound = Gdx.audio.newSound(Gdx.files.internal("sounds/roll0.ogg"));

        //runSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/walk.ogg"));

        init("animations");
        init("default equipment");
        init("exp bar");
        inventory = new Inventory();


        Gdx.input.setInputProcessor(new SimpleDirectionGestureDetector(new SimpleDirectionGestureDetector.DirectionListener() {

            @Override
            public void onUp() {
                if(avaliableJumps>0 && !isDied) {
                    avaliableJumps-=1;
                    body.setLinearVelocity(body.getLinearVelocity().x,0);
                    body.applyLinearImpulse(new Vector2(0, 15000), new Vector2(0, 0), true);
                    jumps[avaliableJumps].play();
                    isAttacking = false;
                    stabilizeSpeed();
                }

                stopped = false;
            }

            @Override
            public void onRight() {
                if(!isDied) {
                    if (wallTouchDirection == -1 && !onGround && avaliableJumps != 2) {
                        body.setLinearVelocity(0, 0);
                        jumps[1].play();
                        body.applyLinearImpulse(new Vector2(-999999999, 999999999), new Vector2(0, 0), true);
                        wallTouchDirection = 0;
                    }

                    direction = 1;
                    stopped = false;
                }
            }

            @Override
            public void onLeft() {
                if(!isDied) {
                    if (wallTouchDirection == 1 && !onGround && avaliableJumps != 2) {
                        body.setLinearVelocity(0, 0);
                        jumps[1].play();
                        body.applyLinearImpulse(new Vector2(999999999, 999999999), new Vector2(0, 0), true);
                        wallTouchDirection = 0;
                    }

                    direction = -1;
                    stopped = false;
                }
            }

            @Override
            public void onDown() {
                if(!onGround) {
                    body.setLinearVelocity(0, -1999999999);
                    setAnimation("fall");
                }

            }

            @Override
            public void onTouch() {
                if(!isDied) {
                    if (!isAttacking) {
                        body.setLinearVelocity(0, body.getLinearVelocity().y);
                        if (hasWeapon) {

                            swipes[random.nextInt(3)].play(1f);
                            setAnimation("attack" + ((++attackOrder % 3) + 1));
                        } else {
                            setAnimation("cast");
                        }
                        //direction = 0;
                        isAttacking = true;
                    } else {
                        anotherOneAttack = true;
                    }
                }

            }

            @Override
            public void onRightDown() {
                if(!isDied)
                roll(1);
            }

            @Override
            public void onLeftDown() {
                if(!isDied)
                roll(-1);
            }
        },game));

    }

    public static void frag() {
        int exp = 0;
        do {
            exp = (int)(( 0.11-(random.nextDouble()/(100-level)) ) * 1000)/2;
        } while (exp <= 30);
        int oldLevel = (int)level;
        level += exp/1000f;
        if ((int)level > oldLevel) {
            Game.log("Level up! Level: " + (int)level);
        }
        expDisplay = 0;
        expTarget = exp;
        drawExpBar();
    }

    public void stop() {
        body.setLinearVelocity(0,0);
        if (hasWeapon) {
            setAnimation("idle-sword");
        } else {
            setAnimation("idle");
        }
        stopped = true;
    }

    private void endAttack() {
        isSwiped = false;
        isAttacking = false;
        if (hasWeapon) {
            setAnimation("idle-sword");
        } else {
            setAnimation("idle");
        }

    }

    public void equip(Item item) {
        equip(item, false);
    }

    public void equip(Item item, boolean silent) {
        if(item.getType() == ItemInfo.TYPE_WEAPON) {
            weapon = item;
            hasWeapon = true;
            if(Game.getUi() != null && !silent) {
                Game.log("Equipped: " + weapon.getName() + ", " + weapon.getParameter1() + " damage");
            }
        }
    }

    public void roll(int rollDirection) {
        if(rollDirection == 1) {
            setAnimation("roll");
        }
        else {
            setAnimation("back-roll");
        }
        enemies = new ArrayList<Enemy>();
        for (int i = 0; i < game.getStage().getActors().size; i++) {
            Actor a = game.getStage().getActors().get(i);
            if(a.getName() != null && a.getName().equals("enemy")) {
                enemies.add((Enemy)game.getStage().getActors().get(i));
            }
        }
        for (Enemy e:enemies) {
            e.getBody().setActive(false);
        }
        rollSound.play();
        isRolling = true;
        rollingTimer = 24;
        this.rollDirection = rollDirection;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(Gdx.input.isKeyPressed(Input.Keys.T)) {
            maxHealth++;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.Y)) {
            health++;
        }
        if (swordSwipe != null) {
            world.destroyBody(swordSwipe);
            swordSwipe = null;
        }
        checkForAnotherOneAttack();
        checkForDeath();
        checkForRoll();
        //printState();
        position = body.getPosition();
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = animations.get(currentAnimation).animation.getKeyFrame(stateTime, !isAttacking && !isDied);
        if(isAttacking && stateTime >= 0.1f && !isSwiped) {
            swipe();
        }
        if(isAttacking && animations.get(currentAnimation).animation.isAnimationFinished(stateTime)){
            endAttack();
        }
        currentFrame.setPosition(body.getPosition().x-9-direction*animations.get(currentAnimation).xOffset,body.getPosition().y-0.5f+animations.get(currentAnimation).yOffset);
        currentFrame.setSize(50f/2f,37f/2f);
        currentFrame.setFlip(direction == -1,false);
        currentFrame.draw(batch,parentAlpha);
        if(damaged > 0) {
            damaged--;
        }

        if(!stopped && !isRolling && !isDied) {
            stabilizeSpeed();
        }
        if(drawExpBar) {

            expBar.setPosition(currentFrame.getX()+6.5f + 1.5f*direction ,currentFrame.getY() + 18);
            expBarBackground.setPosition(expBar.getX(),expBar.getY());
            expBarBackground.setSize(expBarBackground.getWidth(),0.5f);

            expBarBackground.draw(batch,expBarOpacity);
            expBar.setSize(expBarBackground.getWidth() * (float)((level - (int)level)),expBarBackground.getHeight());
            expBar.draw(batch,expBarOpacity);
            expBarOpacity-=(1-expBarOpacity)/25;
            if(expBarOpacity <= 0.01f) {
                expBarOpacity = 0.99f;
                drawExpBar = false;
            }
            if(expTarget > expDisplay) {
                expDisplay += (expTarget-expDisplay)/10;
                UI.drawTextMessage = "Exp +" + expDisplay;
            }

            float cx = game.getStage().getCamera().position.x - game.getStage().getCamera().viewportWidth/2;
            float cy = game.getStage().getCamera().position.y - game.getStage().getCamera().viewportHeight/2;
            float expDrawTextX = (expBar.getX() - cx) * 7.5f;
            float expDrawTextY = (expBar.getY() + 4 - cy) * 7.5f;
            if(!Game.getUi().drawText) {
                Game.getUi().drawText("Exp +" + expDisplay, expDrawTextX, expDrawTextY);
            }
            else {
                Game.getUi().drawTextX = expDrawTextX;
                Game.getUi().drawTextY = expDrawTextY;
            }
        }
    }

    private void checkForRoll() {
        if(rollingTimer > 0 && onGround) {
            rollingTimer--;
            body.setLinearVelocity(120*rollDirection,0);
        }
        else if(isRolling){
            afterRollingSlowlinessTimer = 10;
            stabilizeSpeed();
            isRolling = false;
            for (Enemy e:enemies) {
                e.getBody().setActive(true);
            }
            rollDirection = 0;
        }
    }

    private void checkForDeath() {
        if(health<=0) {
            setAnimation("die");
            isDied = true;
        }
    }

    public boolean isDied() {
        return isDied;
    }

    private void swipe() {
        isSwiped = true;
        BodyDef def = new BodyDef();
        def.gravityScale = 0;
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.x = position.x+3+7*direction;
        def.position.y = position.y+7;

        swordSwipe = world.createBody(def);
        swordSwipe.setFixedRotation(true);
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(6,1);
        fixtureDef.shape = shape;
        fixtureDef.density = 1;
        fixtureDef.friction = 0f;

        swordSwipe.createFixture(fixtureDef);
        swordSwipe.setUserData("weaponSwipe");

    }

    private void printState() {
        System.out.println("X speed: "+body.getLinearVelocity().x+" | Y:"+body.getLinearVelocity().y+" Hero: On Ground: "+onGround+" | Wall touch: "+wallTouchDirection+" | Jumps: "+avaliableJumps);
    }

    private void stabilizeSpeed() {
        if(isAttacking || afterRollingSlowlinessTimer > 0) {
            afterRollingSlowlinessTimer--;
            switch (direction) {
                case(-1):
                    body.setLinearVelocity(-20,body.getLinearVelocity().y);
                    break;
                case (1):
                    body.setLinearVelocity(20,body.getLinearVelocity().y);
                    break;

            }
        }
        else {
            switch (direction) {
                case(-1):
                    body.setLinearVelocity(-95,body.getLinearVelocity().y);
                    break;
                case (1):
                    body.setLinearVelocity(95,body.getLinearVelocity().y);
                    break;

            }
        }

    }

    private void checkForAnotherOneAttack() {
        if(!isAttacking && anotherOneAttack) {
            anotherOneAttack = false;
            isAttacking = true;
            //  direction = 0;
            if(hasWeapon) {
                swipes[random.nextInt(3)].play(1f);
                setAnimation("attack" + ((++attackOrder % 3) + 1));
            }
            else {
                setAnimation("cast");
            }
            body.setLinearVelocity(0, body.getLinearVelocity().y);
        }
    }


    @Override
    public void act(float delta) {

    }

    public static Vector2 getPosition() {
        return position;
    }

    public void init(String request) {
        if(request.equalsIgnoreCase("body")) {
            BodyDef def = new BodyDef();
            def.gravityScale = 70;
            def.type = BodyDef.BodyType.DynamicBody;
            def.position.x = position.x;
            def.position.y = position.y;
            def.bullet = true;

            body = world.createBody(def);
            body.setFixedRotation(true);
            FixtureDef fixtureDef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            Vector2[] points = {
                    /* OLD (Need for presentation)
                    new Vector2(0,(float)Math.sqrt(12)*4),
                    new Vector2(0,1),
                    new Vector2(1,0),
                    new Vector2((float)Math.sqrt(3)*4-1,0),
                    new Vector2((float)Math.sqrt(3)*4,1),
                    new Vector2((float)Math.sqrt(3)*4,(float)Math.sqrt(12)*4)};
                     */
                    new Vector2(0,(float)Math.sqrt(12)*4),
                    new Vector2(0,0.2f),
                    new Vector2(0.2f,0),
                    new Vector2((float)Math.sqrt(3)*4-0.2f,0),
                    new Vector2((float)Math.sqrt(3)*4,0.2f),
                    new Vector2((float)Math.sqrt(3)*4,(float)Math.sqrt(12)*4)};
            shape.set(points);
            fixtureDef.shape = shape;
            fixtureDef.density = 1;
            fixtureDef.friction = 0f;

            body.createFixture(fixtureDef);
            body.setUserData("player");

        }
        else if(request.equalsIgnoreCase("animations")) {
            animations.put("run",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("hero/run",6).content),2,0, -8));
            animations.put("idle",new AnimationWithOffset(new Animation<Sprite>(0.4f,new SpritePack("hero/idle",4).content),2,0, -8));
            animations.put("jump",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("hero/crnr-jmp",2).content),2,0, -8));
            animations.put("roll",new AnimationWithOffset(new Animation<Sprite>(0.09f,new SpritePack("hero/smrslt",4).content),0,-3, -8));
            animations.put("back-roll",new AnimationWithOffset(new Animation<Sprite>(0.09f,new SpritePack("hero/back-smrslt",4).content),0,-3, -8));
            animations.put("die",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("hero/die",7).content),2,0, -8));
            animations.put("attack1",new AnimationWithOffset(new Animation<Sprite>(0.05f,new SpritePack("hero/attack1",5).content),2,0, -8));
            animations.put("attack2",new AnimationWithOffset(new Animation<Sprite>(0.05f,new SpritePack("hero/attack2",6).content),2,0, -8));
            animations.put("attack3",new AnimationWithOffset(new Animation<Sprite>(0.05f,new SpritePack("hero/attack3",6).content),2,0, -8));
            animations.put("getsword",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("hero/swrd-drw",4).content),2,0, -8));
            animations.put("removesword",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("hero/swrd-shte",4).content),2,0, -8));
            animations.put("idle-sword",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("hero/idle-2",4).content),2,0, -8));
            animations.put("cast",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("hero/cast",4).content),2,0, -8));
            animations.put("fall",new AnimationWithOffset(new Animation<Sprite>(0.1f,new SpritePack("hero/fall",2).content),2,0, -8));
            animations.put("wall-slide",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("hero/wall-slide",2).content),0,0, -8));
            animations.put("hurt",new AnimationWithOffset(new Animation<Sprite>(0.1f,new SpritePack("hero/hurt",3).content),0,0, -8));




        }

        else if(request.equals("default equipment")) {
            if(weapon == null) {
                ItemInfo info = new ItemInfo(ItemInfo.TYPE_WEAPON, "Fists", 60, 2, 1);
                weapon = new Item(game, null, info);
                equip(weapon);
                info.setItem(weapon);
                hasWeapon = false;
            }
            else {
                hasWeapon = true;
            }


        }

        else if(request.equals("exp bar")) {
            Pixmap expPixmap = new Pixmap(12,1,Pixmap.Format.RGBA8888);
            expPixmap.setColor(1,176f/255f,46f/255f,1);
            expPixmap.fill();
            expBar = new Sprite(new Texture(expPixmap));

            Pixmap expPixmapBackground = new Pixmap(12,1,Pixmap.Format.RGBA8888);
            expPixmapBackground.setColor(0,0,0,1);
            expPixmapBackground.fill();
            expBarBackground = new Sprite(new Texture(expPixmapBackground));

            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("basis33.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 4;
            parameter.color = Color.WHITE;
            expText = generator.generateFont(parameter);
        }

    }

    private static void drawExpBar() {
        drawExpBar = true;
        expBarOpacity = 0.99f;
        Game.getUi().drawText = false;
    }

    public void damage(float deal) {
        this.health -= deal;
        damaged = 20;
        body.applyLinearImpulse(new Vector2(direction*-2999, 0), new Vector2(0, 0), true);
        setAnimation("hurt");
    }

    public void addItem(Item item) {
        if(inventory.add(item)) {
            if(itemInfoInEdge != null) {
                Game.getUi().getActors().removeValue(itemInfoInEdge,false);
            }
            itemInfoInEdge = item.getInfo();
            Game.getUi().addActor(itemInfoInEdge);
            ItemInfo.resetPosition();
        }
        else {
            Game.log("Not enough free space in inventory...");
        }
    }

    public void touchBlock(Contact contact) {
        touchings++;

        Body block = null;
        if(contact.getFixtureA().getBody().getUserData().equals("player") && contact.getFixtureB().getBody().getUserData().equals("block")) {
            block = contact.getFixtureB().getBody();
        }
        else if(contact.getFixtureB().getBody().getUserData().equals("player") && contact.getFixtureA().getBody().getUserData().equals("block")){
            block = contact.getFixtureA().getBody();
        }
        if(block != null && !isAttacking) {
            if (block.getPosition().y + 4 <= position.y && block.getPosition().x - position.x < 3 * 16) {
                onGround = true;
                wallTouchDirection = 0;
                avaliableJumps = 2;
                if (!currentAnimation.equals("run") && !isRolling) {
                    setAnimation("run");
                    onGroundStep.play();
                }
            } else if (block.getPosition().y - 4 >= position.y - 12 * 16) {
                if (block.getPosition().x > position.x) {
                    if (body.getLinearVelocity().y < -100) {
                        if (!currentAnimation.equals("wall-slide")) {
                            setAnimation("wall-slide");
                        }
                    }
                    wallTouchDirection = 1;
                } else if (block.getPosition().x < position.x) {
                    if (body.getLinearVelocity().y < -100) {
                        if (!currentAnimation.equals("wall-slide")) {
                            setAnimation("wall-slide");
                        }
                    }
                    wallTouchDirection = -1;
                }
            } else {
                wallTouchDirection = 0;
            }
        }
    }

    public void releaseTouch(Contact contact) {
        touchings--;
        if(touchings == 0) {
            setAnimation("jump");
            onGround = false;
        }
    }

    private void setAnimation(String name) {
        if(!isDied) {
            currentAnimation = name;
            stateTime = 0;
        }
    }

    public Body getBody() {
        return body;
    }

    public static void setPosition(Vector2 position) {
        Hero.position = position;
    }

    public void grabSword(Item item) {
        ItemInfo info = new ItemInfo(ItemInfo.TYPE_WEAPON,"Start sword",70,0.04f,1);
        weapon = new Item(game,null,info);
        equip(weapon);
        info.setItem(weapon);
        setAnimation("getsword");
        hasWeapon = true;
    }

    public int getMaxHealth() {
        return (int)maxHealth;
    }

    public Item getWeapon() {
        return weapon;
    }

    public Item getArmor() {
        return armor;
    }

    public float getHealth() {
        return health;
    }

    public int getLevel() {
        return (int)level;
    }

    public void hitEnemy(Enemy enemy) {
        float damage = weapon.getParameter1();
        damage += damage * ((new Random().nextInt(11)-5)/100f);
        boolean critical = Math.random()<(weapon.getParameter2()/100f);
        if(critical) {
            damage *= 1.5f;  // Critical strike
        }
        enemy.damage((int)damage);
        Gdx.input.vibrate(20);
        float cx = game.getStage().getCamera().position.x - game.getStage().getCamera().viewportWidth/2;
        float cy = game.getStage().getCamera().position.y - game.getStage().getCamera().viewportHeight/2;
        Game.getUi().addActor(new OnHitDamageView((int)damage,new Vector2((enemy.getBody().getPosition().x+3 - cx) * 7.5f,(enemy.getBody().getPosition().y+6 - cy) * 7.5f),critical));
    }

    public static void setLevel(double level) {
        Hero.level = level;
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void equipLastItem() {
        equip(inventory.get().get(inventory.get().size()-1));
    }
}
