package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface OnCardHoveringSubscriber
{
    void onCardHovering(AbstractMonster hoveredMonster);
}
