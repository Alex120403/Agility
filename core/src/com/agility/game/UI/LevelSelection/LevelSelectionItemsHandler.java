package com.agility.game.UI.LevelSelection;

import com.agility.game.Game;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Collections;

public class LevelSelectionItemsHandler {
    private static LevelSelectionItem[] items;
    private Game game;
    private static LevelSelectionItem selectedItem;
    private Texture selection;
    private static int selectionIndex;

    public LevelSelectionItemsHandler( Game game) {
        this.game = game;


        FileHandle dirHandle;
        dirHandle = Gdx.files.internal("maps");

        ArrayList<String> names = new ArrayList<String>();

        for (int i = 0; i < dirHandle.list().length; i++) {
            if(!names.contains(dirHandle.list()[i].nameWithoutExtension()) && dirHandle.list()[i].nameWithoutExtension().contains("_")) {
                names.add(dirHandle.list()[i].nameWithoutExtension());
            }
        }

        items = new LevelSelectionItem[ names.size() ];
        for (int i = 0; i < items.length; i++) {
            items[i] = new LevelSelectionItem(new Level(game, names.get(i)));
        }

        bubbleSort(items);

        for (int i = 0; i < items.length; i++) {
            items[i].setPosition(new Vector2(100 + 550*i, 50));
        }

        int w = 530;
        int h = 352;
        Pixmap selectionPixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        selectionPixmap.setColor(0,0.7f,0.2f,1);
        selectionPixmap.fillRectangle(0,0,3,h);
        selectionPixmap.fillRectangle(0,0,w-3,3);
        selectionPixmap.fillRectangle(w-3,0,3,h);
        selectionPixmap.fillRectangle(0,h-3,w-3,3);
        selection = new Texture(selectionPixmap);
    }

    public void draw(Batch batch) {
        for (int i = 0; i < items.length; i++) {
            items[i].draw(batch,1);
        }
        batch.begin();
        batch.draw(selection,items[selectionIndex].getX() - 9, items[selectionIndex].getY() - 9);
        batch.end();
    }

    public static void setSelectedItem(LevelSelectionItem selectedItem) {
        LevelSelectionItemsHandler.selectedItem = selectedItem;
        for (int i = 0; i < items.length; i++) {
            if(items[i].equals(selectedItem)) {
                selectionIndex = i;
                break;
            }
        }
    }

    public static void bubbleSort(LevelSelectionItem[] arr){
        for(int i = arr.length-1 ; i > 0 ; i--){
            for(int j = 0 ; j < i ; j++){
            if( arr[j].compareTo(arr[j+1]) > 0){
                LevelSelectionItem tmp = arr[j];
                arr[j] = arr[j+1];
                arr[j+1] = tmp;
            }
        }
    }
}

    public LevelSelectionItem[] getItems() {
        return items;
    }
}
