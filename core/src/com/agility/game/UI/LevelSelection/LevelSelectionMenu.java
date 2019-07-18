package com.agility.game.UI.LevelSelection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.agility.game.Game;

public class LevelSelectionMenu extends Stage {
    private Game game;
    private LevelSelectionItemsHandler handler;

    public LevelSelectionMenu(Game game) {
        this.game = game;
        handler = new LevelSelectionItemsHandler(game);
    }

    @Override
    public void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0.1f,0.1f,0.1f,1);
        handler.draw(getBatch());
    }

    public void touchDown(int screenX, int screenY) {
        if(game.getCurrentState() == Game.STATE_IN_LEVEL_SELECTION) {
            for (int i = 0; i < handler.getItems().length; i++) {
                handler.getItems()[i].hit(screenX, screenY);
            }
        }
    }


    public void touchUp(int screenX, int screenY) {
        if(game.getCurrentState() == Game.STATE_IN_LEVEL_SELECTION) {

        }
    }
}
