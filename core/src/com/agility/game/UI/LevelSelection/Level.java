package com.agility.game.UI.LevelSelection;

import com.agility.game.BlockFactory;
import com.agility.game.Map;
import com.agility.game.Utils.MapParser;
import com.agility.game.WorldObjects.Block;
import com.badlogic.gdx.math.Vector2;
import com.agility.game.Game;

public class Level {

    private Map map;
    private Block[][] block;
    private String name;
    private Game game;

    public Level(Game game, String name) {
        this.name = name;
        this.game = game;
    }

    private void init() {
        map = MapParser.getInstance().parse(name + ".csv");
        block = new Block[map.getCells().length][map.getCells()[0].length];

        for (int i = 0; i < block.length; i++) {
            for (int j = 0; j < block[0].length; j++) {
                block[i][j] = BlockFactory.getInstance().create(map.getCells()[i][j],new Vector2(8*i,8*j),game.getBackgroundWorld(),game.getMainWorld(),game.getForegroundWorld());

                block[i][j].setZIndex(block[i][j].layer);
                Game.getStage().addActor(block[i][j]);
            }
        }
    }

    public Map getMap() {
        return map;
    }

    public String getName() {
        return name;
    }

    public Block[][] getBlock() {
        return block;
    }

    public void start() {
        game.prepare();
        init();
        game.start(this);
    }
}
