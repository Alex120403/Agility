package com.agility.game.WorldObjects;

import com.agility.game.Game;
import com.agility.game.Hero;
import com.agility.game.UI.MoneyMonitor;
import com.agility.game.Utils.AnimationWithOffset;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Random;

public class Coin extends Actor {

    public static final int TYPE_EMERALD = 0;
    public static final int TYPE_SAPPHIRE = 1;
    public static final int TYPE_RUBY = 2;
    public static final int TYPE_EMPTYCOIN = 3;
    public static final int TYPE_COIN = 4;

    private static final Random random = new Random();
    private Body body;
    private Vector2 position;
    private World world;
    private Game game;
    private static final Texture[] atlases = new Texture[8];
    public static final int X_OFFSET = 110;
    public static final int Y_OFFEST = 40;
    private AnimationWithOffset animation;
    private float stateTime;

    protected Vector2 v2Position;
    protected Vector2 v2Velocity = new Vector2();

    private static Sprite[][] sprites = new Sprite[6][atlases.length];

    // Sprite: 84x100

    public Coin(Vector2 position, World world, Game game) {
        this.position = position;
        this.world = world;
        this.game = game;

        init("body");
        body.applyLinearImpulse((random.nextFloat()+0.2f) * (Math.random() > 0.5f ? 3f : -3f),random.nextFloat()/12f,0,0,false);
        System.out.println(random.nextFloat() * (Math.random() > 0.5f ? 3f : -3f)+"");

        animation = new AnimationWithOffset(new Animation<Sprite>(0.5f,sprites[getRandomType()]),0,0,0);
    }

    public static void loadAtlases() {

        for (int i = 0; i < atlases.length; i++) {
            atlases[i] = new Texture(Gdx.files.internal("itemAtlases/"+i+".gif"));
            for (int j = 0; j < sprites.length-1; j++) {
                sprites[j][i] = new Sprite(new TextureRegion(atlases[i],105+84*j,40,100,84));
                sprites[j][i].setSize(10f/1.5f,8.4f/1.5f);
            }
            sprites[5][i] = new Sprite(new TextureRegion(atlases[i],105+84*4,40+100*3,100,84));
            sprites[5][i].setSize(10f/1.5f,8.4f/1.5f);
        }
        MoneyMonitor.setAnimation(sprites[5]);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        animation.animation.getKeyFrame(stateTime,true).draw(batch,0.7f);
        stateTime+=0.07f;
        checkForPlayerTouch();
        if(stateTime >= 2.5f) {
            moveToHero();

        }
        else {
            body.setLinearVelocity(body.getLinearVelocity().x,body.getLinearVelocity().y/1.05f);
            moveNormally();
        }
    }

    private void moveNormally() {
        position.set(body.getPosition().x-5/1.5f,body.getPosition().y);
        animation.animation.getKeyFrame(stateTime,true).setPosition(body.getPosition().x-5/1.5f,body.getPosition().y);
    }

    private void moveToHero() {

        setVelocity(Hero.getPosition().x,Hero.getPosition().y);
        update();
        animation.animation.getKeyFrame(stateTime,true).setPosition(position.x,position.y);

    }

    public void setVelocity (float toX, float toY) {

// The .set() is setting the distance from the starting position to end position
        v2Velocity.set(toX - position.x, toY - position.y);
        v2Velocity.nor(); // Normalizes the value to be used

        v2Velocity.x *= 2;  // Set speed of the object
        v2Velocity.y *= 2;

    }

    public void update() {
        position.add (v2Velocity);    // Update position
    }

    private void checkForPlayerTouch() {
        if(Math.abs(position.x - game.getHero().getBody().getPosition().x) <= 8 &&
                Math.abs(position.y - game.getHero().getBody().getPosition().y) <= 10){
            try {
                MoneyMonitor.addMoney(random.nextInt(5));
                body.destroyFixture(body.getFixtureList().first());
                game.getStage().getActors().removeValue(this, true);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void init(String request) {
        if(request.equals("body")) {
            // Body init
            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.DynamicBody;
            def.position.x = position.x+4;
            def.position.y = position.y;
            body = world.createBody(def);
            body.setSleepingAllowed(true);
            body.setGravityScale(10);
            body.setUserData("coin");

            FixtureDef fixtureDef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(0.001f,0.001f);
            fixtureDef.shape = shape;
            fixtureDef.friction = 0;
            fixtureDef.density = 1;
            //fixtureDef.restitution = 0.5f;

            body.createFixture(fixtureDef);
        }
    }

    private static int getRandomType() {
        return random.nextInt(5);
    }
}
