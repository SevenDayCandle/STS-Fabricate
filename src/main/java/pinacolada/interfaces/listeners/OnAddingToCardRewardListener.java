package pinacolada.interfaces.listeners;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnAddingToCardRewardListener {
    boolean shouldCancel(AbstractCard card);
}