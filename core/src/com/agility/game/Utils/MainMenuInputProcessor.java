package com.agility.game.Utils;

import com.agility.game.MainMenu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

public class MainMenuInputProcessor implements InputProcessor {
    private MainMenu mainMenu;

    public MainMenuInputProcessor(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        mainMenu.touchDown(screenX,Gdx.graphics.getHeight()-screenY,pointer,button);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
