package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public interface OnLoseHPSubscriber extends PCLCombatSubscriber
{
    int onLoseHP(AbstractPlayer p, DamageInfo info, int amount);
}
