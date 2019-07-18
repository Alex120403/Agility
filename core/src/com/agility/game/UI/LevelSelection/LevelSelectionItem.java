package com.agility.game.UI.LevelSelection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;

public class LevelSelectionItem extends Actor {

    private Sprite preview;
    private Level level;
    private static BitmapFont nameFont;

    public LevelSelectionItem(Level level, Vector2 position) {
        this.level = level;

        preview = new Sprite(new Texture(Gdx.files.internal("maps/"+level.getName()+".png")));
        preview.setSize(512, 576/2);
        preview.setPosition(position.x, position.y);

        if(nameFont == null) {
            initResources();
        }
    }


    public void hit(float x, float y) {
        if (x >= preview.getX() && x <= preview.getX() + preview.getWidth() &&
                y >= preview.getY() && y <= preview.getY() + preview.getHeight()) {
            // Hit handle
            level.start();
        }
    }

    private void scroll(float directedValue) {
        preview.setX(preview.getX() + directedValue);
    }

    private static void initResources() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("basis33.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 48;
        parameter.color = Color.WHITE;
        nameFont = generator.generateFont(parameter);
        generator.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.begin();
        preview.draw(batch);
        nameFont.draw(batch, level.getName(), preview.getX() + 10, preview.getY() + preview.getHeight() + 40);
        batch.end();
    }

    public static void dispose() {
        nameFont.dispose();
        // TODO dispose
    }
}
