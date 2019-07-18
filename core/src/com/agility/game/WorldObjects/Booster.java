package com.agility.game.WorldObjects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.agility.game.Game;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Booster extends Actor {
    public Booster(Game game, Vector2 position) {
        setPosition(position.x, position.y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
