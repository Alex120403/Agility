package com.agility.game.WorldObjects;

import com.agility.game.Game;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Coin extends Actor {
    private Body body;
    private Vector2 position;
    private World world;
    private Game game;

    public Coin(Vector2 position, World world, Game game) {
        this.position = position;
        this.world = world;
        this.game = game;

        init("body");
    }

    private void init(String request) {
        if(request.equals("body")) {
            // Body init
            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.StaticBody;
            def.position.x = position.x+4;
            def.position.y = position.y;
            body = world.createBody(def);
            body.setSleepingAllowed(true);
            body.setGravityScale(0);

            FixtureDef fixtureDef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(5,0.1f);
            fixtureDef.shape = shape;
            fixtureDef.friction = 0;
            fixtureDef.density = 1;

            body.createFixture(fixtureDef);
        }
    }
}
