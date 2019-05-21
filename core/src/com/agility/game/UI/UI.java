package com.agility.game.UI;

import com.agility.game.Game;
import com.agility.game.Hero;
import com.agility.game.Utils.SimpleDirectionGestureDetector;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class UI extends Stage {
    public boolean tapOnUI;
    private Label fps;
    private BitmapFont font;
    private HeroHealthPanel healthPanel;
    private String message = "";
    private float opacity = 0.99f;
    Game game;
    public UI(Game game) {
        super();

        healthPanel = new HeroHealthPanel(game);
        addActor(healthPanel);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("basis33.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
        this.game = game;
    }

    @Override
    public void draw() {
        super.draw();
        getBatch().begin();
        font.setColor(0.8f, 0.8f, 0.8f, opacity);
        font.draw(getBatch(), message, 40, 50);
        Hero.blood.draw(Game.getUi().getBatch(), (float)Math.pow(1 - game.getHero().getHealth()/game.getHero().getMaxHealth(),2));
        if(game.getHero().damaged > 0) {
            Hero.blood.draw(Game.getUi().getBatch(),0.7f*(game.getHero().damaged/20f));

        }
        getBatch().end();
        opacity-=(1-opacity)/25;
    }

    public boolean tap(int x, int y) {
        tapOnUI = false;
        try {
            for (int i = 0; i < getActors().size; i++) {
                getActors().get(i).hit((float)x, (float)Gdx.graphics.getHeight()-y,false);
            }
        }
        catch (Exception e) {

        }
        return tapOnUI;
    }

    @Override
    public void act() {
        super.act();
    }


    public void log(String message) {
        this.message = message;
        opacity = 0.99f;
    }
}
