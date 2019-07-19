package com.agility.game.UI;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class HealthBoosterChoice extends BoosterChoice {
    public HealthBoosterChoice(UI ui) {
        super(ui);
        item1 = new BoosterChoiceItem(BoosterChoiceItem.HEALTH, 600, 0, ui);
        item2 = new BoosterChoiceItem(BoosterChoiceItem.MAX_HEALTH, 200, 1, ui);
    }
}
