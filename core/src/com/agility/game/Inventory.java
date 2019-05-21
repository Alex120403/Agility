package com.agility.game;

import com.agility.game.WorldObjects.Item;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;

public class Inventory extends Actor {
    private final Item[][] content = new Item[7][3];

    public boolean add(Item item) {
        for (int i = 0; i < content.length; i++) {
            for (int j = 0; j < content[i].length; j++) {
                if(content[i][j] == null) {
                    content[i][j] = item;
                    return true;
                }
            }
        }
        return false;
    }
    public Item[][] get() {
        return content;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

    }
}
