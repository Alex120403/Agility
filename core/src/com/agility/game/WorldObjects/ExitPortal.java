package com.agility.game.WorldObjects;

import com.agility.game.Game;
import com.agility.game.Hero;
import com.agility.game.Utils.AnimationWithOffset;
import com.agility.game.Utils.SpritePack;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class ExitPortal extends Actor {
    private final Game game;
    private float stateTime = 0;
    private static AnimationWithOffset animation;
    private boolean addedToWorld = false;
    private static final int SPRITE_SIZE = 32;

    public ExitPortal(Game game, Vector2 position) {
        this.game = game;
        setPosition(position.x, position.y);
        setZIndex(0);
        // Init animation
        animation = new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("portal/portal",9).content),0,0, 0);
        for (int i = 0; i < animation.animation.getKeyFrames().length; i++) {
            animation.animation.getKeyFrames()[i].setFlip(true,false);
            animation.animation.getKeyFrames()[i].setSize(SPRITE_SIZE,SPRITE_SIZE);
            animation.animation.getKeyFrames()[i].setPosition(position.x, position.y);
        }
    }

    public void addToWorld(Stage stage) {
        stage.addActor(this);
        addedToWorld = true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(!addedToWorld) {
            throw new RuntimeException("Portal is not in world!");
        }
        animation.animation.getKeyFrame(stateTime,true).draw(batch);

        stateTime += Gdx.graphics.getDeltaTime();

        checkForPlayerTouch();
    }

    private void checkForPlayerTouch() {
        if(Hero.getPosition().x > getX() && Hero.getPosition().x < getX() + SPRITE_SIZE && Hero.getPosition().y >= getY() && Hero.getPosition().y <= getY() + SPRITE_SIZE) {
            game.heroInPortal();
        }
    }


}
