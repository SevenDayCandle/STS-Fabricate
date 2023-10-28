package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.annotations.CombatSubscriber;
import pinacolada.cards.base.PCLCard;

@CombatSubscriber
public interface OnTryUsingCardSubscriber extends PCLCombatSubscriber {
    boolean canUse(AbstractCard card, AbstractPlayer p, AbstractMonster m, boolean canUse);
    default String getUnplayableMessage() {
        return PCLCard.UNPLAYABLE_MESSAGE;
    }
    default boolean hasEnoughEnergy(AbstractCard card, boolean canUse) {
        return canUse(card, AbstractDungeon.player, null, canUse);
    }
}
