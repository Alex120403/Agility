package com.agility.game;

import com.agility.game.Utils.AnimationWithOffset;
import com.agility.game.Utils.EnemyDef;
import com.agility.game.Utils.SpritePack;
import com.agility.game.WorldObjects.Coin;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.HashMap;
import java.util.Random;

public class Enemy extends Actor {
    private Body body;
    private final World world;
    private Vector2 position, dieposition;
    private int direction = 1, damaged;
    private Sprite currentFrame;
    private String currentAnimation = "idle";
    private final float DEFAULT_COOLDOWN, stateTimeSlash;
    private float stateTime = 0, maxHealth,health, cooldown, alpha = 1 , damage, visibilityX, visibilityY, attackRange, runVelocity;
    private final HashMap<String,AnimationWithOffset> animations;
    private final Texture hpbg,hpfg;
    private boolean died = false,alreadyDealedDamage, isAttacking, hasDrop = true, hasDiamonds = true;
    private Game game;
    private final static Color colorDamage = new Color(1,0.5f,0.5f,1);


    public Enemy(EnemyDef def, World world, Vector2 position, Game game) {
        maxHealth = def.maxHealth;
        health = maxHealth;
        DEFAULT_COOLDOWN = def.cooldown;
        cooldown = def.cooldownInStart;
        animations = def.animations;
        damage = def.damageDealt;
        stateTimeSlash = def.stateTimeSlash;
        visibilityX = def.visibilityX;
        visibilityY = def.visibilityY;
        attackRange = def.attackRange;
        runVelocity = def.runVelocity;
        this.world = world;
        this.position = position;
        this.game = game;
        setName("enemy");
        //cooldown =

        init("body");
        init("animations");

        hpbg = new Texture(Gdx.files.internal("hpbg.jpg"));
        hpfg = new Texture(Gdx.files.internal("hpfg.jpg"));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(Gdx.input.isKeyPressed(Input.Keys.V)) {
            damage(1);
        }

        if(Math.abs(Hero.getPosition().y - position.y) < visibilityY && Math.abs(Hero.getPosition().x - position.x) < visibilityX  && !isAttacking && !died) {
            if (Math.abs(Hero.getPosition().x - position.x) < attackRange) {
                if(Hero.getPosition().x - position.x < 0) {
                    direction = -1;
                }
                else if(Hero.getPosition().x - body.getPosition().x > 0) {
                    direction = 1;
                }
                if(cooldown > 0) {
                    cooldown--;
                }
                if(cooldown == 0 && !game.getHero().isDied()) {
                    attack();
                }
                else if(cooldown != 0 && !currentAnimation.equals("idle")) {
                    setAnimation("idle");
                }
                else if(game.getHero().isDied() && !currentAnimation.equals("idle")) {
                    setAnimation("idle");
                }
            }
            else if(Hero.getPosition().x - position.x < 0) {
                direction = -1;
                if(damaged == 0 && !died) {
                    body.setLinearVelocity(runVelocity*-1, body.getLinearVelocity().y);
                }
                else {
                    body.setLinearVelocity(0, body.getLinearVelocity().y);
                }
                if(!currentAnimation.equals("run")) {
                    setAnimation("run");
                }
            }
            else if(Hero.getPosition().x - body.getPosition().x > 0) {
                direction = 1;
                if(damaged == 0 && !died) {
                    body.setLinearVelocity(runVelocity,body.getLinearVelocity().y);
                }
                else {
                    body.setLinearVelocity(0, body.getLinearVelocity().y);
                }
                if(!currentAnimation.equals("run")) {
                    setAnimation("run");
                }
            }
        }

        else if (Math.abs(Hero.getPosition().y - position.y) < visibilityY && !died && !isAttacking){
            if(Hero.getPosition().x - position.x < 0) {
                direction = -1;
            }
            else if(Hero.getPosition().x - body.getPosition().x > 0) {
                direction = 1;
            }
        }
        else if(!isAttacking){
            body.setLinearVelocity(0,body.getLinearVelocity().y);
            if(!currentAnimation.equals("idle")) {
                setAnimation("idle");
            }
        }
        if(health<=0) {
            dropDiamonds();
            body.setLinearVelocity(0,0);
            alpha-=0.002f;
            if(!died) {
                dieposition = position;
                died = true;
                world.destroyBody(body);
                setAnimation("die");
            }
            position = dieposition;

        }
        else {
            position = body.getPosition();
        }
        stateTime+=Gdx.graphics.getDeltaTime();
        currentFrame = animations.get(currentAnimation).animation.getKeyFrame(stateTime, !isAttacking && !died);

        if(hasDrop && alpha <= 0.8f) {
            hasDrop = false;
            game.addRandomItem(position);

        }


        if(alpha<=0.01f) {
            game.getEnemies().remove(this);
            game.getStage().getActors().removeValue(this,true);
        }

        if(damaged > 0) {
            batch.setColor(colorDamage);
            currentFrame.setColor(colorDamage);
        }
        if(!died && alpha > 0.6f){
            currentFrame.setPosition(body.getPosition().x + animations.get(currentAnimation).defaultXOffset - direction * animations.get(currentAnimation).xOffset, body.getPosition().y - 0.5f + animations.get(currentAnimation).yOffset);
        }
        else {
            currentFrame.setPosition(dieposition.x + animations.get(currentAnimation).defaultXOffset - direction * animations.get(currentAnimation).xOffset, dieposition.y - 0.5f + animations.get(currentAnimation).yOffset);

        }
        currentFrame.setScale(0.5f);
        currentFrame.setFlip(direction == -1,false);
        currentFrame.draw(batch,alpha);
        if(isAttacking && animations.get("attack").animation.isAnimationFinished(stateTime)) {
            isAttacking = false;
            alreadyDealedDamage = false;
        }
        else if(isAttacking) {
            body.setLinearVelocity(0,body.getLinearVelocity().y);
            if(stateTime>=stateTimeSlash && stateTime <= stateTimeSlash+0.1f && !alreadyDealedDamage && !died) {
                if(Math.abs(Hero.getPosition().y - position.y) <= visibilityY) {
                    if(direction == 1 && Hero.getPosition().x - position.x <= attackRange && Hero.getPosition().x - position.x > 0) {
                        game.getHero().damage(damage);
                    }
                    else if(direction == -1 && position.x - Hero.getPosition().x <= attackRange && position.x - Hero.getPosition().x > 0) {
                        game.getHero().damage(damage);
                    }
                    alreadyDealedDamage = true;

                }
            }
        }
        if(damaged > 0) {
            currentFrame.setColor(Color.WHITE);
            batch.setColor(Color.WHITE);
            damaged--;
        }
        if(health>0 && health != maxHealth) {
            batch.draw(hpbg, position.x - 1, position.y + 15, hpbg.getWidth() / 3, hpbg.getHeight() / 10f);
            batch.draw(hpfg, position.x - 1, position.y + 15, hpfg.getWidth() / 3 * (health / maxHealth), hpfg.getHeight() / 10f);
        }

    }

