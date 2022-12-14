package pinacolada.effects.utility;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import pinacolada.effects.PCLEffect;

import java.util.ArrayDeque;

public class SequentialEffect extends PCLEffect
{
    private final ArrayDeque<AbstractGameEffect> effects;
    private AbstractGameEffect current;

    public SequentialEffect()
    {
        super();

        effects = new ArrayDeque<>();
    }

    public void enqueue(AbstractGameEffect effect)
    {
        effects.add(effect);
    }

    @Override
    public void update()
    {
        if (updateCurrent())
        {
            if (effects.size() > 0)
            {
                current = effects.pop();
            }
            else
            {
                complete();
            }
        }
    }

    @Override
    public void render(SpriteBatch sb)
    {
        if (current != null)
        {
            current.render(sb);
        }
    }

    private boolean updateCurrent()
    {
        if (current == null || current.isDone)
        {
            return true;
        }

        current.update();

        if (current.isDone)
        {
            current.dispose();
            return true;
        }

        return false;
    }
}