package com.agility.game.WorldObjects;

import com.agility.game.Game;
import com.agility.game.Hero;
import com.agility.game.UI.ItemInfo;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.Random;

public class Item extends Actor {
    private Sprite icon;
    Game game;
    private boolean alreadyStoppedHero;
    ItemInfo info;
    public Item(Game game, String iconName, ItemInfo info) {
        super();
        this.info = info;
        if(iconName != null) {
            icon = new Sprite(new Texture("items/"+iconName + ".png"));
        }
        this.game = game;
    }
    public void addToWorld(Stage stage, Vector2 position) {
        stage.addActor(this);
        setPosition(position.x,position.y);
        if(icon != null) {
            icon.setPosition(position.x, position.y + 1);
            icon.setFlip(true, false);
            icon.setSize(8, 8);
        }
    }

    public Sprite getIcon() {
        return icon;
    }

    public int getParameter1() {
        return info.getParameter1();
    }

    public float getParameter2() {
        return info.getParameter2();
    }

    public int getLevel() {
        return info.getLevel();
    }

    public int getType() {
        return info.getType();
    }

    public String getName() {
        return info.getName();
    }

    public ItemInfo getInfo() {
        return info;
    }

    public Game getGame() {
        return game;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(icon != null) {
            icon.draw(batch);
        }

        checkForPlayerNearby();
    }

    private void checkForPlayerNearby() {
        if(icon != null) {
            if (Math.abs(Hero.getPosition().x - (icon.getX() + icon.getWidth() / 2)) < 4 &&
                    Math.abs(Hero.getPosition().y - icon.getY()) < 5 && !alreadyStoppedHero) {
                game.getHero().addItem(this);
                game.getStage().getActors().removeValue(this,false);
                alreadyStoppedHero = true;
            } else if (alreadyStoppedHero && !(Hero.getPosition().x >= icon.getX()-6 && Hero.getPosition().x <= icon.getX() + icon.getWidth() &&
                    Math.abs(Hero.getPosition().y - icon.getY()) < 5)) {
                alreadyStoppedHero = false;
            }
        }
    }
    Item(Vector2 position, World world) {
        // Do not use
    }

}
