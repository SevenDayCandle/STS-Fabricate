package pinacolada.effects.utility;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import extendedui.EUIUtils;
import pinacolada.effects.PCLEffect;

import java.util.ArrayList;

public class CombinedEffect extends PCLEffect {
    private final ArrayList<AbstractGameEffect> effects = new ArrayList<>();

    public CombinedEffect() {
        super();
    }

    public <T extends AbstractGameEffect> T add(T effect) {
        effects.add(effect);
        startingDuration = duration = EUIUtils.findMax(effects, e -> e.duration).duration;
        return effect;
    }

    @Override
    public void update() {
        this.duration = 0;

        boolean complete = true;
        for (AbstractGameEffect effect : effects) {
            if (!effect.isDone) {
                effect.update();

                if (effect.isDone) {
                    effect.dispose();
                }
                else {
                    complete = false;

                    if (effect.duration > this.duration) {
                        this.duration = effect.duration;
                    }
                }
            }
        }

        if (complete) {
            complete();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        for (AbstractGameEffect effect : effects) {
            if (!effect.isDone) {
                effect.render(sb);
            }
        }
    }
}