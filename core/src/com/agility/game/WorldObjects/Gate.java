package com.agility.game.WorldObjects;

import com.agility.game.Utils.AnimationWithOffset;
import com.agility.game.Utils.SpritePack;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Gate extends Actor {
    private Body body;
    private AnimationWithOffset animation;
    private Vector2 position;
    private int needToKill;
    private float stateTime;

    public Gate(Vector2 position, World world, float opensWithKillsPart, int enemiesCount) {
        this.position = position;

        needToKill = (int)(enemiesCount*opensWithKillsPart);

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
                new Vector2(0,0),
                new Vector2(8,0),
                new Vector2(8,18),
                new Vector2(0,18)};
        shape.set(points);
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 1f;

        body.createFixture(fixtureDef);
        body.setUserData("block");

        animation = new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("gate/gate",11).content),0,0, 0);
        for (int i = 0; i < animation.animation.getKeyFrames().length; i++) {
            animation.animation.getKeyFrames()[i].setSize(14,20);
            animation.animation.getKeyFrames()[i].setPosition(position.x,position.y-2);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        stateTime+=Gdx.graphics.getDeltaTime();
        animation.animation.getKeyFrame(stateTime,true).draw(batch);
    }
}
