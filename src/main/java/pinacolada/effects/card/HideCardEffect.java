package pinacolada.effects.card;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class HideCardEffect extends AbstractGameEffect
{
    private final AbstractCard c;
    private static final float DUR = 1f;

    public HideCardEffect(AbstractCard c)
    {
        this.duration = 1f;
        this.c = c;
    }

    public void update()
    {
        this.c.current_y = this.c.target_y = -10000;
        this.c.isFlipped = true;
        this.isDone = true;
    }

    public void render(SpriteBatch sb)
    {

    }

    public void dispose()
    {

    }
}