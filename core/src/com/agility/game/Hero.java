package com.agility.game;

import com.agility.game.UI.ItemInfo;
import com.agility.game.UI.OnHitDamageView;
import com.agility.game.Utils.AnimationWithOffset;
import com.agility.game.Utils.LockedCamera;
import com.agility.game.Utils.SimpleDirectionGestureDetector;
import com.agility.game.Utils.SpritePack;
import com.agility.game.WorldObjects.Item;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

import java.util.HashMap;
import java.util.Random;

import javax.management.ObjectName;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class Hero extends Actor {
    private Body body;
    public Body swordSwipe;
    private final World world;
    private static Vector2 position,velocity;
    private int direction = 1, wallTouchDirection, level;
    private Sprite currentFrame;
    public int damaged;
    private String currentAnimation = "idle";
    private float stateTime = 0, maxHealth = 1000,health = maxHealth, afterRollingSlowlinessTimer;
    private int touchings = 0, avaliableJumps = 2, attackOrder, rollingTimer, rollDirection;
    private boolean onGround = true, isAttacking, hasWeapon, isSwiped, isDied, isRolling;
    private Game game;
    private transient ItemInfo itemInfoInEdge;
    private Inventory inventory;
    private final static Color colorDamage = new Color(1,0.5f,0.5f,1);
    public static Sprite blood;


    private static final HashMap<String,AnimationWithOffset> animations = new HashMap<String, AnimationWithOffset>();
    private boolean anotherOneAttack;
    private boolean stopped;
    private Item weapon, armor;
    private Sound[] swipes = new Sound[6];

    public Hero(Vector2 position, World world, final Game game) {
        this.position = position;
        this.world = world;
        this.game = game;
        isDied = false;
        blood = new Sprite(new Texture(Gdx.files.internal("blood.png")));
        blood.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        swipes[0] = Gdx.audio.newSound(Gdx.files.internal("sounds/swipe0.mp3"));
        swipes[1] = Gdx.audio.newSound(Gdx.files.internal("sounds/swipe1.mp3"));
        swipes[2] = Gdx.audio.newSound(Gdx.files.internal("sounds/swipe2.mp3"));
        swipes[3] = Gdx.audio.newSound(Gdx.files.internal("sounds/swipe0.mp3"));
        swipes[4] = Gdx.audio.newSound(Gdx.files.internal("sounds/swipe1.mp3"));
        swipes[5] = Gdx.audio.newSound(Gdx.files.internal("sounds/swipe2.mp3"));
        init("animations");
        init("default equipment");
        inventory = new Inventory();


        Gdx.input.setInputProcessor(new SimpleDirectionGestureDetector(new SimpleDirectionGestureDetector.DirectionListener() {

            @Override
            public void onUp() {
                if(avaliableJumps>0 && !isDied) {
                    avaliableJumps-=1;
                    body.setLinearVelocity(body.getLinearVelocity().x,0);
                    body.applyLinearImpulse(new Vector2(0, 16000), new Vector2(0, 0), true);
                }

                stopped = false;
            }

            @Override
            public void onRight() {
                if(!isDied) {
                    if (wallTouchDirection == -1 && !onGround && avaliableJumps != 2) {
                        body.setLinearVelocity(0, 0);
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

                            swipes[attackOrder % 6].play(1f);
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
        if(item.getType() == ItemInfo.TYPE_WEAPON) {
            weapon = item;
            hasWeapon = true;
            if(Game.getUi() != null) {
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
        isRolling = true;
        rollingTimer = 30;
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
        currentFrame.setSize(50/2,37/2);
        currentFrame.setFlip(direction == -1,false);
        currentFrame.draw(batch,parentAlpha);
        if(damaged > 0) {
            damaged--;
        }

        if(!stopped && !isRolling && !isDied) {
            stabilizeSpeed();
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
        def.position.y = position.y+9;

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
                swipes[attackOrder % 6].play(1f);
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
                    new Vector2(0,1),
                    new Vector2(1,0),
                    new Vector2((float)Math.sqrt(3)*4-1,0),
                    new Vector2((float)Math.sqrt(3)*4,1),
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




        }

        else if(request.equals("default equipment")) {
            ItemInfo info = new ItemInfo(ItemInfo.TYPE_WEAPON,"Fists",60,0.02f,1);
            weapon = new Item(game,null,info);
            equip(weapon);
            hasWeapon = false;
            info.setItem(weapon);
        }

    }

    public void damage(float deal) {
        this.health -= deal;
        damaged = 20;
        body.applyLinearImpulse(new Vector2(direction*-2999, 0), new Vector2(0, 0), true);
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

    public float getMaxHealth() {
        return maxHealth;
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
        return level;
    }

    public void hitEnemy(Enemy enemy) {
        float damage = weapon.getParameter1();
        damage += damage * ((new Random().nextInt(11)-5)/100f);
        boolean critical = Math.random()<weapon.getParameter2();
        if(critical) {
            damage *= 1.5f;  // Critical strike
        }
        enemy.damage((int)damage);
        Gdx.input.vibrate(60);
        float cx = game.getStage().getCamera().position.x - game.getStage().getCamera().viewportWidth/2;
        float cy = game.getStage().getCamera().position.y - game.getStage().getCamera().viewportHeight/2;
        Game.getUi().addActor(new OnHitDamageView((int)damage,new Vector2((enemy.getBody().getPosition().x+3 - cx) * 7.5f,(enemy.getBody().getPosition().y+6 - cy) * 7.5f),critical));
    }
}
