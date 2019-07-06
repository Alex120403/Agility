package com.agility.game;

import com.agility.game.WorldObjects.Block;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

class BlockFactory {
    private static Texture atlas;
    public static final int TILES_FOR_X = 16;

    private static final ArrayList<Integer> midLayerIds = new ArrayList<Integer>();
    private static final ArrayList<Integer> fgLayerIds = new ArrayList<Integer>();
    private static final ArrayList<Integer> reserv = new ArrayList<Integer>();


    private static final BlockFactory ourInstance = new BlockFactory();
    public static Vector2 heroStartPos;
    public static Vector2 startWeaponPos;
    public static ArrayList<Vector2> enemiesPos = new ArrayList<Vector2>();
    public static ArrayList<Vector2> boostsPos = new ArrayList<Vector2>();
    public static Vector2 portalPos;
    public static Vector2 exitPos;

    static BlockFactory getInstance() {
        if (atlas == null) {
            atlas = new Texture(Gdx.files.internal("block_tiles.png"));
        }
        return ourInstance;
    }

    private BlockFactory() {

        final int[] ml = {16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33,
                34, 35, 36, 38, 39, 40, 41, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52,
                59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 74, 75,
                76, 77, 78, 79, 80, 82,83,84,91,92,93,95,98,99,100,107,108,109};
        for (int i = 0; i < ml.length; i++) {
            midLayerIds.add(ml[i]);
        }

        final int[] fgl = {90,106,42,58,94,11,111,141,156,123,124,125,126,155,156,157,158,159,168,169,170};
        for (int i = 0; i < fgl.length; i++) {
            fgLayerIds.add(fgl[i]);
        }
        final int[] reserved = {181, 182, 183, 184, 185, 186};
        for (int i = 0; i < reserved.length; i++) {
            reserv.add(reserved[i]);
        }
    }
    public Block create(int tileId, Vector2 position, World bg, World mid, World fg) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.x = position.x+4;
        def.position.y = position.y+4;
        Body body = null;

        int layer;
        if(midLayerIds.contains(tileId)) {
            body = mid.createBody(def);
            layer = 2;
            body.setSleepingAllowed(true);
            body.setGravityScale(0);

            FixtureDef fixtureDef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(4,4);
            fixtureDef.shape = shape;
            fixtureDef.friction = 1f;
            fixtureDef.density = 1;

            body.createFixture(fixtureDef);
            body.setUserData("block");
        }
        else if(fgLayerIds.contains(tileId)) {
            layer = 4;
        }
        else if(reserv.contains(tileId)) {
            switch (tileId) {
                case (181):
                    heroStartPos = position;
                    break;
                case (182):
                    startWeaponPos = position;
                    break;
                case (183):
                    enemiesPos.add(position);
                    break;
                case (184):
                    boostsPos.add(position);
                    break;
                case (185):
                    exitPos = position;
                    break;
                case (186):
                    portalPos = position;
                    break;
            }
            layer = 1;
        }
        else {
            layer = 1;
        }




        Vector2 tilePosition = new Vector2(tileId%TILES_FOR_X,tileId/TILES_FOR_X);
        TextureRegion tile = new TextureRegion(atlas,(int)tilePosition.x*8,(int)tilePosition.y*8,8,8);



        return new Block(tile,body,layer,position,tileId);
    }

    public static void refreshVariables() {
        heroStartPos = null;
        startWeaponPos = null;
        portalPos = null;
        exitPos = null;
        enemiesPos.clear();
        boostsPos.clear();
    }
}
