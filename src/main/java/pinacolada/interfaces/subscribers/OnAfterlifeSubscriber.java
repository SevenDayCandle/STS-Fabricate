package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public interface OnAfterlifeSubscriber
{
    void onAfterlife(AbstractCard playedCard, ArrayList<AbstractCard> fuelCards);
}
