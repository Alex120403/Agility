package com.agility.game.WorldObjects;

import com.agility.game.Game;
import com.agility.game.Hero;
import com.agility.game.UI.ItemInfo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.io.Serializable;
import java.util.Random;

public class Item extends Actor implements Serializable {
    private transient Sprite icon, info;
    private boolean alreadyStoppedHero;
    ItemInfo state;
    protected transient BitmapFont font;
    public transient Color color;

    public Item() {
    }

    public Item(Game game, String iconName, ItemInfo info) {
        super();
        state = info;
        Game.onGroundItems.add(this);
        this.info = new Sprite(new Texture("itemInfo+.png"));
        if(iconName != null) {
            icon = new Sprite(new Texture("items/"+iconName + ".png"));
        }
        color = new Color((float)Math.random()*2,(float)Math.random()*2,(float)Math.random()*2,1);

        if (font == null) {
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("basis33.ttf"));
            parameter.size = 32;
            parameter.color = Color.WHITE;
            font = generator.generateFont(parameter);
            font.getData().setScale(0.22f);
            generator.dispose();

        }
    }
    public void addToWorld(Stage stage, Vector2 position) {
        stage.addActor(this);
        setPosition(position.x,position.y);
        if(icon != null) {
            icon.setPosition(position.x, position.y + 1);
            icon.setFlip(true, false);
            icon.setSize(8, 8);
            icon.setColor(color);

            info.setPosition(position.x-2, position.y + 9);
            info.setSize(192/9, 128/9);
            Game.getUi().addActor(state);
        }
    }

    public Sprite getIcon() {
        return icon;
    }

    public int getParameter1() {
        return state.getParameter1();
    }

    public float getParameter2() {
        return state.getParameter2();
    }

    public int getLevel() {
        return state.getLevel();
    }

    public int getType() {
        return state.getType();
    }

    public String getName() {
        return state.getName();
    }

    public ItemInfo getInfo() {
        return state;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(icon != null) {
            icon.draw(batch);
            info.draw(batch);


            if (Game.getHero().getWeapon().getParameter1() < state.getParameter1()) {
                font.setColor(Color.GREEN);
                font.draw(batch, ": " + state.getParameter1(), info.getX() + 4.5f, info.getY() + 12.5f);                font.setColor(Color.WHITE);
            } else if (Game.getHero().getWeapon().getParameter1() > state.getParameter1()) {
                font.setColor(Color.RED);
                font.draw(batch, ": " + state.getParameter1(), info.getX() + 4.5f, info.getY() + 12.5f);                font.setColor(Color.WHITE);
            } else {
                font.setColor(Color.YELLOW);
                font.draw(batch, ": " + state.getParameter1(), info.getX() + 4.5f, info.getY() + 12.5f);                font.setColor(Color.WHITE);
            }


            if (Game.getHero().getWeapon().getParameter2() < state.getParameter2()) {
                font.setColor(Color.GREEN);
                font.draw(batch, ": " + (int)state.getParameter2(), info.getX() + 4.5f, info.getY() + 5.5f);
                font.setColor(Color.WHITE);
            } else if (Game.getHero().getWeapon().getParameter2() > state.getParameter2()) {
                font.setColor(Color.RED);
                font.draw(batch, ": " + (int)state.getParameter2(), info.getX() + 4.5f, info.getY() + 5.5f);
                font.setColor(Color.WHITE);
            } else {
                font.setColor(Color.YELLOW);
                font.draw(batch, ": " + (int)state.getParameter2(), info.getX() + 4.5f, info.getY() + 5.5f);
                font.setColor(Color.WHITE);
            }
        }

        //checkForPlayerNearby();
    }



    private void checkForPlayerNearby() {
//        if(icon != null) {
//            if() {
//                Game.getUi().getBatch().begin();
//                state.draw(Game.getUi().getBatch(),1);
//                Game.getUi().getBatch().end();
//            }
//        }
    }
    Item(Vector2 position, World world) {
        // Do not use
    }

    public float rangeToHero() {
        if(icon != null) {
            return (float) Math.hypot(Hero.getPosition().x - icon.getX(), Hero.getPosition().y - icon.getY());
        }
        else {
            return 9999999;
        }
    }

    public boolean isPlayerNearby() {
        float closestRange = 9999999;
        Item nearestItem = null;
        for (Item i : Game.onGroundItems) {
            if(i.rangeToHero() < closestRange) {

                closestRange = i.rangeToHero();
                nearestItem = i;
            }
        }
        return nearestItem.equals(this) && rangeToHero() <= 50;
    }
}
