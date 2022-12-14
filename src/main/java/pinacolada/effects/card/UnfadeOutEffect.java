package pinacolada.effects.card;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.effects.PCLEffect;

public class UnfadeOutEffect extends PCLEffect
{
    private final AbstractCard card;

    public UnfadeOutEffect(AbstractCard card)
    {
        super(Settings.ACTION_DUR_MED, true);

        this.card = card;

        unfadeOut();
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (tickDuration(deltaTime))
        {
            unfadeOut();
        }
    }

    protected void unfadeOut()
    {
        if (card.fadingOut)
        {
            card.unfadeOut();
        }
    }
}