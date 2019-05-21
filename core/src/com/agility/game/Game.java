package com.agility.game;

import com.agility.game.UI.ItemInfo;
import com.agility.game.UI.OnHitDamageView;
import com.agility.game.UI.UI;
import com.agility.game.WorldObjects.Block;
import com.agility.game.Utils.*;
import com.agility.game.WorldObjects.Item;
import com.agility.game.WorldObjects.StartWeapon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Game extends com.badlogic.gdx.Game {

    // Enemy patterns
    public static EnemyDef ENEMY_BANDIT;
    public static EnemyDef ENEMY_SKELETON;
    public static EnemyDef ENEMY_UNDEAD;


    private World background,middle,foreground;
    private Box2DDebugRenderer debugRenderer;
    private Stage stage;
    private static UI ui;
    private static MainMenu mainMenu;
    private static LockedCamera camera;
    private SpriteBatch batch;
    private Map map;
    private Hero hero;
    private boolean inMenu = true;


    Block[][] block = new Block[128][72];
    public static StartWeapon startWeapon;
    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();

    public Game() {

    }

    @Override
    public void create() {
        Gdx.gl.glClearColor(10f/100f,12f/100f,19.7f/100f,1);
        /*File assetsDir = new File("E:\\Android\\Agility\\android\\assets\\enemies\\undead");
        File[] assets = assetsDir.listFiles();
        for (int i = 0; i < assets.length; i++) {
            assets[i].renameTo(new File("E:\\Android\\Agility\\android\\assets\\enemies\\undead\\die-0"+assets[i].getName().replaceAll(".gif","")+".png"));
        }
        */
        System.out.println("Program start");

        System.out.print("Init main menu........");
        init("main menu");

    }

    public void start() {
        mainMenu.music.stop();
        mainMenu.music.dispose();
        if(inMenu) {
            System.out.print("Init box2d............");
            init("box2d");
            System.out.print("Init hero.............");
            init("hero");
            System.out.print("Init camera...........");
            init("camera");
            System.out.print("Init stage............");
            init("stage");
            System.out.print("Init map..............");
            init("map");
            System.out.print("Init enemies..........");
            init("enemies");
            System.out.print("Init stage elements...");
            init("stage elements");
            System.out.print("Init ui...............");
            init("ui");

            inMenu = false;
        }
    }

    public static void log(String message) {
        ui.log(message);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        if(!inMenu) {
            Gdx.gl.glClearColor(33f/255f,38f/255f,63f/255f,1);
            //Gdx.gl.glClearColor(Color.SKY.r,Color.SKY.g,Color.SKY.b,1);
            camera.update();

            stage.getBatch().setProjectionMatrix(camera.combined);
            stage.draw();
            ui.act();
            ui.draw();
            if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
                debugRenderer.render(background, camera.combined);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.X)) {
                debugRenderer.render(middle, camera.combined);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.C)) {
                debugRenderer.render(foreground, camera.combined);
            }


            middle.step((1 / 100f), 4, 4);
        }
        else {
            Gdx.gl.glClearColor(10f/100f,12f/100f,19.7f/100f,1);
            mainMenu.draw();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        if(!inMenu) {
            batch.dispose();
            stage.dispose();
            debugRenderer.dispose();
        }
        mainMenu.dispose();
    }

    public void addRandomItem(Vector2 position, boolean fromBoss) {
        Item item = null;
        if(Math.random()>=0f) {
            item = new ItemFactory(this).createRandomWeapon();
        }
        else {

        }
        item.addToWorld(stage, position);
    }

    public void addRandomItem(Vector2 position) {
        addRandomItem(position,false);
    }

    private void init(String request) {
        if     (request.equalsIgnoreCase("box2D")) {
            background = new World(new Vector2(0,0),true);
            middle = new World(new Vector2(0,-10),true);
            foreground = new World(new Vector2(0,0),true);
            debugRenderer = new Box2DDebugRenderer();
            middle.setContactListener(new ContactHandler(this));
        }
        else if(request.equalsIgnoreCase("hero")) {
            hero = new Hero(new Vector2(25,565),middle,this);
            hero.setZIndex(3);
        }
        else if(request.equalsIgnoreCase("camera")) {
            float abstractHeight = 720/7.5f;
            double w_div_h = Gdx.graphics.getWidth()/Gdx.graphics.getHeight();
            camera  = new LockedCamera(abstractHeight*(float)w_div_h,abstractHeight,hero);
        }
        else if(request.equalsIgnoreCase("stage")) {
            batch = new SpriteBatch();
            stage = new Stage(new ExtendViewport(camera.viewportWidth, camera.viewportHeight, camera),batch);

        }
        else if(request.equalsIgnoreCase("map")) {
            map = MapParser.getInstance().parse("big.csv");
            for (int i = 0; i < block.length; i++) {
                for (int j = 0; j < block[0].length; j++) {
                    block[i][j] = BlockFactory.getInstance().create(map.getCells()[i][j],new Vector2(8*i,8*j),background,middle,foreground);

                    block[i][j].setZIndex(block[i][j].layer);
                    stage.addActor(block[i][j]);
                }
            }

        }

        else if(request.equalsIgnoreCase("ui")) {
            ui = new UI(this);
            OnHitDamageView.init();
        }

        else if(request.equalsIgnoreCase("stage elements")) {
            Hero.setPosition(BlockFactory.heroStartPos);
            hero.init("body");
            stage.addActor(hero);
            ItemInfo.init();
            createStartSword();

        }

        else if(request.equalsIgnoreCase("enemies")) {
            EnemyDef[] patterns = new EnemyDef[3];
            ENEMY_BANDIT = new EnemyDef();
            ENEMY_BANDIT.cooldown = 60;
            ENEMY_BANDIT.cooldownInStart = 30;
            ENEMY_BANDIT.attackRange = 15;
            ENEMY_BANDIT.visibilityY = 5;
            ENEMY_BANDIT.visibilityX = 50;
            ENEMY_BANDIT.attackRange = 15;
            ENEMY_BANDIT.maxHealth = 300+Math.round(Math.random()-0.3)*100;
            ENEMY_BANDIT.runVelocity = 65;
            ENEMY_BANDIT.damageDealt = 75;
            ENEMY_BANDIT.stateTimeSlash = 0.4f;
            ENEMY_BANDIT.animations = new HashMap<String, AnimationWithOffset>();
            ENEMY_BANDIT.animations.put("run",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/bandit/run",8).content),-0,-9, -20));
            ENEMY_BANDIT.animations.put("idle",new AnimationWithOffset(new Animation<Sprite>(0.3f,new SpritePack("enemies/bandit/idle",6).content),-0,-9, -20));
            ENEMY_BANDIT.animations.put("die",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("enemies/bandit/die",6).content),-0,-9, -20));
            ENEMY_BANDIT.animations.put("attack",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/bandit/attack",7).content),-0,-9, -20));
            patterns[0] = ENEMY_BANDIT;


            ENEMY_SKELETON = new EnemyDef();
            ENEMY_SKELETON.cooldown = 60;
            ENEMY_SKELETON.cooldownInStart = 5;
            ENEMY_SKELETON.attackRange = 25;
            ENEMY_SKELETON.visibilityY = 5;
            ENEMY_SKELETON.visibilityX = 60;
            ENEMY_SKELETON.attackRange = 20;
            ENEMY_SKELETON.maxHealth = 400+Math.round(Math.random()-0.3)*100;
            ENEMY_SKELETON.runVelocity = 40;
            ENEMY_SKELETON.damageDealt = 125;
            ENEMY_SKELETON.stateTimeSlash = 1.075f;
            ENEMY_SKELETON.animations = new HashMap<String, AnimationWithOffset>();
            ENEMY_SKELETON.animations.put("run",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/skeleton/run",13).content),0,-8, -8));
            ENEMY_SKELETON.animations.put("idle",new AnimationWithOffset(new Animation<Sprite>(0.3f,new SpritePack("enemies/skeleton/idle",11).content),0,-8, -8));
            ENEMY_SKELETON.animations.put("die",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("enemies/skeleton/die",15).content),0,-8,-8));
            ENEMY_SKELETON.animations.put("attack",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/skeleton/attack",18).content),-4,-9, -18));
            patterns[1] = ENEMY_SKELETON;


            ENEMY_UNDEAD = new EnemyDef();
            ENEMY_UNDEAD.cooldown = 60;
            ENEMY_UNDEAD.cooldownInStart = 5;
            ENEMY_UNDEAD.attackRange = 25;
            ENEMY_UNDEAD.visibilityY = 5;
            ENEMY_UNDEAD.visibilityX = 60;
            ENEMY_UNDEAD.attackRange = 20;
            ENEMY_UNDEAD.maxHealth = 400+Math.round(Math.random()-0.3)*100;
            ENEMY_UNDEAD.runVelocity = 40;
            ENEMY_UNDEAD.damageDealt = 125;
            ENEMY_UNDEAD.stateTimeSlash = 1.075f;
            ENEMY_UNDEAD.animations = new HashMap<String, AnimationWithOffset>();
            ENEMY_UNDEAD.animations.put("run",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/undead/run",13).content),0,-16, -24));
            ENEMY_UNDEAD.animations.put("idle",new AnimationWithOffset(new Animation<Sprite>(0.3f,new SpritePack("enemies/undead/idle",11).content),2,-8, -21));
            ENEMY_UNDEAD.animations.put("die",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("enemies/undead/die",13).content),1,-8,-30));
            ENEMY_UNDEAD.animations.put("attack",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/undead/attack",18).content),0,-16, -23));
            patterns[2] = ENEMY_UNDEAD;


            for (int i = 0; i < BlockFactory.enemiesPos.size(); i++) {
                enemies.add(new Enemy(patterns[new Random().nextInt(patterns.length)],middle,BlockFactory.enemiesPos.get(i),this));
                stage.addActor(enemies.get(i));
            }
        }
        else if(request.equals("main menu")) {
            mainMenu = new MainMenu(this);
        }
        System.out.println("Done");
    }

    private void createStartSword() {
        ItemInfo info = new ItemInfo(ItemInfo.TYPE_WEAPON,"Start sword",70,0.03f,1);
        if(BlockFactory.startWeaponPos != null) {
            startWeapon = new StartWeapon(BlockFactory.startWeaponPos, middle, this, info);
            info.setItem(startWeapon);
            stage.addActor(startWeapon);
        }
    }

    public static LockedCamera getCamera() {
        return camera;
    }

    public Hero getHero() {
        return hero;
    }

    public StartWeapon getStartWeapon() {
        return startWeapon;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public static UI getUi() {
        return ui;
    }

    public World getMainWorld() {
        return middle;
    }

    public Stage getStage() {
        return stage;
    }

    public static boolean tap(float x, float y) {
        return ui.tap((int)x,(int)y);
    }
}
