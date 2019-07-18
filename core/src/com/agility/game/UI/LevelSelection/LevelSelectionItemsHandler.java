package com.agility.game.UI.LevelSelection;

import com.agility.game.Game;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class LevelSelectionItemsHandler {
    private LevelSelectionItem[] items;
    private Game game;

    public LevelSelectionItemsHandler( Game game) {
        this.game = game;


        FileHandle dirHandle;
        dirHandle = Gdx.files.internal("maps");

        ArrayList<String> names = new ArrayList<String>();

        for (int i = 0; i < dirHandle.list().length; i++) {
            if(!names.contains(dirHandle.list()[i].nameWithoutExtension())) {
                names.add(dirHandle.list()[i].nameWithoutExtension());
            }
        }

        items = new LevelSelectionItem[ names.size() ];
        for (int i = 0; i < items.length; i++) {
            items[i] = new LevelSelectionItem(new Level(game, names.get(i)), new Vector2(100 + 550*i, 50));
        }
    }

    public void draw(Batch batch) {
        for (int i = 0; i < items.length; i++) {
            items[i].draw(batch,1);
        }
    }

    public LevelSelectionItem[] getItems() {
        return items;
    }
}