    private void attack() {
        isAttacking = true;
        setAnimation("attack");
        cooldown();
    }
    private void init(String request) {
        if(request.equals("body")) {
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
                    new Vector2((float)Math.sqrt(3)*2,(float)Math.sqrt(12)*4),
                    new Vector2(0,12),
                    new Vector2(0,3),
                    new Vector2(1,0),
                    new Vector2((float)Math.sqrt(3)*4-1,0),
                    new Vector2((float)Math.sqrt(3)*4,3),
                    new Vector2((float)Math.sqrt(3)*4,12),
                    new Vector2((float)Math.sqrt(3)*2,(float)Math.sqrt(12)*4)};
            shape.set(points);
            fixtureDef.shape = shape;
            fixtureDef.density = 0.9f;
            fixtureDef.friction = 1f;

            body.createFixture(fixtureDef);
            body.setUserData("enemy");

        }
        else if(request.equalsIgnoreCase("animations")) {


        }
    }

    public Body getBody() {
        return body;
    }

    public void damage(float deal) {
        this.health -= deal;
        damaged = 10;
        if(health > 0) {
            body.applyLinearImpulse(new Vector2(direction * -5999, 1999), new Vector2(0, 0), true);
        }
        else {


        }
    }
    private void setAnimation(String name) {
        if(died && name.equals("die") || !died) {
            currentAnimation = name;
            stateTime = 0;
        }

    }

    private void dropDiamonds() {
        if(hasDiamonds) {
            hasDiamonds = false;
            for (int i = 0; i < 15; i++) {
                //game.getStage().addActor(new Coin(new Vector2(position.x, position.y+i/2), game.getMainWorld(), game));
            }
        }
    }


    public void cooldown() {
        this.cooldown = DEFAULT_COOLDOWN;
    }
}
