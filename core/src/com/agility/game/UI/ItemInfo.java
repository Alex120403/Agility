package com.agility.game.UI;

import com.agility.game.WorldObjects.Item;
import com.agility.game.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;

import java.io.Serializable;

public class ItemInfo extends Actor implements Disposable, Serializable {
    public static final int TYPE_WEAPON = 0;
    public static final int TYPE_ARMOR  = 1;

    private static transient Sprite frame;
    private boolean vanish = true;
    private transient Sprite itemIcon;
    private transient static BitmapFont nameFont, info;
    private String name;
    private int type,vanishTimer = 480;
    private int parameter1;
    private float parameter2;
    //private transient Game Game;
    private int level;

    public ItemInfo() {
    }

    public ItemInfo(int type, String name, int parameter1, float parameter2, int level) {
        this.type = type;
        this.name = name;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.level = level;
        setName("itemInfo");
    }

    public static void resetPosition() {
        if(frame != null) {
            frame.setX(Gdx.graphics.getWidth() - frame.getWidth() - 20);
            frame.setY(Gdx.graphics.getHeight() - frame.getHeight() - 20);
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if(x >= frame.getX() && x <= frame.getX()+frame.getWidth() && y >= frame.getY() && y <= frame.getY()+frame.getHeight()) {
            Game.getHero().equipLastItem();
            Game.getUi().getActors().removeValue(this, false);
            Game.getUi().tapOnUI = true;
        }
        return super.hit(x, y, touchable);
    }

    public void setItem(Item item) {
        this.name = item.getName();
        this.parameter1 = item.getParameter1();
        this.parameter2 = item.getParameter2();
        this.level = item.getLevel();

        if(item.getIcon() != null) {
            itemIcon = new Sprite(item.getIcon().getTexture());
            itemIcon.setPosition(frame.getX() + 28, frame.getY() + 73);
            itemIcon.setSize(64, 64);
            itemIcon.setColor(item.color);
        }
    }

    public static void init() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("basis33.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36;
        parameter.color = Color.WHITE;
        nameFont = generator.generateFont(parameter);
        generator.dispose();

        generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        parameter.size = 24;
        parameter.color = Color.WHITE;
        info = generator.generateFont(parameter);
        generator.dispose();

        frame = new Sprite(new Texture(Gdx.files.internal("itemInfoFrame.png")));
        frame.setX(Gdx.graphics.getWidth() - frame.getWidth() - 20);
        frame.setY(Gdx.graphics.getHeight() - frame.getHeight() - 20);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        vanishTimer--;
        if (vanishTimer <= 0) {
            frame.setPosition(frame.getX(), frame.getY() + 2);
        }
        if (itemIcon != null) {
            itemIcon.setPosition(frame.getX() + 28, frame.getY() + 73);
            itemIcon.setSize(64, 64);
            frame.draw(batch);
            itemIcon.draw(batch);
            nameFont.draw(batch, name, frame.getX() + 20, frame.getY() + 184);
            info.draw(batch, ": " + parameter1, frame.getX() + 148, frame.getY() + 148);
            info.draw(batch, ": " + (int)parameter2 + "%", frame.getX() + 148, frame.getY() + 118);
            if (Game.getHero().getWeapon().getParameter1() < parameter1) {
                info.setColor(Color.GREEN);
                info.draw(batch, "(+" + (parameter1 - Game.getHero().getWeapon().getParameter1()) + ")", frame.getX() + 208, frame.getY() + 148);
                info.setColor(Color.WHITE);
            } else if (Game.getHero().getWeapon().getParameter1() > parameter1) {
                info.setColor(Color.RED);
                info.draw(batch, "(" + (parameter1 - Game.getHero().getWeapon().getParameter1()) + ")", frame.getX() + 208, frame.getY() + 148);
                info.setColor(Color.WHITE);
            } else {
                info.setColor(Color.ORANGE);
                info.draw(batch, "(+0)", frame.getX() + 208, frame.getY() + 148);
                info.setColor(Color.WHITE);
            }
            if (Game.getHero().getWeapon().getParameter2() < parameter2) {
                info.setColor(Color.GREEN);
                info.draw(batch, "(+" + (int)(parameter2 - Game.getHero().getWeapon().getParameter2())+ "%)", frame.getX() + 208, frame.getY() + 118);
                info.setColor(Color.WHITE);
            } else if (Game.getHero().getWeapon().getParameter2() > parameter2) {
                info.setColor(Color.RED);
                info.draw(batch, "(" + (int)(parameter2 - Game.getHero().getWeapon().getParameter2())+ "%)", frame.getX() + 208, frame.getY() + 118);
                info.setColor(Color.WHITE);
            } else {
                info.setColor(Color.ORANGE);
                info.draw(batch, "(+0)", frame.getX() + 208, frame.getY() + 118);
                info.setColor(Color.WHITE);
            }
        }

    }

    @Override
    public void dispose() {
        frame.getTexture().dispose();
        nameFont.dispose();
        info.dispose();
    }

    @Override
    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public int getParameter1() {
        return parameter1;
    }

    public float getParameter2() {
        return parameter2;
    }

    public int getLevel() {
        return level;
    }
}
