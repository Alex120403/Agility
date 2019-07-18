package com.agility.game.WorldObjects;

import com.agility.game.Game;
import com.agility.game.Hero;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Block extends Actor {
    private final TextureRegion tile;
    private final Body body;
    public final int layer;
    private final Vector2 position;
    private final int tileId;


    public Block(TextureRegion tile, Body body, int layer, Vector2 position, int tileId) {
        this.tile = tile;
        this.body = body;
        this.layer = layer;
        this.tileId = tileId;
        this.position = position;
        setZIndex(1);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

            if (tileId != 0 && tileId != 15) {
                batch.draw(tile, position.x, position.y);
            }

    }

    public int getLayer() {
        return layer;
    }
}
