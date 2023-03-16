package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.CombatSubscriber;
import pinacolada.interfaces.providers.CooldownProvider;

@CombatSubscriber
public interface OnCooldownTriggeredSubscriber extends PCLCombatSubscriber
{
    boolean onCooldownTriggered(AbstractCard card, AbstractCreature m, CooldownProvider cooldown);
}
