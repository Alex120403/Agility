package com.agility.game;

import com.agility.game.Game;
import com.agility.game.UI.MainMenuItem;
import com.agility.game.UI.MenuItemEvent;
import com.agility.game.Utils.GameBalanceConstants;
import com.agility.game.Utils.MainMenuInputProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenu extends Stage {
    private MainMenuItem continueGame, newGame, settings, exit;
    public Music music;
    private Texture background;
    private static int X, WIDTH;
    private Game game;
    public MainMenu(final Game game) {
        super();

        music = Gdx.audio.newMusic(Gdx.files.internal("music/mainMenu.mp3"));
        music.setLooping(true);
        music.play();
        Gdx.input.setInputProcessor(new MainMenuInputProcessor(this, game.getLevelSelectionMenu()));
        background = new Texture(Gdx.files.internal("mainMenu.png"));
        this.game = game;
        WIDTH = Gdx.graphics.getHeight()*16/9;
        X = Gdx.graphics.getWidth()/2 - WIDTH/2;

        continueGame = new MainMenuItem(new MenuItemEvent() {
            @Override
            public void handle() {
                game.openLevelSelectionMenu();
            }
        }, new Texture(Gdx.files.internal("buttons/continue.png")),Gdx.graphics.getHeight()/1.71f);
        addActor(continueGame);
        newGame = new MainMenuItem(new MenuItemEvent() {
            @Override
            public void handle() {
                game.openLevelSelectionMenu();
            }
        }, new Texture(Gdx.files.internal("buttons/newGame.png")),Gdx.graphics.getHeight()/2.18f);
        addActor(newGame);
        settings = new MainMenuItem(new MenuItemEvent() {
            @Override
            public void handle() {

            }
        }, new Texture(Gdx.files.internal("buttons/settings.png")),Gdx.graphics.getHeight()/3f);
        addActor(settings);
        exit = new MainMenuItem(new MenuItemEvent() {
            @Override
            public void handle() {
                Gdx.app.exit();
            }
        }, new Texture(Gdx.files.internal("buttons/exit.png")),Gdx.graphics.getHeight()/4.7f);
        addActor(exit);


    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(game.getCurrentState() == Game.STATE_IN_MAIN_MENU) {
            continueGame.touchDown(screenX, screenY);
            newGame.touchDown(screenX, screenY);
            settings.touchDown(screenX, screenY);
            exit.touchDown(screenX, screenY);
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public void draw() {
        getBatch().begin();
        getBatch().draw(background,X,0,WIDTH,Gdx.graphics.getHeight());
        getBatch().end();
        super.draw();
    }
}
