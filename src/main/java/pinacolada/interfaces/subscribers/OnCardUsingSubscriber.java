package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnCardUsingSubscriber extends PCLCombatSubscriber {
    void onUse(AbstractCard card, AbstractPlayer p, AbstractMonster m);
}
