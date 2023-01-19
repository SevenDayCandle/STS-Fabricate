package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public interface OnAfterlifeSubscriber extends PCLCombatSubscriber
{
    void onAfterlife(AbstractCard playedCard, ArrayList<AbstractCard> fuelCards);
}
