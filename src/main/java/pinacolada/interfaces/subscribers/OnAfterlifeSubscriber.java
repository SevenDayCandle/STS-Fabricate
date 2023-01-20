package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

import java.util.ArrayList;

@CombatSubscriber
public interface OnAfterlifeSubscriber extends PCLCombatSubscriber
{
    void onAfterlife(AbstractCard playedCard, ArrayList<AbstractCard> fuelCards);
}
