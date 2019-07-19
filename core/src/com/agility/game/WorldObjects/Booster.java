package com.agility.game.WorldObjects;

import com.agility.game.Hero;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.agility.game.Game;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.Objects;
import java.util.Random;

public class Booster extends Actor {

    public static final int KIND_HEALTH = 0;
    public static final int KIND_DAMAGE = 1;
    public static final int KIND_MONEY  = 2;

    private int kind;
    private Sprite sprite;
    private Game game;

    public Booster(Game game, Vector2 position) {
        setPosition(position.x, position.y);
        this.game = game;
        kind = new Random().nextInt(3);
        switch (kind) {
            case KIND_HEALTH:
                sprite = new Sprite(new Texture(Gdx.files.internal("scrolls/scroll_health.png")));
                break;
            case KIND_DAMAGE:
                sprite = new Sprite(new Texture(Gdx.files.internal("scrolls/scroll_damage.png")));
                break;
            case KIND_MONEY:
                sprite = new Sprite(new Texture(Gdx.files.internal("scrolls/scroll_money.png")));
                break;
        }
        sprite.setSize(8,8);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.setPosition(getX() - 4, getY() - 4);

        sprite.draw(batch);
        checkForPlayerTouch();
    }

    private void checkForPlayerTouch() {
        if(Math.abs(Hero.getPosition().x - getX()) <= 4 && Math.abs(Hero.getPosition().y - getY()) <= 8) {
            Game.getStage().getActors().removeValue(this,false);
            game.choose(kind);
        }
    }


}
