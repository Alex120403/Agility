package com.agility.game.Utils;

import com.agility.game.Hero;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class LockedCamera extends OrthographicCamera {
    private Hero hero;
    private boolean movedToHero = false;
    public LockedCamera(float viewportWidth, float viewportHeight, Hero hero) {
        super(viewportWidth, viewportHeight);
        this.hero = hero;
    }
// 95 150
    @Override
    public void update() {
        super.update();

        if(hero != null && hero.getBody()!= null) {
            if(!movedToHero) {
                position.x = Hero.getPosition().x;
                position.y = Hero.getPosition().y;
                movedToHero = true;
            }
            float dx = position.x - Hero.getPosition().x;
            float dy = position.y - Hero.getPosition().y;
            position.x += dx*-0.04f;
            position.y += dy*-0.06f;

        }
    }
    public void shake(int direction) {
        position.x += 1*direction;
    }
}
