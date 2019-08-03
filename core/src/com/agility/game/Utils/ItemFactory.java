package com.agility.game.Utils;

import com.agility.game.UI.ItemInfo;
import com.agility.game.WorldObjects.Item;
import com.agility.game.Game;

import java.text.DecimalFormat;
import java.util.Random;

public class ItemFactory {
    private Game game;
    public static final String[] durabilities = {"New" ,"Worn", "Broken"};
    public static final String[] names = {"Broadsword","Silver sword","Shine","Training sword","Rapier","Ancient sword","Sharpness","Quick sword","Sapphire","Saber","Sai","Longsword","Double sword","Triple sword","Ruby","Bayonet","Claw","Light sword","Flame","Fire sabre","Icy cold","Snake","Katana","Curse"};
    private static Random random = new Random();
    public ItemFactory(Game game) {
        this.game = game;
    }
    public Item createRandomWeapon() {
        int swordId = random.nextInt(71);
        int level;
        do {
            level = game.getCurrentLevelNumber() - 1 + random.nextInt(3);
        } while (level <= 0);
        String durability = durabilities[swordId % 3];
        int damage = (int)(60 + 10 * level * (1f/(swordId % 3 + 1))+ random.nextInt(16));
        float criticalPercents = ((int)(3*(1f/(swordId % 3 + 1)))+random.nextInt(3)+level);

        ItemInfo info = new ItemInfo(ItemInfo.TYPE_WEAPON,names[swordId/3]+" "+PrettyLevel.toPretty(level)+" ("+durability + ")",damage,criticalPercents,level);
        Item item = null;

        item = new Item(game,"sword-0"+swordId+"",info);
        info.setItem(item);
        return item;
    }
    public Item createRandomArmor() {
        Item item = null;

        return item;
    }
}
